package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.EnergyConverters;
import com.Da_Technomancer.crossroads.API.alchemy.*;
import com.Da_Technomancer.crossroads.items.alchemy.AbstractGlassware;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import org.apache.commons.lang3.tuple.Triple;

import javax.annotation.Nonnull;
import java.util.HashSet;

public class ReagentTankTileEntity extends AlchemyCarrierTE{

	public ReagentTankTileEntity(){
		super();
	}

	public ReagentTankTileEntity(boolean glass){
		super(glass);
	}

	@Override
	public double transferCapacity(){
		return 10_000D;
	}

	@Nonnull
	public ItemStack rightClickWithItem(ItemStack stack, boolean sneaking){
		if(stack.isEmpty()){
			double temp = correctTemp();

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
			if(stack.getMetadata() == 0){
				//Refuse if made of glass and cannot hold contents
				for(ReagentStack reag : contents){
					if(reag != null && !reag.getType().canGlassContain()){
						return stack;
					}
				}
			}

			Triple<ReagentStack[], Double, Double> phial = ((AbstractGlassware) stack.getItem()).getReagants(stack);
			ReagentStack[] reag = phial.getLeft();
			double endHeat = phial.getMiddle();
			double endAmount = phial.getRight();

			if(phial.getRight() <= AlchemyCore.MIN_QUANTITY){
				//Move from block to item

				double space = ((AbstractGlassware) stack.getItem()).getCapacity() - endAmount;
				if(space <= 0){
					return stack;
				}
				double temp = correctTemp() + 273D;//In kelvin
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
					if(r.increaseAmount(-moved) <= AlchemyCore.MIN_QUANTITY){
						endHeat -= r.getAmount() * callerTemp;
						endAmount -= r.getAmount();
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
					((AbstractGlassware) stack.getItem()).setReagents(stack, reag, Math.max(0, endHeat), Math.max(0, endAmount));
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
					heat += Math.max(0, Math.min(toAdd.getMeltingPoint() + 273D, EnergyConverters.convertBiomeTemp(world.getBiomeForCoordsBody(pos).getTemperature(pos)) + 273D)) * toAddStack.getAmount();
					markDirty();
					dirtyReag = true;
					stack.shrink(1);
					return stack;
				}
			}
		}

		return stack;
	}

	@Override
	protected EnumTransferMode[] getModes(){
		return new EnumTransferMode[] {EnumTransferMode.BOTH, EnumTransferMode.BOTH, EnumTransferMode.BOTH, EnumTransferMode.BOTH, EnumTransferMode.BOTH, EnumTransferMode.BOTH};
	}

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		if(cap == Capabilities.CHEMICAL_HANDLER_CAPABILITY){
			return true;
		}
		return super.hasCapability(cap, side);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(cap == Capabilities.CHEMICAL_HANDLER_CAPABILITY){
			return (T) handler;
		}
		return super.getCapability(cap, side);
	}
}
