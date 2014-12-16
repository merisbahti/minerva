package test;
import minerva.Minerva;

import org.junit.*;

import util.Constants;
import util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
    Test för att kolla frekvensen av svaret givet en query.
 */
public class CeilingTest {
    private ArrayList<Pair<String, String>> al = new ArrayList<Pair<String, String>>();

    @Before
    public void setUp() {
        al.add(new Pair<>("Vad heter Sveriges huvudstad", "stockholm"));
        al.add(new Pair<>("Vad heter Islands Huvudstad", "Reykjavik".toLowerCase()));
        al.add(new Pair<>("Vilka gjorde låten Mamma Mia", "ABBA".toLowerCase()));
        al.add(new Pair<>("Vad är en björk", "Träd".toLowerCase()));
        al.add(new Pair<>("Vad kallas hundens ungar", "valp".toLowerCase()));
        al.add(new Pair<>("Sverige", "Stockholm"));
        al.add(new Pair<>("Vilket år är Göran Persson född", "1949"));
        al.add(new Pair<>("Vad heter Sveriges huvudstad", "Stockholm"));
        al.add(new Pair<>("Hur gammal blev Kurt Cobain", "27"));
        al.add(new Pair<>("Vilken är Kinas tredje-största stad", "Guangzhou"));
    }

    @Test
    public void firstQuestionTest() {
        for (Pair<String, String> qa : al) {
            List<Map<String, String>> results = new Minerva(qa.fst, 100).getPassages();
            ResultDetails occs = occursIn(qa.snd, results);
            System.out.println("FO: " + occs.firstOccurence+"\tTOTO: " + occs.totalOccurences + "\t" + qa.fst );
        }
    }

    private static ResultDetails occursIn(String answer, List<Map<String, String>> results) {
        int firstOccurence  = -1;
        int totalOccurences = 0;
        int curResultNumber = -1;
        int totalWords = 0;
        for (Map<String, String> result : results) {
            curResultNumber++;
            for (Map.Entry<String, String> entry : result.entrySet()) {
                // Put stagger magic here, extract nouns
                // And create relative frequency?
                String[] words = Constants.whiteList(entry.getValue()).split("\\s+");
                for (String word : words) {
                    totalWords++;
                    if (word.equalsIgnoreCase(answer))  {
                        if (firstOccurence == -1) firstOccurence = curResultNumber;
                        totalOccurences++;
                    }
                }
            }
        }
        return new ResultDetails(firstOccurence, totalOccurences, totalWords);
    }

    private static class ResultDetails {
       public final int firstOccurence;
       public final int totalOccurences;
       @SuppressWarnings("unused")
       public final int totalWords;
       ResultDetails(int firstOccurence, int totalOccurences, int totalWords)  {
           this.firstOccurence = firstOccurence;
           this.totalOccurences = totalOccurences;
           this.totalWords = totalWords;
       }
    }
}
