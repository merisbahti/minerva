package tagging;


/**
 * Word containing rankpoints
 * 
 * still some experimental stuff here
 * 
 * @author timdolck
 *
 */
public class ScoreWord extends Word implements Comparable<ScoreWord>{
	private double nounRank = 1;
	private double liblinRank = 1;
	private double totalRank;

	public ScoreWord(String word, String lemma, String pos, String neTag,
			String neTypeTag) {
		super(word, lemma, pos, neTag, neTypeTag);
	}
	
	public ScoreWord(Word word){
		super(word.word, word.lemma, word.pos, word.neTag, word.neTypeTag);
	}
	
	public void addNounIndexScore(double score){
		this.nounRank = score;
		updateTotalRank();
	}
	
	public void addliblinRank(double rank){
		this.liblinRank = rank;
		updateTotalRank();
	}
	
	private void updateTotalRank(){
		totalRank = Math.sqrt(nounRank)*liblinRank*liblinRank;
	}
	
	public double getTotalRank(){
		return totalRank;
	}
	
	public double getLiblinRank(){
		return liblinRank;
	}
	
	public double getNounIndexRank(){
		return nounRank;
	}

	/**
	 * Compare scorewords on totalRank.
	 */
	@Override
	public int compareTo(ScoreWord other) {
		return Double.compare(other.totalRank, this.totalRank);
	}

	public void normalizeScore(double sum) {
		if(sum > 0)
			this.totalRank = 100*totalRank/sum;
	}


}
