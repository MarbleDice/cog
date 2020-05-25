package com.bromleyoil.cog.persist;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.bromleyoil.cog.model.Archetype;

@RunWith(JUnit4.class)
public class YamlPersistorTest {

	private YamlPersistor yamlPersistor = new YamlPersistor();

	@Test
	public void represent_Integer_isInteger() {
		assertThat("Integer 3", YamlPersistor.represent(3), is(3));
	}

	@Test
	public void represent_String_isString() {
		assertThat("YAML", YamlPersistor.represent("YAML"), is("YAML"));
	}

	@Test
	public void represent_Map_isMap() {
		Map<String, Integer> myFruit = new HashMap<>();
		myFruit.put("apple", 2);

		Object rv = YamlPersistor.represent(myFruit);

		assertThat("map type", rv, is(instanceOf(Map.class)));
		@SuppressWarnings("unchecked")
		Map<String, Integer> rvMap = (Map<String, Integer>) rv;
		assertThat("map size", rvMap.size(), is(1));
		assertThat("map contains apple", rvMap.containsKey("apple"), is(true));
		assertThat("apple value", rvMap.get("apple"), is(2));
	}

	@Test
	public void represent_bean_isMap() {
		Archetype arch = new Archetype();
		arch.setId("player");
		arch.setName("Player");

		Object rv = YamlPersistor.represent(arch);

		assertThat("type", rv, is(instanceOf(Map.class)));
		@SuppressWarnings("unchecked")
		Map<String, Object> rvMap = (Map<String, Object>) rv;
		assertThat("map size", rvMap.size(), greaterThanOrEqualTo(2));
		assertThat("arch id", rvMap.get("id"), is("player"));
		assertThat("arch name", rvMap.get("name"), is("Player"));
	}

	@Test
	public void represent_beanWithDefaultField_containsNoKey() {
		Archetype arch = new Archetype();
		arch.setId("player");

		Object rv = YamlPersistor.represent(arch);

		assertThat("type", rv, is(instanceOf(Map.class)));
		@SuppressWarnings("unchecked")
		Map<String, Object> rvMap = (Map<String, Object>) rv;
		assertThat("map size", rvMap.size(), greaterThanOrEqualTo(1));
		assertThat("arch id", rvMap.get("id"), is("player"));
		assertThat("map contains name", rvMap.containsKey("name"), is(false));
	}
}
