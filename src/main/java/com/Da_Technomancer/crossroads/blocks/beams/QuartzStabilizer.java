package com.Da_Technomancer.crossroads.blocks.beams;

import com.Da_Technomancer.crossroads.API.templates.BeamBlock;
import com.Da_Technomancer.crossroads.API.templates.BeamRenderTE;
import com.Da_Technomancer.crossroads.tileentities.beams.QuartzStabilizerTileEntity;
import com.Da_Technomancer.essentials.EssentialsConfig;
import com.Da_Technomancer.essentials.blocks.EssentialsProperties;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class QuartzStabilizer extends BeamBlock{

	private static final VoxelShape[] SHAPE = new VoxelShape[6];

	static{
		//Very crude shape to match the angled model- may be worth refining later
		SHAPE[0] = makeCuboidShape(4, 0, 4, 12, 16, 12);
		SHAPE[1] = makeCuboidShape(4, 0, 4, 12, 16, 12);
		SHAPE[2] = makeCuboidShape(4, 4, 0, 12, 12, 16);
		SHAPE[3] = makeCuboidShape(4, 4, 0, 12, 12, 16);
		SHAPE[4] = makeCuboidShape(0, 4, 0, 16, 12, 12);
		SHAPE[5] = makeCuboidShape(0, 4, 0, 16, 12, 12);
	}

	public QuartzStabilizer(){
		super("quartz_stabilizer");
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new QuartzStabilizerTileEntity();
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context){
		return SHAPE[state.get(EssentialsProperties.FACING).getIndex()];
	}

	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		if(EssentialsConfig.isWrench(playerIn.getHeldItem(hand))){
			if(!worldIn.isRemote){
				TileEntity te = worldIn.getTileEntity(pos);
				if(!playerIn.isSneaking()){
					worldIn.setBlockState(pos, state.cycle(EssentialsProperties.FACING));
					if(te instanceof BeamRenderTE){
						((BeamRenderTE) te).resetBeamer();
					}
				}else if(te instanceof QuartzStabilizerTileEntity){
					playerIn.sendMessage(new TranslationTextComponent("tt.crossroads.quartz_stabilizer.setting", ((QuartzStabilizerTileEntity) te).adjustSetting()));
				}
			}
			return true;
		}

		return false;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced){
		tooltip.add(new TranslationTextComponent("tt.crossroads.quartz_stabilizer.desc"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.quartz_stabilizer.wrench"));
	}
}
