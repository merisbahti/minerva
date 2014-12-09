package jules;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import util.Pair;

public class Categorizer {
	
	public static void main(String[] args){
		String q = "";
		while(!(q = System.console().readLine("Enter a question: ")).equals("q")){
			getCategories(q);
		}
	}

	public static List<Pair> getCategories(String q) {
		String question = q;
		StringBuffer output = new StringBuffer();
		String[] cmdarray = {"bash","-c", "cd ./libshorttext-1.1 && ./demo.py " + q};
		try {
			Process p = Runtime.getRuntime().exec(cmdarray);
		    p.waitFor();
		
		    BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
		 
		    StringBuilder sb = new StringBuilder();
		    String line = "";
		    String categories = "";
		    String decvals = "";
		    boolean catComming = false;
	    
			while ((line = reader.readLine())!= null) {
				if(catComming){
					categories = line.trim();
					catComming = false;
				} 
				
				if (line.contains(question)){
					catComming = true;
				}
				
				if(line.contains("decval")){
					decvals = line.substring(10).trim();
				}
				output.append(line + "\n");
			}
			System.out.println(output.toString());
			System.out.println(categories);
			System.out.println(decvals);
			
			String[] cats = categories.split("\\s+");
			String[] vals = decvals.split("\\s+");
			List<Pair> prs = new ArrayList<Pair>();
			for(int i = 0; i < cats.length; i++){
				Pair pa = new Pair(cats[i], new BigDecimal(vals[i]));
				System.out.println(pa.fst + "=" + pa.snd);
				prs.add(pa);
			}
			return prs;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
