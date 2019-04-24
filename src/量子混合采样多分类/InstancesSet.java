package ���ӻ�ϲ��������;

import java.util.ArrayList;
import java.util.List;

import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;

public class InstancesSet {

	public List<ArrayList<Double>> distanceMatrix;	//�������֮��ľ������
	public Instances rawInstances;
	public Instances originInstances;
	/*
	 * TODO: �����ļ��ж�ȡ�����ݹ淶��
	 * RETURN: �õ�һ���淶�������ݼ���Ȼ�󸶸�orginInstances;
	 * */
	public Instances normalizeInstances(Instances rawInstances) throws Exception {
		Normalize normalizer = new Normalize();
		normalizer.setInputFormat(rawInstances);
		rawInstances = Filter.useFilter(rawInstances, normalizer);
		return rawInstances;
	}
	
	public List<List<Double>> initializeDistanceMatrix(Instances rawInstances) {
		List<List<Double>> distanceMatrix = new ArrayList<List<Double>>();
		for(Instance first: rawInstances) {
			List<Double> rowDistance = new ArrayList<>();
			for(Instance second: rawInstances) {
				if(first == second) {
					rowDistance.add(Double.MAX_VALUE);
				}else {
					rowDistance.add(calDistance(first, second));
				}
			}
			distanceMatrix.add(rowDistance);
		}
		return distanceMatrix;
	}

	
	public static double calDistance(Instance first, Instance second) {
		double distance = 0;
		for(int i = 0; i < first.numAttributes()-1; ++i) {
			double diff = first.value(i) - second.value(i);
			distance += diff;
		}
		return distance;
	}
	
	public static void printInstances(Instances Instances) {
		for(Instance inst: Instances) {
			System.out.println(inst);
		}
	}
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		InstanceDao instanceDao = new InstanceDao();
		InstancesSet instancesSet = new InstancesSet();
		instancesSet.rawInstances = instanceDao.loadDataFromFile("Instancesset/pima.arff");
	}

}
