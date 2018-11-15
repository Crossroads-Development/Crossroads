package com.Da_Technomancer.crossroads.blocks.beams;

import com.Da_Technomancer.crossroads.API.templates.BeamBlock;
import com.Da_Technomancer.crossroads.API.templates.BeamRenderTE;
import com.Da_Technomancer.crossroads.tileentities.beams.QuartzStabilizerTileEntity;
import com.Da_Technomancer.essentials.EssentialsConfig;
import com.Da_Technomancer.essentials.blocks.EssentialsProperties;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class QuartzStabilizer extends BeamBlock{
	
	public QuartzStabilizer(){
		super("quartz_stabilizer");
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return new QuartzStabilizerTileEntity();
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
		if(EssentialsConfig.isWrench(playerIn.getHeldItem(hand), worldIn.isRemote)){
			if(!worldIn.isRemote){
				TileEntity te = worldIn.getTileEntity(pos);
				if(!playerIn.isSneaking()){
					worldIn.setBlockState(pos, state.cycleProperty(EssentialsProperties.FACING));
					if(te instanceof BeamRenderTE){
						((BeamRenderTE) te).resetBeamer();
					}
				}else if(te instanceof QuartzStabilizerTileEntity){
					playerIn.sendMessage(new TextComponentString("Output is now capped at " + ((QuartzStabilizerTileEntity) te).adjustSetting() + " power/cycle"));
				}
			}
			return true;
		}

		return false;
	}


	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced){
		tooltip.add("Buffers beams and emits them with constant power");
		tooltip.add("Configured via shift-right-clicking with a wrench");
	}
}
