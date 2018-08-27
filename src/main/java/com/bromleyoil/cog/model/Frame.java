package com.bromleyoil.cog.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Frame {

	private int duration;
	private Map<String, List<Box>> boxes = new HashMap<>();

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public Map<String, List<Box>> getBoxes() {
		return boxes;
	}

	public void setBoxes(Map<String, List<Box>> boxes) {
		this.boxes = boxes;
	}
}
