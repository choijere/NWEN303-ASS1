package ass1;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * This is a merge sort algorithm. But it uses futures.
 * Unlike sequential, it opens a future and the first 6 branches are given to a future.
 * Meaning, there are effectively 4 branches there after sequentially performing the tasks.
 * Those 4 branches do this simultaneously with each other.
 * The .get() method ensures that the thread waits for a future to complete all it's tasks.
 * once it's complete, it returns it's sorted list to the "host" future. the "host" future waits for the other
 * branch to complete.
 * once it has both sorted lists, the thread resumes and merges them together.
 * @author 300474835
 *
 */
public class MParallelSorter1 implements Sorter {
	//Thanks to overflow
	//source: https://stackoverflow.com/questions/5886417/using-merge-sort-on-arraylist-and-linkedlist-java
	private static final ExecutorService executor = Executors.newFixedThreadPool(10);
	private static int depthCounter = 0;
	
	@Override
	public <T extends Comparable<? super T>> List<T> sort(List<T> list) {
		depthCounter++;
			
		if (list.size() < 2)
			return new ArrayList<T>(list);

		if (list.size() < 20) {
			Sorter s = new MSequentialSorter();
			return s.sort(list);
		}

		ArrayList<T> first = new ArrayList<T>(list.subList(0, list.size() / 2));
		ArrayList<T> second = new ArrayList<T>(list.subList(list.size()/2, list.size()));
		
		if(depthCounter < 3) { //submit to a future
			try {
				Future<List<T>> futureOne = executor.submit(()->sort(first));
				Future<List<T>> futureTwo = executor.submit(()->sort(second));
				
				return merge(futureOne.get(), futureTwo.get());
			} catch(InterruptedException e) {
				e.printStackTrace();
			} catch(ExecutionException e) {
				e.printStackTrace();
			}
		} 
		//"else" do it here
		return merge(sort(first), sort(second));
	}
				
		
	private <T extends Comparable<? super T>> ArrayList<T> merge(List<T> first, List<T> second){
		ArrayList<T> newList = new ArrayList<T>();
		
		int i = 0;
		int j = 0;
		
		while(i<first.size() && j<second.size()) {
			if(first.get(i).compareTo(second.get(j)) <= 0) {
				newList.add(first.get(i));
				i++;
			} else {
				newList.add(second.get(j));
				j++;
			}
		}
		if(i==first.size()) {
			for(int k = j; k<second.size(); k++) {
				newList.add(second.get(k));
			}
		} else {
			for(int l = i; l<first.size(); l++) {
				newList.add(first.get(l));
			}
		}
		
		return newList;
	}
}