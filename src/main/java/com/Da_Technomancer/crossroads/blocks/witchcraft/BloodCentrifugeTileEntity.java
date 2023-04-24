package com.Da_Technomancer.crossroads.blocks.witchcraft;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.api.Capabilities;
import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.api.templates.InventoryTE;
import com.Da_Technomancer.crossroads.api.witchcraft.EntityTemplate;
import com.Da_Technomancer.crossroads.api.witchcraft.IPerishable;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.crossroads.gui.container.BloodCentrifugeContainer;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.witchcraft.BloodSample;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class BloodCentrifugeTileEntity extends InventoryTE{

	public static final BlockEntityType<BloodCentrifugeTileEntity> TYPE = CRTileEntity.createType(BloodCentrifugeTileEntity::new, CRBlocks.bloodCentrifuge);

	public static final double LOW_SPEED = 0;
	public static final double HIGH_SPEED = 10;
	public static final int REQUIRED = 100;
	public static final int INERTIA = 100;

	private int progress = 0;
	private int deviation = 0;

	public BloodCentrifugeTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state, 4);//Input: 0, 1; Output: 2, 3
	}

	@Override
	public void addInfo(ArrayList<Component> chat, Player player, BlockHitResult hit){
		chat.add(Component.translatable("tt.crossroads.blood_centrifuge.deviation", progress == 0 ? 0 : deviation / progress));
		chat.add(Component.translatable("tt.crossroads.boilerplate.progress", progress, REQUIRED));
		super.addInfo(chat, player, hit);
	}

	@Override
	protected boolean useRotary(){
		return true;
	}

	public double getTargetSpeed(){
		return getTargetSpeed(progress);
	}

	public static double getTargetSpeed(int progress){
		return LOW_SPEED + (HIGH_SPEED - LOW_SPEED) * progress / REQUIRED;
	}

	public int getProgress(){
		return progress;
	}

	@Override
	protected double getMoInertia(){
		return INERTIA;
	}

	@Override
	public void serverTick(){
		super.serverTick();

		if((!inventory[0].isEmpty() || !inventory[1].isEmpty()) && (inventory[0].isEmpty() || inventory[2].isEmpty()) && (inventory[1].isEmpty() || inventory[3].isEmpty())){
			//Check we have an input and all relevant output slots are empty
			double targetSpeed = getTargetSpeed();
			//Add difference between target and actual speed magnitude, rounded down
			deviation += (int) Math.abs(Math.abs(axleHandler.getSpeed()) - targetSpeed);
			progress++;
			if(progress >= REQUIRED){
				int degradation = (int) Math.round(CRConfig.bloodCentrifugeMult.get() * (double) deviation / REQUIRED);//Average value of deviation increment
				for(int i = 0; i < 2; i++){
					if(!inventory[i].isEmpty()){
						EntityTemplate template = BloodSample.getEntityTypeData(inventory[i]);
						//Increase degradation based on deviation
						template.setDegradation(template.getDegradation() + degradation);
						//Sets the output to a copy of the input with the item as a separated blood sample instead of normal blood sample
						//Has to copy spoil time and template
						inventory[2 + i] = IPerishable.setSpoilTime(CRItems.separatedBloodSample.withEntityData(new ItemStack(CRItems.separatedBloodSample, 1), template), IPerishable.getAndInitSpoilTime(inventory[i], level), 0);
						inventory[i] = ItemStack.EMPTY;
					}
				}
			}
			setChanged();
		}else if(progress != 0){
			progress = 0;
			deviation = 0;
			setChanged();
		}
	}

	@Override
	public void saveAdditional(CompoundTag nbt){
		super.saveAdditional(nbt);
		nbt.putInt("progress", progress);
		nbt.putInt("deviation", deviation);
	}

	@Override
	public void load(CompoundTag nbt){
		super.load(nbt);
		progress = nbt.getInt("progress");
		deviation = nbt.getInt("deviation");
	}

	@Override
	public CompoundTag getUpdateTag(){
		CompoundTag nbt = super.getUpdateTag();
		nbt.putInt("progress", progress);
		return nbt;
	}

	@Override
	public void setChanged(){
		super.setChanged();
		if(level != null && !level.isClientSide){
			//Update the blockstate in the world
			BlockState state = getBlockState();
			int inputCount = (inventory[0].isEmpty() ? 0 : 1) + (inventory[1].isEmpty() ? 0 : 1);
			if(state.getValue(CRProperties.CONTENTS) != inputCount){
				//No block update
				level.setBlock(worldPosition, state.setValue(CRProperties.CONTENTS, inputCount), MiscUtil.BLOCK_FLAGS_VISUAL);
			}
		}
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction side){
		return index == 2 || index == 3;
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack){
		if(!super.canPlaceItem(index, stack) || index != 0 && index != 1){
			return false;
		}
		return stack.getItem() == CRItems.bloodSample;
	}

	@Override
	public Component getDisplayName(){
		return Component.translatable("container.crossroads.blood_centrifuge");
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player playerEntity){
		return new BloodCentrifugeContainer(id, playerInventory, createContainerBuf());
	}

	@Override
	public void setRemoved(){
		super.setRemoved();
		itemOpt.invalidate();
	}

	private final LazyOptional<IItemHandler> itemOpt = LazyOptional.of(ItemHandler::new);

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing){
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			return (LazyOptional<T>) itemOpt;
		}
		if(capability == Capabilities.AXLE_CAPABILITY && facing == Direction.UP){
			return (LazyOptional<T>) axleOpt;
		}

		return super.getCapability(capability, facing);
	}
}
