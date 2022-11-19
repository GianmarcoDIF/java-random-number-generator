package rng;

import java.util.ArrayList;

public class Run {
	int runLength;
	int runCount;

	public Run(int runLength) {
		this.setRunLength(runLength);
		this.setRunCount(0);
	}

	public void incCount() {
		this.runCount +=1 ;
	}

	public int getRunLength() {
		return runLength;
	}

	public void setRunLength(int runLength) {
		this.runLength = runLength;
	}

	public int getRunCount() {
		return runCount;
	}

	public void setRunCount(int runCount) {
		this.runCount = runCount;
	}

	public static ArrayList<Run> addRun(ArrayList<Run> runs, int l){

		//if is the first
		if(runs.size()==0) {
			//add the skipped value as zero
			for(int i=1; i< l; i++) {
				runs.add(new Run(i));
			}
			//add this value
			runs.add(new Run(l));
			runs.get(l-1).incCount();
			return runs;
		}

		//check if there are runs with zero count skipped before adding the new run length.
		int lastAddedLength = runs.get(runs.size() - 1 ).getRunLength();
		if(l > lastAddedLength) {
			//add empty runs
			for(int i=lastAddedLength + 1 ; i< l; i++) {
				runs.add(new Run(i));
			}
			runs.add(new Run(l));
			runs.get(l-1).incCount();
		}
		else runs.get(l-1).incCount();

		return runs;
	}
}