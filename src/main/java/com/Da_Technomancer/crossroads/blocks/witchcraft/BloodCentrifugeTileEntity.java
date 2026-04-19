package com.Da_Technomancer.crossroads.blocks.witchcraft;

import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.api.rotary.IAxleCapable;
import com.Da_Technomancer.crossroads.api.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.api.templates.InventoryTE;
import com.Da_Technomancer.crossroads.api.witchcraft.EntityTemplate;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.crossroads.gui.container.BloodCentrifugeContainer;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.witchcraft.BloodSample;
import com.Da_Technomancer.essentials.api.IItemCapable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class BloodCentrifugeTileEntity extends InventoryTE implements IAxleCapable, IItemCapable{

	public static final BlockEntityType<BloodCentrifugeTileEntity> TYPE = CRTileEntity.createType(BloodCentrifugeTileEntity::new, CRBlocks.bloodCentrifuge);

	public static final double LOW_SPEED = 0;
	public static final double HIGH_SPEED = 10;
	public static final int REQUIRED = 100;
	public static final int INERTIA = 100;
	public static final double MAX_ADDED_QUALITY = 50;

	private int progress = 0;
	private double deviation = 0;

	private final IItemHandler itemHandler = new ItemHandler();


	public BloodCentrifugeTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state, 4);//Input: 0, 1; Output: 2, 3
	}

	@Override
	public void addInfo(ArrayList<Component> chat, Player player, BlockHitResult hit){
		chat.add(Component.translatable("tt.crossroads.blood_centrifuge.deviation", progress == 0 ? 0 : deviation / progress));
		chat.add(Component.translatable("tt.crossroads.boilerplate.progress", progress, REQUIRED));
		super.addInfo(chat, player, hit);
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
			//Add difference between target and actual speed magnitude
			deviation += Math.abs(Math.abs(axleHandler.getSpeed()) - targetSpeed);
			progress++;
			if(progress >= REQUIRED){
				final double avgDeviation = deviation / REQUIRED;
				//This formula is a quadratic tuned for gameplay, not anything fundamental
				//Basically, baring a redstone master axis, reducing avgDeviation using rotary setups gets increasingly difficult as it gets closer to zero
				//So to not give diminishing returns for clever designs or high-effort builds, we rig the formula to award increasing amounts of quality points for finer-and-finer improvements (to a cap)
				int qualityChange = (int) Math.ceil(MAX_ADDED_QUALITY * Math.max(0D, Math.min(1D, .125D * avgDeviation * avgDeviation - 0.8D * avgDeviation + 1.25D)));
				if(avgDeviation > 3D){
					//Since it's a quadratic, it can push back into the positives at extreme values, which is unwanted. Correct for this.
					qualityChange = 0;
				}
				for(int i = 0; i < 2; i++){
					if(!inventory[i].isEmpty()){
						EntityTemplate template = BloodSample.getBaseTemplate(inventory[i]);
						//Sets the output to a copy of the input with the item as a separated blood sample instead of normal blood sample
						//Has to copy spoil time, template, any other data
						inventory[2 + i] = inventory[i].transmuteCopy(CRItems.separatedBloodSample);
						//Upgrade the quality
						inventory[2 + i].set(CRItems.GENETICS_DATA, template.withQuality(template.quality() + qualityChange));
//						IPerishable.setSpoilTime(CRItems.separatedBloodSample.withEntityData(new ItemStack(CRItems.separatedBloodSample, 1), template), IPerishable.getAndInitSpoilTime(inventory[i], level), 0);
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
	protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries){
		super.saveAdditional(nbt, pRegistries);
		nbt.putInt("progress", progress);
		nbt.putDouble("deviation", deviation);
	}

	@Override
	public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries){
		super.loadAdditional(nbt, registries);
		progress = nbt.getInt("progress");
		deviation = nbt.getDouble("deviation");
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries){
		CompoundTag nbt = super.getUpdateTag(pRegistries);
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

	@Nullable
	@Override
	public IItemHandler getItemHandler(Direction direction){
		return itemHandler;
	}

	@Override
	@Nullable
	public IAxleHandler getAxleHandler(Direction dir){
		if(dir == Direction.UP || dir == null){
			return axleHandler;
		}
		return null;
	}
}
