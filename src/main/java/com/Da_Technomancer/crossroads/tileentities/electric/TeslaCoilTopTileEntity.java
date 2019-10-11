package com.Da_Technomancer.crossroads.tileentities.electric;

import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.blocks.electric.TeslaCoilTop;
import com.Da_Technomancer.crossroads.render.RenderUtil;
import com.Da_Technomancer.essentials.tileentities.ILinkTE;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;

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
			BlockState state = world.getBlockState(pos);
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
	public void receiveLong(byte identifier, long message, @Nullable ServerPlayerEntity sendingPlayer){
		if(identifier == LINK_PACKET_ID){
			linked.add(BlockPos.fromLong(message));
		}else if(identifier == CLEAR_PACKET_ID){
			linked.clear();
		}
	}

	protected void jolt(TeslaCoilTileEntity coilTE){
		TeslaCoilTop.TeslaCoilVariants variant = getVariant();
		int range = variant.range;
		int joltQty = variant.joltAmt;

		if(variant == TeslaCoilTop.TeslaCoilVariants.ATTACK){
			if(world.isRemote){
				return;
			}

			//ATTACK
			List<LivingEntity> ents = world.getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB(pos.getX() - range, pos.getY() - range, pos.getZ() - range, pos.getX() + range, pos.getY() + range, pos.getZ() + range), EntityPredicates.IS_ALIVE);

			if(!ents.isEmpty() && coilTE.getStored() >= joltQty){
				LivingEntity ent = ents.get(world.rand.nextInt(ents.size()));
				coilTE.setStored(coilTE.getStored() - joltQty);
				markDirty();

				RenderUtil.addArc(world.provider.getDimension(), pos.getX() + 0.5F, pos.getY() + 0.75F, pos.getZ() + 0.5F, (float) ent.posX, (float) ent.posY, (float) ent.posZ, 5, 0.6F, ATTACK_COLOR_CODES[(int) (world.getGameTime() % 3)]);
				ent.onStruckByLightning(new LightningBoltEntity(world, ent.posX, ent.posY, ent.posZ, true));
			}
		}else if(variant == TeslaCoilTop.TeslaCoilVariants.DECORATIVE){
			if(coilTE.getStored() >= TeslaCoilTop.TeslaCoilVariants.DECORATIVE.joltAmt){
				if(world.isRemote){
					//Spawn the purely decorative bolts on the client side directly to reduce packet load
					int count = world.rand.nextInt(5) + 1;
					Vec3d start = new Vec3d(pos.getX() + 0.5F, pos.getY() + 0.75F, pos.getZ() + 0.5F);
					for(int i = 0; i < count; i++){
						float angle = world.rand.nextFloat() * 2F * (float) Math.PI;
						float rad = world.rand.nextFloat() * 2F + 3F;
						Vec3d end = start.add(new Vec3d(rad * Math.cos(angle), world.rand.nextFloat() * 2F - 1F, rad * Math.sin(angle)));
						RenderUtil.addArc(world.provider.getDimension(), start, end, 4, 0.6F, COLOR_CODES[world.rand.nextInt(COLOR_CODES.length)]);
					}
				}else{
					coilTE.setStored(coilTE.getStored() - TeslaCoilTop.TeslaCoilVariants.DECORATIVE.joltAmt);
				}
			}
		}else if(!world.isRemote){
			//TRANSFER
			for(BlockPos linkPos : linked){
				if(linkPos != null && coilTE.getStored() >= joltQty && linkPos.distanceSq(BlockPos.ZERO) <= range * range){
					BlockPos actualPos = linkPos.add(pos.getX(), pos.getY() - 1, pos.getZ());
					TileEntity te = world.getTileEntity(actualPos);
					if(te instanceof TeslaCoilTileEntity && world.getTileEntity(actualPos.up()) instanceof TeslaCoilTopTileEntity){
						TeslaCoilTileEntity tcTe = (TeslaCoilTileEntity) te;
						if(tcTe.handlerIn.getMaxEnergyStored() - tcTe.getStored() > joltQty * (double) variant.efficiency / 100D){
							tcTe.setStored(tcTe.getStored() + (int) (joltQty * (double) variant.efficiency / 100D));
							tcTe.markDirty();
							coilTE.setStored(coilTE.getStored() - joltQty);
							markDirty();

							RenderUtil.addArc(world.provider.getDimension(), pos.getX() + 0.5F, pos.getY() + 0.75F, pos.getZ() + 0.5F, actualPos.getX() + 0.5F, actualPos.getY() + 1.75F, actualPos.getZ() + 0.5F, 3, 0.3F, COLOR_CODES[(int) (world.getGameTime() % 3)]);
							break;
						}
					}
				}
			}
		}
	}

	@Override
	public void addInfo(ArrayList<String> chat, PlayerEntity player, Direction side, BlockRayTraceResult hit){
		for(BlockPos link : linked){
			chat.add("Linked Position: X=" + (pos.getX() + link.getX()) + " Y=" + (pos.getY() + link.getY()) + " Z=" + (pos.getZ() + link.getZ()));
		}
	}

	@Override
	public CompoundNBT getUpdateTag(){
		CompoundNBT nbt = super.getUpdateTag();
		for(int i = 0; i < linked.size(); i++){
			nbt.putLong("link" + i, linked.get(i).toLong());
		}
		return nbt;
	}

	@Override
	public void read(CompoundNBT nbt){
		super.read(nbt);
		for(int i = 0; i < 3; i++){
			if(nbt.contains("link" + i)){
				linked.add(BlockPos.fromLong(nbt.getLong("link" + i)));
			}
		}
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		for(int i = 0; i < linked.size(); i++){
			nbt.putLong("link" + i, linked.get(i).toLong());
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
