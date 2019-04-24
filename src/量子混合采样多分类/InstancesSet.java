package 量子混合采样多分类;

import java.util.ArrayList;
import java.util.List;

import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;

public class InstancesSet {

	public List<ArrayList<Double>> distanceMatrix;	//存放样本之间的距离矩阵
	public Instances rawInstances;
	public Instances originInstances;
	/*
	 * TODO: 将从文件中读取的数据规范化
	 * RETURN: 得到一个规范化的数据集，然后付给orginInstances;
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
