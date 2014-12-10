package jules;

import java.util.Collections;
import java.util.List;

import util.Pair;


/**
 * VERY experimental
 * 
 * Reranker using liblinear
 * 
 * @author timdolck
 *
 */

public class Reranker {
	
	private final static double notInTop5 = -100;
	
	public static List<ScoreWord> rerank(List<ScoreWord> input, List<Pair<String,Double>> categories){
		for(ScoreWord sw : input){
			boolean setRank = false;
			for(Pair<String,Double> category : categories){
				if(sw.neTypeTag.equalsIgnoreCase(category.fst)){
					sw.addliblinRank(category.snd);
					setRank = true;
					break;
				}
			}
			if(setRank){
				sw.addliblinRank(notInTop5);
			}
		}
		Collections.sort(input);
		return input;
	}

}
