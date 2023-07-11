package com.Da_Technomancer.crossroads.blocks.fluid;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.api.Capabilities;
import com.Da_Technomancer.crossroads.api.packets.CRPackets;
import com.Da_Technomancer.crossroads.api.templates.InventoryTE;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.crossroads.gui.container.RotaryPumpContainer;
import com.Da_Technomancer.essentials.api.BlockUtil;
import com.Da_Technomancer.essentials.api.packets.SendLongToClient;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;

public class RotaryPumpTileEntity extends InventoryTE{

	public static final BlockEntityType<RotaryPumpTileEntity> TYPE = CRTileEntity.createType(RotaryPumpTileEntity::new, CRBlocks.rotaryPump);

	public static final int INERTIA = 80;
	public static final double POWER_PER_SPEED = 2;
	public static final double REQUIRED = 100;
	private static final int CAPACITY = 4_000;

	private double progress = 0;
	private float progChange = 0;//Last change in progress per tick sent to the client. On the client, used for animation

	public RotaryPumpTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state, 0);
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
	protected double getMoInertia(){
		return INERTIA;
	}

	@Override
	public void clientTick(){
		super.clientTick();
		progress += progChange;
		progress %= REQUIRED;
	}

	@Override
	public void serverTick(){
		super.serverTick();

		BlockPos targetPos = worldPosition.below();
		BlockState state = level.getBlockState(targetPos);
		FluidStack pumpedFluid = getFluidFromBlock(state, level, targetPos);
		if(!pumpedFluid.isEmpty() && (fluids[0].isEmpty() || BlockUtil.sameFluid(fluids[0], pumpedFluid) && CAPACITY - fluids[0].getAmount() >= pumpedFluid.getAmount())){
			//Only gain progress if spinning in positive direction
			double powerDrained = energy < 0 ? 0 : POWER_PER_SPEED * Math.abs(axleHandler.getSpeed());
			powerDrained = Math.min(Math.abs(axleHandler.getEnergy()), Math.min(REQUIRED - progress, powerDrained));
			progress += powerDrained;
			axleHandler.addEnergy(-powerDrained, false);
			updateProgressToClients(powerDrained);

			if(progress >= REQUIRED){
				progress = 0;
				level.setBlockAndUpdate(targetPos, getPumpedBlockState(state, level, targetPos));
				int prevAmount = fluids[0].getAmount();
				fluids[0] = pumpedFluid.copy();
				fluids[0].grow(prevAmount);
			}
		}else{
			updateProgressToClients(0);
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
	}

	public static BlockState getPumpedBlockState(BlockState state, Level world, BlockPos targetPos){
		Block block = state.getBlock();
		if(block == Blocks.WATER_CAULDRON && state.getValue(LayeredCauldronBlock.LEVEL) == 3){
			//Pumps can generate water from a filled water cauldron, without consuming the fluid.
			//This is a special case- they do consume fluid from lava cauldrons
			return state;
		}else if(block == Blocks.LAVA_CAULDRON && state.getValue(LayeredCauldronBlock.LEVEL) == 3){
			return Blocks.CAULDRON.defaultBlockState();
		}else if(block instanceof LiquidBlock lblock && lblock.getFluid().isSource(world.getFluidState(targetPos))){
			//Normal fluids
			return Blocks.AIR.defaultBlockState();
		}else if(block instanceof SimpleWaterloggedBlock && state.hasProperty(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED)){
			//Waterlogged blocks
			return state.setValue(BlockStateProperties.WATERLOGGED, false);
		}
		return state;
	}

	public static FluidStack getFluidFromBlock(BlockState state, Level world, BlockPos targetPos){
		Block block = state.getBlock();
		if(block == Blocks.WATER_CAULDRON && state.getValue(LayeredCauldronBlock.LEVEL) == 3){
			//Pumps can generate water from a filled water cauldron, without consuming the fluid.
			//This is a special case- they do consume fluid from lava cauldrons
			return new FluidStack(Fluids.WATER, 1000);
		}else if(block == Blocks.LAVA_CAULDRON && state.getValue(LayeredCauldronBlock.LEVEL) == 3){
			return new FluidStack(Fluids.LAVA, 1000);
		}else if(block instanceof LiquidBlock lblock && lblock.getFluid().isSource(world.getFluidState(targetPos))){
			//Normal fluids
			Fluid fluid = lblock.getFluid().getSource();
			return new FluidStack(fluid, 1000);
		}else if(block instanceof SimpleWaterloggedBlock && state.hasProperty(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED)){
			//Waterlogged blocks
			return new FluidStack(Fluids.WATER, 1000);
		}
		return FluidStack.EMPTY;
	}

	private void updateProgressToClients(double progressChange){
		if(((progChange == 0) != (progressChange == 0)) || Math.abs(progressChange - progChange) >= CRConfig.speedPrecision.get().floatValue() / 20F){
			progChange = (float) progressChange;
			long packet = Float.floatToIntBits(progChange);
			packet |= (long) Float.floatToIntBits((float) progress) << 32L;
			CRPackets.sendPacketAround(level, worldPosition, new SendLongToClient(1, packet, worldPosition));
		}
	}

	public float getCompletion(){
		return ((float) progress) / ((float) REQUIRED);
	}

	@Override
	public void receiveLong(byte identifier, long message, ServerPlayer player){
		super.receiveLong(identifier, message, player);
		if(identifier == 1){
			progChange = Float.intBitsToFloat((int) (message & 0xFFFFFFFFL));
			progress = Float.intBitsToFloat((int) (message >>> 32L));
		}
	}

	@Override
	public void load(CompoundTag nbt){
		super.load(nbt);
		progress = nbt.getDouble("prog");
		progChange = nbt.getFloat("prog_change");
	}

	@Override
	public void saveAdditional(CompoundTag nbt){
		super.saveAdditional(nbt);
		nbt.putDouble("prog", progress);
		nbt.putFloat("prog_change", progChange);
	}

	@Override
	public CompoundTag getUpdateTag(){
		CompoundTag nbt =  super.getUpdateTag();
		nbt.putDouble("prog", progress);
		nbt.putFloat("prog_change", progChange);
		return nbt;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing){
		if(capability == ForgeCapabilities.FLUID_HANDLER && facing != Direction.DOWN && facing != Direction.UP){
			return (LazyOptional<T>) globalFluidOpt;
		}
		if(capability == Capabilities.AXLE_CAPABILITY && (facing == Direction.UP || facing == null)){
			return (LazyOptional<T>) axleOpt;
		}

		return super.getCapability(capability, facing);
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction){
		return false;
	}

	@Override
	public Component getDisplayName(){
		return Component.translatable("container.rotary_pump");
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player){
		return new RotaryPumpContainer(id, playerInv, createContainerBuf());
	}
}
