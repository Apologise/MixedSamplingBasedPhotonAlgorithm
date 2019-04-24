package 量子混合采样多分类;

import java.util.*;

import weka.core.EuclideanDistance;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;

public class PhotonModel {
	public List<ArrayList<Double>> distanceMatrix;	//存放样本之间的距离矩阵
	public Instances rawData;
	public Instances originData;
	/*
	 * TODO: 将从文件中读取的数据规范化
	 * RETURN: 得到一个规范化的数据集，然后付给orginData;
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
