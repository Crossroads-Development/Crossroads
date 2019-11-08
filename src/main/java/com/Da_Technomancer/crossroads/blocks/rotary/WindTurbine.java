package com.Da_Technomancer.crossroads.blocks.rotary;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.tileentities.rotary.WindTurbineTileEntity;
import com.Da_Technomancer.essentials.EssentialsConfig;
import com.Da_Technomancer.essentials.blocks.redstone.IReadable;
import com.Da_Technomancer.essentials.blocks.redstone.RedstoneUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
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

public class WindTurbine extends ContainerBlock implements IReadable{

	public WindTurbine(){
		super(Properties.create(Material.WOOD).hardnessAndResistance(2));
		String name = "wind_turbine";
		setRegistryName(name);
		CrossroadsBlocks.toRegister.add(this);
		CrossroadsBlocks.blockAddQue(this);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new WindTurbineTileEntity(true);
	}

	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		if(EssentialsConfig.isWrench(playerIn.getHeldItem(hand))){
			if(!worldIn.isRemote){
				TileEntity te = worldIn.getTileEntity(pos);
				if(te instanceof WindTurbineTileEntity){
					((WindTurbineTileEntity) te).rotate();
				}
				worldIn.setBlockState(pos, state.with(CRProperties.HORIZ_FACING, state.get(CRProperties.HORIZ_FACING).rotateY()));
			}
			return true;
		}
		return false;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.MODEL;
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context){
		return getDefaultState().with(CRProperties.HORIZ_FACING, context.getPlacementHorizontalFacing());
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder){
		builder.add(CRProperties.HORIZ_FACING);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced){
		tooltip.add(new TranslationTextComponent("tt.crossroads.wind_turbine.desc"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.wind_turbine.power", 2 * WindTurbineTileEntity.POWER_PER_LEVEL, WindTurbineTileEntity.POWER_PER_LEVEL));
		tooltip.add(new TranslationTextComponent("tt.crossroads.wind_turbine.limits", WindTurbineTileEntity.MAX_SPEED, 2 * WindTurbineTileEntity.POWER_PER_LEVEL));
		tooltip.add(new TranslationTextComponent("tt.crossroads.wind_turbine.env"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.boilerplate.inertia", WindTurbineTileEntity.INERTIA));
	}

	@Override
	public float read(World world, BlockPos pos, BlockState state){
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof WindTurbineTileEntity){
			return ((WindTurbineTileEntity) te).getRedstoneOutput();
		}else{
			return 0;
		}
	}

	@Override
	public boolean hasComparatorInputOverride(BlockState state){
		return true;
	}

	@Override
	public int getComparatorInputOverride(BlockState state, World world, BlockPos pos){
		return RedstoneUtil.clampToVanilla(read(world, pos, state));
	}
}
