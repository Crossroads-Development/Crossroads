package com.Da_Technomancer.crossroads.items;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;

import com.Da_Technomancer.crossroads.CommonProxy;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.IAdvancedRedstoneHandler;
import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.enums.MagicElements;
import com.Da_Technomancer.crossroads.API.enums.PrototypePortTypes;
import com.Da_Technomancer.crossroads.API.magic.IMagicHandler;
import com.Da_Technomancer.crossroads.API.magic.MagicUnit;
import com.Da_Technomancer.crossroads.API.rotary.IAxisHandler;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.API.technomancy.IPrototypeOwner;
import com.Da_Technomancer.crossroads.API.technomancy.IPrototypePort;
import com.Da_Technomancer.crossroads.API.technomancy.PrototypeInfo;
import com.Da_Technomancer.crossroads.API.technomancy.PrototypeWorldSavedData;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.dimensions.ModDimensions;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PrototypeWatch extends MagicUsingItem{

	/**
	 * Stores the IPrototypeOwners of active watches to prevent them from being garbage collected. 
	 */
	private static final HashMap<Integer, WatchPrototypeOwner> watchMap = new HashMap<Integer, WatchPrototypeOwner>();

	public PrototypeWatch(){//The overall handling needs re-writes to reduce unintended behavior.
		String name = "prototype_watch";
		setUnlocalizedName(name);
		setRegistryName(name);
		GameRegistry.register(this);
		setCreativeTab(ModItems.tabCrossroads);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced){
		if(!stack.hasTagCompound()){
			stack.setTagCompound(new NBTTagCompound());
		}
		NBTTagCompound prototypeNBT = stack.getTagCompound().getCompoundTag("prot");
		if(prototypeNBT.hasKey("name")){
			tooltip.add("Name: " + prototypeNBT.getString("name"));
		}
		super.addInformation(stack, playerIn, tooltip, advanced);
	}

	/**
	 * Calls super.onUsingTick (controls magic input).
	 * Activates redstone, controls strength based on sneaking.
	 */
	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase player, int count){
		super.onUsingTick(stack, player, count);
		if(!player.world.isRemote && stack.hasTagCompound() && stack.getTagCompound().hasKey("prot") && player.getActiveHand() == EnumHand.MAIN_HAND){
			NBTTagCompound prototypeNBT = stack.getTagCompound().getCompoundTag("prot");
			int index = prototypeNBT.getInteger("index");

			NBTTagCompound playerNBT = player.getEntityData();
			if(getMaxItemUseDuration(stack) == count || playerNBT.getBoolean("wasSneak") != player.isSneaking()){
				if(watchMap.containsKey(index)){
					watchMap.get(index).mouseActive = true;
					playerNBT.setBoolean("wasSneak", player.isSneaking());

					EnumFacing dir = EnumFacing.SOUTH;
					WorldServer worldDim = DimensionManager.getWorld(ModDimensions.PROTOTYPE_DIM_ID);
					PrototypeInfo info = PrototypeWorldSavedData.get().prototypes.get(index);
					if(info != null && info.ports[dir.getIndex()] != null && info.ports[dir.getIndex()] == PrototypePortTypes.REDSTONE_IN){
						BlockPos relPos = info.portPos[dir.getIndex()].offset(dir);
						relPos = info.chunk.getBlock(relPos.getX(), relPos.getY(), relPos.getZ());
						worldDim.getBlockState(relPos).neighborChanged(worldDim, relPos, ModBlocks.prototypePort, relPos.offset(dir.getOpposite()));
					}
				}
			}
		}
	}

	/**
	 * Stops the activation state. 
	 * Calls onPlayerStoppedUsing.
	 */
	@Override
	public void preChanged(ItemStack stack, EntityPlayer player){
		player.stopActiveHand();
	}

	/**
	 * Verifies watch. 
	 * Stores owner to map/refreshes owner.
	 * Controls dial angle.
	 * Inserts beams from cage.
	 */
	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected){
		if(isSelected && !worldIn.isRemote && stack.hasTagCompound() && stack.getTagCompound().hasKey("prot")){
			NBTTagCompound prototypeNBT = stack.getTagCompound().getCompoundTag("prot");
			int index = prototypeNBT.getInteger("index");
			PrototypeWorldSavedData data = PrototypeWorldSavedData.get();
			if(!watchMap.containsKey(index)){
				if(data.prototypes.size() <= index || data.prototypes.get(index) == null){
					stack.getTagCompound().removeTag("prot");
					return;
				}
				WatchPrototypeOwner owner = new WatchPrototypeOwner(index, entityIn);
				watchMap.put(index, owner);
				data.prototypes.get(index).owner = new WeakReference<IPrototypeOwner>(owner);
			}else{
				WatchPrototypeOwner owner = watchMap.get(index);
				owner.lifetimeBuffer = true;
				//TODO dial Still malfunctioning. Movement speed is a problem.
				double newAngle = stack.getTagCompound().getDouble("angle") + (owner.axle.getMotionData()[0] / 20D);
				int metadata = (int) (8 + (Math.round(newAngle * 4D / Math.PI) % 8)) % 8;

				if(entityIn instanceof EntityLivingBase && ((EntityLivingBase) entityIn).getActiveItemStack().isItemEqualIgnoreDurability(stack)){
					stack.getTagCompound().setDouble("angle", newAngle);
					EntityLivingBase entityLiving = ((EntityLivingBase) entityIn);
					setDamage(stack, metadata);
					EnumHand hand = entityLiving.getActiveHand();
					entityLiving.resetActiveHand();
					entityLiving.setActiveHand(hand);
				}else{
					stack.getTagCompound().setDouble("angle", newAngle);
					setDamage(stack, metadata);
				}
			}

			if(entityIn instanceof EntityPlayer && worldIn.getTotalWorldTime() % IMagicHandler.BEAM_TIME == 0 && ((EntityPlayer) entityIn).getHeldItem(EnumHand.OFF_HAND).getItem() == ModItems.beamCage){
				NBTTagCompound cageNbt = ((EntityPlayer) entityIn).getHeldItem(EnumHand.OFF_HAND).getTagCompound();
				NBTTagCompound nbt = stack.getTagCompound();
				PrototypeInfo info = data.prototypes.get(index);
				BlockPos portPos = info.ports[5] == PrototypePortTypes.MAGIC_IN ? info.portPos[5] : null;
				if(portPos == null){
					return;
				}
				WorldServer worldDim = DimensionManager.getWorld(ModDimensions.PROTOTYPE_DIM_ID);
				TileEntity te = worldDim.getTileEntity(info.chunk.getBlock(portPos.getX(), portPos.getY(), portPos.getZ()));
				if(!(te instanceof IPrototypePort)){
					return;
				}
				IPrototypePort port = (IPrototypePort) te;
				if(!port.hasCapPrototype(Capabilities.MAGIC_HANDLER_CAPABILITY)){
					return;
				}

				int energy = nbt.getInteger(MagicElements.ENERGY.name());
				int potential = nbt.getInteger(MagicElements.POTENTIAL.name());
				int stability = nbt.getInteger(MagicElements.STABILITY.name());
				int voi = nbt.getInteger(MagicElements.VOID.name());
				if(energy <= cageNbt.getInteger("stored_" + MagicElements.ENERGY.name()) && potential <= cageNbt.getInteger("stored_" + MagicElements.POTENTIAL.name()) && stability <= cageNbt.getInteger("stored_" + MagicElements.STABILITY.name()) && voi <= cageNbt.getInteger("stored_" + MagicElements.VOID.name())){
					if(energy + potential + stability + voi > 0){
						cageNbt.setInteger("stored_" + MagicElements.ENERGY.name(), cageNbt.getInteger("stored_" + MagicElements.ENERGY.name()) - energy);
						cageNbt.setInteger("stored_" + MagicElements.POTENTIAL.name(), cageNbt.getInteger("stored_" + MagicElements.POTENTIAL.name()) - potential);
						cageNbt.setInteger("stored_" + MagicElements.STABILITY.name(), cageNbt.getInteger("stored_" + MagicElements.STABILITY.name()) - stability);
						cageNbt.setInteger("stored_" + MagicElements.VOID.name(), cageNbt.getInteger("stored_" + MagicElements.VOID.name()) - voi);
						port.getCapPrototype(Capabilities.MAGIC_HANDLER_CAPABILITY).setMagic(new MagicUnit(energy, potential, stability, voi));
					}
				}
			}
		}
	}

	/**
	 * Sets mouseActive to false in the WatchPrototypeOwner.
	 * Disables any outputed redstone signal.
	 */
	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft){
		if(!worldIn.isRemote && stack.hasTagCompound() && stack.getTagCompound().hasKey("prot")){
			NBTTagCompound prototypeNBT = stack.getTagCompound().getCompoundTag("prot");
			int index = prototypeNBT.getInteger("index");
			if(watchMap.containsKey(index)){
				watchMap.get(index).mouseActive = false;

				//Disable redstone
				EnumFacing dir = EnumFacing.SOUTH;
				WorldServer worldDim = DimensionManager.getWorld(ModDimensions.PROTOTYPE_DIM_ID);
				PrototypeInfo info = PrototypeWorldSavedData.get().prototypes.get(index);
				if(info != null && info.ports[dir.getIndex()] != null && info.ports[dir.getIndex()] == PrototypePortTypes.REDSTONE_IN){
					BlockPos relPos = info.portPos[dir.getIndex()].offset(dir);
					relPos = info.chunk.getBlock(relPos.getX(), relPos.getY(), relPos.getZ());
					worldDim.getBlockState(relPos).neighborChanged(worldDim, relPos, ModBlocks.prototypePort, relPos.offset(dir.getOpposite()));
				}
			}
		}
	}

	private static class WatchPrototypeOwner implements IPrototypeOwner{

		private boolean lifetimeBuffer = true;
		private boolean active = true;
		private final int index;
		private boolean mouseActive = false;
		private final Entity user;

		private WatchPrototypeOwner(int index, Entity user){
			this.index = index;
			this.user = user;
		}

		private final IAxleHandler axle = new AxleHandler();
		private final IAdvancedRedstoneHandler redstone = new RedstoneHandler();
		private final MagicHandler magic = new MagicHandler();

		@Override
		public boolean hasCap(Capability<?> cap, EnumFacing side){
			if(!active){
				return false;
			}
			if(cap == Capabilities.AXLE_HANDLER_CAPABILITY && side == EnumFacing.UP){
				return true;
			}
			if(cap == Capabilities.ADVANCED_REDSTONE_HANDLER_CAPABILITY && side == EnumFacing.SOUTH){
				return true;
			}
			if(cap == Capabilities.MAGIC_HANDLER_CAPABILITY && side == EnumFacing.WEST){
				return true;
			}
			return false;
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T> T getCap(Capability<T> cap, EnumFacing side){
			if(!active){
				return null;
			}
			if(cap == Capabilities.AXLE_HANDLER_CAPABILITY && side == EnumFacing.UP){
				//Dial
				return (T) axle;
			}
			if(cap == Capabilities.ADVANCED_REDSTONE_HANDLER_CAPABILITY && side == EnumFacing.SOUTH){
				//Control
				return (T) redstone;
			}
			if(cap == Capabilities.MAGIC_HANDLER_CAPABILITY && side == EnumFacing.WEST){
				//Magic out
				return (T) magic;
			}
			return null;
		}

		@Override
		public void neighborChanged(EnumFacing fromSide, Block blockIn){

		}

		@Override
		public boolean loadTick(){
			if(lifetimeBuffer){
				lifetimeBuffer = false;
				return false;
			}
			watchMap.remove(index);
			active = false;
			CommonProxy.masterKey++;
			return true;
		}

		private class MagicHandler implements IMagicHandler{

			@Override
			public void setMagic(MagicUnit mag){
				if(mag != null && user instanceof EntityLivingBase){
					EntityLivingBase ent = (EntityLivingBase) user;
					ItemStack offhand = ent.getHeldItem(EnumHand.OFF_HAND);
					if(offhand.getItem() == ModItems.beamCage){
						if(offhand.getTagCompound() == null){
							offhand.setTagCompound(new NBTTagCompound());
						}
						NBTTagCompound nbt = offhand.getTagCompound();
						int energy = nbt.getInteger("stored_" + MagicElements.ENERGY.name());
						energy += mag.getEnergy();
						energy = Math.min(1024, energy);
						nbt.setInteger("stored_" + MagicElements.ENERGY.name(), energy);

						int potential = nbt.getInteger("stored_" + MagicElements.POTENTIAL.name());
						potential += mag.getPotential();
						potential = Math.min(1024, potential);
						nbt.setInteger("stored_" + MagicElements.POTENTIAL.name(), potential);

						int stability = nbt.getInteger("stored_" + MagicElements.STABILITY.name());
						stability += mag.getStability();
						stability = Math.min(1024, stability);
						nbt.setInteger("stored_" + MagicElements.STABILITY.name(), stability);

						int voi = nbt.getInteger("stored_" + MagicElements.VOID.name());
						voi += mag.getVoid();
						voi = Math.min(1024, voi);
						nbt.setInteger("stored_" + MagicElements.VOID.name(), voi);
					}
				}
			}
		}

		private class RedstoneHandler implements IAdvancedRedstoneHandler{

			@Override
			public double getOutput(boolean measure){
				return mouseActive ? (user.isSneaking() ? 1 : 2) : 0;
			}
		}

		private class AxleHandler implements IAxleHandler{

			private final double[] motionData = new double[4];
			private final double[] physData = new double[2];

			@Override
			public double[] getMotionData(){
				return motionData;
			}

			private double rotRatio;
			private byte updateKey;

			@Override
			public void propogate(IAxisHandler masterIn, byte key, double rotRatioIn, double lastRadius){
				//If true, this has already been checked.
				if(key == updateKey || masterIn.addToList(this)){
					return;
				}

				rotRatio = rotRatioIn == 0 ? 1 : rotRatioIn;
				updateKey = key;
			}

			@Override
			public double[] getPhysData(){
				return physData;
			}

			@Override
			public double getRotationRatio(){
				return rotRatio;
			}

			@Override
			public void resetAngle(){

			}

			@SideOnly(Side.CLIENT)
			@Override
			public double getAngle(){
				return 0;
			}

			@Override
			public void addEnergy(double energy, boolean allowInvert, boolean absolute){
				if(allowInvert && absolute){
					motionData[1] += energy;
				}else if(allowInvert){
					motionData[1] += energy * MiscOp.posOrNeg(motionData[1]);
				}else if(absolute){
					int sign = (int) MiscOp.posOrNeg(motionData[1]);
					motionData[1] += energy;
					if(sign != 0 && MiscOp.posOrNeg(motionData[1]) != sign){
						motionData[1] = 0;
					}
				}else{
					int sign = (int) MiscOp.posOrNeg(motionData[1]);
					motionData[1] += energy * ((double) sign);
					if(MiscOp.posOrNeg(motionData[1]) != sign){
						motionData[1] = 0;
					}
				}
				//markDirty();
			}

			@Override
			public void markChanged(){

			}
		}
	}
}
