package com.Da_Technomancer.crossroads.blocks.witchcraft;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.ambient.particles.CRParticles;
import com.Da_Technomancer.crossroads.ambient.particles.ColorParticleData;
import com.Da_Technomancer.crossroads.ambient.sounds.CRSounds;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import net.minecraft.block.*;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.Random;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.OffsetType;
import net.minecraft.world.level.block.state.BlockState;

public class Wheezewort extends DoublePlantBlock implements BonemealableBlock{

	private static final VoxelShape SHAPE_BOTTOM_1 = box(4, 0, 4, 12, 12, 12);
	private static final VoxelShape SHAPE_BOTTOM_23 = box(4, 0, 4, 12, 16, 12);
	private static final VoxelShape SHAPE_TOP = box(4, 0, 4, 12, 12, 12);

	private static final int COOLING = 100_000;

	public Wheezewort(){
		super(BlockBehaviour.Properties.of(Material.PLANT).randomTicks().instabreak().sound(SoundType.GRASS));
		String name = "wheezewort";
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
//		CRBlocks.blockAddQue(this); No item form. Seeds are a separate item

		registerDefaultState(defaultBlockState().setValue(CRProperties.AGE_3, 1));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
		super.createBlockStateDefinition(builder);//Adds a property for DOUBLE_BLOCK_HALF
		builder.add(CRProperties.AGE_3);
	}

	@Override
	public boolean isValidBonemealTarget(BlockGetter world, BlockPos pos, BlockState state, boolean p_176473_4_){
		return state.getValue(CRProperties.AGE_3) != 3;
	}

	@Override
	public boolean isBonemealSuccess(Level world, Random p_180670_2_, BlockPos pos, BlockState state){
		return true;
	}

	@Override
	public void performBonemeal(ServerLevel world, Random random, BlockPos pos, BlockState state){
		grow(world, pos, state);
	}

	private void grow(Level world, BlockPos pos, BlockState state){
		//If this is the top block, re-route the call to the bottom block
		if(state.getValue(HALF) == DoubleBlockHalf.UPPER){
			BlockPos downPos = pos.below();
			BlockState lowerState = world.getBlockState(downPos);
			if(lowerState.is(this) && lowerState.getValue(HALF) == DoubleBlockHalf.LOWER){
				grow(world, downPos, lowerState);
			}
			return;
		}

		int age = state.getValue(CRProperties.AGE_3);
		if(age != 3){
			BlockPos upPos = pos.above();
			BlockState upperState = world.getBlockState(upPos);
			if(age == 1 && upperState.canBeReplacedByLeaves(world, upPos) || age == 2 && upperState.is(this) && upperState.getValue(HALF) == DoubleBlockHalf.UPPER){
				world.setBlockAndUpdate(pos, state.setValue(CRProperties.AGE_3, age + 1));
				world.setBlockAndUpdate(upPos, state.setValue(CRProperties.AGE_3, age + 1).setValue(HALF, DoubleBlockHalf.UPPER));
			}
		}
	}

	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		//Add an extra check- we don't add a top half for age 1 (and this should only be called with age 1 in practice)
		if(state.getValue(CRProperties.AGE_3) != 1 && state.getValue(HALF) == DoubleBlockHalf.LOWER){
			world.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER), 3);
		}
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context){
		if(state.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER){
			return SHAPE_TOP;
		}else if(state.getValue(CRProperties.AGE_3) == 1){
			return SHAPE_BOTTOM_1;
		}else{
			return SHAPE_BOTTOM_23;
		}
	}

	@Override
	public OffsetType getOffsetType(){
		return OffsetType.NONE;//Removes the offset added by the super class
	}

	@Override
	public boolean isRandomlyTicking(BlockState state){
		return state.getValue(CRProperties.AGE_3) != 3;
	}

	private void coldSpurt(ServerLevel world, BlockPos pos){
		CRSounds.playSoundServer(world, pos, CRSounds.STEAM_RELEASE, SoundSource.BLOCKS, 0.75F, 0.5F + world.random.nextFloat());
		CRParticles.summonParticlesFromServer(world, new ColorParticleData(CRParticles.COLOR_SOLID, Color.WHITE), 8, pos.getX() + 0.5F, pos.getY() + 0.55F, pos.getZ() + 0.5F, 0.1F, 0, 0.1F, 0, 0.15F, 0, 0.005F, 0.05F, 0.005F, false);
		BlockEntity te = world.getBlockEntity(pos.above());
		LazyOptional<IHeatHandler> heatOpt;
		if(te != null && (heatOpt = te.getCapability(Capabilities.HEAT_CAPABILITY)).isPresent()){
			heatOpt.orElseThrow(NullPointerException::new).addHeat(-COOLING);
			//Almost certainly drops it to absolute zero for anything normal
			//Drops by 500C for a heat reservoir
		}
	}

	@Override
	public void randomTick(BlockState state, ServerLevel world, BlockPos pos, Random rand){
		boolean bottom = state.getValue(HALF) == DoubleBlockHalf.LOWER;
		int age = state.getValue(CRProperties.AGE_3);

		//Only do growth for random ticks on the bottom
		if(bottom && rand.nextInt(age * 20) == 0){
			grow(world, pos, state);
			return;
		}

		//Cold spurts for ticks on either section
		if(age == 2){
			coldSpurt(world, bottom ? pos.above() : pos);
		}
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltips, TooltipFlag context){
		tooltips.add(new TranslatableComponent("tt.crossroads.wheezewort.purpose", COOLING));
		tooltips.add(new TranslatableComponent("tt.crossroads.wheezewort.age"));
		tooltips.add(new TranslatableComponent("tt.crossroads.wheezewort.dispense"));
		tooltips.add(new TranslatableComponent("tt.crossroads.wheezewort.quip").setStyle(MiscUtil.TT_QUIP));
	}
}
