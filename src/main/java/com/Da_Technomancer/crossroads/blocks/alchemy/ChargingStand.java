package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.tileentities.alchemy.ChargingStandTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class ChargingStand extends GlasswareHolder{

	private static final VoxelShape SHAPE = VoxelShapes.or(box(0, 0, 0, 16, 2, 16), box(0, 14, 0, 16, 16, 16), box(5, 2, 0, 11, 14, 1), box(5, 2, 15, 11, 14,16), box(0, 2, 5, 1, 4, 11), box(15, 2, 5, 16, 14, 11), box(5, 2, 5, 11, 14, 11));

	public ChargingStand(){
		super("charging_stand");
	}

	@Override
	public TileEntity newBlockEntity(IBlockReader worldIn){
		return new ChargingStandTileEntity();
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context){
		return SHAPE;
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder){
		builder.add(CRProperties.CRYSTAL, CRProperties.CONTAINER_TYPE);//No redstone_bool property, unlike superclass
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving){
		//No-op. Prevent redstone interaction in the superclass
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced){
		tooltip.add(new TranslationTextComponent("tt.crossroads.charging_stand.desc"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.charging_stand.power", ChargingStandTileEntity.DRAIN));
	}
}
