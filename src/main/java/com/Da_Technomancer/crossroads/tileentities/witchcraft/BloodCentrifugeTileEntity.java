package com.Da_Technomancer.crossroads.tileentities.witchcraft;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.API.witchcraft.EntityTemplate;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.gui.container.BloodCentrifugeContainer;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.witchcraft.BloodSample;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;
import java.util.ArrayList;

@ObjectHolder(Crossroads.MODID)
public class BloodCentrifugeTileEntity extends InventoryTE{

	@ObjectHolder("blood_centrifuge")
	public static TileEntityType<BloodCentrifugeTileEntity> type = null;

	public static final double LOW_SPEED = 0;
	public static final double HIGH_SPEED = 20;
	public static final int REQUIRED = 100;

	private int progress = 0;
	private int deviation = 0;

	public BloodCentrifugeTileEntity(){
		super(type, 4);//Input: 0, 1; Output: 2, 3
	}

	@Override
	public void addInfo(ArrayList<ITextComponent> chat, PlayerEntity player, BlockRayTraceResult hit){
		chat.add(new TranslationTextComponent("tt.crossroads.blood_centrifuge.deviation", progress == 0 ? 0 : deviation / progress));
		chat.add(new TranslationTextComponent("tt.crossroads.boilerplate.progress", progress, REQUIRED));
		super.addInfo(chat, player, hit);
	}

	@Override
	protected boolean useRotary(){
		return true;
	}

	@Override
	protected AxleHandler createAxleHandler(){
		return new AngleAxleHandler();
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
		return 100;
	}

	@Override
	public void tick(){
		super.tick();

		if(!level.isClientSide){
			if((!inventory[0].isEmpty() || !inventory[1].isEmpty()) && (inventory[0].isEmpty() || inventory[2].isEmpty()) && (inventory[1].isEmpty() || inventory[3].isEmpty())){
				//Check we have an input and all relevant output slots are empty
				double targetSpeed = getTargetSpeed();
				//Add difference between target and actual speed magnitude, rounded down
				deviation += (int) Math.abs(Math.abs(axleHandler.getSpeed()) - targetSpeed);
				progress++;
				if(progress >= REQUIRED){
					int degradation = deviation / REQUIRED;//Average value of deviation increment
					for(int i = 0; i < 2; i++){
						if(!inventory[i].isEmpty()){
							EntityTemplate template = BloodSample.getEntityTypeData(inventory[i]);
							//Increase degradation based on deviation
							template.setDegradation(template.getDegradation() + degradation);
							//Sets the output to a copy of the input with the item as a separated blood sample instead of normal blood sample
							//Has to copy spoil time and template
							inventory[2 + i] = CRItems.separatedBloodSample.setSpoilTime(CRItems.separatedBloodSample.withEntityData(new ItemStack(CRItems.separatedBloodSample, 1), template), CRItems.bloodSample.getSpoilTime(inventory[i], level), 0);
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
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt){
		super.save(nbt);
		nbt.putInt("progress", progress);
		nbt.putInt("deviation", deviation);
		return nbt;
	}

	@Override
	public void load(BlockState state, CompoundNBT nbt){
		super.load(state, nbt);
		progress = nbt.getInt("progress");
		deviation = nbt.getInt("deviation");
	}

	@Override
	public CompoundNBT getUpdateTag(){
		CompoundNBT nbt = super.getUpdateTag();
		nbt.putInt("progress", progress);
		return nbt;
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
	public ITextComponent getDisplayName(){
		return new TranslationTextComponent("container.crossroads.blood_centrifuge");
	}

	@Nullable
	@Override
	public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity playerEntity){
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
