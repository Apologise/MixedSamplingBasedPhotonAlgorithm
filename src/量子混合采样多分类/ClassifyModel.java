package 量子混合采样多分类;

import java.util.ArrayList;
import java.util.List;

import weka.core.Instance;
import weka.core.Instances;

/*
 * Author: apolo
 * Time:   2019.5.22
 * INFO:   多分类框架，用于分类，最终返回一个特定的多分类指标（MAUC, MGmean）
 * */
public class ClassifyModel {
	public List<Instance> m_train;
	public List<Instance> m_test;
	//将m_train和m_test均按类别拆分
	public List<List<Instance>> m_trainInstancesByClass;
	
	public double calMAUC() {
		double mAuc = 0.0d;
		return mAuc;
	}
	public double calMGMean() {
		double mGmean = 0.0d;
		return mGmean;
	}
	/*
	 * trian为已经经过混合采样后的数据集
	 * test为验证集合
	 * */
	public ClassifyModel(List<Instance> train, List<Instance> test) {
		m_train = train;
		m_test = test;
	}
	
	/*
	 * TODO: 将m_trainMajorityInstances按照类别进行拆分
	 * */
	public void splitTrainByClass(List<Instance> m_train) {
			int numOfClass = m_train.get(0).numClasses();
			List<List<Instance>> instancesByClass = new ArrayList<List<Instance>>();
			for(int i = 0; i < numOfClass; ++i) {
				List<Instance> temp = new ArrayList<>();
				m_trainInstancesByClass.add(temp);
			}
			for(Instance inst: m_train) {
				int classLabel = (int)inst.classValue();
				//获得类标为classLabel的List，并将其加入其中
				m_trainInstancesByClass.get(classLabel).add(inst);
			}
	}
	
	/*
	 * TODO: 得到样本的距离矩阵
	 * RETURN:
	 * */
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
