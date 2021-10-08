package com.Da_Technomancer.crossroads.blocks.witchcraft;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CactusBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class PetrolCactus extends CactusBlock{

	private static final Material FLAMMABLE_CACTUS = new Material(MaterialColor.PLANT, false, true, true, false, true, false, PushReaction.DESTROY);

	public PetrolCactus(){
		super(Properties.of(FLAMMABLE_CACTUS).randomTicks().strength(0.4F).sound(SoundType.WOOL));
		String name = "petrol_cactus";
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this, new BlockItem(this, CRBlocks.itemBlockProp){
			@Override
			public int getBurnTime(ItemStack itemStack){
				return 3200;//Makes this a furnace fuel
			}
		});
	}

	@Override
	public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, net.minecraftforge.common.IPlantable plantable){
		return plantable == this && (state.is(this) || state.is(Blocks.SAND) || state.is(Blocks.RED_SAND));
	}

	@Override
	public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face){
		return 100;
	}

	@Override
	public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face){
		return 15;
	}

	@Override
	public void catchFire(BlockState state, Level world, BlockPos pos, @Nullable Direction face, @Nullable LivingEntity igniter){
		detonate(world, pos);
	}

	@Override
	public void wasExploded(Level world, BlockPos pos, Explosion explosion){
		detonate(world, pos);
	}

	@Override
	public void onProjectileHit(Level world, BlockState state, BlockHitResult trace, Projectile projectile){
		if(!world.isClientSide){
			if(projectile.isOnFire()){
				BlockPos blockpos = trace.getBlockPos();
				detonate(world, blockpos);
				world.removeBlock(blockpos, false);
			}
		}
	}

	@Override
	public boolean canDropFromExplosion(BlockState state, BlockGetter world, BlockPos pos, Explosion explosion){
		return false;
	}

	private void detonate(Level world, BlockPos pos){
		if(!world.isClientSide){
			world.playSound(null, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
			world.explode(null, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, 4, Explosion.BlockInteraction.BREAK);
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag advanced){
		tooltip.add(new TranslatableComponent("tt.crossroads.petrol_cactus.desc"));
		tooltip.add(new TranslatableComponent("tt.crossroads.petrol_cactus.quip").setStyle(MiscUtil.TT_QUIP));
	}
}
