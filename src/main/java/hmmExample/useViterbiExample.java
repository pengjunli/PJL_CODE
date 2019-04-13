package hmmExample;

import java.util.Arrays;

import hmmPredictor.viterbiAlgorithm;
import pjl_hmm.pjlhmm.Hmm;

/**
 * 该例子实现的是李航《统计学习方法》中第186页的例10.3
 * @author pjl
 *
 */
public class useViterbiExample {
	public static void main(String[] args) {
		double[] pi = new double[] {0.2,0.4,0.4};
		double[][] Aij = new double[][] {
			{0.5,0.2,0.3},
			{0.3,0.5,0.2},
			{0.2,0.3,0.5},
		};
		double[][] Bij = new double[][] {
			{0.5,0.5},
			{0.4,0.6},
			{0.7,0.3},
		};
		String[] stateList = new String[]{"a","b","c"};
		String[] observationList = new String[] {"红","白"};
		
		//配置hmm模型
		Hmm hmm = new Hmm();
		hmm.setPi(pi);
		hmm.setAij(Aij);
		hmm.setBij(Bij);
		hmm.setStateList(stateList);
		hmm.setObservationList(observationList);
//		hmm.setStateIndexMap();
//		hmm.setObservationIndexMap();
		
		String[] sequence = new String[] {"红","白","红"};
		viterbiAlgorithm doit = new viterbiAlgorithm();
		int[] path = doit.compute(sequence, hmm);
		
		System.out.println(Arrays.toString(path));
		for(int i:path) {
			System.out.println(i + " :" +stateList[i]);
		}
	}
}
