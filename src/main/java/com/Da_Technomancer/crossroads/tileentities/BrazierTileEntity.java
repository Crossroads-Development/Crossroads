package com.Da_Technomancer.crossroads.tileentities;

import java.util.ArrayList;
import java.util.Random;

import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.items.crafting.RecipeHolder;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class BrazierTileEntity extends TileEntity implements ISidedInventory, ITickable{

	private static Random rand = new Random();

	private int ticksExisted = 0;

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState){
		return (oldState.getBlock() != newState.getBlock());
	}

	@Override
	public void update(){
		if(worldObj.isRemote){
			return;
		}
		ticksExisted++;

		if(getState() != 0 && --time <= 0){
			decrStackSize(0, 1);
			time = 6000;
		}

		if(getState() == 0){
			if(worldObj.getBlockState(pos).getValue(Properties.LIGHT)){
				worldObj.setBlockState(pos, ModBlocks.brazier.getDefaultState().withProperty(Properties.LIGHT, false), 2);
			}
		}else if(!worldObj.getBlockState(pos).getValue(Properties.LIGHT)){
			worldObj.setBlockState(pos, ModBlocks.brazier.getDefaultState().withProperty(Properties.LIGHT, true), 2);
		}

		WorldServer server = (WorldServer) worldObj;

		if(ticksExisted % 10 == 0){
			ItemStack out;
			switch(getState()){
				case 0:
					break;
				case 1:
					server.spawnParticle(EnumParticleTypes.FLAME, false, pos.getX() + .25 + (.5 * rand.nextDouble()), pos.getY() + 1 + (rand.nextDouble() * .25D), pos.getZ() + .25 + (.5 * rand.nextDouble()), 1, 0, 0, 0, 0, new int[0]);
					break;
				case 2:
					server.spawnParticle(EnumParticleTypes.REDSTONE, false, pos.getX() + .25 + (.5 * rand.nextDouble()), pos.getY() + 1 + (rand.nextDouble() * .25D), pos.getZ() + .25 + (.5 * rand.nextDouble()), 0, -1, 1, 1, 1, new int[0]);
					break;
				case 3:
					server.spawnParticle(EnumParticleTypes.REDSTONE, false, pos.getX() + .25 + (.5 * rand.nextDouble()), pos.getY() + 1 + (rand.nextDouble() * .25D), pos.getZ() + .25 + (.5 * rand.nextDouble()), 0, 0, .4, .4, 1, new int[0]);
					if((out = RecipeHolder.recipeMatch(false, (ArrayList<EntityItem>) worldObj.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos.add(-1, 0, -1), pos.add(1, 1, 1)), EntitySelectors.IS_ALIVE))) != null){
						for(EntityItem item : worldObj.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos.add(-1, 0, -1), pos.add(1, 1, 1)), EntitySelectors.IS_ALIVE)){
							item.setDead();
						}

						worldObj.createExplosion(null, pos.getX(), pos.getY() + 1, pos.getZ(), 0, true);
						worldObj.spawnEntityInWorld(new EntityItem(worldObj, pos.getX(), pos.getY() + 1, pos.getZ(), out.copy()));
					}
					break;
				case 4:
					server.spawnParticle(EnumParticleTypes.REDSTONE, false, pos.getX() + .25 + (.5 * rand.nextDouble()), pos.getY() + 1 + (rand.nextDouble() * .25D), pos.getZ() + .25 + (.5 * rand.nextDouble()), 0, -1, 0, 1, 1, new int[0]);
					server.spawnParticle(EnumParticleTypes.REDSTONE, false, pos.getX() + .25 + (.5 * rand.nextDouble()), pos.getY() + 1 + (rand.nextDouble() * .25D), pos.getZ() + .25 + (.5 * rand.nextDouble()), 0, 0, 1, 0, 1, new int[0]);
					if((out = RecipeHolder.recipeMatch(true, (ArrayList<EntityItem>) worldObj.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos.add(-1, 0, -1), pos.add(1, 1, 1)), EntitySelectors.IS_ALIVE))) != null){
						for(EntityItem item : worldObj.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos.add(-1, 0, -1), pos.add(1, 1, 1)), EntitySelectors.IS_ALIVE)){
							item.setDead();
						}

						worldObj.spawnEntityInWorld(new EntityLightningBolt(worldObj, pos.getX() + 1, pos.getY(), pos.getZ(), true));
						worldObj.spawnEntityInWorld(new EntityItem(worldObj, pos.getX(), pos.getY() + 1, pos.getZ(), out.copy()));
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
	private ItemStack inventory;

	// 0 means not lit, 1 means lit normally, 2 means lit with salt, 3 means lit
	// with mashed potato, 4 means lit with poisonous potato.
	private byte getState(){
		if(inventory == null){
			return 0;
		}

		if(inventory.getItem() == ModItems.dustSalt){
			return 2;
		}

		if(inventory.getItem() == Items.COAL && inventory.getMetadata() == 1){
			return 1;
		}

		if(inventory.getItem() == ModItems.mashedPotato){
			return 3;
		}

		if(inventory.getItem() == Items.POISONOUS_POTATO){
			return 4;
		}

		return 0;
	}

	public ItemStack addFuel(ItemStack stack){
		if((stack.getItem() == Items.COAL && stack.getMetadata() == 1) || stack.getItem() == ModItems.dustSalt || stack.getItem() == Items.POISONOUS_POTATO || stack.getItem() == ModItems.mashedPotato){
			if(inventory == null || (stack.getItem() == inventory.getItem() && inventory.stackSize != getInventoryStackLimit())){

				stack.stackSize--;

				inventory = new ItemStack(stack.getItem(), 1 + (inventory == null ? 0 : inventory.stackSize), stack.getMetadata());

				if(stack.stackSize <= 0){
					stack = null;
				}
			}
		}
		return stack;
	}

	@Override
	public int getSizeInventory(){
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int index){
		return index == 0 ? inventory : null;
	}

	@Override
	public ItemStack decrStackSize(int index, int count){
		if(index != 0 || inventory == null){
			return null;
		}

		int holder = Math.min(inventory.stackSize, count);
		ItemStack taken = inventory.splitStack(holder);
		if(inventory.stackSize == 0){
			inventory = null;
		}
		this.markDirty();
		return taken;
	}

	@Override
	public ItemStack removeStackFromSlot(int index){
		if(index != 0){
			return null;
		}
		ItemStack output = inventory;
		inventory = null;
		return output;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack){
		if(index == 0){
			inventory = stack;
		}
	}

	@Override
	public int getInventoryStackLimit(){
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player){
		return this.worldObj.getTileEntity(this.getPos()) == this && player.getDistanceSq(this.pos.add(0.5, 0.5, 0.5)) <= 64;
	}

	@Override
	public void openInventory(EntityPlayer player){

	}

	@Override
	public void closeInventory(EntityPlayer player){

	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack){
		return index == 0 && (stack.getItem() == ModItems.dustSalt || (stack.getItem() == Items.COAL && stack.getMetadata() == 1) || stack.getItem() == ModItems.mashedPotato || stack.getItem() == Items.POISONOUS_POTATO);
	}

	@Override
	public int getField(int id){
		return 0;
	}

	@Override
	public void setField(int id, int value){

	}

	@Override
	public int getFieldCount(){
		return 0;
	}

	@Override
	public void clear(){
		inventory = null;
	}

	@Override
	public String getName(){
		return "container.brazier";
	}

	@Override
	public boolean hasCustomName(){
		return false;
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side){
		return new int[] {0};
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction){
		return isItemValidForSlot(index, itemStackIn);
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction){
		return false;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);

		NBTTagList list = new NBTTagList();
		for(int i = 0; i < this.getSizeInventory(); ++i){
			if(this.getStackInSlot(i) != null){
				NBTTagCompound stackTag = new NBTTagCompound();
				stackTag.setByte("Slot", (byte) i);
				this.getStackInSlot(i).writeToNBT(stackTag);
				list.appendTag(stackTag);
			}
		}
		nbt.setTag("Items", list);

		nbt.setInteger("time", time);

		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);

		NBTTagList list = nbt.getTagList("Items", 10);
		for(int i = 0; i < list.tagCount(); ++i){
			NBTTagCompound stackTag = list.getCompoundTagAt(i);
			int slot = stackTag.getByte("Slot") & 255;
			this.setInventorySlotContents(slot, ItemStack.loadItemStackFromNBT(stackTag));
		}

		time = nbt.getInteger("time");
	}
}
