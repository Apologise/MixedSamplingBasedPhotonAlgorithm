package 量子混合采样多分类;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class InstanceDao {
	public Instances loadDataFromFile(String filePath) throws Exception {
		Instances rawData = DataSource.read(filePath);
		if(rawData.classIndex() == -1) {
			rawData.setClassIndex(rawData.numAttributes()-1);
		}
		return rawData;
	}
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		InstanceDao dao = new InstanceDao();
		Instances trainInstances = dao.loadDataFromFile("dataset/wine-5-fold/wine-5-1tra.arff");
		Instances testInstances = dao.loadDataFromFile("dataset/wine-5-fold/wine-5-1tst.arff");
		//对训练样本进行处理
		List<List<Instance>> instancesByClass = new ArrayList<List<Instance>>();
		int numOfClass = trainInstances.numClasses();
		for(int i = 0; i < numOfClass; ++i) {
			List<Instance> list = new ArrayList<>();
			instancesByClass.add(list);
		}
		for(Instance inst: trainInstances) {
			int classValue = (int)inst.classValue();
			instancesByClass.get(classValue).add(inst);
		}
		Instances trainInstances0v1 = new Instances(trainInstances);
		trainInstances0v1.clear();
		for(Instance inst: instancesByClass.get(0)) {
			trainInstances0v1.add(inst);
		}
		for(Instance inst: instancesByClass.get(1)) {
			trainInstances0v1.add(inst);
		}
		
		
		//对测试集进行处理
		List<List<Instance>> instancesByClassTest = new ArrayList<List<Instance>>();
		for(int i = 0; i < numOfClass; ++i) {
			List<Instance> list = new ArrayList<>();
			instancesByClassTest.add(list);
		}
		for(Instance inst: testInstances) {
			int classValue = (int)inst.classValue();
			instancesByClassTest.get(classValue).add(inst);
		}
		Instances testInstances0v1 = new Instances(trainInstances);
		testInstances0v1.clear();
		for(Instance inst: instancesByClassTest.get(0)) {
			testInstances0v1.add(inst);
		}
		for(Instance inst: instancesByClassTest.get(1)) {
			testInstances0v1.add(inst);
		}
		Classifier classifier = new J48();	
		classifier.buildClassifier(trainInstances0v1);
		Evaluation evaluation = new Evaluation(trainInstances0v1);
		evaluation.evaluateModel(classifier, testInstances0v1);
		double fitness = evaluation.areaUnderROC(0);
		System.out.println(fitness);
	}

}
