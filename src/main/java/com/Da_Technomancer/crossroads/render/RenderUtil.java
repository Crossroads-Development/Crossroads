package com.Da_Technomancer.crossroads.render;

import com.Da_Technomancer.crossroads.API.packets.AddVisualToClient;
import com.Da_Technomancer.crossroads.API.packets.CrossroadsPackets;
import com.Da_Technomancer.crossroads.API.packets.SafeCallable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.function.Function;

public class RenderUtil{

	@SuppressWarnings("unchecked")
	public static final Function<CompoundNBT, IVisualEffect>[] visualFactories = (Function<CompoundNBT, IVisualEffect>[]) new Function[2];

	static{
		visualFactories[0] = LooseBeamRenderable::readFromNBT;
		visualFactories[1] = LooseArcRenderable::readFromNBT;
	}

	public static void addBeam(World world, double x, double y, double z, double length, float angleX, float angleY, byte width, int color){
		CompoundNBT nbt = new CompoundNBT();
		nbt.putInt("id", 0);
		nbt.putDouble("x", x);
		nbt.putDouble("y", y);
		nbt.putDouble("z", z);
		nbt.putDouble("length", length);
		nbt.putFloat("angle_x", angleX);
		nbt.putFloat("angle_y", angleY);
		nbt.putByte("width", width);
		nbt.putInt("color", color);
		CrossroadsPackets.sendPacketAround(world, new BlockPos(x, y, z), new AddVisualToClient(nbt));
	}

	public static void addArc(World world, Vec3d start, Vec3d end, int count, float diffusionRate, int color){
		addArc(world, (float) start.x, (float) start.y, (float) start.z, (float) end.x, (float) end.y, (float) end.z, count, diffusionRate, color);
	}

	public static void addArc(World world, float xSt, float ySt, float zSt, float xEn, float yEn, float zEn, int count, float diffusionRate, int color){
		addArc(world, xSt, ySt, zSt, xEn, yEn, zEn, xSt, ySt, zSt, count, diffusionRate, (byte) 10, color);
	}

	public static void addArc(World world, float xSt, float ySt, float zSt, float xEn, float yEn, float zEn, float xStFin, float yStFin, float zStFin, int count, float diffusionRate, byte lifespan, int color){
		world.playSound(null, xSt, ySt, zSt, SoundEvents.BLOCK_REDSTONE_TORCH_BURNOUT, SoundCategory.BLOCKS, 0.1F, 0F);

		CompoundNBT nbt = new CompoundNBT();
		nbt.putInt("id", 1);
		nbt.putFloat("x", xSt);
		nbt.putFloat("y", ySt);
		nbt.putFloat("z", zSt);
		nbt.putFloat("x_e", xEn);
		nbt.putFloat("y_e", yEn);
		nbt.putFloat("z_e", zEn);
		nbt.putFloat("x_f", xStFin);
		nbt.putFloat("y_f", yStFin);
		nbt.putFloat("z_f", zStFin);
		nbt.putInt("count", count);
		nbt.putFloat("diffu", diffusionRate);
		nbt.putInt("color", color);
		nbt.putByte("lif", lifespan);

		if(world.isRemote){
			SafeCallable.effectsToRender.add(visualFactories[1].apply(nbt));
		}else{
			CrossroadsPackets.sendPacketAround(world, new BlockPos(xSt, ySt, zSt), new AddVisualToClient(nbt));
		}
	}
}
