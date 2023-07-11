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
import com.Da_Technomancer.essentials.api.packets.INBTReceiver;
import com.Da_Technomancer.essentials.api.packets.SendNBTToClient;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class LensFrameTileEntity extends BeamRenderTE implements INBTReceiver, ContainerListener{

	public static final BlockEntityType<LensFrameTileEntity> TYPE = CRTileEntity.createType(LensFrameTileEntity::new, CRBlocks.lensFrame);

	private final SimpleContainer inventoryWrapper = new SimpleContainer(1);
	private Direction.Axis axis = null;
	private BeamLensRec currRec;
	private boolean recipeCheck;
	private int lastRedstone;

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
		inventoryWrapper.addListener(this);
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
		return inventoryWrapper.getItem(0);
	}

	public void setLensItem(ItemStack lens){
		inventoryWrapper.setItem(0, lens);
		if(level != null && !level.isClientSide){
			//Update on the client
			CRPackets.sendPacketAround(level, worldPosition, new SendNBTToClient(lens.save(new CompoundTag()), worldPosition));
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
	public BeamLensRec getCurrRec() {
		if(!recipeCheck){
			Optional<BeamLensRec> rec = level.getRecipeManager().getRecipeFor(CRRecipes.BEAM_LENS_TYPE, inventoryWrapper, level);
			currRec = rec.orElse(null);
			recipeCheck = true;
		}
		return currRec;
	}

	public int getRedstone(){
		return lastRedstone;
	}

	@Override
	public CompoundTag getUpdateTag(){
		CompoundTag nbt = super.getUpdateTag();
		ItemStack lensItem = getLensItem();
		if(!lensItem.isEmpty()){
			nbt.put("inv", lensItem.save(new CompoundTag()));
		}
		return nbt;
	}

	@Override
	public void saveAdditional(CompoundTag nbt){
		super.saveAdditional(nbt);
		nbt.putInt("reds", lastRedstone);
		ItemStack lensItem = getLensItem();
		if(!lensItem.isEmpty()){
			nbt.put("inv", lensItem.save(new CompoundTag()));
		}
	}

	@Override
	public void load(CompoundTag nbt){
		super.load(nbt);
		lastRedstone = nbt.getInt("reds");
		if(nbt.contains("inv")){
			setLensItem(ItemStack.of(nbt.getCompound("inv")));
		}else{
			setLensItem(ItemStack.EMPTY);
		}
	}

	@Override
	public void setRemoved(){
		super.setRemoved();
		lensOpt.invalidate();
	}

	private final LazyOptional<IItemHandler> lensOpt = LazyOptional.of(LensHandler::new);

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side){
		if(cap == ForgeCapabilities.ITEM_HANDLER){
			return (LazyOptional<T>)lensOpt;
		}

		return super.getCapability(cap, side);
	}

	@Override
	public void setChanged(){
		super.setChanged();
	}

	@Override
	public void receiveNBT(CompoundTag nbt, ServerPlayer serverPlayer){
		setLensItem(ItemStack.of(nbt));
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
					needUpdateBeamRender |= i+1;
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

	private class LensBeamHandler implements IBeamHandler {

		@Override
		public void setBeam(@Nonnull BeamUnit beamIn, BeamHit beamHit){
			//Apply lens recipe to incoming beam
			BeamLensRec recipe = getCurrRec();
			BeamMod mod = BeamMod.IDENTITY;
			if(recipe != null){
				if(!beamIn.isEmpty() && EnumBeamAlignments.getAlignment(beamIn) == recipe.getTransmuteAlignment() && (recipe.isVoid() == (beamIn.getVoid() > 0))){
					setLensItem(recipe.assemble(inventoryWrapper));
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
			return slot == 0 && getLevel().getRecipeManager().getRecipeFor(CRRecipes.BEAM_LENS_TYPE, new SimpleContainer(stack), level).isPresent();
		}
	}
}
