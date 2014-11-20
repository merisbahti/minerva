package test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jules.QueryPassenger;

import org.junit.Test;

public class RankNounsTest {

	@Test
	public void test() {
		List<Map<String,String>> res = QueryPassenger.query("Stockholm", 10);
		LinkedHashMap<String, Integer> lm = QueryPassenger.findTopNouns(res);
		//Display top 30 frequent nouns
		int i = 0;
		for(String s : lm.keySet()){
			if(i > 30)
				break;
			System.out.println(s + " \t" + lm.get(s));
		}
	}

}
