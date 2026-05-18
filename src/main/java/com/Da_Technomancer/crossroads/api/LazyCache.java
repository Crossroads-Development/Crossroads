package com.Da_Technomancer.crossroads.api;

import com.Da_Technomancer.crossroads.Crossroads;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Stores a reference to a single value, which can either be set at object creation or lazy-loaded based on a supplier
 * Meant for contexts where we want to cache a lazy-loaded value, but we need to store the cache in an immutable variable
 * @param <T> The datatype to store
 */
public class LazyCache<T>{

	private T value;
	private Supplier<T> valueSupplier;

	public LazyCache(@Nonnull Supplier<T> valueSupplier){
		this.value = null;
		this.valueSupplier = valueSupplier;
	}

	public static <T> LazyCache<T> create(@Nullable T fixedValue){
		return new LazyCache<>(fixedValue);
	}

	private LazyCache(@Nullable T value){
		this.value = value;
		this.valueSupplier = null;
	}

	@Nullable
	public T get(){
		if(valueSupplier != null){
			value = valueSupplier.get();
			valueSupplier = null;
		}
		return value;
	}

	private Object identityGet(){
		try{
			return get();
		}catch(Exception e){
			//Implies we tried valueSupplier.get() too early - define identity based on the value supplier itself
			Crossroads.logger.error("Premature LazyCache identity fetching; report to mod author", e);
			return valueSupplier;
		}
	}

	@Override
	public boolean equals(Object o){
		if(o == null || getClass() != o.getClass()){
			return false;
		}
		LazyCache<?> lazyCache = (LazyCache<?>) o;
		return Objects.equals(identityGet(), lazyCache.identityGet());
	}

	@Override
	public int hashCode(){
		return Objects.hash(identityGet());
	}
}
