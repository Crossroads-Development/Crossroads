package com.Da_Technomancer.crossroads.tileentities.technomancy;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.ModConfig;
import com.Da_Technomancer.crossroads.API.enums.PrototypePortTypes;
import com.Da_Technomancer.crossroads.API.gui.AbstractInventory;
import com.Da_Technomancer.crossroads.API.packets.IStringReceiver;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendLogToClient;
import com.Da_Technomancer.crossroads.API.technomancy.IPrototypePort;
import com.Da_Technomancer.crossroads.API.technomancy.PrototypeInfo;
import com.Da_Technomancer.crossroads.API.technomancy.PrototypeWorldSavedData;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.dimensions.ModDimensions;
import com.Da_Technomancer.crossroads.items.itemSets.OreSetUp;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class PrototypingTableTileEntity extends AbstractInventory implements IStringReceiver{

	private ItemStack copshowium = ItemStack.EMPTY;
	private ItemStack template = ItemStack.EMPTY;
	private ItemStack output = ItemStack.EMPTY;

	/** 
	 * @return Pair<PrototypePortTypes[], BlockPos[]> representing ports if it passed, BlockPos if it failed with the actual pos of what it failed on.
	 * 
	 * If multiple ports have the same side set, it passes with the last one taking precedence.
	 */
	private static Object regionValid(World fromWorld, BlockPos startPos, int lengthX, int lengthY, int lengthZ){
		List<String> blackList = Arrays.asList(ModConfig.blockedPrototype.getStringList());
		PrototypePortTypes[] ports = new PrototypePortTypes[6];
		BlockPos[] portPos = new BlockPos[6];

		for(int x = startPos.getX(); x < startPos.getX() + lengthX; x++){
			for(int z = startPos.getZ(); z < startPos.getZ() + lengthZ; z++){
				for(int y = startPos.getY(); y < lengthY + startPos.getY(); y++){
					BlockPos pos = new BlockPos(x, y, z);
					if(blackList.contains(fromWorld.getBlockState(pos).getBlock().getRegistryName().toString())){
						return pos;
					}else{
						TileEntity teCheck = fromWorld.getTileEntity(pos);
						if(teCheck instanceof IPrototypePort){
							IPrototypePort port = (IPrototypePort) teCheck;
							int facing = port.getSide().getIndex();
							ports[facing] = port.getType();
							portPos[facing] = new BlockPos(x - startPos.getX(), 16 + y - startPos.getY(), z - startPos.getZ());
						}
					}
				}
			}
		}
		return Pair.of(ports, portPos);
	}

	/**
	 * @return true if something went wrong (an exception was caught). In this case, the caller should cancel the operation.
	 */
	private static boolean setChunk(Chunk copyTo, World fromWorld, BlockPos startPos, int lengthX, int lengthY, int lengthZ, int index){
		for(int x = 0; x < lengthX; x++){
			for(int z = 0; z < lengthZ; z++){
				copyTo.setBlockState(new BlockPos(x, 15, z), Blocks.BARRIER.getDefaultState());
				copyTo.setBlockState(new BlockPos(x, 16 + lengthY, z), Blocks.BARRIER.getDefaultState());
				for(int y = 16; y < lengthY + 16; y++){
					BlockPos pos = new BlockPos(x, y, z);
					BlockPos oldPos = startPos.add(x, y - 16, z);
					try{
						copyTo.setBlockState(pos, fromWorld.getBlockState(oldPos));
						TileEntity oldTe = fromWorld.getTileEntity(oldPos);
						if(oldTe != null){
							NBTTagCompound nbt = new NBTTagCompound();
							oldTe.writeToNBT(nbt);
							BlockPos newPos = pos.add(copyTo.getPos().getBlock(0, 0, 0));
							nbt.setInteger("x", newPos.getX());
							nbt.setInteger("y", newPos.getY());
							nbt.setInteger("z", newPos.getZ());
							TileEntity newTe = copyTo.getWorld().getTileEntity(newPos);
							newTe.readFromNBT(nbt);
							if(newTe instanceof IPrototypePort){
								((IPrototypePort) newTe).makeActive();
								((IPrototypePort) newTe).setIndex(index);
							}
						}
					}catch(Exception e){
						Main.logger.error("Something went wrong while setting up a prototype. Error cloning block at " + oldPos.toString() + " in dimension " + fromWorld.provider.getDimension() + ". Errored prototype invalidated. Report to mod author, and disable prototyping that block type in the config. This errored gracefully.");
						Main.logger.catching(e);
						return true;
					}
				}
			}
		}


		copyTo.setModified(true);
		copyTo.checkLight();
		return false;
	}

	@Override
	public void receiveString(String context, String message, EntityPlayerMP player){
		if(context.equals("create") && message != null){
			if(!copshowium.isEmpty() && output.isEmpty() && ModConfig.allowPrototype.getInt() != -1){
				if(!template.isEmpty()){
					if(template.getItem() instanceof ItemBlock && ((ItemBlock) template.getItem()).getBlock() == ModBlocks.prototype && ModConfig.allowPrototype.getInt() != 1){
						WorldServer dimWorld = DimensionManager.getWorld(ModDimensions.PROTOTYPE_DIM_ID);
						ArrayList<PrototypeInfo> infoList = PrototypeWorldSavedData.get().prototypes;
						int index = template.getTagCompound().getInteger("index");
						if(infoList.size() < index + 1){
							template = ItemStack.EMPTY;
							markDirty();
							if(player != null){
								ModPackets.network.sendTo(new SendLogToClient("prototypeCreate", "INVALID/BUGGED TEMPLATE! Removing.", Color.RED, false), player);
							}
							return;
						}
						PrototypeInfo info = infoList.get(index);
						if(info == null){
							template = ItemStack.EMPTY;
							markDirty();
							if(player != null){
								ModPackets.network.sendTo(new SendLogToClient("prototypeCreate", "INVALID/BUGGED TEMPLATE! Removing.", Color.RED, false), player);
							}
							return;
						}
						int cost = 3 + info.getTotalPorts();
						if(cost < copshowium.getCount()){
							if(player != null){
								ModPackets.network.sendTo(new SendLogToClient("prototypeCreate", "Insufficient copshowium.", Color.YELLOW, false), player);
							}
							return;
						}
						//Even though regionValid should pass as the original already passed to be created, it should be checked again as the config may change.
						if(regionValid(dimWorld, info.chunk.getBlock(0, 16, 0), 16, 16, 16) instanceof BlockPos){
							infoList.set(index, null);
							PrototypeWorldSavedData.get().markDirty();
							template = ItemStack.EMPTY;
							markDirty();
							if(player != null){
								ModPackets.network.sendTo(new SendLogToClient("prototypeCreate", "INVALID TEMPLATE! Blame config changes. Removing.", Color.RED, false), player);
							}
							return;
						}
						int newChunk = ModDimensions.nextFreePrototypeChunk(info.ports, info.portPos);
						if(newChunk != -1){
							ChunkPos chunkPos = infoList.get(newChunk).chunk;
							if(setChunk(dimWorld.getChunkFromChunkCoords(chunkPos.chunkXPos, chunkPos.chunkZPos), dimWorld, info.chunk.getBlock(0, 16, 0), 16, 16, 16, newChunk)){
								infoList.set(newChunk, null);
								PrototypeWorldSavedData.get().markDirty();
								if(player != null){
									ModPackets.network.sendTo(new SendLogToClient("prototypeCreate", "ERROR WHILE COPYING! View logs for more info.", Color.RED, false), player);
								}
								return;
							}
							copshowium.shrink(cost);
							output = template.copy();
							output.getTagCompound().setInteger("index", newChunk);
							output.getTagCompound().setString("name", message);
							markDirty();
							if(player != null){
								ModPackets.network.sendTo(new SendLogToClient("prototypeCreate", "Prototype copied." , Color.WHITE, false), player);
							}
						}else{
							if(player != null){
								ModPackets.network.sendTo(new SendLogToClient("prototypeCreate", "All " + ModDimensions.PROTOTYPE_LIMIT + " slots are used. Recycle for slots.", Color.YELLOW, false), player);
							}
						}
					}else{
						return;
					}
				}else{
					Object validityCheck = regionValid(world, pos.add(1, 1, 1), 16, 16, 16);
					if(validityCheck instanceof BlockPos){
						if(player != null){
							ModPackets.network.sendTo(new SendLogToClient("prototypeCreate", "Blacklisted block at pos " + validityCheck.toString() + ".", Color.YELLOW, false), player);
						}
						return;
					}
					@SuppressWarnings("unchecked")
					Pair<PrototypePortTypes[], BlockPos[]> portInfo = (Pair<PrototypePortTypes[], BlockPos[]>) validityCheck;
					int cost = 3;
					for(PrototypePortTypes type : portInfo.getLeft()){
						if(type != null){
							cost++;
						}
					}
					if(cost > copshowium.getCount()){
						if(player != null){
							ModPackets.network.sendTo(new SendLogToClient("prototypeCreate", "Insufficient copshowium.", Color.YELLOW, false), player);
						}
						return;
					}
					ArrayList<PrototypeInfo> infoList = PrototypeWorldSavedData.get().prototypes;
					WorldServer dimWorld = DimensionManager.getWorld(ModDimensions.PROTOTYPE_DIM_ID);
					int newChunk = ModDimensions.nextFreePrototypeChunk(portInfo.getLeft(), portInfo.getRight());
					if(newChunk != -1){
						ChunkPos chunkPos = infoList.get(newChunk).chunk;
						if(setChunk(dimWorld.getChunkFromChunkCoords(chunkPos.chunkXPos, chunkPos.chunkZPos), world, pos.add(1, 1, 1), 16, 16, 16, newChunk)){
							infoList.set(newChunk, null);
							PrototypeWorldSavedData.get().markDirty();
							if(player != null){
								ModPackets.network.sendTo(new SendLogToClient("prototypeCreate", "ERROR WHILE COPYING! View server logs for more info.", Color.RED, false), player);
							}
							return;
						}
						copshowium.shrink(cost);
						output = new ItemStack(ModBlocks.prototype, 1);
						output.setTagCompound(new NBTTagCompound());
						output.getTagCompound().setInteger("index", newChunk);
						output.getTagCompound().setString("name", message);
						markDirty();
						if(player != null){
							ModPackets.network.sendTo(new SendLogToClient("prototypeCreate", "Prototype created." , Color.WHITE, false), player);
						}
					}else{
						if(player != null){
							ModPackets.network.sendTo(new SendLogToClient("prototypeCreate", "All " + ModDimensions.PROTOTYPE_LIMIT + " slots are used. Recycle for slots.", Color.YELLOW, false), player);
						}
					}
				}
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);

		if(nbt.hasKey("cop")){
			copshowium = new ItemStack(nbt.getCompoundTag("cop"));
		}
		if(nbt.hasKey("temp")){
			template = new ItemStack(nbt.getCompoundTag("temp"));
		}
		if(nbt.hasKey("out")){
			output = new ItemStack(nbt.getCompoundTag("out"));
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);

		if(!copshowium.isEmpty()){
			NBTTagCompound invTag = new NBTTagCompound();
			copshowium.writeToNBT(invTag);
			nbt.setTag("cop", invTag);
		}
		if(!template.isEmpty()){
			NBTTagCompound invTag = new NBTTagCompound();
			template.writeToNBT(invTag);
			nbt.setTag("temp", invTag);
		}
		if(!output.isEmpty()){
			NBTTagCompound invTag = new NBTTagCompound();
			output.writeToNBT(invTag);
			nbt.setTag("out", invTag);
		}
		return nbt;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing){
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			return true;
		}

		return super.hasCapability(capability, facing);
	}

	private final CopshowiumItemHandler copshowiumHandler = new CopshowiumItemHandler();
	private final OutputItemHandler outputHandler = new OutputItemHandler();

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing){
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			if(facing == EnumFacing.DOWN){
				return (T) outputHandler;
			}
			return (T) copshowiumHandler;
		}

		return super.getCapability(capability, facing);
	}

	@Override
	public int getSizeInventory(){
		//The fake fourth slot is for destroying prototypes
		return 4;
	}

	@Override
	public ItemStack getStackInSlot(int index){
		switch(index){
			case 0:
				return copshowium;
			case 1:
				return template;
			case 2:
				return output;
			default:
				return ItemStack.EMPTY;
		}
	}

	@Override
	public ItemStack decrStackSize(int index, int count){
		ItemStack slot = getStackInSlot(index);
		if(slot.isEmpty()){
			return ItemStack.EMPTY;
		}
		markDirty();
		return slot.splitStack(count);
	}

	@Override
	public ItemStack removeStackFromSlot(int index){
		ItemStack stack = ItemStack.EMPTY;
		if(index < 3 && index >= 0){
			markDirty();
			switch(index){
				case 0:
					stack = copshowium;
					copshowium = ItemStack.EMPTY;
					break;
				case 1:
					stack = template;
					template = ItemStack.EMPTY;
					break;
				case 2:
					stack = output;
					output = ItemStack.EMPTY;
					break;
			}
		}

		return stack;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack){
		if(index == 0){
			copshowium = stack;
			markDirty();
		}else if(index == 1){
			template = stack;
			markDirty();
		}else if(index == 2){
			output = stack;
			markDirty();
		}else if(index == 3 && stack.getItem() == Item.getItemFromBlock(ModBlocks.prototype)){
			markDirty();
			int ind = stack.getTagCompound().hasKey("index") ? stack.getTagCompound().getInteger("index") : -1;
			if(ind == -1){
				copshowium = new ItemStack(OreSetUp.ingotCopshowium, Math.min(copshowium.getCount() + 3, 64));
				return;
			}
			if(!world.isRemote){
				ArrayList<PrototypeInfo> infoList = PrototypeWorldSavedData.get().prototypes;
				if(infoList.size() <= ind || infoList.get(ind) == null){
					copshowium = new ItemStack(OreSetUp.ingotCopshowium, Math.min(copshowium.getCount() + 3, 64));
					return;
				}
				int out = 3;
				PrototypePortTypes[] types = infoList.get(ind).ports;
				for(int i = 0; i < 6; i++){
					if(types[i] != null){
						out++;
					}
				}
				copshowium = new ItemStack(OreSetUp.ingotCopshowium, Math.min(copshowium.getCount() + out, 64));
				infoList.set(ind, null);
				PrototypeWorldSavedData.get().markDirty();
			}
		}
	}

	@Override
	public int getInventoryStackLimit(){
		return 64;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack){
		return index == 0 && stack.getItem() == OreSetUp.ingotCopshowium;
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
		copshowium = ItemStack.EMPTY;
		template = ItemStack.EMPTY;
		output = ItemStack.EMPTY;
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side){
		return side == EnumFacing.DOWN ? new int[] {2} : new int[] {0};
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction){
		return direction != EnumFacing.DOWN && isItemValidForSlot(index, itemStackIn);
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction){
		return direction == EnumFacing.DOWN && stack.getItem() instanceof ItemBlock && ((ItemBlock) stack.getItem()).getBlock() == ModBlocks.prototype;
	}

	@Override
	public String getName(){
		return "container.prototype_table";
	}

	@Override
	public boolean isEmpty(){
		//This is used for automated item transport, so it doesn't need to know about template.
		return copshowium.isEmpty() && output.isEmpty();
	}

	private class CopshowiumItemHandler implements IItemHandler{

		@Override
		public int getSlots(){
			return 1;
		}

		@Override
		public ItemStack getStackInSlot(int slot){
			return slot == 0 ? copshowium : ItemStack.EMPTY;
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate){
			if(slot == 0 && stack.getItem() == OreSetUp.ingotCopshowium && copshowium.getCount() < 64){
				ItemStack out = new ItemStack(stack.getItem(), stack.getCount() + copshowium.getCount() - 64);
				if(!simulate){
					copshowium = new ItemStack(stack.getItem(), Math.min(copshowium.getCount() + stack.getCount(), 64));
					markDirty();
				}
				if(out.getCount() <= 0){
					return ItemStack.EMPTY;
				}
				return out;
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

	private class OutputItemHandler implements IItemHandler{

		@Override
		public int getSlots(){
			return 1;
		}

		@Override
		public ItemStack getStackInSlot(int slot){
			return slot == 0 ? output : ItemStack.EMPTY;
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate){
			return stack;
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate){
			if(slot != 0 || amount <= 0 || output.isEmpty()){
				return ItemStack.EMPTY;
			}

			ItemStack extracted = output.copy();

			if(!simulate){
				output = ItemStack.EMPTY;
				markDirty();
			}

			return extracted;
		}

		@Override
		public int getSlotLimit(int slot){
			return slot == 0 ? 1 : 0;
		}
	}
}
