package com.example.jzm.ttrsadmin;

import java.util.Comparator;

public class PinyinComparator implements Comparator<com.example.jzm.ttrsadmin.SortModel> {

	public int compare(com.example.jzm.ttrsadmin.SortModel o1, com.example.jzm.ttrsadmin.SortModel o2) {
		if (o1.getLetters().equals("@")
				|| o2.getLetters().equals("#")) {
			return 1;
		} else if (o1.getLetters().equals("#")
				|| o2.getLetters().equals("@")) {
			return -1;
		} else {
			return o1.getLetters().compareTo(o2.getLetters());
		}
	}

}
