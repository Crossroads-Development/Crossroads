package com.Da_Technomancer.crossroads.blocks.rotary;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.api.CRCapabilities;
import com.Da_Technomancer.crossroads.api.rotary.IAxisHandler;
import com.Da_Technomancer.crossroads.api.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.api.rotary.RotaryUtil;
import com.Da_Technomancer.crossroads.api.templates.InventoryTE;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.crossroads.crafting.CRRecipes;
import com.Da_Technomancer.crossroads.crafting.StampMillRec;
import com.Da_Technomancer.crossroads.gui.container.StampMillContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;

import net.neoforged.neoforge.items.IItemHandler;
import org.apache.logging.log4j.core.jmx.Server;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Optional;

public class StampMillTileEntity extends InventoryTE{

	public static final BlockEntityType<StampMillTileEntity> TYPE = CRTileEntity.createType(StampMillTileEntity::new, CRBlocks.stampMill);

	public static final int TIME_LIMIT = 100;
	public static final int INERTIA = 200;
	public static final double REQUIRED = 800;
	public static final double PROGRESS_PER_RADIAN = 20D;//Energy to consume per radian the internal gear turns
	private double progress = 0;
	private int timer = 0;
	private final IItemHandler itemHandler = new ItemHandler();


	public StampMillTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state, 2);
	}

	public int getProgress(){
		return (int) Math.round(progress);
	}

	public int getTimer(){
		return timer;
	}

	@Override
	protected boolean useRotary(){
		return true;
	}

	@Override
	protected AxleHandler createAxleHandler(){
		return new ThroughAxleHandler();
	}

	public float renderAngle(float partialTicks){
		//This machines doesn't visually switch rotation direction when rotary direction changes
		float prev = axleHandler.getAngle(partialTicks - 1F);
		float cur = axleHandler.getAngle(partialTicks);
		return Math.signum(cur - prev) * cur;
	}

	@Override
	public void addInfo(ArrayList<Component> chat, Player player, BlockHitResult hit){
		chat.add(Component.translatable("tt.crossroads.boilerplate.progress", (int) progress, (int) REQUIRED));
		super.addInfo(chat, player, hit);
	}

	@Override
	public void serverTick(){
		super.serverTick();
		BlockState state = getBlockState();

		if(state.getBlock() != CRBlocks.stampMill){
			return;
		}

		if(inventory[1].isEmpty() && !inventory[0].isEmpty()){
			double progChange = Math.min(Math.abs(energy), Math.min(REQUIRED - progress, PROGRESS_PER_RADIAN * Math.abs(axleHandler.getSpeed()) / 20D));
			axleHandler.addEnergy(-progChange, false);
			progress += progChange;

			if(progress >= REQUIRED){
				successCraft();
			}else if(++timer >= TIME_LIMIT){
				//By default, stamp mill damping is zero
				if(level.random.nextDouble() < (progress / REQUIRED) * (1D - CRConfig.stampMillDamping.get() / 100D)){
					successCraft();
				}else{
					failCraft();
				}
			}

			setChanged();
		}
	}

	private void successCraft(){
		progress = 0;
		timer = 0;
		level.playLocalSound(worldPosition.getX() + 0.5, worldPosition.getY() + 1, worldPosition.getZ() + 0.5, SoundEvents.ANVIL_PLACE, SoundSource.BLOCKS, 1, level.random.nextFloat(), true);
		Optional<RecipeHolder<StampMillRec>> recOpt = level.getRecipeManager().getRecipeFor(CRRecipes.STAMP_MILL_TYPE, this, level);
		ItemStack produced;
		if(recOpt.isPresent()){
			produced = recOpt.get().value().getResultItem();
			produced = produced.copy();
		}else{
			produced = inventory[0].copy();
			produced.setCount(1);
		}
		inventory[0].shrink(1);
		inventory[1] = produced;
	}

	private void failCraft(){
		progress = 0;
		timer = 0;
		inventory[1] = inventory[0].split(1);
	}

	@Override
	protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries){
		super.saveAdditional(nbt, pRegistries);
		nbt.putDouble("prog", progress);
		nbt.putInt("timer", timer);
	}

	@Override
	public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries){
		super.loadAdditional(nbt, registries);
		progress = nbt.getDouble("prog");
		timer = nbt.getInt("timer");
	}

	@Override
	public void setBlockState(BlockState stateIn){
		super.setBlockState(stateIn);
	}

	@Override
	@Nullable
	public IAxleHandler getAxleHandler(Direction dir){
		if(dir == null || dir.getAxis() == getBlockState().getValue(CRProperties.HORIZ_AXIS)){
			return axleHandler;
		}
		return null;
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player){
		return new StampMillContainer(id, playerInv, createContainerBuf());
	}

	private class ThroughAxleHandler extends AxleHandler{

		@Override
		public void propagate(IAxisHandler masterIn, byte key, double rotRatioIn, double lastRadius, boolean renderOffset){
			//If true, this has already been checked.
			if(key == updateKey || masterIn.addToList(this)){
				return;
			}

			rotRatio = rotRatioIn == 0 ? 1 : rotRatioIn;
			updateKey = key;
			axis = masterIn;

			BlockState state = getBlockState();
			if(state.getBlock() != CRBlocks.stampMill){
				return;
			}
			Direction.Axis ax = state.getValue(CRProperties.HORIZ_AXIS);
			for(Direction.AxisDirection dir : Direction.AxisDirection.values()){
				Direction side = Direction.get(dir, ax);
				RotaryUtil.propagateAxially(level.getBlockEntity(worldPosition.relative(side)), side.getOpposite(), this, masterIn, key, renderOffset);
			}
		}
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction){
		return index == 1;
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack){
		return index == 0 && level.getRecipeManager().getRecipeFor(CRRecipes.STAMP_MILL_TYPE, new SingleRecipeInput(stack), level).isPresent();
	}

	@Override
	public Component getDisplayName(){
		return Component.translatable("container.stamp_mill");
	}

	@Override
	public double getMoInertia(){
		return INERTIA;
	}

	private static final AABB RENDER_BOX = new AABB(0, 0, 0, 1, 2, 1);

	@Override
	public AABB getRenderBoundingBox(){
		return RENDER_BOX.move(worldPosition);
	}
}
