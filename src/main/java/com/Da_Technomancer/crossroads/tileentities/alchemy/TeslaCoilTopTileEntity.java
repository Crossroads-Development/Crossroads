package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.alchemy.LooseArcRenderable;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendLooseArcToClient;
import com.Da_Technomancer.crossroads.blocks.alchemy.TeslaCoilTop;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TeslaCoilTopTileEntity extends TileEntity implements IInfoTE{

	public static final int[] COLOR_CODES = {new Color(128, 0, 255, 128).getRGB(), new Color(64, 0, 255, 128).getRGB(), new Color(100, 0, 255, 128).getRGB()};
	private static final int[] ATTACK_COLOR_CODES = {new Color(255, 32, 0, 128).getRGB(), new Color(255, 0, 32, 128).getRGB(), new Color(255, 32, 32, 128).getRGB()};
	private static final float EFFICIENCY = 0.95F;

	//Relative to this TileEntity's position
	public BlockPos[] linked = new BlockPos[3];

	private TeslaCoilTop.TeslaCoilVariants variant = null;

	private TeslaCoilTop.TeslaCoilVariants getVariant(){
		if(variant == null){
			IBlockState state = world.getBlockState(pos);
			if(state.getBlock() instanceof TeslaCoilTop){
				variant = ((TeslaCoilTop) state.getBlock()).variant;
			}else{
				invalidate();
				return TeslaCoilTop.TeslaCoilVariants.NORMAL;
			}
		}
		return variant;
	}

	protected boolean jolt(TeslaCoilTileEntity coilTE){
		int range = getVariant().range;
		int joltQty = getVariant().joltAmt;
		
		if(getVariant() == TeslaCoilTop.TeslaCoilVariants.ATTACK){
			//ATTACK
			List<EntityLivingBase> ents = world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(pos.getX() - range, pos.getY() - range, pos.getZ() - range, pos.getX() + range, pos.getY() + range, pos.getZ() + range), EntitySelectors.IS_ALIVE);

			if(!ents.isEmpty()){
				EntityLivingBase ent = ents.get(world.rand.nextInt(ents.size()));
				coilTE.stored -= joltQty;
				markDirty();

				ModPackets.network.sendToAllAround(new SendLooseArcToClient(new LooseArcRenderable(pos.getX() + 0.5F, pos.getY() + 2F, pos.getZ() + 0.5F, (float) ent.posX, (float) ent.posY, (float) ent.posZ, 5, 0.6F, 1F, ATTACK_COLOR_CODES[(int) (world.getTotalWorldTime() % 3)])), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
				world.playSound(null, pos.getX() + 0.5F, pos.getY() + 2F, pos.getZ() + 0.5F, SoundEvents.BLOCK_REDSTONE_TORCH_BURNOUT, SoundCategory.BLOCKS, 0.1F, 0F);

				ent.onStruckByLightning(new EntityLightningBolt(world, ent.posX, ent.posY, ent.posZ, true));
				return true;
			}
		}else{
			//TRANSFER
			for(BlockPos linkPos : linked){
				if(linkPos != null && coilTE.stored >= joltQty && linkPos.distanceSq(BlockPos.ORIGIN) <= range * range){
					BlockPos actualPos = linkPos.add(pos.getX(), pos.getY() - 1, pos.getZ());
					TileEntity te = world.getTileEntity(actualPos);
					if(te instanceof TeslaCoilTileEntity && world.getTileEntity(actualPos.up()) instanceof TeslaCoilTopTileEntity){
						TeslaCoilTileEntity tcTe = (TeslaCoilTileEntity) te;
						if(tcTe.handlerIn.getMaxEnergyStored() - tcTe.stored > joltQty * EFFICIENCY){
							tcTe.stored += joltQty * EFFICIENCY;
							tcTe.markDirty();
							coilTE.stored -= joltQty;
							markDirty();

							ModPackets.network.sendToAllAround(new SendLooseArcToClient(new LooseArcRenderable(pos.getX() + 0.5F, pos.getY() + 1F, pos.getZ() + 0.5F, actualPos.getX() + 0.5F, actualPos.getY() + 2F, actualPos.getZ() + 0.5F, 5, 0.6F, 1F, COLOR_CODES[(int) (world.getTotalWorldTime() % 3)])), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
							world.playSound(null, pos.getX() + 0.5F, pos.getY() + 2F, pos.getZ() + 0.5F, SoundEvents.BLOCK_REDSTONE_TORCH_BURNOUT, SoundCategory.BLOCKS, 0.1F, 0F);
							break;
						}
					}
				}
			}
		}


		return false;
	}

	@Override
	public void addInfo(ArrayList<String> chat, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ){
		for(int i = 0; i < 3; i++){
			if(linked[i] != null){
				chat.add("Linked Position: X=" + (pos.getX() + linked[i].getX()) + " Y=" + (pos.getY() + linked[i].getY()) + " Z=" + (pos.getZ() + linked[i].getZ()));
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		for(int i = 0; i < 3; i++){
			if(nbt.hasKey("link" + i)){
				linked[i] = BlockPos.fromLong(nbt.getLong("link" + i));
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		for(int i = 0; i < 3; i++){
			if(linked[i] != null){
				nbt.setLong("link" + i, linked[i].toLong());
			}
		}
		return nbt;
	}
}
