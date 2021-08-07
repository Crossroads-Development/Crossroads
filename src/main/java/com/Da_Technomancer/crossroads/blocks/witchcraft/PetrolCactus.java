package com.Da_Technomancer.crossroads.blocks.witchcraft;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CactusBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.material.PushReaction;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

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
	public boolean canSustainPlant(BlockState state, IBlockReader world, BlockPos pos, Direction facing, net.minecraftforge.common.IPlantable plantable){
		return plantable == this && (state.is(this) || state.is(Blocks.SAND) || state.is(Blocks.RED_SAND));
	}

	@Override
	public int getFlammability(BlockState state, IBlockReader world, BlockPos pos, Direction face){
		return 100;
	}

	@Override
	public int getFireSpreadSpeed(BlockState state, IBlockReader world, BlockPos pos, Direction face){
		return 15;
	}

	@Override
	public void catchFire(BlockState state, World world, BlockPos pos, @Nullable Direction face, @Nullable LivingEntity igniter){
		detonate(world, pos);
	}

	@Override
	public void wasExploded(World world, BlockPos pos, Explosion explosion){
		detonate(world, pos);
	}

	@Override
	public void onProjectileHit(World world, BlockState state, BlockRayTraceResult trace, ProjectileEntity projectile){
		if(!world.isClientSide){
			if(projectile.isOnFire()){
				BlockPos blockpos = trace.getBlockPos();
				detonate(world, blockpos);
				world.removeBlock(blockpos, false);
			}
		}
	}

	@Override
	public boolean canDropFromExplosion(BlockState state, IBlockReader world, BlockPos pos, Explosion explosion){
		return false;
	}

	private void detonate(World world, BlockPos pos){
		if(!world.isClientSide){
			world.playSound(null, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, SoundEvents.TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
			world.explode(null, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, 4, Explosion.Mode.BREAK);
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced){
		tooltip.add(new TranslationTextComponent("tt.crossroads.petrol_cactus.desc"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.petrol_cactus.quip").setStyle(MiscUtil.TT_QUIP));
	}
}
