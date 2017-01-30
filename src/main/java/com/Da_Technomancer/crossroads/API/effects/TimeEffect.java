package com.Da_Technomancer.crossroads.API.effects;

import java.util.Random;

import com.Da_Technomancer.crossroads.API.Capabilities;

import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public class TimeEffect implements IEffect{

	private final Random rand = new Random();

	@Override
	public void doEffect(World worldIn, BlockPos pos, double mult){
		if(worldIn.getTileEntity(pos) instanceof ITickable){
			for(EnumFacing dir : EnumFacing.values()){
				if(worldIn.getTileEntity(pos).hasCapability(Capabilities.MAGIC_HANDLER_CAPABILITY, dir)){
					return;
				}
			}
			

			for(int i = rand.nextInt((int) mult); i < mult; i++){
				((ITickable) worldIn.getTileEntity(pos)).update();
				if(!(worldIn.getTileEntity(pos) instanceof ITickable)){
					break;
				}
			}
		}

		for(int i = rand.nextInt((int) mult); i < mult; i++){
			worldIn.getBlockState(pos).getBlock().randomTick(worldIn, pos, worldIn.getBlockState(pos), rand);
		}
	}

	public static class VoidTimeEffect implements IEffect{

		@Override
		public void doEffect(World worldIn, BlockPos pos, double mult){
			//TODO causes a chunk reset
			//I don't mean I deleted the code because it was causing a chunk reset,
			//I mean I want this to CAUSE a chunk reset. 
			//But I don't know how.
			
			worldIn.setBlockState(pos, Blocks.STANDING_SIGN.getDefaultState());
			ITextComponent[] text = ((TileEntitySign) worldIn.getTileEntity(pos)).signText;
			text[0].appendText("NYI");
			text[1].appendText("Be glad of that");
		}
	}
}
