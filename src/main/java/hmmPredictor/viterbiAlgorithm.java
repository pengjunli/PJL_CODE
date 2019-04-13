package hmmPredictor;

import java.util.HashMap;

import pjl_hmm.pjlhmm.Hmm;

/**
 * 借鉴hanks的代码，在此基础上进行的修改，网址：https://github.com/hankcs/Viterbi
 * @author pjl
 *
 */
public class viterbiAlgorithm {
	/**
	 * 采用维特比算法求解观测序列的最优路径
	 * @param observationSequences  观测序列     obs为观测序列每个元素对应观测列表中索引的序列
	 * @param model    HMM模型，包含初始概率、状态转移概率分布矩阵、观测概率分布矩阵
	 * @return  最优路径
	 */
    public int[] compute(String[] observationSequences,Hmm model){
    	//加载模型内容
    	double[] pi = model.getPiVector();
    	double[][] Aij = model.getAijMatrix();
    	double[][] Bij = model.getBijMatrix();
    	String[] stateList = model.getStateList();
    	HashMap<String,Integer> sateIndexMap = model.getStateIndexMap();
    	HashMap<String,Integer> observationIndexMap = model.getObservationIndexMap();
    	
    	
    	int[] obs = getSequenceIndex(observationSequences,observationIndexMap);
    	int[] states = getSequenceIndex(stateList, sateIndexMap);
    	
    	double[][] V = new double[obs.length][states.length];
        int[][] path = new int[states.length][obs.length];  //在李航的统计学习方法中

        for (int y : states)  //初始化，在t=1时，对每个状态i，求状态为i的观测o1的概率
        {
            V[0][y] = pi[y] * Bij[y][obs[0]];
            path[y][0] = y;
        }

        for (int t = 1; t < obs.length; ++t)
        {
            int[][] newpath = new int[states.length][obs.length];

            for (int y : states)
            {
                double prob = -1;
                int state;
                for (int y0 : states)
                {
                    double nprob = V[t - 1][y0] * Aij[y0][y] * Bij[y][obs[t]];
                    if (nprob > prob)
                    {
                        prob = nprob;
                        state = y0;
                        // 记录最大概率
                        V[t][y] = prob;
                        // 记录路径,将0-t的内容从pathcopy到newpath中
                        System.arraycopy(path[state], 0, newpath[y], 0, t);  
                       
                        newpath[y][t] = y0;   //记录概率最大路径的前一个状态y0  newpath[y][t]=arg max(nprob(y0))
                    }
                }
            }

            path = newpath;  //更新path
        }

        //最优路径回溯
        double prob = -1;
        int[] optimalPath = new int[obs.length];
        for (int y : states)
        {
            if (V[obs.length - 1][y] > prob)
            {
                prob = V[obs.length - 1][y];
                optimalPath[obs.length-1] = y;
            }
        }
        for(int i=obs.length-2;i>=0;i--) {
        	optimalPath[i] = path[optimalPath[i+1]][i+1];
        }

        return optimalPath;
    }
    
    /**
     * 得到序列对应列表的索引，如观测序列每个值在观测列表中的位置值；状态列表对应的位置值
     * @param sequence
     * @param List
     * @return 对应的索引值
     */
    private int[] getSequenceIndex(String[] sequence,HashMap<String,Integer> List) {
    	int[] sequenceIndex = new int[sequence.length];
    	for(int i=0;i<sequenceIndex.length;i++) {
    		sequenceIndex[i] = List.get(sequence[i]);
    	}
    	return sequenceIndex;
    }

}
