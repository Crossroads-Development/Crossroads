package com.Da_Technomancer.crossroads.tileentities.magic;

import java.awt.Color;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Triple;

import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.magic.BeamManager;
import com.Da_Technomancer.crossroads.API.magic.BeamRenderTE;
import com.Da_Technomancer.crossroads.API.magic.IMagicHandler;
import com.Da_Technomancer.crossroads.API.magic.MagicUnit;
import com.Da_Technomancer.crossroads.API.packets.IIntReceiver;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendIntToClient;
import com.Da_Technomancer.crossroads.items.crafting.RecipeHolder;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class ArcaneExtractorTileEntity extends BeamRenderTE implements ITickable, IIntReceiver{

	private ItemStack inv;
	private Triple<Color, Integer, Integer> visual;
	
	@Override
	public void refresh(){
		if(beamer != null){
			beamer.emit(null);
		}
	}
	
	@Override
	@Nullable
	public MagicUnit[] getLastFullSent(){
		return beamer == null || beamer.getLastFullSent() == null ? null : new MagicUnit[] {beamer.getLastFullSent()};
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Triple<Color, Integer, Integer>[] getBeam(){
		Triple<Color, Integer, Integer>[] out = new Triple[6];
		out[worldObj.getBlockState(pos).getValue(Properties.FACING).getIndex()] = visual;
		return out;
	}
	
	@Override
	public void update(){
		if(worldObj.isRemote){
			return;
		}
		
		if(beamer == null){
			beamer = new BeamManager(worldObj.getBlockState(pos).getValue(Properties.FACING), pos, worldObj);
		}
		
		if(worldObj.getTotalWorldTime() % IMagicHandler.BEAM_TIME == 0){
			if(inv != null && RecipeHolder.magExtractRecipes.containsKey(inv.getItem())){
				MagicUnit mag = RecipeHolder.magExtractRecipes.get(inv.getItem());
				if(--inv.stackSize <= 0){
					inv = null;
				}
				if(beamer.emit(mag)){
					ModPackets.network.sendToAllAround(new SendIntToClient("beam", beamer.getPacket(), pos), new TargetPoint(worldObj.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
				}
			}else{
				inv = null;
				if(beamer.emit(null)){
					ModPackets.network.sendToAllAround(new SendIntToClient("beam", 0, pos), new TargetPoint(worldObj.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
				}
			}
		}
	}
	
	private BeamManager beamer;
	
	@Override
	public void receiveInt(String context, int message){
		if(context.equals("beam")){
			visual = BeamManager.getTriple(message);
		}
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		
		if(inv != null){
			nbt.setTag("inv", inv.writeToNBT(new NBTTagCompound()));
		}
		return nbt;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		
		inv = nbt.hasKey("inv") ? ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("inv")) : null;
	}
	
	private final IItemHandler itemHandler = new ItemHandler();

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing){
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && (facing == null || facing == EnumFacing.UP)){
			return (T) itemHandler;
		}

		return super.getCapability(capability, facing);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing){
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && (facing == null || facing == EnumFacing.UP)){
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	private class ItemHandler implements IItemHandler{

		@Override
		public int getSlots(){
			return 1;
		}

		@Override
		public ItemStack getStackInSlot(int slot){
			return slot == 0 ? inv : null;
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate){
			if(slot != 0 || stack == null || !(RecipeHolder.magExtractRecipes.containsKey(stack.getItem()))){
				return stack;
			}

			if(inv != null && !ItemStack.areItemsEqual(stack, inv)){
				return stack;
			}

			int limit = Math.min(stack.getMaxStackSize() - (inv == null ? 0 : inv.stackSize), stack.stackSize);
			if(!simulate){
				if(inv == null){
					inv = new ItemStack(stack.getItem(), limit, stack.getMetadata());
				}else{
					inv.stackSize += limit;
				}

			}

			return stack.stackSize == limit ? null : new ItemStack(stack.getItem(), stack.stackSize - limit, stack.getMetadata());
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate){
			return null;
		}
	}
}
