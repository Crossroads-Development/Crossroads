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
import com.Da_Technomancer.crossroads.entity.EntityBullet;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PrototypePistol extends MagicUsingItem{
	
	/**
	 * Stores the IPrototypeOwners of active pistols to prevent them from being garbage collected. 
	 */
	private static final HashMap<Integer, PistolPrototypeOwner> pistolMap = new HashMap<Integer, PistolPrototypeOwner>();
	
	public PrototypePistol(){
		String name = "prototype_pistol";
		setUnlocalizedName(name);
		setRegistryName(name);
		GameRegistry.register(this);
		setCreativeTab(ModItems.tabCrossroads);
	}
	
	private boolean wasSneaking = false;
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced){
		if(!stack.hasTagCompound()){
			stack.setTagCompound(new NBTTagCompound());
		}
		tooltip.add("Loaded: " + stack.getTagCompound().getBoolean("loaded"));
		NBTTagCompound prototypeNBT = stack.getTagCompound().getCompoundTag("prot");
		if(prototypeNBT.hasKey("name")){
			tooltip.add("Name: " + prototypeNBT.getString("name"));
		}
		super.addInformation(stack, playerIn, tooltip, advanced);
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand){
		if(!worldIn.isRemote && playerIn.isSneaking() && hand == EnumHand.MAIN_HAND && playerIn.getHeldItem(hand).hasTagCompound() && !playerIn.getHeldItem(hand).getTagCompound().getBoolean("loaded")){
			for(int i = 0; i < playerIn.inventory.getSizeInventory(); i++){
				if(playerIn.inventory.getStackInSlot(i).getItem() == Items.field_191525_da){
					playerIn.getHeldItem(hand).getTagCompound().setBoolean("loaded", true);
					playerIn.inventory.decrStackSize(i, 1);
					worldIn.playSound(null, playerIn.getPosition(), SoundEvents.ITEM_ARMOR_EQUIP_IRON, SoundCategory.PLAYERS, 5, 1F);
					return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(hand));
				}
			}
			
			return new ActionResult<ItemStack>(EnumActionResult.FAIL, playerIn.getHeldItem(hand));
		}
		return super.onItemRightClick(worldIn, playerIn, hand);
	}
	
	private static final double MASS_OF_BULLET = .035D;
	
	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase player, int count){
		super.onUsingTick(stack, player, count);
		if(!player.world.isRemote && stack.hasTagCompound() && stack.getTagCompound().hasKey("prot") && player.getActiveHand() == EnumHand.MAIN_HAND){
			NBTTagCompound prototypeNBT = stack.getTagCompound().getCompoundTag("prot");
			int index = prototypeNBT.getInteger("index");
			
			if(getMaxItemUseDuration(stack) == count || wasSneaking != player.isSneaking()){
				if(pistolMap.containsKey(index)){
					pistolMap.get(index).mouseActive = true;
					wasSneaking = player.isSneaking();
					
					EnumFacing dir = EnumFacing.SOUTH;
					WorldServer worldDim = DimensionManager.getWorld(ModDimensions.PROTOTYPE_DIM_ID);
					PrototypeInfo info = PrototypeWorldSavedData.get().prototypes.get(index);
					if(info != null && info.ports[dir.getIndex()] != null && info.ports[dir.getIndex()] == PrototypePortTypes.REDSTONE_IN){
						BlockPos relPos = info.portPos[dir.getIndex()].offset(dir);
						worldDim.getBlockState(info.chunk.getBlock(relPos.getX(), relPos.getY(), relPos.getZ())).neighborChanged(worldDim, relPos, ModBlocks.prototypePort, relPos.offset(dir.getOpposite()));
					}
				}
			}
			if(getMaxItemUseDuration(stack) - count >= 15 && stack.getTagCompound().getBoolean("loaded") && !player.isSneaking() && pistolMap.containsKey(index)){
				PistolPrototypeOwner owner = pistolMap.get(index);
				if(owner.axle.getMotionData()[1] > 0){
					//In blocks(meters)/s Yes, this would be the IRL formula if all energy were losslessly transfered. And yes, I am insane.
					double speed = Math.sqrt(owner.axle.getMotionData()[1] * 2D / MASS_OF_BULLET);
					System.out.println(speed);
					EntityBullet bullet = new EntityBullet(player.world, player, (int) Math.round(speed / 20D), owner.magic.lastOut);
					bullet.setPosition(player.posX, player.posY + player.getEyeHeight(), player.posZ);
					bullet.setHeadingFromThrower(player, player.rotationPitch, player.rotationYaw, 0, (float) speed / 20F /*In blocks/tick*/, 0);
					player.world.spawnEntity(bullet);
					stack.getTagCompound().setBoolean("loaded", false);
					player.resetActiveHand();
					player.world.playSound(null, player.getPosition(), SoundEvents.BLOCK_METAL_PRESSPLATE_CLICK_ON, SoundCategory.PLAYERS, 15, ((float) speed) % 1F);
				}
				
				owner.magic.lastOut = null;
			}
		}
	}
	
	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected){
		if(isSelected && !worldIn.isRemote && stack.hasTagCompound() && stack.getTagCompound().hasKey("prot")){
			NBTTagCompound prototypeNBT = stack.getTagCompound().getCompoundTag("prot");
			int index = prototypeNBT.getInteger("index");
			PrototypeWorldSavedData data = PrototypeWorldSavedData.get();
			if(!pistolMap.containsKey(index)){
				if(data.prototypes.size() <= index || data.prototypes.get(index) == null){
					stack.getTagCompound().removeTag("prot");
					return;
				}
				PistolPrototypeOwner owner = new PistolPrototypeOwner(index, entityIn);
				pistolMap.put(index, owner);
				data.prototypes.get(index).owner = new WeakReference<IPrototypeOwner>(owner);
			}else{
				pistolMap.get(index).lifetimeBuffer = true;
			}
			
			if(entityIn instanceof EntityPlayer && ((EntityPlayer) entityIn).getHeldItem(EnumHand.OFF_HAND).getItem() == ModItems.beamCage){
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
	
	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft){
		if(!worldIn.isRemote && stack.hasTagCompound() && stack.getTagCompound().hasKey("prot")){
			NBTTagCompound prototypeNBT = stack.getTagCompound().getCompoundTag("prot");
			int index = prototypeNBT.getInteger("index");
			if(pistolMap.containsKey(index)){
				pistolMap.get(index).mouseActive = false;
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
		public boolean loadTick(){
			if(lifetimeBuffer){
				lifetimeBuffer = false;
				return false;
			}
			pistolMap.remove(index);
			active = false;
			CommonProxy.masterKey++;
			return true;
		}
		
		private class MagicHandler implements IMagicHandler{
			
			private MagicUnit lastOut;
			
			@Override
			public void setMagic(MagicUnit mag){
				if(mouseActive){
					lastOut = mag;
					return;
				}
				
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
			private final double[] physData = new double[] {2 * MASS_OF_BULLET, 10};
			
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
				//markDirty();
				//Storing the energy long term in this case is difficult. It could be done through nbt, but then there is a risk of gaining energy.
				//Instead, this just loses all stored energy between loads. It should be a small amount in any case.
			}
		}
	}
}
