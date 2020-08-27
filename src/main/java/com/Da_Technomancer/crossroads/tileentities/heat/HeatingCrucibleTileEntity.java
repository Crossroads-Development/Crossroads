package com.Da_Technomancer.crossroads.tileentities.heat;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.API.packets.CRPackets;
import com.Da_Technomancer.crossroads.API.packets.IStringReceiver;
import com.Da_Technomancer.crossroads.API.packets.SendStringToClient;
import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.gui.container.CrucibleContainer;
import com.Da_Technomancer.crossroads.crafting.CRRecipes;
import com.Da_Technomancer.crossroads.crafting.recipes.CrucibleRec;
import com.Da_Technomancer.essentials.blocks.BlockUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Optional;

@ObjectHolder(Crossroads.MODID)
public class HeatingCrucibleTileEntity extends InventoryTE implements IStringReceiver{

	@ObjectHolder("crucible")
	public static TileEntityType<HeatingCrucibleTileEntity> type = null;

	public static final int[] TEMP_TIERS = {1000, 1500, 2500};
	public static final int USAGE = 20;
	public static final int REQUIRED = 1000;
	private int progress = 0;
	public IntReferenceHolder progressRef = IntReferenceHolder.single();
	/**
	 * The texture to be displayed, if any.
	 */
	@Nullable
	private ResourceLocation activeText = null;
	private Integer col = null;//Color applied to the liquid texture

	public HeatingCrucibleTileEntity(){
		super(type, 1);
		fluidProps[0] = new TankProperty(4_000, false, true);
		initFluidManagers();
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
	public void receiveString(byte context, String message, @Nullable ServerPlayerEntity sender){
		if(world.isRemote){
			if(context == 0){
				activeText = message.length() == 0 ? null : new ResourceLocation(message);
			}else if(context == 1){
				try{
					col = Integer.valueOf(message);
				}catch(NumberFormatException e){
					col = null;
				}
			}
		}
	}

	public ResourceLocation getActiveTexture(){
		return activeText;
	}

	public Color getCol(){
		return activeText == null ? Color.WHITE : col == null ? Color.WHITE : new Color(col);
	}

	@Override
	public void tick(){
		super.tick();

		if(world.isRemote){
			return;
		}

		if(world.getGameTime() % 2 == 0){
			int fullness = Math.min(3, (int) Math.ceil((float) fluids[0].getAmount() * 3F / (float) fluidProps[0].capacity));
			BlockState state = world.getBlockState(pos);
			if(state.getBlock() != CRBlocks.heatingCrucible){
				remove();
				return;
			}

			if(state.get(CRProperties.FULLNESS) != fullness){
				world.setBlockState(pos, state.with(CRProperties.FULLNESS, fullness), 18);
			}

			if(fullness != 0 && !fluids[0].isEmpty()){
				ResourceLocation goal = fluids[0].getFluid().getAttributes().getStillTexture();
				if(!goal.equals(activeText)){
					activeText = goal;
					col = fluids[0].getFluid().getAttributes().getColor(fluids[0]);
					CRPackets.sendPacketAround(world, pos, new SendStringToClient(0, activeText.toString(), pos));
					CRPackets.sendPacketAround(world, pos, new SendStringToClient(1, Integer.toString(col), pos));
				}
			}else if(activeText != null){
				activeText = null;
				CRPackets.sendPacketAround(world, pos, new SendStringToClient(0, "", pos));
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
					Optional<CrucibleRec> recOpt = world.getRecipeManager().getRecipe(CRRecipes.CRUCIBLE_TYPE, this, world);
					if(recOpt.isPresent()){
						FluidStack created = recOpt.get().getOutput();
						if(fluidProps[0].capacity - fluids[0].getAmount() >= created.getAmount() && (fluids[0].isEmpty() || BlockUtil.sameFluid(fluids[0], created))){
							progress = 0;
							if(fluids[0].isEmpty()){
								fluids[0] = created.copy();
							}else{
								fluids[0].grow(created.getAmount());
							}
							inventory[0].shrink(1);
							markDirty();
						}
					}else{
						inventory[0] = ItemStack.EMPTY;
					}
				}
			}
			progressRef.set(progress);

			markDirty();
		}
	}

	@Override
	public void read(BlockState state, CompoundNBT nbt){
		super.read(state, nbt);
		String textStr = nbt.getString("act");
		if(textStr.length() == 0){
			activeText = null;
		}else{
			activeText = new ResourceLocation(textStr);
		}
		col = nbt.contains("col") ? nbt.getInt("col") : null;
		progress = nbt.getInt("prog");
		progressRef.set(progress);
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		nbt.putString("act", activeText == null ? "" : activeText.toString());
		if(col != null){
			nbt.putInt("col", col);
		}
		nbt.putInt("prog", progress);

		return nbt;
	}

	@Override
	public CompoundNBT getUpdateTag(){
		CompoundNBT nbt = super.getUpdateTag();
		nbt.putString("act", activeText == null ? "" : activeText.toString());
		if(col != null){
			nbt.putInt("col", col);
		}
		return nbt;
	}

	@Override
	public void remove(){
		super.remove();
		itemOpt.invalidate();
	}

	private final LazyOptional<IItemHandler> itemOpt = LazyOptional.of(ItemHandler::new);

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing){
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && facing != Direction.UP){
			return (LazyOptional<T>) globalFluidOpt;
		}

		if(capability == Capabilities.HEAT_CAPABILITY && facing != Direction.UP){
			return (LazyOptional<T>) heatOpt;
		}

		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			return (LazyOptional<T>) itemOpt;
		}

		return super.getCapability(capability, facing);
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, Direction direction){
		return false;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack){
		return index == 0 && world.getRecipeManager().getRecipe(CRRecipes.CRUCIBLE_TYPE, new Inventory(stack), world).isPresent();
	}

	@Override
	public ITextComponent getDisplayName(){
		return new TranslationTextComponent("container.crucible");
	}

	@Nullable
	@Override
	public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity playerEntity){
		return new CrucibleContainer(id, playerInventory, createContainerBuf());
	}
}
