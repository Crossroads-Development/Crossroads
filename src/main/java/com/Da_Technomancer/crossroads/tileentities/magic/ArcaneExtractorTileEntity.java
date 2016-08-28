package com.Da_Technomancer.crossroads.tileentities.magic;

import java.awt.Color;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Triple;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.effects.IEffect;
import com.Da_Technomancer.crossroads.API.enums.MagicElements;
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

	private int ticksExisted;
	private ItemStack inv;
	private Color col;
	private int reach;
	private int size;
	
	@SuppressWarnings("unchecked")
	@Override
	public Triple<Color, Integer, Integer>[] getBeam(){
		Triple<Color, Integer, Integer>[] out = new Triple[6];
		out[worldObj.getBlockState(pos).getValue(Properties.FACING).getIndex()] = col == null ? null : Triple.of(col, reach, size);
		return out;
	}
	
	@Override
	public void update(){
		if(worldObj.isRemote){
			return;
		}
		
		if(++ticksExisted % IMagicHandler.BEAM_TIME == 0){
			if(inv != null && RecipeHolder.magExtractRecipes.containsKey(inv.getItem())){
				MagicUnit mag = RecipeHolder.magExtractRecipes.get(inv.getItem());
				if(--inv.stackSize <= 0){
					inv = null;
				}
				emit(mag, worldObj.getBlockState(pos).getValue(Properties.FACING));
			}else{
				inv = null;
				wipeBeam();
			}
		}
	}
	
	private void wipeBeam(){
		if(col != null || reach != 0 || size != 0){
			col = null;
			reach = 0;
			size = 0;
			ModPackets.network.sendToAllAround(new SendIntToClient("beam", 0, pos), new TargetPoint(worldObj.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
		}
	}
	
	private void emit(MagicUnit mag, EnumFacing dir){
		if(mag == null || mag.getRGB() == null){
			return;
		}
		for(int i = 1; i <= IMagicHandler.MAX_DISTANCE; i++){
			if(worldObj.getTileEntity(pos.offset(dir, i)) != null && worldObj.getTileEntity(pos.offset(dir, i)).hasCapability(Capabilities.MAGIC_HANDLER_CAPABILITY, dir.getOpposite())){
				int siz = Math.min((int) Math.sqrt(mag.getPower()) - 1, 7);
				if(col == null || mag.getRGB().getRGB() != col.getRGB() || siz != size || i != reach){
					ModPackets.network.sendToAllAround(new SendIntToClient("beam", ((i - 1) << 24) + (mag.getRGB().getRGB() & 16777215) + (siz << 28), pos), new TargetPoint(worldObj.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
					size = siz;
					col = mag.getRGB();
					reach = i;
				}
				worldObj.getTileEntity(pos.offset(dir, i)).getCapability(Capabilities.MAGIC_HANDLER_CAPABILITY, dir.getOpposite()).recieveMagic(mag);
				return;
			}
			
			if(i == IMagicHandler.MAX_DISTANCE || (worldObj.getBlockState(pos.offset(dir, i)) != null && !worldObj.getBlockState(pos.offset(dir, i)).getBlock().isAir(worldObj.getBlockState(pos.offset(dir, i)), worldObj, pos.offset(dir, i)))){
				int siz = Math.min((int) Math.sqrt(mag.getPower()) - 1, 7);
				if(col == null || mag.getRGB().getRGB() != col.getRGB() || siz != size || i != reach){
					ModPackets.network.sendToAllAround(new SendIntToClient("beam", ((i - 1) << 24) + (mag.getRGB().getRGB() & 16777215) + (siz << 28), pos), new TargetPoint(worldObj.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
					size = siz;
					col = mag.getRGB();
					reach = i;
				}
				IEffect e = MagicElements.getElement(mag).getMixEffect(mag.getRGB());
				if(e != null){
					e.doEffect(worldObj, pos.offset(dir, i));
				}
				return;
			}
		}
	}
	
	@Override
	public void receiveInt(String context, int message){
		if(context.equals("beam")){
			int i = message & 16777215;
			col = Color.decode(Integer.toString(i));
			reach = ((message & 251658240) >> 24) + 1;
			size = ((message - reach) >> 28) + 1;
			
		}
	}
	
	@Override
	public NBTTagCompound getUpdateTag(){
		NBTTagCompound nbt = super.getUpdateTag();
		if(col != null){
			nbt.setInteger("col", col.getRGB() & 16777215);
		}
		nbt.setInteger("reach", reach);
		nbt.setInteger("size", size + 1);
		return nbt;
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		
		if(inv != null){
			nbt.setTag("inv", inv.writeToNBT(new NBTTagCompound()));
		}
		if(col != null){
			nbt.setInteger("col", col.getRGB() & 16777215);
		}
		nbt.setInteger("reach", reach);
		nbt.setInteger("size", size);
		
		return nbt;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		
		inv = nbt.hasKey("inv") ? ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("inv")) : null;
		col = nbt.hasKey("col") ? Color.decode(Integer.toString(nbt.getInteger("col"))) : null;
		reach = nbt.getInteger("reach");
		size = nbt.getInteger("size");
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
