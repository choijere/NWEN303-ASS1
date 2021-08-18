package ass1;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * This final sorter uses fork join tasks.
 * It's almost exactly like futures.
 * Where a future begin's it's tasks immediately,
 * The forkjoinpool uses an "invoke".
 * So first, it defines what the fork's need to do (via RecursiveTask objects)
 * THEN, it waits for the main thread to give the "execute" method, in this case "invoke"
 * the main thread waits until all task objects have returned.
 * Here, the ForkJoiner returns with a merge-sorted list.
 * The main thread recieves these sorted lists, and merges it before returning it to the mainclass.
 * 
 * @author 300474835
 *
 */
public class MParallelSorter3 implements Sorter {
	//Thanks to overflow and javacodegeeks
	//source: 
	//	https://stackoverflow.com/questions/5886417/using-merge-sort-on-arraylist-and-linkedlist-java
	//	https://www.javacodegeeks.com/2012/08/java-mergesort-using-forkjoin-framework.html
	static final ForkJoinPool pool = new ForkJoinPool();
	
	public <T extends Comparable<? super T>> List<T> sort(List<T> list) {
		List<T> listCopy = new ArrayList<T>();
		listCopy.addAll(list);
		
		if (listCopy.size() < 2)
			return listCopy;
		
		if(list.size()<20) {
			Sorter s = new MSequentialSorter();
			return s.sort(listCopy);
		}

		List<T> sortedList = pool.invoke(new ForkJoiner<T>(listCopy));
		
		System.out.println(sortedList);
		
		return sortedList;
	}
}

class ForkJoiner <T extends Comparable<? super T>> extends RecursiveTask<List<T>> {
	public static int depthCounter = 0;
	
	List<T> list;
	
	public ForkJoiner(List<T> input) {
		list = input;
	}
	
	@Override
	protected List<T> compute() {
		depthCounter++;
		if(depthCounter < 4) {

			ForkJoiner<T> left = new ForkJoiner<T>(list.subList(0, list.size() / 2));
			ForkJoiner<T> right = new ForkJoiner<T>(list.subList(list.size()/2, list.size()));
			invokeAll(left, right);
			
			return merge(left.join(), right.join());
		}
		//else, do merge sort sequentially here.
		return sort(list);
	}
	
	public List<T> sort(List<T> list) {
		if (list.size() < 2)
			return new ArrayList<T>(list);
		
		ArrayList<T> first = new ArrayList<T>(list.subList(0, list.size() / 2));
		ArrayList<T> second = new ArrayList<T>(list.subList(list.size()/2, list.size()));
		
		return merge(sort(first), sort(second));
	}
	
	private List<T> merge(List<T> first, List<T> second) {
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