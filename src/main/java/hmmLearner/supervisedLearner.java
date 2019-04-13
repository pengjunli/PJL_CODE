package hmmLearner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import pjl_hmm.pjlhmm.Hmm;

/**
 * 该类实现了HMM中的监督学习方法，输入为状态列表、观测序列文件、观测序列对应的状态序列文件，输出为hmm模型
 * 首先构造实例时需要输入包含所有可能状态的列表，接着调用stateFileOperation函数，处理状态序列文件，然后调用
 * observationFileOperation函数，处理观测序列文件
 * @author pjl
 *
 */
public class supervisedLearner {
	String[] stateList = null; //状态类型列表
	String[] observationList = null; //观测类型列表
	double[] eachStateTransitionNum = null; //每个状态在训练集中转移到别的状态的总数
//	double[] tempraryList = new double[stateList.length];
	List<List<String>> observationSequenceList = new ArrayList<>();   //观测序列列表
	List<List<String>> stateSequenceList = new ArrayList<>();   //状态序列列表
	double[] pi = null;   //初始概率分布
	double[][] Aij = null;  //状态转移概率分布 i=j
	double[][] Bij = null;  //观测概率分布 i为状态数目   j为观测
	
	public supervisedLearner(String[] stateList) {
		this.stateList = stateList;
		eachStateTransitionNum = new double[stateList.length];
		pi = new double[stateList.length];
		Aij = new double[stateList.length][stateList.length];
		init();
	}
	
	/**
	 * 初始化
	 */
	private void init() {
		for(int i=0;i<stateList.length;i++) {
			pi[i] = 0.0;
			for(int j=0;j<stateList.length;j++) {
				Aij[i][j] = 0.0;
			}
		}
	}
	
	/**
	 *该函数用于处理训练集中的状态序列文件，输入状态序列所在的文件，通过该函数，可以得到初始概率和状态转移概率矩阵 ；
	 *文件格式为一行一个状态序列；
	 *其中，观测序列文件中的观测序列与状态序列文件中的状态序列一一对应
	 * @param stateFile
	 * @throws IOException
	 */
	public void stateFileOperation(File stateFile) throws IOException {
		BufferedReader stateReader = new BufferedReader(new FileReader(stateFile));
		String temp = null;
		String[] str = null;
		while((temp=stateReader.readLine())!=null) {
			str = temp.split(" ");
			List<String> stateSequence = new ArrayList<>();
			pi[Arrays.binarySearch(stateList, str[0])] += 1.0;   //每行状态序列的第一个状态都进行叠加，用于计算初始概率
			for(int i=0;i<(str.length-1);i++) {   //计算时刻t处于状态i，时刻t+1处于状态j的总次数，并计算i转移到其他状态的总次数
				int Ai = Arrays.binarySearch(stateList, str[i]);
				int Aj = Arrays.binarySearch(stateList, str[i+1]);
				Aij[Ai][Aj] += 1.0;
				eachStateTransitionNum[Ai] += 1.0;
				stateSequence.add(str[i]);
			}
			stateSequence.add(str[str.length-1]);
			stateSequenceList.add(stateSequence);
		}
		stateReader.close();
		get_Pi_and_Aij();
	}
	
	/**
	 * 计算初始概率分布和状态转移概率分布矩阵
	 */
	private void get_Pi_and_Aij() {
		double listNum = (double)stateSequenceList.size();
		for(int i=0;i<stateList.length;i++) {
			pi[i] = pi[i]/listNum;   //得到初始概率分布
			for(int j=0;j<stateList.length;j++) {
				Aij[i][j] = Aij[i][j]/eachStateTransitionNum[i];   //计算每行状态转移概率
			}
		}
	}
	
	/**
	 * 该函数用于处理训练集中的观测序列文件，输入观测序列所在的文件，通过该函数，可以得到观测概率分布矩阵 ；
	 *文件格式为一行一个观测序列；
	 *其中，观测序列文件中的观测序列与状态序列文件中的状态序列一一对应
	 * @param observationFile
	 * @throws Exception
	 */
	public void observationFileOperation(File observationFile) throws Exception {
		HashSet<String> temporaryList = new HashSet<>();  //用于去除重复观测，获得全部观测值
		List<String> tempraryObservationList = new ArrayList<>();
		BufferedReader observationReader = new BufferedReader(new FileReader(observationFile));
		String temp = null;
		String[] str = null;
		while((temp=observationReader.readLine())!=null) {
			str = temp.split(" ");
			List<String> observationList = new ArrayList<>();
			for(int i=0;i<str.length;i++) {
				observationList.add(str[i]);
				if(!temporaryList.contains(str[i])) {
					temporaryList.add(str[i]);
					tempraryObservationList.add(str[i]);
					
				}
			}
			observationSequenceList.add(observationList);
		}
		//将所有观测值存入观测值列表中
		this.observationList = new String[tempraryObservationList.size()];
		for(int i=0;i<tempraryObservationList.size();i++) {
			this.observationList[i] = tempraryObservationList.get(i);
		}
		
		observationReader.close();
		
		Bij = new double[stateList.length][this.observationList.length];
		get_Bij_count();
		get_Bij();
	}
	
	/**
	 * 计算得到观测概率分布矩阵Bij
	 * @throws Exception
	 */
	private void get_Bij_count() throws Exception {
		int stateListSize = stateSequenceList.size();
		int observationListSize = observationSequenceList.size();
		if(stateListSize!=observationListSize)
			throw new Exception("The number of state sequences is different from the number of observation sequences.");
		
		for(int i=0;i<stateListSize;i++) {
			List<String> state_sequence = stateSequenceList.get(i);
			List<String> observation_sequence = observationSequenceList.get(i);
			Bij_count(state_sequence,observation_sequence);
		}
	}
	
	/**
	 * 计算得到状态为j并观测为k的频数
	 * @param state_sequence
	 * @param observation_sequence
	 * @throws Exception
	 */
	private void Bij_count(List<String> state_sequence,List<String> observation_sequence) throws Exception {
		int stateNum = state_sequence.size();
		int observationNum = observation_sequence.size();
		double[] tempraryList = new double[stateList.length];
		if(stateNum!=observationNum)
			throw new Exception("The number of states is different from the number of observations.");
		
		//计算得到状态为j并观测为k的频数和状态为j出现的频数
		for(int i=0;i<stateNum;i++) {
			Bij[Arrays.binarySearch(stateList, state_sequence.get(i))][Arrays.binarySearch(observationList, observation_sequence.get(i))] += 1.0;
//			tempraryList[Arrays.binarySearch(stateList, state_sequence.get(i))] += 1.0;
		}
	}
	
	private void get_Bij() {
		for(int i=0;i<stateList.length;i++) {
			for(int j=0;j<observationList.length;j++) {
				Bij[i][j] = Bij[i][j]/eachStateTransitionNum[i];
			}
		}
	}
	
	/**
	 * 返回已经训练好的hmm模型
	 * @return
	 */
	public Hmm getHmm() {
		Hmm model = new Hmm();
		model.setAij(Aij);
		model.setBij(Bij);
		model.setPi(pi);
		model.setStateList(stateList);
		model.setObservationList(observationList);
		
		return model;
	}

}
