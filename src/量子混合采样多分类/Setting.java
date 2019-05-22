package 量子混合采样多分类;


public class Setting {
	public int K;
	public int noisyK;
	public double underSampleRatio = 0.3;
	public int popSize;
	public int iteration;
	public double rotateAngle;
	public Enum_Classifier cls;
	public Setting(int K, int noisyK, int popSize, int iteration, int rotateAngle, Enum_Classifier cls) {
		this.K = K;
		this.noisyK = noisyK;
		this.popSize = popSize;
		this.iteration = iteration;
		this.rotateAngle = rotateAngle*Math.PI/180;
		this.cls = cls;
	}
}
