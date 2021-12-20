package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.tileentities.alchemy.ChargingStandTileEntity;
import com.Da_Technomancer.essentials.tileentities.ITickableTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;

public class ChargingStand extends GlasswareHolder{

	private static final VoxelShape SHAPE = Shapes.or(box(0, 0, 0, 16, 2, 16), box(0, 14, 0, 16, 16, 16), box(5, 2, 0, 11, 14, 1), box(5, 2, 15, 11, 14,16), box(0, 2, 5, 1, 4, 11), box(15, 2, 5, 16, 14, 11), box(5, 2, 5, 11, 14, 11));

	public ChargingStand(){
		super("charging_stand");
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new ChargingStandTileEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> type){
		return ITickableTileEntity.createTicker(type, ChargingStandTileEntity.TYPE);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context){
		return SHAPE;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
		builder.add(CRProperties.CRYSTAL, CRProperties.CONTAINER_TYPE);//No redstone_bool property, unlike superclass
	}

	@Override
	public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving){
		//No-op. Prevent redstone interaction in the superclass
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag advanced){
		tooltip.add(new TranslatableComponent("tt.crossroads.charging_stand.desc"));
		tooltip.add(new TranslatableComponent("tt.crossroads.charging_stand.power", ChargingStandTileEntity.DRAIN));
	}
}
