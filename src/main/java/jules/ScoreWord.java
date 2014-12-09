package jules;

import tagging.Word;

/**
 * Very experimental right now
 * 
 * @author timdolck
 *
 */
public class ScoreWord extends Word implements Comparable<ScoreWord>{
	private int occurances = 0;
	private double nounRank = 1;
	private double liblinRank = 1;
	private double totalRank;
	
	//Experimental feature where we can use the type from stagger to boost the score??
	private final double staggerTypeBoost = 0.5;

	public ScoreWord(String word, String lemma, String pos, String neTag,
			String neTypeTag) {
		super(word, lemma, pos, neTag, neTypeTag);
	}
	
	public ScoreWord(Word word){
		super(word.word, word.lemma, word.pos, word.neTag, word.neTypeTag);
	}
	
	public void addNounIndexScore(double score){
		this.nounRank = score;
		totalRank = nounRank*liblinRank;
	}
	
	public void addliblinRank(double rank){
		this.liblinRank = rank;
		totalRank = nounRank*liblinRank;
	}
	
	public double getTotalRank(){
		return totalRank;
	}
	
	/*public void incOccurances(){
		this.occurances++;
	}*/

	/**
	 * Used to sort the scorewords.
	 */
	@Override
	public int compareTo(ScoreWord other) {
		return Double.compare(other.totalRank, this.totalRank);
	}


}
