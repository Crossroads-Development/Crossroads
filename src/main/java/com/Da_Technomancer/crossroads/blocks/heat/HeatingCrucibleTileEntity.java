package com.Da_Technomancer.crossroads.blocks.heat;

import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.api.heat.HeatUtil;
import com.Da_Technomancer.crossroads.api.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.api.packets.CRPackets;
import com.Da_Technomancer.crossroads.api.templates.InventoryTE;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.crossroads.crafting.CRRecipes;
import com.Da_Technomancer.crossroads.crafting.CrucibleRec;
import com.Da_Technomancer.crossroads.gui.container.CrucibleContainer;
import com.Da_Technomancer.essentials.api.BlockUtil;
import com.Da_Technomancer.essentials.api.packets.INBTReceiver;
import com.Da_Technomancer.essentials.api.packets.SendNBTToTE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Optional;

public class HeatingCrucibleTileEntity extends InventoryTE implements INBTReceiver{

	public static final BlockEntityType<HeatingCrucibleTileEntity> TYPE = CRTileEntity.createType(HeatingCrucibleTileEntity::new, CRBlocks.heatingCrucible);

	public static final int[] TEMP_TIERS = {1000, 1500, 2500};
	public static final int USAGE = 20;
	public static final int REQUIRED = 1000;
	private int progress = 0;
	/**
	 * The fluid to be displayed for rendering in-world. Quantity is NOT synced; only type, NBT, empty or not empty
	 * On server side, acts as a record of what was sent to client
	 */
	private FluidStack renderFluid = FluidStack.EMPTY;
	/**
	 * Cache for the texture and color to be displayed, if any.
	 */
	@Nullable
	private ResourceLocation activeText = null;
	private Integer col = null;//Color applied to the liquid texture

	private final IItemHandler itemHandler = new ItemHandler();

	public HeatingCrucibleTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state, 1);
		fluidProps[0] = new TankProperty(4_000, false, true);
		initFluidManagers();
	}

	public int getProgress(){
		return progress;
	}

	@Override
	protected int fluidTanks(){
		return 1;
	}

	@Override
	protected boolean useHeat(){
		return true;
	}

	@Override
	public void receiveNBT(CompoundTag nbt, @Nullable ServerPlayer sender){
		if(level.isClientSide && nbt.contains("render_fluid")){
			renderFluid = BlockUtil.nbtToFluidStack(nbt, level.registryAccess());
			updateRendering();
		}
	}

	private void updateRendering(){
		//Call on the client-side only
		IClientFluidTypeExtensions renderProps = IClientFluidTypeExtensions.of(renderFluid.getFluid());
		activeText = renderProps.getStillTexture(renderFluid);
		col = renderProps.getTintColor(renderFluid);
	}

	public ResourceLocation getActiveTexture(){
		return activeText;
	}

	public Color getCol(){
		return activeText == null ? Color.WHITE : col == null ? Color.WHITE : new Color(col);
	}

	@Override
	public void serverTick(){
		super.serverTick();

		if(level.getGameTime() % 2 == 0){
			int fullness = Math.min(3, (int) Math.ceil((float) fluids[0].getAmount() * 3F / (float) fluidProps[0].capacity));
			BlockState state = getBlockState();
			if(state.getBlock() != CRBlocks.heatingCrucible){
				setRemoved();
				return;
			}

			if(state.getValue(CRProperties.FULLNESS) != fullness){
				level.setBlock(worldPosition, state.setValue(CRProperties.FULLNESS, fullness), 18);
			}

			if(!BlockUtil.sameFluid(renderFluid, fluids[0])){
				renderFluid = fluids[0].copy();
				CompoundTag nbt = BlockUtil.stackToNBT(renderFluid, level.registryAccess());
				nbt.putBoolean("render_fluid", true);
				CRPackets.sendPacketAround(level, worldPosition, new SendNBTToTE(nbt, worldPosition));
			}
		}

		int tier = HeatUtil.getHeatTier(temp, TEMP_TIERS);

		if(tier >= 0){
			temp -= USAGE * (tier + 1);
			if(inventory[0].isEmpty()){
				progress = 0;
			}else{
				progress = Math.min(REQUIRED, progress + USAGE * (tier + 1));
				if(progress >= REQUIRED){
					Optional<RecipeHolder<CrucibleRec>> recOpt = level.getRecipeManager().getRecipeFor(CRRecipes.CRUCIBLE_TYPE, this, level);
					if(recOpt.isPresent()){
						FluidStack created = recOpt.get().value().getOutput();
						if(fluidProps[0].capacity - fluids[0].getAmount() >= created.getAmount() && (fluids[0].isEmpty() || BlockUtil.sameFluid(fluids[0], created))){
							progress = 0;
							if(fluids[0].isEmpty()){
								fluids[0] = created.copy();
							}else{
								fluids[0].grow(created.getAmount());
							}
							inventory[0].shrink(1);
							setChanged();
						}
					}else{
						inventory[0] = ItemStack.EMPTY;
					}
				}
			}

			setChanged();
		}
	}

	@Override
	public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries){
		super.loadAdditional(nbt, registries);
		renderFluid = BlockUtil.nbtToFluidStack(nbt.getCompound("render_fluid"), registries);
		if(nbt.getBoolean("is_client")){
			updateRendering();
		}
		progress = nbt.getInt("prog");
	}

	@Override
	protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries){
		super.saveAdditional(nbt, pRegistries);
		nbt.put("render_fluid", BlockUtil.stackToNBT(renderFluid, pRegistries));
		nbt.putBoolean("is_client", true);
		nbt.putInt("prog", progress);
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries){
		CompoundTag nbt = super.getUpdateTag(pRegistries);
		nbt.put("render_fluid", BlockUtil.stackToNBT(renderFluid, pRegistries));
		return nbt;
	}

	@Nullable
	@Override
	public IItemHandler getItemHandler(Direction direction){
		return itemHandler;
	}

	@Override
	@Nullable
	public IFluidHandler getFluidHandler(Direction dir){
		if(dir != Direction.UP){
			return globalFluidHandler;
		}
		return null;
	}

	@Override
	@Nullable
	public IHeatHandler getHeatHandler(Direction dir){
		if(dir != Direction.UP){
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
		return index == 0 && level.getRecipeManager().getRecipeFor(CRRecipes.CRUCIBLE_TYPE, new SingleRecipeInput(stack), level).isPresent();
	}

	@Override
	public Component getDisplayName(){
		return Component.translatable("container.crucible");
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player playerEntity){
		return new CrucibleContainer(id, playerInventory, createContainerBuf());
	}
}
