package ass1;

import java.util.ArrayList;
import java.util.List;

/**
 * This sorts data using the merge sort algorithm, but executes it sequentially.
 * Meaning, all actions are done in this one singular thread.
 * First, it splits the array, just like the algorithm suggests, using depth first traversal.
 * Meaning, it branches down one array, and keeps branching down until it's recusion is complete, then it
 * goes to the very next, the furtherest down to the bottom.
 * All before it returns to the root and finally merges the two starting branches together.
 * @author 300474835
 *
 */
public class MSequentialSorter implements Sorter {
	//Thanks to overflow
	//source: https://stackoverflow.com/questions/5886417/using-merge-sort-on-arraylist-and-linkedlist-java
	
	@Override
	public <T extends Comparable<? super T>> List<T> sort(List<T> list) {
		if (list.size() < 2)
			return new ArrayList<T>(list);
		
		ArrayList<T> first = new ArrayList<T>(list.subList(0, list.size() / 2));
		ArrayList<T> second = new ArrayList<T>(list.subList(list.size()/2, list.size()));
		
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