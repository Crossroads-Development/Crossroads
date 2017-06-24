package com.Da_Technomancer.crossroads.tileentities.magic;

import java.awt.Color;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Triple;

import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.magic.BeamManager;
import com.Da_Technomancer.crossroads.API.magic.BeamRenderTE;
import com.Da_Technomancer.crossroads.API.magic.MagicUnit;
import com.Da_Technomancer.crossroads.API.packets.IIntReceiver;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendIntToClient;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.items.crafting.RecipeHolder;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class ArcaneExtractorTileEntity extends BeamRenderTE implements ITickable, IIntReceiver{

	private ItemStack inv = ItemStack.EMPTY;
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
		if(world.getBlockState(pos).getBlock() != ModBlocks.arcaneExtractor){
			return null;
		}
		out[world.getBlockState(pos).getValue(Properties.FACING).getIndex()] = visual;
		return out;
	}

	@Override
	public void update(){
		if(world.isRemote){
			return;
		}

		if(beamer == null){
			beamer = new BeamManager(world.getBlockState(pos).getValue(Properties.FACING), pos, world);
		}

		if(BeamManager.beamStage == 0){
			if(!inv.isEmpty() && RecipeHolder.magExtractRecipes.containsKey(inv.getItem())){
				MagicUnit mag = RecipeHolder.magExtractRecipes.get(inv.getItem());
				inv.shrink(1);
				markDirty();
				if(beamer.emit(mag)){
					ModPackets.network.sendToAllAround(new SendIntToClient(0, beamer.getPacket(), pos), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
				}
			}else{
				if(!inv.isEmpty()){
					inv = ItemStack.EMPTY;
					markDirty();
				}
				if(beamer.emit(null)){
					ModPackets.network.sendToAllAround(new SendIntToClient(0, 0, pos), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
				}
			}
		}
	}

	private BeamManager beamer;

	@Override
	public void receiveInt(int identifier, int message, EntityPlayerMP player){
		if(identifier == 0){
			visual = BeamManager.getTriple(message);
		}
	}

	private int memTrip;

	@Override
	public NBTTagCompound getUpdateTag(){
		NBTTagCompound nbt = super.getUpdateTag();
		nbt.setInteger("beam", memTrip);
		return nbt;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		if(!inv.isEmpty()){
			nbt.setTag("inv", inv.writeToNBT(new NBTTagCompound()));
		}
		nbt.setInteger("memTrip", beamer == null ? 0 : beamer.getPacket());
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		memTrip = nbt.getInteger("memTrip");
		inv = nbt.hasKey("inv") ? new ItemStack(nbt.getCompoundTag("inv")) : ItemStack.EMPTY;
		if(nbt.hasKey("beam")){
			visual = BeamManager.getTriple(nbt.getInteger("beam"));
		}
	}

	private final IItemHandler itemHandler = new ItemHandler();

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing){
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing != world.getBlockState(pos).getValue(Properties.FACING)){
			return (T) itemHandler;
		}

		return super.getCapability(capability, facing);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing){
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing != world.getBlockState(pos).getValue(Properties.FACING)){
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
			return slot == 0 ? inv : ItemStack.EMPTY;
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate){
			if(slot != 0 || stack.isEmpty() || !(RecipeHolder.magExtractRecipes.containsKey(stack.getItem()))){
				return stack;
			}

			if(!inv.isEmpty() && !ItemStack.areItemsEqual(stack, inv)){
				return stack;
			}

			int limit = Math.min(stack.getMaxStackSize() - inv.getCount(), stack.getCount());
			if(!simulate){
				if(inv.isEmpty()){
					inv = new ItemStack(stack.getItem(), limit, stack.getMetadata());
				}else{
					inv.grow(limit);
				}
				markDirty();
			}

			return stack.getCount() == limit ? ItemStack.EMPTY : new ItemStack(stack.getItem(), stack.getCount() - limit, stack.getMetadata());
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
}
