package com.calderagames.spacelab.util;

import java.util.ArrayList;

public class ResolutionList {
	private ArrayList<Integer> res_width;
	private ArrayList<Integer> res_height;
	private int index;

	public ResolutionList(ArrayList<Integer> res_width, ArrayList<Integer> res_height) {
		this.res_width = res_width;
		this.res_height = res_height;

		index = getResolutionIndex();
	}

	public int getResolutionIndex() {
		for(int i = 0; i < res_width.size(); i++) {
			if(res_width.get(i) == ResolutionHandler.CURRENT_WIDTH && res_height.get(i) == ResolutionHandler.CURRENT_HEIGHT)
				index = i;
		}

		return index;
	}

	public ArrayList<Integer> getWidths() {
		return res_width;
	}

	public ArrayList<Integer> getHeights() {
		return res_height;
	}

	public String[] toStringArray() {
		String[] res = new String[res_width.size()];

		for(int i = 0; i < res_width.size(); i++) {
			res[i] = res_width.get(i) + " x " + res_height.get(i);
		}

		return res;
	}
}
