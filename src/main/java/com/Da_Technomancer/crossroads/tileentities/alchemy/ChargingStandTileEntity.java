package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.IInfoDevice;
import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.alchemy.*;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendLooseArcToClient;
import com.Da_Technomancer.crossroads.API.technomancy.EnumGoggleLenses;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.items.alchemy.AbstractGlassware;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import org.apache.commons.lang3.tuple.Triple;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;

public class ChargingStandTileEntity extends AlchemyReactorTE{

	private boolean occupied = false;
	/** 
	 * Meaningless if !occupied. If true, florence flask, else phial.
	 */
	private boolean florence = false;
	private int fe = 0;
	private static final int ENERGY_CAPACITY = 1000;

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState){
		return oldState.getBlock() != newState.getBlock();
	}

	@Override
	public void addInfo(ArrayList<String> chat, IInfoDevice device, EntityPlayer player, @Nullable EnumFacing side){
		if((device == ModItems.omnimeter || device == EnumGoggleLenses.RUBY) && occupied && amount > 0){
			chat.add("Temp: " + MiscOp.betterRound((heat / amount) - 273D, 3) + "°C");
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
		if(fe > 0){
			fe = Math.max(0, fe - 10);
			if(world.getTotalWorldTime() % 10 == 0){
				NBTTagCompound nbt = new NBTTagCompound();
				new LooseArcRenderable(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, pos.getX() + world.rand.nextFloat(), pos.getY() + world.rand.nextFloat(), pos.getZ() + world.rand.nextFloat(), 1, 0F, 0.18F, TeslaCoilTileEntity.COLOR_CODES[(int) (world.getTotalWorldTime() % 3)]).saveToNBT(nbt);
				ModPackets.network.sendToAllAround(new SendLooseArcToClient(nbt), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
				world.playSound(null, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, SoundEvents.BLOCK_REDSTONE_TORCH_BURNOUT, SoundCategory.BLOCKS, 0.1F, -5F);
			}
		}
		super.update();
	}

	@Override
	public boolean isCharged(){
		return fe > 0;
	}

	@Override
	protected double transferCapacity(){
		return occupied ? florence ? ModItems.florenceFlask.getCapacity() : ModItems.phial.getCapacity() : 0;
	}

	@Override
	public void destroyChamber(){
		double temp = getTemp();
		IBlockState state = world.getBlockState(pos);
		world.setBlockState(pos, state.withProperty(Properties.ACTIVE, false).withProperty(Properties.CRYSTAL, false).withProperty(Properties.CONTAINER_TYPE, false));
		world.playSound(null, pos, SoundType.GLASS.getBreakSound(), SoundCategory.BLOCKS, SoundType.GLASS.getVolume(), SoundType.GLASS.getPitch());
		occupied = false;
		florence = false;
		this.heat = 0;
		this.contents = new ReagentStack[AlchemyCore.REAGENT_COUNT];
		dirtyReag = true;
		for(ReagentStack r : contents){
			if(r != null){
				r.getType().onRelease(world, pos, r.getAmount(), temp, r.getPhase(temp), contents);
			}
		}
	}

	public void onBlockDestroyed(IBlockState state){
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
					boolean changed;

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
		}else if(stack.getItem() == ModItems.phial || stack.getItem() == ModItems.florenceFlask){
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
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		occupied = nbt.getBoolean("occupied");
		florence = occupied && nbt.getBoolean("florence");
		fe = nbt.getInteger("fe");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setBoolean("occupied", occupied);
		nbt.setBoolean("florence", florence);
		nbt.setInteger("fe", fe);
		return nbt;
	}

	@Override
	protected EnumTransferMode[] getModes(){
		return new EnumTransferMode[] {EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE};
	}

	private final ElecHandler elecHandler = new ElecHandler();

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		if(cap == CapabilityEnergy.ENERGY){
			return true;
		}
		return super.hasCapability(cap, side);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(cap == CapabilityEnergy.ENERGY){
			return (T) elecHandler;
		}
		return super.getCapability(cap, side);
	}

	private class ElecHandler implements IEnergyStorage{


		@Override
		public int receiveEnergy(int maxReceive, boolean simulate){
			int toMove = Math.min(ENERGY_CAPACITY - fe, maxReceive);

			if(!simulate && toMove > 0){
				fe += toMove;
				markDirty();
			}

			return toMove;
		}

		@Override
		public int extractEnergy(int maxExtract, boolean simulate){
			return 0;
		}

		@Override
		public int getEnergyStored(){
			return fe;
		}

		@Override
		public int getMaxEnergyStored(){
			return ENERGY_CAPACITY;
		}

		@Override
		public boolean canExtract(){
			return false;
		}

		@Override
		public boolean canReceive(){
			return true;
		}
	}
}
