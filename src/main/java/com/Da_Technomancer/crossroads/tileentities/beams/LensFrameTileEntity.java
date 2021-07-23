package com.Da_Technomancer.crossroads.tileentities.beams;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.beams.*;
import com.Da_Technomancer.crossroads.API.packets.CRPackets;
import com.Da_Technomancer.crossroads.API.packets.IIntReceiver;
import com.Da_Technomancer.crossroads.API.packets.SendIntToClient;
import com.Da_Technomancer.crossroads.API.templates.IBeamRenderTE;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.crafting.CRRecipes;
import com.Da_Technomancer.crossroads.crafting.recipes.BeamLensRec;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.itemSets.OreSetup;
import com.Da_Technomancer.essentials.blocks.BlockUtil;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import com.Da_Technomancer.essentials.packets.INBTReceiver;
import com.Da_Technomancer.essentials.packets.SendNBTToClient;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.AxisDirection;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

@ObjectHolder(Crossroads.MODID)
public class LensFrameTileEntity extends TileEntity implements IBeamRenderTE, IIntReceiver, INBTReceiver, IInventoryChangedListener{

	@ObjectHolder("lens_frame")
	public static TileEntityType<LensFrameTileEntity> type = null;

	private int packetNeg;
	private int packetPos;
	private final Inventory inventoryWrapper = new Inventory(1);
	private Direction.Axis axis = null;
	private BeamUnit prevMag = BeamUnit.EMPTY;
	private BeamLensRec currRec;
	private boolean recipeCheck;
	private int lastRedstone;

	public LensFrameTileEntity(){
		super(type);
		inventoryWrapper.addListener(this);
	}

	private Direction.Axis getAxis(){
		if(axis == null){
			BlockState state = getBlockState();
			if(state.getBlock() != CRBlocks.lensFrame){
				return Direction.Axis.X;
			}
			axis = state.getValue(ESProperties.AXIS);
		}

		return axis;
	}

	public ItemStack getLensItem(){
		return inventoryWrapper.getItem(0);
	}

	public void setLensItem(ItemStack lens){
		inventoryWrapper.setItem(0, lens);
		if(level != null && !level.isClientSide){
			//Update on the client
			CRPackets.sendPacketAround(level, worldPosition, new SendNBTToClient(lens.save(new CompoundNBT()), worldPosition));
		}
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

	@Nullable
	public BeamLensRec getCurrRec() {
		if(!recipeCheck){
			updateRecipe();
		}
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
		ItemStack lensItem = getLensItem();
		if(!lensItem.isEmpty()){
			nbt.put("inv", lensItem.save(new CompoundNBT()));
		}
		return nbt;
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt){
		super.save(nbt);
		nbt.putInt("beam_neg", packetNeg);
		nbt.putInt("beam_pos", packetPos);
		nbt.putInt("reds", lastRedstone);
		ItemStack lensItem = getLensItem();
		if(!lensItem.isEmpty()){
			nbt.put("inv", lensItem.save(new CompoundNBT()));
		}
		return nbt;
	}

	@Override
	public void load(BlockState state, CompoundNBT nbt){
		super.load(state, nbt);
		packetPos = nbt.getInt("beam_pos");
		packetNeg = nbt.getInt("beam_neg");
		lastRedstone = nbt.getInt("reds");
		if(nbt.contains("inv")){
			setLensItem(ItemStack.of(nbt.getCompound("inv")));
		}else if(nbt.contains("contents")){
			// Load from legacy data
			Item item = null;
			switch(nbt.getInt("contents")){
				case 1:
					item = OreSetup.gemRuby;
					break;
				case 2:
					item = Items.EMERALD;
					break;
				case 3:
					item = Items.DIAMOND;
					break;
				case 4:
					item = CRItems.pureQuartz;
					break;
				case 5:
					item = CRItems.brightQuartz;
					break;
				case 6:
					item = OreSetup.voidCrystal;
					break;
			}
			setLensItem(item != null ? new ItemStack(item) : ItemStack.EMPTY);
		}else{
			setLensItem(ItemStack.EMPTY);
		}
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
	public void setChanged(){
		super.setChanged();
	}

	@Override
	public void receiveNBT(CompoundNBT nbt, ServerPlayerEntity serverPlayer){
		setLensItem(ItemStack.of(nbt));
	}

	public void updateRecipe() {
		Optional<BeamLensRec> rec = level.getRecipeManager().getRecipeFor(CRRecipes.BEAM_LENS_TYPE, inventoryWrapper, level);
		currRec = rec.orElse(null);
		recipeCheck = true;
	}

	@Override
	public void containerChanged(IInventory changedInv){
		setChanged();
		recipeCheck = false;
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
			BeamLensRec recipe = getCurrRec();
			BeamMod mod = BeamMod.EMPTY;

			if(recipe != null){
				if(!mag.isEmpty() && EnumBeamAlignments.getAlignment(mag) == recipe.getTransmuteAlignment() && (recipe.isVoid() == (mag.getVoid() > 0))){
					setLensItem(recipe.assemble(inventoryWrapper));
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
			return 1;
		}

		@Nonnull
		@Override
		public ItemStack getStackInSlot(int slot){
			return slot == 0 ? inventoryWrapper.getItem(slot) : ItemStack.EMPTY;
		}

		@Nonnull
		@Override
		public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate){
			ItemStack lensItem = getLensItem();
			if(isItemValid(slot, stack) && (lensItem.isEmpty() || BlockUtil.sameItem(stack, lensItem))){
				int oldCount = lensItem.getCount();
				int moved = Math.min(stack.getCount(), Math.min(stack.getMaxStackSize(), getSlotLimit(slot)) - oldCount);
				ItemStack out = stack.copy();
				out.setCount(stack.getCount() - moved);

				if(!simulate){
					setChanged();
					lensItem = stack.copy();
					lensItem.setCount(moved + oldCount);
					setLensItem(lensItem);
				}
				return out;
			}else{
				return stack;
			}
		}

		@Nonnull
		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate){
			if(slot >= 1){
				return ItemStack.EMPTY;
			}
			ItemStack lensItem = getLensItem();
			int moved = Math.min(amount, lensItem.getCount());
			if(simulate){
				ItemStack simOut = lensItem.copy();
				simOut.setCount(moved);
				return simOut;
			}
			setChanged();
			ItemStack out = lensItem.split(moved);
			setLensItem(lensItem);
			return out;
		}

		@Override
		public int getSlotLimit(int slot){
			return 1;
		}

		@Override
		public boolean isItemValid(int slot, @Nonnull ItemStack stack){
			return slot == 0 && getLevel().getRecipeManager().getRecipeFor(CRRecipes.BEAM_LENS_TYPE, new Inventory(stack), level).isPresent();
		}
	}
}
