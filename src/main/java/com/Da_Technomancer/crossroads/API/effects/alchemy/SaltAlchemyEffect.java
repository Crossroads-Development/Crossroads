package com.Da_Technomancer.crossroads.API.effects.alchemy;


import com.Da_Technomancer.crossroads.API.alchemy.EnumMatterPhase;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentMap;
import com.Da_Technomancer.crossroads.blocks.BlockSalt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;

import java.util.function.Predicate;

public class SaltAlchemyEffect implements IAlchEffect{

	private static final Predicate<Entity> FILTER = (Entity e) -> (e instanceof Slime || e instanceof Creeper) && EntitySelector.ENTITY_STILL_ALIVE.test(e);

	@Override
	public void doEffect(Level world, BlockPos pos, int amount, EnumMatterPhase phase, ReagentMap reags){
		//Affect mobs
		float range = 0.5F;
		for(Mob e : world.getEntitiesOfClass(Mob.class, new AABB(pos.getX() + 0.5F - range, pos.getY() + 0.5F - range, pos.getZ() + 0.5F - range, pos.getX() + 0.5F + range, pos.getY() + 0.5F + range, pos.getZ() + 0.5F + range), FILTER)){
			if(e instanceof Slime){
				e.remove();
				Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.SLIME_BALL, ((Slime) e).getSize() + 1));
			}else{
				e.remove();
				Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Blocks.DEAD_BUSH, 1));
			}
		}

		//Affect blocks
		BlockSalt.salinate(world, pos);
	}

	@Override
	public Component getName(){
		return new TranslatableComponent("effect.salt");
	}
}
