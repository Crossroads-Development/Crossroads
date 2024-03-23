package com.Da_Technomancer.crossroads.api.technomancy;

import com.Da_Technomancer.crossroads.api.MiscUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Objects;

public class Location{

	public final BlockPos pos;
	public final ResourceLocation dim;
	private ResourceKey<Level> cache;//Used to retrieve the associated world data more quickly

	public Location(BlockPos pos, Level world){
		this.pos = pos.immutable();
		cache = world.dimension();
		this.dim = cache.location();
	}

	public Location(long posSerial, String dimSerial){
		this.pos = BlockPos.of(posSerial);
		this.dim = new ResourceLocation(dimSerial);
	}

	@Nullable
	public Level evalDim(MinecraftServer server){
		try{
			cache = MiscUtil.getWorldKey(dim, cache);
			return MiscUtil.getWorld(cache, server);
		}catch(Exception e){
			return null;
		}
	}

	public boolean isSameLocation(BlockPos testPos, Level testWorld){
		return pos.equals(testPos) && isSameWorld(testWorld);
	}

	public boolean isSameWorld(Level testWorld){
		return dim.equals(testWorld.dimension().location());
	}

	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(o == null || getClass() != o.getClass()){
			return false;
		}
		Location location = (Location) o;
		return dim == location.dim && pos.equals(location.pos);
	}

	@Override
	public int hashCode(){
		return Objects.hash(pos, dim);
	}
}
