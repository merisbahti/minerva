package ranker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tagging.ScoreWord;
import util.Pair;

public class Puncher {
	private Map<String, String[]> matchMap = new HashMap<String, String[]>();

	// Stagger categories: person, place, inst, myth, work, animal, other
	public Puncher() {
		matchMap.put("person", new String[] { "person", "other", "-" });
		matchMap.put("location", new String[] { "place", "inst", "other", "-" });
	}

	public List<ScoreWord> punch(List<ScoreWord> topNouns, List<Pair<String, Double>> categories) {
		List<ScoreWord> result = new ArrayList<ScoreWord>();
		for (ScoreWord sw : topNouns) {
			System.out.println(sw.lemma);
			if (match(sw.neTypeTag, categories)) {
				result.add(sw);
			}
		}
		return result.size() > 100 ? result.subList(0, 100) : result;
	}

	private boolean match(String neTypeTag, List<Pair<String, Double>> categories) {
		double limit = 0.001;
		int noMatch = 0;
		
		for (Pair<String, Double> p : categories) {
			System.out.println(p.fst + " " + p.snd);
			if (p.snd > limit && matchMap.containsKey(p.fst)) {
				String[] staggTags = matchMap.get(p.fst);
				for (String s : staggTags) {
					System.out.println(p.fst + " " + s);
					if (s.equalsIgnoreCase(neTypeTag))
						return true;
				}
			} else {
				noMatch ++;
			}
		}
		if(noMatch == categories.size()){
			return true;
		}
		
		return false;
	}

}
