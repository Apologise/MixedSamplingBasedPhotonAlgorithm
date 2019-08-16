package 量子混合采样多分类;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;

public class Test {
	public static void main(String[] args) throws Exception {
		InstanceDao instanceDao = new InstanceDao();
		Instances rawInstances = instanceDao.loadDataFromFile("多分类数据集/balance-5-fold/balance-5-1tra.arff");
	//	System.out.println(rawInstances.get(0).toString());
		Instance instance = rawInstances.get(0);
		Instance instance1 = (Instance) instance.copy();
		instance.setValue(0, -1);
		instance1.setValue(0, -2);
		System.out.println(instance.toString());
		System.out.println(instance1.toString());
		System.out.println(instance.toString());
		/*
		for(Instance inst: rawInstances) {
			System.out.println(inst.toString());
			int classLabel = (int)inst.classValue();
			if(classLabel == 2) {
				inst.setClassValue(classLabel-1);
			}
			System.out.println(inst.toString());
		}
		
		Instances testInstances = instanceDao.loadDataFromFile("多分类数据集/balance-5-fold/balance-5-1tst.arff");
		Classifier cls = new J48();
		cls.buildClassifier(rawInstances);
		Evaluation eval = new Evaluation(rawInstances);
		eval.evaluateModel(cls, testInstances);
		System.out.println("ROC:"+eval.areaUnderROC(1));
		double[][] confusionMatrix = eval.confusionMatrix();
		for(int i = 0; i < confusionMatrix.length; ++i) {
			for(int j = 0; j < confusionMatrix[i].length; ++j) {
				System.out.print(confusionMatrix[i][j]+ " ");
			}
			System.out.println();
		}
		*/
	}

}
