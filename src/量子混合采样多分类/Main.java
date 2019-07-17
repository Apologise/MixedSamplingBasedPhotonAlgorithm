package 量子混合采样多分类;

import java.io.File;
import java.io.FileWriter;

public class Main {
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String[] dataSets = { "glass1", "pima", "glass0", "yeast1", "vehicle1", "glass0123vs456", "ecoli1",
				"newthyroid1", "newthyroid2", "ecoli2", "glass6", "yeast3", "ecoli3", "glass016v2", "yeast1v7",
				"glass4", "glass5", "yeast2v8", "yeast4", "yeast6" };
		for(int cnt = 0; cnt < 10; ++cnt) {
		for (int set = 0; set < dataSets.length; ++set) {
			System.out.println("当前运行数据集为：" + dataSets[set]);
			for (int cls = 0; cls <= 0; ++cls) {

				File file = new File(
						"实验结果\\完整版实验均值\\" + Enum_Classifier.values()[cls] + "2019/7/15完整版实验结果10次均值"+cnt+".dat");
				// 创建文件
				file.createNewFile();
				// creates a FileWriter Object
				FileWriter fw = new FileWriter(file, true);
				try {
					fw.write(dataSets[set] + ": ");
					Setting setting = new Setting(20, 500, Enum_Classifier.values()[cls]);
					InstancesSet instancesSet = new InstancesSet(dataSets[set], setting);
					double sum = 0;
					for (int i = 0; i <= 0; ++i) {
						instancesSet.initializeInstancesSet(i);
						QuantumModel quantumModel = new QuantumModel(setting, instancesSet);
						quantumModel.run();
						sum += quantumModel.gBestIndividual.calAUC(Enum_Classifier.values()[cls]);
						;
						double temp = quantumModel.gBestIndividual.calAUC1(Enum_Classifier.values()[cls]);
					}
					sum /= 5;
					fw.write("" + sum + "\n");
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					fw.flush();
					fw.close();
				}
			}
		}
		}
	}
}
