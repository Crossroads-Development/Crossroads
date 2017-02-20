package com.Da_Technomancer.crossroads.tileentities;

import java.util.ArrayList;

import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendLightningToClient;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.items.crafting.RecipeHolder;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class BrazierTileEntity extends TileEntity implements ITickable{

	private int ticksExisted = 0;

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState){
		return (oldState.getBlock() != newState.getBlock());
	}

	@Override
	public void update(){
		if(world.isRemote){
			return;
		}
		ticksExisted++;

		if(getState() != 0 && --time <= 0){
			inventory.shrink(1);
			time = 6000;
			markDirty();
		}

		if(getState() == 0){
			if(world.getBlockState(pos).getValue(Properties.LIGHT)){
				world.setBlockState(pos, ModBlocks.brazier.getDefaultState().withProperty(Properties.LIGHT, false), 2);
			}
		}else if(!world.getBlockState(pos).getValue(Properties.LIGHT)){
			world.setBlockState(pos, ModBlocks.brazier.getDefaultState().withProperty(Properties.LIGHT, true), 2);
		}

		WorldServer server = (WorldServer) world;

		if(ticksExisted % 10 == 0){
			ItemStack out;
			switch(getState()){
				case 0:
					break;
				case 1:
					server.spawnParticle(EnumParticleTypes.FLAME, false, pos.getX() + .25 + (.5 * Math.random()), pos.getY() + 1 + (Math.random() * .25D), pos.getZ() + .25 + (.5 * Math.random()), 1, 0, 0, 0, 0, new int[0]);
					break;
				case 2:
					server.spawnParticle(EnumParticleTypes.REDSTONE, false, pos.getX() + .25 + (.5 * Math.random()), pos.getY() + 1 + (Math.random() * .25D), pos.getZ() + .25 + (.5 * Math.random()), 0, -1, 1, 1, 1, new int[0]);
					break;
				case 3:
					server.spawnParticle(EnumParticleTypes.REDSTONE, false, pos.getX() + .25 + (.5 * Math.random()), pos.getY() + 1 + (Math.random() * .25D), pos.getZ() + .25 + (.5 * Math.random()), 0, -1, 0, 1, 1, new int[0]);
					server.spawnParticle(EnumParticleTypes.REDSTONE, false, pos.getX() + .25 + (.5 * Math.random()), pos.getY() + 1 + (Math.random() * .25D), pos.getZ() + .25 + (.5 * Math.random()), 0, 0, 1, 0, 1, new int[0]);
					if((out = RecipeHolder.recipeMatch((ArrayList<EntityItem>) world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos.add(-1, 0, -1), pos.add(1, 1, 1)), EntitySelectors.IS_ALIVE))) != ItemStack.EMPTY){
						for(EntityItem item : world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos.add(-1, 0, -1), pos.add(1, 1, 1)), EntitySelectors.IS_ALIVE)){
							item.setDead();
						}

						world.spawnEntity(new EntityLightningBolt(world, pos.getX(), pos.getY() + 1, pos.getZ(), true));
						ModPackets.network.sendToAllAround(new SendLightningToClient(pos), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY() + 1, pos.getZ(), 512));
						world.spawnEntity(new EntityItem(world, pos.getX(), pos.getY() + 1, pos.getZ(), out.copy()));
					}
					break;
			}
		}
	}

	public static boolean blockSpawning(World worldIn, double X, double Y, double Z){

		// 64 squared
		final int RANGE_SQUARED = 4096;

		for(TileEntity te : worldIn.loadedTileEntityList){
			if(te instanceof BrazierTileEntity && ((BrazierTileEntity) te).getState() == 2 && te.getDistanceSq(X, Y, Z) <= RANGE_SQUARED){
				return true;
			}
		}

		return false;
	}

	private int time = 6000;
	private ItemStack inventory = ItemStack.EMPTY;
	
	/**
	 * For internal use
	 */
	public ItemStack getInventory(){
		return inventory;
	}
	
	// 0 means not lit, 1 means lit normally, 2 means lit with salt, 3 means lit
	// with poisonous potato.
	private byte getState(){
		if(inventory.isEmpty()){
			return 0;
		}

		if(inventory.getItem() == ModItems.dustSalt){
			return 2;
		}

		if(inventory.getItem() == Items.COAL && inventory.getMetadata() == 1){
			return 1;
		}

		if(inventory.getItem() == Items.POISONOUS_POTATO){
			return 3;
		}

		return 0;
	}

	private final FuelHandler fuelHandler = new FuelHandler();
	
	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			return true;
		}
		return super.hasCapability(cap, side);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			return (T) fuelHandler;
		}
		return super.getCapability(cap, side);
	}
	
	private class FuelHandler implements IItemHandler{

		@Override
		public int getSlots(){
			return 1;
		}

		@Override
		public ItemStack getStackInSlot(int slot){
			return slot == 0 ? inventory : ItemStack.EMPTY;
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate){
			if(slot == 0 && ((stack.getItem() == Items.COAL && stack.getMetadata() == 1) || stack.getItem() == ModItems.dustSalt || stack.getItem() == Items.POISONOUS_POTATO)){
				if(inventory.isEmpty() || (stack.getItem() == inventory.getItem() && inventory.getCount() < 64)){
					ItemStack out = new ItemStack(stack.getItem(), stack.getCount() + inventory.getCount() - 64);
					if(!simulate){
						inventory = new ItemStack(stack.getItem(), Math.min(inventory.getCount() + stack.getCount(), 64), stack.getMetadata());
						markDirty();
					}
					if(out.getCount() <= 0){
						return ItemStack.EMPTY;
					}
					return out;
				}
			}
			return stack;
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate){
			return ItemStack.EMPTY;
		}

		@Override
		public int getSlotLimit(int slot){
			return slot == 0 ? 64 : 0;
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		inventory.writeToNBT(nbt);
		nbt.setInteger("time", time);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		inventory = new ItemStack(nbt);
		time = nbt.getInteger("time");
	}
}
