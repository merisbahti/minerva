package tagging;

public class Word {
	public String word;
	public String lemma;
	public String pos;
	//To be continued
	
	public Word(String word, String lemma, String pos){
		this.word = word;
		this.lemma = lemma;
		this.pos = pos;
	}

	@Override
	public String toString() {
		return word + "\t" + pos;
	}

}
