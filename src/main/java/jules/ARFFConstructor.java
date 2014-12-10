package jules;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;

import tagging.PosTagger;
import tagging.Word;

public class ARFFConstructor {

	//"concept", "location", "definition", "description", "multiplechoice", 
	//"amount", "organization", "other", "person", "abbreviation", "verb"
	public static void main(String[] args) {
		String tr = "TRUE";
		String fa = "FALSE";
		String qDir = "./questions/";
		File dir = new File(qDir);
		try{
			PosTagger tagger = PosTagger.getInstance();
			PrintWriter writer = new PrintWriter("train_file.ARFF", "UTF-8");
			writer.println("@relation category");
			writer.println();
			writer.println("@attribute word string");
			writer.println("@attribute category {concept, location, definition, description, multiplechoice, amount, organization, other, person, abbreviation, verb}");
			/*
			writer.println("@attribute concept {TRUE, FALSE}");
			writer.println("@attribute location {TRUE, FALSE}");
			writer.println("@attribute definition {TRUE, FALSE}");
			writer.println("@attribute description {TRUE, FALSE}");
			writer.println("@attribute multiplechoice {TRUE, FALSE}");
			writer.println("@attribute amoun {TRUE, FALSE}");
			writer.println("@attribute organization {TRUE, FALSE}");
			writer.println("@attribute other {TRUE, FALSE}");
			writer.println("@attribute person {TRUE, FALSE}");
			writer.println("@attribute abbreviation {TRUE, FALSE}");
			writer.println("@attribute verb {TRUE, FALSE}");
			*/
			writer.println();
			writer.println("@data");
			String[] cats = {"concept", "location", "definition", "description", "multiplechoice", "amount", "organization", "other", "person", "abbreviation", "verb"};
			for(File f : dir.listFiles()){
				if(f.getName().startsWith(".")) continue;
				System.out.println("Reading file: " + f.getName());
				BufferedReader br = new BufferedReader(new FileReader(f));
				String line, category, answer, lq;
				
				while((line = br.readLine()) != null){
					String[] cols = line.split("\t");
					category = cols[8].toLowerCase();
					answer = cols[6].toLowerCase();
					if (answer.contains(" ")) continue;
					lq = "";
					for(Word w : tagger.tagString(answer).get(0)){
						lq += w.lemma + " ";
					}
					//String print = category + "\t" + lq.trim();
					/*
					String catlist = "";
					for (String s : cats){
						catlist += ",";
						if (s.equals(category)){
							catlist += tr;
						} else {
							catlist += fa;
						}
					}
					*/
					
					System.out.println(lq);
					String print = lq.trim() + "," + category.trim();
					writer.println(print);
				}
			}
			writer.close();
		} catch (Exception e){
			e.printStackTrace();
		}
	}

}
