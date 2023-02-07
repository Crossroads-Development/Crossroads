package com.Da_Technomancer.crossroads.blocks.rotary;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.api.Capabilities;
import com.Da_Technomancer.crossroads.api.templates.InventoryTE;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.crossroads.gui.container.WindingTableContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class WindingTableTileEntity extends InventoryTE{

	public static final BlockEntityType<WindingTableTileEntity> TYPE = CRTileEntity.createType(WindingTableTileEntity::new, CRBlocks.windingTable);

	public static final double INERTIA = 50;
	public static final double INCREMENT = 0.2;

	private boolean redstone = false;//Whether this block was powered by redstone

	public WindingTableTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state, 1);
	}

	@Override
	public void addInfo(ArrayList<Component> chat, Player player, BlockHitResult hit){
		if(inventory[0].getItem() instanceof IWindableItem item){
			double speed = item.getWindLevel(inventory[0]);
			double maxSpeed = item.getMaxWind();
			chat.add(Component.translatable("tt.crossroads.winding_table.winding", CRConfig.formatVal(speed), CRConfig.formatVal(maxSpeed)));
			chat.add(Component.translatable("tt.crossroads.winding_table.power", CRConfig.formatVal(speed * CRConfig.windingResist.get())));
		}else{
			chat.add(Component.translatable("tt.crossroads.winding_table.empty"));
			chat.add(Component.translatable("tt.crossroads.winding_table.power", 0));
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
			if(reds && inventory[0].getItem() instanceof IWindableItem item && !item.isBroken(inventory[0])){
				double itemSpeed = item.getWindLevel(inventory[0]);

				if(itemSpeed < item.getMaxWind()){
					//Wind the item
					itemSpeed += INCREMENT;
					itemSpeed = Math.min(itemSpeed, item.getMaxWind());
					item.setWindLevel(inventory[0], itemSpeed);
					level.playSound(null, worldPosition, SoundEvents.METAL_PRESSURE_PLATE_CLICK_ON, SoundSource.BLOCKS, 1F, (float) Math.random());
				}else{
					level.playSound(null, worldPosition, SoundEvents.METAL_PRESSURE_PLATE_CLICK_OFF, SoundSource.BLOCKS, 2F, (float) Math.random());
				}
			}
			setChanged();
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
		if(inventory[0].getItem() instanceof IWindableItem item){
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
	public void serverTick(){
		super.serverTick();

		if(!(inventory[0].getItem() instanceof IWindableItem item)){
			return;
		}

		double itemSpeed = item.getWindLevel(inventory[0]);
		if(itemSpeed > axleHandler.getSpeed()){
			//Machine speed too slow
			//Release all stored energy
			axleHandler.addEnergy(INERTIA * itemSpeed * itemSpeed / 2D, true);
			level.playSound(null, worldPosition, SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, 1F, 1F);
			if(CRConfig.windingDestroy.get()){
				//Break the item
				item.setBrokenState(inventory[0], true);
				setChanged();
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
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction){
		return true;
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack){
		return index == 0 && stack.getItem() instanceof IWindableItem;
	}

	@Override
	public Component getDisplayName(){
		return Component.translatable("container.winding_table");
	}

	@Override
	public void load(CompoundTag nbt){
		super.load(nbt);
		redstone = nbt.getBoolean("reds");
	}

	@Override
	public void saveAdditional(CompoundTag nbt){
		super.saveAdditional(nbt);
		nbt.putBoolean("reds", redstone);
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
	public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player){
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
			return isBroken(stack) ? 0 : stack.getOrCreateTag().getDouble("winding_energy");
		}

		default void setBrokenState(ItemStack stack, boolean isBroken){
			stack.getOrCreateTag().putBoolean("winding_broken", isBroken);
		}

		default boolean isBroken(ItemStack stack){
			return stack.getOrCreateTag().getBoolean("winding_broken");
		}

		default void appendTooltip(ItemStack stack, List<Component> tooltip, TooltipFlag flagIn){
			if(isBroken(stack)){
				tooltip.add(Component.translatable("tt.crossroads.boilerplate.spring_broken"));
			}else{
				double wind = getWindLevel(stack);
				double maxWind = getMaxWind();
				tooltip.add(Component.translatable("tt.crossroads.boilerplate.spring_speed", CRConfig.formatVal(wind), CRConfig.formatVal(maxWind)));
			}
		}
	}
}
