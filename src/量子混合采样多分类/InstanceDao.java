package 量子混合采样多分类;

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
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
