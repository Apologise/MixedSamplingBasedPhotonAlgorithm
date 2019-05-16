package 量子混合采样多分类;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.FormatFlagsConversionMismatchException;
import java.util.List;
import java.util.Random;
import java.util.function.DoubleToLongFunction;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.SMO;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.MakeIndicator;


public class GenerateSample {
	public Setting setting;
	public GenerateSample(Setting setting) {
		this.setting = setting;
	}
	public  void generateSample(Instance inst,Instances inputData,Instances majority, List<Instance> output ,int N) {
		List<Integer> knn = calNeighborsWithDensity(inst, inputData, majority);
		
			int IR = N;
			while(IR != 0) {
				double[] values = new double[inputData.numAttributes()];
				for(int j = 0; j < inputData.numAttributes()-1; ++j) {
					double gap = Math.random();
					int indexK = (int)(gap*setting.K);
					/*
					if(gap<0.2) {
						gap = 0.2;
					}else if(gap >0.8) {
						gap = 0.8;
					}
					*/
					double diff = inputData.get(knn.get(indexK)).value(j)-inst.value(j);
					values[j] = inst.value(j) + gap*diff;
				}
				values[inputData.numAttributes()-1] = inputData.get(0).classValue();
				output.add(inputData.get(0).copy(values));
				IR--;
		}
	}
	

	
	public static double calGap(Instance a, Instance b, Instances majority) {
		double gap = 0.5;
		ArrayList<Instance> knn_majority = new ArrayList<>();
		//寰楀埌a涓巄涔嬮棿鐨勬墍鏈夊鏁扮被鏍锋湰
		double r = calDistance(a,b)/2;
		//姹傚緱涓偣
		double[] mean = new double[a.numAttributes()-1];
		for(int i = 0; i < a.numAttributes()-1; ++i) {
			mean[i] = (a.value(i)+b.value(i))/2.0;
		}
		for(Instance inst: majority) {
			double temp_distance = calDistance(mean, inst);
			if(temp_distance < r) {
				knn_majority.add(inst);
			}
		}
		if(knn_majority.size()!=0) {
			System.out.println("======="+knn_majority.size());
		}
		gap = calGapForError(a,b,knn_majority);
		return gap;
	}
	
	//璁＄畻a,b涓ょ偣涔嬮棿鐨勫瘑搴�
	public static int calDensity(Instance a, Instance b, Instances majority ) {
		int density = 0;
		//寰楀埌a涓巄涔嬮棿鐨勬墍鏈夊鏁扮被鏍锋湰
		double r = calDistance(a,b)/2;
		//姹傚緱涓偣
		double[] mean = new double[a.numAttributes()-1];
		for(int i = 0; i < a.numAttributes()-1; ++i) {
			mean[i] = (a.value(i)+b.value(i))/2.0;
		}
		for(int i = 0; i<majority.size(); ++i) {
			double temp_distance = calDistance(mean, majority.get(i));
			if(temp_distance < r) {
				density++;
			}
		}
		return density;
	}
	
	//鏍规嵁涓ょ偣涔嬮棿鐨勫瘑搴︽潵閫夋嫨杩戦偦
	public List<Integer> calNeighborsWithDensity(Instance inst, Instances minority, Instances majority){
		List<Integer> knn = new ArrayList<Integer>();
		Density[] den = new Density[minority.size()];
		//璁＄畻鍑烘墍鏈夌偣鍒癷nst涔嬮棿鐨勮窛绂讳笌瀵嗗害
		int[] density = new int[minority.size()];
	///	System.out.println(minority.size());
		for(int ii = 0; ii < minority.size(); ++ii) {
			den[ii] = new Density();
			den[ii].density = calDensity(inst, minority.get(ii), majority);
			den[ii].distance = calDistance(inst, minority.get(ii));
			den[ii].index = ii;
	
		}
		//鍏堝den鏁扮粍杩涜density鍗囧簭鎺掑簭
		Arrays.sort(den, new Comparator<Density>() {
			@Override
			public int compare(Density o1, Density o2) {
				// TODO Auto-generated method stub
				if(o1.density > o2.density) {
					return 1;
				}else if(o1.density < o2.density) {
					return -1;
				}
				return 0;
			}
		});
		Arrays.sort(den, new Comparator<Density>() {

			@Override
			public int compare(Density o1, Density o2) {
				// TODO Auto-generated method stub
				if(o1.distance > o2.distance) {
					return 1;
				}else if(o1.distance < o2.distance){
					return -1;
				}
				return 0;
			}
		});
		Arrays.sort(den,new Comparator<Density>() {

			@Override
			public int compare(Density o1, Density o2) {
				// TODO Auto-generated method stub
					double area1 = o1.density/(o1.distance*o1.distance*4+1);
					double area2 = o2.density/(o2.distance*o2.distance*4+1);
					if(area1 > area2 ) {
						return 1;
					}else if(area1 < area2 ) {
						return -1;
					}
				return 0;
			}	
		});

		for(int ii = 0,cnt=0; ii < den.length&& cnt < setting.K;++ii) {
			if(den[ii].density < 1e-5&& den[ii].distance < 1e-5) {

			}else {
				knn.add(den[ii].index);
				cnt++;
			}
		}
		/*
		System.out.print("[");
		for(int ii = 0; ii < knn.size(); ++ii) {
			if(ii== 0) {
				System.out.print(knn.get(ii));
			}else {
				System.out.print(","+knn.get(ii));
			}
		}
		System.out.print("],");
		*/
		return knn;
	}
	
	
	
	public static double calGapForError(Instance a, Instance b,ArrayList<Instance> knn_majority) {
		double gap=0.2;
		if(knn_majority.size()==0) {
			gap = 0.5;
			return gap;
		}
		for(double i = 0.2; i<=0.8; i=i+0.01) {
			double[] temp = new double[a.numAttributes()-1];
			for(int j = 0; j < temp.length; ++j) {
				temp[j] = a.value(j)+i*(b.value(j)-a.value(j));
			}
			double error = 0.0f;
			double error_max = 0.0f;
			for(Instance inst:knn_majority) {
				
				error += calDistance(temp,inst);
			}
			if(error > error_max) {
				error_max = error;
				gap = i;
			}
		}
		return gap;
	}
	/*
	public static Instances generateSample1(Instances inputData, int N) {
		
		Instances systhetic = new Instances(inputData);
		systhetic.clear();
		for(int i = 0; i < inputData.size(); ++i) {
			List<Integer> knn = kNeighbors(inputData, inputData.get(i));
			int IR = N;
			while(IR != 0) {
				double[] values = new double[inputData.numAttributes()];
				Random random = new Random();
				int index = random.nextInt(SETTING.K);
				for(int j = 0; j < inputData.numAttributes()-1; ++j) {
					double gap = Math.random();
					double diff = inputData.get(knn.get(index)).value(j)-inputData.get(i).value(j);
					values[j] = inputData.get(i).value(j) + gap*diff;
				}
				values[inputData.numAttributes()-1] = inputData.get(0).classValue();
				systhetic.add(inputData.get(0).copy(values));
				IR--;
			}
		}
		return systhetic;
	}
	*/
	
	//鏍规嵁鍒扮粓鐐圭殑璺濈鏉ョ畻杩戦偦
	public  List<Integer> kNeighbors(Instances inputData, Instances majority,Instance inst) {
		List<Integer> knn = new ArrayList<>();
		List<Double> distances = new ArrayList<>(); 
		for(int i = 0; i < inputData.size(); ++i) {
			double tempDistance = calDistance(inst, inputData.get(i))
					+calDistanceWithinR(inst, inputData.get(i), majority);
			distances.add(tempDistance);
		}
		int[] flag = new int[inputData.size()];
		flag[inputData.indexOf(inst)] = 1;
		for(int i = 0; i < setting.K; ++i) {

			double temp = Double.MAX_VALUE;
			int index = -1;
			for(int j = 0; j < inputData.size(); ++j) {
			
				if(flag[j] == 0) {
					if(temp > distances.get(j)) {
						temp = distances.get(j);
						index = j;
					}
				}
			}
			flag[index] = 1;
			knn.add(index);
		}
		return  knn;
		
	}
	public static double calDistance(Instance a, Instance b) {
		double distance = 0.0f;
		
		for(int i = 0; i < a.numAttributes()-1; ++i) {
			distance += (a.value(i) - b.value(i))*(a.value(i) - b.value(i));
		}
		distance = Math.sqrt(distance);
		return distance;
	}
	
	public static double calDistance(double[] mean, Instance a) {
		double distance = 0.0f;
		for(int i = 0; i < a.numAttributes()-1; ++i) {
			double temp = a.value(i)-mean[i];
			distance += temp*temp;
		}
		distance = Math.sqrt(distance);
		return distance;
	}
	
	//鍒ゆ柇涓や釜鏍锋湰鏄惁鐩哥瓑
	public static boolean InstanceisEqual(Instance a, Instance b) {
		boolean flag = true;
		for(int i = 0; i < a.numAttributes()-1; ++i) {
			double eps = 1e-6;
			if(Math.abs(a.value(i)-b.value(i)) < eps) {
				
			}else {
				flag = false;
				break;
			}
		}
		return flag;
	}
	//璁＄畻鍦ㄥ渾鍦堝唴鐨勮窛绂�
	public static double calDistanceWithinR(Instance a, Instance b, Instances majority) {
		double result = 0.0f;
		double r = calDistance(a,b)/2;
		//姹傚緱涓偣
		double[] mean = new double[a.numAttributes()-1];
		for(int i = 0; i < a.numAttributes()-1; ++i) {
			mean[i] = (a.value(i)+b.value(i))/2.0;
		}
		for(Instance inst: majority) {
			double temp_distance = calDistance(mean, inst);
			if(temp_distance < r) {
				result = result +  temp_distance;
			}
		}
		return result;
	}
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
	
	
	}

}
