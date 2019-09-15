package com.Da_Technomancer.crossroads.tileentities.beams;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.beams.BeamManager;
import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.API.beams.IBeamHandler;
import com.Da_Technomancer.crossroads.API.packets.IIntReceiver;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendIntToClient;
import com.Da_Technomancer.crossroads.API.redstone.IAdvancedRedstoneHandler;
import com.Da_Technomancer.crossroads.API.templates.BeamRenderTEBase;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.items.CrossroadsItems;
import com.Da_Technomancer.crossroads.items.itemSets.OreSetup;
import com.Da_Technomancer.essentials.blocks.EssentialsProperties;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.AxisDirection;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

public class LensFrameTileEntity extends BeamRenderTEBase implements IIntReceiver{

	private int packetNeg;
	private int packetPos;
	private int contents = 0;
	private Direction.Axis axis = null;
	private BeamUnit prevMag = null;

	private Direction.Axis getAxis(){
		if(axis == null){
			BlockState state = world.getBlockState(pos);
			if(state.getBlock() != CrossroadsBlocks.lensFrame){
				return Direction.Axis.X;
			}
			axis = state.get(EssentialsProperties.AXIS);
		}

		return axis;
	}

	public ItemStack getItem(){
		switch(contents){
			case 1:
				return new ItemStack(OreSetup.gemRuby, 1);
			case 2:
				return new ItemStack(Items.EMERALD, 1);
			case 3:
				return new ItemStack(Items.DIAMOND, 1);
			case 4:
				return new ItemStack(CrossroadsItems.pureQuartz, 1);
			case 5:
				return new ItemStack(CrossroadsItems.luminescentQuartz, 1);
			case 6:
				return new ItemStack(OreSetup.voidCrystal, 1);
			default:
				return ItemStack.EMPTY;
		}
	}

	public int getIDFromItem(ItemStack stack){
		if(MiscUtil.hasOreDict(stack, "gemRuby")){
			return 1;
		}
		if(MiscUtil.hasOreDict(stack, "gemEmerald")){
			return 2;
		}
		if(MiscUtil.hasOreDict(stack, "gemDiamond")){
			return 3;
		}
		if(stack.getItem() == CrossroadsItems.pureQuartz){
			return 4;
		}
		if(stack.getItem() == CrossroadsItems.luminescentQuartz){
			return 5;
		}
		if(stack.getItem() == OreSetup.voidCrystal){
			return 6;
		}
		return 0;
	}

	public void setContents(int id){
		contents = id;
		markDirty();
		ModPackets.network.sendToAllAround(new SendIntToClient((byte) 2, contents, pos), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
	}

	public int getContents(){
		return contents;
	}

	public void refresh(){
		if(beamer[1] != null){
			beamer[1].emit(null, world);
			refreshBeam(true);
		}
		if(beamer[0] != null){
			beamer[0].emit(null, world);
			refreshBeam(false);
		}
		axis = null;
		ModPackets.network.sendToAllAround(new SendIntToClient((byte) 3, 0, pos), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
	}

	private void refreshBeam(boolean positive){
		int index = positive ? 1 : 0;
		int packet = beamer[index].genPacket();
		if(positive){
			packetPos = packet;
		}else{
			packetNeg = packet;
		}
		ModPackets.network.sendToAllAround(new SendIntToClient((byte) index, packet, pos), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
		if(beamer[index].getLastSent() != null){
			prevMag = beamer[index].getLastSent();
		}
	}

	@Override
	@Nullable
	public BeamUnit[] getLastSent(){
		return new BeamUnit[] {prevMag};
	}

	private double lastRedstone;

	public double getRedstone(){
		return lastRedstone;
	}

	@Override
	public int[] getRenderedBeams(){
		int[] out = new int[6];
		out[Direction.getFacingFromAxis(AxisDirection.POSITIVE, getAxis()).getIndex()] = packetPos;
		out[Direction.getFacingFromAxis(AxisDirection.NEGATIVE, getAxis()).getIndex()] = packetNeg;
		return out;
	}

	private BeamManager[] beamer = new BeamManager[2];//0: neg; 1: pos

	@Override
	public void receiveInt(byte identifier, int message, ServerPlayerEntity player){
		switch(identifier){
			case 0:
				packetNeg = message;
				break;
			case 1:
				packetPos = message;
				break;
			case 2:
				contents = message;
				break;
			case 3:
				axis = null;
				break;

		}
	}

	@Override
	public CompoundNBT getUpdateTag(){
		CompoundNBT nbt = super.getUpdateTag();
		nbt.setInteger("beam_neg", packetNeg);
		nbt.setInteger("beam_pos", packetPos);
		nbt.setInteger("contents", contents);
		return nbt;
	}

	@Override
	public CompoundNBT writeToNBT(CompoundNBT nbt){
		super.writeToNBT(nbt);
		nbt.setInteger("beam_neg", packetNeg);
		nbt.setInteger("beam_pos", packetPos);
		nbt.setDouble("reds", lastRedstone);
		nbt.setInteger("contents", contents);
		return nbt;
	}

	@Override
	public void readFromNBT(CompoundNBT nbt){
		super.readFromNBT(nbt);
		packetPos = nbt.getInteger("beam_pos");
		packetNeg = nbt.getInteger("beam_neg");
		lastRedstone = nbt.getDouble("reds");
		contents = nbt.getInteger("contents");
	}

	private final IBeamHandler magicHandler = new BeamHandler(AxisDirection.NEGATIVE);
	private final IBeamHandler magicHandlerNeg = new BeamHandler(AxisDirection.POSITIVE);
	private final IItemHandler lensHandler = new LensHandler();
	private final RedstoneHandler redstoneHandler = new RedstoneHandler();

	@Override
	public boolean hasCapability(Capability<?> cap, Direction side){
		if(cap == Capabilities.BEAM_CAPABILITY && (side == null || getAxis() == side.getAxis())){
			return true;
		}
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || cap == Capabilities.ADVANCED_REDSTONE_CAPABILITY){
			return true;
		}


		return super.hasCapability(cap, side);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, Direction side){
		if(cap == Capabilities.BEAM_CAPABILITY && (side == null || getAxis() == side.getAxis())){
			return side == null || side.getAxisDirection() == AxisDirection.POSITIVE ? (T) magicHandler : (T) magicHandlerNeg;
		}
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			return (T) lensHandler;
		}
		if(cap == Capabilities.ADVANCED_REDSTONE_CAPABILITY){
			return (T) redstoneHandler;
		}

		return super.getCapability(cap, side);
	}

	private class RedstoneHandler implements IAdvancedRedstoneHandler{

		@Override
		public double getOutput(boolean read){
			return read ? lastRedstone : 0;
		}
	}

	private class BeamHandler implements IBeamHandler{

		private final AxisDirection dir;

		private BeamHandler(AxisDirection dir){
			this.dir = dir;
		}

		@Override
		public void setMagic(BeamUnit mag){
			if(beamer[0] == null || beamer[1] == null){
				beamer[0] = new BeamManager(Direction.getFacingFromAxis(AxisDirection.NEGATIVE, getAxis()), pos);
				beamer[1] = new BeamManager(Direction.getFacingFromAxis(AxisDirection.POSITIVE, getAxis()), pos);
			}

			if(mag != null && mag.getVoid() != 0 && contents != 0 && contents != 6){
				setContents(0);
				if(beamer[dir == AxisDirection.POSITIVE ? 1 : 0].emit(mag, world)){
					refreshBeam(dir == AxisDirection.POSITIVE);
				}
				lastRedstone = Math.max(beamer[0].getLastSent() == null ? 0 : ((double) beamer[0].getLastSent().getPower()), beamer[1].getLastSent() == null ? 0 : ((double) beamer[1].getLastSent().getPower()));
				markDirty();
				return;
			}

			switch(contents){
				case 0:
					if(beamer[dir == AxisDirection.POSITIVE ? 1 : 0].emit(mag, world)){
						refreshBeam(dir == AxisDirection.POSITIVE);
					}
					break;
				case 1:
					if(beamer[dir == AxisDirection.POSITIVE ? 1 : 0].emit(mag == null || mag.getEnergy() == 0 ? null : new BeamUnit(mag.getEnergy(), 0, 0, 0), world)){
						refreshBeam(dir == AxisDirection.POSITIVE);
					}
					break;
				case 2:
					if(beamer[dir == AxisDirection.POSITIVE ? 1 : 0].emit(mag == null || mag.getPotential() == 0 ? null : new BeamUnit(0, mag.getPotential(), 0, 0), world)){
						refreshBeam(dir == AxisDirection.POSITIVE);
					}
					break;
				case 3:
					if(beamer[dir == AxisDirection.POSITIVE ? 1 : 0].emit(mag == null || mag.getStability() == 0 ? null : new BeamUnit(0, 0, mag.getStability(), 0), world)){
						refreshBeam(dir == AxisDirection.POSITIVE);
					}
					break;
				case 4:
					if(EnumBeamAlignments.getAlignment(mag) == EnumBeamAlignments.LIGHT){
						setContents(5);
					}
					if(beamer[dir == AxisDirection.POSITIVE ? 1 : 0].emit(mag, world)){
						refreshBeam(dir == AxisDirection.POSITIVE);
					}
					break;
				case 5:
					if(beamer[dir == AxisDirection.POSITIVE ? 1 : 0].emit(mag, world)){
						refreshBeam(dir == AxisDirection.POSITIVE);
					}
					break;
				case 6:
					if(beamer[dir == AxisDirection.POSITIVE ? 1 : 0].emit(mag == null ? null : new BeamUnit(0, 0, 0, mag.getPower()), world)){
						refreshBeam(dir == AxisDirection.POSITIVE);
					}
					break;
			}

			lastRedstone = Math.max(beamer[0].getLastSent() == null ? 0 : beamer[0].getLastSent().getPower(), beamer[1].getLastSent() == null ? 0 : beamer[1].getLastSent().getPower());
			markDirty();
		}
	}

	private class LensHandler implements IItemHandler{

		@Override
		public int getSlots(){
			return 1;
		}

		@Override
		public ItemStack getStackInSlot(int slot){
			return slot == 0 ? getItem() : ItemStack.EMPTY;
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate){
			if(slot != 0 || contents != 0 || getIDFromItem(stack) == 0){
				return stack;
			}

			if(!simulate){
				setContents(getIDFromItem(stack));
			}

			return stack.getCount() - 1 <= 0 ? ItemStack.EMPTY : new ItemStack(stack.getItem(), stack.getCount() - 1);
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate){
			if(slot != 0 || amount < 1 || contents == 0){
				return ItemStack.EMPTY;
			}
			ItemStack toOutput = getItem();
			if(!simulate){
				setContents(0);
			}
			return toOutput;
		}

		@Override
		public int getSlotLimit(int slot){
			return slot == 0 ? 1 : 0;
		}
	}
} 
