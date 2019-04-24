package ���ӻ�ϲ��������;

import java.util.*;

import weka.core.EuclideanDistance;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;

public class PhotonModel {
	public List<ArrayList<Double>> distanceMatrix;	//�������֮��ľ������
	public Instances rawData;
	public Instances originData;
	/*
	 * TODO: �����ļ��ж�ȡ�����ݹ淶��
	 * RETURN: �õ�һ���淶�������ݼ���Ȼ�󸶸�orginData;
	 * */
	public Instances normalizeData(Instances rawData) throws Exception {
		Normalize normalizer = new Normalize();
		normalizer.setInputFormat(rawData);
		rawData = Filter.useFilter(rawData, normalizer);
		return rawData;
	}
	
	public List<List<Double>> initializeDistanceMatrix(Instances rawData) {
		List<List<Double>> distanceMatrix = new ArrayList<List<Double>>();
		for(Instance first: rawData) {
			List<Double> rowDistance = new ArrayList<>();
			for(Instance second: rawData) {
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
	
	public static void printInstances(Instances data) {
		for(Instance inst: data) {
			System.out.println(inst);
		}
	}
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		InstanceDao instanceDao = new InstanceDao();
		PhotonModel photonModel = new PhotonModel();
		photonModel.rawData = instanceDao.loadDataFromFile("dataset/pima.arff");
		
	}
}
