package jules;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import util.Pair;

public class Categorizer {

	public static void main(String[] args) {
		String q = "";
		while (!(q = System.console().readLine("Enter a question: "))
				.equals("q")) {
			getCategories(q);
		}
	}

	public static List<Pair<String, Double>> getCategories(String q) {
		String question = q;
		StringBuffer output = new StringBuffer();
		String[] cmdarray = { "bash", "-c",
				"cd ./libshorttext-1.1 && python3 ./demo.py \"" + q + "\""};
		try {
			Process p = Runtime.getRuntime().exec(cmdarray);
			p.waitFor();

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					p.getInputStream()));

			String line = "";
			String categories = "";
			String decvals = "";
			boolean catComming = false;

			while ((line = reader.readLine()) != null) {
				if (catComming) {
					categories = line.trim();
					catComming = false;
				}

				if (line.contains(question)) {
					catComming = true;
				}

				if (line.contains("decval")) {
					decvals = line.substring(10).trim();
				}
				output.append(line + "\n");
			}
			/*
			System.out.println(output.toString());
			System.out.println(categories);
			System.out.println(decvals);*/
			String[] cats = categories.split("\\s{1,10}");

			String[] vals = decvals.split("\\s+");
			List<Double> valDubsAbs = new ArrayList<Double>();
			for (String val : vals) {
				if (!"".equals(val.trim()) || !val.trim().isEmpty())
					valDubsAbs.add(Math.abs(new BigDecimal(val).doubleValue()));
			}

			double maxAbs = Collections.max(valDubsAbs);
			List<Pair<String, Double>> prs = new ArrayList<Pair<String, Double>>();

			for (int i = 0; i < cats.length; i++) {
				cats[i] = cats[i].trim();
				if (!cats[i].isEmpty()) {
					try {
						Pair<String, Double> pa = new Pair<String, Double>(cats[i],((new BigDecimal(vals[i]).doubleValue())/ maxAbs + 1) / 2);
						prs.add(pa);
					} catch (Exception e) {
						System.err.println("Something went wrong in categorizer: ");
						System.out.println(question);
						System.out.println(categories);
						System.out.println(decvals);
						//prs.add(new Pair<String, Double>("other", 0.0));
					}
				}
			}
			return prs;
		} catch (Exception e) {
			System.out.println(q);
			e.printStackTrace();
		}
		return null;
	}
}
