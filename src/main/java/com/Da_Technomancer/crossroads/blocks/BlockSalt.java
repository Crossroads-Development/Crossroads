package com.Da_Technomancer.crossroads.blocks;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class BlockSalt extends FallingBlock{

	private static final DamageSource SALT_DAMAGE = new DamageSource("salt");
	private static final HashMap<Block, Block> coralMap = new HashMap<>(20);//The field to get the dead version of a coral from the live block is private, and having a big map is better than reflection
	
	static {
		coralMap.put(Blocks.TUBE_CORAL_BLOCK, Blocks.DEAD_TUBE_CORAL_BLOCK);
		coralMap.put(Blocks.BRAIN_CORAL_BLOCK, Blocks.DEAD_BRAIN_CORAL_BLOCK);
		coralMap.put(Blocks.BUBBLE_CORAL_BLOCK, Blocks.DEAD_BUBBLE_CORAL_BLOCK);
		coralMap.put(Blocks.FIRE_CORAL_BLOCK, Blocks.DEAD_FIRE_CORAL_BLOCK);
		coralMap.put(Blocks.HORN_CORAL_BLOCK, Blocks.DEAD_HORN_CORAL_BLOCK);
		coralMap.put(Blocks.TUBE_CORAL, Blocks.DEAD_TUBE_CORAL);
		coralMap.put(Blocks.BRAIN_CORAL, Blocks.DEAD_BRAIN_CORAL);
		coralMap.put(Blocks.BUBBLE_CORAL, Blocks.DEAD_BUBBLE_CORAL);
		coralMap.put(Blocks.FIRE_CORAL, Blocks.DEAD_FIRE_CORAL);
		coralMap.put(Blocks.HORN_CORAL, Blocks.DEAD_HORN_CORAL);
		coralMap.put(Blocks.TUBE_CORAL_FAN, Blocks.DEAD_TUBE_CORAL_FAN);
		coralMap.put(Blocks.BRAIN_CORAL_FAN, Blocks.DEAD_BRAIN_CORAL_FAN);
		coralMap.put(Blocks.BUBBLE_CORAL_FAN, Blocks.DEAD_BUBBLE_CORAL_FAN);
		coralMap.put(Blocks.FIRE_CORAL_FAN, Blocks.DEAD_FIRE_CORAL_FAN);
		coralMap.put(Blocks.HORN_CORAL_FAN, Blocks.DEAD_HORN_CORAL_FAN);
		coralMap.put(Blocks.TUBE_CORAL_WALL_FAN, Blocks.DEAD_TUBE_CORAL_WALL_FAN);
		coralMap.put(Blocks.BRAIN_CORAL_WALL_FAN, Blocks.DEAD_BRAIN_CORAL_WALL_FAN);
		coralMap.put(Blocks.BUBBLE_CORAL_WALL_FAN, Blocks.DEAD_BUBBLE_CORAL_WALL_FAN);
		coralMap.put(Blocks.FIRE_CORAL_WALL_FAN, Blocks.DEAD_FIRE_CORAL_WALL_FAN);
		coralMap.put(Blocks.HORN_CORAL_WALL_FAN, Blocks.DEAD_HORN_CORAL_WALL_FAN);
	}
	
	protected BlockSalt(){
		super(Properties.create(Material.SAND).harvestTool(ToolType.SHOVEL).harvestLevel(0).hardnessAndResistance(.5F).sound(SoundType.SAND).tickRandomly());
		String name = "block_salt";
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this);
	}

	@Override
	public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn){
		if(!worldIn.isRemote && (entityIn instanceof SlimeEntity || entityIn instanceof CreeperEntity)){
			entityIn.attackEntityFrom(SALT_DAMAGE, 20);
		}

		super.onEntityWalk(worldIn, pos, entityIn);
	}
	
	public static void salinate(World worldIn, BlockPos pos){
		BlockState killState = worldIn.getBlockState(pos);

		if(killState.getBlock() == Blocks.DIRT){
			//Make dirt infertile
			worldIn.setBlockState(pos, Blocks.COARSE_DIRT.getDefaultState());
		}else if(killState.getBlock() instanceof SpreadableSnowyDirtBlock){
			//Kill grass and mycelium (podzol is already "dead")
			worldIn.setBlockState(pos, Blocks.DIRT.getDefaultState());
		}else if(killState.getBlock() instanceof BushBlock){
			//Kill plant life
			worldIn.setBlockState(pos, Blocks.DEAD_BUSH.getDefaultState());
		}else if(killState.getBlock() == Blocks.FARMLAND){
			//Trample farmland
			worldIn.setBlockState(pos, Blocks.DIRT.getDefaultState());
		}else if(coralMap.containsKey(killState.getBlock())){
			//Kill coral
			worldIn.setBlockState(pos, coralMap.get(killState.getBlock()).getDefaultState());
		}
	}

	@Override
	public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand){
		if(worldIn.isRemote){
			return;
		}
		super.tick(state, worldIn, pos, rand);
		
		for(int i = 0; i < 10; ++i){
			BlockPos killPos = pos.add(rand.nextInt(5) - 2, rand.nextInt(3) - 1, rand.nextInt(5) - 2);
			salinate(worldIn, killPos);
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public int getDustColor(BlockState state, IBlockReader world, BlockPos pos){
		return Color.WHITE.getRGB();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced){
		tooltip.add(new TranslationTextComponent("tt.crossroads.salt_block"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.salt_block.quip").func_230530_a_(MiscUtil.TT_QUIP));
	}
}
