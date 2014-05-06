package simulation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


public class DempsterShaferTest {

	@SuppressWarnings({ "unchecked", "serial" })
	public static void test() {
		final HashMap<String, Float> oldScores = new HashMap<String, Float>() {{
			put("R-1", 1.0f);
		}};
		
		final HashMap<String, Float> newScores = new HashMap<String, Float>() {{
			put("R-1", 0.2f);
			put("R3", 0.8f);
		}};
		
		ArrayList<HashMap<String, Float>> scores = new ArrayList<HashMap<String, Float>>() {{
			add(oldScores);
			add(newScores);
		}};
		
		ArrayList<String> rules = new ArrayList<String>();
		for (String rule : oldScores.keySet()) {
			if (!rules.contains(rule)) {
				rules.add(rule);
			}
		}
		for (String rule : newScores.keySet()) {
			if (!rules.contains(rule)) {
				rules.add(rule);
			}
		}
		
		System.out.println("Old scores = ");
		dumpScores(oldScores);
		
		System.out.println("new scores = ");
		dumpScores(newScores);
		
		float k = 1 - calculateK((ArrayList<HashMap<String, Float>>) scores.clone(), new ArrayList<String>());
		
		System.out.println("K === " + k);
		
		HashMap<String, Float> compoundScoreMap = new HashMap<String, Float>();
		Iterator<String> rulesIterator = rules.iterator();
		while (rulesIterator.hasNext()) {
			String rule = rulesIterator.next();
			float belief = calculateBelief((ArrayList<HashMap<String, Float>>) scores.clone(), rule, false);
			System.out.println("belief = " + belief);
			compoundScoreMap.put(rule, belief / k);
		}
		
		System.out.println("compound scores = ");
		dumpScores(compoundScoreMap);
	}
	
	@SuppressWarnings("unchecked")
	private static float calculateK(ArrayList<HashMap<String, Float>> toIterate, ArrayList<String> memory) {
		
		float result = 0f;
		
		if (toIterate.size() > 0) {
			
			HashMap<String, Float> scoreMap = toIterate.remove(0);
			Iterator<String> ruleIterator = scoreMap.keySet().iterator();
			while (ruleIterator.hasNext()) {
				String rule = ruleIterator.next();
				if (!rule.equals("R-1")) {
				
					if (toIterate.size() > 0) {
						memory.add(rule);
						ArrayList<HashMap<String, Float>> temp = new ArrayList<HashMap<String, Float>>();
						temp.addAll(toIterate);
						
						result = result + (float) (scoreMap.get(rule).floatValue() * calculateK(temp, (ArrayList<String>) memory.clone()));
						
						memory.remove(rule);
						
					} else {
						if (checkList(memory)) {
							result = result + scoreMap.get(rule).floatValue();
						} else if (rule != memory.get(0)) {
							result = result + scoreMap.get(rule).floatValue();
						}
					}
				}
			}
		}
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	private static float calculateBelief(ArrayList<HashMap<String, Float>> toIterate, String memoryRule, boolean ok) {
		
		print("calculate belief called ");
		
		print("memory rule " + memoryRule);
		
		print("to iterate length  = " + toIterate.size());
		
		float result = 0;
		
		if (toIterate.size() > 0) {
			
			print("1");
			
			HashMap<String, Float> scoreMap = toIterate.remove(0);
			
			print("removed scoremap");
			dumpScores(scoreMap);
			
			Iterator<String> ruleIterator = scoreMap.keySet().iterator();
			while (ruleIterator.hasNext()) {
				String rule = ruleIterator.next();
				print("Processing rule " + rule);
				if (rule == memoryRule) {
					print("2");
					if (toIterate.size() > 0) {
						print("3");
						print("existing " + scoreMap.get(rule).floatValue());
						print("existing result = " + result);
						result = result + (float) scoreMap.get(rule).floatValue() * calculateBelief((ArrayList<HashMap<String, Float>>) toIterate.clone(), memoryRule, true);
					} else {
						print("4");
						print("existing " + scoreMap.get(rule).floatValue());
						result = result + scoreMap.get(rule).floatValue();
						print("result = " + result);
					}
				} else if (rule.equals("R-1")) {
					print("5");
					if (toIterate.size() > 0) {
						print("6");
						print("existing " + scoreMap.get(rule).floatValue());
						print("existing result = " + result);
						result = result + (float) scoreMap.get(rule).floatValue() * calculateBelief((ArrayList<HashMap<String, Float>>) toIterate.clone(), memoryRule, ok);
						print("result = " + result);
					} else if (ok) {
						print("ok");
						result = result + scoreMap.get(rule).floatValue();
						print("result = " + result);
					}
				}
			}
		}
		
		return result;
	}
	
	private static boolean checkList(ArrayList<String> list) {
		if (list.size() > 1) {
			for (int i = 1; i < list.size(); i++) {
				if (list.get(i) != list.get(0)) {
					return true;
				}
			}
			return false;
		}
		return true;
	}
	
	private static void dumpScores(HashMap<String, Float> scores) {
		
		for (String rule : scores.keySet()) {
			System.out.println("" + rule + " : " + scores.get(rule).floatValue());
		}
	}
	
	private static void print(String s) {
		System.out.println(s);
	}
}
