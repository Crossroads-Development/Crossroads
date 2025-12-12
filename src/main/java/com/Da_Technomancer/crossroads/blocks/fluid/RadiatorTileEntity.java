package com.Da_Technomancer.crossroads.blocks.fluid;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.api.crafting.CraftingUtil;
import com.Da_Technomancer.crossroads.api.heat.IHeatCapable;
import com.Da_Technomancer.crossroads.api.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.api.templates.InventoryTE;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.crossroads.fluids.CRFluids;
import com.Da_Technomancer.crossroads.gui.container.RadiatorContainer;
import com.Da_Technomancer.essentials.api.IFluidCapable;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;

public class RadiatorTileEntity extends InventoryTE implements IHeatCapable, IFluidCapable{

	public static final BlockEntityType<RadiatorTileEntity> TYPE = CRTileEntity.createType(RadiatorTileEntity::new, CRBlocks.radiator);

	public static final int[] TIERS = new int[] {100, 200, 300, 400, 500};//Steam use per tick
	private int mode = 0;

	public RadiatorTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state, 0);
		fluidProps[0] = new TankProperty(10_000, true, false, f -> CraftingUtil.tagContains(CRFluids.STEAM, f));
		fluidProps[1] = new TankProperty(10_000, false, true);
		initFluidManagers();
	}

	@Override
	public int fluidTanks(){
		return 2;
	}

	public int getMode(){
		return mode;
	}

	public int cycleMode(){
		mode = (mode + 1) % TIERS.length;
		setChanged();
		return mode;
	}

	@Override
	public void receiveLong(byte identifier, long message, @Nullable ServerPlayer sendingPlayer){
		super.receiveLong(identifier, message, sendingPlayer);
		if(identifier == 5 && message >= 0 && message < TIERS.length){
			mode = (int) message;
			setChanged();
		}
	}

	@Override
	public void serverTick(){
		super.serverTick();

		if(fluids[0].getAmount() >= TIERS[mode] && fluidProps[1].capacity - fluids[1].getAmount() >= TIERS[mode]){
			temp += TIERS[mode] * (double) CRConfig.steamWorth.get() / 1000;
			if(fluids[1].isEmpty()){
				fluids[1] = new FluidStack(CRFluids.distilledWater.getStill(), TIERS[mode]);
			}else{
				fluids[1].grow(TIERS[mode]);
			}

			fluids[0].shrink(TIERS[mode]);
			setChanged();
		}
	}

	@Override
	@Nullable
	public IFluidHandler getFluidHandler(Direction dir){
		if(dir == null || dir.getAxis() == Direction.Axis.Y){
			return globalFluidHandler;
		}
		return null;
	}

	@Override
	@Nullable
	public IHeatHandler getHeatHandler(Direction dir){
		if(dir != Direction.UP && dir != Direction.DOWN){
			return heatHandler;
		}
		return null;
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction){
		return false;
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack){
		return false;
	}

	@Override
	public Component getDisplayName(){
		return Component.translatable("container.radiator");
	}

	public void encodeBuf(FriendlyByteBuf buf){
		buf.writeBlockPos(worldPosition);
		buf.writeByte(mode);
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player playerEntity){
		FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
		encodeBuf(buf);
		return new RadiatorContainer(id, playerInventory, buf);
	}

	@Override
	public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries){
		super.loadAdditional(nbt, registries);
		mode = nbt.getInt("mode");
	}

	@Override
	protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries){
		super.saveAdditional(nbt, pRegistries);
		nbt.putInt("mode", mode);
	}
}
