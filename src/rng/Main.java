package rng;

import java.security.Security;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Set;

public class Main {

	public static void main(String[] args) {

		Scanner scanner = new Scanner(System.in);		

		String unsecAlg = requestUnsecureAlgorithm(scanner);
		if(unsecAlg==null) return; 

		String secAlg = requestSecureAlgorithm(scanner);		
		if(secAlg==null) return; 

		int totalAttempts=requestNumberOfAttempts(scanner);
		if(totalAttempts==0) return;

		int resLen = requestNumberOfBits(scanner);
		if(resLen==0) return;

		System.out.println();

		ArrayList<Test> t = Test.declareTests(new ArrayList<Test>(), false, unsecAlg, totalAttempts, resLen);
		t = Test.declareTests(t, true, secAlg, totalAttempts, resLen);

		for(int i=0; i<t.size(); i++) {
			t.get(i).compute();
		}

		for(int i=0; i<t.size(); i++) {
			t.get(i).computeRunTest();;
		}

		System.out.println();

		for(int i=0; i<t.size(); i++) {
			t.get(i).printVariance();
		}

		if(resLen*totalAttempts < 50000) {//overflow
			System.out.print("\nShow all the generated bits?(Yes/No):");
			if(scanner.nextLine().equals("Yes")) {
				for(int i=0; i<t.size(); i++) {
					t.get(i).printMatrix();
					System.out.println();
				}
			}
		}

		scanner.close();
	}


	public static String requestUnsecureAlgorithm(Scanner scanner) {
		System.out.println("Impemented NON CSPRNG: [Math, Random, ThreadLocalRandom]");
		System.out.print("Insert NON CSPRNG name (write 'All' to use all): ");
		String unsecAlg = scanner.nextLine();
		if(!unsecAlg.equals("All") && !unsecAlg.equals("Math") && !unsecAlg.equals("Random") && !unsecAlg.equals("ThreadLocalRandom")) {
			scanner.close();
			System.out.println("\n" + unsecAlg + " is not valid.");
			return null;
		}
		return unsecAlg;
	}
	public static String requestSecureAlgorithm(Scanner scanner) {
		Set<String> availableSec = Security.getAlgorithms("SecureRandom");;
		System.out.println("Available CSPRNG algorithms for SecureRandom: " + availableSec);
		System.out.print("Insert CSPRNG name (write 'All' to use all): ");
		String secAlg = scanner.nextLine();
		if(!availableSec.contains(secAlg) && !secAlg.equals("All")) {
			scanner.close();
			System.out.println("\n" + secAlg + " is not valid.");
			return null;
		}
		return secAlg;
	}

	public static int requestNumberOfAttempts(Scanner scanner) {
		System.out.println("How many attempts for each algorithm? ");
		int totalAttempts=0;
		try {
			totalAttempts = Integer.parseInt(scanner.nextLine());
		}
		catch (NumberFormatException e ) {
			System.out.println("Invalid integer format.");
			return 0;
		}
		return totalAttempts;
	}

	public static int requestNumberOfBits(Scanner scanner) {
		System.out.println("How many bits for each attempt (multiple of 8): ");
		int resLen=0;
		try {
			resLen = Integer.parseInt(scanner.nextLine());
		}
		catch (NumberFormatException e ) {
			System.out.println("Invalid integer format.");
			return 0;
		}
		if(resLen%8!=0) {
			System.out.println("Not a multiple of 8!!!");
			return 0;
		}
		return resLen;
	}
}