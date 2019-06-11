package ���ӻ�ϲ��������;

import java.util.ArrayList;
import java.util.List;

import weka.core.Instance;
import weka.core.Instances;

/*
 * Author: apolo
 * Time:   2019.5.22
 * INFO:   ������ܣ����ڷ��࣬���շ���һ���ض��Ķ����ָ�꣨MAUC, MGmean��
 * */
public class ClassifyModel {
	public List<Instance> m_train;
	public List<Instance> m_test;
	//��m_train��m_test���������
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
	 * trianΪ�Ѿ�������ϲ���������ݼ�
	 * testΪ��֤����
	 * */
	public ClassifyModel(List<Instance> train, List<Instance> test) {
		m_train = train;
		m_test = test;
	}
	
	/*
	 * TODO: ��m_trainMajorityInstances���������в��
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
				//������ΪclassLabel��List���������������
				m_trainInstancesByClass.get(classLabel).add(inst);
			}
	}
	
	/*
	 * TODO: �õ������ľ������
	 * RETURN:
	 * */
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
