package com.Da_Technomancer.crossroads.tileentities.rotary;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.rotary.IAxisHandler;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.crafting.CRRecipes;
import com.Da_Technomancer.crossroads.crafting.recipes.StampMillRec;
import com.Da_Technomancer.crossroads.gui.container.StampMillContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Optional;

@ObjectHolder(Crossroads.MODID)
public class StampMillTileEntity extends InventoryTE{

	@ObjectHolder("stamp_mill")
	public static BlockEntityType<StampMillTileEntity> TYPE = null;

	public static final int TIME_LIMIT = 100;
	public static final int INERTIA = 200;
	public static final double REQUIRED = 800;
	public static final double PROGRESS_PER_RADIAN = 20D;//Energy to consume per radian the internal gear turns
	private double progress = 0;
	private int timer = 0;

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
		chat.add(new TranslatableComponent("tt.crossroads.boilerplate.progress", (int) progress, (int) REQUIRED));
		super.addInfo(chat, player, hit);
	}

	@Override
	public void serverTick(){
		super.serverTick();
		BlockState state = getBlockState();

		if(state.getBlock() != CRBlocks.stampMill){
			return;
		}

		double progChange = Math.min(Math.abs(energy), Math.min(REQUIRED - progress, PROGRESS_PER_RADIAN * Math.abs(axleHandler.getSpeed()) / 20D));
		axleHandler.addEnergy(-progChange, false);
		if(inventory[1].isEmpty() && !inventory[0].isEmpty()){
			progress += progChange;
			if(++timer >= TIME_LIMIT || progress >= REQUIRED){
				timer = 0;
				if(progress >= REQUIRED){
					progress = 0;
					level.playLocalSound(worldPosition.getX() + 0.5, worldPosition.getY() + 1, worldPosition.getZ() + 0.5, SoundEvents.ANVIL_PLACE, SoundSource.BLOCKS, 1, level.random.nextFloat(), true);
					Optional<StampMillRec> recOpt = level.getRecipeManager().getRecipeFor(CRRecipes.STAMP_MILL_TYPE, this, level);
					ItemStack produced;
					if(recOpt.isPresent()){
						produced = recOpt.get().getResultItem();
						produced = produced.copy();
					}else{
						produced = inventory[0].copy();
						produced.setCount(1);
					}
					inventory[0].shrink(1);
					inventory[1] = produced;
				}else{
					inventory[1] = inventory[0].split(1);
					progress -= REQUIRED * CRConfig.stampMillDamping.get() / 100;//By default, stamp mill damping is zero
					if(progress < 0){
						progress = 0;
					}
				}
			}
			setChanged();
		}
	}

	@Override
	public CompoundTag save(CompoundTag nbt){
		super.save(nbt);
		nbt.putDouble("prog", progress);
		nbt.putInt("timer", timer);
		return nbt;
	}

	@Override
	public void load(CompoundTag nbt){
		super.load(nbt);
		progress = nbt.getDouble("prog");
		timer = nbt.getInt("timer");
	}

	@Override
	public void setRemoved(){
		super.setRemoved();
		itemOpt.invalidate();
	}

	@Override
	public void setBlockState(BlockState stateIn){
		super.setBlockState(stateIn);
		axleOpt.invalidate();
		axleOpt = LazyOptional.of(() -> axleHandler);
	}

	private final LazyOptional<IItemHandler> itemOpt = LazyOptional.of(ItemHandler::new);

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side){
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			return (LazyOptional<T>) itemOpt;
		}

		BlockState state = getBlockState();
		if(state.getBlock() == CRBlocks.stampMill && cap == Capabilities.AXLE_CAPABILITY && (side == null || side.getAxis() == state.getValue(CRProperties.HORIZ_AXIS))){
			return (LazyOptional<T>) axleOpt;
		}

		return super.getCapability(cap, side);
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
				BlockEntity te = level.getBlockEntity(worldPosition.relative(side));
				if(te != null){
					LazyOptional<IAxisHandler> axisOpt = te.getCapability(Capabilities.AXIS_CAPABILITY, side.getOpposite());
					if(axisOpt.isPresent()){
						axisOpt.orElseThrow(NullPointerException::new).trigger(masterIn, key);
					}
					LazyOptional<IAxleHandler> oAxleOpt = te.getCapability(Capabilities.AXLE_CAPABILITY, side.getOpposite());
					if(oAxleOpt.isPresent()){
						oAxleOpt.orElseThrow(NullPointerException::new).propagate(masterIn, key, rotRatioIn, lastRadius, renderOffset);
					}
				}
			}
		}
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction){
		return index == 1;
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack){
		return index == 0 && level.getRecipeManager().getRecipeFor(CRRecipes.STAMP_MILL_TYPE, new SimpleContainer(stack), level).isPresent();
	}

	@Override
	public Component getDisplayName(){
		return new TranslatableComponent("container.stamp_mill");
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
