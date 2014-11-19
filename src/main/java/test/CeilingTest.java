package test;
import jules.Indexer;
import org.junit.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/*
    Test för att kolla frekvensen av svaret givet en query.
 */
public class CeilingTest {
    // Out of ideas, don't know which dependencies we can use.
    // Let's just use a List<String> where even elements are questions
    // and uneven elements are the answers to the previous elements question.
    // Fuck java.
    private ArrayList<String> al = new ArrayList(Arrays.asList(
            new String[]{
                    "Vad heter Sveriges huvudstad?", "Stockholm",
                    "Vad heter Islands Huvudstad?", "Reykjavik",
                    "Vilka gjorde låten Mamma Mia?", "ABBA",
                    "Vad är en björk?", "Träd",
                    "Vad kallas hundens ungar?", "valp"
            }));

    @Test
    public void firstQuestionTest() {
        for (int i = 0; i < al.size(); i=i+2) {
            System.out.println(al.get(i));
            List<Map<String, String>> results = Indexer.query(al.get(i), 1000);
            occursIn(al.get(i+1), results);
        }
        assert(true);
    }

    private static void occursIn(String answer, List<Map<String, String>> results) {
        int firstOccurence  = -1;
        int totalOccurences = 0;
        int curResultNumber = -1;
        int totalWords = 0;
        for (Map<String, String> result : results) {
            curResultNumber++;
            for (Map.Entry<String, String> entry : result.entrySet()) {
                // Put stagger magic here, extract nouns
                // And create relative frequency?
                String[] words = entry.getValue().split("\\s+");
                for (String word : words) {
                    totalWords++;
                    if (word.toLowerCase().replaceAll("[^a-zåäö]", "").equals(answer.toLowerCase()))  {
                        if (firstOccurence == -1) {
                            firstOccurence = curResultNumber;
                        }
                        totalOccurences++;
                    }
                }
            }
        }
        System.out.println("First occurence: " + firstOccurence);
        System.out.println("total occurences: " + totalOccurences);
        System.out.println("total occurences / total words: " +  (float) totalOccurences / totalWords);
    }
}
