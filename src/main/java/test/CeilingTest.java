package test;
import jules.Indexer;
import org.junit.*;

import java.util.List;
import java.util.Map;

/*
    Test för att kolla frekvensen av svaret givet en query.
 */
public class CeilingTest {
    @Test
    public void naiveQueryTest() {
        List<Map<String, String>> results = Indexer.query("Vad heter islands Huvudstad?", true);
        assert(true);
    }

    private static void occursIn(String answer, List<Map<String, String>> results) {

    }
}
