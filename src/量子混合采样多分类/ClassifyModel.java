package 量子混合采样多分类;

import java.util.List;

import weka.core.Instance;
import weka.core.Instances;

/*
 * Author: apolo
 * Time:   2019.5.22
 * INFO:   多分类框架，用于分类，最终返回一个特定的多分类指标（MAUC, MGmean）
 * */
public class ClassifyModel {
	public Instances m_train;
	public Instances m_test;
	//将m_train和m_test均按类别拆分
	public List<List<Instance>> train_majorityInstances;
	public List<List<Instance>> test_majorityInstances;
	public List<List<Instance>> train_minorityInstances;
	public List<List<Instance>> test_minorityInstances;
	
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
	public ClassifyModel(Instances train, Instances test) {
		m_train = train;
		m_test = test;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
