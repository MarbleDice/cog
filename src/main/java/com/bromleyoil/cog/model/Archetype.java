package com.bromleyoil.cog.model;

import java.util.ArrayList;
import java.util.List;

import com.bromleyoil.cog.persist.annotation.PersistedBy;
import com.bromleyoil.cog.persist.annotation.PropertyOrder;

@PersistedBy("id")
@PropertyOrder({ "id", "name" })
public class Archetype {

	private String id;
	private String name;
	private List<State> states = new ArrayList<>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<State> getStates() {
		return states;
	}

	public State getState(int index) {
		return states.get(index);
	}

	public void setStates(List<State> states) {
		this.states = states;
	}
}
