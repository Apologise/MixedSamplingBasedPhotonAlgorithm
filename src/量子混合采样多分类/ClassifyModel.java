package ���ӻ�ϲ��������;

import java.util.List;

import weka.core.Instance;
import weka.core.Instances;

/*
 * Author: apolo
 * Time:   2019.5.22
 * INFO:   ������ܣ����ڷ��࣬���շ���һ���ض��Ķ����ָ�꣨MAUC, MGmean��
 * */
public class ClassifyModel {
	public Instances m_train;
	public Instances m_test;
	//��m_train��m_test���������
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
	 * trianΪ�Ѿ�������ϲ���������ݼ�
	 * testΪ��֤����
	 * */
	public ClassifyModel(Instances train, Instances test) {
		m_train = train;
		m_test = test;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
