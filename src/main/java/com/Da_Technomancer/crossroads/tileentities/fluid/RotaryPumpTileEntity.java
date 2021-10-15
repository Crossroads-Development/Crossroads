package com.Da_Technomancer.crossroads.tileentities.fluid;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.packets.CRPackets;
import com.Da_Technomancer.crossroads.API.rotary.RotaryUtil;
import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.gui.container.RotaryPumpContainer;
import com.Da_Technomancer.essentials.packets.SendLongToClient;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.PowderSnowBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
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
	public static BlockEntityType<RotaryPumpTileEntity> TYPE = null;

	public static final int INERTIA = 80;
	public static final double MAX_POWER = 5;
	public static final double MAX_SPEED = 2.5;
	private static final int CAPACITY = 4_000;
	private static final double REQUIRED = 100;

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

		if(CAPACITY - fluids[0].getAmount() < FluidAttributes.BUCKET_VOLUME){
			return;
		}

		FluidState fstate = level.getFluidState(worldPosition.below());
		if(fstate.isSource()){
			//Only gain progress if spinning in positive direction
			double powerDrained = energy < 0 ? 0 : MAX_POWER * RotaryUtil.findEfficiency(axleHandler.getSpeed(), 0, MAX_SPEED);
			progress += powerDrained;
			axleHandler.addEnergy(-powerDrained, false);
			updateProgressToClients(powerDrained);

			if(progress >= REQUIRED){
				progress = 0;
				BlockPos targetPos = worldPosition.below();
				BlockState state = level.getBlockState(targetPos);
				Block block = state.getBlock();
				if(block instanceof BucketPickup bp && !(bp instanceof PowderSnowBlock)){
					//As of MC1.17, not all instances of BucketPickup represent fluids
					//We blacklist in code any non-fluid examples
					//And verify the result, reverting any change if it gave a non-fluid output

					ItemStack resultStack = bp.pickupBlock(level, targetPos, state);
					if(resultStack.getItem() instanceof BucketItem bItem){
						Fluid fl = bItem.getFluid();
						fluids[0] = new FluidStack(fl, 1000 + fluids[0].getAmount());
					}else{
						//Invalid block, revert any change by setting the blockstate to the original value
						level.setBlock(targetPos, state, 2);
					}
				}
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
	public CompoundTag save(CompoundTag nbt){
		super.save(nbt);
		nbt.putDouble("prog", progress);
		nbt.putFloat("prog_change", progChange);
		return nbt;
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
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && facing != Direction.DOWN && facing != Direction.UP){
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
		return new TranslatableComponent("container.rotary_pump");
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player){
		return new RotaryPumpContainer(id, playerInv, createContainerBuf());
	}
}
