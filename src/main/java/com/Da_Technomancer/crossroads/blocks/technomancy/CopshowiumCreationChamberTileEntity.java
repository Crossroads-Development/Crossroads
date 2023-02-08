package com.Da_Technomancer.crossroads.blocks.technomancy;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.api.Capabilities;
import com.Da_Technomancer.crossroads.api.beams.BeamUnit;
import com.Da_Technomancer.crossroads.api.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.api.beams.IBeamHandler;
import com.Da_Technomancer.crossroads.api.technomancy.FluxUtil;
import com.Da_Technomancer.crossroads.api.technomancy.IFluxLink;
import com.Da_Technomancer.crossroads.api.templates.InventoryTE;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.crossroads.crafting.CRRecipes;
import com.Da_Technomancer.crossroads.crafting.CopshowiumRec;
import com.Da_Technomancer.crossroads.fluids.CRFluids;
import com.Da_Technomancer.crossroads.gui.container.CopshowiumMakerContainer;
import com.Da_Technomancer.essentials.api.ILinkTE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;


public class CopshowiumCreationChamberTileEntity extends InventoryTE implements IFluxLink{

	public static final BlockEntityType<CopshowiumCreationChamberTileEntity> TYPE = CRTileEntity.createType(CopshowiumCreationChamberTileEntity::new, CRBlocks.copshowiumCreationChamber);

	public static final int CAPACITY = 1_000;
	public static final int FLUX_PER_INGOT = 4;

	private final FluxHelper fluxHelper;

	public CopshowiumCreationChamberTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state, 0);
		fluxHelper = new FluxHelper(TYPE, pos, state, this, Behaviour.SOURCE);
		fluidProps[0] = new TankProperty(CAPACITY, true, true, f -> true);//Input
		fluidProps[1] = new TankProperty(CAPACITY, false, true);//Copshowium output
		initFluidManagers();
	}

	@Override
	public void addInfo(ArrayList<Component> chat, Player player, BlockHitResult hit){
		FluxUtil.addFluxInfo(chat, this, -1);
		super.addInfo(chat, player, hit);
		fluxHelper.addInfo(chat, player, hit);
	}

	@Override
	protected int fluidTanks(){
		return 2;
	}

	public float getRedstone(){
		return fluids[0].getAmount();
	}

	public FluidStack getInputFluid(){
		return fluids[0];
	}

	@Override
	public AABB getRenderBoundingBox(){
		//Increase render BB to include links
		return new AABB(worldPosition).inflate(getRange());
	}

	@Override
	public void serverTick(){
		super.serverTick();
		fluxHelper.serverTick();
	}

	@Override
	public void clientTick(){
		super.clientTick();
		fluxHelper.clientTick();
	}

	@Override
	public void saveAdditional(CompoundTag nbt){
		super.saveAdditional(nbt);
		fluxHelper.writeData(nbt);
	}

	@Override
	public void load(CompoundTag nbt){
		super.load(nbt);
		fluxHelper.readData(nbt);
	}

	@Override
	public CompoundTag getUpdateTag(){
		CompoundTag nbt = super.getUpdateTag();
		fluxHelper.writeData(nbt);
		return nbt;
	}

	@Override
	public int getReadingFlux(){
		return fluxHelper.getReadingFlux();
	}

	@Override
	public void addFlux(int deltaFlux){
		fluxHelper.addFlux(deltaFlux);
	}

	@Override
	public boolean canAcceptLinks(){
		return fluxHelper.canAcceptLinks();
	}

	@Override
	public void receiveLong(byte identifier, long message, @Nullable ServerPlayer sendingPlayer){
		super.receiveLong(identifier, message, sendingPlayer);
		fluxHelper.receiveLong(identifier, message, sendingPlayer);
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction){
		return false;
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack){
		return false;
	}

	@Override
	public Component getDisplayName(){
		return Component.translatable("container.copshowium_maker");
	}

	@Override
	public int getFlux(){
		return fluxHelper.getFlux();
	}

	@Override
	public boolean canBeginLinking(){
		return fluxHelper.canBeginLinking();
	}

	@Override
	public boolean canLink(ILinkTE otherTE){
		return fluxHelper.canLink(otherTE);
	}

	@Override
	public Set<BlockPos> getLinks(){
		return fluxHelper.getLinks();
	}

	@Override
	public boolean createLinkSource(ILinkTE endpoint, @Nullable Player player){
		return fluxHelper.createLinkSource(endpoint, player);
	}

	@Override
	public void removeLinkSource(BlockPos end){
		fluxHelper.removeLinkSource(end);
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player){
		return new CopshowiumMakerContainer(id, playerInv, createContainerBuf());
	}

	@Override
	public void setRemoved(){
		super.setRemoved();
		inputOpt.invalidate();
		outputOpt.invalidate();
		beamOpt.invalidate();
	}

	//Make the top handler an IFluidTank to allow pipes to do bi-directional stuff
	private final LazyOptional<IFluidHandler> inputOpt = LazyOptional.of(() -> new FluidTankHandler(0));
	private final LazyOptional<IFluidHandler> outputOpt = LazyOptional.of(() -> new FluidHandler(1));
	private final LazyOptional<IBeamHandler> beamOpt = LazyOptional.of(BeamHandler::new);

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing){
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
			return facing == null ? (LazyOptional<T>) globalFluidOpt : facing == Direction.UP ? (LazyOptional<T>) inputOpt : facing == Direction.DOWN ? (LazyOptional<T>) outputOpt : LazyOptional.empty();
		}

		if(capability == Capabilities.BEAM_CAPABILITY && (facing == null || facing.getAxis() != Direction.Axis.Y)){
			return (LazyOptional<T>) beamOpt;
		}

		return super.getCapability(capability, facing);
	}

	@Override
	public void receiveInts(byte context, int[] message, @Nullable ServerPlayer sendingPlayer){
		fluxHelper.receiveInts(context, message, sendingPlayer);
	}

	@Override
	public int[] getRenderedArcs(){
		return fluxHelper.getRenderedArcs();
	}

	private class BeamHandler implements IBeamHandler{

		@Override
		public void setBeam(BeamUnit mag){
			if(mag.isEmpty() || fluxHelper.isShutDown()){
				return;
			}

			EnumBeamAlignments align = EnumBeamAlignments.getAlignment(mag);
			if(mag.getVoid() != 0){
				//A void beam destroys all stored liquid
				fluids[0] = FluidStack.EMPTY;
				fluids[1] = FluidStack.EMPTY;
				setChanged();
			}else if((!CRConfig.cccRequireTime.get() || align == EnumBeamAlignments.TIME) && !fluids[0].isEmpty()){
				Optional<CopshowiumRec> recOpt = level.getRecipeManager().getRecipeFor(CRRecipes.COPSHOWIUM_TYPE, CopshowiumCreationChamberTileEntity.this, level);
				if(recOpt.isPresent()){
					CopshowiumRec rec = recOpt.get();
					int created = (int) (fluids[0].getAmount() * rec.getMult());
					if(fluids[1].isEmpty()){
						fluids[1] = new FluidStack(CRFluids.moltenCopshowium.still, created);
					}else{
						fluids[1].grow(created);
					}
					fluids[0] = FluidStack.EMPTY;
					setChanged();

					//Check for overflowing
					if(fluids[1].getAmount() > CAPACITY){
						if(CRConfig.allowOverflow.get()){
							level.setBlockAndUpdate(worldPosition, CRFluids.moltenCopshowium.block.defaultBlockState());
						}else{
							fluids[1].setAmount(CAPACITY);//The config is disabled- just delete any excess fluid
						}
					}
					if(rec.isFlux()){
						//Create flux if applicable
						addFlux(FLUX_PER_INGOT * created / CRConfig.mbPerIngot.get());
					}
				}else{
					//Wipe the contents- no matching recipe
					fluids[0] = FluidStack.EMPTY;
					setChanged();
				}
			}
		}
	}
}