package jules;

import tagging.Word;

/**
 * Word containing rankpoints
 * 
 * still some experimental stuff here
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
		updateTotalRank();
	}
	
	public void addliblinRank(double rank){
		this.liblinRank = rank;
		updateTotalRank();
	}
	
	private void updateTotalRank(){
		totalRank = nounRank*liblinRank;
	}
	
	public double getTotalRank(){
		return totalRank;
	}
	
	/*public void incOccurances(){
		this.occurances++;
	}*/

	/**
	 * Compare scorewords on totalRank.
	 */
	@Override
	public int compareTo(ScoreWord other) {
		return Double.compare(other.totalRank, this.totalRank);
	}


}
