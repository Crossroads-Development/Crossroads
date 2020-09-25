package com.Da_Technomancer.crossroads.API.effects.alchemy;


import com.Da_Technomancer.crossroads.API.alchemy.EnumMatterPhase;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentMap;
import com.Da_Technomancer.crossroads.blocks.BlockSalt;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import java.util.function.Predicate;

public class SaltAlchemyEffect implements IAlchEffect{

	private static final Predicate<Entity> FILTER = (Entity e) -> (e instanceof SlimeEntity || e instanceof CreeperEntity) && EntityPredicates.IS_ALIVE.test(e);

	@Override
	public void doEffect(World world, BlockPos pos, int amount, EnumMatterPhase phase, ReagentMap reags){
		//Affect mobs
		float range = 0.5F;
		for(MobEntity e : world.getEntitiesWithinAABB(MobEntity.class, new AxisAlignedBB(pos.getX() + 0.5F - range, pos.getY() + 0.5F - range, pos.getZ() + 0.5F - range, pos.getX() + 0.5F + range, pos.getY() + 0.5F + range, pos.getZ() + 0.5F + range), FILTER)){
			if(e instanceof SlimeEntity){
				e.remove();
				InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.SLIME_BALL, ((SlimeEntity) e).getSlimeSize() + 1));
			}else{
				e.remove();
				InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Blocks.DEAD_BUSH, 1));
			}
		}

		//Affect blocks
		BlockSalt.salinate(world, pos);
	}

	@Override
	public ITextComponent getName(){
		return new TranslationTextComponent("effect.salt");
	}
}
