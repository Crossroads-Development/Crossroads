package com.Da_Technomancer.crossroads.tileentities.beams;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.beams.*;
import com.Da_Technomancer.crossroads.API.packets.*;
import com.Da_Technomancer.crossroads.API.templates.IBeamRenderTE;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.crafting.CRItemTags;
import com.Da_Technomancer.crossroads.crafting.CRRecipes;
import com.Da_Technomancer.crossroads.crafting.recipes.BeamLensRec;
import com.Da_Technomancer.crossroads.crafting.recipes.BeamTransmuteRec;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.itemSets.OreSetup;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import com.Da_Technomancer.essentials.packets.INBTReceiver;
import com.Da_Technomancer.essentials.packets.SendNBTToClient;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.AxisDirection;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

@ObjectHolder(Crossroads.MODID)
public class LensFrameTileEntity extends TileEntity implements IBeamRenderTE, IIntReceiver, INBTReceiver, IInventory {

	@ObjectHolder("lens_frame")
	public static TileEntityType<LensFrameTileEntity> type = null;

	private int packetNeg;
	private int packetPos;
	private ItemStack inv = ItemStack.EMPTY;
	private Direction.Axis axis = null;
	private BeamUnit prevMag = BeamUnit.EMPTY;
	private BeamLensRec currRec;
	private int lastRedstone;

	public LensFrameTileEntity(){
		super(type);
	}

	private Direction.Axis getAxis(){
		if(axis == null){
			BlockState state = level.getBlockState(worldPosition);
			if(state.getBlock() != CRBlocks.lensFrame){
				return Direction.Axis.X;
			}
			axis = state.getValue(ESProperties.AXIS);
		}

		return axis;
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox(){
		return INFINITE_EXTENT_AABB;
	}

	@Override
	public void clearCache(){
		super.clearCache();
		if(beamer[1] != null && level != null){
			beamer[1].emit(BeamUnit.EMPTY, level);
			refreshBeam(true);
		}
		if(beamer[0] != null && level != null){
			beamer[0].emit(BeamUnit.EMPTY, level);
			refreshBeam(false);
		}
		axis = null;
		magicOpt.invalidate();
		magicOptNeg.invalidate();
		magicOpt = LazyOptional.of(() -> new BeamHandler(AxisDirection.NEGATIVE));
		magicOptNeg = LazyOptional.of(() -> new BeamHandler(AxisDirection.POSITIVE));

		if(level != null && !level.isClientSide){
			CRPackets.sendPacketAround(level, worldPosition, new SendIntToClient((byte)3, 0, worldPosition));
		}
	}

	private void refreshBeam(boolean positive){
		int index = positive ? 1 : 0;
		int packet = beamer[index].genPacket();
		if(positive){
			packetPos = packet;
		}else{
			packetNeg = packet;
		}
		CRPackets.sendPacketAround(level, worldPosition, new SendIntToClient((byte)index, packet, worldPosition));
		if(!beamer[index].getLastSent().isEmpty()){
			prevMag = beamer[index].getLastSent();
		}
	}

	@Override
	@Nullable
	public BeamUnit[] getLastSent(){
		return new BeamUnit[]{prevMag};
	}

	public BeamLensRec getCurrRec() {
		return currRec;
	}

	public int getRedstone(){
		return lastRedstone;
	}

	@Override
	public int[] getRenderedBeams(){
		int[] out = new int[6];
		out[Direction.get(AxisDirection.POSITIVE, getAxis()).get3DDataValue()] = packetPos;
		out[Direction.get(AxisDirection.NEGATIVE, getAxis()).get3DDataValue()] = packetNeg;
		return out;
	}

	private final BeamManager[] beamer = new BeamManager[2];//0: neg; 1: pos

	@Override
	public void receiveInt(byte identifier, int message, ServerPlayerEntity player){
		switch(identifier){
			case 0:
				packetNeg = message;
				break;
			case 1:
				packetPos = message;
				break;
			case 3:
				axis = null;
				break;
		}
	}

	@Override
	public CompoundNBT getUpdateTag(){
		CompoundNBT nbt = super.getUpdateTag();
		nbt.putInt("beam_neg", packetNeg);
		nbt.putInt("beam_pos", packetPos);
		if(!inv.isEmpty()){
			nbt.put("inv", inv.save(new CompoundNBT()));
		}
		return nbt;
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt){
		super.save(nbt);
		nbt.putInt("beam_neg", packetNeg);
		nbt.putInt("beam_pos", packetPos);
		nbt.putInt("reds", lastRedstone);
		if(!inv.isEmpty()){
			nbt.put("inv", inv.save(new CompoundNBT()));
		}
		return nbt;
	}

	@Override
	public void load(BlockState state, CompoundNBT nbt){
		super.load(state, nbt);
		packetPos = nbt.getInt("beam_pos");
		packetNeg = nbt.getInt("beam_neg");
		lastRedstone = nbt.getInt("reds");
		inv = nbt.contains("inv") ? ItemStack.of(nbt.getCompound("inv")) : ItemStack.EMPTY;
	}

	@Override
	public void setRemoved(){
		super.setRemoved();
		magicOpt.invalidate();
		magicOptNeg.invalidate();
		lensOpt.invalidate();
		if(beamer != null && level != null){
			for(BeamManager manager : beamer){
				if(manager != null){
					manager.emit(BeamUnit.EMPTY, level);
				}
			}
		}
	}

	private LazyOptional<IBeamHandler> magicOpt = LazyOptional.of(() -> new BeamHandler(AxisDirection.NEGATIVE));
	private LazyOptional<IBeamHandler> magicOptNeg = LazyOptional.of(() -> new BeamHandler(AxisDirection.POSITIVE));
	private final LazyOptional<IItemHandler> lensOpt = LazyOptional.of(LensHandler::new);

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side){
		if(cap == Capabilities.BEAM_CAPABILITY && (side == null || getAxis() == side.getAxis())){
			return side == null || side.getAxisDirection() == AxisDirection.POSITIVE ? (LazyOptional<T>)magicOpt : (LazyOptional<T>)magicOptNeg;
		}
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			return (LazyOptional<T>)lensOpt;
		}

		return super.getCapability(cap, side);
	}

	@Override
	public int getContainerSize(){
		return 1;
	}

	@Override
	public boolean isEmpty(){
		return inv.isEmpty();
	}

	@Override
	public ItemStack getItem(int index){
		return index == 0 ? inv : ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeItem(int index, int count){
		if(index == 0){
			setChanged();
			return inv.split(count);
		}
		return ItemStack.EMPTY;
	}

	@Override
	public void setChanged(){
		super.setChanged();
		CRPackets.sendPacketAround(level, worldPosition, new SendNBTToClient(inv.save(new CompoundNBT()), this.getBlockPos()));
		updateRecipe();
	}

	@Override
	public ItemStack removeItemNoUpdate(int index){
		if(index != 0){
			return ItemStack.EMPTY;
		}
		setChanged();
		ItemStack held = inv;
		inv = ItemStack.EMPTY;
		return held;
	}

	@Override
	public void setItem(int index, ItemStack stack){
		if(index == 0){
			inv = stack;
			setChanged();
		}
	}

	@Override
	public boolean stillValid(PlayerEntity p_70300_1_){
		// Normally this detects whether the player is close enough to still access the inventory
		// However this is not an inventory with a UI so we don't care
		return false;
	}

	@Override
	public void clearContent(){
		inv = ItemStack.EMPTY;
		setChanged();
	}

	@Override
	public void receiveNBT(CompoundNBT nbt, ServerPlayerEntity serverPlayer){
		inv = ItemStack.of(nbt);
		updateRecipe();
	}

	public void updateRecipe() {
		Optional<BeamLensRec> rec = level.getRecipeManager().getRecipeFor(CRRecipes.BEAM_LENS_TYPE, this, level);
		currRec = rec.isPresent() ? rec.get() : null;
	}

	private class BeamHandler implements IBeamHandler {

		private final AxisDirection dir;

		private BeamHandler(AxisDirection dir){
			this.dir = dir;
		}

		@Override
		public void setBeam(@Nonnull BeamUnit mag){
			if(beamer[0] == null || beamer[1] == null){
				beamer[0] = new BeamManager(Direction.get(AxisDirection.NEGATIVE, getAxis()), worldPosition);
				beamer[1] = new BeamManager(Direction.get(AxisDirection.POSITIVE, getAxis()), worldPosition);
			}

			BeamManager activeBeam = beamer[dir == AxisDirection.POSITIVE ? 1 : 0];
			BeamLensRec recipe = ((LensFrameTileEntity)getTileEntity()).getCurrRec();
			BeamMod mod = BeamMod.EMPTY;

			if(recipe == null && !getItem(0).isEmpty()) {
				updateRecipe();
			}

			if(recipe != null){
				// TODO: Proper support for lens transmutation
				if(EnumBeamAlignments.getAlignment(mag) == EnumBeamAlignments.LIGHT){
					setItem(0, recipe.getResultItem());
				}
				mod = recipe.getOutput();
			}

			if(activeBeam.emit(mod.mult(mag), level)){
				refreshBeam(dir == AxisDirection.POSITIVE);
			}

			lastRedstone = Math.max(beamer[0].getLastSent().getPower(), beamer[1].getLastSent().getPower());
			setChanged();
		}
	}

	private class LensHandler implements IItemHandler {

		@Override
		public int getSlots(){
			return getContainerSize();
		}

		@Override
		public ItemStack getStackInSlot(int slot){
			return getItem(slot);
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate){
			if(slot != 0 || inv.isEmpty() || !isItemValid(0, stack)){
				return stack;
			}

			ItemStack insStack = stack.getCount() - 1 <= 0 ? ItemStack.EMPTY : new ItemStack(stack.getItem(), stack.getCount() - 1);
			if(!simulate){
				inv = insStack;
			}

			return insStack;
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate){
			if(slot != 0 || amount < 1 || inv.isEmpty()){
				return ItemStack.EMPTY;
			}
			ItemStack toOutput = inv;
			if(!simulate){
				inv = ItemStack.EMPTY;
			}
			return toOutput;
		}

		@Override
		public int getSlotLimit(int slot){
			return slot == 0 ? 1 : 0;
		}

		@Override
		public boolean isItemValid(int slot, @Nonnull ItemStack stack){
			return slot == 0 && getLevel()
					.getRecipeManager()
					.getRecipeFor(CRRecipes.BEAM_LENS_TYPE, new Inventory(stack), level)
					.isPresent();
		}
	}
}
