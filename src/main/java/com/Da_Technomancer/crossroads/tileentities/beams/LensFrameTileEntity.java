package com.Da_Technomancer.crossroads.tileentities.beams;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.beams.BeamManager;
import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.API.beams.IBeamHandler;
import com.Da_Technomancer.crossroads.API.packets.CrossroadsPackets;
import com.Da_Technomancer.crossroads.API.packets.IIntReceiver;
import com.Da_Technomancer.crossroads.API.packets.SendIntToClient;
import com.Da_Technomancer.crossroads.API.templates.IBeamRenderTE;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.crafting.CRItemTags;
import com.Da_Technomancer.crossroads.items.itemSets.OreSetup;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.AxisDirection;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@ObjectHolder(Crossroads.MODID)
public class LensFrameTileEntity extends TileEntity implements IBeamRenderTE, IIntReceiver{

	@ObjectHolder("lens_frame")
	private static TileEntityType<LensFrameTileEntity> type = null;

	private int packetNeg;
	private int packetPos;
	private int contents = 0;
	private Direction.Axis axis = null;
	private BeamUnit prevMag = BeamUnit.EMPTY;
	private int lastRedstone;

	public LensFrameTileEntity(){
		super(type);
	}

	private Direction.Axis getAxis(){
		if(axis == null){
			BlockState state = world.getBlockState(pos);
			if(state.getBlock() != CRBlocks.lensFrame){
				return Direction.Axis.X;
			}
			axis = state.get(ESProperties.AXIS);
		}

		return axis;
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox(){
		return INFINITE_EXTENT_AABB;
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
				return new ItemStack(CRItems.pureQuartz, 1);
			case 5:
				return new ItemStack(CRItems.brightQuartz, 1);
			case 6:
				return new ItemStack(OreSetup.voidCrystal, 1);
			default:
				return ItemStack.EMPTY;
		}
	}

	public int getIDFromItem(ItemStack stack){
		if(CRItemTags.GEMS_RUBY.contains(stack.getItem())){
			return 1;
		}
		if(Tags.Items.GEMS_EMERALD.contains(stack.getItem())){
			return 2;
		}
		if(Tags.Items.GEMS_DIAMOND.contains(stack.getItem())){
			return 3;
		}
		if(stack.getItem() == CRItems.pureQuartz){
			return 4;
		}
		if(stack.getItem() == CRItems.brightQuartz){
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
		CrossroadsPackets.sendPacketAround(world, pos, new SendIntToClient((byte) 2, contents, pos));
	}

	public int getContents(){
		return contents;
	}

	public void refresh(){
		if(beamer[1] != null){
			beamer[1].emit(BeamUnit.EMPTY, world);
			refreshBeam(true);
		}
		if(beamer[0] != null){
			beamer[0].emit(BeamUnit.EMPTY, world);
			refreshBeam(false);
		}
		axis = null;
		magicOpt.invalidate();
		magicOptNeg.invalidate();
		CrossroadsPackets.sendPacketAround(world, pos, new SendIntToClient((byte) 3, 0, pos));
	}

	private void refreshBeam(boolean positive){
		int index = positive ? 1 : 0;
		int packet = beamer[index].genPacket();
		if(positive){
			packetPos = packet;
		}else{
			packetNeg = packet;
		}
		CrossroadsPackets.sendPacketAround(world, pos, new SendIntToClient((byte) index, packet, pos));
		if(!beamer[index].getLastSent().isEmpty()){
			prevMag = beamer[index].getLastSent();
		}
	}

	@Override
	@Nullable
	public BeamUnit[] getLastSent(){
		return new BeamUnit[] {prevMag};
	}

	public int getRedstone(){
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
		nbt.putInt("beam_neg", packetNeg);
		nbt.putInt("beam_pos", packetPos);
		nbt.putInt("contents", contents);
		return nbt;
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		nbt.putInt("beam_neg", packetNeg);
		nbt.putInt("beam_pos", packetPos);
		nbt.putInt("reds", lastRedstone);
		nbt.putInt("contents", contents);
		return nbt;
	}

	@Override
	public void read(CompoundNBT nbt){
		super.read(nbt);
		packetPos = nbt.getInt("beam_pos");
		packetNeg = nbt.getInt("beam_neg");
		lastRedstone = nbt.getInt("reds");
		contents = nbt.getInt("contents");
	}

	@Override
	public void remove(){
		super.remove();
		magicOpt.invalidate();
		magicOptNeg.invalidate();
		lensOpt.invalidate();
	}

	private final LazyOptional<IBeamHandler> magicOpt = LazyOptional.of(() -> new BeamHandler(AxisDirection.NEGATIVE));
	private final LazyOptional<IBeamHandler> magicOptNeg = LazyOptional.of(() -> new BeamHandler(AxisDirection.POSITIVE));
	private final LazyOptional<IItemHandler> lensOpt = LazyOptional.of(LensHandler::new);

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side){
		if(cap == Capabilities.BEAM_CAPABILITY && (side == null || getAxis() == side.getAxis())){
			return side == null || side.getAxisDirection() == AxisDirection.POSITIVE ? (LazyOptional<T>) magicOpt : (LazyOptional<T>) magicOptNeg;
		}
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			return (LazyOptional<T>) lensOpt;
		}

		return super.getCapability(cap, side);
	}

	private class BeamHandler implements IBeamHandler{

		private final AxisDirection dir;

		private BeamHandler(AxisDirection dir){
			this.dir = dir;
		}

		@Override
		public void setBeam(@Nonnull BeamUnit mag){
			if(beamer[0] == null || beamer[1] == null){
				beamer[0] = new BeamManager(Direction.getFacingFromAxis(AxisDirection.NEGATIVE, getAxis()), pos);
				beamer[1] = new BeamManager(Direction.getFacingFromAxis(AxisDirection.POSITIVE, getAxis()), pos);
			}

			if(mag.getVoid() != 0 && contents != 0 && contents != 6){
				setContents(0);
				if(beamer[dir == AxisDirection.POSITIVE ? 1 : 0].emit(mag, world)){
					refreshBeam(dir == AxisDirection.POSITIVE);
				}
				lastRedstone = Math.max(beamer[0].getLastSent().getPower(), beamer[1].getLastSent().getPower());
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
					if(beamer[dir == AxisDirection.POSITIVE ? 1 : 0].emit(new BeamUnit(mag.getEnergy(), 0, 0, 0), world)){
						refreshBeam(dir == AxisDirection.POSITIVE);
					}
					break;
				case 2:
					if(beamer[dir == AxisDirection.POSITIVE ? 1 : 0].emit(new BeamUnit(0, mag.getPotential(), 0, 0), world)){
						refreshBeam(dir == AxisDirection.POSITIVE);
					}
					break;
				case 3:
					if(beamer[dir == AxisDirection.POSITIVE ? 1 : 0].emit(new BeamUnit(0, 0, mag.getStability(), 0), world)){
						refreshBeam(dir == AxisDirection.POSITIVE);
					}
					break;
				case 4:
					if(EnumBeamAlignments.getAlignment(mag) == EnumBeamAlignments.LIGHT){
						setContents(5);
					}
					//No break
				case 5:
					if(beamer[dir == AxisDirection.POSITIVE ? 1 : 0].emit(mag, world)){
						refreshBeam(dir == AxisDirection.POSITIVE);
					}
					break;
				case 6:
					if(beamer[dir == AxisDirection.POSITIVE ? 1 : 0].emit(new BeamUnit(0, 0, 0, mag.getPower()), world)){
						refreshBeam(dir == AxisDirection.POSITIVE);
					}
					break;
			}

			lastRedstone = Math.max(beamer[0].getLastSent().getPower(), beamer[1].getLastSent().getPower());
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
			if(slot != 0 || contents != 0 || !isItemValid(0, stack)){
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

		@Override
		public boolean isItemValid(int slot, @Nonnull ItemStack stack){
			return getIDFromItem(stack) != 0;
		}
	}
} 
