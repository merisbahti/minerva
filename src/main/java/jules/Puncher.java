package jules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
			if (match(sw.neTypeTag, categories)) {
				result.add(sw);
			}
		}
		return result.size() > 100 ? result.subList(0, 100) : result;
	}

	private boolean match(String neTypeTag, List<Pair<String, Double>> categories) {
		int noMatch = 0;
		for (Pair<String, Double> p : categories) {
			if (matchMap.containsKey(p.snd)) {
				String[] staggTags = matchMap.get(p.snd);
				for (String s : staggTags) {
					if (s.equalsIgnoreCase(neTypeTag))
						return true;
				}
			} else {
				noMatch ++;
			}
		}
		if(noMatch == categories.size())
			return true;
		
		return false;
	}

}
