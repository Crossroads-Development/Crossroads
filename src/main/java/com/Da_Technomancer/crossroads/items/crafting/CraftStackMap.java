package com.Da_Technomancer.crossroads.items.crafting;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

/**
 * For storing recipes with ICraftingStack keys
 * 
 * This (intentionally) does not follow the contract of the map interface, so it does not actually extend map. 
 * @param <T> What the ICraftingStack compares
 * @param <K> The type of CraftingStack
 * @param <V> The stored value type
 */
public class CraftStackMap<T, K extends ICraftingStack<T>, V>{

	HashSet<Pair<K, V>> entries = new HashSet<Pair<K, V>>();

	public int size(){
		return entries.size();
	}

	public boolean isEmpty(){
		return entries.isEmpty();
	}

	public boolean containsKey(K key){
		for(Pair<K, V> ent : entries){
			if(ent.getKey().equals(key)){
				return true;
			}
		}

		return false;
	}

	public V get(T target){
		for(Pair<K, V> ent : entries){
			if(ent.getKey().softMatch(target)){
				return ent.getValue();
			}
		}

		return null;
	}

	public V put(K key, V value){
		V prevValue = null;
		for(Entry<K, V> ent : entries){
			if(ent.getKey().equals(key)){
				prevValue = ent.getValue();
				entries.remove(ent);
				break;
			}
		}
		entries.add(Pair.of(key, value));
		return prevValue;
	}

	public V remove(K key){
		V prevValue = null;
		for(Entry<K, V> ent : entries){
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

	public Set<Pair<K, V>> entrySet(){
		return entries;
	}
}
