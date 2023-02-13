package com.Da_Technomancer.crossroads.blocks.witchcraft;

import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.api.CircuitUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.essentials.api.ITickableTileEntity;
import com.Da_Technomancer.essentials.api.TEBlock;
import com.Da_Technomancer.essentials.api.redstone.IReadable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;

public class FormulationVat extends TEBlock implements IReadable{

	public FormulationVat(){
		super(CRBlocks.getMetalProperty());
		String name = "formulation_vat";
		CRBlocks.toRegister.put(name, this);
		CRBlocks.blockAddQue(name, this);
		registerDefaultState(defaultBlockState().setValue(CRProperties.POWER_LEVEL_7, 0));
	}

	@Override
	public VoxelShape getVisualShape(BlockState p_60479_, BlockGetter p_60480_, BlockPos p_60481_, CollisionContext p_60482_){
		return box(0, 0, 0, 16, 30, 16);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
		builder.add(CRProperties.POWER_LEVEL_7);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new FormulationVatTileEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> type){
		return ITickableTileEntity.createTicker(type, FormulationVatTileEntity.TYPE);
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand hand, BlockHitResult hit){
		BlockEntity te;
		if(!worldIn.isClientSide && (te = worldIn.getBlockEntity(pos)) instanceof MenuProvider){
			NetworkHooks.openScreen((ServerPlayer) playerIn, (MenuProvider) te, pos);
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag advanced){
		tooltip.add(Component.translatable("tt.crossroads.formulation_vat.desc"));
		for(int i = 0; i < FormulationVatTileEntity.TEMP_TIERS.length; i++){
			tooltip.add(Component.translatable("tt.crossroads.formulation_vat.tier", FormulationVatTileEntity.TEMP_TIERS[i], FormulationVatTileEntity.SPEED_MULT[i], FormulationVatTileEntity.HEAT_DRAIN[i]));
		}
	}

	@Override
	public float read(Level world, BlockPos pos, BlockState state){
		BlockEntity te = world.getBlockEntity(pos);
		if(te instanceof Container cte){
			return CircuitUtil.getRedstoneFromSlots(cte, 0);
		}else{
			return 0;
		}
	}
}
