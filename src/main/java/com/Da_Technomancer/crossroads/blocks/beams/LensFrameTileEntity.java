package com.Da_Technomancer.crossroads.blocks.beams;

import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.api.beams.*;
import com.Da_Technomancer.crossroads.api.packets.CRPackets;
import com.Da_Technomancer.crossroads.api.templates.BeamRenderTE;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.crossroads.crafting.BeamLensRec;
import com.Da_Technomancer.crossroads.crafting.CRRecipes;
import com.Da_Technomancer.essentials.api.BlockUtil;
import com.Da_Technomancer.essentials.api.IItemCapable;
import com.Da_Technomancer.essentials.api.packets.INBTReceiver;
import com.Da_Technomancer.essentials.api.packets.SendNBTToTE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class LensFrameTileEntity extends BeamRenderTE implements INBTReceiver, ContainerListener, IItemCapable, RecipeInput{

	public static final BlockEntityType<LensFrameTileEntity> TYPE = CRTileEntity.createType(LensFrameTileEntity::new, CRBlocks.lensFrame);

	private Direction.Axis axis = null;
	private BeamLensRec currRec;
	private boolean recipeCheck;
	private int lastRedstone;
	private ItemStack lensItem = ItemStack.EMPTY;

	private final IItemHandler lensItemHandler = new LensHandler();


	/*
	 * The way this block handles beams is abnormal
	 * Rather than imposing a delay of BEAM_TIME ticks, it outputs incoming beams immediately
	 * This is not what the beam API was designed around, and it has some side effects
	 * - Can't mix multiple incoming beams properly. Most recent incoming beam 'wins'- normally not an issue, but there are some edge cases
	 * - If it were possible for beams from a lens frame to turn corners, it would be possible to crash the game from stack overflow by making a loop of these things all immediately calling each other
	 * - When output switches from non-empty to empty beam unit, takes a 1-tick time delay to show the change
	 */

	public LensFrameTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
	}

	private Direction.Axis getAxis(){
		if(axis == null){
			BlockState state = getBlockState();
			if(state.getBlock() != CRBlocks.lensFrame){
				return Direction.Axis.X;
			}
			axis = state.getValue(CRProperties.AXIS);
		}

		return axis;
	}

	public ItemStack getLensItem(){
		return lensItem;
	}

	public void setLensItem(ItemStack lens){
		lensItem = lens;
		setChanged();
		if(level != null && !level.isClientSide){
			//Update on the client
			CRPackets.sendPacketAround(level, worldPosition, new SendNBTToTE(BlockUtil.stackToNBT(lens, level.registryAccess()), worldPosition));
		}
	}

	@Override
	protected boolean[] inputSides(){
		boolean[] sides = new boolean[6];
		Direction.Axis axis = getAxis();
		sides[Direction.fromAxisAndDirection(axis, AxisDirection.POSITIVE).get3DDataValue()] = true;
		sides[Direction.fromAxisAndDirection(axis, AxisDirection.NEGATIVE).get3DDataValue()] = true;
		return sides;
	}

	@Override
	protected boolean[] outputSides(){
		return inputSides();
	}

	@Override
	public void setBlockState(BlockState stateIn){
		super.setBlockState(stateIn);

		axis = null;
	}

	@Nullable
	public BeamLensRec getCurrRec(){
		if(!recipeCheck){
			RecipeInput input = null;
			Optional<RecipeHolder<BeamLensRec>> rec = level.getRecipeManager().getRecipeFor(CRRecipes.BEAM_LENS_TYPE, input, level);
			currRec = rec.orElse(null) == null ? null : rec.get().value();
			recipeCheck = true;
		}
		return currRec;
	}

	public int getRedstone(){
		return lastRedstone;
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries){
		CompoundTag nbt = super.getUpdateTag(pRegistries);
		ItemStack lensItem = getLensItem();
		if(!lensItem.isEmpty()){
			nbt.put("inv", BlockUtil.stackToNBT(lensItem, pRegistries));
		}
		return nbt;
	}

	@Override
	protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries){
		super.saveAdditional(nbt, pRegistries);
		nbt.putInt("reds", lastRedstone);
		ItemStack lensItem = getLensItem();
		if(!lensItem.isEmpty()){
			nbt.put("inv", BlockUtil.stackToNBT(lensItem, pRegistries));
		}
	}

	@Override
	public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries){
		super.loadAdditional(nbt, registries);
		lastRedstone = nbt.getInt("reds");
		if(nbt.contains("inv")){
			setLensItem(BlockUtil.nbtToItemStack(nbt.getCompound("inv"), registries));
		}else{
			setLensItem(ItemStack.EMPTY);
		}
	}

	@Nullable
	@Override
	public IItemHandler getItemHandler(Direction direction){
		return lensItemHandler;
	}

	@Override
	public void setChanged(){
		super.setChanged();
	}

	@Override
	public void receiveNBT(CompoundTag nbt, ServerPlayer serverPlayer){
		setLensItem(BlockUtil.nbtToItemStack(nbt, level.registryAccess()));
	}

	@Override
	public void containerChanged(Container changedInv){
		setChanged();
		recipeCheck = false;
	}

	private int needUpdateBeamRender = 0;

	@Nonnull
	@Override
	protected BeamUnit shiftStorage(){
		//see comment on lack-of-time-delay for this block

		//Instead of handling emit in doEmit, we do it in shiftStorage, and treat the two queues as separate outputs
		Direction.Axis axis = getAxis();
		int dirPos = Direction.fromAxisAndDirection(axis, AxisDirection.POSITIVE).get3DDataValue();
		int dirNeg = Direction.fromAxisAndDirection(axis, AxisDirection.NEGATIVE).get3DDataValue();

		for(int i = 0; i < 2; i++){
			int dir = i == 1 ? dirPos : dirNeg;
			BeamHelper helper = getBeamHelpers()[dir];
			if(helper.emit(queued[i].getOutput(), level)){
				if(helper.getLastSent().isEmpty()){
					//Delay disabling the rendered output by one tick
					//Slight visual desync, but prevents tick-order dependent flickering
					needUpdateBeamRender |= i + 1;
				}else{
					refreshBeam(dir);
				}
			}
			queued[i].clear();
		}
		updateRedstone();
		setChanged();
		return BeamUnit.EMPTY;
	}

	@Override
	public void serverTick(){
		super.serverTick();
		if(level.getGameTime() % BeamUtil.BEAM_TIME == 1 && needUpdateBeamRender > 0){
			//Handle deferred rendering updates
			if((needUpdateBeamRender & 1) != 0){
				refreshBeam(Direction.fromAxisAndDirection(axis, AxisDirection.NEGATIVE).get3DDataValue());
			}
			if((needUpdateBeamRender & 2) != 0){
				refreshBeam(Direction.fromAxisAndDirection(axis, AxisDirection.POSITIVE).get3DDataValue());
			}
			needUpdateBeamRender = 0;
		}
	}

	@Override
	protected void doEmit(@Nonnull BeamUnit toEmit){
		//see comment on lack-of-time-delay for this block
		//no-op
	}

	private void updateRedstone(){
		BeamHelper[] helpers = getBeamHelpers();
		lastRedstone = 0;
		for(BeamHelper helper1 : helpers){
			if(helper1 != null){
				lastRedstone = Math.max(lastRedstone, helper1.getLastSent().getPower());
			}
		}
	}

	@Override
	protected IBeamHandler createBeamHandler(){
		return new LensBeamHandler();
	}

	@Override
	public ItemStack getItem(int i){
		return i == 0 ? lensItem : ItemStack.EMPTY;
	}

	@Override
	public int size(){
		return 1;
	}

	private class LensBeamHandler implements IBeamHandler{

		@Override
		public void setBeam(@Nonnull BeamUnit beamIn, BeamHit beamHit){
			//Apply lens recipe to incoming beam
			BeamLensRec recipe = getCurrRec();
			BeamMod mod = BeamMod.IDENTITY;
			if(recipe != null){
				if(!beamIn.isEmpty() && EnumBeamAlignments.getAlignment(beamIn) == recipe.getTransmuteAlignment() && (recipe.isVoid() == (beamIn.getVoid() > 0))){
					setLensItem(recipe.assemble(LensFrameTileEntity.this));
				}
				mod = recipe.getOutput();
			}
			beamIn = mod.mult(beamIn);

			//see comment on lack-of-time-delay for this block
			boolean putInQueue = level.getGameTime() != activeCycle;
			Direction outDir = beamHit.getDirection().getOpposite();
			int queueIndex = outDir.getAxisDirection() == AxisDirection.POSITIVE ? 1 : 0;

			if(putInQueue){
				//Put it in queue to be emitted immediately once this ticks (during this game tick)
				//Deliberate decision to introduce a bug:
				//Adding to the queue (normal behavior for most beam blocks) would combine some beams properly but overwrite others, depending on tick order- intermittent bugged/working behavior
				//Decided to instead overwrite the queue, enforcing consistent bugged behavior of not combining beams
				queued[queueIndex].clear();
				queued[queueIndex].addBeam(beamIn);
				setChanged();
			}else{
				//Emit it immediately; this machine has already ticked, so no risk of it getting wiped immediately
				int dir = outDir.get3DDataValue();
				BeamHelper helper = getBeamHelpers()[dir];
				if(helper.emit(beamIn, level)){
					refreshBeam(dir);
				}
				updateRedstone();
				setChanged();
			}
		}

		@Override
		@Deprecated
		public void setBeam(@Nonnull BeamUnit mag){
			//No-op
		}
	}

	private class LensHandler implements IItemHandler{

		@Override
		public int getSlots(){
			return 1;
		}

		@Nonnull
		@Override
		public ItemStack getStackInSlot(int slot){
			return slot == 0 ? lensItem : ItemStack.EMPTY;
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
			return slot == 0 && getLevel().getRecipeManager().getRecipeFor(CRRecipes.BEAM_LENS_TYPE, new SingleRecipeInput(stack), level).isPresent();
		}
	}
}
