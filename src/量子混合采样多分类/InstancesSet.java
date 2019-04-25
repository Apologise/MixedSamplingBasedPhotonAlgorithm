package ���ӻ�ϲ��������;

import java.util.ArrayList;
import java.util.FormatFlagsConversionMismatchException;
import java.util.List;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.neighboursearch.NearestNeighbourSearch;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;

public class InstancesSet {

	public List<ArrayList<Double>> distanceMatrix;	//�������֮��ľ������
	public Instances rawInstances;	//δ���κδ����ԭʼ����
	public Instances originInstances;	//�����˹�һ������׼����ȥ��������������
	public int K = 4;	//�ж��Ƿ�Ϊ�����Ĳ���K
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

	/*
	 * TODO��ȥ�����ݼ��е�����
	 * ��һ��������Χ��K���ڶ�������겻��ͬʱ�����ж�Ϊ��������
	 * RETURN������һ��������������
	 * */
	public Instances removeNoiseInstance() {
		//����������
		List<List<Double>> distanceMatrix = initializeDistanceMatrix(rawInstances);
		List<Instance> noisyInstances = new ArrayList<>();
		for(int i = 0; i < rawInstances.size(); ++i) {
			//�����i��������k����
			List<Instance> knearestInstances = knearestNeighbours(i, distanceMatrix, rawInstances);
			//����K���ڵ�������ж��Ƿ�Ϊ��������
			boolean isNoiseInstance = true;
			for(int j = 0; j < knearestInstances.size(); ++j) {
				if((int)rawInstances.get(i).classValue()  == (int)knearestInstances.get(j).classValue()) {
					isNoiseInstance = false;
					break;
				}
			}
			if(isNoiseInstance) {
				noisyInstances.add(rawInstances.get(i));
			}
		}
		//�Ƴ���������
		for(int i = 0; i < noisyInstances.size(); ++i) {
			rawInstances.remove(noisyInstances.get(i));
		}
		return rawInstances;
	}
	
	public List<Instance> knearestNeighbours(int instanceIndex, List<List<Double>> distanceMatrix, Instances instancesSet){
		List<Instance> knearestNeighbours = new ArrayList<Instance>();
		//�Ծ����������
		//ȡ��inst��������Ӧ�ľ�������
		List<Double> distance = distanceMatrix.get(instanceIndex);
		int[] visited = new int[distance.size()];
		int tempK = K;
		while(tempK!=0) {
			double temp = Double.MAX_VALUE;
			int index = -1;
			for(int i = 0; i < distance.size(); ++i) {
				if(visited[i] == 0 && temp > distance.get(i)) {
					temp = distance.get(i);
					index = i;
				}
			}
			visited[index] = 1;
			knearestNeighbours.add(instancesSet.get(index));
			tempK--;
		}
		return knearestNeighbours;
	}
	
	public static double calDistance(Instance first, Instance second) {
		double distance = 0;
		for(int i = 0; i < first.numAttributes()-1; ++i) {
			double diff = first.value(i) - second.value(i);
			distance += diff*diff;
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
		instancesSet.rawInstances = instanceDao.loadDataFromFile("dataset/pima.arff");
		instancesSet.rawInstances = instancesSet.normalizeInstances(instancesSet.rawInstances);
		instancesSet.rawInstances = instancesSet.removeNoiseInstance();
		instancesSet.removeNoiseInstance();
		
	}
}
