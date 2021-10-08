package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.crossroads.API.technomancy.FluxUtil;
import com.Da_Technomancer.crossroads.API.technomancy.IFluxLink;
import com.Da_Technomancer.crossroads.API.templates.BeamRenderTE;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.gui.container.BeaconHarnessContainer;
import com.Da_Technomancer.essentials.tileentities.ILinkTE;
import io.netty.buffer.Unpooled;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.Set;

import com.Da_Technomancer.crossroads.API.technomancy.IFluxLink.Behaviour;
import com.Da_Technomancer.crossroads.API.technomancy.IFluxLink.FluxHelper;

@ObjectHolder(Crossroads.MODID)
public class BeaconHarnessTileEntity extends BeamRenderTE implements IFluxLink, Container, MenuProvider{

	@ObjectHolder("beacon_harness")
	public static BlockEntityType<BeaconHarnessTileEntity> type = null;

	public static final int FLUX_GEN = 4;
	public static final int LOOP_TIME = 120;//Time to make one full rotation around the color wheel in cycles. Must be a multiple of 3
	private static final int SAFETY_BUFFER = 8;//Duration of the switchover period in cycles

	private boolean running;
	private int cycles = -11;
	private int loadSafetyTime = 0;

	//Flux related fields
	private final FluxHelper fluxHelper = new FluxHelper(type, this, Behaviour.SOURCE);

	public BeaconHarnessTileEntity(){
		super(type);
	}

	@Override
	public void addInfo(ArrayList<Component> chat, Player player, BlockHitResult hit){
		super.addInfo(chat, player, hit);
		FluxUtil.addFluxInfo(chat, this, running ? FLUX_GEN : 0);
		fluxHelper.addInfo(chat, player, hit);
	}

	public int getCycles(){
		return cycles;
	}

	@Override
	public void tick(){
		super.tick();
		fluxHelper.tick();

		// Actual beam production is in the emit() method

		if(level.isClientSide){
			//For the server side,
			//decreasing loadSafetyTime happens in emit(), which is a consistent and reliable way of doing this
			//For the client side, emit() is never called, so we decrement it here
			//On the client side, this is only used for the UI, and does not need to be perfectly accurate
			if(--loadSafetyTime < 0){
				loadSafetyTime = 0;
			}
		}
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

	private boolean invalid(Color col, BeamUnit last){
		if(last.isEmpty() || last.getVoid() != 0 || (col.getRed() != 0 && last.getEnergy() != 0) || (col.getGreen() != 0 && last.getPotential() != 0) || (col.getBlue() != 0 && last.getStability() != 0)){
			return true;
		}

		return fluxHelper.isShutDown() || positionInvalid();
	}

	//Requires beneath a beacon, and all blocks between this and the beacon are legal beacon bases
	private boolean positionInvalid(){
		BlockPos.MutableBlockPos checkPos = new BlockPos.MutableBlockPos(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ());
		for(int y = 0; y < 5; y++){
			checkPos.move(Direction.UP);
			BlockState state = level.getBlockState(checkPos);
			if(state.getBlock() == Blocks.BEACON){
				return false;
			}
			if(!state.getBlock().is(BlockTags.BEACON_BASE_BLOCKS)){
				return true;
			}
		}
		return true;
	}

	public void trigger(){
		if(!running && !positionInvalid()){
			running = true;
		}
	}

	@Override
	public CompoundTag getUpdateTag(){
		CompoundTag nbt = super.getUpdateTag();
//		nbt.putBoolean("run", running);
		fluxHelper.writeData(nbt);
		return nbt;
	}

	@Override
	public CompoundTag save(CompoundTag nbt){
		super.save(nbt);
		nbt.putBoolean("run", running);
		nbt.putInt("cycle", cycles);
		fluxHelper.writeData(nbt);
		return nbt;
	}

	@Override
	public void load(BlockState state, CompoundTag nbt){
		super.load(state, nbt);
		running = nbt.getBoolean("run");
		cycles = nbt.getInt("cycle");
		fluxHelper.readData(nbt);
	}

	@Override
	public void onLoad(){
		super.onLoad();
		if(CRConfig.beaconHarnessLoadSafety.get()){
			loadSafetyTime = LOOP_TIME;
		}
	}

	public boolean isSafetyPeriod(int cycles){
		return cycles % (LOOP_TIME / 3) < SAFETY_BUFFER || loadSafetyTime != 0;
	}

	public static BeamUnit getOutput(int cycles){
		if(cycles < 0){
			return BeamUnit.EMPTY;
		}
		//The color calculation takes advantage of the fact that the "color wheel" as most people know it is the slice of the HSB color cylinder with saturation=1. The outer rim is brightness=1. The angle is controlled by hue
		Color outColor = Color.getHSBColor(((float) cycles) / LOOP_TIME, 1, 1);
		BeamUnit out = new BeamUnit(outColor.getRed(), outColor.getGreen(), outColor.getBlue(), 0);
		out = out.mult(CRConfig.beaconHarnessPower.get() / ((double) out.getPower()), false);
		return out;
	}

	@Override
	protected void doEmit(BeamUnit input){
		if(--loadSafetyTime < 0){
			loadSafetyTime = 0;
		}
		if(running){
			++cycles;
			cycles %= LOOP_TIME;
			BeamUnit out = getOutput(cycles);
			if(cycles >= 0){
				//Don't check color during a safety period
				if(!isSafetyPeriod(cycles) && invalid(out.getRGB(), input)){
					//Wrong input- shut down
					running = false;
					cycles = -11;//Easy way of adding a startup cooldown- 10 cycles

					if(beamer[0].emit(BeamUnit.EMPTY, level)){
						refreshBeam(0);
					}
				}else{
					beamer[0].emit(out, level);
					refreshBeam(0);//Assume the beam changed as the color constantly cycles
					prevMag[0] = out;
					addFlux(FLUX_GEN);
					setChanged();
				}
			}
		}
	}

	@Override
	protected boolean[] inputSides(){
		return new boolean[] {false, false, true, true, true, true};
	}

	@Override
	protected boolean[] outputSides(){
		return new boolean[] {true, false, false, false, false, false};
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

	@Override
	public int getFlux(){
		return fluxHelper.getFlux();
	}

	@Override
	public void receiveLong(byte identifier, long message, @Nullable ServerPlayer serverPlayerEntity){
		fluxHelper.receiveLong(identifier, message, serverPlayerEntity);
	}

	@Override
	public void receiveInts(byte context, int[] message, @Nullable ServerPlayer sendingPlayer){
		fluxHelper.receiveInts(context, message, sendingPlayer);
	}

	@Override
	public int[] getRenderedArcs(){
		return fluxHelper.getRenderedArcs();
	}

	//IInventory methods, all no-op
	@Override
	public int getContainerSize(){
		return 0;
	}

	@Override
	public boolean isEmpty(){
		return true;
	}

	@Override
	public ItemStack getItem(int index){
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeItem(int index, int count){
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeItemNoUpdate(int index){
		return ItemStack.EMPTY;
	}

	@Override
	public void setItem(int index, ItemStack stack){

	}

	@Override
	public boolean stillValid(Player player){
		return level.getBlockEntity(worldPosition) == this && player.distanceToSqr(worldPosition.getX() + 0.5D, worldPosition.getY() + 0.5D, worldPosition.getZ() + 0.5D) <= 64;
	}

	@Override
	public void clearContent(){

	}

	//INamedGuiProvides methods

	@Override
	public Component getDisplayName(){
		return new TranslatableComponent("container.beacon_harness");
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player){
		return new BeaconHarnessContainer(id, playerInv, new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(worldPosition));
	}
}
