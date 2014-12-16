package util;

public class Constants {
	// Dirs
	public static final String qDir = "./questions/";
	public static final String indexDir = "./indexDir/";
	public static final String modelsDir = "./model/";
	public static final String outputDir = "./output/";
	public static final String trainDir = "./train/";
	//files
	public static final String liblinearWordMap = modelsDir + "training_indexes.txt";
	public static final String wikiFile = "./sewiki-20141104-pages-meta-current.xml";
	public static final String staggerModel = modelsDir + "swedish.bin";
	public static final String liblinearTrain = trainDir + "train_file.scale";
	public static final String liblinearModel = modelsDir + "train_file.scale.model";

	public static String whiteList(String s) {
		return s.replaceAll("[^åäöa-zA-ZÅÄÖ0-9\\s]","");
	}
}
