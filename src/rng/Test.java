package rng;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class Test {
	boolean[][] matrix;	
	boolean isFullFill;
	boolean isSafe;
	int totalAttempts;
	int resLen;
	String algName;
	ArrayList<ArrayList<Run>> runsTest;
	float[] avg, variance, stdDev , vc, rate;


	public Test(boolean isFullFill, boolean isSafe, String algName, int totalAttempts, int resLen) {
		super();
		this.matrix = new boolean[totalAttempts][resLen];
		this.isFullFill = isFullFill;
		this.isSafe = isSafe;
		this.totalAttempts=totalAttempts;
		this.resLen = resLen;
		this.algName = algName;
		this.runsTest = new ArrayList<ArrayList<Run>>();
	}

	public boolean[][] getMatrix() {
		return matrix;
	}
	public void setMatrix(boolean[][] matrix) {
		this.matrix = matrix;
	}
	public boolean isFullFill() {
		return isFullFill;
	}
	public void setFullFill(boolean isFullFill) {
		this.isFullFill = isFullFill;
	}
	public boolean isSafe() {
		return isSafe;
	}
	public void setSafe(boolean isSafe) {
		this.isSafe = isSafe;
	}
	public String getAlgName() {
		return algName;
	}
	public void setAlgName(String algName) {
		this.algName = algName;
	}

	public static ArrayList<Test> declareTests(ArrayList<Test> t, boolean isSafe, String algName, int totalAttempts, int resLen) {
		if(!isSafe) {
			switch(algName) {
			case "All" :
				t.add(new Test(false, false, "Random", totalAttempts, resLen));
				t.add(new Test(false, false, "ThreadLocalRandom", totalAttempts, resLen));
				t.add(new Test(false, false, "Math", totalAttempts, resLen));

				t.add(new Test(true, false, "Random", totalAttempts, resLen));
				t.add(new Test(true, false, "ThreadLocalRandom", totalAttempts, resLen));
				break;
			default: { //AlgorithmSpecified
				t.add(new Test(false, false, algName, totalAttempts, resLen));
				if(!algName.equals("Math")) {
					t.add(new Test(true, false, algName, totalAttempts, resLen));
				}
			}
			}
		}
		else {
			switch(algName) {
			case "All" :
				Set<String> availableSec = Security.getAlgorithms("SecureRandom");
				for (String found : availableSec) {
					//availableSec.contains(algNamesecAlg) && !secAlg.equals("All")
					t.add(new Test(true, true, found, totalAttempts, resLen));
					t.add(new Test(false, true, found, totalAttempts, resLen));
				}
				break;

			default: { //AlgorithmSpecified
				t.add(new Test(false, true, algName, totalAttempts, resLen));
				t.add(new Test(true, true, algName, totalAttempts, resLen));
			}
			}
		}
		return t;
	}

	public void compute() {
		if(this.isSafe && this.isFullFill) {
			secFullFillGeneration();
			return;
		}
		if(this.isSafe && !this.isFullFill) {
			secBitByBitGeneration();
			return;
		}
		if(!this.isSafe && this.isFullFill) {
			unsecFullFillGeneration();
			return;
		}
		if(!this.isSafe && !this.isFullFill) {
			unsecBitByBitGeneration();
		}
	}

	public void unsecBitByBitGeneration(){
		LocalDateTime startTime = LocalDateTime.now();
		for(int i=0; i<totalAttempts; i++) {

			switch(algName) {
			case "Math":
				for(int j=0; j<resLen; j++) {					
					matrix[i][j] = Math.random() < 0.5;
				}
				break;

			case "ThreadLocalRandom":
				for(int j=0; j<resLen; j++) {					
					matrix[i][j] = ThreadLocalRandom.current().nextBoolean();
				}
				break;
			case "Random":
				Random unsecRandom= new Random(); 
				for(int j=0; j<resLen; j++) {					
					matrix[i][j] = unsecRandom.nextBoolean();
				}
			}
		}
		printTestInfo();
		System.out.print(" generated in "+ ChronoUnit.MILLIS.between(startTime, LocalDateTime.now())+ " ms.");
	}

	public void unsecFullFillGeneration(){
		if(algName=="Math") return; //FullFill not supported
		byte bytes[] = new byte[resLen]; //store the random bytes
		LocalDateTime startTime = LocalDateTime.now();

		for(int i=0; i<totalAttempts; i++) {

			switch(algName) {
			case "ThreadLocalRandom":
				ThreadLocalRandom.current().nextBytes(bytes);
				matrix[i] = byteArrayToBitArray(bytes, resLen);
				break;
			case "Random":
				Random unsecRandom= new Random();
				unsecRandom.nextBytes(bytes);
				matrix[i] = byteArrayToBitArray(bytes, resLen);
			}
		}
		printTestInfo();
		System.out.print(" generated in "+ ChronoUnit.MILLIS.between(startTime, LocalDateTime.now())+ " ms.");
	}

	public void secFullFillGeneration(){
		byte bytes[] = new byte[resLen]; //store the random bytes
		LocalDateTime startTime = LocalDateTime.now();

		for(int i=0; i<totalAttempts; i++) {
			//Instantiate the object
			SecureRandom secureRandom = null;
			try {
				secureRandom = SecureRandom.getInstance(algName);
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}

			secureRandom.nextBytes(bytes);
			//convert into bit array and fill the matrix
			matrix[i] = byteArrayToBitArray(bytes, resLen);
		}
		printTestInfo();
		System.out.print(" generated in "+ ChronoUnit.MILLIS.between(startTime, LocalDateTime.now())+ " ms.");
	}

	public void secBitByBitGeneration(){
		LocalDateTime startTime = LocalDateTime.now();

		for(int i=0; i<totalAttempts; i++) {
			//Instantiate the object
			SecureRandom secureRandom = null;
			try {
				secureRandom = SecureRandom.getInstance(algName);
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}

			for(int j=0; j<resLen; j++) {
				matrix[i][j] = secureRandom.nextBoolean();
			}
		}
		printTestInfo();
		System.out.print(" generated in "+ ChronoUnit.MILLIS.between(startTime, LocalDateTime.now())+ " ms.");
	}

	public static boolean[] byteArrayToBitArray(byte[] bytes, int resLen){
		boolean [] bits= new boolean[resLen];
		for(int j=0; j< resLen; j+=8) {
			bits[j+0] = ((bytes[j] & 0x01) != 0);
			bits[j+1] = ((bytes[j] & 0x02) != 0);
			bits[j+2] = ((bytes[j] & 0x04) != 0);
			bits[j+3] = ((bytes[j] & 0x08) != 0);
			bits[j+4] = ((bytes[j] & 0x10) != 0);
			bits[j+5] = ((bytes[j] & 0x20) != 0);
			bits[j+6] = ((bytes[j] & 0x40) != 0);
			bits[j+7] = ((bytes[j] & 0x80) != 0);
		}
		return bits;
	}

	public void computeRunTest() {
		runsTest = new ArrayList<ArrayList<Run>>();

		for(int i=0; i < totalAttempts; i++) {
			ArrayList<Run> runs = new ArrayList<Run>();
			int l=1;
			for(int j=1; j<resLen; j++) {
				if(matrix[i][j]==matrix[i][j-1]) {
					++l;
					continue;
				}

				else {
					runs = Run.addRun(runs, l);
					l=1;
				}
			}

			//consider also the last bit
			if(matrix[i][resLen-1]==matrix[i][resLen-2]) {
				if(runs.size()< l) { //if last run is the largest, create it.
					runs = Run.addRun(runs, l);
				}
				else {
					runs.get(l-1).incCount();
				}
			}
			else { //last bit is on run 1
				if(runs.size()< 1) {//run of length 1 does not exist yet
					runs = Run.addRun(runs, 1);
				}
				runs.get(0).incCount();
			}

			//add all the runs produced by i to the overall runs
			runsTest.add(runs);
		}
	}

	public void computeRate(){ 
		rate = new float[totalAttempts];
		Arrays.fill(rate, 0);
		for (int i=0; i<totalAttempts; i++) {
			int ones=0;
			int zeroes=0;
			for(int j=0; j< resLen; j++) {
				if(matrix[i][j] == true) ++ones;
				else ++zeroes;
			}
			rate[i]=((float)ones/zeroes);
		}
	}

	public void printVariance() {

		//find the longest
		int longest=0;
		for(int i=0; i<totalAttempts; i++) {
			int tl= runsTest.get(i).size();
			if(tl > longest) longest = tl;
		}
		avg = new float[longest];
		variance = new float[longest];
		stdDev = new float[longest];
		vc = new float[longest];
		Arrays.fill(avg, 0);
		Arrays.fill(variance, 0);
		Arrays.fill(stdDev, 0);
		Arrays.fill(vc, 0);

		//Average
		for(int i=0; i<totalAttempts; i++) {
			for(int j=0; j< runsTest.get(i).size(); j++) {
				avg[j] += (float) runsTest.get(i).get(j).getRunCount();
			}
		}

		for(int i=0; i<longest -1 ; i++) {
			avg[i] = avg[i]/totalAttempts;
		}


		//Variance
		for(int i=0; i<totalAttempts; i++) {
			for(int j=0; j< longest -1 ; j++) {
				int xi = 0;
				if(runsTest.get(i).size() > j) {
					xi = runsTest.get(i).get(j).getRunCount();
				}
				variance[j] += Math.pow(xi -avg[j], 2);
			}
		}

		for(int i=0; i< longest; i++) {
			variance[i] /= totalAttempts;
		}

		//Standard Deviation
		for(int i=0; i< longest; i++) {
			stdDev[i] = (float) Math.sqrt(variance[i]);
		}

		//Coefficient of variation
		for(int i=0; i< longest; i++) {
			vc[i] = stdDev[i]/avg[i];
		}

		printTestInfo();
		System.out.print("\n    AVERAGE   |");
		for(int i=0; i<longest; i++) {
			System.out.printf("(" + (i+1) + ")%.2f ", avg[i]);
		}

		System.out.print("\n   VARIANCE   |");
		for(int i=0; i<longest; i++) {
			System.out.printf("(" + (i+1) + ")%.2f ", variance[i]);
		}

		System.out.print("\n   STD.DEV.   |");
		for(int i=0; i<longest; i++) {
			System.out.printf("(" + (i+1) + ") %.2f ", stdDev[i]);
		}

		System.out.print("\n    COEVAR    |");
		for(int i=0; i<longest; i++) {
			System.out.printf("(" + (i+1) + ") %.2f ", stdDev[i]);
		}
		System.out.println();
	}

	public void printTests() {
		printTestInfo();
		System.out.println("TEST");
		//find the longest run in all the test
		int longest=0;
		for(int i=0; i < totalAttempts; i++) {
			for(int j=0; j<runsTest.get(i).size(); j++) {
				int rl = runsTest.get(i).get(j).getRunLength();
				if(rl > longest)
					longest=rl;
			}
		}

		for(int i=0; i < totalAttempts; i++) {
			System.out.printf("\n%02d) |%f|", rate[i]);
			for(int j=0; j<runsTest.get(i).size(); j++) {
				System.out.printf("| %02d %04d ", runsTest.get(i).get(j).getRunLength(), runsTest.get(i).get(j).getRunCount());
			}
		}
	}


	public void printMatrix() {
		printTestInfo();
		computeRate();
		for(int i=0; i< totalAttempts; i++) {
			System.out.printf("\n%02d) %.5f", i, rate[i]);
			System.out.printf(" | ");
			for(int j=0; j< resLen; j++) {
				System.out.print((matrix[i][j])  ? 1 : 0);
			}
		}
	}

	public void printTestInfo() {
		System.out.print("\n[Secure(" + isSafe +") FullFill(" + isFullFill + ") " + algName + "]");
	}
}