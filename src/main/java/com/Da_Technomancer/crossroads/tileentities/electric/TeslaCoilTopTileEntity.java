package com.Da_Technomancer.crossroads.tileentities.electric;

import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.electric.TeslaCoilTop;
import com.Da_Technomancer.crossroads.render.CRRenderUtil;
import com.Da_Technomancer.essentials.tileentities.ILinkTE;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@ObjectHolder(Crossroads.MODID)
public class TeslaCoilTopTileEntity extends TileEntity implements IInfoTE, ILinkTE{

	@ObjectHolder("tesla_coil_top")
	public static TileEntityType<TeslaCoilTopTileEntity> type = null;

	public static final int[] COLOR_CODES = {0xFFECCFFF, 0xFFFCDFFF, 0xFFFFFAFF};
	private static final int[] ATTACK_COLOR_CODES = {0xFFFFCCCC, 0xFFFFFFCC, 0xFFFFFAFA};

	//Relative to this TileEntity's position
	private HashSet<BlockPos> linked = new HashSet<>(3);

	private TeslaCoilTop.TeslaCoilVariants variant = null;

	public TeslaCoilTopTileEntity(){
		super(type);
	}

	private TeslaCoilTop.TeslaCoilVariants getVariant(){
		if(variant == null){
			BlockState state = world.getBlockState(pos);
			if(state.getBlock() instanceof TeslaCoilTop){
				variant = ((TeslaCoilTop) state.getBlock()).variant;
			}else{
				remove();
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

				CRRenderUtil.addArc(world, pos.getX() + 0.5F, pos.getY() + 0.75F, pos.getZ() + 0.5F, (float) ent.getPosX(), (float) ent.getPosY(), (float) ent.getPosZ(), 5, 0.2F, ATTACK_COLOR_CODES[(int) (world.getGameTime() % 3)]);
				ent.onStruckByLightning(new LightningBoltEntity(world, ent.getPosX(), ent.getPosY(), ent.getPosZ(), true));
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
						CRRenderUtil.addArc(world, start, end, 6, 0.6F, COLOR_CODES[world.rand.nextInt(COLOR_CODES.length)]);
					}
				}else{
					coilTE.setStored(coilTE.getStored() - TeslaCoilTop.TeslaCoilVariants.DECORATIVE.joltAmt);
				}
			}
		}else if(!world.isRemote){
			//TRANSFER
			for(BlockPos linkPos : linked){
				if(linkPos != null && coilTE.getStored() >= joltQty && linkPos.distanceSq(0, 0, 0, false) <= range * range){
					BlockPos actualPos = linkPos.add(pos.getX(), pos.getY() - 1, pos.getZ());
					TileEntity te = world.getTileEntity(actualPos);
					if(te instanceof TeslaCoilTileEntity && world.getTileEntity(actualPos.up()) instanceof TeslaCoilTopTileEntity){
						TeslaCoilTileEntity tcTe = (TeslaCoilTileEntity) te;
						if(tcTe.getCapacity() - tcTe.getStored() > joltQty * (double) variant.efficiency / 100D){
							tcTe.setStored(tcTe.getStored() + (int) (joltQty * (double) variant.efficiency / 100D));
							tcTe.markDirty();
							coilTE.setStored(coilTE.getStored() - joltQty);
							markDirty();

							CRRenderUtil.addArc(world, pos.getX() + 0.5F, pos.getY() + 0.75F, pos.getZ() + 0.5F, actualPos.getX() + 0.5F, actualPos.getY() + 1.75F, actualPos.getZ() + 0.5F, 5, (100F - variant.efficiency) / 100F, COLOR_CODES[(int) (world.getGameTime() % 3)]);
							break;
						}
					}
				}
			}
		}
	}

	@Override
	public void addInfo(ArrayList<ITextComponent> chat, PlayerEntity player, BlockRayTraceResult hit){
		for(BlockPos link : linked){
			chat.add(new TranslationTextComponent("tt.crossroads.boilerplate.link", pos.getX() + link.getX(), pos.getY() + link.getY(), pos.getZ() + link.getZ()));
		}
	}

	@Override
	public CompoundNBT getUpdateTag(){
		CompoundNBT nbt = super.getUpdateTag();
		int count = 0;
		for(BlockPos relPos : linked){
			nbt.putLong("link" + count++, relPos.toLong());
		}
		return nbt;
	}

	@Override
	public void read(CompoundNBT nbt){
		super.read(nbt);
		int count = 0;
		while(nbt.contains("link" + count)){
			linked.add(BlockPos.fromLong(nbt.getLong("link" + count)));
			count++;
		}
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		int count = 0;
		for(BlockPos relPos : linked){
			nbt.putLong("link" + count++, relPos.toLong());
		}
		return nbt;
	}

	@Override
	public TileEntity getTE(){
		return this;
	}

	@Override
	public boolean canBeginLinking(){
		return getVariant() != TeslaCoilTop.TeslaCoilVariants.ATTACK && getVariant() != TeslaCoilTop.TeslaCoilVariants.DECORATIVE;
	}

	@Override
	public boolean canLink(ILinkTE otherTE){
		return otherTE instanceof TeslaCoilTopTileEntity && getVariant() != TeslaCoilTop.TeslaCoilVariants.ATTACK;
	}

	@Override
	public HashSet<BlockPos> getLinks(){
		return linked;
	}

	@Override
	public int getRange(){
		return getVariant().range;
	}
}
