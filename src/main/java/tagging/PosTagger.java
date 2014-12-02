package tagging;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import se.su.ling.stagger.SwedishTokenizer;
import se.su.ling.stagger.TagNameException;
import se.su.ling.stagger.TagSet;
import se.su.ling.stagger.TaggedToken;
import se.su.ling.stagger.Tagger;
import se.su.ling.stagger.Token;
import se.su.ling.stagger.Tokenizer;

public class PosTagger {
	public static void test() throws ClassNotFoundException, IOException,
			TagNameException {
		String s = "Det var en gång en katt som hette Nils och bodde i Rio de Janeiro.";
		try {
			PosTagger t = new PosTagger();
			List<Word[]> res = t.tagString(s);
			for (Word[] sent : res) {
				for (Word token : sent) {
					System.out.println(token);
				}
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}

	}

	private Tagger tagger;
	private boolean silent;
	private static PosTagger instance = null;

	protected PosTagger() throws IOException {
		this.silent = true;
		String modelFile = "./model/swedish.bin";

		ObjectInputStream modelReader;
		try {
			modelReader = new ObjectInputStream(new FileInputStream(modelFile));
		} catch (Exception e) {
			throw new IOException("Couldn't load modelfile");
		}
		print("Loading Stagger model ...");

		try {
			tagger = (Tagger) modelReader.readObject();
		} catch (Exception e) {
			modelReader.close();
			throw new IOException("Model file found but unable to be loaded.");
		}
		print("Model loaded!");
		modelReader.close();
	}

	public static PosTagger getInstance() throws IOException {
		if (instance == null) {
			instance = new PosTagger();
		}
		return instance;
	}

	public List<Word[]> tagString(String document) {
		print(document);
		BufferedReader reader = new BufferedReader(new StringReader(document));

		Tokenizer tokenizer = new SwedishTokenizer(reader);
		ArrayList<Word[]> taggedSents = new ArrayList<Word[]>();
		ArrayList<Token> sentence;
		int sentIdx = 0;
		try {
			while ((sentence = tokenizer.readSentence()) != null) {
				TaggedToken[] sent = new TaggedToken[sentence.size()];
				for (int j = 0; j < sentence.size(); j++) {
					Token tok = sentence.get(j);
					String id;
					id = sentIdx + ":" + tok.offset;
					sent[j] = new TaggedToken(tok, id);
				}
				TaggedToken[] taggedSent; 
				try{
					taggedSent = tagger.tagSentence(sent, true, false);
				}catch (Exception e){
					for(Token t : sentence){
						System.err.print(t.value + " ");
					}
					System.err.println();
					continue;
				}
				TagSet tagset = tagger.getTaggedData().getPosTagSet();
				TagSet netagset = tagger.getTaggedData().getNETagSet();
				TagSet netypetagset = tagger.getTaggedData().getNETypeTagSet();
				Word[] words = new Word[taggedSent.length];
				for (int i = 0; i < taggedSent.length; i++) {
					TaggedToken token = taggedSent[i];
					String posTag;
					String neTag;
					String neTypetag;
					try {
						posTag = tagset.getTagName(token.posTag).split("\\|")[0];

					} catch (TagNameException e) {
						posTag = "-"; // Todo: determine which type the
										// empty(unknown) postag should have
					}
					try {
						neTag = netagset.getTagName(token.neTag);
					} catch (TagNameException e) {
						neTag = "O";
					}
					try {
						neTypetag = netypetagset.getTagName(token.neTypeTag);
					} catch (TagNameException e) {
						neTypetag = "-";
					}

					words[i] = new Word(token.token.value, token.lf, posTag,
							neTag, neTypetag);
				}

				taggedSents.add(words);

				sentIdx++;
			}
		} catch (IOException e) {
			//Do nothing more if we cant read more sentences
		}

		try {
			tokenizer.yyclose();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return taggedSents;
	}

	private void print(String s) {
		if (!silent)
			System.out.println(s);
	}
}
