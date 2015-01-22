package edu.wsu.eecs.nrl;

/*
 * Value is the object in Item's value part. Its value is not changed once
 * belonging Item instance is created.
 */
public class Value {

	private String val;
	
	public Value(){
		
	}
	
	public Value(String val){
		this.val = val;
	}
	
	public boolean equals(Value v){
		if(this.val.equals(v.getVal()))
			return true;
		return false;
	}

	public String getVal() {
		return val;
	}

	public void setVal(String val) {
		this.val = val;
	}	
	
}
