package com.Da_Technomancer.crossroads.API.effects;

import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class ExplodeEffect extends BeamEffect{

	@Override
	public void doBeamEffect(EnumBeamAlignments align, boolean voi, int power, Level worldIn, BlockPos pos, @Nullable Direction dir){
		if(!performTransmute(align, voi, power, worldIn, pos)){
			if(voi){
				worldIn.explode(null, pos.getX(), pos.getY(), pos.getZ(), (int) Math.min(Math.ceil(power / 4D), 16), Explosion.BlockInteraction.BREAK);
			}
		}
	}
}
