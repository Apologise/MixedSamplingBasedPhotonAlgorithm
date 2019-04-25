package 量子混合采样多分类;

import java.util.ArrayList;
import java.util.FormatFlagsConversionMismatchException;
import java.util.List;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.neighboursearch.NearestNeighbourSearch;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;

public class InstancesSet {

	public List<ArrayList<Double>> distanceMatrix;	//存放样本之间的距离矩阵
	public Instances rawInstances;	//未经任何处理的原始数据
	public Instances originInstances;	//经过了归一化、标准化和去除了噪声的数据
	public int K = 4;	//判定是否为噪声的参数K
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

	/*
	 * TODO：去除数据集中的噪声
	 * 当一个样本周围的K近邻都与其类标不相同时，则判定为噪声样本
	 * RETURN：返回一个噪声样本集合
	 * */
	public Instances removeNoiseInstance() {
		//计算距离矩阵
		List<List<Double>> distanceMatrix = initializeDistanceMatrix(rawInstances);
		List<Instance> noisyInstances = new ArrayList<>();
		for(int i = 0; i < rawInstances.size(); ++i) {
			//计算第i个样本的k近邻
			List<Instance> knearestInstances = knearestNeighbours(i, distanceMatrix, rawInstances);
			//根据K近邻的类标来判断是否为噪声样本
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
		//移除噪声样本
		for(int i = 0; i < noisyInstances.size(); ++i) {
			rawInstances.remove(noisyInstances.get(i));
		}
		return rawInstances;
	}
	
	public List<Instance> knearestNeighbours(int instanceIndex, List<List<Double>> distanceMatrix, Instances instancesSet){
		List<Instance> knearestNeighbours = new ArrayList<Instance>();
		//对距离进行排序
		//取出inst样本所对应的距离向量
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
