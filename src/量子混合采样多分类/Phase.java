package 量子混合采样多分类;

import java.io.Serializable;

public class Phase implements Serializable{
	double alpha;
	double beta;
	public Phase() {
		alpha = 1.0/Math.sqrt(2);
		beta = 1.0/Math.sqrt(2);
	}
}
