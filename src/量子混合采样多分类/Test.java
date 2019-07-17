package 量子混合采样多分类;

import java.util.Random;

public class Test {
	public static void main(String[] args) {
		System.out.println(Enum_Classifier.values()[0]);
	}
}

interface Test1{
	public void print() ;
}

class Test2 implements Test1{

	@Override
	public void print() {
		// TODO Auto-generated method stub
		System.out.println("这是继承类方法");
	}
}

class Test3 extends Test2 implements Test1{
	
}
