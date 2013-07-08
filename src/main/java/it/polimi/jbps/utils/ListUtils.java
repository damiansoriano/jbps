package it.polimi.jbps.utils;

import static com.google.common.collect.Lists.newLinkedList;
import java.util.List;

import com.google.common.base.Function;

public class ListUtils {
	
	public static <Q, T> List<T> map(Function<Q, T> func, List<Q> qList) {
		List<T> tList = newLinkedList();
		for (Q q : qList) {
			tList.add(func.apply(q));
		}
		return tList;
	}
	
}
