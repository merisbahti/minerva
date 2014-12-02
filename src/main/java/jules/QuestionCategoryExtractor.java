package jules;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;

import tagging.PosTagger;
import tagging.Word;

public class QuestionCategoryExtractor {

	public static void main(String[] args) {
		String qDir = "./questions/";
		File dir = new File(qDir);
		try{
			PosTagger tagger = PosTagger.getInstance();
			PrintWriter writer = new PrintWriter("train_file.txt", "UTF-8");
			for(File f : dir.listFiles()){
				if(f.getName().startsWith(".")) continue;
				System.out.println("Reading file: " + f.getName());
				BufferedReader br = new BufferedReader(new FileReader(f));
				String line, category, question, lq;
				
				while((line = br.readLine()) != null){
					String[] cols = line.split("\t");
					category = cols[8].toLowerCase();
					question = cols[5].toLowerCase();
					lq = "";
					for(Word w : tagger.tagString(question).get(0)){
						lq += w.lemma + " ";
					}
					String print = category + "\t" + lq.trim();
					writer.println(print);
				}
			}
			writer.close();
		} catch (Exception e){
			e.printStackTrace();
		}

	}

}
