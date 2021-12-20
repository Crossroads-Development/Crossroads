package com.Da_Technomancer.crossroads.blocks.witchcraft;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.templates.TEBlock;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.witchcraft.AutoInjectorTileEntity;
import com.Da_Technomancer.essentials.ESConfig;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import com.Da_Technomancer.essentials.blocks.redstone.IReadable;
import com.Da_Technomancer.essentials.tileentities.ITickableTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
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
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;

public class AutoInjector extends TEBlock implements IReadable{

	private static final VoxelShape[] SHAPES = new VoxelShape[6];

	static{
		SHAPES[0] = Shapes.or(box(0, 7, 0, 16, 16, 16), box(7.5, 0, 7.5, 8.5, 7, 8.5));
		SHAPES[1] = Shapes.or(box(0, 0, 0, 16, 9, 16), box(7.5, 9, 7.5, 8.5, 16, 8.5));
		SHAPES[2] = Shapes.or(box(0, 0, 7, 16, 16, 16), box(7.5, 7.5, 0, 8.5, 8.5, 7));
		SHAPES[3] = Shapes.or(box(0, 0, 0, 16, 16, 9), box(7.5, 7.5, 9, 8.5, 8.5, 16));
		SHAPES[4] = Shapes.or(box(7, 0, 0, 16, 16, 16), box(0, 7.5, 7.5, 7, 8.5, 8.5));
		SHAPES[5] = Shapes.or(box(0, 0, 0, 9, 16, 16), box(9, 7.5, 7.5, 16, 8.5, 8.5));
	}

	public AutoInjector(){
		super(CRBlocks.getMetalProperty());
		String name = "auto_injector";
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new AutoInjectorTileEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> type){
		return ITickableTileEntity.createTicker(type, AutoInjectorTileEntity.TYPE);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
		builder.add(ESProperties.FACING);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context){
		return SHAPES[state.getValue(ESProperties.FACING).get3DDataValue()];
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context){
		return defaultBlockState().setValue(ESProperties.FACING, context.getNearestLookingDirection().getOpposite());
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand hand, BlockHitResult hit){
		BlockEntity te;
		if(ESConfig.isWrench(playerIn.getItemInHand(hand))){
			if(playerIn.isShiftKeyDown()){
				//Sneak clicking- change setting
				if(!worldIn.isClientSide){
					te = worldIn.getBlockEntity(pos);
					if(te instanceof AutoInjectorTileEntity){
						int duration = ((AutoInjectorTileEntity) te).increaseSetting() / 20;//Converted from ticks to seconds
						MiscUtil.chatMessage(playerIn, new TranslatableComponent("tt.crossroads.auto_injector.duration_setting_new", duration));
					}
				}
			}else{
				//Rotate this machine
				worldIn.setBlockAndUpdate(pos, state.cycle(ESProperties.FACING));
			}
		}else if(!worldIn.isClientSide && (te = worldIn.getBlockEntity(pos)) instanceof MenuProvider){
			//Open the UI
			NetworkHooks.openGui((ServerPlayer) playerIn, (MenuProvider) te, pos);
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag advanced){
		tooltip.add(new TranslatableComponent("tt.crossroads.auto_injector.desc", CRConfig.injectionEfficiency.get()));
		tooltip.add(new TranslatableComponent("tt.crossroads.auto_injector.dose"));
		tooltip.add(new TranslatableComponent("tt.crossroads.auto_injector.redstone"));
	}

	@Override
	public float read(Level world, BlockPos pos, BlockState state){
		BlockEntity te = world.getBlockEntity(pos);
		if(te instanceof AutoInjectorTileEntity){
			return ((AutoInjectorTileEntity) te).getDuration();
		}else{
			return 0;
		}
	}
}
