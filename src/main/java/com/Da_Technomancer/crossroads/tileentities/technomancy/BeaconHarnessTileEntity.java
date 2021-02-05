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
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.Set;

@ObjectHolder(Crossroads.MODID)
public class BeaconHarnessTileEntity extends BeamRenderTE implements IFluxLink, IInventory, INamedContainerProvider{

	@ObjectHolder("beacon_harness")
	public static TileEntityType<BeaconHarnessTileEntity> type = null;

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
	public void addInfo(ArrayList<ITextComponent> chat, PlayerEntity player, BlockRayTraceResult hit){
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

		if(world.isRemote){
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
		BlockPos.Mutable checkPos = new BlockPos.Mutable(pos.getX(), pos.getY(), pos.getZ());
		for(int y = 0; y < 5; y++){
			checkPos.move(Direction.UP);
			BlockState state = world.getBlockState(checkPos);
			if(state.getBlock() == Blocks.BEACON){
				return false;
			}
			if(!state.getBlock().isIn(BlockTags.BEACON_BASE_BLOCKS)){
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
	public CompoundNBT getUpdateTag(){
		CompoundNBT nbt = super.getUpdateTag();
//		nbt.putBoolean("run", running);
		fluxHelper.writeData(nbt);
		return nbt;
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		nbt.putBoolean("run", running);
		nbt.putInt("cycle", cycles);
		fluxHelper.writeData(nbt);
		return nbt;
	}

	@Override
	public void read(BlockState state, CompoundNBT nbt){
		super.read(state, nbt);
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

					if(beamer[0].emit(BeamUnit.EMPTY, world)){
						refreshBeam(0);
					}
				}else{
					beamer[0].emit(out, world);
					refreshBeam(0);//Assume the beam changed as the color constantly cycles
					prevMag[0] = out;
					addFlux(FLUX_GEN);
					markDirty();
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
	public boolean createLinkSource(ILinkTE endpoint, @Nullable PlayerEntity player){
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
	public void receiveLong(byte identifier, long message, @Nullable ServerPlayerEntity serverPlayerEntity){
		fluxHelper.receiveLong(identifier, message, serverPlayerEntity);
	}

	@Override
	public void receiveInts(byte context, int[] message, @Nullable ServerPlayerEntity sendingPlayer){
		fluxHelper.receiveInts(context, message, sendingPlayer);
	}

	@Override
	public int[] getRenderedArcs(){
		return fluxHelper.getRenderedArcs();
	}

	//IInventory methods, all no-op
	@Override
	public int getSizeInventory(){
		return 0;
	}

	@Override
	public boolean isEmpty(){
		return true;
	}

	@Override
	public ItemStack getStackInSlot(int index){
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack decrStackSize(int index, int count){
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeStackFromSlot(int index){
		return ItemStack.EMPTY;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack){

	}

	@Override
	public boolean isUsableByPlayer(PlayerEntity player){
		return world.getTileEntity(pos) == this && player.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64;
	}

	@Override
	public void clear(){

	}

	//INamedGuiProvides methods

	@Override
	public ITextComponent getDisplayName(){
		return new TranslationTextComponent("container.beacon_harness");
	}

	@Nullable
	@Override
	public Container createMenu(int id, PlayerInventory playerInv, PlayerEntity player){
		return new BeaconHarnessContainer(id, playerInv, new PacketBuffer(Unpooled.buffer()).writeBlockPos(pos));
	}
}
