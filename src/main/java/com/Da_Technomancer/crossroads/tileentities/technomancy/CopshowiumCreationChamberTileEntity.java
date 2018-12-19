package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.EnergyConverters;
import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.API.beams.IBeamHandler;
import com.Da_Technomancer.crossroads.API.technomancy.FluxUtil;
import com.Da_Technomancer.crossroads.API.technomancy.IFluxHandler;
import com.Da_Technomancer.crossroads.API.templates.ILinkTE;
import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.ModConfig;
import com.Da_Technomancer.crossroads.fluids.BlockMoltenCopshowium;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class CopshowiumCreationChamberTileEntity extends InventoryTE implements IFluxHandler, ILinkTE{

	protected int flux;
	protected ArrayList<BlockPos> links = new ArrayList<>(getMaxLinks());
	public static final int CAPACITY = 1_296;


	public CopshowiumCreationChamberTileEntity(){
		super(0);
		fluidProps[0] = new TankProperty(0, CAPACITY, true, true, (Fluid f) -> f != null && (f.getName().equals(ModConfig.getConfigString(ModConfig.cccExpenLiquid, false)) || f.getName().equals(ModConfig.getConfigString(ModConfig.cccFieldLiquid, false))));//Input
		fluidProps[1] = new TankProperty(1, CAPACITY, false, true);//Copshowium
	}

	@Override
	protected int fluidTanks(){
		return 2;
	}

	@Override
	public void update(){
		super.update();

		if(world.isRemote || flux == 0 || world.getTotalWorldTime() % FluxUtil.FLUX_TIME != 0){
			return;
		}

		int moved = FluxUtil.transFlux(world, pos, links, flux);
		if(moved != 0){
			flux -= moved;
			markDirty();
		}
	}

	private final FluidHandler inputHandler = new FluidHandler(0);
	private final FluidHandler outputHandler = new FluidHandler(1);
	private final FluidHandler internalHandler = new FluidHandler(-1);
	private final IBeamHandler magicHandler = new BeamHandler();

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing){
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
			return facing == null ? (T) internalHandler : facing == EnumFacing.UP ? (T) inputHandler : facing == EnumFacing.DOWN ? (T) outputHandler : null;
		}

		if(capability == Capabilities.MAGIC_CAPABILITY && (facing == null || facing.getAxis() != EnumFacing.Axis.Y)){
			return (T) magicHandler;
		}

		return super.getCapability(capability, facing);
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction){
		return false;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack){
		return false;
	}

	@Override
	public String getName(){
		return "container.copshowium_maker";
	}


	@Override
	public void addInfo(ArrayList<String> chat, EntityPlayer player, @Nullable EnumFacing side, float hitX, float hitY, float hitZ){
		super.addInfo(chat, player, side, hitX, hitY, hitZ);
		for(BlockPos link : links){
			chat.add("Linked Position: X=" + (pos.getX() + link.getX()) + " Y=" + (pos.getY() + link.getY()) + " Z=" + (pos.getZ() + link.getZ()));
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setInteger("flux", flux);
		for(int i = 0; i < links.size(); i++){
			nbt.setLong("link" + i, links.get(i).toLong());
		}

		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		flux = nbt.getInteger("flux");
		for(int i = 0; i < getMaxLinks(); i++){
			if(nbt.hasKey("link" + i)){
				links.add(BlockPos.fromLong(nbt.getLong("link" + i)));
			}
		}
	}

	@Override
	public TileEntity getTE(){
		return this;
	}

	@Override
	public boolean canLink(ILinkTE otherTE){
		return otherTE instanceof IFluxHandler && ((IFluxHandler) otherTE).isFluxReceiver();
	}

	@Override
	public ArrayList<BlockPos> getLinks(){
		return links;
	}

	@Override
	public int canAccept(){
		return 0;
	}

	@Override
	public int getFlux(){
		return flux;
	}

	@Override
	public int getMaxLinks(){
		return 4;
	}

	@Override
	public int getCapacity(){
		return 64;
	}

	@Override
	public int addFlux(int fluxIn){
		flux += fluxIn;
		markDirty();
		if(flux > getCapacity()){
			world.destroyBlock(pos, false);
			world.createExplosion(null, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, 0, false);
			FluxUtil.fluxEvent(world, pos, 64);
		}
		return flux;
	}

	@Override
	public boolean isFluxEmitter(){
		return true;
	}

	@Override
	public boolean isFluxReceiver(){
		return false;
	}

	private class BeamHandler implements IBeamHandler{

		@Override
		public void setMagic(BeamUnit mag){
			if(EnumBeamAlignments.getAlignment(mag) == EnumBeamAlignments.TIME && fluids[0] != null){
				if(fluids[0].getFluid().getName().equals(ModConfig.getConfigString(ModConfig.cccExpenLiquid, false))){
					fluids[1] = new FluidStack(BlockMoltenCopshowium.getMoltenCopshowium(), (int) (((double) fluids[0].amount) * EnergyConverters.COPSHOWIUM_PER_COPPER) + (fluids[1] == null ? 0 : fluids[1].amount));
					fluids[0] = null;
					markDirty();
					if(fluids[1].amount > CAPACITY){
						world.setBlockState(pos, BlockMoltenCopshowium.getMoltenCopshowium().getBlock().getDefaultState());
					}
				}else if(fluids[0].getFluid().getName().equals(ModConfig.getConfigString(ModConfig.cccFieldLiquid, false))){
					int created = (int) (((double) fluids[0].amount) * EnergyConverters.COPSHOWIUM_PER_COPPER);
					fluids[1] = new FluidStack(BlockMoltenCopshowium.getMoltenCopshowium(), created + (fluids[1] == null ? 0 : fluids[1].amount));
					fluids[0] = null;
					addFlux(4 * created / EnergyConverters.INGOT_MB);
					markDirty();
					if(fluids[1].amount > CAPACITY){
						world.setBlockState(pos, BlockMoltenCopshowium.getMoltenCopshowium().getBlock().getDefaultState());
					}
				}
			}
		}
	}
}