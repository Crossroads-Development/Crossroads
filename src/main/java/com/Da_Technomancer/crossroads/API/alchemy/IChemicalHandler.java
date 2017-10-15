package com.Da_Technomancer.crossroads.API.alchemy;

import javax.annotation.Nonnull;

import net.minecraft.util.EnumFacing;

public interface IChemicalHandler extends IAlchemyContainer{
	
	@Nonnull
	public EnumTransferMode getMode(EnumFacing side);
	
	@Nonnull
	public EnumContainerType getChannel(EnumFacing side);
	
	/**
	 * MUST be called after changing reagents externally
	 */
	public void markChanged();

}
