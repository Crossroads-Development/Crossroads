package com.Da_Technomancer.crossroads.effects.alchemy_effects;

import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.api.alchemy.EnumMatterPhase;
import com.Da_Technomancer.crossroads.api.alchemy.IAlchEffect;
import com.Da_Technomancer.crossroads.api.alchemy.ReagentMap;
import com.Da_Technomancer.crossroads.api.render.CRRenderUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.awt.*;
import java.util.List;

public class VoltusEffect implements IAlchEffect{

	private static final Color[] BOLT_COLORS = new Color[] {new Color(255, 255, 0, 220), new Color(255, 228, 34, 220), new Color(255, 194, 62, 220)};

	@Override
	public void doEffect(Level world, BlockPos pos, int amount, EnumMatterPhase phase, ReagentMap reags) {
		//The odds of spawning a bolt in flame form is decreased due to the much larger number of total calls to this method there will be
		if(Math.random() > (phase == EnumMatterPhase.FLAME ? 0.92D : phase == EnumMatterPhase.SOLID ? 0 : 0.8D)){
			List<LivingEntity> ents = world.getEntitiesOfClass(LivingEntity.class, new AABB(pos.getX() - 5, pos.getY() - 5, pos.getZ() - 5, pos.getX() + 5, pos.getY() + 5, pos.getZ() + 5), EntitySelector.ENTITY_STILL_ALIVE);
			for(LivingEntity ent : ents){
				MiscUtil.attackWithLightning(ent, 10, null);
				CRRenderUtil.addArc(world, (float) pos.getX() + 0.5F, (float) pos.getY() + 0.5F, (float) pos.getZ() + 0.5F, (float) ent.getX(), (float) ent.getY(), (float) ent.getZ(), 1, 0F, BOLT_COLORS[(int) (world.getGameTime() % 3)].getRGB());
			}
		}
	}

	@Override
	public Component getName(){
		return Component.translatable("effect.electric");
	}
}
