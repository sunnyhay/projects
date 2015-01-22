public class InputTest {
    public static void main(String[] args) {
	int[] input = new int[3];
	int i = 0;
	for(String s: args) {
	    input[i++] = Integer.parseInt(s);
	    System.out.println(s);
	}
	for(int j = 0; j < input.length; j ++)
	    System.out.println(input[j]);
    }
}
