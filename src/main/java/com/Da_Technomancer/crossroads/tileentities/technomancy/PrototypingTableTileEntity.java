package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.CrossroadsProperties;
import com.Da_Technomancer.crossroads.API.packets.IStringReceiver;
import com.Da_Technomancer.crossroads.API.packets.CrossroadsPackets;
import com.Da_Technomancer.crossroads.API.packets.SendLogToClient;
import com.Da_Technomancer.crossroads.API.technomancy.IPrototypePort;
import com.Da_Technomancer.crossroads.API.technomancy.PrototypeInfo;
import com.Da_Technomancer.crossroads.API.technomancy.PrototypePortTypes;
import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.CrossroadsConfig;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.dimensions.ModDimensions;
import com.Da_Technomancer.crossroads.dimensions.PrototypeWorldSavedData;
import com.Da_Technomancer.crossroads.items.itemSets.OreSetup;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PrototypingTableTileEntity extends InventoryTE implements IStringReceiver{
	
	public boolean visible = false;

	public PrototypingTableTileEntity(){
		super(4);//0: Copshowium; 1: Template; 2: Output; 3: Prototype recycling slot
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
						copyTo.getWorld().setBlockState(newPos, fromWorld.getBlockState(oldPos), 16);
						TileEntity oldTe = fromWorld.getTileEntity(oldPos);
						if(oldTe != null){
							CompoundNBT nbt = new CompoundNBT();
							nbt = oldTe.writeToNBT(nbt).copy(); //Copied to prevent tile entities sharing instances of NBTTagCompound (can happen if nested).
							nbt.putInt("x", newPos.getX());
							nbt.putInt("y", newPos.getY());
							nbt.putInt("z", newPos.getZ());
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
						Crossroads.logger.error("Something went wrong while setting up a prototype. Error cloning block at " + oldPos.toString() + " in dimension " + fromWorld.provider.getDimension() + ". Errored prototype invalidated. Report to mod author, and disable prototyping that block type in the config. This errored gracefully.");
						Crossroads.logger.catching(e);
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
	public void receiveString(String context, String message, ServerPlayerEntity player){
		if(player == null || !context.equals("create") || message == null){
			return;
		}

		if(CrossroadsConfig.allowPrototype.getInt() == -1){
			CrossroadsPackets.network.sendTo(new SendLogToClient("prototypeCreate", "Prototyping disabled in config.", Color.YELLOW, false), player);
			return;
		}
		if(inventory[0].isEmpty() || inventory[0].getCount() < 3){
			CrossroadsPackets.network.sendTo(new SendLogToClient("prototypeCreate", "Insufficient inventory[0].", Color.YELLOW, false), player);
			return;
		}
		if(!inventory[2].isEmpty()){
			CrossroadsPackets.network.sendTo(new SendLogToClient("prototypeCreate", "Full inventory[2] slot.", Color.YELLOW, false), player);
			return;
		}

		//Copy or new prototype?
		if(!inventory[1].isEmpty()){
			//Sanity checks
			if(!(inventory[1].getItem() instanceof BlockItem) || ((BlockItem) inventory[1].getItem()).getBlock() != CrossroadsBlocks.prototype){
				CrossroadsPackets.network.sendTo(new SendLogToClient("prototypeCreate", "Invalid inventory[1] item.", Color.YELLOW, false), player);
				return;
			}
			if(CrossroadsConfig.allowPrototype.getInt() == 1){
				CrossroadsPackets.network.sendTo(new SendLogToClient("prototypeCreate", "Copying disabled in config.", Color.YELLOW, false), player);
				return;
			}

			PrototypeWorldSavedData data = PrototypeWorldSavedData.get(true);
			ArrayList<PrototypeInfo> infoList = data.prototypes;
			WorldServer dimWorld = DimensionManager.getWorld(ModDimensions.PROTOTYPE_DIM_ID);
			int index = inventory[1].getTag().getInt("index");
			//Sanity check
			if(infoList.size() < index + 1){
				inventory[1] = ItemStack.EMPTY;
				markDirty();
				CrossroadsPackets.network.sendTo(new SendLogToClient("prototypeCreate", "INVALID/BUGGED TEMPLATE! Removing.", Color.RED, false), player);
				return;
			}
			PrototypeInfo info = infoList.get(index);
			//Sanity check
			if(info == null){
				inventory[1] = ItemStack.EMPTY;
				markDirty();
				CrossroadsPackets.network.sendTo(new SendLogToClient("prototypeCreate", "INVALID/BUGGED TEMPLATE! Removing.", Color.RED, false), player);

				return;
			}



			List<String> blackList = Arrays.asList(CrossroadsConfig.blockedPrototype.getStringList());
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
							TileEntity teCheck = dimWorld.getTileEntity(pos);
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
						errors.add(new TemplateError(2, "Duplicate port: " + Direction.byIndex(i), port.getRight()));
					}
				}else if(ports[i].size() == 1){
					portArray[i] = ports[i].get(0).getLeft();
					posArray[i] = ports[i].get(0).getRight();
				}
			}

			//Even though regionValid should pass as the original already passed to be created, it should be checked again as the config may change.
			if(!errors.isEmpty()){
				for(TemplateError err : errors){
					Crossroads.logger.info(err.err + " at " + err.pos);
				}
				infoList.set(index, null);
				data.markDirty();
				inventory[1] = ItemStack.EMPTY;
				markDirty();
				CrossroadsPackets.network.sendTo(new SendLogToClient("prototypeCreate", "INVALID TEMPLATE! Removing.", Color.RED, false), player);
				return;
			}

			int newChunk = ModDimensions.nextFreePrototypeChunk(portArray, posArray);
			if(newChunk != -1){
				ChunkPos chunkPos = infoList.get(newChunk).chunk;
				if(setChunk(dimWorld.getChunk(chunkPos.x, chunkPos.z), dimWorld, info.chunk.getBlock(0, 16, 0), newChunk, false)){
					infoList.set(newChunk, null);
					data.markDirty();
					CrossroadsPackets.network.sendTo(new SendLogToClient("prototypeCreate", "ERROR! View logs for info.", Color.RED, false), player);
					return;
				}
				inventory[0].shrink(3);
				inventory[2] = inventory[1].copy();
				inventory[2].getTag().putInt("index", newChunk);
				inventory[2].getTag().putString("name", message);
				markDirty();
				CrossroadsPackets.network.sendTo(new SendLogToClient("prototypeCreate", "Prototype copied." , Color.WHITE, false), player);

			}else{
				CrossroadsPackets.network.sendTo(new SendLogToClient("prototypeCreate", "All " + ModDimensions.PROTOTYPE_LIMIT + " slots are used. Recycle for slots.", Color.YELLOW, false), player);
			}
		}else{
			List<String> blackList = Arrays.asList(CrossroadsConfig.blockedPrototype.getStringList());
			@SuppressWarnings("unchecked")
			List<Pair<PrototypePortTypes, BlockPos>>[] ports = new List[] {new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()};
			ArrayList<TemplateError> errors = new ArrayList<>();

			int startX = pos.getX();
			int startY = pos.getY() + 1;
			int startZ = pos.getZ();

			switch(world.getBlockState(pos).get(CrossroadsProperties.HORIZ_FACING)){
				case NORTH:
					startX -= 16;
					startZ -= 16;
					break;
				case SOUTH:
					startX += 1;
					startZ += 1;
					break;
				case EAST:
					startX -= 16;
					startZ += 1;
					break;
				case WEST:
					startX += 1;
					startZ -= 16;
					break;
				default:
			}

			int endX = startX + 16;
			int endY = startY + 16;
			int endZ = startZ + 16;

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
								ports[facing].add(Pair.of(port.getType(), pos.add(-startX, 16 - startY, -startZ)));
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
						errors.add(new TemplateError(2, "Duplicate port: " + Direction.byIndex(i), port.getRight()));
					}
				}else if(ports[i].size() == 1){
					portArray[i] = ports[i].get(0).getLeft();
					posArray[i] = ports[i].get(0).getRight();
				}
			}

			if(!errors.isEmpty()){
				for(TemplateError err : errors){
					err.trigger(world);
					CrossroadsPackets.network.sendTo(new SendLogToClient("prototypeCreate", err.err + " at ", err.sev == 1 ? Color.YELLOW : Color.RED, false), player);
					CrossroadsPackets.network.sendTo(new SendLogToClient("prototypeCreate", "{x: " + err.pos.getX() + ", Y: " + err.pos.getY() + ", Z: " + err.pos.getZ() + "}", err.sev == 1 ? Color.YELLOW : Color.RED, false), player);
				}
				return;
			}

			PrototypeWorldSavedData data = PrototypeWorldSavedData.get(true);
			ArrayList<PrototypeInfo> infoList = data.prototypes;
			WorldServer dimWorld = DimensionManager.getWorld(ModDimensions.PROTOTYPE_DIM_ID);

			int newChunk = ModDimensions.nextFreePrototypeChunk(portArray, posArray);
			if(newChunk != -1){
				ChunkPos chunkPos = infoList.get(newChunk).chunk;
				if(setChunk(dimWorld.getChunk(chunkPos.x, chunkPos.z), world, new BlockPos(startX, startY, startZ), newChunk, CrossroadsConfig.allowPrototype.getInt() == 1)){
					infoList.set(newChunk, null);
					data.markDirty();
					CrossroadsPackets.network.sendTo(new SendLogToClient("prototypeCreate", "ERROR! View server logs for info.", Color.RED, false), player);
					return;
				}
				inventory[0].shrink(3);
				inventory[2] = new ItemStack(CrossroadsBlocks.prototype, 1);
				inventory[2].put(new CompoundNBT());
				inventory[2].getTag().putInt("index", newChunk);
				inventory[2].getTag().putString("name", message);
				for(int i = 0; i < 6; i++){
					if(descArray[i] != null && !descArray[i].isEmpty()){
						inventory[2].getTag().putString("ttip" + i, descArray[i]);
					}
				}
				markDirty();
				CrossroadsPackets.network.sendTo(new SendLogToClient("prototypeCreate", "Prototype created." , Color.WHITE, false), player);
			}else{
				CrossroadsPackets.network.sendTo(new SendLogToClient("prototypeCreate", "All " + ModDimensions.PROTOTYPE_LIMIT + " slots are used. Recycle for slots.", Color.YELLOW, false), player);
			}
		}
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox(){
		return INFINITE_EXTENT_AABB;
	}

	private final ItemHandler itemHandler = new ItemHandler(null);

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, Direction facing){
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			return (T) itemHandler;
		}

		return super.getCapability(capability, facing);
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack){
		if(index >= 0 && index < 3){
			inventory[index] = stack;
			markDirty();
		}else if(index == 3 && stack.getItem() == Item.getItemFromBlock(CrossroadsBlocks.prototype)){
			markDirty();
			int ind = stack.getTag().contains("index") ? stack.getTag().getInt("index") : -1;
			if(ind == -1){
				inventory[0] = new ItemStack(OreSetup.ingotCopshowium, Math.min(inventory[0].getCount() + 3, 64));
				return;
			}
			if(!world.isRemote){
				PrototypeWorldSavedData data = PrototypeWorldSavedData.get(false);
				ArrayList<PrototypeInfo> infoList = data.prototypes;
				if(infoList.size() <= ind || infoList.get(ind) == null){
					inventory[0] = new ItemStack(OreSetup.ingotCopshowium, Math.min(inventory[0].getCount() + 3, 64));
					return;
				}
				int out = 3;
				inventory[0] = new ItemStack(OreSetup.ingotCopshowium, Math.min(inventory[0].getCount() + out, 64));
				infoList.set(ind, null);
				data.markDirty();
			}
		}
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack){
		return index == 0 && MiscUtil.hasOreDict(stack, "ingotCopshowium") || (index == 1 || index == 3) && stack.getItem() instanceof BlockItem && ((BlockItem) stack.getItem()).getBlock() == CrossroadsBlocks.prototype;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, Direction direction){
		return index == 2 && stack.getItem() instanceof BlockItem && ((BlockItem) stack.getItem()).getBlock() == CrossroadsBlocks.prototype;
	}

	@Override
	public String getName(){
		return "";
	}

	private static class TemplateError{

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
