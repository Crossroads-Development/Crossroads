package com.Da_Technomancer.crossroads.items.technomancy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.redstone.IAdvancedRedstoneHandler;
import com.Da_Technomancer.crossroads.API.beams.BeamManager;
import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.API.beams.IBeamHandler;
import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.essentials.shared.IAxisHandler;
import com.Da_Technomancer.essentials.shared.IAxleHandler;
import com.Da_Technomancer.crossroads.API.technomancy.IPrototypeOwner;
import com.Da_Technomancer.crossroads.API.technomancy.IPrototypePort;
import com.Da_Technomancer.crossroads.API.technomancy.PrototypeInfo;
import com.Da_Technomancer.crossroads.API.technomancy.PrototypePortTypes;
import com.Da_Technomancer.crossroads.CommonProxy;
import com.Da_Technomancer.crossroads.EventHandlerCommon;
import com.Da_Technomancer.crossroads.ModConfig;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.dimensions.ModDimensions;
import com.Da_Technomancer.crossroads.dimensions.PrototypeWorldProvider;
import com.Da_Technomancer.crossroads.dimensions.PrototypeWorldSavedData;
import com.Da_Technomancer.crossroads.entity.EntityBullet;
import com.Da_Technomancer.crossroads.items.ModItems;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;

public class PrototypePistol extends BeamUsingItem{

	/**
	 * Stores the IPrototypeOwners of active pistols to prevent them from being garbage collected. 
	 */
	private static final HashMap<Integer, PistolPrototypeOwner> pistolMap = new HashMap<Integer, PistolPrototypeOwner>();
	private static final int MAG_SIZE = 6;

	public PrototypePistol(){
		String name = "prototype_pistol";
		setTranslationKey(name);
		setRegistryName(name);
		setCreativeTab(ModItems.TAB_CROSSROADS);
		ModItems.toRegister.add(this);
		ModItems.itemAddQue(this);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced){
		if(!stack.hasTagCompound()){
			stack.setTagCompound(new NBTTagCompound());
		}
		tooltip.add("Ammo: " + stack.getTagCompound().getInteger("ammo") + "/" + MAG_SIZE);
		NBTTagCompound prototypeNBT = stack.getTagCompound().getCompoundTag("prot");
		if(prototypeNBT.hasKey("name")){
			tooltip.add("Name: " + prototypeNBT.getString("name"));
		}
		super.addInformation(stack, world, tooltip, advanced);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand){
		if(!worldIn.isRemote && hand == EnumHand.MAIN_HAND && playerIn.getHeldItem(hand).hasTagCompound()){
			NBTTagCompound nbt = playerIn.getHeldItem(hand).getTagCompound();
			int index = nbt.getCompoundTag("prot").getInteger("index");
			if(playerIn.isSneaking()){
				if(nbt.getInteger("ammo") < MAG_SIZE){
					for(int i = 0; i < playerIn.inventory.getSizeInventory(); i++){
						if(playerIn.inventory.getStackInSlot(i).getItem() == Items.IRON_NUGGET){
							playerIn.getHeldItem(hand).getTagCompound().setInteger("ammo", 1 + playerIn.getHeldItem(hand).getTagCompound().getInteger("ammo"));
							playerIn.inventory.decrStackSize(i, 1);
							worldIn.playSound(null, playerIn.getPosition(), SoundEvents.ITEM_ARMOR_EQUIP_IRON, SoundCategory.PLAYERS, 5, 1F);
							return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(hand));
						}
					}
				}
				return new ActionResult<ItemStack>(EnumActionResult.FAIL, playerIn.getHeldItem(hand));
			}else if(pistolMap.containsKey(index) && nbt.getInteger("ammo") != 0 && !playerIn.isSneaking()){
				PistolPrototypeOwner owner = pistolMap.get(index);
				if(owner.axle.getMotionData()[0] != 0){
					EntityBullet bullet = new EntityBullet(playerIn.world, playerIn, getDamage(owner.axle.getMotionData()[0]));
					bullet.setPosition(playerIn.posX, playerIn.posY + playerIn.getEyeHeight(), playerIn.posZ);
					bullet.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0, getBulletSpeed(owner.axle.getMotionData()[0]) /*In blocks/tick*/, 0);
					bullet.ignoreEntity = playerIn;
					playerIn.world.spawnEntity(bullet);
					nbt.setInteger("ammo", nbt.getInteger("ammo") - 1);
					playerIn.resetActiveHand();
					playerIn.world.playSound(null, playerIn.getPosition(), SoundEvents.BLOCK_METAL_PRESSPLATE_CLICK_ON, SoundCategory.PLAYERS, 15, ((float) getBulletSpeed(owner.axle.getMotionData()[0])) % 1F);
					return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(hand));
				}
			}
		}
		return super.onItemRightClick(worldIn, playerIn, hand);
	}

	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase player, int count){
		super.onUsingTick(stack, player, count);
		if(!player.world.isRemote && stack.hasTagCompound() && stack.getTagCompound().hasKey("prot") && player.getActiveHand() == EnumHand.MAIN_HAND){
			NBTTagCompound prototypeNBT = stack.getTagCompound().getCompoundTag("prot");
			int index = prototypeNBT.getInteger("index");

			NBTTagCompound playerNBT = player.getEntityData();
			if(getMaxItemUseDuration(stack) == count || playerNBT.getBoolean("wasSneak") != player.isSneaking()){
				if(pistolMap.containsKey(index)){
					pistolMap.get(index).mouseActive = true;
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

	private float getBulletSpeed(double gearSpeed){
		return (float) Math.abs(gearSpeed) * 0.5F;
	}

	private int getDamage(double gearSpeed){
		int maxDamage = ModConfig.getConfigInt(ModConfig.maximumPistolDamage, false);

		return maxDamage < 0 ? (int) Math.round(gearSpeed * 4F) : Math.min(maxDamage, (int) Math.round(Math.abs(gearSpeed) * 4F));
	}

	@Override
	public void preChanged(ItemStack stack, EntityPlayer player){
		onPlayerStoppedUsing(stack, player.world, player, 0);
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected){
		if(isSelected && !worldIn.isRemote && stack.hasTagCompound() && stack.getTagCompound().hasKey("prot")){
			NBTTagCompound prototypeNBT = stack.getTagCompound().getCompoundTag("prot");
			int index = prototypeNBT.getInteger("index");
			PrototypeWorldSavedData data = PrototypeWorldSavedData.get(true);
			if(!pistolMap.containsKey(index)){
				if(data.prototypes.size() <= index || data.prototypes.get(index) == null){
					stack.getTagCompound().removeTag("prot");
					return;
				}
				PistolPrototypeOwner owner = new PistolPrototypeOwner(index, entityIn);
				pistolMap.put(index, owner);
				data.prototypes.get(index).owner = new WeakReference<IPrototypeOwner>(owner);
				EventHandlerCommon.updateLoadedPrototypeChunks();
			}else{
				pistolMap.get(index).lifetimeBuffer = true;
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
				if(!port.hasCapPrototype(Capabilities.MAGIC_HANDLER_CAPABILITY)){
					return;
				}

				int energy = nbt.getInteger(EnumBeamAlignments.ENERGY.name());
				int potential = nbt.getInteger(EnumBeamAlignments.POTENTIAL.name());
				int stability = nbt.getInteger(EnumBeamAlignments.STABILITY.name());
				int voi = nbt.getInteger(EnumBeamAlignments.VOID.name());
				if(energy <= cageNbt.getInteger("stored_" + EnumBeamAlignments.ENERGY.name()) && potential <= cageNbt.getInteger("stored_" + EnumBeamAlignments.POTENTIAL.name()) && stability <= cageNbt.getInteger("stored_" + EnumBeamAlignments.STABILITY.name()) && voi <= cageNbt.getInteger("stored_" + EnumBeamAlignments.VOID.name())){
					if(energy + potential + stability + voi > 0){
						cageNbt.setInteger("stored_" + EnumBeamAlignments.ENERGY.name(), cageNbt.getInteger("stored_" + EnumBeamAlignments.ENERGY.name()) - energy);
						cageNbt.setInteger("stored_" + EnumBeamAlignments.POTENTIAL.name(), cageNbt.getInteger("stored_" + EnumBeamAlignments.POTENTIAL.name()) - potential);
						cageNbt.setInteger("stored_" + EnumBeamAlignments.STABILITY.name(), cageNbt.getInteger("stored_" + EnumBeamAlignments.STABILITY.name()) - stability);
						cageNbt.setInteger("stored_" + EnumBeamAlignments.VOID.name(), cageNbt.getInteger("stored_" + EnumBeamAlignments.VOID.name()) - voi);
						port.getCapPrototype(Capabilities.MAGIC_HANDLER_CAPABILITY).setMagic(new BeamUnit(energy, potential, stability, voi));
					}
				}
			}
		}
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft){
		if(!worldIn.isRemote && stack.hasTagCompound() && stack.getTagCompound().hasKey("prot")){
			NBTTagCompound prototypeNBT = stack.getTagCompound().getCompoundTag("prot");
			int index = prototypeNBT.getInteger("index");
			if(pistolMap.containsKey(index)){
				pistolMap.get(index).mouseActive = false;

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

	private static class PistolPrototypeOwner implements IPrototypeOwner{

		private boolean lifetimeBuffer = true;
		private boolean active = true;
		private final int index;
		private boolean mouseActive = false;
		private final Entity user;

		private PistolPrototypeOwner(int index, Entity user){
			this.index = index;
			this.user = user;
		}

		private final IAxleHandler axle = new AxleHandler();
		private final IAdvancedRedstoneHandler redstone = new RedstoneHandler();
		private final BeamHandler magic = new BeamHandler();

		@Override
		public boolean hasCap(Capability<?> cap, EnumFacing side){
			return cap == Capabilities.AXLE_HANDLER_CAPABILITY && side == EnumFacing.UP || cap == Capabilities.ADVANCED_REDSTONE_HANDLER_CAPABILITY && side == EnumFacing.SOUTH || cap == Capabilities.MAGIC_HANDLER_CAPABILITY && side == EnumFacing.WEST;
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T> T getCap(Capability<T> cap, EnumFacing side){
			if(!active){
				return null;
			}
			if(cap == Capabilities.AXLE_HANDLER_CAPABILITY && side == EnumFacing.UP){
				//Motor
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
		public void loadTick(){
			if(lifetimeBuffer){
				lifetimeBuffer = false;
				return;
			}
			pistolMap.remove(index);
			active = false;
			CommonProxy.masterKey++;
		}

		@Override
		public boolean shouldRun(){
			return pistolMap.containsKey(index);
		}

		private class BeamHandler implements IBeamHandler{

			@Override
			public void setMagic(BeamUnit mag){
				if(mag != null && user instanceof EntityLivingBase){
					EntityLivingBase ent = (EntityLivingBase) user;
					ItemStack offhand = ent.getHeldItem(EnumHand.OFF_HAND);
					if(offhand.getItem() == ModItems.beamCage){
						if(offhand.getTagCompound() == null){
							offhand.setTagCompound(new NBTTagCompound());
						}
						NBTTagCompound nbt = offhand.getTagCompound();
						int energy = nbt.getInteger("stored_" + EnumBeamAlignments.ENERGY.name());
						energy += mag.getEnergy();
						energy = Math.min(1024, energy);
						nbt.setInteger("stored_" + EnumBeamAlignments.ENERGY.name(), energy);

						int potential = nbt.getInteger("stored_" + EnumBeamAlignments.POTENTIAL.name());
						potential += mag.getPotential();
						potential = Math.min(1024, potential);
						nbt.setInteger("stored_" + EnumBeamAlignments.POTENTIAL.name(), potential);

						int stability = nbt.getInteger("stored_" + EnumBeamAlignments.STABILITY.name());
						stability += mag.getStability();
						stability = Math.min(1024, stability);
						nbt.setInteger("stored_" + EnumBeamAlignments.STABILITY.name(), stability);

						int voi = nbt.getInteger("stored_" + EnumBeamAlignments.VOID.name());
						voi += mag.getVoid();
						voi = Math.min(1024, voi);
						nbt.setInteger("stored_" + EnumBeamAlignments.VOID.name(), voi);
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
			public void propogate(IAxisHandler masterIn, byte key, double rotRatioIn, double lastRadius){
				//If true, this has already been checked.
				if(key == updateKey || masterIn.addToList(this)){
					return;
				}

				rotRatio = rotRatioIn == 0 ? 1 : rotRatioIn;
				updateKey = key;
			}

			@Override
			public double getMoInertia(){
				return 10;
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
				//markDirty();
				//Storing the energy long term in this case is difficult. It could be done through nbt, but then there is a risk of gaining energy.
				//Instead, this just loses all stored energy between loads. It should be a very small amount in any case.
			}

			@Override
			public boolean shouldManageAngle(){
				return false;
			}
		}
	}
}
