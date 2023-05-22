package com.Da_Technomancer.crossroads.blocks.fluid;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.ambient.sounds.CRSounds;
import com.Da_Technomancer.crossroads.api.Capabilities;
import com.Da_Technomancer.crossroads.api.MathUtil;
import com.Da_Technomancer.crossroads.api.packets.CRPackets;
import com.Da_Technomancer.crossroads.api.templates.InventoryTE;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.crossroads.gui.container.RotaryPumpContainer;
import com.Da_Technomancer.essentials.api.BlockUtil;
import com.Da_Technomancer.essentials.api.packets.INBTReceiver;
import com.Da_Technomancer.essentials.api.packets.SendLongToClient;
import com.Da_Technomancer.essentials.api.packets.SendNBTToClient;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
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
import net.minecraft.world.phys.AABB;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.awt.*;

public class RotaryPumpTileEntity extends InventoryTE implements INBTReceiver{

	public static final BlockEntityType<RotaryPumpTileEntity> TYPE = CRTileEntity.createType(RotaryPumpTileEntity::new, CRBlocks.rotaryPump);

	public static final int INERTIA = 80;
	public static final double POWER_PER_SPEED = 2;
	public static final double REQUIRED = 100;
	private static final int CAPACITY = 4_000;

	private double progress = 0;//Positive progress: progress towards pumping 'up'; Negative progress: progress towards pumping 'down'
	private float progChange = 0;//Last change in progress per tick sent to the client. On the client, used for animation

	/**
	 * The fluid to be displayed for rendering in-world. Quantity is NOT synced; only type, NBT, empty or not empty
	 * On server side, acts as a record of what was sent to client
	 */
	private FluidStack renderFluid = FluidStack.EMPTY;
	/**
	 * Cache for the texture and color to be displayed, if any.
	 */
	@Nullable
	private ResourceLocation activeText = null;
	private Integer col = null;//Color applied to the liquid texture

	public RotaryPumpTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state, 0);
		fluidProps[0] = new TankProperty(CAPACITY, true, true);
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

	private boolean didClientInit = false;

	@Override
	public void clientTick(){
		super.clientTick();
		progress += progChange;
		progress %= REQUIRED;
		if(!didClientInit){
			didClientInit = true;
			updateRendering();
		}
	}

	@Override
	public void serverTick(){
		super.serverTick();

		BlockPos targetPos = worldPosition.below();
		BlockState belowState = level.getBlockState(targetPos);
		if(energy > 0){
			//Spinning in positive direction; pump from world into pump inventory
			FluidStack pumpedFluid = getFluidFromBlock(belowState, level, targetPos);
			boolean canPump = !pumpedFluid.isEmpty() && (fluids[0].isEmpty() || BlockUtil.sameFluid(fluids[0], pumpedFluid) && CAPACITY - fluids[0].getAmount() >= pumpedFluid.getAmount());
			if(progress < 0 || canPump){
				updateRenderFluid(pumpedFluid);

				double powerDrained = POWER_PER_SPEED * Math.abs(axleHandler.getSpeed());
				//When we're on negative progress, allowed to reverse negative progress regardless of circumstances, but must meet requirements to make positive progress
				powerDrained = MathUtil.min(Math.abs(axleHandler.getEnergy()), !canPump ? -progress : REQUIRED - progress, powerDrained);
				progress += powerDrained;
				axleHandler.addEnergy(-powerDrained, false);
				updateProgressToClients(powerDrained);

				if(progress >= REQUIRED){
					progress = 0;
					level.setBlockAndUpdate(targetPos, getPumpedBlockState(belowState, level, targetPos));
					int prevAmount = fluids[0].getAmount();
					fluids[0] = pumpedFluid.copy();
					fluids[0].grow(prevAmount);
					SoundEvent sound = fluids[0].getFluid().getFluidType().getSound(SoundActions.BUCKET_FILL);
					if(sound != null){
						CRSounds.playSoundServer(level, targetPos, sound, SoundSource.BLOCKS, 0.5F, 1F);
					}
				}
			}else{
				progress = 0;
				updateProgressToClients(0);
			}
		}else{
			//Spinning in negative direction; pump from pump inventory into world
			BlockState filledState = getFilledBlockState(fluids[0], belowState, level, targetPos);
			boolean canPump = filledState != null;
			if(progress > 0 || canPump){
				updateRenderFluid(fluids[0]);

				double powerDrained = POWER_PER_SPEED * Math.abs(axleHandler.getSpeed());
				//When we're on positive progress, allowed to reverse positive progress regardless of circumstances, but must meet requirements to make negative progress
				powerDrained = MathUtil.min(Math.abs(axleHandler.getEnergy()), !canPump ? progress : REQUIRED + progress, powerDrained);
				progress -= powerDrained;
				axleHandler.addEnergy(-powerDrained, false);
				updateProgressToClients(-powerDrained);

				if(progress <= -REQUIRED){
					progress = 0;
					if(canPump){
						level.setBlockAndUpdate(targetPos, filledState);
						SoundEvent sound = fluids[0].getFluid().getFluidType().getSound(SoundActions.BUCKET_EMPTY);
						if(sound != null){
							CRSounds.playSoundServer(level, targetPos, sound, SoundSource.BLOCKS, 0.5F, 1F);
						}
						fluids[0].shrink(1000);
					}
				}
			}else{
				progress = 0;
				updateProgressToClients(0);
			}
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

	private void updateRenderFluid(FluidStack newRenderFluid){
		//Doesn't send empty render fluids to the client (unnecessary)
		if(!newRenderFluid.isEmpty() && !BlockUtil.sameFluid(renderFluid, newRenderFluid)){
			renderFluid = newRenderFluid.copy();
			CompoundTag nbt = renderFluid.writeToNBT(new CompoundTag());
			nbt.putBoolean("render_fluid", true);
			CRPackets.sendPacketAround(level, worldPosition, new SendNBTToClient(nbt, worldPosition));
		}
	}

	/*
	 * Return null if cannot be filled with fluid toAdd
	 * Otherwise, return the resulting blockstate
	 * Assumes that 1000mB would be consumed to perform this operation
	 */
	@Nullable
	public static BlockState getFilledBlockState(FluidStack toAdd, BlockState state, Level world, BlockPos targetPos){
		if(toAdd.getAmount() < 1000){
			return null;
		}
		Block block = state.getBlock();
		if(block == Blocks.CAULDRON){
			if(toAdd.getFluid() == Fluids.WATER){
				return Blocks.WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 3);
			}
			if(toAdd.getFluid() == Fluids.LAVA){
				return Blocks.LAVA_CAULDRON.defaultBlockState();
			}
		}else if(block == Blocks.WATER_CAULDRON && toAdd.getFluid() == Fluids.WATER){
			//Allow perpetual filling of water cauldrons as the converse of the infinite draining
			return Blocks.WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 3);
		}else if(toAdd.getFluid() == Fluids.WATER && block instanceof SimpleWaterloggedBlock && state.hasProperty(BlockStateProperties.WATERLOGGED) && !state.getValue(BlockStateProperties.WATERLOGGED)){
			//Waterlogged blocks
			return state.setValue(BlockStateProperties.WATERLOGGED, true);
		}else if(!(block instanceof LiquidBlock) && state.canBeReplaced(toAdd.getFluid())){
			//Normal fluids
			BlockState newState = toAdd.getFluid().defaultFluidState().createLegacyBlock();
			if(newState != Blocks.AIR.defaultBlockState()){
				return newState;
			}
		}
		return null;
	}

	public static BlockState getPumpedBlockState(BlockState state, Level world, BlockPos targetPos){
		Block block = state.getBlock();
		if(block == Blocks.WATER_CAULDRON && state.getValue(LayeredCauldronBlock.LEVEL) == 3){
			//Pumps can generate water from a filled water cauldron, without consuming the fluid.
			//This is a special case- they do consume fluid from lava cauldrons
			return state;
		}else if(block == Blocks.LAVA_CAULDRON){
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
		}else if(block == Blocks.LAVA_CAULDRON){
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
		if(Math.signum(progChange) != Math.signum(progressChange) || Math.abs(progressChange - progChange) >= CRConfig.speedPrecision.get().floatValue() / 20F){
			progChange = (float) progressChange;
			long packet = (long) Float.floatToIntBits(progChange) & 0xFFFFFFFFL;
			packet |= ((long) Float.floatToIntBits((float) progress) & 0xFFFFFFFFL) << 32L;
			CRPackets.sendPacketAround(level, worldPosition, new SendLongToClient(1, packet, worldPosition));
		}
	}

	private static final AABB RENDER_BOX = new AABB(0, -1, 0, 1, 1, 1);

	@Override
	public AABB getRenderBoundingBox(){
		return RENDER_BOX.move(worldPosition);
	}

	public float getCompletion(float partialTicks){
		return MathUtil.clamp(((float) progress + partialTicks * progChange) / (float) REQUIRED, -1F, 1F);
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
	public void receiveNBT(CompoundTag nbt, @Nullable ServerPlayer sender){
		if(level.isClientSide && nbt.contains("render_fluid")){
			renderFluid = FluidStack.loadFluidStackFromNBT(nbt);
			updateRendering();
		}
	}

	private void updateRendering(){
		//Call on the client-side only
		IClientFluidTypeExtensions renderProps = IClientFluidTypeExtensions.of(renderFluid.getFluid());
		activeText = renderProps.getStillTexture(renderFluid);
		col = renderProps.getTintColor(renderFluid.getFluid().defaultFluidState(), level, worldPosition);
	}

	public ResourceLocation getActiveTexture(){
		return activeText;
	}

	public Color getCol(){
		return activeText == null ? Color.WHITE : col == null ? Color.WHITE : new Color(col);
	}

	@Override
	public void load(CompoundTag nbt){
		super.load(nbt);
		progress = nbt.getDouble("prog");
		progChange = nbt.getFloat("prog_change");
		renderFluid = FluidStack.loadFluidStackFromNBT(nbt.getCompound("render_fluid"));
	}

	@Override
	public void saveAdditional(CompoundTag nbt){
		super.saveAdditional(nbt);
		nbt.putDouble("prog", progress);
		nbt.putFloat("prog_change", progChange);
		nbt.put("render_fluid", renderFluid.writeToNBT(new CompoundTag()));
	}

	@Override
	public CompoundTag getUpdateTag(){
		CompoundTag nbt =  super.getUpdateTag();
		nbt.putDouble("prog", progress);
		nbt.putFloat("prog_change", progChange);
		nbt.put("render_fluid", renderFluid.writeToNBT(new CompoundTag()));
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
