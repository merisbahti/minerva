package ranker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tagging.ScoreWord;
import util.Pair;

public class Puncher {
	private Map<String, String[]> matchMap = new HashMap<String, String[]>();

	// Stagger categories: person, place, inst, myth, work, animal, other
	public Puncher() {
		matchMap.put("person", new String[] { "person" });
		matchMap.put("location", new String[] { "place" });
	}

	public List<ScoreWord> punch(List<ScoreWord> topNouns, List<Pair<String, Double>> categories) {
		List<ScoreWord> result = new ArrayList<ScoreWord>();
		for (ScoreWord sw : topNouns) {
			if (match(sw.neTypeTag, categories)) {
				sw.addPunchDown(1);
			}else{
				sw.addPunchDown(0.5);
			}
			result.add(sw);
		}
		Collections.sort(result);
		return result.size() > 100 ? result.subList(0, 100) : result;
	}

	private boolean match(String neTypeTag, List<Pair<String, Double>> categories) {
		double limit = 0.5;
		int noMatch = 0;
		
		for (Pair<String, Double> p : categories) {
			if (p.snd > limit && matchMap.containsKey(p.fst)) {
				String[] staggTags = matchMap.get(p.fst);
				for (String s : staggTags) {
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
