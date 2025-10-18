package com.Da_Technomancer.crossroads.api.alchemy;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public record ReagentStack(@Nonnull String typeId, int amount){

	public static final Codec<ReagentStack> CODEC = RecordCodecBuilder.create(instance -> instance.group(Codec.STRING.fieldOf("type").forGetter(ReagentStack::typeId), ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("qty", 1).forGetter(ReagentStack::amount)).apply(instance, ReagentStack::new));
	public static final StreamCodec<ByteBuf, ReagentStack> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, ReagentStack::typeId, ByteBufCodecs.VAR_INT, ReagentStack::amount, ReagentStack::new);

	//The default reagent used for empty stacks in place of null
	private static final String DEFAULT = EnumReagents.WATER.id();

	public ReagentStack(@Nullable String typeId, int amount){
		this.typeId = typeId == null ? DEFAULT : typeId;
		this.amount = amount;
	}

	public ReagentStack(@Nonnull IReagent type, int amount){
		this(type.getID(), amount);
	}

	public boolean isEmpty(){
		return amount <= 0;
	}

	/**
	 * @deprecated Use typeId() instead
	 */
	@Deprecated(forRemoval = true)
	public String getId(){
		return typeId();
	}

	@Nonnull
	public IReagent getType(){
		IReagent type = ReagentManager.getReagent(typeId);
		return type == null ? ReagentManager.getReagent(DEFAULT) : type;
	}

	/**
	 * @return The amount of this substance. In moles (where applicable).
	 * @deprecated Use amount() instead
	 */
	@Deprecated(forRemoval = true)
	public int getAmount(){
		return amount();
	}

	@Nonnull
	@Override
	public String toString(){
		if(isEmpty()){
			return "Empty Reagent";
		}else{
			IReagent reag = ReagentManager.getReagent(typeId);
			if(reag == null){
				return "Unresolved: " + typeId + ", Qty: " + amount;
			}else{
				return reag.getName() + ", Qty: " + amount;
			}
		}
	}
}
