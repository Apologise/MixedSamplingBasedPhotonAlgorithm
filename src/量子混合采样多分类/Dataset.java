package 量子混合采样多分类;

import javax.print.attribute.standard.PrinterLocation;

public class Dataset {
	public static String[] chooseDataset(String dataName, int n) {
		String[] strings = {"数据集找不到",""};
		//ecoli1
		String[] trainSetAutomobile = {
				"多分类数据集/automobile-5-fold/automobile-5-1tra.arff",
				"多分类数据集/automobile-5-fold/automobile-5-2tra.arff",
				"多分类数据集/automobile-5-fold/automobile-5-3tra.arff",
				"多分类数据集/automobile-5-fold/automobile-5-4tra.arff",
				"多分类数据集/automobile-5-fold/automobile-5-5tra.arff",
		};
		String[] testSetAutomobile = {
				"多分类数据集/automobile-5-fold/automobile-5-1tst.arff",
				"多分类数据集/automobile-5-fold/automobile-5-2tst.arff",
				"多分类数据集/automobile-5-fold/automobile-5-3tst.arff",
				"多分类数据集/automobile-5-fold/automobile-5-4tst.arff",
				"多分类数据集/automobile-5-fold/automobile-5-5tst.arff",
		};
		if(dataName.equals("automobileMulti")&& n == 0) {
			return trainSetAutomobile;
		}else if(dataName.equals("automobileMulti")&& n == 1) {
			return testSetAutomobile;
		}
		String[] trainSetBalance = {
				"多分类数据集/balance-5-fold/balance-5-1tra.arff",
				"多分类数据集/balance-5-fold/balance-5-2tra.arff",
				"多分类数据集/balance-5-fold/balance-5-3tra.arff",
				"多分类数据集/balance-5-fold/balance-5-4tra.arff",
				"多分类数据集/balance-5-fold/balance-5-5tra.arff",
		};
		String[] testSetBalance = {
				"多分类数据集/balance-5-fold/balance-5-1tst.arff",
				"多分类数据集/balance-5-fold/balance-5-2tst.arff",
				"多分类数据集/balance-5-fold/balance-5-3tst.arff",
				"多分类数据集/balance-5-fold/balance-5-4tst.arff",
				"多分类数据集/balance-5-fold/balance-5-5tst.arff",
		};
		if(dataName.equals("balanceMulti")&& n == 0) {
			return trainSetBalance;
		}else if(dataName.equals("balanceMulti")&& n == 1) {
			return testSetBalance;
		}
		String[] trainSetContraceptive = {
				"多分类数据集/contraceptive-5-fold/contraceptive-5-1tra.arff",
				"多分类数据集/contraceptive-5-fold/contraceptive-5-2tra.arff",
				"多分类数据集/contraceptive-5-fold/contraceptive-5-3tra.arff",
				"多分类数据集/contraceptive-5-fold/contraceptive-5-4tra.arff",
				"多分类数据集/contraceptive-5-fold/contraceptive-5-5tra.arff",
		};
		String[] testSetContraceptive = {
				"多分类数据集/contraceptive-5-fold/contraceptive-5-1tst.arff",
				"多分类数据集/contraceptive-5-fold/contraceptive-5-2tst.arff",
				"多分类数据集/contraceptive-5-fold/contraceptive-5-3tst.arff",
				"多分类数据集/contraceptive-5-fold/contraceptive-5-4tst.arff",
				"多分类数据集/contraceptive-5-fold/contraceptive-5-5tst.arff",
		};
		if(dataName.equals("contraceptiveMulti")&& n == 0) {
			return trainSetContraceptive;
		}else if(dataName.equals("contraceptiveMulti")&& n == 1) {
			return testSetContraceptive;
		}
		String[] trainSetDermatology = {
				"多分类数据集/dermatology-5-fold/dermatology-5-1tra.arff",
				"多分类数据集/dermatology-5-fold/dermatology-5-2tra.arff",
				"多分类数据集/dermatology-5-fold/dermatology-5-3tra.arff",
				"多分类数据集/dermatology-5-fold/dermatology-5-4tra.arff",
				"多分类数据集/dermatology-5-fold/dermatology-5-5tra.arff",
		};
		String[] testSetDermatology = {
				"多分类数据集/dermatology-5-fold/dermatology-5-1tst.arff",
				"多分类数据集/dermatology-5-fold/dermatology-5-2tst.arff",
				"多分类数据集/dermatology-5-fold/dermatology-5-3tst.arff",
				"多分类数据集/dermatology-5-fold/dermatology-5-4tst.arff",
				"多分类数据集/dermatology-5-fold/dermatology-5-5tst.arff",
		};
		if(dataName.equals("dermatologyMulti")&& n == 0) {
			return trainSetDermatology;
		}else if(dataName.equals("dermatologyMulti")&& n == 1) {
			return testSetDermatology;
		}
		String[] trainSetEcoli = {
				"多分类数据集/ecoli-5-fold/ecoli-5-1tra.arff",
				"多分类数据集/ecoli-5-fold/ecoli-5-2tra.arff",
				"多分类数据集/ecoli-5-fold/ecoli-5-3tra.arff",
				"多分类数据集/ecoli-5-fold/ecoli-5-4tra.arff",
				"多分类数据集/ecoli-5-fold/ecoli-5-5tra.arff",
		};
		String[] testSetEcoli = {
				"多分类数据集/ecoli-5-fold/ecoli-5-1tst.arff",
				"多分类数据集/ecoli-5-fold/ecoli-5-2tst.arff",
				"多分类数据集/ecoli-5-fold/ecoli-5-3tst.arff",
				"多分类数据集/ecoli-5-fold/eoli-5-4tst.arff",
				"多分类数据集/ecoli-5-fold/ecoli-5-5tst.arff",
		};
		if(dataName.equals("ecoliMulti")&& n == 0) {
			return trainSetEcoli;
		}else if(dataName.equals("ecoliMulti")&& n == 1) {
			return testSetEcoli;
		}
		
		String[] trainSetGlass = {
				"多分类数据集/glass-5-fold/glass-5-1tra.arff",
				"多分类数据集/glass-5-fold/glass-5-2tra.arff",
				"多分类数据集/glass-5-fold/glass-5-3tra.arff",
				"多分类数据集/glass-5-fold/glass-5-4tra.arff",
				"多分类数据集/glass-5-fold/glass-5-5tra.arff",
		};
		String[] testSetGlass = {
				"多分类数据集/glass-5-fold/glass-5-1tst.arff",
				"多分类数据集/glass-5-fold/glass-5-2tst.arff",
				"多分类数据集/glass-5-fold/glass-5-3tst.arff",
				"多分类数据集/glass-5-fold/glass-5-4tst.arff",
				"多分类数据集/glass-5-fold/glass-5-5tst.arff",
		};
		if(dataName.equals("glassMulti")&& n == 0) {
			return trainSetGlass;
		}else if(dataName.equals("glassMulti")&& n == 1) {
			return testSetGlass;
		}
		
		String[] trainSetHayesroth = {
				"多分类数据集/hayes-roth-5-fold/hayes-roth-5-1tra.arff",
				"多分类数据集/hayes-roth-5-fold/hayes-roth-5-2tra.arff",
				"多分类数据集/hayes-roth-5-fold/hayes-roth-5-3tra.arff",
				"多分类数据集/hayes-roth-5-fold/hayes-roth-5-4tra.arff",
				"多分类数据集/hayes-roth-5-fold/hayes-roth-5-5tra.arff",
		};
		String[] testSetHayesroth = {
				"多分类数据集/hayes-roth-5-fold/hayes-roth-5-1tst.arff",
				"多分类数据集/hayes-roth-5-fold/hayes-roth-5-2tst.arff",
				"多分类数据集/hayes-roth-5-fold/hayes-roth-5-3tst.arff",
				"多分类数据集/hayes-roth-5-fold/hayes-roth-5-4tst.arff",
				"多分类数据集/hayes-roth-5-fold/hayes-roth-5-5tst.arff",
		};
		if(dataName.equals("hayesrothMulti")&& n == 0) {
			return trainSetHayesroth;
		}else if(dataName.equals("hayesrothMulti")&& n == 1) {
			return testSetHayesroth;
		}
		String[] trainSetLymphography = {
				"多分类数据集/lymphography-5-fold/lymphography-5-1tra.arff",
				"多分类数据集/lymphography-5-fold/lymphography-5-2tra.arff",
				"多分类数据集/lymphography-5-fold/lymphography-5-3tra.arff",
				"多分类数据集/lymphography-5-fold/lymphography-5-4tra.arff",
				"多分类数据集/lymphography-5-fold/lymphography-5-5tra.arff",
		};
		String[] testSetLymphography = {
				"多分类数据集/lymphography-5-fold/lymphography-5-1tst.arff",
				"多分类数据集/lymphography-5-fold/lymphography-5-2tst.arff",
				"多分类数据集/lymphography-5-fold/lymphography-5-3tst.arff",
				"多分类数据集/lymphography-5-fold/lymphography-5-4tst.arff",
				"多分类数据集/lymphography-5-fold/lymphography-5-5tst.arff",
		};
		if(dataName.equals("lymphographyMulti")&& n == 0) {
			return trainSetLymphography;
		}else if(dataName.equals("lymphographyMulti")&& n == 1) {
			return testSetLymphography;
		}
		String[] trainSetNewthroid = {
				"多分类数据集/newthyroid-5-fold/newthyroid-5-1tra.arff",
				"多分类数据集/newthyroid-5-fold/newthyroid-5-2tra.arff",
				"多分类数据集/newthyroid-5-fold/newthyroid-5-3tra.arff",
				"多分类数据集/newthyroid-5-fold/newthyroid-5-4tra.arff",
				"多分类数据集/newthyroid-5-fold/newthyroid-5-5tra.arff",
		};
		String[] testSetNewthroid = {
				"多分类数据集/newthyroid-5-fold/newthyroid-5-1tst.arff",
				"多分类数据集/newthyroid-5-fold/newthyroid-5-2tst.arff",
				"多分类数据集/newthyroid-5-fold/newthyroid-5-3tst.arff",
				"多分类数据集/newthyroid-5-fold/newthyroid-5-4tst.arff",
				"多分类数据集/newthyroid-5-fold/newthyroid-5-5tst.arff",
		};
		if(dataName.equals("newthyroidMulti")&& n == 0) {
			return trainSetNewthroid;
		}else if(dataName.equals("newthyroidMulti")&& n == 1) {
			return testSetNewthroid;
		}
		String[] trainSetPageblocks = {
				"多分类数据集/page-blocks-5-fold/page-blocks-5-1tra.arff",
				"多分类数据集/page-blocks-5-fold/page-blocks-5-2tra.arff",
				"多分类数据集/page-blocks-5-fold/page-blocks-5-3tra.arff",
				"多分类数据集/page-blocks-5-fold/page-blocks-5-4tra.arff",
				"多分类数据集/page-blocks-5-fold/page-blocks-5-5tra.arff",
		};
		String[] testSetPageBlocks = {
				"多分类数据集/page-blocks-5-fold/page-blocks-5-1tst.arff",
				"多分类数据集/page-blocks-5-fold/page-blocks-5-2tst.arff",
				"多分类数据集/page-blocks-5-fold/page-blocks-5-3tst.arff",
				"多分类数据集/page-blocks-5-fold/page-blocks-5-4tst.arff",
				"多分类数据集/page-blocks-5-fold/page-blocks-5-5tst.arff",
		};
		if(dataName.equals("pageblocksMulti")&& n == 0) {
			return trainSetPageblocks;
		}else if(dataName.equals("pageblocksMulti")&& n == 1) {
			return testSetPageBlocks;
		}
		
		String[] trainSetPenbased = {
				"多分类数据集/penbased-5-fold/penbased-5-1tra.arff",
				"多分类数据集/penbased-5-fold/penbased-5-2tra.arff",
				"多分类数据集/penbased-5-fold/penbased-5-3tra.arff",
				"多分类数据集/penbased-5-fold/penbased-5-4tra.arff",
				"多分类数据集/penbased-5-fold/penbased-5-5tra.arff",
		};
		String[] testSetPenbased = {
				"多分类数据集/penbased-5-fold/penbased-5-1tst.arff",
				"多分类数据集/penbased-5-fold/penbased-5-2tst.arff",
				"多分类数据集/penbased-5-fold/penbased-5-3tst.arff",
				"多分类数据集/penbased-5-fold/penbased-5-4tst.arff",
				"多分类数据集/penbased-5-fold/penbased-5-5tst.arff",
		};
		if(dataName.equals("penbasedMulti")&& n == 0) {
			return trainSetPenbased;
		}else if(dataName.equals("penbasedMulti")&& n == 1) {
			return testSetPenbased;
		}
		String[] trainSetShuttle = {
				"多分类数据集/shuttle-5-fold/shuttle-5-1tra.arff",
				"多分类数据集/shuttle-5-fold/shuttle-5-2tra.arff",
				"多分类数据集/shuttle-5-fold/shuttle-5-3tra.arff",
				"多分类数据集/shuttle-5-fold/shuttle-5-4tra.arff",
				"多分类数据集/shuttle-5-fold/shuttle-5-5tra.arff",
		};
		String[] testSetShuttle = {
				"多分类数据集/shuttle-5-fold/shuttle-5-1tst.arff",
				"多分类数据集/shuttle-5-fold/shuttle-5-2tst.arff",
				"多分类数据集/shuttle-5-fold/shuttle-5-3tst.arff",
				"多分类数据集/shuttle-5-fold/shuttle-5-4tst.arff",
				"多分类数据集/shuttle-5-fold/shuttle-5-5tst.arff",
		};
		if(dataName.equals("shuttleMulti")&& n == 0) {
			return trainSetShuttle;
		}else if(dataName.equals("shuttleMulti")&& n == 1) {
			return testSetShuttle;
		}
		String[] trainSetThyroid = {
				"多分类数据集/thyroid-5-fold/thyroid-5-1tra.arff",
				"多分类数据集/thyroid-5-fold/thyroid-5-2tra.arff",
				"多分类数据集/thyroid-5-fold/thyroid-5-3tra.arff",
				"多分类数据集/thyroid-5-fold/thyroid-5-4tra.arff",
				"多分类数据集/thyroid-5-fold/thyroid-5-5tra.arff",
		};
		String[] testSetThyroid = {
				"多分类数据集/thyroid-5-fold/thyroid-5-1tst.arff",
				"多分类数据集/thyroid-5-fold/thyroid-5-2tst.arff",
				"多分类数据集/thyroid-5-fold/thyroid-5-3tst.arff",
				"多分类数据集/thyroid-5-fold/thyroid-5-4tst.arff",
				"多分类数据集/thyroid-5-fold/thyroid-5-5tst.arff",
		};
		if(dataName.equals("thyroidMulti")&& n == 0) {
			return trainSetThyroid;
		}else if(dataName.equals("thyroidMulti")&& n == 1) {
			return testSetThyroid;
		}
		
		String[] trainSetWine = {
				"多分类数据集/wine-5-fold/wine-5-1tra.arff",
				"多分类数据集/wine-5-fold/wine-5-2tra.arff",
				"多分类数据集/wine-5-fold/wine-5-3tra.arff",
				"多分类数据集/wine-5-fold/wine-5-4tra.arff",
				"多分类数据集/wine-5-fold/wine-5-5tra.arff",
		};
		String[] testSetWine = {
				"多分类数据集/wine-5-fold/wine-5-1tst.arff",
				"多分类数据集/wine-5-fold/wine-5-2tst.arff",
				"多分类数据集/wine-5-fold/wine-5-3tst.arff",
				"多分类数据集/wine-5-fold/wine-5-4tst.arff",
				"多分类数据集/wine-5-fold/wine-5-5tst.arff",
		};
		if(dataName.equals("wineMulti")&& n == 0) {
			return trainSetWine;
		}else if(dataName.equals("wineMulti")&& n == 1) {
			return testSetWine;
		}
		String[] trainSetYeast = {
				"多分类数据集/yeast-5-fold/yeast-5-1tra.arff",
				"多分类数据集/yeast-5-fold/yeast-5-2tra.arff",
				"多分类数据集/yeast-5-fold/yeast-5-3tra.arff",
				"多分类数据集/yeast-5-fold/yeast-5-4tra.arff",
				"多分类数据集/yeast-5-fold/yeast-5-5tra.arff",
		};
		String[] testSetYeast = {
				"多分类数据集/yeast-5-fold/yeast-5-1tst.arff",
				"多分类数据集/yeast-5-fold/yeast-5-2tst.arff",
				"多分类数据集/yeast-5-fold/yeast-5-3tst.arff",
				"多分类数据集/yeast-5-fold/yeast-5-4tst.arff",
				"多分类数据集/yeast-5-fold/yeast-5-5tst.arff",
		};
		if(dataName.equals("yeastMulti")&& n == 0) {
			return trainSetYeast;
		}else if(dataName.equals("yeastMulti")&& n == 1) {
			return testSetYeast;
		}
		String[] trainSetVehicle = {
				"多分类数据集/vehicle-5-fold/vehicle-5-1tra.arff",
				"多分类数据集/vehicle-5-fold/vehicle-5-2tra.arff",
				"多分类数据集/vehicle-5-fold/vehicle-5-3tra.arff",
				"多分类数据集/vehicle-5-fold/vehicle-5-4tra.arff",
				"多分类数据集/vehicle-5-fold/vehicle-5-5tra.arff",
		};
		String[] testSetVehicle = {
				"多分类数据集/vehicle-5-fold/vehicle-5-1tst.arff",
				"多分类数据集/vehicle-5-fold/vehicle-5-2tst.arff",
				"多分类数据集/vehicle-5-fold/vehicle-5-3tst.arff",
				"多分类数据集/vehicle-5-fold/vehicle-5-4tst.arff",
				"多分类数据集/vehicle-5-fold/vehicle-5-5tst.arff",
		};
		if(dataName.equals("vehicleMulti")&& n == 0) {
			return trainSetVehicle;
		}else if(dataName.equals("vehicleMulti")&& n == 1) {
			return testSetVehicle;
		}
		
		String[] trainSetVowel = {
				"多分类数据集/vowel-5-fold/vowel-5-1tra.arff",
				"多分类数据集/vowel-5-fold/vowel-5-2tra.arff",
				"多分类数据集/vowel-5-fold/vowel-5-3tra.arff",
				"多分类数据集/vowel-5-fold/vowel-5-4tra.arff",
				"多分类数据集/vowel-5-fold/vowel-5-5tra.arff",
		};
		String[] testSetVowel = {
				"多分类数据集/vowel-5-fold/vowel-5-1tst.arff",
				"多分类数据集/vowel-5-fold/vowel-5-2tst.arff",
				"多分类数据集/vowel-5-fold/vowel-5-3tst.arff",
				"多分类数据集/vowel-5-fold/vowel-5-4tst.arff",
				"多分类数据集/vowel-5-fold/vowel-5-5tst.arff",
		};
		if(dataName.equals("vowelMulti")&& n == 0) {
			return trainSetVowel;
		}else if(dataName.equals("vowelMulti")&& n == 1) {
			return testSetVowel;
		}
		String[] trainSetTae = {
				"多分类数据集/tae-5-fold/tae-5-1tra.arff",
				"多分类数据集/tae-5-fold/tae-5-2tra.arff",
				"多分类数据集/tae-5-fold/tae-5-3tra.arff",
				"多分类数据集/tae-5-fold/tae-5-4tra.arff",
				"多分类数据集/tae-5-fold/tae-5-5tra.arff",
		};
		String[] testSetTae = {
				"多分类数据集/tae-5-fold/tae-5-1tst.arff",
				"多分类数据集/tae-5-fold/tae-5-2tst.arff",
				"多分类数据集/tae-5-fold/tae-5-3tst.arff",
				"多分类数据集/tae-5-fold/tae-5-4tst.arff",
				"多分类数据集/tae-5-fold/tae-5-5tst.arff",
		};
		if(dataName.equals("taeMulti")&& n == 0) {
			return trainSetTae;
		}else if(dataName.equals("taeMulti")&& n == 1) {
			return testSetTae;
		}
		String[] trainSetSegment = {
				"多分类数据集/segment-5-fold/segment-5-1tra.arff",
				"多分类数据集/segment-5-fold/segment-5-2tra.arff",
				"多分类数据集/segment-5-fold/segment-5-3tra.arff",
				"多分类数据集/segment-5-fold/segment-5-4tra.arff",
				"多分类数据集/segment-5-fold/segment-5-5tra.arff",
		};
		String[] testSetSegment = {
				"多分类数据集/segment-5-fold/segment-5-1tst.arff",
				"多分类数据集/segment-5-fold/segment-5-2tst.arff",
				"多分类数据集/segment-5-fold/segment-5-3tst.arff",
				"多分类数据集/segment-5-fold/segment-5-4tst.arff",
				"多分类数据集/segment-5-fold/segment-5-5tst.arff",
		};
		if(dataName.equals("segmentMulti")&& n == 0) {
			return trainSetSegment;
		}else if(dataName.equals("segmentMulti")&& n == 1) {
			return testSetSegment;
		}
		String[] trainSetEcoli1 = { 
				"5-fold-ecoli1/ecoli1-5-1tra.arff",
				"5-fold-ecoli1/ecoli1-5-2tra.arff",
				"5-fold-ecoli1/ecoli1-5-3tra.arff", 
				"5-fold-ecoli1/ecoli1-5-4tra.arff",
				"5-fold-ecoli1/ecoli1-5-5tra.arff" };
		String[] testSetEcoli1 = { 
				"5-fold-ecoli1/ecoli1-5-1tst.arff", 
				"5-fold-ecoli1/ecoli1-5-2tst.arff",
				"5-fold-ecoli1/ecoli1-5-3tst.arff", 
				"5-fold-ecoli1/ecoli1-5-4tst.arff",
				"5-fold-ecoli1/ecoli1-5-5tst.arff" };
		if(dataName.equals("ecoli1")&& n == 0) {
			return trainSetEcoli1;
		}else if(dataName.equals("ecoli1")&& n == 1) {
			return testSetEcoli1;
		}
		//ecoli2
		String[] trainSetEcoli2 = { 
				"5-fold-ecoli2/ecoli2-5-1tra.arff", 
				"5-fold-ecoli2/ecoli2-5-2tra.arff",
				"5-fold-ecoli2/ecoli2-5-3tra.arff", 
				"5-fold-ecoli2/ecoli2-5-4tra.arff",
				"5-fold-ecoli2/ecoli2-5-5tra.arff" };
		String[] testSetEcoli2 = { 
				"5-fold-ecoli2/ecoli2-5-1tst.arff",
				"5-fold-ecoli2/ecoli2-5-2tst.arff",
				"5-fold-ecoli2/ecoli2-5-3tst.arff", 
				"5-fold-ecoli2/ecoli2-5-4tst.arff",
				"5-fold-ecoli2/ecoli2-5-5tst.arff" };
		
		//ecoli3
		String[] trainSetEcoli3 = { 
				"5-fold-ecoli3/ecoli3-5-1tra.arff", 
				"5-fold-ecoli3/ecoli3-5-2tra.arff",
				"5-fold-ecoli3/ecoli3-5-3tra.arff", 
				"5-fold-ecoli3/ecoli3-5-4tra.arff",
				"5-fold-ecoli3/ecoli3-5-5tra.arff" };
		String[] testSetEcoli3 = {
				"5-fold-ecoli3/ecoli3-5-1tst.arff", 
				"5-fold-ecoli3/ecoli3-5-2tst.arff",
				"5-fold-ecoli3/ecoli3-5-3tst.arff",
				"5-fold-ecoli3/ecoli3-5-4tst.arff",
				"5-fold-ecoli3/ecoli3-5-5tst.arff" };
		
		String[] trainSetEcoli4 = { 
				"5-fold-ecoli4/ecoli4-5-1tra.arff", 
				"5-fold-ecoli4/ecoli4-5-2tra.arff",
				"5-fold-ecoli4/ecoli4-5-3tra.arff", 
				"5-fold-ecoli4/ecoli4-5-4tra.arff",
				"5-fold-ecoli4/ecoli4-5-5tra.arff" };
		String[] testSetEcoli4 = {
				"5-fold-ecoli4/ecoli4-5-1tst.arff", 
				"5-fold-ecoli4/ecoli4-5-2tst.arff",
				"5-fold-ecoli4/ecoli4-5-3tst.arff",
				"5-fold-ecoli4/ecoli4-5-4tst.arff",
				"5-fold-ecoli4/ecoli4-5-5tst.arff" };
		String[] trainSetEcoli0137v26 = { 
				"5-fold-ecoli-0-1-3-7-vs-2-6/ecoli-0-1-3-7-vs-2-6-5-1tra.arff", 
				"5-fold-ecoli-0-1-3-7-vs-2-6/ecoli-0-1-3-7-vs-2-6-5-2tra.arff",
				"5-fold-ecoli-0-1-3-7-vs-2-6/ecoli-0-1-3-7-vs-2-6-5-3tra.arff", 
				"5-fold-ecoli-0-1-3-7-vs-2-6/ecoli-0-1-3-7-vs-2-6-5-4tra.arff",
				"5-fold-ecoli-0-1-3-7-vs-2-6/ecoli-0-1-3-7-vs-2-6-5-5tra.arff" };
		String[] testSetEcoli0137v26 = {
				"5-fold-ecoli-0-1-3-7-vs-2-6/ecoli-0-1-3-7-vs-2-6-5-1tst.arff", 
				"5-fold-ecoli-0-1-3-7-vs-2-6/ecoli-0-1-3-7-vs-2-6-5-2tst.arff",
				"5-fold-ecoli-0-1-3-7-vs-2-6/ecoli-0-1-3-7-vs-2-6-5-3tst.arff", 
				"5-fold-ecoli-0-1-3-7-vs-2-6/ecoli-0-1-3-7-vs-2-6-5-4tst.arff",
				"5-fold-ecoli-0-1-3-7-vs-2-6/ecoli-0-1-3-7-vs-2-6-5-5tst.arff" };
		
		//yeast1
		String[] trainSetYeast1 = {
				"5-fold-yeast1/yeast1-5-1tra.arff",
				"5-fold-yeast1/yeast1-5-2tra.arff",
				"5-fold-yeast1/yeast1-5-3tra.arff",
				"5-fold-yeast1/yeast1-5-4tra.arff",
				"5-fold-yeast1/yeast1-5-5tra.arff"};
		String[] testSetYeast1 = {
				"5-fold-yeast1/yeast1-5-1tst.arff",
				"5-fold-yeast1/yeast1-5-2tst.arff",
				"5-fold-yeast1/yeast1-5-3tst.arff",
				"5-fold-yeast1/yeast1-5-4tst.arff",
				"5-fold-yeast1/yeast1-5-5tst.arff"};
			
		String[] trainSetYeast3 = {
				"5-fold-yeast3/yeast3-5-1tra.arff",
				"5-fold-yeast3/yeast3-5-2tra.arff",
				"5-fold-yeast3/yeast3-5-3tra.arff",
				"5-fold-yeast3/yeast3-5-4tra.arff",
				"5-fold-yeast3/yeast3-5-5tra.arff"};
		String[] testSetYeast3 = {
				"5-fold-yeast3/yeast3-5-1tst.arff",
				"5-fold-yeast3/yeast3-5-2tst.arff", 
				"5-fold-yeast3/yeast3-5-3tst.arff",
				"5-fold-yeast3/yeast3-5-4tst.arff",
				"5-fold-yeast3/yeast3-5-5tst.arff"};
		String[] trainSetYeast4 = {
				"5-fold-yeast4/yeast4-5-1tra.arff",
				"5-fold-yeast4/yeast4-5-2tra.arff",
				"5-fold-yeast4/yeast4-5-3tra.arff",
				"5-fold-yeast4/yeast4-5-4tra.arff",
				"5-fold-yeast4/yeast4-5-5tra.arff"};
		String[] testSetYeast4 = {
				"5-fold-yeast4/yeast4-5-1tst.arff",
				"5-fold-yeast4/yeast4-5-2tst.arff", 
				"5-fold-yeast4/yeast4-5-3tst.arff",
				"5-fold-yeast4/yeast4-5-4tst.arff",
				"5-fold-yeast4/yeast4-5-5tst.arff"};
		String[] trainSetYeast5 = {
				"5-fold-yeast5/yeast5-5-1tra.arff",
				"5-fold-yeast5/yeast5-5-2tra.arff",
				"5-fold-yeast5/yeast5-5-3tra.arff",
				"5-fold-yeast5/yeast5-5-4tra.arff",
				"5-fold-yeast5/yeast5-5-5tra.arff"};
		String[] testSetYeast5 = {
				"5-fold-yeast5/yeast5-5-1tst.arff",
				"5-fold-yeast5/yeast5-5-2tst.arff", 
				"5-fold-yeast5/yeast5-5-3tst.arff",
				"5-fold-yeast5/yeast5-5-4tst.arff",
				"5-fold-yeast5/yeast5-5-5tst.arff"};
		String[] trainSetYeast6 = {
				"5-fold-yeast6/yeast6-5-1tra.arff",
				"5-fold-yeast6/yeast6-5-2tra.arff",
				"5-fold-yeast6/yeast6-5-3tra.arff",
				"5-fold-yeast6/yeast6-5-4tra.arff",
				"5-fold-yeast6/yeast6-5-5tra.arff"};
		String[] testSetYeast6 = {
				"5-fold-yeast6/yeast6-5-1tst.arff",
				"5-fold-yeast6/yeast6-5-2tst.arff", 
				"5-fold-yeast6/yeast6-5-3tst.arff",
				"5-fold-yeast6/yeast6-5-4tst.arff",
				"5-fold-yeast6/yeast6-5-5tst.arff"};
		
		String[] trainSetglass0 = {
				"5-fold-glass0/glass0-5-1tra.arff",
				"5-fold-glass0/glass0-5-2tra.arff",
				"5-fold-glass0/glass0-5-3tra.arff",
				"5-fold-glass0/glass0-5-4tra.arff",
				"5-fold-glass0/glass0-5-5tra.arff"};
		String[] testSetglass0 = {
				"5-fold-glass0/glass0-5-1tst.arff",
				"5-fold-glass0/glass0-5-2tst.arff",
				"5-fold-glass0/glass0-5-3tst.arff",
				"5-fold-glass0/glass0-5-4tst.arff",
				"5-fold-glass0/glass0-5-5tst.arff"};
		
		String[] trainSetglass1 = {
				"5-fold-glass1/glass1-5-1tra.arff",
				"5-fold-glass1/glass1-5-2tra.arff",
				"5-fold-glass1/glass1-5-3tra.arff",
				"5-fold-glass1/glass1-5-4tra.arff",
				"5-fold-glass1/glass1-5-5tra.arff"};
		String[] testSetglass1 = {
				"5-fold-glass1/glass1-5-1tst.arff",
				"5-fold-glass1/glass1-5-2tst.arff",
				"5-fold-glass1/glass1-5-3tst.arff",
				"5-fold-glass1/glass1-5-4tst.arff",
				"5-fold-glass1/glass1-5-5tst.arff"};
		
		String[] trainSetglass2 = {
				"5-fold-glass2/glass2-5-1tra.arff",
				"5-fold-glass2/glass2-5-2tra.arff",
				"5-fold-glass2/glass2-5-3tra.arff",
				"5-fold-glass2/glass2-5-4tra.arff",
				"5-fold-glass2/glass2-5-5tra.arff"};
		String[] testSetglass2 = {
				"5-fold-glass2/glass2-5-1tst.arff",
				"5-fold-glass2/glass2-5-2tst.arff",
				"5-fold-glass2/glass2-5-3tst.arff",
				"5-fold-glass2/glass2-5-4tst.arff",
				"5-fold-glass2/glass2-5-5tst.arff"};
		String[] trainSetglass4 = {
				"5-fold-glass4/glass4-5-1tra.arff",
				"5-fold-glass4/glass4-5-2tra.arff",
				"5-fold-glass4/glass4-5-3tra.arff",
				"5-fold-glass4/glass4-5-4tra.arff",
				"5-fold-glass4/glass4-5-5tra.arff"};
		String[] testSetglass4 = {
				"5-fold-glass4/glass4-5-1tst.arff",
				"5-fold-glass4/glass4-5-2tst.arff",
				"5-fold-glass4/glass4-5-3tst.arff",
				"5-fold-glass4/glass4-5-4tst.arff",
				"5-fold-glass4/glass4-5-5tst.arff"};
		String[] trainSetglass5 = {
				"5-fold-glass5/glass5-5-1tra.arff",
				"5-fold-glass5/glass5-5-2tra.arff",
				"5-fold-glass5/glass5-5-3tra.arff",
				"5-fold-glass5/glass5-5-4tra.arff",
				"5-fold-glass5/glass5-5-5tra.arff"};
		String[] testSetglass5 = {
				"5-fold-glass5/glass5-5-1tst.arff",
				"5-fold-glass5/glass5-5-2tst.arff",
				"5-fold-glass5/glass5-5-3tst.arff",
				"5-fold-glass5/glass5-5-4tst.arff",
				"5-fold-glass5/glass5-5-5tst.arff"};
		String[] trainSetglass6 = {
				"5-fold-glass6/glass6-5-1tra.arff",
				"5-fold-glass6/glass6-5-2tra.arff",
				"5-fold-glass6/glass6-5-3tra.arff",
				"5-fold-glass6/glass6-5-4tra.arff",
				"5-fold-glass6/glass6-5-5tra.arff"};
		String[] testSetglass6 = {
				"5-fold-glass6/glass6-5-1tst.arff",
				"5-fold-glass6/glass6-5-2tst.arff",
				"5-fold-glass6/glass6-5-3tst.arff",
				"5-fold-glass6/glass6-5-4tst.arff",
				"5-fold-glass6/glass6-5-5tst.arff"};
		String[] trainSetglass016v2 = {
				"5-fold-glass-0-1-6-vs-2/glass-0-1-6-vs-2-5-1tra.arff",
				"5-fold-glass-0-1-6-vs-2/glass-0-1-6-vs-2-5-2tra.arff",
				"5-fold-glass-0-1-6-vs-2/glass-0-1-6-vs-2-5-3tra.arff",
				"5-fold-glass-0-1-6-vs-2/glass-0-1-6-vs-2-5-4tra.arff",
				"5-fold-glass-0-1-6-vs-2/glass-0-1-6-vs-2-5-5tra.arff"};
		String[] testSetglass016v2 = {
				"5-fold-glass-0-1-6-vs-2/glass-0-1-6-vs-2-5-1tst.arff",
				"5-fold-glass-0-1-6-vs-2/glass-0-1-6-vs-2-5-2tst.arff",
				"5-fold-glass-0-1-6-vs-2/glass-0-1-6-vs-2-5-3tst.arff",
				"5-fold-glass-0-1-6-vs-2/glass-0-1-6-vs-2-5-4tst.arff",
				"5-fold-glass-0-1-6-vs-2/glass-0-1-6-vs-2-5-5tst.arff"};
		String[] trainSetglass016v5 = {
				"5-fold-glass-0-1-6-vs-5/glass-0-1-6-vs-5-5-1tra.arff",
				"5-fold-glass-0-1-6-vs-5/glass-0-1-6-vs-5-5-2tra.arff",
				"5-fold-glass-0-1-6-vs-5/glass-0-1-6-vs-5-5-3tra.arff",
				"5-fold-glass-0-1-6-vs-5/glass-0-1-6-vs-5-5-4tra.arff",
				"5-fold-glass-0-1-6-vs-5/glass-0-1-6-vs-5-5-5tra.arff"};
		String[] testSetglass016v5 = {
				"5-fold-glass-0-1-6-vs-5/glass-0-1-6-vs-5-5-1tst.arff",
				"5-fold-glass-0-1-6-vs-5/glass-0-1-6-vs-5-5-2tst.arff",
				"5-fold-glass-0-1-6-vs-5/glass-0-1-6-vs-5-5-3tst.arff",
				"5-fold-glass-0-1-6-vs-5/glass-0-1-6-vs-5-5-4tst.arff",
				"5-fold-glass-0-1-6-vs-5/glass-0-1-6-vs-5-5-5tst.arff"};
		String[] trainSetglass0123vs456 = {
				"5-fold-glass0123-vs-456/glass-0-1-2-3-vs-4-5-6-5-1tra.arff",
				"5-fold-glass0123-vs-456/glass-0-1-2-3-vs-4-5-6-5-2tra.arff",
				"5-fold-glass0123-vs-456/glass-0-1-2-3-vs-4-5-6-5-3tra.arff",
				"5-fold-glass0123-vs-456/glass-0-1-2-3-vs-4-5-6-5-4tra.arff",
				"5-fold-glass0123-vs-456/glass-0-1-2-3-vs-4-5-6-5-5tra.arff"};
		String[] testSetglass0123vs456 = {
				"5-fold-glass0123-vs-456/glass-0-1-2-3-vs-4-5-6-5-1tst.arff",
				"5-fold-glass0123-vs-456/glass-0-1-2-3-vs-4-5-6-5-2tst.arff",
				"5-fold-glass0123-vs-456/glass-0-1-2-3-vs-4-5-6-5-3tst.arff",
				"5-fold-glass0123-vs-456/glass-0-1-2-3-vs-4-5-6-5-4tst.arff",
				"5-fold-glass0123-vs-456/glass-0-1-2-3-vs-4-5-6-5-5tst.arff"};
		
		String[] trainSetecoli0v1 = {
				"5-fold-ecoli-0-vs-1/ecoli-0-vs-1-5-1tra.arff",
				"5-fold-ecoli-0-vs-1/ecoli-0-vs-1-5-2tra.arff",
				"5-fold-ecoli-0-vs-1/ecoli-0-vs-1-5-3tra.arff",
				"5-fold-ecoli-0-vs-1/ecoli-0-vs-1-5-4tra.arff",
				"5-fold-ecoli-0-vs-1/ecoli-0-vs-1-5-5tra.arff"};
		String[] testSetecoli0v1 = {
				"5-fold-ecoli-0-vs-1/ecoli-0-vs-1-5-1tst.arff",
				"5-fold-ecoli-0-vs-1/ecoli-0-vs-1-5-2tst.arff",
				"5-fold-ecoli-0-vs-1/ecoli-0-vs-1-5-3tst.arff",
				"5-fold-ecoli-0-vs-1/ecoli-0-vs-1-5-4tst.arff",
				"5-fold-ecoli-0-vs-1/ecoli-0-vs-1-5-5tst.arff"};
		
		String[] trainSetvehicle0 = {
				"5-fold-vehicle0/vehicle0-5-1tra.arff",
				"5-fold-vehicle0/vehicle0-5-2tra.arff",
				"5-fold-vehicle0/vehicle0-5-3tra.arff",
				"5-fold-vehicle0/vehicle0-5-4tra.arff",
				"5-fold-vehicle0/vehicle0-5-5tra.arff"};
		String[] testSetvehicle0 = {
				"5-fold-vehicle0/vehicle0-5-1tst.arff",
				"5-fold-vehicle0/vehicle0-5-2tst.arff",
				"5-fold-vehicle0/vehicle0-5-3tst.arff",
				"5-fold-vehicle0/vehicle0-5-4tst.arff",
				"5-fold-vehicle0/vehicle0-5-5tst.arff"};
		
		String[] trainabalone918= {
				"5-fold-abalone9-18/abalone9-18-5-1tra.arff",
				"5-fold-abalone9-18/abalone9-18-5-2tra.arff",
				"5-fold-abalone9-18/abalone9-18-5-3tra.arff",
				"5-fold-abalone9-18/abalone9-18-5-4tra.arff",
				"5-fold-abalone9-18/abalone9-18-5-5tra.arff"};
		String[] testabalone918= {
				"5-fold-abalone9-18/abalone9-18-5-1tst.arff",
				"5-fold-abalone9-18/abalone9-18-5-2tst.arff",
				"5-fold-abalone9-18/abalone9-18-5-3tst.arff",
				"5-fold-abalone9-18/abalone9-18-5-4tst.arff",
				"5-fold-abalone9-18/abalone9-18-5-5tst.arff"};
		
		String[] trainabalone19= {
				"5-fold-abalone19/abalone19-5-1tra.arff",
				"5-fold-abalone19/abalone19-5-2tra.arff",
				"5-fold-abalone19/abalone19-5-3tra.arff",
				"5-fold-abalone19/abalone19-5-4tra.arff",
				"5-fold-abalone19/abalone19-5-5tra.arff"};
		String[] testabalone19= {
				"5-fold-abalone19/abalone19-5-1tst.arff",
				"5-fold-abalone19/abalone19-5-2tst.arff",
				"5-fold-abalone19/abalone19-5-3tst.arff",
				"5-fold-abalone19/abalone19-5-4tst.arff",
				"5-fold-abalone19/abalone19-5-5tst.arff"};
		String[] trainnewthyroid1 = {
				"5-fold-new-thyroid1/new-thyroid1-5-1tra.arff",
				"5-fold-new-thyroid1/new-thyroid1-5-2tra.arff",
				"5-fold-new-thyroid1/new-thyroid1-5-3tra.arff",
				"5-fold-new-thyroid1/new-thyroid1-5-4tra.arff",
				"5-fold-new-thyroid1/new-thyroid1-5-5tra.arff"};
		String[] testnewthyroid1 = {
				"5-fold-new-thyroid1/new-thyroid1-5-1tst.arff",
				"5-fold-new-thyroid1/new-thyroid1-5-2tst.arff",
				"5-fold-new-thyroid1/new-thyroid1-5-3tst.arff",
				"5-fold-new-thyroid1/new-thyroid1-5-4tst.arff",
				"5-fold-new-thyroid1/new-thyroid1-5-5tst.arff"};
		String[] trainnewthyroid2 = {
				"5-fold-new-thyroid2/newthyroid2-5-1tra.arff",
				"5-fold-new-thyroid2/newthyroid2-5-2tra.arff",
				"5-fold-new-thyroid2/newthyroid2-5-3tra.arff",
				"5-fold-new-thyroid2/newthyroid2-5-4tra.arff",
				"5-fold-new-thyroid2/newthyroid2-5-5tra.arff"};
		String[] testnewthyroid2 = {
				"5-fold-new-thyroid2/newthyroid2-5-1tst.arff",
				"5-fold-new-thyroid2/newthyroid2-5-2tst.arff",
				"5-fold-new-thyroid2/newthyroid2-5-3tst.arff",
				"5-fold-new-thyroid2/newthyroid2-5-4tst.arff",
				"5-fold-new-thyroid2/newthyroid2-5-5tst.arff"};
		String[] trainpima = {
				"5-fold-pima/pima-5-1tra.arff",
				"5-fold-pima/pima-5-2tra.arff",
				"5-fold-pima/pima-5-3tra.arff",
				"5-fold-pima/pima-5-4tra.arff",
				"5-fold-pima/pima-5-5tra.arff"};
		String[] testpima = {
				"5-fold-pima/pima-5-1tst.arff",
				"5-fold-pima/pima-5-2tst.arff",
				"5-fold-pima/pima-5-3tst.arff",
				"5-fold-pima/pima-5-4tst.arff",
				"5-fold-pima/pima-5-5tst.arff"};
		String[] trainvehicle1 = {
				"5-fold-vehicle1/vehicle1-5-1tra.arff",
				"5-fold-vehicle1/vehicle1-5-2tra.arff",
				"5-fold-vehicle1/vehicle1-5-3tra.arff",
				"5-fold-vehicle1/vehicle1-5-4tra.arff",
				"5-fold-vehicle1/vehicle1-5-5tra.arff"};
		String[] testvehicle1 = {
				"5-fold-vehicle1/vehicle1-5-1tst.arff",
				"5-fold-vehicle1/vehicle1-5-2tst.arff",
				"5-fold-vehicle1/vehicle1-5-3tst.arff",
				"5-fold-vehicle1/vehicle1-5-4tst.arff",
				"5-fold-vehicle1/vehicle1-5-5tst.arff"};
		String[] trainpageblock13v4= {
				"page-blocks-1-3_vs_4-5-fold/page-blocks-1-3_vs_4-5-1tra.arff",
				"page-blocks-1-3_vs_4-5-fold/page-blocks-1-3_vs_4-5-2tra.arff",
				"page-blocks-1-3_vs_4-5-fold/page-blocks-1-3_vs_4-5-3tra.arff",
				"page-blocks-1-3_vs_4-5-fold/page-blocks-1-3_vs_4-5-4tra.arff",
				"page-blocks-1-3_vs_4-5-fold/page-blocks-1-3_vs_4-5-5tra.arff",
		};
		String[] testpageblock13v4= {
				"page-blocks-1-3_vs_4-5-fold/page-blocks-1-3_vs_4-5-1tst.arff",
				"page-blocks-1-3_vs_4-5-fold/page-blocks-1-3_vs_4-5-2tst.arff",
				"page-blocks-1-3_vs_4-5-fold/page-blocks-1-3_vs_4-5-3tst.arff",
				"page-blocks-1-3_vs_4-5-fold/page-blocks-1-3_vs_4-5-4tst.arff",
				"page-blocks-1-3_vs_4-5-fold/page-blocks-1-3_vs_4-5-5tst.arff",
		};
		String[] trainshuttle0v4= {
				"shuttle-c0-vs-c4-5-fold/shuttle-c0-vs-c4-5-1tra.arff",
				"shuttle-c0-vs-c4-5-fold/shuttle-c0-vs-c4-5-2tra.arff",
				"shuttle-c0-vs-c4-5-fold/shuttle-c0-vs-c4-5-3tra.arff",
				"shuttle-c0-vs-c4-5-fold/shuttle-c0-vs-c4-5-4tra.arff",
				"shuttle-c0-vs-c4-5-fold/shuttle-c0-vs-c4-5-5tra.arff",
				
		};
		String[] testshuttle0v4= {
				"shuttle-c0-vs-c4-5-fold/shuttle-c0-vs-c4-5-1tst.arff",
				"shuttle-c0-vs-c4-5-fold/shuttle-c0-vs-c4-5-2tst.arff",
				"shuttle-c0-vs-c4-5-fold/shuttle-c0-vs-c4-5-3tst.arff",
				"shuttle-c0-vs-c4-5-fold/shuttle-c0-vs-c4-5-4tst.arff",
				"shuttle-c0-vs-c4-5-fold/shuttle-c0-vs-c4-5-5tst.arff",
				
		};
		String[] trainyeast1v7= {
				"yeast-1_vs_7-5-fold/yeast-1_vs_7-5-1tra.arff",
				"yeast-1_vs_7-5-fold/yeast-1_vs_7-5-2tra.arff",
				"yeast-1_vs_7-5-fold/yeast-1_vs_7-5-3tra.arff",
				"yeast-1_vs_7-5-fold/yeast-1_vs_7-5-4tra.arff",
				"yeast-1_vs_7-5-fold/yeast-1_vs_7-5-5tra.arff",
		};
		String[] testyeast1v7= {
				"yeast-1_vs_7-5-fold/yeast-1_vs_7-5-1tst.arff",
				"yeast-1_vs_7-5-fold/yeast-1_vs_7-5-2tst.arff",
				"yeast-1_vs_7-5-fold/yeast-1_vs_7-5-3tst.arff",
				"yeast-1_vs_7-5-fold/yeast-1_vs_7-5-4tst.arff",
				"yeast-1_vs_7-5-fold/yeast-1_vs_7-5-5tst.arff",
		};
		String[] trainyeast1458v7= {
				"yeast-1-4-5-8_vs_7-5-fold/yeast-1-4-5-8_vs_7-5-1tra.arff",
				"yeast-1-4-5-8_vs_7-5-fold/yeast-1-4-5-8_vs_7-5-2tra.arff",
				"yeast-1-4-5-8_vs_7-5-fold/yeast-1-4-5-8_vs_7-5-3tra.arff",
				"yeast-1-4-5-8_vs_7-5-fold/yeast-1-4-5-8_vs_7-5-4tra.arff",
				"yeast-1-4-5-8_vs_7-5-fold/yeast-1-4-5-8_vs_7-5-5tra.arff",
				
		};
		String[] testyeast1458v7= {
				"yeast-1-4-5-8_vs_7-5-fold/yeast-1-4-5-8_vs_7-5-1tst.arff",
				"yeast-1-4-5-8_vs_7-5-fold/yeast-1-4-5-8_vs_7-5-2tst.arff",
				"yeast-1-4-5-8_vs_7-5-fold/yeast-1-4-5-8_vs_7-5-3tst.arff",
				"yeast-1-4-5-8_vs_7-5-fold/yeast-1-4-5-8_vs_7-5-4tst.arff",
				"yeast-1-4-5-8_vs_7-5-fold/yeast-1-4-5-8_vs_7-5-5tst.arff",
				
		};
		String[] trainyeast2v8= {
				"yeast-2_vs_8-5-fold/yeast-2_vs_8-5-1tra.arff",
				"yeast-2_vs_8-5-fold/yeast-2_vs_8-5-2tra.arff",
				"yeast-2_vs_8-5-fold/yeast-2_vs_8-5-3tra.arff",
				"yeast-2_vs_8-5-fold/yeast-2_vs_8-5-4tra.arff",
				"yeast-2_vs_8-5-fold/yeast-2_vs_8-5-5tra.arff",
		};
		String[] testyeast2v8= {
				"yeast-2_vs_8-5-fold/yeast-2_vs_8-5-1tst.arff",
				"yeast-2_vs_8-5-fold/yeast-2_vs_8-5-2tst.arff",
				"yeast-2_vs_8-5-fold/yeast-2_vs_8-5-3tst.arff",
				"yeast-2_vs_8-5-fold/yeast-2_vs_8-5-4tst.arff",
				"yeast-2_vs_8-5-fold/yeast-2_vs_8-5-5tst.arff",
		};
		String[] trainshuttlec2vc4= {
				"shuttle-c2-vs-c4-5-fold/shuttle-c2-vs-c4-5-1tra.arff",
				"shuttle-c2-vs-c4-5-fold/shuttle-c2-vs-c4-5-2tra.arff",
				"shuttle-c2-vs-c4-5-fold/shuttle-c2-vs-c4-5-3tra.arff",
				"shuttle-c2-vs-c4-5-fold/shuttle-c2-vs-c4-5-4tra.arff",
				"shuttle-c2-vs-c4-5-fold/shuttle-c2-vs-c4-5-5tra.arff",
		};
		String[] testshuttlec2vc4= {
				"shuttle-c2-vs-c4-5-fold/shuttle-c2-vs-c4-5-1tst.arff",
				"shuttle-c2-vs-c4-5-fold/shuttle-c2-vs-c4-5-2tst.arff",
				"shuttle-c2-vs-c4-5-fold/shuttle-c2-vs-c4-5-3tst.arff",
				"shuttle-c2-vs-c4-5-fold/shuttle-c2-vs-c4-5-4tst.arff",
				"shuttle-c2-vs-c4-5-fold/shuttle-c2-vs-c4-5-5tst.arff",
		};
		String[] trainshuttle6v23= {
				"shuttle-c2-vs-c4-5-fold/shuttle-c2-vs-c4-5-1tst.arff",
				"shuttle-c2-vs-c4-5-fold/shuttle-c2-vs-c4-5-2tst.arff",
				"shuttle-c2-vs-c4-5-fold/shuttle-c2-vs-c4-5-3tst.arff",
				"shuttle-c2-vs-c4-5-fold/shuttle-c2-vs-c4-5-4tst.arff",
				"shuttle-c2-vs-c4-5-fold/shuttle-c2-vs-c4-5-5tst.arff",
		};
		String[] testshuttle6v23= {
				"shuttle-c2-vs-c4-5-fold/shuttle-c2-vs-c4-5-1tst.arff",
				"shuttle-c2-vs-c4-5-fold/shuttle-c2-vs-c4-5-2tst.arff",
				"shuttle-c2-vs-c4-5-fold/shuttle-c2-vs-c4-5-3tst.arff",
				"shuttle-c2-vs-c4-5-fold/shuttle-c2-vs-c4-5-4tst.arff",
				"shuttle-c2-vs-c4-5-fold/shuttle-c2-vs-c4-5-5tst.arff",
		};
		String[] trainyeast1289v7= {
				"yeast-1-2-8-9_vs_7-5-fold/yeast-1-2-8-9_vs_7-5-1tra.arff",
				"yeast-1-2-8-9_vs_7-5-fold/yeast-1-2-8-9_vs_7-5-2tra.arff",
				"yeast-1-2-8-9_vs_7-5-fold/yeast-1-2-8-9_vs_7-5-3tra.arff",
				"yeast-1-2-8-9_vs_7-5-fold/yeast-1-2-8-9_vs_7-5-4tra.arff",
				"yeast-1-2-8-9_vs_7-5-fold/yeast-1-2-8-9_vs_7-5-5tra.arff",
		};
		String[] testyeast1289v7= {
				"yeast-1-2-8-9_vs_7-5-fold/yeast-1-2-8-9_vs_7-5-1tra.arff",
				"yeast-1-2-8-9_vs_7-5-fold/yeast-1-2-8-9_vs_7-5-2tra.arff",
				"yeast-1-2-8-9_vs_7-5-fold/yeast-1-2-8-9_vs_7-5-3tra.arff",
				"yeast-1-2-8-9_vs_7-5-fold/yeast-1-2-8-9_vs_7-5-4tra.arff",
				"yeast-1-2-8-9_vs_7-5-fold/yeast-1-2-8-9_vs_7-5-5tra.arff",
		};
		String[] trainshuttle2v5= {
				"shuttle-2_vs_5-5-fold/shuttle-2_vs_5-5-1tra.arff",
				"shuttle-2_vs_5-5-fold/shuttle-2_vs_5-5-2tra.arff",
				"shuttle-2_vs_5-5-fold/shuttle-2_vs_5-5-3tra.arff",
				"shuttle-2_vs_5-5-fold/shuttle-2_vs_5-5-4tra.arff",
				"shuttle-2_vs_5-5-fold/shuttle-2_vs_5-5-5tra.arff",
		};
		String[] testshuttle2v5= {
				"shuttle-2_vs_5-5-fold/shuttle-2_vs_5-5-1tst.arff",
				"shuttle-2_vs_5-5-fold/shuttle-2_vs_5-5-2tst.arff",
				"shuttle-2_vs_5-5-fold/shuttle-2_vs_5-5-3tst.arff",
				"shuttle-2_vs_5-5-fold/shuttle-2_vs_5-5-4tst.arff",
				"shuttle-2_vs_5-5-fold/shuttle-2_vs_5-5-5tst.arff",
		};
		String[] trainpoker9v7= {
				"poker-9_vs_7-5-fold/poker-9_vs_7-5-1tra.arff",
				"poker-9_vs_7-5-fold/poker-9_vs_7-5-2tra.arff",
				"poker-9_vs_7-5-fold/poker-9_vs_7-5-3tra.arff",
				"poker-9_vs_7-5-fold/poker-9_vs_7-5-4tra.arff",
				"poker-9_vs_7-5-fold/poker-9_vs_7-5-5tra.arff",
				
		};
		String[] testpoker9v7= {
				"poker-9_vs_7-5-fold/poker-9_vs_7-5-1tra.arff",
				"poker-9_vs_7-5-fold/poker-9_vs_7-5-2tra.arff",
				"poker-9_vs_7-5-fold/poker-9_vs_7-5-3tra.arff",
				"poker-9_vs_7-5-fold/poker-9_vs_7-5-4tra.arff",
				"poker-9_vs_7-5-fold/poker-9_vs_7-5-5tra.arff",
				
		};
		if(dataName.equals("poker9v7")&& n == 0) {
			return trainpoker9v7;
		}else if(dataName.equals("poker9v7")&& n == 1) {
			return testpoker9v7;
		}
		if(dataName.equals("shuttle2v5")&& n == 0) {
			return trainshuttle2v5;
		}else if(dataName.equals("shuttle2v5")&& n == 1) {
			return testshuttle2v5;
		}
		if(dataName.equals("yeast1289v7")&& n == 0) {
			return trainyeast1289v7;
		}else if(dataName.equals("yeast1289v7")&& n == 1) {
			return testyeast1289v7;
		}
		if(dataName.equals("shuttle6v23")&& n == 0) {
			return trainshuttle6v23;
		}else if(dataName.equals("shuttle6v23")&& n == 1) {
			return testshuttle6v23;
		}
		if(dataName.equals("shuttlec2vc4")&& n == 0) {
			return trainshuttlec2vc4;
		}else if(dataName.equals("shuttlec2vc4")&& n == 1) {
			return testshuttlec2vc4;
		}
		if(dataName.equals("yeast2v8")&& n == 0) {
			return trainyeast2v8;
		}else if(dataName.equals("yeast2v8")&& n == 1) {
			return testyeast2v8;
		}
		
		if(dataName.equals("yeast1458v7")&& n == 0) {
			return trainyeast1458v7;
		}else if(dataName.equals("yeast1458v7")&& n == 1) {
			return testyeast1458v7;
		}
		if(dataName.equals("yeast1v7")&& n == 0) {
			return trainyeast1v7;
		}else if(dataName.equals("yeast1v7")&& n == 1) {
			return testyeast1v7;
		}
		if(dataName.equals("shuttle0v4")&& n == 0) {
			return trainshuttle0v4;
		}else if(dataName.equals("shuttle0v4")&& n == 1) {
			return testshuttle0v4;
		}
		if(dataName.equals("pageblocks13v4")&& n == 0) {
			return trainpageblock13v4;
		}else if(dataName.equals("pageblocks13v4")&& n == 1) {
			return testpageblock13v4;
		}
		if(dataName.equals("vehicle1")&& n == 0) {
			return trainvehicle1;
		}else if(dataName.equals("vehicle1")&& n == 1) {
			return testvehicle1;
		}
		
		
		if(dataName.equals("ecoli2")&& n == 0) {
			return trainSetEcoli2;
		}else if(dataName.equals("ecoli2")&& n == 1) {
			return testSetEcoli2;
		}
		
		if(dataName.equals("ecoli3")&& n == 0) {
			return trainSetEcoli3;
		}else if(dataName.equals("ecoli3")&& n == 1) {
			return testSetEcoli3;
		}
		
		if(dataName.equals("yeast1")&& n == 0) {

			return trainSetYeast1;
		}else if(dataName.equals("yeast1")&& n == 1) {

			return testSetYeast1;
		}
		
		if(dataName.equals("yeast3")&& n == 0) {
	
			return trainSetYeast3;
		}else if(dataName.equals("yeast3")&& n == 1) {
	
			return testSetYeast3;
		}
		if(dataName.equals("yeast4")&& n == 0) {
			
			return trainSetYeast4;
		}else if(dataName.equals("yeast4")&& n == 1) {
	
			return testSetYeast4;
		}
		if(dataName.equals("yeast5")&& n == 0) {
			
			return trainSetYeast5;
		}else if(dataName.equals("yeast5")&& n == 1) {
	
			return testSetYeast5;
		}
		if(dataName.equals("yeast6")&& n == 0) {
			
			return trainSetYeast6;
		}else if(dataName.equals("yeast6")&& n == 1) {
	
			return testSetYeast6;
		}
		
		
		if(dataName.equals("glass0")&& n == 0) {

			return trainSetglass0;
		}else if(dataName.equals("glass0")&& n == 1) {

			return testSetglass0;
		}
		if(dataName.equals("glass1")&& n == 0) {
		
			return trainSetglass1;
		}else if(dataName.equals("glass1")&& n == 1) {

			return testSetglass1;
		}
		if(dataName.equals("glass0123vs456")&& n == 0) {
	
			return trainSetglass0123vs456;
		}else if(dataName.equals("glass0123vs456")&& n == 1) {
	
			return testSetglass0123vs456;
		}
		if(dataName.equals("ecoli0v1")&& n == 0) {
		
			return trainSetecoli0v1;
		}else if(dataName.equals("ecoli0v1")&& n == 1) {
	
			return testSetecoli0v1;
		}
		if(dataName.equals("vehicle0")&& n == 0) {
			
			return trainSetvehicle0;
		}else if(dataName.equals("vehicle0")&& n == 1) {
	
			return testSetvehicle0;
		}
		if(dataName.equals("ecoli4")&& n == 0) {
			
			return trainSetEcoli4;
		}else if(dataName.equals("ecoli4")&& n == 1) {
	
			return testSetEcoli4;
		}
		if(dataName.equals("glass2")&& n == 0) {
			
			return trainSetglass2;
		}else if(dataName.equals("glass2")&& n == 1) {
	
			return testSetglass2;
		}
		if(dataName.equals("glass5")&& n == 0) {
			
			return trainSetglass5;
		}else if(dataName.equals("glass5")&& n == 1) {
	
			return testSetglass5;
		}
		if(dataName.equals("glass6")&& n == 0) {
			
			return trainSetglass6;
		}else if(dataName.equals("glass6")&& n == 1) {
	
			return testSetglass6;
		}
		if(dataName.equals("glass4")&& n == 0) {
			
			return trainSetglass4;
		}else if(dataName.equals("glass4")&& n == 1) {
	
			return testSetglass4;
		}
		if(dataName.equals("glass016v2")&& n == 0) {
			
			return trainSetglass016v2;
		}else if(dataName.equals("glass016v2")&& n == 1) {
	
			return testSetglass016v2;
		}
		if(dataName.equals("glass016v5")&& n == 0) {
			
			return trainSetglass016v5;
		}else if(dataName.equals("glass016v5")&& n == 1) {
	
			return testSetglass016v5;
		}
		if(dataName.equals("ecoli0137v26")&& n == 0) {
			
			return trainSetEcoli0137v26;
		}else if(dataName.equals("ecoli0137v26")&& n == 1) {
	
			return testSetEcoli0137v26;
		}
		if(dataName.equals("abalone918")&& n == 0) {
			
			return trainabalone918;
		}else if(dataName.equals("abalone918")&& n == 1) {
	
			return testabalone918;
		}
		if(dataName.equals("abalone19")&& n == 0) {
			
			return trainabalone19;
		}else if(dataName.equals("abalone19")&& n == 1) {
	
			return testabalone19;
		}
		if(dataName.equals("newthyroid1")&& n == 0) {
			
			return trainnewthyroid1;
		}else if(dataName.equals("newthyroid1")&& n == 1) {
	
			return testnewthyroid1;
		}
		if(dataName.equals("newthyroid2")&& n == 0) {
			
			return trainnewthyroid2;
		}else if(dataName.equals("newthyroid2")&& n == 1) {
	
			return testnewthyroid2;
		}
		if(dataName.equals("pima")&& n == 0) {
			
			return trainpima;
		}else if(dataName.equals("pima")&& n == 1) {
	
			return testpima;
		}
		return strings;
		
	}
	
}
