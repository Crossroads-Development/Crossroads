package com.Da_Technomancer.crossroads.blocks.electric;

import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.api.render.CRRenderUtil;
import com.Da_Technomancer.crossroads.api.templates.IInfoTE;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.essentials.api.ILinkTE;
import com.Da_Technomancer.essentials.api.LinkHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TeslaCoilTopTileEntity extends BlockEntity implements IInfoTE, ILinkTE{

	public static final BlockEntityType<TeslaCoilTopTileEntity> TYPE = CRTileEntity.createType(TeslaCoilTopTileEntity::new, CRBlocks.teslaCoilTopAttack, CRBlocks.teslaCoilTopDecorative, CRBlocks.teslaCoilTopDistance, CRBlocks.teslaCoilTopEfficiency, CRBlocks.teslaCoilTopNormal);

	public static final int[] COLOR_CODES = {0xFFECCFFF, 0xFFFCDFFF, 0xFFFFFAFF};
	private static final int[] ATTACK_COLOR_CODES = {0xFFFFCCCC, 0xFFFFFFCC, 0xFFFFFAFA};
	private static final Color LINK_COLOR = new Color(COLOR_CODES[0]);

	private final LinkHelper linkHelper = new LinkHelper(this);

	private TeslaCoilTop.TeslaCoilVariants variant = null;

	public TeslaCoilTopTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
	}

	private TeslaCoilTop.TeslaCoilVariants getVariant(){
		if(variant == null){
			BlockState state = getBlockState();
			if(state.getBlock() instanceof TeslaCoilTop){
				variant = ((TeslaCoilTop) state.getBlock()).variant;
			}else{
				setRemoved();
				return TeslaCoilTop.TeslaCoilVariants.NORMAL;
			}
		}
		return variant;
	}

	@Override
	public void receiveLong(byte identifier, long message, @Nullable ServerPlayer sendingPlayer){
		linkHelper.handleIncomingPacket(identifier, message);
	}

	protected void jolt(TeslaCoilTileEntity coilTE){
		TeslaCoilTop.TeslaCoilVariants variant = getVariant();
		int range = variant.range;
		int joltQty = variant.joltAmt;

		int totalFE = coilTE.getTotalFE();
		if(variant == TeslaCoilTop.TeslaCoilVariants.ATTACK){
			if(level.isClientSide){
				return;
			}

			//ATTACK
			List<LivingEntity> ents = level.getEntitiesOfClass(LivingEntity.class, new AABB(worldPosition.getX() - range, worldPosition.getY() - range, worldPosition.getZ() - range, worldPosition.getX() + range, worldPosition.getY() + range, worldPosition.getZ() + range), EntitySelector.ENTITY_STILL_ALIVE);

			if(!ents.isEmpty() && totalFE >= joltQty){
				LivingEntity ent = ents.get(level.random.nextInt(ents.size()));
				coilTE.setTotalFE(totalFE - joltQty);

				CRRenderUtil.addArc(level, worldPosition.getX() + 0.5F, worldPosition.getY() + 0.75F, worldPosition.getZ() + 0.5F, (float) ent.getX(), (float) ent.getY(), (float) ent.getZ(), 5, 0.2F, ATTACK_COLOR_CODES[(int) (level.getGameTime() % 3)]);
				MiscUtil.attackWithLightning(ent, 0, null);
			}
		}else if(variant == TeslaCoilTop.TeslaCoilVariants.DECORATIVE){
			if(totalFE >= joltQty){
				if(level.isClientSide){
					//Spawn the purely decorative bolts on the client side directly to reduce packet load
					int count = level.random.nextInt(5) + 1;
					Vec3 start = new Vec3(worldPosition.getX() + 0.5F, worldPosition.getY() + 0.75F, worldPosition.getZ() + 0.5F);
					for(int i = 0; i < count; i++){
						float angle = level.random.nextFloat() * 2F * (float) Math.PI;
						float rad = level.random.nextFloat() * 2F + 3F;
						Vec3 end = start.add(new Vec3(rad * Math.cos(angle), level.random.nextFloat() * 2F - 1F, rad * Math.sin(angle)));
						CRRenderUtil.addArc(level, start, end, 6, 0.6F, COLOR_CODES[level.random.nextInt(COLOR_CODES.length)]);
					}
				}else{
					coilTE.setTotalFE(totalFE - joltQty);
				}
			}
		}else if(!level.isClientSide){
			//TRANSFER
			for(BlockPos linkPos : linkHelper.getLinksRelative()){
				if(linkPos != null && totalFE >= joltQty && linkPos.distSqr(Vec3i.ZERO) <= range * range){
					BlockPos actualPos = linkPos.offset(worldPosition.getX(), worldPosition.getY() - 1, worldPosition.getZ());
					if(level.getBlockEntity(actualPos) instanceof TeslaCoilTileEntity tcTe && level.getBlockEntity(actualPos.above()) instanceof TeslaCoilTopTileEntity){
						int targetTotalFE = tcTe.getTotalFE();
						int transferFE = (int) (joltQty * (double) variant.efficiency / 100D);
						if(tcTe.getCapacity() - targetTotalFE >= transferFE){
							tcTe.setTotalFE(targetTotalFE + transferFE);
							coilTE.setTotalFE(totalFE - joltQty);

							CRRenderUtil.addArc(level, worldPosition.getX() + 0.5F, worldPosition.getY() + 0.75F, worldPosition.getZ() + 0.5F, actualPos.getX() + 0.5F, actualPos.getY() + 1.75F, actualPos.getZ() + 0.5F, 5, (100F - variant.efficiency) / 100F, COLOR_CODES[(int) (level.getGameTime() % 3)]);
							break;
						}
					}
				}
			}
		}
	}

	@Override
	public void addInfo(ArrayList<Component> chat, Player player, BlockHitResult hit){
		for(BlockPos link : linkHelper.getLinksAbsolute()){
			chat.add(Component.translatable("tt.crossroads.boilerplate.link", link.getX(), link.getY(), link.getZ()));
		}
	}

	@Override
	public CompoundTag getUpdateTag(){
		CompoundTag nbt = super.getUpdateTag();
		linkHelper.writeNBT(nbt);
		return nbt;
	}

	@Override
	public void load(CompoundTag nbt){
		super.load(nbt);
		linkHelper.readNBT(nbt);
	}

	@Override
	public void saveAdditional(CompoundTag nbt){
		super.saveAdditional(nbt);
		linkHelper.writeNBT(nbt);
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
	public Set<BlockPos> getLinks(){
		return linkHelper.getLinksRelative();
	}

	@Override
	public boolean createLinkSource(ILinkTE endpoint, @Nullable Player player){
		return linkHelper.addLink(endpoint, player);
	}

	@Override
	public void removeLinkSource(BlockPos end){
		linkHelper.removeLink(end);
	}

	@Override
	public int getRange(){
		return getVariant().range;
	}

	@Override
	public Color getColor(){
		return LINK_COLOR;
	}
}
