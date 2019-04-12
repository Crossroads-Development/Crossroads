package com.Da_Technomancer.crossroads.render;

import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.AddVisualToClient;
import com.Da_Technomancer.crossroads.API.packets.SafeCallable;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import java.util.function.Function;

public class RenderUtil{

	@SuppressWarnings("unchecked")
	public static final Function<NBTTagCompound, IVisualEffect>[] visualFactories = (Function<NBTTagCompound, IVisualEffect>[]) new Function[2];

	static{
		visualFactories[0] = LooseBeamRenderable::readFromNBT;
		visualFactories[1] = LooseArcRenderable::readFromNBT;
	}

	public static void addBeam(int dimension, double x, double y, double z, double length, float angleX, float angleY, byte width, int color){
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("id", 0);
		nbt.setDouble("x", x);
		nbt.setDouble("y", y);
		nbt.setDouble("z", z);
		nbt.setDouble("length", length);
		nbt.setFloat("angle_x", angleX);
		nbt.setFloat("angle_y", angleY);
		nbt.setByte("width", width);
		nbt.setInteger("color", color);
		ModPackets.network.sendToAllAround(new AddVisualToClient(nbt), new NetworkRegistry.TargetPoint(dimension, x, y, z, 512));
	}

	public static void addArc(int dimension, Vec3d start, Vec3d end, int count, float diffusionRate, int color){
		addArc(dimension, (float) start.x, (float) start.y, (float) start.z, (float) end.x, (float) end.y, (float) end.z, count, diffusionRate, color);
	}

	public static void addArc(int dimension, float xSt, float ySt, float zSt, float xEn, float yEn, float zEn, int count, float diffusionRate, int color){
		addArc(dimension, xSt, ySt, zSt, xEn, yEn, zEn, xSt, ySt, zSt, count, diffusionRate, (byte) 10, color);
	}

	public static void addArc(int dimension, float xSt, float ySt, float zSt, float xEn, float yEn, float zEn, float xStFin, float yStFin, float zStFin, int count, float diffusionRate, byte lifespan, int color){
		World world = DimensionManager.getWorld(dimension);
		world.playSound(null, xSt, ySt, zSt, SoundEvents.BLOCK_REDSTONE_TORCH_BURNOUT, SoundCategory.BLOCKS, 0.1F, 0F);

		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("id", 1);
		nbt.setFloat("x", xSt);
		nbt.setFloat("y", ySt);
		nbt.setFloat("z", zSt);
		nbt.setFloat("x_e", xEn);
		nbt.setFloat("y_e", yEn);
		nbt.setFloat("z_e", zEn);
		nbt.setFloat("x_f", xStFin);
		nbt.setFloat("y_f", yStFin);
		nbt.setFloat("z_f", zStFin);
		nbt.setInteger("count", count);
		nbt.setFloat("diffu", diffusionRate);
		nbt.setInteger("color", color);
		nbt.setByte("lif", lifespan);

		if(world.isRemote){
			SafeCallable.effectsToRender.add(visualFactories[1].apply(nbt));
		}else{
			ModPackets.network.sendToAllAround(new AddVisualToClient(nbt), new NetworkRegistry.TargetPoint(dimension, xSt, ySt, zSt, 512));
		}
	}
}
