package gpig.group2.imageui.util;

import java.util.List;

public class Utils {

	public static <T> void unshift(List<T> li, T itm) {
		int lastIx = li.size() - 1;
		
		for (int ix = lastIx; ix >= 0; ix--) {
			li.add(ix + 1, li.get(ix));
		}
		
		li.add(0, itm);
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

}
