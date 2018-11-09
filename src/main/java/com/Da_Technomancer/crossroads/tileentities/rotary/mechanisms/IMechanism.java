package com.Da_Technomancer.crossroads.tileentities.rotary.mechanisms;

import com.Da_Technomancer.crossroads.API.rotary.EnumGearType;
import com.Da_Technomancer.essentials.shared.IAxisHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IMechanism{

	/**
	 * Called when the redstone signal received changes
	 * @param prevValue The previous redstone value
	 * @param newValue The new redstone value
	 * @param mat The material of this mechanism
	 * @param side The side this mechanism is on. If null, this is in the axle slot (center)
	 * @param axis If side is null (axle slot), this is the orientation of this mechanism. If side is not null, this should be ignored, and may be null
	 * @param motData The motion data of this mechanism, in order [0]=w, [1]=E, [2]=P, [3]=lastE
	 * @param te The containing TileEntity
	 */
	public default void onRedstoneChange(double prevValue, double newValue, EnumGearType mat, @Nullable EnumFacing side, @Nullable EnumFacing.Axis axis, double[] motData, MechanismTileEntity te){

	}

	/**
	 * Gets the ratiator (or comparator) read output signal for this mechanism. Will only be called if this mechanism is in the axle slot.
	 * @param mat The material of this mechanism
	 * @param axis The axle axis. Will not be null
	 * @param motData The motionData of this mechanism
	 * @param te The calling TE
	 * @return The value a ratiator should read. If a comparator is reading this, it will be floored and bounded to [0, 15].
	 */
	public default double getRatiatorSignal(EnumGearType mat, EnumFacing.Axis axis, double[] motData, MechanismTileEntity te){
		return 0;
	}

	/**
	 * Returns the moment of inertia of this component when added
	 * @param mat The material of this mechanism
	 * @param side The side this mechanism is on. If null, this is in the axle slot (center)
	 * @param axis If side is null (axle slot), this is the orientation of this mechanism. If side is not null, this should be ignored, and may be null
	 * @return The moment of inertia
	 */
	public double getInertia(EnumGearType mat, @Nullable EnumFacing side, @Nullable EnumFacing.Axis axis);

	/**
	 * Whether the capability exists. Will only be called for the AxleHandlerCapability and CogHandlerCapability
	 * @param cap The capability
	 * @param capSide The EnumFacing from hasCapability in TileEntity
	 * @param mat The material of this mechanism
	 * @param side The side this mechanism is on. If null, this is in the axle slot (center)
	 * @param axis If side is null (axle slot), this is the orientation of this mechanism. If side is not null, this should be ignored, and may be null
	 * @param te The containing TileEntity
	 * @return Whether to allow this capability
	 */
	public boolean hasCap(Capability<?> cap, EnumFacing capSide, EnumGearType mat, @Nullable EnumFacing side, @Nullable EnumFacing.Axis axis, MechanismTileEntity te);

	/**
	 * Called when performing a rotary propogation. The mechanism is responsible for propogating
	 * @param mat The material of this mechanism
	 * @param side The side this mechanism is on. If null, this is in the axle slot (center)
	 * @param axis If side is null (axle slot), this is the orientation of this mechanism. If side is not null, this should be ignored, and may be null
	 * @param te The containing TileEntity
	 * @param handler The calling SidedAxleHandler.
	 * @param masterIn The source Master Axis
	 * @param key The propogation key
	 * @param rotRatioIn The previous rotation ratio
	 * @param lastRadius The previous radius
	 */
	public void propogate(EnumGearType mat, @Nullable EnumFacing side, @Nullable EnumFacing.Axis axis, MechanismTileEntity te, MechanismTileEntity.SidedAxleHandler handler, IAxisHandler masterIn, byte key, double rotRatioIn, double lastRadius);

	/**
	 * Used to get the item that should be dropped when broken
	 * @param mat The material of this mechanism
	 * @return The dropped itemstack
	 */
	@Nonnull
	public ItemStack getDrop(EnumGearType mat);

	/**
	 * Used to get the bounding box for breaking and collision
	 * @param side The side this mechanism is on. If null, this is in the axle slot (center)
	 * @param axis If side is null (axle slot), this is the orientation of this mechanism. If side is not null, this should be ignored, and may be null
	 * @return The bounding box of this mechanism
	 */
	public AxisAlignedBB getBoundingBox(@Nullable EnumFacing side, @Nullable EnumFacing.Axis axis);

	@SideOnly(Side.CLIENT)
	public void doRender(MechanismTileEntity te, float partialTicks, EnumGearType mat, @Nullable EnumFacing side, @Nullable EnumFacing.Axis axis);
}
