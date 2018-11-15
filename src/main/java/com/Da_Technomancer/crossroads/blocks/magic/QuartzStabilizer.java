package com.Da_Technomancer.crossroads.blocks.magic;

import com.Da_Technomancer.crossroads.API.templates.BeamBlock;
import com.Da_Technomancer.crossroads.API.templates.BeamRenderTE;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.tileentities.magic.QuartzStabilizerTileEntity;
import com.Da_Technomancer.essentials.EssentialsConfig;
import com.Da_Technomancer.essentials.blocks.EssentialsProperties;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class QuartzStabilizer extends BeamBlock{
	
	private boolean large;
	
	public QuartzStabilizer(boolean large){
		super((large ? "large" : "small") + "_quartz_stabilizer");
		this.large = large;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return new QuartzStabilizerTileEntity(large);
	}
}
