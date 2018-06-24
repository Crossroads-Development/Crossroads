package com.Da_Technomancer.crossroads.items.crafting;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

	private HashSet<Pair<Predicate<T>, V>> entries = new HashSet<Pair<Predicate<T>, V>>();

	public int size(){
		return entries.size();
	}

	public boolean isEmpty(){
		return entries.isEmpty();
	}

	public boolean containsKey(Predicate<T> key){
		for(Pair<Predicate<T>, V> ent : entries){
			if(ent.getKey().equals(key)){
				return true;
			}
		}

		return false;
	}

	public V get(T target){
		for(Pair<Predicate<T>, V> ent : entries){
			if(ent.getKey().test(target)){
				return ent.getValue();
			}
		}

		return null;
	}

	public V put(Predicate<T> key, V value){
		V prevValue = null;
		for(Pair<Predicate<T>, V> ent : entries){
			if(ent.getKey().equals(key)){
				prevValue = ent.getValue();
				entries.remove(ent);
				break;
			}
		}
		entries.add(Pair.of(key, value));
		return prevValue;
	}

	public V remove(Predicate<T> key){
		V prevValue = null;
		for(Pair<Predicate<T>, V> ent : entries){
			if(ent.getKey().equals(key)){
				prevValue = ent.getValue();
				entries.remove(ent);
				break;
			}
		}
		return prevValue;
	}

	public void clear(){
		entries.clear();
	}

	public Set<Pair<Predicate<T>, V>> entrySet(){
		return entries;
	}

	public List<V> values(){
		List<V> val = new ArrayList<>(entries.size());
		for(Pair<Predicate<T>, V> ent : entries){
			val.add(ent.getRight());
		}
		return val;
	}
}
