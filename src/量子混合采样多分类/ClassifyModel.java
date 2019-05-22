package 量子混合采样多分类;

import weka.core.Instances;

/*
 * Author: apolo
 * Time:   2019.5.22
 * INFO:   多分类框架，用于分类，最终返回一个特定的多分类指标（MAUC, MGmean）
 * */
public class ClassifyModel {
	public double calMAUC() {
		double mAuc = 0.0d;
		return mAuc;
	}
	public double calMGMean() {
		double mGmean = 0.0d;
		return mGmean;
	}
	/*
	 * trian为已经经过混合采样后的数据集
	 * test为验证集合
	 * */
	public ClassifyModel(Instances train, Instances test) {
		
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
