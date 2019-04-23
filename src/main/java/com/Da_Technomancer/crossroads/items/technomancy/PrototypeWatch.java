package com.Da_Technomancer.crossroads.items.technomancy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.crossroads.API.redstone.IAdvancedRedstoneHandler;
import com.Da_Technomancer.crossroads.API.beams.BeamManager;
import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.API.beams.IBeamHandler;
import com.Da_Technomancer.crossroads.API.rotary.IAxisHandler;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.API.technomancy.IPrototypeOwner;
import com.Da_Technomancer.crossroads.API.technomancy.IPrototypePort;
import com.Da_Technomancer.crossroads.API.technomancy.PrototypeInfo;
import com.Da_Technomancer.crossroads.API.technomancy.PrototypePortTypes;
import com.Da_Technomancer.crossroads.CommonProxy;
import com.Da_Technomancer.crossroads.EventHandlerCommon;
import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.dimensions.ModDimensions;
import com.Da_Technomancer.crossroads.dimensions.PrototypeWorldProvider;
import com.Da_Technomancer.crossroads.dimensions.PrototypeWorldSavedData;
import com.Da_Technomancer.crossroads.items.ModItems;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;

public class PrototypeWatch extends BeamUsingItem{

	/**
	 * Stores the IPrototypeOwners of active watches to prevent them from being garbage collected. 
	 */
	private static final HashMap<Integer, WatchPrototypeOwner> watchMap = new HashMap<Integer, WatchPrototypeOwner>();

	public PrototypeWatch(){
		String name = "prototype_watch";
		setTranslationKey(name);
		setRegistryName(name);
		setCreativeTab(ModItems.TAB_CROSSROADS);
		setMaxStackSize(1);
		ModItems.toRegister.add(this);
		for(int i = 0; i < 8; i++){
			ModItems.toClientRegister.put(Pair.of(this, i), new ModelResourceLocation(Main.MODID + ":watch_" + i));
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced){
		if(!stack.hasTagCompound()){
			stack.setTagCompound(new NBTTagCompound());
		}
		NBTTagCompound prototypeNBT = stack.getTagCompound().getCompoundTag("prot");
		if(prototypeNBT.hasKey("name")){
			tooltip.add("Name: " + prototypeNBT.getString("name"));
		}
		super.addInformation(stack, world, tooltip, advanced);
	}

	/**
	 * Calls super.onUsingTick (controls beams input).
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
					PrototypeInfo info = PrototypeWorldSavedData.get(true).prototypes.get(index);
					WorldServer worldDim = DimensionManager.getWorld(ModDimensions.PROTOTYPE_DIM_ID);
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

	@Nullable
	public static double[] getValues(ItemStack watch){
		if(watch.isEmpty() || watch.getItem() != ModItems.watch || !watch.hasTagCompound()){
			return null;
		}
		NBTTagCompound nbt = watch.getTagCompound();
		return new double[] {nbt.getDouble("v_0"), nbt.getDouble("v_1"), nbt.getDouble("v_2")};
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
			PrototypeWorldSavedData data = PrototypeWorldSavedData.get(true);
			if(!watchMap.containsKey(index)){
				if(data.prototypes.size() <= index || data.prototypes.get(index) == null){
					stack.getTagCompound().removeTag("prot");
					return;
				}
				WatchPrototypeOwner owner = new WatchPrototypeOwner(index, entityIn);
				watchMap.put(index, owner);
				data.prototypes.get(index).owner = new WeakReference<IPrototypeOwner>(owner);
				EventHandlerCommon.updateLoadedPrototypeChunks();
			}else{
				WatchPrototypeOwner owner = watchMap.get(index);
				owner.lifetimeBuffer = true;
				double[] newVal = new double[3];
				for(int i = 0; i < 3; i++){
					newVal[i] = owner.axles[i].getMotionData()[0] * (i == 0 ? 1 : -1);
				}

				for(int i = 0; i < 3; i++){
					stack.getTagCompound().setDouble("v_" + i, newVal[i]);
				}

				if(entityIn instanceof EntityLivingBase && ((EntityLivingBase) entityIn).getActiveItemStack().isItemEqualIgnoreDurability(stack)){
					//The next 4 lines exist solely because vanilla's handling of item changes is terrible
					EntityLivingBase entityLiving = ((EntityLivingBase) entityIn);
					EnumHand hand = entityLiving.getActiveHand();
					entityLiving.resetActiveHand();
					entityLiving.setActiveHand(hand);
				}
			}

			PrototypeWorldProvider.tickChunk(((index % 100) * 2) - 99, (index / 50) - 99);

			if(entityIn instanceof EntityPlayer && BeamManager.beamStage == 0 && ((EntityPlayer) entityIn).getHeldItem(EnumHand.OFF_HAND).getItem() == ModItems.beamCage && ((EntityPlayer) entityIn).getHeldItem(EnumHand.OFF_HAND).hasTagCompound()){
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
				if(!port.hasCapPrototype(Capabilities.BEAM_CAPABILITY)){
					return;
				}

				int energy = nbt.getInteger(EnumBeamAlignments.ENERGY.name());
				int potential = nbt.getInteger(EnumBeamAlignments.POTENTIAL.name());
				int stability = nbt.getInteger(EnumBeamAlignments.STABILITY.name());
				int voi = nbt.getInteger(EnumBeamAlignments.VOID.name());
				ItemStack heldCage = ((EntityPlayer) entityIn).getHeldItem(EnumHand.OFF_HAND);
				BeamUnit cageBeam = BeamCage.getStored(heldCage);
				int beamEn = cageBeam == null ? 0 : cageBeam.getEnergy();
				int beamPo = cageBeam == null ? 0 : cageBeam.getPotential();
				int beamSt = cageBeam == null ? 0 : cageBeam.getStability();
				int beamVo = cageBeam == null ? 0 : cageBeam.getVoid();
				if(energy <= beamEn && potential <= beamPo && stability <= beamSt && voi <= beamVo){
					if(energy + potential + stability + voi > 0){
						BeamCage.storeBeam(heldCage, new BeamUnit(beamEn - energy, beamPo - potential, beamSt - stability, beamVo - voi));
						port.getCapPrototype(Capabilities.BEAM_CAPABILITY).setMagic(new BeamUnit(energy, potential, stability, voi));
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
				PrototypeInfo info = PrototypeWorldSavedData.get(true).prototypes.get(index);
				WorldServer worldDim = DimensionManager.getWorld(ModDimensions.PROTOTYPE_DIM_ID);
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

		private final IAxleHandler[] axles = new AxleHandler[] {new AxleHandler(), new AxleHandler(), new AxleHandler()};
		private final IAdvancedRedstoneHandler redstone = new RedstoneHandler();
		private final BeamHandler magic = new BeamHandler();

		@Override
		public boolean hasCap(Capability<?> cap, EnumFacing side){
			if(!active){
				return false;
			}
			if(cap == Capabilities.AXLE_CAPABILITY && (side == EnumFacing.UP || side == EnumFacing.DOWN || side == EnumFacing.NORTH)){
				return true;
			}
			if(cap == Capabilities.ADVANCED_REDSTONE_CAPABILITY && side == EnumFacing.SOUTH){
				return true;
			}
			if(cap == Capabilities.BEAM_CAPABILITY && side == EnumFacing.WEST){
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
			if(cap == Capabilities.AXLE_CAPABILITY){
				switch(side){
					case UP:
						//Dial 0
						return (T) axles[0];
					case DOWN:
						//Dial 1
						return (T) axles[1];
					case NORTH:
						//Dial 2
						return (T) axles[2];
					default:
				}

			}
			if(cap == Capabilities.ADVANCED_REDSTONE_CAPABILITY && side == EnumFacing.SOUTH){
				//Control
				return (T) redstone;
			}
			if(cap == Capabilities.BEAM_CAPABILITY && side == EnumFacing.WEST){
				//Magic out
				return (T) magic;
			}
			return null;
		}

		@Override
		public void neighborChanged(EnumFacing fromSide, Block blockIn){

		}

		@Override
		public void loadTick(){
			if(lifetimeBuffer){
				lifetimeBuffer = false;
				return;
			}
			watchMap.remove(index);
			active = false;
			CommonProxy.masterKey++;
		}

		@Override
		public boolean shouldRun(){
			return watchMap.containsKey(index);
		}

		private class BeamHandler implements IBeamHandler{

			@Override
			public void setMagic(BeamUnit mag){
				if(mag != null && user instanceof EntityLivingBase){
					EntityLivingBase ent = (EntityLivingBase) user;
					ItemStack offhand = ent.getHeldItem(EnumHand.OFF_HAND);
					if(offhand.getItem() == ModItems.beamCage){
						BeamUnit cageBeam = BeamCage.getStored(offhand);
						if(cageBeam == null){
							cageBeam = new BeamUnit(0, 0, 0, 0);
						}
						int energy = cageBeam.getEnergy();
						energy += mag.getEnergy();
						energy = Math.min(1024, energy);

						int potential = cageBeam.getPotential();
						potential += mag.getPotential();
						potential = Math.min(1024, potential);

						int stability = cageBeam.getStability();
						stability += mag.getStability();
						stability = Math.min(1024, stability);

						int voi = cageBeam.getVoid();
						voi += mag.getVoid();
						voi = Math.min(1024, voi);

						BeamCage.storeBeam(offhand, energy + potential + stability + voi == 0 ? null : new BeamUnit(energy, potential, stability, voi));
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

			@Override
			public double[] getMotionData(){
				return motionData;
			}

			private double rotRatio;
			private byte updateKey;

			@Override
			public void propogate(IAxisHandler masterIn, byte key, double rotRatioIn, double lastRadius, boolean renderOffset){
				//If true, this has already been checked.
				if(key == updateKey || masterIn.addToList(this)){
					return;
				}

				rotRatio = rotRatioIn == 0 ? 1 : rotRatioIn;
				updateKey = key;
			}

			@Override
			public double getMoInertia(){
				return 0;
			}

			@Override
			public double getRotationRatio(){
				return rotRatio;
			}

			@Override
			public void addEnergy(double energy, boolean allowInvert, boolean absolute){
				if(allowInvert && absolute){
					motionData[1] += energy;
				}else if(allowInvert){
					motionData[1] += energy * Math.signum(motionData[1]);
				}else if(absolute){
					int sign = (int) Math.signum(motionData[1]);
					motionData[1] += energy;
					if(sign != 0 && Math.signum(motionData[1]) != sign){
						motionData[1] = 0;
					}
				}else{
					int sign = (int) Math.signum(motionData[1]);
					motionData[1] += energy * ((double) sign);
					if(Math.signum(motionData[1]) != sign){
						motionData[1] = 0;
					}
				}
				//markDirty();
			}

			@Override
			public void markChanged(){

			}

			@Override
			public float getAngle(float partialTicks){
				return 0;
			}

			@Override
			public boolean shouldManageAngle(){
				return false;
			}
		}
	}
}
