package ���ӻ�ϲ��������;

import java.io.File;
import java.io.FileWriter;

public class Main {
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String[] dataSets = { "glass1", "pima", "glass0", "yeast1", "vehicle1", "glass0123vs456", "ecoli1",
				"newthyroid1", "newthyroid2", "ecoli2", "glass6", "yeast3", "ecoli3", "glass016v2", "yeast1v7",
				"glass4", "glass5", "yeast2v8", "yeast4", "yeast6" };
	
		for (int set = 13; set <  dataSets.length; ++set) {
			System.out.println("��ǰ�������ݼ�Ϊ��" + dataSets[set]);
			for (int cls = 0; cls < 4; ++cls) {
				
				File file = new File("ʵ����\\dev0���ڱ߽��ʵ����\\"+Enum_Classifier.values()[cls]+".dat");
		        // �����ļ�
		        file.createNewFile();
		        // creates a FileWriter Object
				FileWriter fw = new FileWriter(file,true);
				try {
				fw.write(dataSets[set]+": ");
				Setting setting = new Setting(20, 500, Enum_Classifier.values()[cls]);
				InstancesSet instancesSet = new InstancesSet(dataSets[set], setting);
				double sum = 0;
				for (int i = 0; i <  5; ++i) {
					instancesSet.initializeInstancesSet(i);
					QuantumModel quantumModel = new QuantumModel(setting, instancesSet);
					quantumModel.run();
					sum += quantumModel.gBestIndividual.calAUC(Enum_Classifier.values()[cls]);;
				}
				sum /= 5;
				fw.write(""+sum+"\n");
				}catch(Exception e) {
					System.out.println("�����쳣");
				}finally {
					fw.flush();
					fw.close();
				}
			}
		}

	}

}
