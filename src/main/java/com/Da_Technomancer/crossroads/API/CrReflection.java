package com.Da_Technomancer.crossroads.API;

import com.Da_Technomancer.essentials.ReflectionUtil;
import net.minecraft.client.gui.NewChatGui;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.ZombieVillagerEntity;
import net.minecraft.world.Explosion;

import javax.annotation.Nullable;

public enum CrReflection implements ReflectionUtil.IReflectionKey{

	SET_CHAT(NewChatGui.class, "setChatLine", "func_146237_a", "Update the chat log without spamming it"),
	CURE_ZOMBIE(ZombieVillagerEntity.class, "startConverting", "func_191991_a", "Cure zombie villagers with SO2"),
	EXPLOSION_POWER(Explosion.class, "size", "field_77280_f", "Perpetuate explosions with Collapse beams (1)"),
	EXPLOSION_SMOKE(Explosion.class, "causesFire", "field_77286_a", "Perpetuate explosions with Collapse beams (2)"),
	SWING_TIME(LivingEntity.class, "ticksSinceLastSwing", "field_184617_aD", "Mechanical Arm attacking");

	private final Class<?> clazz;
	public final String obf;//Obfuscated name
	public final String mcp;//Human readable MCP name
	private final String purpose;

	CrReflection(@Nullable Class<?> clazz, String obf, String mcp, String purpose){
		this.clazz = clazz;
		this.obf = obf;
		this.mcp = mcp;
		this.purpose = purpose;
	}

	@Nullable
	@Override
	public Class<?> getSourceClass(){
		return clazz;
	}

	@Override
	public String getObfName(){
		return obf;
	}

	@Override
	public String getMcpName(){
		return mcp;
	}

	@Override
	public String getPurpose(){
		return purpose;
	}
}
