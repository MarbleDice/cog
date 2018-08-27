package com.bromleyoil.cog;

import static java.lang.System.*;

import java.util.Arrays;

import org.yaml.snakeyaml.Yaml;

import com.bromleyoil.cog.model.Archetype;
import com.bromleyoil.cog.model.Box;
import com.bromleyoil.cog.model.Frame;
import com.bromleyoil.cog.model.State;
import com.bromleyoil.cog.persist.YamlPersistor;

public final class Main {

	public static void main(String[] args) {
		Archetype arch = getArch();

		out.println((new Yaml()).dump(arch));

		YamlPersistor yaml = new YamlPersistor();
		out.println(yaml.present(arch));
	}

	public static Archetype getArch() {
		Frame frame;
		State state;

		Archetype arch = new Archetype();
		arch.setId("player");
		arch.setName("The Player");

		state = new State();
		state.setName("idle");
		frame = new Frame();
		frame.setDuration(1);
		state.getFrames().add(frame);
		arch.getStates().add(state);

		state = new State();
		state.setName("attack");
		frame = new Frame();
		frame.setDuration(21);
		frame.getBoxes().put("hit", Arrays.asList(new Box(2, 2, 28, 28)));
		state.getFrames().add(frame);
		frame = new Frame();
		frame.setDuration(22);
		state.getFrames().add(frame);
		frame = new Frame();
		frame.setDuration(23);
		state.getFrames().add(frame);
		arch.getStates().add(state);

		arch.getState(0).setNextState(arch.getState(1));

		return arch;
	}
}
