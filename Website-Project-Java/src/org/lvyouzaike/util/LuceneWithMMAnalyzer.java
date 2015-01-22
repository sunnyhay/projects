package org.lvyouzaike.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.Scorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.chenlb.mmseg4j.analysis.SimpleAnalyzer;

public class LuceneWithMMAnalyzer {
	private static Directory dir = null;                      //dir object
	private static Analyzer analyzer = null;                  //analyzer, currently mmseg4j
	private static IndexWriterConfig conf = null;             //configuration of index writer for index purpose
	private static IndexReader reader = null;                 //index reader for search purpose 
	private static final int MAXRESULT = 10000;               //the maximum search result
	private static final int LENGTHLIMIT = 150;               //the limit of abstract for each research result
	private static final Version LUCENEVERSION = Version.LUCENE_36;     //version of current lucene package
	private static final String FORMATHEAD = "<font color='red'><b>";   //head format for highlighted query string in the result
	private static final String FORMATTAIL = "</b></font>";             //tail format for highlighted query string in the result
	private static final String HTMLPOSTFIX = ".html";                  //for neko HTML parsing
	private static final String HTMPOSTFIX = ".htm";                    //for neko HTM parsing
	private static final String JSPPOSTFIX = ".jsp";                    //for neko JSP parsing
	private static final String REGEXEMPTYLINE = "(?m)^[ \t]*\r?\n";    //regular expression to remove empty lines and spaces

	//
	private static boolean postfixCheck(File file){
		return file.getName().endsWith(HTMLPOSTFIX) || file.getName().endsWith(JSPPOSTFIX) || file.getName().endsWith(HTMPOSTFIX);
	}
	
	//recursively parse the DOM node and get the total text string
	private static void getText(Node node, StringBuffer str){
		if(node.getNodeType() == Node.TEXT_NODE)
			str.append(node.getNodeValue());
        Node child = node.getFirstChild();
        while (child != null) {
            getText(child, str);
            child = child.getNextSibling();
        }
	}
	
	//parse a given file
	public static String parse(String filepath) throws SAXException, IOException{
		DOMParser parser = new DOMParser();
		parser.parse(filepath);
		org.w3c.dom.Document doc = parser.getDocument();
        StringBuffer str = new StringBuffer();
        getText(doc, str);
        //System.out.println(str.toString().replaceAll(REGEXEMPTYLINE, ""));
        return str.toString().replaceAll(REGEXEMPTYLINE, "");
	}
	
	//a file filter to discard sub directories under a root
	private static File[] getDirFiles(String fileDir){
		return new File(fileDir).listFiles(new FileFilter(){
			@Override
			public boolean accept(File file) {
				return !file.isDirectory();
			}
			
		});
	}
	
	//file's name, content, size, path
	private static Document file2Document(String path) throws IOException, SAXException {
		File file = new File(path);
		Document doc = new Document();

		doc.add(new Field("name", file.getName(), Store.YES, Index.ANALYZED));
		doc.add(new Field("size", String.valueOf(file.length()), Store.YES,
				Index.NOT_ANALYZED));
		doc.add(new Field("path", file.getName(), Store.YES, Index.NO));
		
		if(postfixCheck(file)){//HTML file, need to be parsed by neko
			doc.add(new Field("content", parse(file.getAbsolutePath()), Store.YES,
					Index.ANALYZED));
		}else{
			doc.add(new Field("content", readFileContent(file), Store.YES,
					Index.ANALYZED));
		}		

		return doc;
	}

	/*
	 * read a file and return the content with string form
	 */
	private static String readFileContent(File file) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(file)));
		StringBuffer content = new StringBuffer();

		for (String line = null; (line = reader.readLine()) != null;) {
			content.append(line).append("\n");
		}

		return content.toString();
	}

	/*private static void printDocumentInfo(Document doc) {
		System.out.println("name: " + doc.get("name"));
		System.out.println("content: " + doc.get("content"));
		System.out.println("size: " + doc.get("size"));
		System.out.println("path: " + doc.get("path"));
	}*/
	
	public static void createIndice(String indexPath, List<String> fileDirs) throws IOException, SAXException{
		dir = FSDirectory.open(new File(indexPath));
		analyzer = new SimpleAnalyzer();
		conf = new IndexWriterConfig(LUCENEVERSION, analyzer);
		conf.setOpenMode(OpenMode.CREATE);
		
		Set<Document> docs = new HashSet<Document>();
		for(String fileDir: fileDirs){
			for(File file: getDirFiles(fileDir)){
				/*System.out.println("this file's path: " + file.getPath());
				System.out.println("this file's absolute path: " + file.getAbsolutePath());
				System.out.println("this file's canonical path: " + file.getCanonicalPath());
				System.out.println("this file's name: " + file.getName());*/
				docs.add(file2Document(file.getPath()));
			}
		}		
				
		IndexWriter indexWriter = new IndexWriter(dir, conf);
		indexWriter.addDocuments(docs);
								
		indexWriter.close();
	}
	
	public static List<Document> search(String indexPath, String queryString) throws IOException, ParseException, InvalidTokenOffsetsException{
		return search(indexPath, queryString, LENGTHLIMIT, MAXRESULT);
	}
	
	private static List<Document> search(String indexPath, String queryString, int lengthLimit, int maxResult) throws IOException, ParseException, InvalidTokenOffsetsException{
		//1. initialization
		analyzer = new SimpleAnalyzer();
		dir = FSDirectory.open(new File(indexPath));
		reader = IndexReader.open(dir);
		List<Document> documents = new ArrayList<Document>();
		
		//2. convert the query string into a Query object
		String[] fields = {"name", "content"};
		QueryParser queryParser = new MultiFieldQueryParser(LUCENEVERSION, fields, analyzer);
		Query query = queryParser.parse(queryString);
		
		//3. query
		IndexSearcher indexSearcher = new IndexSearcher(reader);
		Filter filter = null;

		TopDocs topDocs = indexSearcher.search(query, filter, maxResult, Sort.RELEVANCE);
		
		//System.out.println("total matched records: " + topDocs.totalHits);

		//4. highlighter
		Formatter formatter = new SimpleHTMLFormatter(FORMATHEAD, FORMATTAIL);
		Scorer scorer = new QueryScorer(query);
		Highlighter highlighter = new Highlighter(formatter, scorer);
		Fragmenter frag = new SimpleFragmenter(lengthLimit);

		highlighter.setTextFragmenter(frag);

		String hc = ""; // for highlight show
		
		for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
			int docId = scoreDoc.doc; // internal index for document
			// float score = scoreDoc.score; //relative indication
			Document doc = indexSearcher.doc(docId); // fetch corresponding document with correct document id

			// use highlighter. if no key searched, return null.
			hc = highlighter.getBestFragment(analyzer, "content", doc.get("content"));
			if (null != hc) {
				Field field = (Field) doc.getFieldable("content");
				field.setValue(hc);
				// doc.getField("content").setValue(hc);
			}
			//printDocumentInfo(doc); // print out all info of this document
			documents.add(doc);
		}

		reader.close();
		indexSearcher.close();
		return documents;
	}
}
