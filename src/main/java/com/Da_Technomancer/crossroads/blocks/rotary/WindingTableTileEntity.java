package com.Da_Technomancer.crossroads.blocks.rotary;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.api.rotary.IAxleCapable;
import com.Da_Technomancer.crossroads.api.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.api.templates.ICreativeTabPopulatingItem;
import com.Da_Technomancer.crossroads.api.templates.InventoryTE;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.crossroads.gui.container.WindingTableContainer;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.essentials.api.IItemCapable;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class WindingTableTileEntity extends InventoryTE implements IAxleCapable, IItemCapable{

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
			chat.add(Component.translatable("tt.crossroads.winding_table.winding", CRConfig.formatVal(speed, player), CRConfig.formatVal(maxSpeed, player)));
			chat.add(Component.translatable("tt.crossroads.winding_table.power", CRConfig.formatVal(speed * CRConfig.windingResist.get(), player)));
		}else{
			chat.add(Component.translatable("tt.crossroads.winding_table.empty"));
			chat.add(Component.translatable("tt.crossroads.winding_table.power", 0));
		}

		super.addInfo(chat, player, hit);
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
	public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries){
		super.loadAdditional(nbt, registries);
		redstone = nbt.getBoolean("reds");
	}

	@Override
	protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries){
		super.saveAdditional(nbt, pRegistries);
		nbt.putBoolean("reds", redstone);
	}

	@Override
	@Nullable
	public IAxleHandler getAxleHandler(Direction dir){
		if(dir == null || dir == Direction.UP){
			return axleHandler;
		}
		return null;
	}

	@Nullable
	@Override
	public IItemHandler getItemHandler(Direction direction){
		return itemHandler;
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player){
		return new WindingTableContainer(id, playerInv, createContainerBuf());
	}

	public interface IWindableItem extends ICreativeTabPopulatingItem{

		/**
		 * In rad/s
		 * @return The maximum winding speed on this item
		 */
		double getMaxWind();

		default void setWindLevel(ItemStack stack, double energy){
			stack.set(CRItems.WINDING_DATA, new WindingStatus(isBroken(stack), energy));
		}

		default double getWindLevel(ItemStack stack){
			return stack.getOrDefault(CRItems.WINDING_DATA, WindingStatus.DEFAULT).windingLevel();
		}

		default void setBrokenState(ItemStack stack, boolean isBroken){
			stack.set(CRItems.WINDING_DATA, new WindingStatus(isBroken, getWindLevel(stack)));
		}

		default boolean isBroken(ItemStack stack){
			return stack.getOrDefault(CRItems.WINDING_DATA, WindingStatus.DEFAULT).broken();
		}

		default void appendTooltip(ItemStack stack, List<Component> tooltip, TooltipFlag flagIn){
			if(isBroken(stack)){
				tooltip.add(Component.translatable("tt.crossroads.boilerplate.spring_broken"));
			}else{
				double wind = getWindLevel(stack);
				double maxWind = getMaxWind();
				tooltip.add(Component.translatable("tt.crossroads.boilerplate.spring_speed", CRConfig.formatVal(wind, null), CRConfig.formatVal(maxWind, null)));
			}
		}

		@Nonnull
		@Override
		default ItemStack[] populateCreativeTab(){
			ItemStack woundStack = new ItemStack((Item) this);
			setWindLevel(woundStack, getMaxWind());
			return new ItemStack[] {new ItemStack((Item) this), woundStack};
		}
	}

	public static record WindingStatus(boolean broken, double windingLevel){

		public static final WindingStatus DEFAULT = new WindingStatus(false, 0);
		public static final Codec<WindingStatus> CODEC = RecordCodecBuilder.create(instance -> instance.group(Codec.BOOL.fieldOf("broken").forGetter(WindingStatus::broken), Codec.DOUBLE.fieldOf("winding_level").forGetter(WindingStatus::windingLevel)).apply(instance, WindingStatus::new));
		public static final StreamCodec<ByteBuf, WindingStatus> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.BOOL, WindingStatus::broken, ByteBufCodecs.DOUBLE, WindingStatus::windingLevel, WindingStatus::new);

		@Override
		public double windingLevel(){
			return broken ? 0 : windingLevel;
		}
	}
}
