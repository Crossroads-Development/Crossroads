package com.Da_Technomancer.crossroads.tileentities.magic;

import java.awt.Color;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Triple;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.IAdvancedRedstoneHandler;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.enums.MagicElements;
import com.Da_Technomancer.crossroads.API.magic.BeamManager;
import com.Da_Technomancer.crossroads.API.magic.BeamRenderTE;
import com.Da_Technomancer.crossroads.API.magic.IMagicHandler;
import com.Da_Technomancer.crossroads.API.magic.MagicUnit;
import com.Da_Technomancer.crossroads.API.packets.IIntReceiver;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendIntToClient;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.items.itemSets.OreSetUp;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class LensHolderTileEntity extends BeamRenderTE implements IIntReceiver{

	private Triple<Color, Integer, Integer> trip;
	private Triple<Color, Integer, Integer> tripUp;

	@Override
	@Nullable
	public MagicUnit[] getLastFullSent(){
		return new MagicUnit[] {beamer == null ? null : beamer.getLastFullSent(), beamerUp == null ? null : beamerUp.getLastFullSent()};
	}

	public double getRedstone(){
		return lastRedstone;
	}
	private double lastRedstone;

	@SuppressWarnings("unchecked")
	@Override
	public Triple<Color, Integer, Integer>[] getBeam(){
		if(world.getBlockState(pos).getBlock() != ModBlocks.lensHolder){
			return null;
		}
		Triple<Color, Integer, Integer>[] out = new Triple[6];
		out[EnumFacing.getFacingFromAxis(AxisDirection.POSITIVE, world.getBlockState(pos).getValue(Properties.ORIENT) ? Axis.X : Axis.Z).getIndex()] = tripUp;
		out[EnumFacing.getFacingFromAxis(AxisDirection.NEGATIVE, world.getBlockState(pos).getValue(Properties.ORIENT) ? Axis.X : Axis.Z).getIndex()] = trip;
		return out;
	}

	@Override
	public void refresh(){
		if(beamer != null){
			beamer.emit(null);
		}
		if(beamerUp != null){
			beamerUp.emit(null);
		}
	}

	private BeamManager beamer;
	private BeamManager beamerUp;

	@Override
	public void receiveInt(int identifier, int message, EntityPlayerMP player){
		if(identifier == 0){
			trip = BeamManager.getTriple(message);
		}else if(identifier == 1){
			tripUp = BeamManager.getTriple(message);
		}
	}

	private int memTrip;
	private int memTripUp;

	@Override
	public NBTTagCompound getUpdateTag(){
		NBTTagCompound nbt = super.getUpdateTag();
		nbt.setInteger("beam", memTrip);
		nbt.setInteger("beamUp", memTripUp);
		return nbt;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setInteger("memTrip", beamer == null ? 0 : beamer.getPacket());
		nbt.setInteger("memTripUp", beamerUp == null ? 0 : beamerUp.getPacket());
		nbt.setDouble("reds", lastRedstone);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		memTrip = nbt.getInteger("memTrip");
		memTripUp = nbt.getInteger("memTripUp");
		lastRedstone = nbt.getDouble("reds");
		if(nbt.hasKey("beam")){
			trip = BeamManager.getTriple(nbt.getInteger("beam"));
		}
		if(nbt.hasKey("beamUp")){
			tripUp = BeamManager.getTriple(nbt.getInteger("beamUp"));
		}
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState){
		return ModBlocks.lensHolder != newState.getBlock();
	}

	private final IMagicHandler magicHandler = new MagicHandler(AxisDirection.NEGATIVE);
	private final IMagicHandler magicHandlerNeg = new MagicHandler(AxisDirection.POSITIVE);
	private final IItemHandler lensHandler = new LensHandler();
	private final RedstoneHandler redstoneHandler = new RedstoneHandler();

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		if(cap == Capabilities.MAGIC_HANDLER_CAPABILITY && (side == null || (world.getBlockState(pos).getValue(Properties.ORIENT) ? side.getAxis() == Axis.X : side.getAxis() == Axis.Z))){
			return true;
		}
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || cap == Capabilities.ADVANCED_REDSTONE_HANDLER_CAPABILITY){
			return true;
		}


		return super.hasCapability(cap, side);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(cap == Capabilities.MAGIC_HANDLER_CAPABILITY && (side == null || (world.getBlockState(pos).getValue(Properties.ORIENT) ? side.getAxis() == Axis.X : side.getAxis() == Axis.Z))){
			return side == null || side.getAxisDirection() == AxisDirection.POSITIVE ? (T) magicHandler : (T) magicHandlerNeg;
		}
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			return (T) lensHandler;
		}
		if(cap == Capabilities.ADVANCED_REDSTONE_HANDLER_CAPABILITY){
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

	private class MagicHandler implements IMagicHandler{

		private final AxisDirection dir;

		private MagicHandler(AxisDirection dir){
			this.dir = dir;
		}

		@Override
		public void setMagic(MagicUnit mag){
			if(beamer == null || beamerUp == null){
				beamer = new BeamManager(EnumFacing.getFacingFromAxis(AxisDirection.NEGATIVE, world.getBlockState(pos).getValue(Properties.ORIENT) ? Axis.X : Axis.Z), pos, world);
				beamerUp = new BeamManager(EnumFacing.getFacingFromAxis(AxisDirection.POSITIVE, world.getBlockState(pos).getValue(Properties.ORIENT) ? Axis.X : Axis.Z), pos, world);
			}

			if(mag != null && mag.getVoid() != 0 && world.getBlockState(pos).getValue(Properties.TEXTURE_7) != 0){
				world.setBlockState(pos, world.getBlockState(pos).withProperty(Properties.TEXTURE_7, 0));
				(dir == AxisDirection.POSITIVE ? beamerUp : beamer).emit(mag);
				double holder = Math.max(beamer.getLastSent() == null ? 0 : ((double) beamer.getLastSent().getPower()), beamerUp.getLastSent() == null ? 0 : ((double) beamerUp.getLastSent().getPower()));
				lastRedstone = holder;
				markDirty();
				return;
			}

			switch(world.getBlockState(pos).getValue(Properties.TEXTURE_7)){
				case 0:
					if((dir == AxisDirection.POSITIVE ? beamerUp : beamer).emit(mag) || world.getTotalWorldTime() % (IMagicHandler.BEAM_TIME * 20) == 0){
						ModPackets.network.sendToAllAround(new SendIntToClient(dir == AxisDirection.POSITIVE ? 1 : 0, (dir == AxisDirection.POSITIVE ? beamerUp : beamer).getPacket(), pos), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
					}
					break;
				case 1:
					if((dir == AxisDirection.POSITIVE ? beamerUp : beamer).emit(mag == null || mag.getEnergy() == 0 ? null : new MagicUnit(mag.getEnergy(), 0, 0, 0)) || world.getTotalWorldTime() % (IMagicHandler.BEAM_TIME * 20) == 0){
						ModPackets.network.sendToAllAround(new SendIntToClient(dir == AxisDirection.POSITIVE ? 1 : 0, (dir == AxisDirection.POSITIVE ? beamerUp : beamer).getPacket(), pos), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
					}
					break;
				case 2:
					if((dir == AxisDirection.POSITIVE ? beamerUp : beamer).emit(mag == null || mag.getPotential() == 0 ? null : new MagicUnit(0, mag.getPotential(), 0, 0)) || world.getTotalWorldTime() % (IMagicHandler.BEAM_TIME * 20) == 0){
						ModPackets.network.sendToAllAround(new SendIntToClient(dir == AxisDirection.POSITIVE ? 1 : 0, (dir == AxisDirection.POSITIVE ? beamerUp : beamer).getPacket(), pos), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
					}
					break;
				case 3:
					if((dir == AxisDirection.POSITIVE ? beamerUp : beamer).emit(mag == null || mag.getStability() == 0 ? null : new MagicUnit(0, 0, mag.getStability(), 0)) || world.getTotalWorldTime() % (IMagicHandler.BEAM_TIME * 20) == 0){
						ModPackets.network.sendToAllAround(new SendIntToClient(dir == AxisDirection.POSITIVE ? 1 : 0, (dir == AxisDirection.POSITIVE ? beamerUp : beamer).getPacket(), pos), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
					}
					break;
				case 4:
					if(MagicElements.getElement(mag) == MagicElements.LIGHT){
						world.setBlockState(pos, ModBlocks.lensHolder.getDefaultState().withProperty(Properties.ORIENT, world.getBlockState(pos).getValue(Properties.ORIENT)).withProperty(Properties.TEXTURE_7, 5));
					}
					if((dir == AxisDirection.POSITIVE ? beamerUp : beamer).emit(mag) || world.getTotalWorldTime() % (IMagicHandler.BEAM_TIME * 20) == 0){
						ModPackets.network.sendToAllAround(new SendIntToClient(dir == AxisDirection.POSITIVE ? 1 : 0, (dir == AxisDirection.POSITIVE ? beamerUp : beamer).getPacket(), pos), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
					}
					break;
				case 5:
					if((dir == AxisDirection.POSITIVE ? beamerUp : beamer).emit(mag) || world.getTotalWorldTime() % (IMagicHandler.BEAM_TIME * 20) == 0){
						ModPackets.network.sendToAllAround(new SendIntToClient(dir == AxisDirection.POSITIVE ? 1 : 0, (dir == AxisDirection.POSITIVE ? beamerUp : beamer).getPacket(), pos), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
					}
					break;
				case 6:
					if((dir == AxisDirection.POSITIVE ? beamerUp : beamer).emit(mag == null || MagicElements.getElement(mag) != MagicElements.RIFT ? null : new MagicUnit(0, 0, 0, mag.getPower())) || world.getTotalWorldTime() % (IMagicHandler.BEAM_TIME * 20) == 0){
						ModPackets.network.sendToAllAround(new SendIntToClient(dir == AxisDirection.POSITIVE ? 1 : 0, (dir == AxisDirection.POSITIVE ? beamerUp : beamer).getPacket(), pos), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
					}
					break;
			}

			lastRedstone = Math.max(beamer.getLastSent() == null ? 0 : beamer.getLastSent().getPower(), beamerUp.getLastSent() == null ? 0 : beamerUp.getLastSent().getPower());
			markDirty();
		}
	}

	private class LensHandler implements IItemHandler{

		private ItemStack getLens(){
			switch(world.getBlockState(pos).getValue(Properties.TEXTURE_7)){
				case 0:
					return ItemStack.EMPTY;
				case 1:
					return new ItemStack(OreSetUp.gemRuby, 1);
				case 2:
					return new ItemStack(Items.EMERALD, 1);
				case 3:
					return new ItemStack(Items.DIAMOND, 1);
				case 4:
					return new ItemStack(ModItems.pureQuartz, 1);
				case 5:
					return new ItemStack(ModItems.luminescentQuartz, 1);
				default: 
					return ItemStack.EMPTY;
			}
		}

		@Override
		public int getSlots(){
			return 1;
		}

		@Override
		public ItemStack getStackInSlot(int slot){
			return slot == 0 ? getLens() : ItemStack.EMPTY;
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate){
			if(slot != 0 || !getLens().isEmpty() || stack.isEmpty() || (stack.getItem() != Items.DIAMOND && stack.getItem() != Items.EMERALD && stack.getItem() != ModItems.pureQuartz && stack.getItem() != OreSetUp.gemRuby)){
				return stack;
			}

			if(!simulate){
				world.setBlockState(pos, ModBlocks.lensHolder.getDefaultState().withProperty(Properties.ORIENT, world.getBlockState(pos).getValue(Properties.ORIENT)).withProperty(Properties.TEXTURE_7, stack.getItem() == ModItems.pureQuartz ? 4 : stack.getItem() == Items.EMERALD ? 2 : stack.getItem() == Items.DIAMOND ? 3 : 1));
				markDirty();
			}

			return stack.getCount() - 1 <= 0 ? ItemStack.EMPTY : new ItemStack(stack.getItem(), stack.getCount() - 1);
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate){
			if(slot != 0 || amount < 1 || getLens() == ItemStack.EMPTY){
				return ItemStack.EMPTY;
			}
			ItemStack holder = getLens();
			if(!simulate){
				world.setBlockState(pos, ModBlocks.lensHolder.getDefaultState().withProperty(Properties.ORIENT, world.getBlockState(pos).getValue(Properties.ORIENT)).withProperty(Properties.TEXTURE_7, 0));
				markDirty();
			}
			return holder;
		}

		@Override
		public int getSlotLimit(int slot){
			return slot == 0 ? 1 : 0;
		}
	}
} 
