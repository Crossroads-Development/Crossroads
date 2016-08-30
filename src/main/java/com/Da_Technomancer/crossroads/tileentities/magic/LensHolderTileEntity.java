package com.Da_Technomancer.crossroads.tileentities.magic;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.enums.MagicElements;
import com.Da_Technomancer.crossroads.API.magic.IMagicHandler;
import com.Da_Technomancer.crossroads.API.magic.MagicUnit;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.items.ModItems;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class LensHolderTileEntity extends TileEntity{
	
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState){
		return (oldState.getBlock() != newState.getBlock());
	}
	
	private final IMagicHandler magicHandler = new MagicHandler();
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
			return (T) magicHandler;
		}
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			return (T) lensHandler;
		}
		
		return super.getCapability(cap, side);
	}
	
	private class MagicHandler implements IMagicHandler{

		@Override
		public MagicUnit canPass(MagicUnit mag){
			if(mag.getVoid() != 0){
				worldObj.setBlockState(pos, ModBlocks.lensHolder.getDefaultState().withProperty(Properties.ORIENT, worldObj.getBlockState(pos).getValue(Properties.ORIENT)).withProperty(Properties.TUXTURE_6, 0));
				return mag;
			}
			
			switch(worldObj.getBlockState(pos).getValue(Properties.TUXTURE_6)){
				case 0:
					return mag;
				case 1:
					return mag.getEnergy() == 0 ? null : new MagicUnit(mag.getEnergy(), 0, 0, 0);
				case 2:
					return mag.getPotential() == 0 ? null : new MagicUnit(0, mag.getPotential(), 0, 0);
				case 3:
					return mag.getStability() == 0 ? null : new MagicUnit(0, 0, mag.getStability(), 0);
				case 4:
					if(MagicElements.getElement(mag) == MagicElements.LIGHT){
						worldObj.setBlockState(pos, ModBlocks.lensHolder.getDefaultState().withProperty(Properties.ORIENT, worldObj.getBlockState(pos).getValue(Properties.ORIENT)).withProperty(Properties.TUXTURE_6, 5));
					}
					return mag;
				case 5:
					return mag;
				default: 
					return null;
			}
		}

		@Override
		public void recieveMagic(MagicUnit mag){
			
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
