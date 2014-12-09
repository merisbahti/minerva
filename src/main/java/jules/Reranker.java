package jules;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import de.bwaldvogel.liblinear.*;

/**
 * VERY experimental
 * 
 * Reranker using liblinear
 * 
 * @author timdolck
 *
 */

public class Reranker {
	private Model model;
	private String modelFile = "Something";
	
	public Reranker() throws IOException{
		model = Model.load(new File(modelFile));
	
	}
	
	
	public List<ScoreWord> rerank(List<ScoreWord> input, Feature[] features){
		for(ScoreWord sw : input){
			double res = Linear.predict(model, features);
			sw.addliblinRank(res);
		}
		Collections.sort(input);
		return input;
	}

}
