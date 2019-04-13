package hmmExample;

import java.io.File;
import java.io.IOException;

import pjl_hmm.pjlhmm.Hmm;

public class save_and_load_model {
	public static void main(String[] args) throws IOException {
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
		
		//保存模型
		File modelFile = new File("model/hmm.model");
		hmm.save(modelFile);
		
		//加载模型
		Hmm newHmm = new Hmm();
		newHmm.load(modelFile);
		
		System.out.println("over");
	}

}
