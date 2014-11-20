package tagging;

import java.io.*;
import java.util.*;

import se.su.ling.stagger.*;

public class PosTagging {
	public static void test() throws ClassNotFoundException, IOException,
			TagNameException {
		String s = "Det var en gång en katt som hette Nils.";
		try {
			List<Word[]> res = posTagging(s, false);
			for (Word[] sent : res) {
				for (Word token : sent) {
					System.out.println(token);
				}
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}

	}

	public static List<Word[]> posTagging(String document, boolean silent)
			throws IOException {
		String modelFile = "./model/swedish.bin";

		ObjectInputStream modelReader;
		try {
			modelReader = new ObjectInputStream(new FileInputStream(modelFile));
		} catch (Exception e) {
			throw new IOException("Couldn't load modelfile");
		}
		print("Loading Stagger model ...", silent);
		
		Tagger tagger;
		try {
			tagger = (Tagger) modelReader.readObject();
		} catch (Exception e) {
			modelReader.close();
			throw new IOException("Model file found but unable to be loaded.");
		}
		print("Model loaded!", silent);
		modelReader.close();

		System.out.println(document);
		BufferedReader reader = new BufferedReader(new StringReader(document));

		Tokenizer tokenizer = new SwedishTokenizer(reader);
		ArrayList<Word[]> taggedSents = new ArrayList<Word[]>();
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
			TaggedToken[] taggedSent = tagger.tagSentence(sent, true, false);
			TagSet tagset = tagger.getTaggedData().getPosTagSet();
			Word[] words = new Word[taggedSent.length];
			for (int i = 0; i < taggedSent.length; i++) {
				TaggedToken token = taggedSent[i];
				String posTag;
				try {
					posTag = tagset.getTagName(token.posTag);
				} catch (TagNameException e) {
					posTag = null; // Todo: determine which type the
									// empty(unknown) postag should have
				}
				words[i] = new Word(token.token.value, token.lf, posTag);
			}
			taggedSents.add(words);

			sentIdx++;
		}

		tokenizer.yyclose();
		return taggedSents;
	}

	private static void print(String s, boolean silent) {
		if (!silent)
			System.out.println(s);
	}
}
