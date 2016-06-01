package gpig.group2.imageui.util;

import java.util.ArrayList;
import java.util.List;

public class Utils {

	public static <T> void unshift(List<T> li, T itm) {

		List<T> tmp = new ArrayList<>();
		tmp.addAll(li);
		li.clear();

		li.add(itm);

		for(T item : tmp) {
			li.add(item);
		}
		

	}

	public static <T> T pop(List<T> li) {
	
		int ix = li.size() - 1;
	
		T obj = null;
		if (ix >= 0) {
			obj = li.get(ix);
			li.remove(ix);
		}
	
		return obj;
	}

	public static <T> T peek(List<T> li) {
		
		int ix = li.size() - 1;
	
		T obj = null;
		if (ix >= 0) {
			obj = li.get(ix);
		}
	
		return obj;
	}
}
