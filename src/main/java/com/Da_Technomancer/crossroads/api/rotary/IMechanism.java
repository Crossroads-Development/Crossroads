package com.Da_Technomancer.crossroads.api.rotary;

import com.Da_Technomancer.crossroads.blocks.rotary.mechanisms.MechanismTileEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.BlockCapability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;

public interface IMechanism<T extends IMechanismProperty>{

	/**
	 * Called when the redstone signal received changes
	 * @param prevValue The previous redstone value
	 * @param newValue The new redstone value
	 * @param mat The material of this mechanism
	 * @param side The side this mechanism is on. If null, this is in the axle slot (center)
	 * @param axis If side is null (axle slot), this is the orientation of this mechanism. If side is not null, this should be ignored, and may be null
	 * @param energy The energy of this mechanism
	 * @param speed The speed of this mechanism
	 * @param te The containing TileEntity
	 */
	default void onRedstoneChange(double prevValue, double newValue, IMechanismProperty mat, @Nullable Direction side, @Nullable Direction.Axis axis, double energy, double speed, MechanismTileEntity te){

	}

	/**
	 * Gets the Circuit Reader (or comparator) read output signal for this mechanism. Will only be called if this mechanism is in the axle slot.
	 * @param mat The material of this mechanism
	 * @param axis The axle axis. Will not be null
	 * @param energy The energy of this mechanism
	 * @param speed The speed of this mechanism
	 * @param te The calling TE
	 * @return The value a Circuit Reader should read. If a comparator is reading this, it will be rounded and bounded to [0, 15].
	 */
	default double getCircuitSignal(IMechanismProperty mat, @Nonnull Direction.Axis axis, double energy, double speed, MechanismTileEntity te){
		return 0;
	}

	/**
	 * Returns the moment of inertia of this component when added
	 * @param mat The material of this mechanism
	 * @param side The side this mechanism is on. If null, this is in the axle slot (center)
	 * @param axis If side is null (axle slot), this is the orientation of this mechanism. If side is not null, this should be ignored, and may be null
	 * @return The moment of inertia
	 */
	double getInertia(IMechanismProperty mat, @Nullable Direction side, @Nullable Direction.Axis axis);

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
	boolean hasCap(BlockCapability<?, ?> cap, Direction capSide, IMechanismProperty mat, @Nullable Direction side, @Nullable Direction.Axis axis, MechanismTileEntity te);

	/**
	 * Called when performing a rotary propagation. The mechanism is responsible for propagating
	 * @param mat The material of this mechanism
	 * @param side The side this mechanism is on. If null, this is in the axle slot (center)
	 * @param axis If side is null (axle slot), this is the orientation of this mechanism. If side is not null, this should be ignored, and may be null
	 * @param te The containing TileEntity
	 * @param handler The calling SidedAxleHandler.
	 * @param masterIn The source Master Axis
	 * @param key The propagation key
	 * @param rotRatioIn The previous rotation ratio
	 * @param lastRadius The previous radius
	 */
	void propagate(IMechanismProperty mat, @Nullable Direction side, @Nullable Direction.Axis axis, MechanismTileEntity te, IMechanismAxleHandler handler, IAxisHandler masterIn, byte key, double rotRatioIn, double lastRadius);

	/**
	 * Called when performing ICogHandler connection
	 * @param mat The material of this mechanism
	 * @param side The side this mechanism is on. If null, this is in the axle slot (center)
	 * @param axis If side is null (axle slot), this is the orientation of this mechanism. If side is not null, this should be ignored, and may be null
	 * @param te The containing TileEntity
	 * @param handler The associated SidedAxleHandler for this location
	 * @param masterIn
	 * @param key
	 * @param rotationRatioIn
	 * @param lastRadius
	 * @param cogOrient The orientation of the cogs in the plane (as opposed to the alignment of the plane, which is the capability side)
	 * @param renderOffset Whether to render this block at an offset angle. This value should ONLY be used for rendering. Invert when connecting to other blocks before passing to the IAxleHandler (don't invert when connecting axially)
	 */
	default void connect(IMechanismProperty mat, @Nullable Direction side, @Nullable Direction.Axis axis, MechanismTileEntity te, IMechanismAxleHandler handler, @Nonnull IAxisHandler masterIn, byte key, double rotationRatioIn, double lastRadius, Direction cogOrient, boolean renderOffset){
		handler.propagate(masterIn, key, rotationRatioIn, lastRadius, !renderOffset);//Go through the handler - don't skip directly to IMechanism::propagate
	}

	/**
	 * @deprecated Override the version with more parameters instead
	 */
	@Nonnull
	@Deprecated(forRemoval = true)
	default ItemStack getDrop(IMechanismProperty mat){
		return ItemStack.EMPTY;
	}

	/**
	 * Used to get the item that should be dropped when broken
	 * @param mat The material of this mechanism
	 * @param side The side this mechanism is on. If null, this is in the axle slot (center)
	 * @param axis If side is null (axle slot), this is the orientation of this mechanism. If side is not null, this is the orientation of the axle, if there is one
	 * @param te The tile entity this is part of
	 * @return The dropped itemstack
	 */
	@Nonnull
	default ItemStack getDrop(IMechanismProperty mat, @Nullable Direction side, @Nullable Direction.Axis axis, @Nullable MechanismTileEntity te){
		return getDrop(mat);
	}

	/**
	 * Used to get the bounding box for breaking and collision
	 * @param side The side this mechanism is on. If null, this is in the axle slot (center)
	 * @param axis If side is null (axle slot), this is the orientation of this mechanism. If side is not null, this is the orientation of the axle, if there is one
	 * @return The bounding box of this mechanism
	 */
	VoxelShape getBoundingBox(@Nullable Direction side, @Nullable Direction.Axis axis);

	/**
	 * Renders this mechanism as part of the tile entity
	 * Implementers do not need to restore the matrix stack to original condition
	 * @param te The tile entity this is part of
	 * @param matrix The matrix, centered but not oriented
	 * @param buffer A buffer
	 * @param combinedLight World light
	 * @param partialTicks partial time, [0, 1]
	 * @param mat Gear material of this mechanism
	 * @param side The side this mechanism is on, null if in axle slot
	 * @param axis The axle orientation, if there is one
	 */
	@OnlyIn(Dist.CLIENT)
	void doRender(MechanismTileEntity te, PoseStack matrix, MultiBufferSource buffer, int combinedLight, float partialTicks, IMechanismProperty mat, @Nullable Direction side, @Nullable Direction.Axis axis);

	/**
	 * @param nbt NBT with this saved.
	 * @return A new property, read from NBT
	 */
	T readProperty(CompoundTag nbt);

	/**
	 * @return Whether this mechanism should break if on a side without a supporting block or connected axle
	 */
	default boolean requiresSupport(){
		return true;
	}

	/**
	 * Only called on axle-slot mechanisms. If true, updates circuit readers and comparators reading the signal from this block.
	 * @return Whether to update circuit reader and comparator values
	 */
	default boolean shouldUpdateCircuitReaders(IMechanismProperty mat, Direction.Axis axis, double energy, double speed, MechanismTileEntity te){
		return false;
	}

	/**
	 * @param chat Chat list to append to
	 * @param player Player taking information
	 * @param mat Property for this mechanism
	 * @param side The side this mechanism is on. If null, this is in the axle slot (center)
	 * @param axis If side is null (axle slot), this is the orientation of this mechanism. If side is not null, this should be ignored, and may be null
	 * @param te The containing TileEntity
	 * @param handler The associated SidedAxleHandler.
	 */
	default void addInfo(ArrayList<Component> chat, Player player, IMechanismProperty mat, @Nullable Direction side, @Nullable Direction.Axis axis, MechanismTileEntity te, IMechanismAxleHandler handler){
		RotaryUtil.addRotaryInfo(chat, handler, false, player);
	}

	default void onRemoved(IMechanismProperty mat, @Nullable Direction side, @Nullable Direction.Axis axis, MechanismTileEntity te){

	}
}
