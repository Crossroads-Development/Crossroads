package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.EnergyConverters;
import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.API.beams.IBeamHandler;
import com.Da_Technomancer.crossroads.API.technomancy.FluxUtil;
import com.Da_Technomancer.crossroads.API.technomancy.IFluxLink;
import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.fluids.CRFluids;
import com.Da_Technomancer.crossroads.gui.container.CopshowiumMakerContainer;
import com.Da_Technomancer.crossroads.items.crafting.CRRecipes;
import com.Da_Technomancer.crossroads.items.crafting.recipes.CopshowiumRec;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@ObjectHolder(Crossroads.MODID)
public class CopshowiumCreationChamberTileEntity extends InventoryTE implements IFluxLink{

	@ObjectHolder("copshowium_creation_chamber")
	private static TileEntityType<CopshowiumCreationChamberTileEntity> type = null;

	public static final int CAPACITY = 1_440;
	public static final int FLUX_PER_INGOT = 4;

	private int flux = 0;
	private int fluxToTrans = 0;
	private final HashSet<BlockPos> link = new HashSet<>(1);

	public CopshowiumCreationChamberTileEntity(){
		super(type, 0);
		fluidProps[0] = new TankProperty(CAPACITY, true, true, f -> f != null && f.getFluid() != CRFluids.moltenCopshowium.still);//Input
		fluidProps[1] = new TankProperty(CAPACITY, false, true);//Copshowium
		initFluidManagers();
	}

	@Override
	public void addInfo(ArrayList<ITextComponent> chat, PlayerEntity player, BlockRayTraceResult hit){
		FluxUtil.addFluxInfo(chat, this, -1);
		super.addInfo(chat, player, hit);
		FluxUtil.addLinkInfo(chat, this);
	}

	@Override
	protected int fluidTanks(){
		return 2;
	}

	public float getRedstone(){
		return (float) fluids[0].getAmount() / CAPACITY;
	}

	public FluidStack getInputFluid(){
		return fluids[0];
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox(){
		//Increase render BB to include links
		return new AxisAlignedBB(pos).grow(getRange());
	}

	@Override
	public void tick(){
		super.tick();

		//Handle flux
		long stage = world.getGameTime() % FluxUtil.FLUX_TIME;
		if(stage == 0 && flux != 0){
			fluxToTrans += flux;
			flux = 0;
			markDirty();
		}else if(stage == 1){
			flux += FluxUtil.performTransfer(this, link, fluxToTrans);
			fluxToTrans = 0;
			FluxUtil.checkFluxOverload(this);
		}
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		nbt.putInt("flux", flux);
		nbt.putInt("flux_trans", fluxToTrans);
		for(BlockPos linked : link){//Size 0 or 1
			nbt.putLong("link", linked.toLong());
		}
		return nbt;
	}

	@Override
	public void read(CompoundNBT nbt){
		super.read(nbt);
		flux = nbt.getInt("flux");
		fluxToTrans = nbt.getInt("flux_trans");
		if(nbt.contains("link")){
			link.add(BlockPos.fromLong(nbt.getLong("link")));
		}else{
			link.clear();
		}
	}

	@Override
	public CompoundNBT getUpdateTag(){
		CompoundNBT nbt = super.getUpdateTag();
		for(BlockPos linked : link){//Size 0 or 1
			nbt.putLong("link", linked.toLong());
		}
		return nbt;
	}

	@Override
	public int getReadingFlux(){
		return FluxUtil.findReadingFlux(this, flux, fluxToTrans);
	}

	@Override
	public void receiveLong(byte identifier, long message, @Nullable ServerPlayerEntity sendingPlayer){
		super.receiveLong(identifier, message, sendingPlayer);
		if(identifier == LINK_PACKET_ID){
			link.add(BlockPos.fromLong(message));
			markDirty();
		}else if(identifier == CLEAR_PACKET_ID){
			link.clear();
			markDirty();
		}
	}

	@Override
	public void remove(){
		super.remove();
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
	public boolean canExtractItem(int index, ItemStack stack, Direction direction){
		return false;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack){
		return false;
	}

	@Override
	public ITextComponent getDisplayName(){
		return new TranslationTextComponent("container.copshowium_maker");
	}

	@Override
	public int getFlux(){
		return flux;
	}

	@Override
	public void setFlux(int newFlux){
		flux = newFlux;
		markDirty();
	}

	@Override
	public Set<BlockPos> getLinks(){
		return link;
	}

	@Nullable
	@Override
	public Container createMenu(int id, PlayerInventory playerInv, PlayerEntity player){
		return new CopshowiumMakerContainer(id, playerInv, createContainerBuf());
	}

	private class BeamHandler implements IBeamHandler{

		@Override
		public void setBeam(BeamUnit mag){
			EnumBeamAlignments align = EnumBeamAlignments.getAlignment(mag);
			if(mag.getVoid() != 0){
				//A void beam destroys all stored liquid
				fluids[0] = FluidStack.EMPTY;
				fluids[1] = FluidStack.EMPTY;
				markDirty();
			}else if(align == EnumBeamAlignments.TIME && !fluids[0].isEmpty()){
				Optional<CopshowiumRec> recOpt = world.getRecipeManager().getRecipe(CRRecipes.COPSHOWIUM_TYPE, CopshowiumCreationChamberTileEntity.this, world);
				if(recOpt.isPresent()){
					CopshowiumRec rec = recOpt.get();
					int created = (int) (fluids[0].getAmount() * rec.getMult());
					if(fluids[1].isEmpty()){
						fluids[1] = new FluidStack(CRFluids.moltenCopshowium.still, created);
					}else{
						fluids[1].grow(created);
					}
					fluids[0] = FluidStack.EMPTY;
					markDirty();

					//Check for overflowing
					if(fluids[1].getAmount() > CAPACITY){
						if(CRConfig.allowOverflow.get()){
							world.setBlockState(pos, CRFluids.moltenCopshowium.still.getDefaultState().getBlockState());
						}else{
							fluids[1].setAmount(CAPACITY);//The config is disabled- just delete any excess fluid
						}
					}
					if(rec.isFlux()){
						//Create flux if applicable
						addFlux(FLUX_PER_INGOT * created / EnergyConverters.INGOT_MB);
					}
				}else{
					//Wipe the contents- no matching recipe
					fluids[0] = FluidStack.EMPTY;
					markDirty();
				}
		}
	}
}
			}