package com.Da_Technomancer.crossroads.tileentities.electric;

import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.templates.ILinkTE;
import com.Da_Technomancer.crossroads.blocks.electric.TeslaCoilTop;
import com.Da_Technomancer.crossroads.render.RenderUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class TeslaCoilTopTileEntity extends TileEntity implements IInfoTE, ILinkTE{

	public static final int[] COLOR_CODES = {0xFFECCFFF, 0xFFFCDFFF, 0xFFFFFAFF};
	private static final int[] ATTACK_COLOR_CODES = {0xFFFFCCCC, 0xFFFFFFCC, 0xFFFFFAFA};

	//Relative to this TileEntity's position
	private ArrayList<BlockPos> linked = new ArrayList<>(3);

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

	@Override
	public void receiveLong(byte identifier, long message, @Nullable EntityPlayerMP sendingPlayer){
		if(identifier == LINK_PACKET_ID){
			linked.add(BlockPos.fromLong(message));
		}else if(identifier == CLEAR_PACKET_ID){
			linked.clear();
		}
	}

	protected boolean jolt(TeslaCoilTileEntity coilTE){
		int range = getVariant().range;
		int joltQty = getVariant().joltAmt;
		
		if(getVariant() == TeslaCoilTop.TeslaCoilVariants.ATTACK){
			//ATTACK
			List<EntityLivingBase> ents = world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(pos.getX() - range, pos.getY() - range, pos.getZ() - range, pos.getX() + range, pos.getY() + range, pos.getZ() + range), EntitySelectors.IS_ALIVE);

			if(!ents.isEmpty() && coilTE.stored >= joltQty){
				EntityLivingBase ent = ents.get(world.rand.nextInt(ents.size()));
				coilTE.stored -= joltQty;
				markDirty();

				RenderUtil.addArc(world.provider.getDimension(), pos.getX() + 0.5F, pos.getY() + 0.75F, pos.getZ() + 0.5F, (float) ent.posX, (float) ent.posY, (float) ent.posZ, 5, 0.6F, ATTACK_COLOR_CODES[(int) (world.getTotalWorldTime() % 3)]);
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
						if(tcTe.handlerIn.getMaxEnergyStored() - tcTe.stored > joltQty * (double) getVariant().efficiency / 100D){
							tcTe.stored += joltQty * (double) getVariant().efficiency / 100D;
							tcTe.markDirty();
							coilTE.stored -= joltQty;
							markDirty();

							RenderUtil.addArc(world.provider.getDimension(), pos.getX() + 0.5F, pos.getY() + 0.75F, pos.getZ() + 0.5F, actualPos.getX() + 0.5F, actualPos.getY() + 1.75F, actualPos.getZ() + 0.5F, 3, 0.3F, COLOR_CODES[(int) (world.getTotalWorldTime() % 3)]);
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
		for(BlockPos link : linked){
			chat.add("Linked Position: X=" + (pos.getX() + link.getX()) + " Y=" + (pos.getY() + link.getY()) + " Z=" + (pos.getZ() + link.getZ()));
		}
	}

	@Override
	public NBTTagCompound getUpdateTag(){
		NBTTagCompound nbt = super.getUpdateTag();
		for(int i = 0; i < linked.size(); i++){
			nbt.setLong("link" + i, linked.get(i).toLong());
		}
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		for(int i = 0; i < 3; i++){
			if(nbt.hasKey("link" + i)){
				linked.add(BlockPos.fromLong(nbt.getLong("link" + i)));
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		for(int i = 0; i < linked.size(); i++){
			nbt.setLong("link" + i, linked.get(i).toLong());
		}
		return nbt;
	}

	@Override
	public TileEntity getTE(){
		return this;
	}

	@Override
	public boolean canBeginLinking(){
		return getVariant() != TeslaCoilTop.TeslaCoilVariants.ATTACK;
	}

	@Override
	public boolean canLink(ILinkTE otherTE){
		return otherTE instanceof TeslaCoilTopTileEntity && getVariant() != TeslaCoilTop.TeslaCoilVariants.ATTACK;
	}

	@Override
	public ArrayList<BlockPos> getLinks(){
		return linked;
	}

	@Override
	public int getRange(){
		return getVariant().range;
	}
}
