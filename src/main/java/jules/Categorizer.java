package jules;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Categorizer {

	public static void main(String[] args) {
		StringBuffer output = new StringBuffer();
		String[] cmdarray = {"bash","-c", "cd ./libshorttext-1.1 && ./demo.py vad heter sveriges huvudstad?"};
		try {
			Process p = Runtime.getRuntime().exec(cmdarray);
		    p.waitFor();
		
		    BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
		 
		    StringBuilder sb = new StringBuilder();
		    String line = "";			
	    
			while ((line = reader.readLine())!= null) {
				output.append(line + "\n");
			}
			 System.out.println(output.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
