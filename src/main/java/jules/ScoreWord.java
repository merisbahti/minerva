package jules;

import tagging.Word;

/**
 * Very experimental right now
 * 
 * @author timdolck
 *
 */
public class ScoreWord extends Word implements Comparable<ScoreWord>{
	private int nounRank = 1;
	private double liblinRank = 1;
	private double totalRank;
	
	//Experimental feature where we can use the type from stagger to boost the score??
	private final double staggerTypeBoost = 0.5;

	public ScoreWord(String word, String lemma, String pos, String neTag,
			String neTypeTag) {
		super(word, lemma, pos, neTag, neTypeTag);
	}
	
	public void addNounIndex(int index){
		this.nounRank = index;
		totalRank = nounRank*liblinRank;
	}
	
	public void addliblinRank(double rank){
		this.liblinRank = rank;
		totalRank = nounRank*liblinRank;
	}
	
	public double getTotalRank(){
		return totalRank;
	}

	/**
	 * Used to sort the scorewords.
	 */
	@Override
	public int compareTo(ScoreWord other) {
		return Double.compare(this.totalRank, other.totalRank);
	}


}
