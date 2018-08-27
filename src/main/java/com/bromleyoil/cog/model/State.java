package com.bromleyoil.cog.model;

import java.util.ArrayList;
import java.util.List;

import com.bromleyoil.cog.persist.annotation.PropertyOrder;
import com.bromleyoil.cog.persist.annotation.ReferencedBy;

@PropertyOrder({ "name" })
public class State {

	private String name;
	private List<Frame> frames = new ArrayList<>();
	private State nextState;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Frame> getFrames() {
		return frames;
	}

	public void setFrames(List<Frame> frames) {
		this.frames = frames;
	}

	@ReferencedBy("name")
	public State getNextState() {
		return nextState;
	}

	public void setNextState(State nextState) {
		this.nextState = nextState;
	}
}
