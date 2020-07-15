package com.Da_Technomancer.crossroads.tileentities.fluid;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.packets.CRPackets;
import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.gui.container.RotaryPumpContainer;
import com.Da_Technomancer.essentials.packets.SendLongToClient;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;

@ObjectHolder(Crossroads.MODID)
public class RotaryPumpTileEntity extends InventoryTE{

	@ObjectHolder("rotary_pump")
	public static TileEntityType<RotaryPumpTileEntity> type = null;

	public static final int INERTIA = 80;
	public static final double MAX_POWER = 5;
	private static final int CAPACITY = 4_000;
	private static final double REQUIRED = 100;

	private double progress = 0;
	private int lastProgress = 0;

	public RotaryPumpTileEntity(){
		super(type, 0);
		fluidProps[0] = new TankProperty(CAPACITY, false, true);
		initFluidManagers();
	}

	@Override
	protected int fluidTanks(){
		return 1;
	}

	@Override
	protected boolean useRotary(){
		return true;
	}

	@Override
	protected AxleHandler createAxleHandler(){
		return new AngleAxleHandler();
	}

	@Override
	protected double getMoInertia(){
		return INERTIA;
	}

	@Override
	public void tick(){
		super.tick();

		if(world.isRemote || CAPACITY - fluids[0].getAmount() < FluidAttributes.BUCKET_VOLUME){
			return;
		}

		FluidState fstate = world.getFluidState(pos.down());
		if(fstate.isSource()){
			//Only gain progress if spinning in positive direction
			double holder = motData[1] < 0 ? 0 : Math.min(Math.min(MAX_POWER, motData[1]), REQUIRED - progress);
			motData[1] -= holder;
			progress += holder;

			if(progress >= REQUIRED){
				progress = 0;
				BlockState state = world.getBlockState(pos.down());
				Block block = state.getBlock();
				if(block instanceof IBucketPickupHandler){
					Fluid fl = ((IBucketPickupHandler) block).pickupFluid(world, pos.down(), state);
					fluids[0] = new FluidStack(fl, 1000 + fluids[0].getAmount());
				}else{
					Crossroads.logger.info("Pump attempted to drain a non-traditional fluid at pos: " + pos.down().toString());
				}
			}
		}else{
			progress = 0;
		}

		/*
		BlockState fluidBlockstate = world.getBlockState(pos.offset(Direction.DOWN));
		Block fluidBlock = fluidBlockstate.getBlock();
		Fluid fl = FluidRegistry.lookupFluidForBlock(fluidBlock);
		//2017: If anyone knows a builtin way to simplify this if statement, be my guest. It's so long it scares me...
		//2019-01-29: Looking back on this if statement 2 years later, this is a perfectly reasonable length and not at all scary. If anything, it's too short. If anyone can find a way to make it longer, that would be appreciated
		//2019-11-11: The new fluid system has made this if statement obsolete. Farewell, old friend
		if(fl != null && (fluidBlock instanceof BlockFluidClassic && ((BlockFluidClassic) fluidBlock).isSourceBlock(world, pos.offset(Direction.DOWN)) || fluidBlockstate.get(BlockLiquid.LEVEL) == 0) && (fluids[0] == null || (CAPACITY - fluids[0].amount >= 1000 && fluids[0].getFluid() == fl))){
			double holder = motData[1] < 0 ? 0 : Math.min(Math.min(MAX_POWER, motData[1]), REQUIRED - progress);
			motData[1] -= holder;
			progress += holder;
		}else{
			progress = 0;
		}
		*/

		if(lastProgress != (int) progress){
			//This is really bad- sending a packet every tick while running is inefficient
			//Should be optimized
			CRPackets.sendPacketAround(world, pos, new SendLongToClient(1, (long) progress, pos));
			lastProgress = (int) progress;
		}
	}

	public float getCompletion(){
		return ((float) progress) / ((float) REQUIRED);
	}

	@Override
	public void receiveLong(byte identifier, long message, ServerPlayerEntity player){
		super.receiveLong(identifier, message, player);
		if(identifier == 1){
			progress = message;
		}
	}

	@Override
	public void read(BlockState state, CompoundNBT nbt){
		super.read(state, nbt);
		progress = nbt.getDouble("prog");
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		nbt.putDouble("prog", progress);
		return nbt;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing){
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && facing != Direction.DOWN && facing != Direction.UP){
			return (LazyOptional<T>) globalFluidOpt;
		}
		if(capability == Capabilities.AXLE_CAPABILITY && (facing == Direction.UP || facing == null)){
			return (LazyOptional<T>) axleOpt;
		}

		return super.getCapability(capability, facing);
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, Direction direction){
		return false;
	}

	@Override
	public ITextComponent getDisplayName(){
		return new TranslationTextComponent("container.rotary_pump");
	}

	@Nullable
	@Override
	public Container createMenu(int id, PlayerInventory playerInv, PlayerEntity player){
		return new RotaryPumpContainer(id, playerInv, createContainerBuf());
	}
}
