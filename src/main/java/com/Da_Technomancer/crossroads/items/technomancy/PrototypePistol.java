package com.Da_Technomancer.crossroads.items.technomancy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.beams.BeamManager;
import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.API.beams.IBeamHandler;
import com.Da_Technomancer.crossroads.API.redstone.IAdvancedRedstoneHandler;
import com.Da_Technomancer.crossroads.API.rotary.IAxisHandler;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.API.rotary.RotaryUtil;
import com.Da_Technomancer.crossroads.API.technomancy.IPrototypeOwner;
import com.Da_Technomancer.crossroads.API.technomancy.IPrototypePort;
import com.Da_Technomancer.crossroads.API.technomancy.PrototypeInfo;
import com.Da_Technomancer.crossroads.API.technomancy.PrototypePortTypes;
import com.Da_Technomancer.crossroads.EventHandlerCommon;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.dimensions.ModDimensions;
import com.Da_Technomancer.crossroads.dimensions.PrototypeWorldProvider;
import com.Da_Technomancer.crossroads.dimensions.PrototypeWorldSavedData;
import com.Da_Technomancer.crossroads.entity.EntityBullet;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.ServerWorld;
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
		setCreativeTab(CRItems.TAB_CROSSROADS);
		setMaxStackSize(1);
		CRItems.toRegister.add(this);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced){
		if(!stack.hasTag()){
			stack.put(new CompoundNBT());
		}
		tooltip.add("Ammo: " + stack.getTag().getInt("ammo") + "/" + MAG_SIZE);
		CompoundNBT prototypeNBT = stack.getTag().getCompound("prot");
		if(prototypeNBT.contains("name")){
			tooltip.add("Name: " + prototypeNBT.getString("name"));
		}
		super.addInformation(stack, world, tooltip, advanced);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand hand){
		if(!worldIn.isRemote && hand == Hand.MAIN_HAND && playerIn.getHeldItem(hand).hasTag()){
			CompoundNBT nbt = playerIn.getHeldItem(hand).getTag();
			int index = nbt.getCompound("prot").getInt("index");
			if(playerIn.isSneaking()){
				if(nbt.getInt("ammo") < MAG_SIZE){
					for(int i = 0; i < playerIn.inventory.getSizeInventory(); i++){
						if(playerIn.inventory.getStackInSlot(i).getItem() == Items.IRON_NUGGET){
							playerIn.getHeldItem(hand).getTag().putInt("ammo", 1 + playerIn.getHeldItem(hand).getTag().getInt("ammo"));
							playerIn.inventory.decrStackSize(i, 1);
							worldIn.playSound(null, playerIn.getPosition(), SoundEvents.ITEM_ARMOR_EQUIP_IRON, SoundCategory.PLAYERS, 5, 1F);
							return new ActionResult<ItemStack>(ActionResultType.SUCCESS, playerIn.getHeldItem(hand));
						}
					}
				}
				return new ActionResult<ItemStack>(ActionResultType.FAIL, playerIn.getHeldItem(hand));
			}else if(pistolMap.containsKey(index) && nbt.getInt("ammo") != 0 && !playerIn.isSneaking()){
				PistolPrototypeOwner owner = pistolMap.get(index);
				if(owner.axle.getMotionData()[0] != 0){
					EntityBullet bullet = new EntityBullet(playerIn.world, playerIn, getDamage(owner.axle.getMotionData()[0]));
					bullet.setPosition(playerIn.posX, playerIn.posY + playerIn.getEyeHeight(), playerIn.posZ);
					bullet.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0, getBulletSpeed(owner.axle.getMotionData()[0]) /*In blocks/tick*/, 0);
					bullet.ignoreEntity = playerIn;
					playerIn.world.addEntity(bullet);
					nbt.putInt("ammo", nbt.getInt("ammo") - 1);
					playerIn.resetActiveHand();
					playerIn.world.playSound(null, playerIn.getPosition(), SoundEvents.BLOCK_METAL_PRESSPLATE_CLICK_ON, SoundCategory.PLAYERS, 15, ((float) getBulletSpeed(owner.axle.getMotionData()[0])) % 1F);
					return new ActionResult<ItemStack>(ActionResultType.SUCCESS, playerIn.getHeldItem(hand));
				}
			}
		}
		return super.onItemRightClick(worldIn, playerIn, hand);
	}

	@Override
	public void onUsingTick(ItemStack stack, LivingEntity player, int count){
		super.onUsingTick(stack, player, count);
		if(!player.world.isRemote && stack.hasTag() && stack.getTag().contains("prot") && player.getActiveHand() == Hand.MAIN_HAND){
			CompoundNBT prototypeNBT = stack.getTag().getCompound("prot");
			int index = prototypeNBT.getInt("index");

			CompoundNBT playerNBT = player.getPersistentData();
			if(getMaxItemUseDuration(stack) == count || playerNBT.getBoolean("wasSneak") != player.isSneaking()){
				if(pistolMap.containsKey(index)){
					pistolMap.get(index).mouseActive = true;
					playerNBT.putBoolean("wasSneak", player.isSneaking());

					Direction dir = Direction.SOUTH;
					PrototypeInfo info = PrototypeWorldSavedData.get(true).prototypes.get(index);
					ServerWorld worldDim = DimensionManager.getWorld(ModDimensions.PROTOTYPE_DIM_ID);
					if(info != null && info.ports[dir.getIndex()] != null && info.ports[dir.getIndex()] == PrototypePortTypes.REDSTONE_IN){
						BlockPos relPos = info.portPos[dir.getIndex()].offset(dir);
						relPos = info.chunk.getBlock(relPos.getX(), relPos.getY(), relPos.getZ());
						worldDim.getBlockState(relPos).neighborChanged(worldDim, relPos, CrossroadsBlocks.prototypePort, relPos.offset(dir.getOpposite()));
					}
				}
			}
		}
	}

	private float getBulletSpeed(double gearSpeed){
		return (float) Math.abs(gearSpeed) * 0.5F;
	}

	private int getDamage(double gearSpeed){
		int maxDamage = CRConfig.maximumPistolDamage.get();

		return maxDamage < 0 ? (int) Math.round(gearSpeed * 4F) : Math.min(maxDamage, (int) Math.round(Math.abs(gearSpeed) * 4F));
	}

	@Override
	public void preChanged(ItemStack stack, PlayerEntity player){
		onPlayerStoppedUsing(stack, player.world, player, 0);
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected){
		if(isSelected && !worldIn.isRemote && stack.hasTag() && stack.getTag().contains("prot")){
			CompoundNBT prototypeNBT = stack.getTag().getCompound("prot");
			int index = prototypeNBT.getInt("index");
			PrototypeWorldSavedData data = PrototypeWorldSavedData.get(true);
			if(!pistolMap.containsKey(index)){
				if(data.prototypes.size() <= index || data.prototypes.get(index) == null){
					stack.getTag().remove("prot");
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

			if(entityIn instanceof PlayerEntity && BeamManager.beamStage == 0 && ((PlayerEntity) entityIn).getHeldItem(Hand.OFF_HAND).getItem() == CRItems.beamCage && ((PlayerEntity) entityIn).getHeldItem(Hand.OFF_HAND).hasTag()){
				CompoundNBT nbt = stack.getTag();
				PrototypeInfo info = data.prototypes.get(index);
				BlockPos portPos = info.ports[5] == PrototypePortTypes.MAGIC_IN ? info.portPos[5] : null;
				if(portPos == null){
					return;
				}
				ServerWorld worldDim = DimensionManager.getWorld(ModDimensions.PROTOTYPE_DIM_ID);
				TileEntity te = worldDim.getTileEntity(info.chunk.getBlock(portPos.getX(), portPos.getY(), portPos.getZ()));
				if(!(te instanceof IPrototypePort)){
					return;
				}
				IPrototypePort port = (IPrototypePort) te;
				if(!port.hasCapPrototype(Capabilities.BEAM_CAPABILITY)){
					return;
				}

				int energy = nbt.getInt(EnumBeamAlignments.ENERGY.name());
				int potential = nbt.getInt(EnumBeamAlignments.POTENTIAL.name());
				int stability = nbt.getInt(EnumBeamAlignments.STABILITY.name());
				int voi = nbt.getInt(EnumBeamAlignments.VOID.name());
				ItemStack heldCage = ((PlayerEntity) entityIn).getHeldItem(Hand.OFF_HAND);
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

	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft){
		if(!worldIn.isRemote && stack.hasTag() && stack.getTag().contains("prot")){
			CompoundNBT prototypeNBT = stack.getTag().getCompound("prot");
			int index = prototypeNBT.getInt("index");
			if(pistolMap.containsKey(index)){
				pistolMap.get(index).mouseActive = false;

				//Disable redstone
				Direction dir = Direction.SOUTH;
				PrototypeInfo info = PrototypeWorldSavedData.get(true).prototypes.get(index);
				ServerWorld worldDim = DimensionManager.getWorld(ModDimensions.PROTOTYPE_DIM_ID);
				if(info != null && info.ports[dir.getIndex()] != null && info.ports[dir.getIndex()] == PrototypePortTypes.REDSTONE_IN){
					BlockPos relPos = info.portPos[dir.getIndex()].offset(dir);
					relPos = info.chunk.getBlock(relPos.getX(), relPos.getY(), relPos.getZ());
					worldDim.getBlockState(relPos).neighborChanged(worldDim, relPos, CrossroadsBlocks.prototypePort, relPos.offset(dir.getOpposite()));
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
		public boolean hasCap(Capability<?> cap, Direction side){
			return cap == Capabilities.AXLE_CAPABILITY && side == Direction.UP || cap == Capabilities.ADVANCED_REDSTONE_CAPABILITY && side == Direction.SOUTH || cap == Capabilities.BEAM_CAPABILITY && side == Direction.WEST;
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T> T getCap(Capability<T> cap, Direction side){
			if(!active){
				return null;
			}
			if(cap == Capabilities.AXLE_CAPABILITY && side == Direction.UP){
				//Motor
				return (T) axle;
			}
			if(cap == Capabilities.ADVANCED_REDSTONE_CAPABILITY && side == Direction.SOUTH){
				//Control
				return (T) redstone;
			}
			if(cap == Capabilities.BEAM_CAPABILITY && side == Direction.WEST){
				//Magic out
				return (T) magic;
			}
			return null;
		}

		@Override
		public void neighborChanged(Direction fromSide, Block blockIn){

		}

		@Override
		public void loadTick(){
			if(lifetimeBuffer){
				lifetimeBuffer = false;
				return;
			}
			pistolMap.remove(index);
			active = false;
			RotaryUtil.increaseMasterKey(true);
		}

		@Override
		public boolean shouldRun(){
			return pistolMap.containsKey(index);
		}

		private class BeamHandler implements IBeamHandler{

			@Override
			public void setMagic(BeamUnit mag){
				if(mag != null && user instanceof LivingEntity){
					LivingEntity ent = (LivingEntity) user;
					ItemStack offhand = ent.getHeldItem(Hand.OFF_HAND);
					if(offhand.getItem() == CRItems.beamCage){
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
