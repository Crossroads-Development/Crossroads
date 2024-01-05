package com.Da_Technomancer.crossroads.blocks;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.api.crafting.CraftingUtil;
import com.Da_Technomancer.crossroads.entity.CRMobDamage;
import com.Da_Technomancer.essentials.blocks.FertileSoil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.HashMap;
import java.util.List;

public class BlockSalt extends FallingBlock{

	private static final HashMap<Block, Block> coralMap = new HashMap<>(20);//The field to get the dead version of a coral from the live block is private, and having a big map is better than reflection
	private static final TagKey<EntityType<?>> SALT_VULNERABLE = CraftingUtil.getTagKey(ForgeRegistries.Keys.ENTITY_TYPES, new ResourceLocation(Crossroads.MODID, "salt_vulnerable"));

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
		super(Properties.of().mapColor(MapColor.SAND).strength(.5F).sound(SoundType.SAND).randomTicks());//Mine with shovel
		String name = "block_salt";
		CRBlocks.queueForRegister(name, this);
	}

	@Override
	public void stepOn(Level worldIn, BlockPos pos, BlockState state, Entity entityIn){
		if(!worldIn.isClientSide && CraftingUtil.tagContains(SALT_VULNERABLE, entityIn.getType())){
			entityIn.hurt(CRMobDamage.damageSource(CRMobDamage.SALT, worldIn), 20);
		}

		super.stepOn(worldIn, pos, state, entityIn);
	}
	
	public static boolean salinate(Level worldIn, BlockPos pos){
		BlockState killState = worldIn.getBlockState(pos);
		Block killBlock = killState.getBlock();
		BlockState resultState = killState;

		if(CraftingUtil.tagContains(BlockTags.DIRT, killBlock) && killBlock != Blocks.COARSE_DIRT){
			//Kill dirt, grass, etc
			resultState = Blocks.COARSE_DIRT.defaultBlockState();
		}else if(killBlock instanceof BushBlock){
			//Kill plant life
			resultState = Blocks.DEAD_BUSH.defaultBlockState();
		}else if(killBlock == Blocks.FARMLAND){
			//Trample farmland
			resultState = Blocks.COARSE_DIRT.defaultBlockState();
		}else if(killState.is(BlockTags.NYLIUM)){
			//Kill nylium
			resultState = Blocks.NETHERRACK.defaultBlockState();
		}else if(coralMap.containsKey(killBlock)){
			//Kill coral
			resultState = coralMap.get(killBlock).defaultBlockState();
		}else if(killBlock instanceof FertileSoil){
			//Ruin fertile soil
			resultState = Blocks.COARSE_DIRT.defaultBlockState();
		}else if(CraftingUtil.tagContains(BlockTags.LEAVES, killBlock)){
			//Destroy leaves without dropping look
			resultState = Blocks.AIR.defaultBlockState();
		}
		
		if(killState != resultState){
			worldIn.setBlockAndUpdate(pos, resultState);
			return true;
		}
		return false;
	}

	@Override
	public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource rand){
		if(worldIn.isClientSide){
			return;
		}
		super.tick(state, worldIn, pos, rand);
		
		for(int i = 0; i < 10; ++i){
			BlockPos killPos = pos.offset(rand.nextInt(5) - 2, rand.nextInt(3) - 1, rand.nextInt(5) - 2);
			salinate(worldIn, killPos);
		}
	}

	@Override
	public int getDustColor(BlockState state, BlockGetter world, BlockPos pos){
		return Color.WHITE.getRGB();
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag advanced){
		tooltip.add(Component.translatable("tt.crossroads.salt_block"));
		tooltip.add(Component.translatable("tt.crossroads.salt_block.quip").setStyle(MiscUtil.TT_QUIP));
	}
}
