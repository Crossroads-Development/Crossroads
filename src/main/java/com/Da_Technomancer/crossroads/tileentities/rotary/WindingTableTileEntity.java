package com.Da_Technomancer.crossroads.tileentities.rotary;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.gui.container.WindingTableContainer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
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
public class WindingTableTileEntity extends InventoryTE{

	@ObjectHolder("winding_table")
	private static TileEntityType<WindingTableTileEntity> type = null;

	public static final double INERTIA = 50;
	public static final double INCREMENT = 0.2;

	private boolean redstone = false;//Whether this block was powered by redstone

	public WindingTableTileEntity(){
		super(type, 1);
	}

	@Override
	public void addInfo(ArrayList<ITextComponent> chat, PlayerEntity player, BlockRayTraceResult hit){
		if(inventory[0].getItem() instanceof IWindableItem){
			IWindableItem item = (IWindableItem) inventory[0].getItem();
			double speed = item.getWindLevel(inventory[0]);
			double maxSpeed = item.getMaxWind();
			chat.add(new TranslationTextComponent("tt.crossroads.winding_table.winding", CRConfig.formatVal(speed), CRConfig.formatVal(maxSpeed)));
			chat.add(new TranslationTextComponent("tt.crossroads.winding_table.power", CRConfig.formatVal(speed * CRConfig.windingResist.get())));
		}else{
			chat.add(new TranslationTextComponent("tt.crossroads.winding_table.empty"));
			chat.add(new TranslationTextComponent("tt.crossroads.winding_table.power", 0));
		}

		super.addInfo(chat, player, hit);
	}

	@Override
	protected boolean useRotary(){
		return true;
	}

	public void redstoneTrigger(boolean reds){
		if(reds != redstone){
			redstone = reds;
			if(reds && inventory[0].getItem() instanceof IWindableItem){
				IWindableItem item = (IWindableItem) inventory[0].getItem();
				double itemSpeed = item.getWindLevel(inventory[0]);

				if(itemSpeed < item.getMaxWind()){
					//Wind the item
					itemSpeed += INCREMENT;
					itemSpeed = Math.min(itemSpeed, item.getMaxWind());
					item.setWindLevel(inventory[0], itemSpeed);
					world.playSound(null, pos, SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_ON, SoundCategory.BLOCKS, 1F, (float) Math.random());
				}else{
					world.playSound(null, pos, SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF, SoundCategory.BLOCKS, 2F, (float) Math.random());
				}
			}
			markDirty();
		}
	}

	public double getStoredSpeed(){
		if(inventory[0].getItem() instanceof IWindableItem){
			return ((IWindableItem) inventory[0].getItem()).getWindLevel(inventory[0]);
		}
		return 0;
	}

	public int getProgress(){
		//Server side only, used for UI rendering, percentage of winding level
		if(inventory[0].getItem() instanceof IWindableItem){
			IWindableItem item = (IWindableItem) inventory[0].getItem();
			double speed = item.getWindLevel(inventory[0]);
			double maxSpeed = item.getMaxWind();
			return (int) Math.round(speed / maxSpeed * 100);
		}else{
			return 0;
		}
	}

	@Override
	public double getMoInertia(){
		return INERTIA;
	}

	@Override
	public void tick(){
		super.tick();

		if(world.isRemote || !(inventory[0].getItem() instanceof IWindableItem)){
			return;
		}

		IWindableItem item = (IWindableItem) inventory[0].getItem();
		double itemSpeed = item.getWindLevel(inventory[0]);
		if(itemSpeed > axleHandler.getSpeed()){
			//Machine speed too slow
			//Release all stored energy
			axleHandler.addEnergy(INERTIA * itemSpeed * itemSpeed / 2D, true);
			world.playSound(null, pos, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.BLOCKS, 1F, 1F);
			if(CRConfig.windingDestroy.get()){
				//Break the item
				inventory[0] = ItemStack.EMPTY;
				markDirty();
			}else{
				//Remove all stored energy
				itemSpeed = 0;
				item.setWindLevel(inventory[0], itemSpeed);
			}
		}else{
			axleHandler.addEnergy(-CRConfig.windingResist.get() * itemSpeed, false);//Apply resistance power
		}
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, Direction direction){
		return true;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack){
		return index == 0 && stack.getItem() instanceof IWindableItem;
	}

	@Override
	public ITextComponent getDisplayName(){
		return new TranslationTextComponent("container.winding_table");
	}

	@Override
	public void read(BlockState state, CompoundNBT nbt){
		super.read(state, nbt);
		redstone = nbt.getBoolean("reds");
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		nbt.putBoolean("reds", redstone);
		return nbt;
	}

	private final LazyOptional<IItemHandler> itemOpt = LazyOptional.of(ItemHandler::new);

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side){
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			return (LazyOptional<T>) itemOpt;
		}
		if(cap == Capabilities.AXLE_CAPABILITY && (side == Direction.UP || side == null)){
			return (LazyOptional<T>) axleOpt;
		}

		return super.getCapability(cap, side);
	}

	@Nullable
	@Override
	public Container createMenu(int id, PlayerInventory playerInv, PlayerEntity player){
		return new WindingTableContainer(id, playerInv, createContainerBuf());
	}

	public interface IWindableItem{

		/**
		 * In rad/s
		 * @return The maximum winding speed on this item
		 */
		double getMaxWind();

		default void setWindLevel(ItemStack stack, double energy){
			stack.getOrCreateTag().putDouble("winding_energy", Math.max(0, energy));
		}

		default double getWindLevel(ItemStack stack){
			return stack.getOrCreateTag().getDouble("winding_energy");
		}
	}
}
