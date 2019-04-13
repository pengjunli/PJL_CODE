package hmmExample;

import java.io.File;

import hmmLearner.supervisedLearner;
import pjl_hmm.pjlhmm.Hmm;

public class supervisedLearningExample {
	public static void main(String[] args) throws Exception {
		File stateFile = new File("");   //状态序列所在文件
		File observationFile = new File("");  //观测序列所在文件
		String[] stateList = new String[] {};  //状态列表
		
		//创建监督学习方法实例
		supervisedLearner learner = new supervisedLearner(stateList);
		learner.stateFileOperation(stateFile);  //处理状态序列文件
		learner.observationFileOperation(observationFile);  //处理观测序列文件
		Hmm hmm = learner.getHmm();
	}

}
