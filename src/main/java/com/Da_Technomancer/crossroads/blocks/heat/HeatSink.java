package com.Da_Technomancer.crossroads.blocks.heat;

import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.heat.HeatSinkTileEntity;
import com.Da_Technomancer.essentials.EssentialsConfig;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class HeatSink extends ContainerBlock{

	public HeatSink(){
		super(Block.Properties.create(Material.IRON).hardnessAndResistance(3).sound(SoundType.METAL));
		String name = "heat_sink";
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new HeatSinkTileEntity();
	}

	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		if(EssentialsConfig.isWrench(playerIn.getHeldItem(hand))){
			if(!worldIn.isRemote){
				TileEntity te = worldIn.getTileEntity(pos);
				if(te instanceof HeatSinkTileEntity){
					int mode = ((HeatSinkTileEntity) te).cycleMode();
					playerIn.sendMessage(new TranslationTextComponent("tt.crossroads.heat_sink.loss", HeatSinkTileEntity.MODES[mode]));
				}
			}
			return true;
		}

		return false;
	}


	@Override
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.MODEL;
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced){
		tooltip.add(new TranslationTextComponent("tt.crossroads.heat_sink.desc"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.heat_sink.rate", HeatSinkTileEntity.MODES[0], HeatSinkTileEntity.MODES[1], HeatSinkTileEntity.MODES[2], HeatSinkTileEntity.MODES[3], HeatSinkTileEntity.MODES[4]));
	}
}
