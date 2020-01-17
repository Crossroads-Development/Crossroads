package com.Da_Technomancer.crossroads.blocks.electric;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.templates.ModuleTE;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.electric.DynamoTileEntity;
import com.Da_Technomancer.essentials.EssentialsConfig;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
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

public class Dynamo extends ContainerBlock{

	private static final VoxelShape[] SHAPES = new VoxelShape[2];
	static{
		SHAPES[0] = makeCuboidShape(0, 0, 4, 16, 8, 12);
		SHAPES[1] = makeCuboidShape(4, 0, 0, 12, 8, 16);
	}

	public Dynamo(){
		super(Properties.create(Material.IRON).hardnessAndResistance(2).sound(SoundType.METAL));
		String name = "dynamo";
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new DynamoTileEntity();
	}

	@Override
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.MODEL;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context){
		return SHAPES[state.get(CRProperties.HORIZ_FACING).getAxis() == Direction.Axis.X ? 0 : 1];
	}

	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		if(EssentialsConfig.isWrench(playerIn.getHeldItem(hand))){
			if(!worldIn.isRemote){
				TileEntity te = worldIn.getTileEntity(pos);
				if(te instanceof ModuleTE){
					((ModuleTE) te).rotate();
				}
				worldIn.setBlockState(pos, state.cycle(CRProperties.HORIZ_FACING));
			}
			return true;
		}
		return false;
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced){
		tooltip.add(new TranslationTextComponent("tt.crossroads.dynamo.power", CRConfig.electPerJoule.get()));
		tooltip.add(new TranslationTextComponent("tt.crossroads.dynamo.usage", DynamoTileEntity.INERTIA / 2));
		tooltip.add(new TranslationTextComponent("tt.crossroads.boilerplate.inertia", DynamoTileEntity.INERTIA));
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder){
		builder.add(CRProperties.HORIZ_FACING);
	}
}
