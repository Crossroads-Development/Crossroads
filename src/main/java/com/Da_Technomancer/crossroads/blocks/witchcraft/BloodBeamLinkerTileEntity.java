package com.Da_Technomancer.crossroads.blocks.witchcraft;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.api.Capabilities;
import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.api.beams.*;
import com.Da_Technomancer.crossroads.api.templates.InventoryTE;
import com.Da_Technomancer.crossroads.api.witchcraft.IPerishable;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.crossroads.gui.container.BloodBeamLinkerContainer;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.witchcraft.BloodSample;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public class BloodBeamLinkerTileEntity extends InventoryTE{

	public static final BlockEntityType<BloodBeamLinkerTileEntity> TYPE = CRTileEntity.createType(BloodBeamLinkerTileEntity::new, CRBlocks.bloodBeamLinker);

	//Prevents accepting 2 beams in one cycle
	private long lastActiveBeamCycle = -1;

	public BloodBeamLinkerTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state, 1);
	}

	public float getRedstone(){
		//Return average lifetime remaining for the contents, in seconds
		return AbstractNutrientEnvironmentTileEntity.getAverageLifetime(level, inventory) / 20F;
	}

	@Override
	public void serverTick(){
		super.serverTick();

		if(!inventory[0].isEmpty()){
			setChanged();//Update comparator signals as the contents age
		}
	}

	@Override
	public void setChanged(){
		super.setChanged();
		if(level != null && !level.isClientSide){
			//Update the blockstate in the world
			BlockState state = getBlockState();
			if(state.getBlock() == CRBlocks.bloodBeamLinker){
				int contents = state.getValue(CRProperties.CONTENTS);
				int newContents = 0;
				if(!inventory[0].isEmpty()){
					newContents = IPerishable.isSpoiled(inventory[0], level) ? 1 : 2;
				}
				if(newContents != contents){
					//No block update
					level.setBlock(worldPosition, state.setValue(CRProperties.CONTENTS, newContents), MiscUtil.BLOCK_FLAGS_VISUAL);
				}
			}
		}
	}

	@Override
	public void setRemoved(){
		super.setRemoved();
		itemOpt.invalidate();
		beamOpt.invalidate();
	}

	private final LazyOptional<IItemHandler> itemOpt = LazyOptional.of(ItemHandler::new);
	private final LazyOptional<IBeamHandler> beamOpt = LazyOptional.of(BeamHandler::new);

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing){
		if(capability == Capabilities.BEAM_CAPABILITY){
			return (LazyOptional<T>) beamOpt;
		}
		if(capability == ForgeCapabilities.ITEM_HANDLER){
			return (LazyOptional<T>) itemOpt;
		}

		return super.getCapability(capability, facing);
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction){
		return true;//Output slots
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack){
		if(!super.canPlaceItem(index, stack)){
			return false;
		}
		return stack.getItem() == CRItems.bloodSample;
	}

	@Override
	public Component getDisplayName(){
		return Component.translatable("container.crossroads.blood_beam_linker");
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player playerEntity){
		return new BloodBeamLinkerContainer(id, playerInventory, createContainerBuf());
	}

	private class BeamHandler implements IBeamHandler{

		@Override
		public void setBeam(@Nonnull BeamUnit beamIn, @Nonnull BeamHit beamHitIn){
			if(beamIn.isEmpty()){
				return;
			}
			long beamCycle = level.getGameTime() / BeamUtil.BEAM_TIME;
			UUID srcUUID;
			if(beamCycle != lastActiveBeamCycle && inventory[0].getItem() instanceof BloodSample && (srcUUID = BloodSample.getEntityTypeData(inventory[0]).getOriginatingUUID()) != null){
				//Only allow one beam effect per cycle
				lastActiveBeamCycle = beamCycle;
				ServerLevel serverLevel = (ServerLevel) level;
				Entity target = serverLevel.getEntity(srcUUID);
				//Target null when missing/dead, in another dimension, or in unloaded chunks
				if(target != null){
					//10% of applying penalty while the sample is spoiled
					boolean spoiledBeam = IPerishable.isSpoiled(inventory[0], level) && level.random.nextInt(10) < 1;
					//Base the alignment/effect on the full incoming beam, but only transmit the power-limited version
					EnumBeamAlignments align;
					boolean voidBeam = beamIn.getVoid() != 0;;
					int beamPower = Math.min(Math.min(beamIn.getPower(), CRConfig.maximumBloodLinkerPower.get()), BeamUtil.MAX_EFFECT_POWER);
					BeamUnit toTransmit;
					if(spoiledBeam){
						//When spoiled, don't allow absorbing the beam unit
						toTransmit = BeamUnit.EMPTY;
						beamPower = beamPower * 10;//Penalty beam is more powerful than normal beam, and can go over the limit
						if(beamIn.getVoid() == beamIn.getPower()){
							//For pure void beam, invert it to be a random non-void alignment
							align = EnumBeamAlignments.values()[level.random.nextInt(EnumBeamAlignments.values().length - 2)];
							voidBeam = false;
						}else{
							//Same beam alignment, but invert void/not-void
							align = beamIn.getAlignment();
							voidBeam = !voidBeam;
						}
					}else{
						align = beamIn.getAlignment();
						toTransmit = new BeamUnit(MiscUtil.withdrawExact(beamIn.getValues(), beamPower));
					}
					BeamHit hit = new BeamHit(serverLevel, target.blockPosition(), beamHitIn.getDirection(), null, toTransmit, beamHitIn.getRay(), target.position());
					align.getEffect().doBeamEffect(align, voidBeam, beamPower, hit);
				}
			}
		}

		@Override
		public void setBeam(@Nonnull BeamUnit mag){
			setBeam(mag, new BeamHit((ServerLevel) level, worldPosition, Direction.UP, null, mag));
		}
	}
}
