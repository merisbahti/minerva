package jules;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import tagging.PosTagger;
import tagging.ScoreWord;
import tagging.Word;

public class RankNouns {

	private static String[] nounTags = { "NN", "PM" };

	public static List<ScoreWord> findTopNouns(List<Map<String, String>> docs) {
		PosTagger posTagger = null;
		try {
			posTagger = PosTagger.getInstance();
		} catch (IOException e1) {
			e1.printStackTrace();
			return new ArrayList<ScoreWord>();
		}
	
		HashMap<Word, Double> freqs = new HashMap<Word, Double>();
	
		for (Map<String, String> doc : docs) {
			// Extract score and use it in ScoreWord
			double score = Double.parseDouble(doc.get("Score"));
			Set<String> keys = doc.keySet();
			keys.remove("Score");
	
			for (String fieldKey : keys) {
				String fieldValue = doc.get(fieldKey);
				List<Word[]> sents = posTagger.tagString(fieldValue);
				for (Word[] sent : sents) {
					for (Word word : sent) {
						if (word.word.length() < 2 && !word.word.equalsIgnoreCase("ö|å"))
							continue;
						if (RankNouns.matchingPos(nounTags, word.pos)) {
							if (freqs.containsKey(word)) {
								freqs.put(word, freqs.get(word) + score);
							} else {
								freqs.put(word, (double) score);
							}
						}
					}
				}
			}
		}
	
		ArrayList<ScoreWord> scores = new ArrayList<ScoreWord>();
		for (Word freqKey : freqs.keySet()) {
			ScoreWord sw = new ScoreWord(freqKey);
			sw.addNounIndexScore(freqs.get(freqKey));
			scores.add(sw);
		}
	
		Collections.sort(scores);
		return scores;
	}

	private static boolean matchingPos(String[] tags, String pos) {
		for (String tag : tags) {
			if (tag.equalsIgnoreCase(pos))
				return true;
		}
		return false;
	}

}
