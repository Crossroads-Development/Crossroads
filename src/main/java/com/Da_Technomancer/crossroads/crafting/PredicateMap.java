package com.Da_Technomancer.crossroads.crafting;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

/**
 * For storing recipes with Predicate keys
 * 
 * This (intentionally) does not follow the contract of the map interface, so it does not actually extend map. 
 * @param <T> What the Predicate compares
 * @param <V> The stored value type
 */
public class PredicateMap<T, V>{

	private final HashMap<Predicate<T>, V> entries = new HashMap<>();
	private final V nullCase;

	public PredicateMap(){
		nullCase = null;
	}

	/**
	 * @param nullCase What this object's methods should return in place of null. Used for things like ItemStacks which rarely permit null values
	 */
	public PredicateMap(V nullCase){
		this.nullCase = nullCase;
	}

	public int size(){
		return entries.size();
	}

	public boolean isEmpty(){
		return entries.isEmpty();
	}

	public boolean containsKey(@Nonnull Predicate<T> key){
		return entries.containsKey(key);
	}

	/**
	 * Returns the value associated with the first predicate to pass on target, or nullcase if nothing matched
	 * Runs in linear time, unlike a traditional hashmap where this runs in constant time
	 * @param target The object to be matched by a predicate key
	 * @return The value associated with the first predicate to pass
	 */
	public V get(T target){
		for(Map.Entry<Predicate<T>, V> ent : entries.entrySet()){
			if(ent.getKey().test(target)){
				return ent.getValue();
			}
		}

		return nullCase;
	}

	public V put(@Nonnull Predicate<T> key, V value){
		V removed = entries.put(key, value);
		return removed == null ? nullCase : removed;
	}

	public V remove(@Nonnull Predicate<T> key){
		V removed = entries.remove(key);
		return removed == null ? nullCase : removed;
	}

	public void clear(){
		entries.clear();
	}

	public Set<Map.Entry<Predicate<T>, V>> entrySet(){
		return entries.entrySet();
	}

	public Collection<V> values(){
		return entries.values();
	}
}
