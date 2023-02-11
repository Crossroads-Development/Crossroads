package com.Da_Technomancer.crossroads.api.packets;

import com.Da_Technomancer.crossroads.ambient.particles.CRParticles;
import com.Da_Technomancer.essentials.api.packets.ClientPacket;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;

public class CreateParticlesOnClient extends ClientPacket{

	public ParticleOptions particle;
	public double x;
	public double y;
	public double z;
	public float xDeviation;
	public float yDeviation;
	public float zDeviation;
	public float xVel;
	public float yVel;
	public float zVel;
	public float xVelDeviation;
	public float yVelDeviation;
	public float zVelDeviation;
	public int count;
	public boolean gaussian;

	private static final Field[] FIELDS = fetchFields(CreateParticlesOnClient.class, "particle", "x", "y", "z", "xDeviation", "yDeviation", "zDeviation", "xVel", "yVel", "zVel", "xVelDeviation", "yVelDeviation", "zVelDeviation", "count", "gaussian");

	@SuppressWarnings("unused")
	public CreateParticlesOnClient(){

	}

	public CreateParticlesOnClient(ParticleOptions particle, double x, double y, double z, float xDeviation, float yDeviation, float zDeviation, float xVel, float yVel, float zVel, float xVelDeviation, float yVelDeviation, float zVelDeviation, int count, boolean gaussian){
		this.particle = particle;
		this.x = x;
		this.y = y;
		this.z = z;
		this.xDeviation = xDeviation;
		this.yDeviation = yDeviation;
		this.zDeviation = zDeviation;
		this.xVel = xVel;
		this.yVel = yVel;
		this.zVel = zVel;
		this.xVelDeviation = xVelDeviation;
		this.yVelDeviation = yVelDeviation;
		this.zVelDeviation = zVelDeviation;
		this.count = count;
		this.gaussian = gaussian;
	}

	@Nonnull
	@Override
	protected Field[] getFields(){
		return FIELDS;
	}

	@Override
	protected void run(){
		Level world = SafeCallable.getClientWorld();
		if(world != null){
			CRParticles.summonParticlesFromClient(world, particle, count, x, y, z, xDeviation, yDeviation, zDeviation, xVel, yVel, zVel, xVelDeviation, yVelDeviation, zVelDeviation, gaussian);
		}
	}
}
