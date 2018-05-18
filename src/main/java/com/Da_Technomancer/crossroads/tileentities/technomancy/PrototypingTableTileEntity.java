package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.gui.AbstractInventory;
import com.Da_Technomancer.crossroads.API.packets.IStringReceiver;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendLogToClient;
import com.Da_Technomancer.crossroads.API.technomancy.IPrototypePort;
import com.Da_Technomancer.crossroads.API.technomancy.PrototypeInfo;
import com.Da_Technomancer.crossroads.API.technomancy.PrototypePortTypes;
import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.ModConfig;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.dimensions.ModDimensions;
import com.Da_Technomancer.crossroads.dimensions.PrototypeWorldSavedData;
import com.Da_Technomancer.crossroads.items.itemSets.OreSetup;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PrototypingTableTileEntity extends AbstractInventory implements IStringReceiver{

	private ItemStack copshowium = ItemStack.EMPTY;
	private ItemStack template = ItemStack.EMPTY;
	private ItemStack output = ItemStack.EMPTY;
	public boolean visible = false;

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState){
		return oldState.getBlock() != newState.getBlock();
	}

	public void dropItems(){
		InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), copshowium);
		InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), template);
		InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), output);
		copshowium = ItemStack.EMPTY;
		template = ItemStack.EMPTY;
		output = ItemStack.EMPTY;
		markDirty();
	}

	/**
	 * @return true if something went wrong (an exception was caught). In this case, the caller should cancel the operation.
	 */
	private static boolean setChunk(Chunk copyTo, World fromWorld, BlockPos startPos, int index, boolean wipeOld){
		for(int x = 0; x < 16; x++){
			for(int z = 0; z < 16; z++){
				copyTo.setBlockState(new BlockPos(x, 15, z), Blocks.BARRIER.getDefaultState());
				copyTo.setBlockState(new BlockPos(x, 32, z), Blocks.BARRIER.getDefaultState());
				for(int y = 16; y < 32; y++){
					BlockPos oldPos = startPos.add(x, y - 16, z);
					try{
						BlockPos newPos = copyTo.getPos().getBlock(x, y, z);
						copyTo.getWorld().setBlockState(newPos, fromWorld.getBlockState(oldPos), 0);
						TileEntity oldTe = fromWorld.getTileEntity(oldPos);
						if(oldTe != null){
							NBTTagCompound nbt = new NBTTagCompound();
							nbt = oldTe.writeToNBT(nbt).copy(); //Copied to prevent tile entities sharing instances of NBTTagCompound (can happen if nested).
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
						if(wipeOld){
							fromWorld.setBlockState(oldPos, Blocks.AIR.getDefaultState());
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
		if(player == null || !context.equals("create") || message == null){
			return;
		}

		if(ModConfig.allowPrototype.getInt() == -1){
			ModPackets.network.sendTo(new SendLogToClient("prototypeCreate", "Prototyping disabled in config.", Color.YELLOW, false), player);
			return;
		}
		if(copshowium.isEmpty() || copshowium.getCount() < 3){
			ModPackets.network.sendTo(new SendLogToClient("prototypeCreate", "Insufficient copshowium.", Color.YELLOW, false), player);
			return;
		}
		if(!output.isEmpty()){
			ModPackets.network.sendTo(new SendLogToClient("prototypeCreate", "Full output slot.", Color.YELLOW, false), player);
			return;
		}

		//Copy or new prototype?
		if(!template.isEmpty()){
			//Sanity checks
			if(!(template.getItem() instanceof ItemBlock) || ((ItemBlock) template.getItem()).getBlock() != ModBlocks.prototype){
				ModPackets.network.sendTo(new SendLogToClient("prototypeCreate", "Invalid template item.", Color.YELLOW, false), player);
				return;
			}
			if(ModConfig.allowPrototype.getInt() == 1){
				ModPackets.network.sendTo(new SendLogToClient("prototypeCreate", "Copying disabled in config.", Color.YELLOW, false), player);
				return;
			}

			PrototypeWorldSavedData data = PrototypeWorldSavedData.get(true);
			ArrayList<PrototypeInfo> infoList = data.prototypes;
			WorldServer dimWorld = DimensionManager.getWorld(ModDimensions.PROTOTYPE_DIM_ID);
			int index = template.getTagCompound().getInteger("index");
			//Sanity check
			if(infoList.size() < index + 1){
				template = ItemStack.EMPTY;
				markDirty();
				ModPackets.network.sendTo(new SendLogToClient("prototypeCreate", "INVALID/BUGGED TEMPLATE! Removing.", Color.RED, false), player);
				return;
			}
			PrototypeInfo info = infoList.get(index);
			//Sanity check
			if(info == null){
				template = ItemStack.EMPTY;
				markDirty();
				ModPackets.network.sendTo(new SendLogToClient("prototypeCreate", "INVALID/BUGGED TEMPLATE! Removing.", Color.RED, false), player);

				return;
			}



			List<String> blackList = Arrays.asList(ModConfig.blockedPrototype.getStringList());
			@SuppressWarnings("unchecked")
			List<Pair<PrototypePortTypes, BlockPos>>[] ports = new List[] {new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()};
			ArrayList<TemplateError> errors = new ArrayList<>();

			for(int x = 0; x < 16; x++){
				for(int z = 0; z < 16; z++){
					for(int y = 16; y < 32; y++){
						BlockPos pos = info.chunk.getBlock(x, y, z);
						if(blackList.contains(dimWorld.getBlockState(pos).getBlock().getRegistryName().toString())){
							errors.add(new TemplateError(0, "Illegal Block", pos));
						}else{
							TileEntity teCheck = world.getTileEntity(pos);
							if(teCheck instanceof IPrototypePort){
								IPrototypePort port = (IPrototypePort) teCheck;
								int facing = port.getSide().getIndex();
								ports[facing].add(Pair.of(port.getType(), pos.add(0, -16, 0)));
							}
						}
					}
				}
			}

			PrototypePortTypes[] portArray = new PrototypePortTypes[6];
			BlockPos[] posArray = new BlockPos[6];

			for(int i = 0; i < 6; i++){
				if(ports[i].size() > 1){
					for(Pair<PrototypePortTypes, BlockPos> port : ports[i]){
						errors.add(new TemplateError(2, "Duplicate port: " + EnumFacing.getFront(i), port.getRight()));
					}
				}else if(ports[i].size() == 1){
					portArray[i] = ports[i].get(0).getLeft();
					posArray[i] = ports[i].get(0).getRight();
				}
			}

			//Even though regionValid should pass as the original already passed to be created, it should be checked again as the config may change.
			if(!errors.isEmpty()){
				for(TemplateError err : errors){
					Main.logger.info(err.err + " at " + err.pos);
				}
				infoList.set(index, null);
				data.markDirty();
				template = ItemStack.EMPTY;
				markDirty();
				ModPackets.network.sendTo(new SendLogToClient("prototypeCreate", "INVALID TEMPLATE! Removing.", Color.RED, false), player);
				return;
			}

			int newChunk = ModDimensions.nextFreePrototypeChunk(portArray, posArray);
			if(newChunk != -1){
				ChunkPos chunkPos = infoList.get(newChunk).chunk;
				if(setChunk(dimWorld.getChunkFromChunkCoords(chunkPos.x, chunkPos.z), dimWorld, info.chunk.getBlock(0, 16, 0), newChunk, false)){
					infoList.set(newChunk, null);
					data.markDirty();
					ModPackets.network.sendTo(new SendLogToClient("prototypeCreate", "ERROR! View logs for info.", Color.RED, false), player);
					return;
				}
				copshowium.shrink(3);
				output = template.copy();
				output.getTagCompound().setInteger("index", newChunk);
				output.getTagCompound().setString("name", message);
				markDirty();
				ModPackets.network.sendTo(new SendLogToClient("prototypeCreate", "Prototype copied." , Color.WHITE, false), player);

			}else{
				ModPackets.network.sendTo(new SendLogToClient("prototypeCreate", "All " + ModDimensions.PROTOTYPE_LIMIT + " slots are used. Recycle for slots.", Color.YELLOW, false), player);
			}
		}else{
			List<String> blackList = Arrays.asList(ModConfig.blockedPrototype.getStringList());
			@SuppressWarnings("unchecked")
			List<Pair<PrototypePortTypes, BlockPos>>[] ports = new List[] {new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()};
			ArrayList<TemplateError> errors = new ArrayList<>();

			int startY = pos.getY() + 1;
			int endY = pos.getY() + 17;
			int startX = pos.getX();
			int startZ = pos.getZ();
			int endX = pos.getX();
			int endZ = pos.getZ();

			switch(world.getBlockState(pos).getValue(Properties.HORIZONTAL_FACING)){
				case NORTH:
					endX -= 1;
					startX -= 17;
					startZ -= 17;
					endZ -= 1;
					break;
				case SOUTH:
					startX += 1;
					endX += 17;
					startZ += 1;
					endZ += 17;
					break;
				case EAST:
					startX -= 17;
					endX -= 1;
					startZ += 1;
					endZ += 17;
					break;
				case WEST:
					startX += 1;
					endX += 17;
					startZ -= 17;
					endZ -= 1;
					break;
				default:
			}

			String[] descArray = new String[6];

			for(int x = startX; x < endX; x++){
				for(int z = startZ; z < endZ; z++){
					for(int y = startY; y < endY; y++){
						BlockPos pos = new BlockPos(x, y, z);
						if(blackList.contains(world.getBlockState(pos).getBlock().getRegistryName().toString())){
							errors.add(new TemplateError(0, "Illegal Block", pos));
						}else{
							TileEntity teCheck = world.getTileEntity(pos);
							if(teCheck instanceof IPrototypePort){
								IPrototypePort port = (IPrototypePort) teCheck;
								int facing = port.getSide().getIndex();
								ports[facing].add(Pair.of(port.getType(), pos.add(-startX, -startY, -startZ)));
								descArray[port.getSide().getIndex()] = port.getDesc();
							}
						}
					}
				}
			}

			PrototypePortTypes[] portArray = new PrototypePortTypes[6];
			BlockPos[] posArray = new BlockPos[6];

			for(int i = 0; i < 6; i++){
				if(ports[i].size() > 1){
					for(Pair<PrototypePortTypes, BlockPos> port : ports[i]){
						errors.add(new TemplateError(2, "Duplicate port: " + EnumFacing.getFront(i), port.getRight()));
					}
				}else if(ports[i].size() == 1){
					portArray[i] = ports[i].get(0).getLeft();
					posArray[i] = ports[i].get(0).getRight();
				}
			}

			if(!errors.isEmpty()){
				for(TemplateError err : errors){
					err.trigger(world);
					ModPackets.network.sendTo(new SendLogToClient("prototypeCreate", err.err + " at " + err.pos, err.sev == 1 ? Color.YELLOW : Color.RED, false), player);
				}
				return;
			}

			PrototypeWorldSavedData data = PrototypeWorldSavedData.get(true);
			ArrayList<PrototypeInfo> infoList = data.prototypes;
			WorldServer dimWorld = DimensionManager.getWorld(ModDimensions.PROTOTYPE_DIM_ID);

			int newChunk = ModDimensions.nextFreePrototypeChunk(portArray, posArray);
			if(newChunk != -1){
				ChunkPos chunkPos = infoList.get(newChunk).chunk;
				if(setChunk(dimWorld.getChunkFromChunkCoords(chunkPos.x, chunkPos.z), world, new BlockPos(startX, startY, startZ), newChunk, ModConfig.allowPrototype.getInt() == 1)){
					infoList.set(newChunk, null);
					data.markDirty();
					ModPackets.network.sendTo(new SendLogToClient("prototypeCreate", "ERROR! View server logs for info.", Color.RED, false), player);
					return;
				}
				copshowium.shrink(3);
				output = new ItemStack(ModBlocks.prototype, 1);
				output.setTagCompound(new NBTTagCompound());
				output.getTagCompound().setInteger("index", newChunk);
				output.getTagCompound().setString("name", message);
				for(int i = 0; i < 6; i++){
					if(descArray[i] != null && !descArray[i].isEmpty()){
						output.getTagCompound().setString("ttip" + i, descArray[i]);
					}
				}
				markDirty();
				ModPackets.network.sendTo(new SendLogToClient("prototypeCreate", "Prototype created." , Color.WHITE, false), player);
			}else{
				ModPackets.network.sendTo(new SendLogToClient("prototypeCreate", "All " + ModDimensions.PROTOTYPE_LIMIT + " slots are used. Recycle for slots.", Color.YELLOW, false), player);
			}
		}
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox(){
		return INFINITE_EXTENT_AABB;
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
				copshowium = new ItemStack(OreSetup.ingotCopshowium, Math.min(copshowium.getCount() + 3, 64));
				return;
			}
			if(!world.isRemote){
				PrototypeWorldSavedData data = PrototypeWorldSavedData.get(false);
				ArrayList<PrototypeInfo> infoList = data.prototypes;
				if(infoList.size() <= ind || infoList.get(ind) == null){
					copshowium = new ItemStack(OreSetup.ingotCopshowium, Math.min(copshowium.getCount() + 3, 64));
					return;
				}
				int out = 3;
				copshowium = new ItemStack(OreSetup.ingotCopshowium, Math.min(copshowium.getCount() + out, 64));
				infoList.set(ind, null);
				data.markDirty();
			}
		}
	}

	@Override
	public int getInventoryStackLimit(){
		return 64;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack){
		return index == 0 && stack.getItem() == OreSetup.ingotCopshowium;
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
			if(slot == 0 && stack.getItem() == OreSetup.ingotCopshowium && copshowium.getCount() < 64){
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

	private class TemplateError{

		private final int sev;
		private final String err;
		private final BlockPos pos;

		public TemplateError(int severity, String error, BlockPos pos){
			sev = severity;
			err = error;
			this.pos = pos;
		}

		public void trigger(World w){
			w.createExplosion(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0, false);
		}
	}
}
