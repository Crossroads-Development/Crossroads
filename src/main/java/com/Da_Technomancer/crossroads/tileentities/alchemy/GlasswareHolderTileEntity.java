package com.Da_Technomancer.crossroads.tileentities.alchemy;

import java.util.ArrayList;
import java.util.HashSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Triple;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.EnergyConverters;
import com.Da_Technomancer.crossroads.API.IInfoDevice;
import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.alchemy.AlchemyCore;
import com.Da_Technomancer.crossroads.API.alchemy.AlchemyReactorTE;
import com.Da_Technomancer.crossroads.API.alchemy.EnumContainerType;
import com.Da_Technomancer.crossroads.API.alchemy.EnumMatterPhase;
import com.Da_Technomancer.crossroads.API.alchemy.EnumTransferMode;
import com.Da_Technomancer.crossroads.API.alchemy.IChemicalHandler;
import com.Da_Technomancer.crossroads.API.alchemy.IReagent;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentStack;
import com.Da_Technomancer.crossroads.API.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.API.technomancy.EnumGoggleLenses;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.items.alchemy.AbstractGlassware;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

public class GlasswareHolderTileEntity extends AlchemyReactorTE{

	private boolean occupied = false;
	/** 
	 * Meaningless if !occupied. If true, florence flask, else phial. 
	 */
	private boolean florence = false;
	private double cableTemp = 0;
	private boolean init = false;

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState){
		return oldState.getBlock() != newState.getBlock();
	}

	@Override
	public void addInfo(ArrayList<String> chat, IInfoDevice device, EntityPlayer player, @Nullable EnumFacing side){
		if((device == ModItems.omnimeter || device == EnumGoggleLenses.RUBY) && occupied && amount > 0){
			chat.add("Temp: " + MiscOp.betterRound(florence ? cableTemp : (heat / amount) - 273D, 3) + "°C");
		}
		if(device == ModItems.omnimeter || device == EnumGoggleLenses.DIAMOND){
			if(amount == 0){
				chat.add("No reagents");
			}
			for(ReagentStack reag : contents){
				if(reag != null){
					chat.add(reag.toString());
				}
			}
		}
	}

	@Override
	public void update(){
		if(world.isRemote){
			return;
		}
		if(!init){
			cableTemp = EnergyConverters.convertBiomeTemp(world.getBiomeForCoordsBody(pos).getTemperature(pos));
			init = true;
		}

		super.update();
	}

	@Override
	protected double transferCapacity(){
		return occupied ? florence ? ModItems.florenceFlask.getCapacity() : ModItems.phial.getCapacity() : 0;
	}

	public void onBlockDestoyed(IBlockState state){
		if(occupied){
			AbstractGlassware glasswareType = florence ? ModItems.florenceFlask : ModItems.phial;
			ItemStack flask = new ItemStack(glasswareType, 1, glass ? 0 : 1);
			glasswareType.setReagents(flask, contents, heat, amount);
			this.heat = 0;
			this.contents = new ReagentStack[AlchemyCore.REAGENT_COUNT];
			dirtyReag = true;
			occupied = false;
			markDirty();
			InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), flask);
		}
	}

	/**
	 * Normal behavior: 
	 * Shift click with empty hand: Remove phial
	 * Normal click with empty hand: Try to remove solid reagent
	 * Normal click with phial: Add phial to stand, or merge phial contents
	 * Normal click with non-phial item: Try to add solid reagent
	 */
	@Nonnull
	public ItemStack rightClickWithItem(ItemStack stack, boolean sneaking){
		IBlockState state = world.getBlockState(pos);

		if(occupied){
			if(stack.isEmpty()){
				if(sneaking){
					AbstractGlassware glasswareType = florence ? ModItems.florenceFlask : ModItems.phial;
					world.setBlockState(pos, state.withProperty(Properties.ACTIVE, false).withProperty(Properties.CRYSTAL, false).withProperty(Properties.CONTAINER_TYPE, false));
					ItemStack flask = new ItemStack(glasswareType, 1, glass ? 0 : 1);
					occupied = false;
					glasswareType.setReagents(flask, contents, heat, amount);
					this.heat = 0;
					this.contents = new ReagentStack[AlchemyCore.REAGENT_COUNT];
					dirtyReag = true;
					markDirty();
					return flask;
				}

				double temp = getTemp();

				for(int i = 0; i < contents.length; i++){
					ReagentStack reag = contents[i];
					if(reag != null && reag.getPhase(temp) == EnumMatterPhase.SOLID){
						ItemStack toRemove = reag.getType().getStackFromReagent(reag);
						if(!toRemove.isEmpty()){
							double amountDecrease = reag.getType().getReagentFromStack(toRemove).getAmount() * toRemove.getCount();
							if(contents[i].increaseAmount(-amountDecrease) <= 0){
								contents[i] = null;
							}
							heat -= (temp + 273D) * amountDecrease;
							markDirty();
							dirtyReag = true;

							return toRemove;
						}
					}
				}				
			}else if(stack.getItem() instanceof AbstractGlassware){
				Triple<ReagentStack[], Double, Double> phial = ((AbstractGlassware) stack.getItem()).getReagants(stack);
				ReagentStack[] reag = phial.getLeft();
				double endHeat = phial.getMiddle();
				double endAmount = phial.getRight();

				if(phial.getRight() <= 0){
					//Move from block to item

					double space = ((AbstractGlassware) stack.getItem()).getCapacity() - endAmount;
					if(space <= 0){
						return stack;
					}
					double temp = getTemp() + 273D;//In kelvin
					boolean changed = false;

					HashSet<Integer> validIds = new HashSet<Integer>(4);
					double totalValid = 0;

					for(int i = 0; i < AlchemyCore.REAGENT_COUNT; i++){
						ReagentStack r = contents[i];
						if(r != null){
							validIds.add(i);
							totalValid += r.getAmount();
						}
					}

					totalValid = Math.min(1D, space / totalValid);

					for(int i : validIds){
						ReagentStack r = contents[i];
						double moved = r.getAmount() * totalValid;
						if(moved <= 0D){
							continue;
						}
						amount -= moved;
						changed = true;
						space -= moved;
						double heatTrans = moved * temp;
						if(r.increaseAmount(-moved) <= 0){
							contents[i] = null;
						}
						endAmount += moved;
						heat -= heatTrans;
						endHeat += heatTrans;
						if(reag[i] == null){
							reag[i] = new ReagentStack(AlchemyCore.REAGENTS[i], moved);
						}else{
							reag[i].increaseAmount(moved);
						}
					}

					if(changed){
						dirtyReag = true;
						markDirty();
						((AbstractGlassware) stack.getItem()).setReagents(stack, reag, endHeat, endAmount);
					}
					return stack;

				}else{
					//Move from item to block
					double space = transferCapacity() - amount;
					if(space <= 0){
						return stack;
					}
					double callerTemp = phial.getMiddle() / phial.getRight();//In kelvin
					boolean changed = false;

					HashSet<Integer> validIds = new HashSet<Integer>(4);
					double totalValid = 0;

					for(int i = 0; i < AlchemyCore.REAGENT_COUNT; i++){
						ReagentStack r = reag[i];
						if(r != null){
							validIds.add(i);
							totalValid += r.getAmount();
						}
					}

					totalValid = Math.min(1D, space / totalValid);

					changed = !validIds.isEmpty();

					for(int i : validIds){
						ReagentStack r = reag[i];
						double moved = r.getAmount() * totalValid;
						if(moved <= 0D){
							continue;
						}
						amount += moved;
						space -= moved;
						double heatTrans = moved * callerTemp;
						if(r.increaseAmount(-moved) <= 0){
							reag[i] = null;
						}
						endAmount -= moved;
						heat += heatTrans;
						endHeat -= heatTrans;
						if(contents[i] == null){
							contents[i] = new ReagentStack(AlchemyCore.REAGENTS[i], moved);
						}else{
							contents[i].increaseAmount(moved);
						}
					}

					if(changed){
						dirtyReag = true;
						markDirty();
						((AbstractGlassware) stack.getItem()).setReagents(stack, reag, endHeat, endAmount);
					}
					return stack;
				}

			}else{
				IReagent toAdd = AlchemyCore.ITEM_TO_REAGENT.get(stack.getItem());
				if(toAdd != null){
					ReagentStack toAddStack = toAdd.getReagentFromStack(stack);
					if(toAddStack != null && transferCapacity() - amount >= toAddStack.getAmount()){
						if(contents[toAdd.getIndex()] == null){
							contents[toAdd.getIndex()] = toAddStack;
						}else{
							contents[toAdd.getIndex()].increaseAmount(toAddStack.getAmount());
						}
						heat += Math.min(toAdd.getMeltingPoint() + 263D, 290D) * toAddStack.getAmount();
						markDirty();
						dirtyReag = true;
						stack.shrink(1);
						return stack;
					}
				}
			}
		}else if(stack.getItem() instanceof AbstractGlassware){
			//Add item into TE
			Triple<ReagentStack[], Double, Double> phialCont = ((AbstractGlassware) stack.getItem()).getReagants(stack);
			this.heat = phialCont.getMiddle();
			this.contents = phialCont.getLeft();
			glass = stack.getMetadata() == 0;
			dirtyReag = true;
			markDirty();
			occupied = true;
			florence = stack.getItem() == ModItems.florenceFlask;
			world.setBlockState(pos, state.withProperty(Properties.ACTIVE, true).withProperty(Properties.CRYSTAL, !glass).withProperty(Properties.CONTAINER_TYPE, florence));
			return ItemStack.EMPTY;
		}

		return stack;
	}

	@Override
	protected Vec3d getParticlePos(){
		return new Vec3d(pos).addVector(0.5D, 0.25D, 0.5D);
	}

	@Override
	protected double correctTemp(){
		if(florence){
			//Shares heat between internal cable & contents
			cableTemp = amount <= 0 ? cableTemp : (cableTemp + EnergyConverters.ALCHEMY_TEMP_CONVERSION * amount * ((heat / amount) - 273D)) / (EnergyConverters.ALCHEMY_TEMP_CONVERSION * amount + 1D);		
			heat = (cableTemp + 273D) * amount;
			return cableTemp;
		}
		return super.correctTemp();
	}

	@Override
	protected void performTransfer(){
		IBlockState state = world.getBlockState(pos);
		if(state.getBlock() == ModBlocks.glasswareHolder && state.getValue(Properties.ACTIVE)){
			EnumFacing side = EnumFacing.UP;
			TileEntity te = world.getTileEntity(pos.offset(side));
			if(amount <= 0 || te == null || !te.hasCapability(Capabilities.CHEMICAL_HANDLER_CAPABILITY, side.getOpposite())){
				return;
			}

			IChemicalHandler otherHandler = te.getCapability(Capabilities.CHEMICAL_HANDLER_CAPABILITY, side.getOpposite());
			EnumContainerType cont = otherHandler.getChannel(side.getOpposite());
			if(cont != EnumContainerType.NONE && (cont == EnumContainerType.GLASS ? !glass : glass)){
				return;
			}

			if(amount != 0){
				if(otherHandler.insertReagents(contents, side.getOpposite(), handler, state.getValue(Properties.REDSTONE_BOOL))){
					correctReag();
					markDirty();
				}
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		cableTemp = nbt.getDouble("temp");
		init = nbt.getBoolean("init");
		occupied = nbt.getBoolean("occupied");
		florence = occupied && nbt.getBoolean("florence");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setDouble("temp", cableTemp);
		nbt.setBoolean("init", init);
		nbt.setBoolean("occupied", occupied);
		nbt.setBoolean("florence", florence);
		return nbt;
	}

	@Override
	protected EnumTransferMode[] getModes(){
		EnumTransferMode[] modes = {EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE};
		if(occupied){
			modes[1] = EnumTransferMode.BOTH;
		}
		return modes;
	}

	private final HeatHandler heatHandler = new HeatHandler();

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		if((side == null || side == EnumFacing.UP) && cap == Capabilities.CHEMICAL_HANDLER_CAPABILITY && occupied){
			return true;
		}
		if((side == null || side == EnumFacing.DOWN) && cap == Capabilities.HEAT_HANDLER_CAPABILITY){
			IBlockState state = world.getBlockState(pos);
			if(state.getValue(Properties.CONTAINER_TYPE)){
				return true;	
			}
		}
		return super.hasCapability(cap, side);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if((side == null || side == EnumFacing.UP) && cap == Capabilities.CHEMICAL_HANDLER_CAPABILITY && occupied){
			return (T) handler;
		}
		if((side == null || side == EnumFacing.DOWN) && cap == Capabilities.HEAT_HANDLER_CAPABILITY){
			IBlockState state = world.getBlockState(pos);
			if(state.getValue(Properties.CONTAINER_TYPE)){
				return (T) heatHandler;	
			}
		}
		return super.getCapability(cap, side);
	}

	private class HeatHandler implements IHeatHandler{

		private void init(){
			if(!init){
				init = true;
				cableTemp = EnergyConverters.convertBiomeTemp(world.getBiomeForCoordsBody(pos).getTemperature(pos));
			}
		}

		@Override
		public double getTemp(){
			init();
			return cableTemp;
		}

		@Override
		public void setTemp(double tempIn){
			init = true;
			cableTemp = tempIn;
			//Shares heat between internal cable & contents
			if(amount != 0){
				cableTemp = (cableTemp + EnergyConverters.ALCHEMY_TEMP_CONVERSION * amount * ((heat / amount) - 273D)) / (EnergyConverters.ALCHEMY_TEMP_CONVERSION * amount + 1D);		
				heat = (cableTemp + 273D) * amount;
				dirtyReag = true;
			}
			markDirty();
		}

		@Override
		public void addHeat(double tempChange){
			init();
			cableTemp += tempChange;
			//Shares heat between internal cable & contents
			if(amount != 0){
				cableTemp = (cableTemp + EnergyConverters.ALCHEMY_TEMP_CONVERSION * amount * ((heat / amount) - 273D)) / (EnergyConverters.ALCHEMY_TEMP_CONVERSION * amount + 1D);		
				heat = (cableTemp + 273D) * amount;
				dirtyReag = true;
			}
			markDirty();
		}
	}
}
