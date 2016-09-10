package com.Da_Technomancer.crossroads.tileentities.magic;

import java.awt.Color;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Triple;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.API.Capabilities;
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

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class LensHolderTileEntity extends BeamRenderTE implements ITickable, IIntReceiver{
	
	private Triple<Color, Integer, Integer> trip;
	private Triple<Color, Integer, Integer> tripUp;
	
	@Override
	@Nullable
	public MagicUnit[] getLastSent(){
		return new MagicUnit[] {beamer == null ? null : beamer.getLastSent(), beamerUp == null ? null : beamerUp.getLastSent()};
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Triple<Color, Integer, Integer>[] getBeam(){
		Triple<Color, Integer, Integer>[] out = new Triple[6];
		out[EnumFacing.getFacingFromAxis(AxisDirection.POSITIVE, worldObj.getBlockState(pos).getValue(Properties.ORIENT) ? Axis.X : Axis.Z).getIndex()] = tripUp;
		out[EnumFacing.getFacingFromAxis(AxisDirection.NEGATIVE, worldObj.getBlockState(pos).getValue(Properties.ORIENT) ? Axis.X : Axis.Z).getIndex()] = trip;
		return out;
	}

	@Override
	public void refresh(){
		if(beamer != null){
			beamer.emit(null, 0);
		}
		if(beamerUp != null){
			beamerUp.emit(null, 0);
		}
	}
	
	@Override
	public void update(){
		if(worldObj.isRemote){
			return;
		}

		if(beamer == null){
			beamer = new BeamManager(EnumFacing.getFacingFromAxis(AxisDirection.NEGATIVE, worldObj.getBlockState(pos).getValue(Properties.ORIENT) ? Axis.X : Axis.Z), pos, worldObj);
		}
		if(beamerUp == null){
			beamerUp = new BeamManager(EnumFacing.getFacingFromAxis(AxisDirection.POSITIVE, worldObj.getBlockState(pos).getValue(Properties.ORIENT) ? Axis.X : Axis.Z), pos, worldObj);
		}
	}
	
	private BeamManager beamer;
	private BeamManager beamerUp;

	@Override
	public void receiveInt(String context, int message){
		if(context.equals("beam")){
			trip = BeamManager.getTriple(message);
		}else if(context.equals("beamUp")){
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
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		memTrip = nbt.getInteger("memTrip");
		memTripUp = nbt.getInteger("memTripUp");
		if(nbt.hasKey("beam")){
			trip = BeamManager.getTriple(nbt.getInteger("beam"));
		}
		if(nbt.hasKey("beamUp")){
			tripUp = BeamManager.getTriple(nbt.getInteger("beamUp"));
		}
	}
	
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState){
		return (oldState.getBlock() != newState.getBlock());
	}
	
	private final IMagicHandler magicHandler = new MagicHandler(AxisDirection.NEGATIVE);
	private final IMagicHandler magicHandlerNeg = new MagicHandler(AxisDirection.POSITIVE);
	private final IItemHandler lensHandler = new LensHandler();
	
	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		if(cap == Capabilities.MAGIC_HANDLER_CAPABILITY && (side == null || (side.getAxis() == Axis.X) == worldObj.getBlockState(pos).getValue(Properties.ORIENT))){
			return true;
		}
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			return true;
		}
		
		
		return super.hasCapability(cap, side);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(cap == Capabilities.MAGIC_HANDLER_CAPABILITY && (side == null || (side.getAxis() == Axis.X) == worldObj.getBlockState(pos).getValue(Properties.ORIENT))){
			return side == null ? (T) magicHandler : side.getAxisDirection() == AxisDirection.NEGATIVE ? (T) magicHandlerNeg : (T) magicHandler;
		}
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			return (T) lensHandler;
		}
		
		return super.getCapability(cap, side);
	}
	
	private class MagicHandler implements IMagicHandler{

		private final AxisDirection dir;
		
		private MagicHandler(AxisDirection dir){
			this.dir = dir;
		}

		@Override
		public void setMagic(MagicUnit mag, int steps){
			if(beamer == null || beamerUp == null){
				return;
			}
			if(mag != null && mag.getVoid() != 0){
				worldObj.setBlockState(pos, ModBlocks.lensHolder.getDefaultState().withProperty(Properties.ORIENT, worldObj.getBlockState(pos).getValue(Properties.ORIENT)).withProperty(Properties.TUXTURE_6, 0));
				(dir == AxisDirection.POSITIVE ? beamerUp : beamer).emit(mag, steps);
				return;
			}
			
			switch(worldObj.getBlockState(pos).getValue(Properties.TUXTURE_6)){
				case 0:
					if((dir == AxisDirection.POSITIVE ? beamerUp : beamer).emit(mag, steps)){
						ModPackets.network.sendToAllAround(new SendIntToClient("beam" + (dir == AxisDirection.POSITIVE ? "Up" : ""), (dir == AxisDirection.POSITIVE ? beamerUp : beamer).getPacket(), pos), new TargetPoint(worldObj.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
					}
					break;
				case 1:
					if((dir == AxisDirection.POSITIVE ? beamerUp : beamer).emit(mag == null || mag.getEnergy() == 0 ? null : new MagicUnit(mag.getEnergy(), 0, 0, 0), steps)){
						ModPackets.network.sendToAllAround(new SendIntToClient("beam" + (dir == AxisDirection.POSITIVE ? "Up" : ""), (dir == AxisDirection.POSITIVE ? beamerUp : beamer).getPacket(), pos), new TargetPoint(worldObj.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
					}
					break;
				case 2:
					if((dir == AxisDirection.POSITIVE ? beamerUp : beamer).emit(mag == null || mag.getPotential() == 0 ? null : new MagicUnit(0, mag.getPotential(), 0, 0), steps)){
						ModPackets.network.sendToAllAround(new SendIntToClient("beam" + (dir == AxisDirection.POSITIVE ? "Up" : ""), (dir == AxisDirection.POSITIVE ? beamerUp : beamer).getPacket(), pos), new TargetPoint(worldObj.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
					}
					break;
				case 3:
					if((dir == AxisDirection.POSITIVE ? beamerUp : beamer).emit(mag == null || mag.getStability() == 0 ? null : new MagicUnit(0, 0, mag.getStability(), 0), steps)){
						ModPackets.network.sendToAllAround(new SendIntToClient("beam" + (dir == AxisDirection.POSITIVE ? "Up" : ""), (dir == AxisDirection.POSITIVE ? beamerUp : beamer).getPacket(), pos), new TargetPoint(worldObj.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
					}
					break;
				case 4:
					if(MagicElements.getElement(mag) == MagicElements.LIGHT){
						worldObj.setBlockState(pos, ModBlocks.lensHolder.getDefaultState().withProperty(Properties.ORIENT, worldObj.getBlockState(pos).getValue(Properties.ORIENT)).withProperty(Properties.TUXTURE_6, 5));
					}
					if((dir == AxisDirection.POSITIVE ? beamerUp : beamer).emit(mag, steps)){
						ModPackets.network.sendToAllAround(new SendIntToClient("beam" + (dir == AxisDirection.POSITIVE ? "Up" : ""), (dir == AxisDirection.POSITIVE ? beamerUp : beamer).getPacket(), pos), new TargetPoint(worldObj.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
					}
					break;
				case 5:
					if((dir == AxisDirection.POSITIVE ? beamerUp : beamer).emit(mag, steps)){
						ModPackets.network.sendToAllAround(new SendIntToClient("beam" + (dir == AxisDirection.POSITIVE ? "Up" : ""), (dir == AxisDirection.POSITIVE ? beamerUp : beamer).getPacket(), pos), new TargetPoint(worldObj.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
					}
			}
		}
	}
	
	private class LensHandler implements IItemHandler{

		private ItemStack getLens(){
			switch(worldObj.getBlockState(pos).getValue(Properties.TUXTURE_6)){
				case 0:
					return null;
				case 1:
					return new ItemStack(Item.getByNameOrId(Main.MODID + ":gemRuby"), 1);
				case 2:
					return new ItemStack(Items.EMERALD, 1);
				case 3:
					return new ItemStack(Items.DIAMOND, 1);
				case 4:
					return new ItemStack(ModItems.pureQuartz, 1);
				case 5:
					return new ItemStack(ModItems.luminescentQuartz, 1);
				default: 
					return null;
			}
		}
		
		@Override
		public int getSlots(){
			return 1;
		}

		@Override
		public ItemStack getStackInSlot(int slot){
			return slot == 0 ? getLens() : null;
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate){
			if(slot != 0 || getLens() != null || stack == null || (stack.getItem() != Items.DIAMOND && stack.getItem() != Items.EMERALD && stack.getItem() != ModItems.pureQuartz && stack.getItem() != Item.getByNameOrId(Main.MODID + ":gemRuby"))){
				return stack;
			}
			
			if(!simulate){
				worldObj.setBlockState(pos, ModBlocks.lensHolder.getDefaultState().withProperty(Properties.ORIENT, worldObj.getBlockState(pos).getValue(Properties.ORIENT)).withProperty(Properties.TUXTURE_6, stack.getItem() == ModItems.pureQuartz ? 4 : stack.getItem() == Items.EMERALD ? 2 : stack.getItem() == Items.DIAMOND ? 3 : 1));
			}
			
			return stack.stackSize - 1 <= 0 ? null : new ItemStack(stack.getItem(), stack.stackSize - 1);
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate){
			if(slot != 0 || amount < 1 || getLens() == null){
				return null;
			}
			ItemStack holder = getLens();
			if(!simulate){
				worldObj.setBlockState(pos, ModBlocks.lensHolder.getDefaultState().withProperty(Properties.ORIENT, worldObj.getBlockState(pos).getValue(Properties.ORIENT)).withProperty(Properties.TUXTURE_6, 0));
			}
			return holder;
		}
	}
} 
