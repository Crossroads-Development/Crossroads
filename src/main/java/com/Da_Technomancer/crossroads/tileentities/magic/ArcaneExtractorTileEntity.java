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
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class ArcaneExtractorTileEntity extends BeamRenderTE implements ITickable, IIntReceiver{

	private ItemStack inv;
	private Color col;
	private int reach;
	private int size;
	
	@Override
	public Triple<Color, Integer, Integer> getBeam(){
		return col == null ? null : Triple.of(col, reach, size);
	}
	
	@Override
	public void update(){
		if(worldObj.isRemote){
			if(col != null || reach != 0 || size != 0){
				col = null;
				reach = 0;
				size = 0;
			}
			return;
		}
		
				
		if(inv != null){
			if(!RecipeHolder.magExtractRecipes.containsKey(inv.getItem())){
				inv = null;
				return;
			}
			
			MagicUnit mag = RecipeHolder.magExtractRecipes.get(inv.getItem());
			if(--inv.stackSize <= 0){
				inv = null;
			}
			emit(mag, worldObj.getBlockState(pos).getValue(Properties.FACING));
		}
	}
	
	private void emit(MagicUnit mag, EnumFacing dir){
		for(int i = 1; i <= IMagicHandler.MAX_DISTANCE; i++){
			if(worldObj.getTileEntity(pos.offset(dir, i)) != null && worldObj.getTileEntity(pos.offset(dir, i)).hasCapability(Capabilities.MAGIC_HANDLER_CAPABILITY, dir.getOpposite())){
				ModPackets.network.sendToAllAround(new SendIntToClient("beam", ((i - 1) << 24) + (mag.getRGB().getRGB() & 16777215) + ((Math.min((int) Math.sqrt(mag.getPower()) - 1, 8)) << 28), this.getPos()), new TargetPoint(this.getWorld().provider.getDimension(), getPos().getX(), getPos().getY(), getPos().getZ(), 512));
				worldObj.getTileEntity(pos.offset(dir, i)).getCapability(Capabilities.MAGIC_HANDLER_CAPABILITY, dir.getOpposite()).recieveMagic(mag);
				return;
			}
			
			if(i == IMagicHandler.MAX_DISTANCE || (worldObj.getBlockState(pos.offset(dir, i)) != null && worldObj.getBlockState(pos.offset(dir, i)).isOpaqueCube())){
				ModPackets.network.sendToAllAround(new SendIntToClient("beam", ((i - 1) << 24) + (mag.getRGB().getRGB() & 16777215) + ((Math.min((int) Math.sqrt(mag.getPower()) - 1, 8)) << 28), this.getPos()), new TargetPoint(this.getWorld().provider.getDimension(), getPos().getX(), getPos().getY(), getPos().getZ(), 512));
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
			reach = 1 + ((message - i) >> 24);
			size = 1 + ((message - reach) >> 28);
			
		}
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
