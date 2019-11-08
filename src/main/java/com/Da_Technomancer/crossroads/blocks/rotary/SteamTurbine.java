package com.Da_Technomancer.crossroads.blocks.rotary;

import com.Da_Technomancer.crossroads.API.EnergyConverters;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.tileentities.rotary.SteamTurbineTileEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class SteamTurbine extends ContainerBlock{

	private VoxelShape SHAPE = VoxelShapes.or(makeCuboidShape(2, 0, 2, 14, 16, 14), makeCuboidShape(0, 5, 5, 16, 11, 11), makeCuboidShape(5, 5, 0, 11, 11, 16));

	public SteamTurbine(){
		super(Properties.create(Material.IRON).hardnessAndResistance(3).sound(SoundType.METAL));
		String name = "steam_turbine";
		setRegistryName(name);
		CrossroadsBlocks.toRegister.add(this);
		CrossroadsBlocks.blockAddQue(this);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new SteamTurbineTileEntity();
	}

	@Override
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.MODEL;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context){
		return SHAPE;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BlockRenderLayer getRenderLayer(){
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced){
		tooltip.add(new TranslationTextComponent("tt.crossroads.steam_turbine.input", 100 * SteamTurbineTileEntity.LIMIT));
		tooltip.add(new TranslationTextComponent("tt.crossroads.steam_turbine.output", ((double) SteamTurbineTileEntity.LIMIT) * 0.1D * EnergyConverters.degPerSteamBucket() / EnergyConverters.degPerJoule()));
		tooltip.add(new TranslationTextComponent("tt.crossroads.boilerplate.inertia", SteamTurbineTileEntity.INERTIA));
	}
}
