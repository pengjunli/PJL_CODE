package pjl_hmm.pjlhmm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class Hmm {
	/**
	 * 初始概率分布
	 */
	private double[] pi = null;
	/**
	 * 状态转移概率分布矩阵
	 */
	private double[][] Aij = null;
	/**
	 * 状态列表
	 */
	private String[] stateList = null;
	/**
	 * 观测列表
	 */
	private String[] observationList = null;
	/**
	 * 观测概率分布矩阵
	 */
	private double[][] Bij = null;
	
	private HashMap<String,Integer> stateIndexMap = new HashMap<>(); 
	
	private HashMap<String,Integer> observationIndexMap = new HashMap<>(); 
	
	
	public Hmm() {}
	
	public Hmm(String[] stateList) {
		this.stateList = stateList;
		this.pi = new double[stateList.length];
		this.Aij = new double[stateList.length][stateList.length];
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
	 * 得到状态转移概率矩阵
	 * @return
	 */
	public double[][] getAijMatrix(){
		return Aij;
	}
	
	/**
	 * 得到观测概率矩阵
	 * @return
	 */
	public double[][] getBijMatrix(){
		return Bij;
	}
	
	/**
	 * 得到初始概率向量
	 * @return
	 */
	public double[] getPiVector() {
		return pi;
	}
	
	/**
	 * 得到状态转移概率值
	 * @param i
	 * @param j
	 * @return
	 */
	public double getAij(int i,int j){
		return Aij[i][j];
	}
	
	/**
	 * 得到观测概率值
	 * @param i
	 * @param j
	 * @return
	 */
	public double getBij(int i,int j){
		return Bij[i][j];
	}
	
	/**
	 * 得到初始概率值
	 * @param i
	 * @return
	 */
	public double getPi(int i) {
		return pi[i];
	}
	
	/**
	 * 得到状态的索引map
	 * @return
	 */
	public HashMap<String,Integer> getStateIndexMap(){
		return stateIndexMap;
	}
	
	/**
	 * 得到观测的索引map
	 * @return
	 */
	public HashMap<String,Integer> getObservationIndexMap(){
		return observationIndexMap;
	}
	
	/**
	 * 设置状态转移概率
	 * @param i
	 * @param j
	 * @param value
	 */
	public void setAijValue(int i,int j,double value) {
		Aij[i][j] = value;
	}
	
	/**
	 * 设置观测概率
	 * @param i
	 * @param j
	 * @param value
	 */
	public void setBijValue(int i,int j,double value) {
		Bij[i][j] = value;
	}
	
	/**
	 * 设置初始概率
	 * @param i
	 * @param value
	 */
	public void setPiValue(int i,double value) {
		pi[i] = value;
	}
	
	/**
	 * 设置状态转移概率矩阵
	 * @param Aij
	 */
	public void setAij(double[][] Aij) {
		this.Aij = Aij;
	}
	
	/**
	 * 设置观测概率矩阵
	 * @param Bij
	 */
	public void setBij(double[][] Bij) {
		this.Bij = Bij;
	}
	
	public void setPi(double[] pi) {
		this.pi = pi;
	}
	
	public void setStateList(String[] stateList) {
		this.stateList = stateList;
		setStateIndexMap();
	}
	
	public void setObservationList(String[] observationList) {
		this.observationList = observationList;
		setObservationIndexMap();
	}
	
	public String[] getStateList() {
		return stateList;
	}
	
	public String[] getObservationList() {
		return observationList;
	}
	
	private void setStateIndexMap() {
		for(int i=0;i<stateList.length;i++) {
			stateIndexMap.put(stateList[i], i);
		}
	}
	
	private void setObservationIndexMap() {
		for(int i=0;i<observationList.length;i++) {
			observationIndexMap.put(observationList[i], i);
		}
	}
	
	/**
	 * 保存模型，第一行是stateList（状态列表），第二行是observationList（观测列表），第三行是pi（初始概率）
	 * 第四行是Aij（状态转移概率分布矩阵），第五行是Bij（观测概率分布矩阵）
	 * @param modelFile
	 * @throws IOException
	 */
	public void save(File modelFile) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(modelFile));
		
		//写入状态列表
		for(int i=0;i<stateList.length;i++) {
			if(i==(stateList.length-1))
				writer.write(stateList[i]);
			else
				writer.write(stateList[i]+",");
		}
		writer.newLine();
		
		//写入观测列表
		for(int i=0;i<observationList.length;i++) {
			if(i==(observationList.length-1))
				writer.write(observationList[i]);
			else
				writer.write(observationList[i]+",");
		}
		writer.newLine();
		
		//写入pi
		for(int i=0;i<pi.length;i++) {
			if(i==(pi.length-1))
				writer.write(String.valueOf(pi[i]));
			else
				writer.write(pi[i]+",");
		}
		writer.newLine();
		
		//写入Aij
		for(int i=0;i<Aij.length;i++) {
			for(int j=0;j<Aij[0].length;j++) {
				if(j==(Aij[0].length-1)&i<(Aij.length-1))
					writer.write(Aij[i][j]+";");
				else {
					if(j==(Aij[0].length-1))
						writer.write(String.valueOf(Aij[i][j]));
					else
						writer.write(Aij[i][j]+",");
				}
			}
		}
		writer.newLine();
		
		//写入Bij
		for(int i=0;i<Bij.length;i++) {
			for(int j=0;j<Bij[0].length;j++) {
				if(j==(Bij[0].length-1)&i<(Bij.length-1))
					writer.write(Bij[i][j]+";");
				else {
					if(j==(Bij[0].length-1))
						writer.write(String.valueOf(Bij[i][j]));
					else
						writer.write(Bij[i][j]+",");
				}
			}
		}
		writer.close();
	}
	
	/**
	 * 从文件中加载模型参数
	 * @param modelFile
	 * @throws IOException
	 */
	public void load(File modelFile) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(modelFile));
		String temp = null;
		int count = 0;
		while((temp=reader.readLine())!=null) {
			switch(count) {
			case 0:
				stateList = temp.split(",");
				setStateIndexMap();
				break;
			case 1:
				observationList = temp.split(",");
				setObservationIndexMap();
				break;
			case 2:
				String[] piList = temp.split(",");
				pi = new double[piList.length];
				for(int i=0;i<piList.length;i++) {
					pi[i] = (double)Double.valueOf(piList[i]);
				}
				break;
			case 3:
				Aij = getMatrix(temp);
				break;
			case 4:
				Bij = getMatrix(temp);
				break;
			default:
				System.out.println("It does not have that value:" + count);
			}
			count++;
		}
		reader.close();
	}
	
	/**
	 * 在模型文件中，aij和Bij矩阵各占一行，（形式为：1,2,3;3,5,2;2,4,5）
	 * 该函数将模型文件中的Aij和Bij进行还原，得到Aij和Bij矩阵
	 * @param temp
	 * @return
	 */
	private double[][] getMatrix(String temp){
		String[] line = temp.split(";");
		int num = line[0].split(",").length;
		double[][] temporary = new double[line.length][num];
		for(int i=0;i<line.length;i++) {
			String[] element = line[i].split(",");
			for(int j=0;j<num;j++) {
				temporary[i][j] = Double.valueOf(element[j]);
			}
		}
		return temporary;
	}

}
