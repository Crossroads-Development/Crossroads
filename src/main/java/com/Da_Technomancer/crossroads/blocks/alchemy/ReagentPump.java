package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.api.CRCapabilities;
import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.api.alchemy.EnumContainerType;
import com.Da_Technomancer.crossroads.api.alchemy.EnumTransferMode;
import com.Da_Technomancer.crossroads.api.alchemy.IChemicalHandler;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.essentials.api.ConfigUtil;
import com.Da_Technomancer.essentials.api.ITickableTileEntity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;

public class ReagentPump extends BaseEntityBlock{

	public static final MapCodec<ReagentPump> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(Codec.BOOL.fieldOf("crystal").forGetter(ReagentPump::isCrystal)).apply(instance, ReagentPump::new));

	private static final double SIZE = 6.1D;
	private static final double CORE_SIZE = 4D;

	protected static final VoxelShape[] SHAPES = new VoxelShape[16];

	static{
		final double sizeN = 16D - SIZE;
		//There are 16 (2^4) possible shapes for this block
		VoxelShape core = Shapes.or(box(CORE_SIZE, 0, CORE_SIZE, 16 - CORE_SIZE, 11, 16 - CORE_SIZE), box(SIZE, 11, SIZE, sizeN, 16, sizeN));
		VoxelShape[] pieces = new VoxelShape[4];
		pieces[0] = box(SIZE, SIZE, 0, sizeN, sizeN, SIZE);
		pieces[1] = box(SIZE, SIZE, sizeN, sizeN, sizeN, 16);
		pieces[2] = box(0, SIZE, SIZE, SIZE, sizeN, sizeN);
		pieces[3] = box(sizeN, SIZE, SIZE, 16, sizeN, sizeN);
		for(int i = 0; i < 16; i++){
			VoxelShape comp = core;
			for(int j = 0; j < 4; j++){
				if((i & (1 << j)) != 0){
					comp = Shapes.or(comp, pieces[j]);
				}
			}
			SHAPES[i] = comp;
		}
	}

	private final boolean crystal;

	public ReagentPump(boolean crystal){
		super(CRBlocks.getGlassProperty());
		this.crystal = crystal;
		String name = (crystal ? "crystal_" : "") + "reagent_pump";
		CRBlocks.queueForRegister(name, this);
	}

	private boolean isCrystal(){
		return crystal;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new ReagentPumpTileEntity(pos, state, !crystal);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> type){
		return ITickableTileEntity.createTicker(type, ReagentPumpTileEntity.TYPE);
	}

	@Override
	public ItemInteractionResult useItemOn(ItemStack held, BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand hand, BlockHitResult hit){
		if(ConfigUtil.isWrench(playerIn.getItemInHand(hand))){
			if(!worldIn.isClientSide){
				worldIn.setBlockAndUpdate(pos, state.cycle(CRProperties.ACTIVE));
			}
			return ItemInteractionResult.sidedSuccess(worldIn.isClientSide);
		}
		return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec(){
		return CRBlocks.REAGENT_PUMP_TYPE.value();
	}

	@Override
	public RenderShape getRenderShape(BlockState state){
		return RenderShape.MODEL;
	}

//	@Override
//	@OnlyIn(Dist.CLIENT)
//	public BlockRenderLayer getRenderLayer(){
//		return BlockRenderLayer.TRANSLUCENT;
//	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
		builder.add(CRProperties.ACTIVE, CRProperties.HAS_MATCH_SIDES[2], CRProperties.HAS_MATCH_SIDES[3], CRProperties.HAS_MATCH_SIDES[4], CRProperties.HAS_MATCH_SIDES[5]);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context){
		int index = 0;
		for(int i = 2; i < 5; i++){
			index |= state.getValue(CRProperties.HAS_MATCH_SIDES[i]) ? 1 << i - 2 : 0;
		}
		return SHAPES[index];
	}


	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context){
		boolean[] connect = new boolean[6];
		EnumContainerType contType = crystal ? EnumContainerType.CRYSTAL : EnumContainerType.GLASS;
		for(int i = 2; i < 6; i++){
			IChemicalHandler otherHandler;
			Direction dir = Direction.from3DDataValue(i).getOpposite();
			if((otherHandler = context.getLevel().getCapability(CRCapabilities.CHEMICAL_CAPABILITY, context.getClickedPos().relative(Direction.from3DDataValue(i)), Direction.from3DDataValue(i).getOpposite())) != null && otherHandler.getChannel(dir).connectsWith(contType) && otherHandler.getMode(dir).connectsWith(EnumTransferMode.INPUT)){
				connect[i] = true;
			}
		}
		return defaultBlockState().setValue(CRProperties.HAS_MATCH_SIDES[2], connect[2]).setValue(CRProperties.HAS_MATCH_SIDES[3], connect[3]).setValue(CRProperties.HAS_MATCH_SIDES[4], connect[4]).setValue(CRProperties.HAS_MATCH_SIDES[5], connect[5]);
	}

	@Override
	public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos pos, BlockPos facingPos){
		BlockEntity thisTE = worldIn.getBlockEntity(pos);
		IChemicalHandler otherHandler;
		Direction dir = facing.getOpposite();
		boolean connect = thisTE instanceof ReagentPumpTileEntity && (otherHandler = thisTE.getLevel().getCapability(CRCapabilities.CHEMICAL_CAPABILITY, facingPos, facing.getOpposite())) != null && otherHandler.getChannel(dir).connectsWith(crystal ? EnumContainerType.CRYSTAL : EnumContainerType.GLASS) && otherHandler.getMode(dir).connectsWith(EnumTransferMode.INPUT);
		if(facing.getAxis() != Direction.Axis.Y){
			BooleanProperty prop = CRProperties.HAS_MATCH_SIDES[facing.get3DDataValue()];
			return stateIn.setValue(prop, connect);
		}else{
			return stateIn;
		}
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag){
		tooltip.add(Component.translatable("tt.crossroads.reagent_pump.desc"));
		tooltip.add(Component.translatable("tt.crossroads.reagent_pump.power"));
	}
}
