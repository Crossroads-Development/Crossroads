package com.Da_Technomancer.crossroads.items.crafting;

import java.util.List;
import java.util.function.Predicate;

/**
 * A form of Predicate that also provides a List of matches, for JEI integration
 * 
 * Implementers of this interface should override {@link Object#equals(Object)} & {@link Object#hashCode()} for aiding with CraftTweaker integration
 */
@Deprecated
public interface RecipePredicate<T> extends Predicate<T>{
	
	/**
	 * A list of every T that matches this stack, it need not be exhaustive (every possible metadata, NBT, stacksize, etc.)
	 * This is only used for JEI support.
	 */
	public List<T> getMatchingList();
}
