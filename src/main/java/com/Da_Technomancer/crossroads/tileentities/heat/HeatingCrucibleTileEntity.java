package com.Da_Technomancer.crossroads.tileentities.heat;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.API.packets.IStringReceiver;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendStringToClient;
import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.items.crafting.RecipeHolder;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;

public class HeatingCrucibleTileEntity extends InventoryTE implements IStringReceiver{

	public static final int[] TEMP_TIERS = {1000, 1400, 2500};
	public static final int USAGE = 20;
	public static final int REQUIRED = 1200;
	private int progress = 0;

	public HeatingCrucibleTileEntity(){
		super(1);
		fluidProps[0] = new TankProperty(0, 4_000, false, true);
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
	public void receiveString(String context, String message, EntityPlayerMP sender){
		if(world.isRemote && context.equals("text")){
			activeText = message;
		}
	}

	/**
	 * The texture to be displayed, if any. 
	 */
	private String activeText = "";

	public String getActiveTexture(){
		return activeText;
	}

	@Override
	public void update(){
		super.update();

		if(world.isRemote){
			return;
		}

		if(world.getTotalWorldTime() % 2 == 0){
			int fullness = Math.min(3, (int) Math.ceil(fluids[0] == null ? 0F : (float) fluids[0].amount * 3F / (float) fluidProps[0].getCapacity()));
			IBlockState state = world.getBlockState(pos);
			if(state.getBlock() != ModBlocks.heatingCrucible){
				invalidate();
				return;
			}

			if(state.getValue(Properties.FULLNESS) != fullness){
				world.setBlockState(pos, state.withProperty(Properties.FULLNESS, fullness), 18);
			}

			if(fullness != 0 && fluids[0] != null && fluids[0].getFluid().getStill() != null){
				String goal = fluids[0].getFluid().getStill().toString();
				if(!goal.equals(activeText)){
					activeText = goal;
					ModPackets.network.sendToAllAround(new SendStringToClient("text", activeText, pos), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
				}
			}else if(!activeText.isEmpty()){
				activeText = "";
				ModPackets.network.sendToAllAround(new SendStringToClient("text", activeText, pos), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
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
					FluidStack created = RecipeHolder.crucibleRecipes.get(inventory[0]);

					if(created == null){
						inventory[0] = ItemStack.EMPTY;
					}else if(fluids[0] == null || (fluidProps[0].getCapacity() - fluids[0].amount >= created.amount && fluids[0].getFluid() == created.getFluid())){
						progress = 0;
						if(fluids[0] == null){
							fluids[0] = created.copy();
						}else{
							fluids[0].amount += created.amount;
						}
						inventory[0].shrink(1);
					}
				}
			}

			markDirty();
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		activeText = nbt.getString("act");
		progress = nbt.getInteger("prog");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		if(activeText.length() != 0){
			nbt.setString("act", activeText);
		}
		nbt.setInteger("prog", progress);

		return nbt;
	}

	@Override
	public NBTTagCompound getUpdateTag(){
		NBTTagCompound nbt = super.getUpdateTag();
		if(activeText.length() != 0){
			nbt.setString("act", activeText);
		}
		return nbt;
	}

	private final FluidHandler fluidHandler = new FluidHandler(0);
	private final ItemHandler itemHandler = new ItemHandler(null);

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing){
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && facing != EnumFacing.UP){
			return (T) fluidHandler;
		}

		if(capability == Capabilities.HEAT_HANDLER_CAPABILITY && (facing == EnumFacing.DOWN || facing == null)){
			return (T) heatHandler;
		}

		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			return (T) itemHandler;
		}

		return super.getCapability(capability, facing);
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction){
		return false;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack){
		return index == 0 && RecipeHolder.crucibleRecipes.get(stack) != null;
	}

	@Override
	public String getName(){
		return "container.crucible";
	}

	@Override
	public int getField(int id){
		return id == getFieldCount() - 1 ? progress : super.getField(id);
	}

	@Override
	public void setField(int id, int value){
		if(id == getFieldCount() - 1){
			progress = value;
		}else{
			super.setField(id, value);
		}
	}

	@Override
	public int getFieldCount(){
		return super.getFieldCount() + 1;
	}
}
