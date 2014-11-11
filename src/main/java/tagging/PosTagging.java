package tagging;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import se.su.ling.stagger.*;

public class PosTagging {
	public static void main(String[] args) throws ClassNotFoundException, IOException, TagNameException{
		List<String> strings = new ArrayList<String>();
		strings.add("Det var en gång en katt som hette Nils.");
		posTagging(strings);
		
	}
	public static void posTagging(List<String> documents) throws IOException, TagNameException, ClassNotFoundException {
		String modelFile = "./model/swedish.bin";
		TaggedToken[][] inputSents = null;

		ObjectInputStream modelReader = new ObjectInputStream(
				new FileInputStream(modelFile));
		System.err.println("Loading Stagger model ...");
		Tagger tagger = (Tagger) modelReader.readObject();
		String lang = "sv";
		modelReader.close();

		for (String inputFile : documents) {
			BufferedReader reader = new BufferedReader(new StringReader(inputFile));
			
			Tokenizer tokenizer = new SwedishTokenizer(reader);
			
			ArrayList<Token> sentence;
			int sentIdx = 0;
			while ((sentence = tokenizer.readSentence()) != null) {
				TaggedToken[] sent = new TaggedToken[sentence.size()];
				for (int j = 0; j < sentence.size(); j++) {
					Token tok = sentence.get(j);
					String id;
					id = sentIdx + ":" + tok.offset;
					sent[j] = new TaggedToken(tok, id);
				}
				TaggedToken[] taggedSent = tagger
						.tagSentence(sent, true, false);
				tagger.getTaggedData().writeConllSentence(System.out, taggedSent,true);
				sentIdx++;
			}
			tokenizer.yyclose();

		}
	}
}
