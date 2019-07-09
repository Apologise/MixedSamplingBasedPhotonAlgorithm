package 量子混合采样多分类;

import java.io.File;
import java.io.FileWriter;

public class Main {
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String[] dataSets = { "glass1", "pima", "glass0", "yeast1", "vehicle1", "glass0123vs456", "ecoli1",
				"newthyroid1", "newthyroid2", "ecoli2", "glass6", "yeast3", "ecoli3", "glass016v2", "yeast1v7",
				"glass4", "glass5", "yeast2v8", "yeast4", "yeast6" };
	
		for (int set = 0; set <= 5; ++set) {
			System.out.println("当前运行数据集为：" + dataSets[set]);
			for (int cls = 0; cls <= 3; ++cls) {
				
				File file = new File("实验结果\\dev0基于边界的实验结果(最大值)\\"+Enum_Classifier.values()[cls]+"K邻居集成集成（过采样+KMeans+修复BUG版+0.5+调试Bug版+少数类移除BUG）.dat");
		        // 创建文件
		        file.createNewFile();
		        // creates a FileWriter Object
				FileWriter fw = new FileWriter(file,true);
				try {
				fw.write(dataSets[set]+": ");
				Setting setting = new Setting(10, 500, Enum_Classifier.values()[cls]);
				InstancesSet instancesSet = new InstancesSet(dataSets[set], setting);
				double sum = 0;
				for (int i = 0; i <=  4; ++i) {
					instancesSet.initializeInstancesSet(i);
					QuantumModel quantumModel = new QuantumModel(setting, instancesSet);
					quantumModel.run();
					sum += quantumModel.gBestIndividual.calAUC(Enum_Classifier.values()[cls]);;
//					double  temp = quantumModel.gBestIndividual.calAUC1(Enum_Classifier.values()[cls]);
				}
				sum /= 5;
				fw.write(""+sum+"\n");
				}catch(Exception e) {
					e.printStackTrace();
				}finally {
					fw.flush();
					fw.close();
				}
			}
		}
	}
}
