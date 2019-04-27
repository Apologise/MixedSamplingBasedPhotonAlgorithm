package 量子混合采样多分类;

public class Setting {
	public int T;
	public int K;
	public int noisyK;
	public int popSize;
	public int iteration;
	public double rotateAngle;
	public Setting(int T, int K, int noisyK, int popSize, int iteration, int rotateAngle) {
		this.T = T;
		this.K = K;
		this.noisyK = noisyK;
		this.popSize = popSize;
		this.iteration = iteration;
		this.rotateAngle = rotateAngle*Math.PI/180;
	}
	public static void test() {}
}
