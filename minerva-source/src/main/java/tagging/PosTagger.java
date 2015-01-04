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
import util.Constants;

/**
 * PosTagger
 * 
 * Uses Stagger to tag sentences 
 * Singleton object so that the model only will be loaded once per run
 * 
 * @author timdolck
 * 
 */
public class PosTagger {
	private Tagger tagger;
	private boolean silent = true; // set to false for a verbose output
	private static PosTagger instance = null;

	protected PosTagger() throws IOException {
		String modelFile = Constants.staggerModel;

		ObjectInputStream modelReader;
		try {
			modelReader = new ObjectInputStream(new FileInputStream(modelFile));
		} catch (Exception e) {
			throw new IOException("Couldn't load modelfile");
		}

		System.out.println("Loading stagger model...");
		try {
			tagger = (Tagger) modelReader.readObject();
		} catch (Exception e) {
			modelReader.close();
			throw new IOException("Model file found but unable to be loaded.");
		}
		System.out.println("Model loaded!");
		modelReader.close();
	}

	public static PosTagger getInstance() throws IOException {
		if (instance == null) {
			instance = new PosTagger();
		}
		return instance;
	}

	/**
	 * Tags a string(document)
	 * 
	 * @param document
	 * @return list of tagged sentences
	 */
	public List<Word[]> tagString(String document) {
		print(document);
		document = Constants.whiteList(document);
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
				try {
					taggedSent = tagger.tagSentence(sent, true, false);
				} catch (Exception e) {
					/*for (Token t : sentence) {
						System.err.print(t.value + " ");
					}
					System.err.println();*/
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

					words[i] = new Word(token.token.value, token.lf, posTag, neTag, neTypetag);
				}

				taggedSents.add(words);

				sentIdx++;
			}
		} catch (IOException e) {
			// Do nothing more if we cant read more sentences
			//TODO: Could be improved exception handling, remove non swedish letters?
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
