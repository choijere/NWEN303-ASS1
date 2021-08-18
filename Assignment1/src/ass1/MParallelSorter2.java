package ass1;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * This sorter uses completable futures.
 * Where the MParallelSorter1 waits until both futures have returned,
 * completable future doesn't.
 * instead, after it sets the "future" branches on their way, the main thread carries on with it's own tasks.
 * The "sortedlists" it needs in that meantime is instead actually a placeholder variable.
 * Once the future's returned with all it's sorted list, it goes straight to the main thread's sorting work.
 * @author 300474835
 *
 */
public class MParallelSorter2 implements Sorter {
	//Thanks to overflow
	//source: https://stackoverflow.com/questions/5886417/using-merge-sort-on-arraylist-and-linkedlist-java
	private static int depthCounter = 0;
	
	@Override
	public <T extends Comparable<? super T>> List<T> sort(List<T> list) {
		depthCounter++;
		if (list.size() < 20) {
			Sorter s = new MSequentialSorter();
			return s.sort(list);
		}
			
		if (list.size() < 2)
			return new ArrayList<T>(list);

		ArrayList<T> first = new ArrayList<T>(list.subList(0, list.size() / 2));
		ArrayList<T> second = new ArrayList<T>(list.subList(list.size()/2, list.size()));
		
		if(depthCounter < 3) { //submit to a future
			try {
				CompletableFuture<List<T>> futureOne = CompletableFuture.supplyAsync(()->sort(first));
				CompletableFuture<List<T>> futureTwo = CompletableFuture.supplyAsync(()->sort(second));
				
				return merge(futureOne.join(), futureTwo.join());
			} catch(CompletionException e) {
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