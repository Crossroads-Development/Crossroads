package com.Da_Technomancer.crossroads.API.alchemy;

import java.util.HashSet;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.tuple.Triple;

import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.items.alchemy.AbstractGlassware;

import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class AlchemyGlasswareHolderTE extends AlchemyReactorTE{

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState){
		return oldState.getBlock() != newState.getBlock();
	}
	
	@Override
	protected double transferCapacity(){
		return getPhialType().getCapacity();
	}

	public void onBlockDestoyed(IBlockState state){
		if(state.getValue(Properties.ACTIVE)){
			ItemStack flask = new ItemStack(getPhialType(), 1, state.getValue(Properties.LIGHT) ? 1 : 0);
			getPhialType().setReagents(flask, contents, heat, amount);
			this.heat = 0;
			this.contents = new ReagentStack[AlchemyCore.REAGENT_COUNT];
			dirtyReag = true;
			markDirty();
			InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), flask);
		}
	}

	public boolean hasPhial(IBlockState state){
		return state.getValue(Properties.ACTIVE);
	}

	public IBlockState setHasPhial(IBlockState state, boolean hasPhial, boolean glass){
		return state.withProperty(Properties.ACTIVE, hasPhial).withProperty(Properties.LIGHT, glass);
	}

	public abstract AbstractGlassware getPhialType();

	@Nonnull
	public ItemStack rightClickWithItem(ItemStack stack){
		IBlockState state = world.getBlockState(pos);

		if(hasPhial(state)){
			if(stack.isEmpty()){
				world.setBlockState(pos, setHasPhial(state, false, false));
				ItemStack flask = new ItemStack(getPhialType(), 1, state.getValue(Properties.LIGHT) ? 1 : 0);
				getPhialType().setReagents(flask, contents, heat, amount);
				this.heat = 0;
				this.contents = new ReagentStack[AlchemyCore.REAGENT_COUNT];
				dirtyReag = true;
				markDirty();
				return flask;
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
		}else if(stack.getItem() == getPhialType()){
			//Add item into TE
			Triple<ReagentStack[], Double, Double> phialCont = getPhialType().getReagants(stack);
			this.heat = phialCont.getMiddle();
			this.contents = phialCont.getLeft();
			dirtyReag = true;
			glass = stack.getMetadata() == 0;
			markDirty();
			world.setBlockState(pos, setHasPhial(state, true, stack.getMetadata() == 1));
			return ItemStack.EMPTY;
		}

		return stack;
	}	
}
