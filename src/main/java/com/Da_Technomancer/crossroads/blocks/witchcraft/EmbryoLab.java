package com.Da_Technomancer.crossroads.blocks.witchcraft;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.tileentities.witchcraft.EmbryoLabTileEntity;
import com.Da_Technomancer.essentials.blocks.BlockUtil;
import com.Da_Technomancer.essentials.blocks.redstone.IReadable;
import com.Da_Technomancer.essentials.blocks.redstone.RedstoneUtil;
import net.minecraft.block.*;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

public class EmbryoLab extends BaseEntityBlock implements IReadable{

	private static final VoxelShape SHAPE = box(3, 0, 3, 13, 16, 13);

	public static final OptionalDispenseItemBehavior DISPENSE_ONTO_EMBRYO_LAB = new OptionalDispenseItemBehavior(){

		@Override
		protected ItemStack execute(BlockSource source, ItemStack stack){
			Level world = source.getLevel();
			if(!world.isClientSide()){
				BlockPos pos = source.getPos().relative(source.getBlockState().getValue(DispenserBlock.FACING));
				BlockEntity te = world.getBlockEntity(pos);
				if(te instanceof EmbryoLabTileEntity){
					setSuccess(true);
					return ((EmbryoLabTileEntity) te).addItem(stack);
				}
			}
			return stack;
		}
	};

	public EmbryoLab(){
		super(CRBlocks.getMetalProperty());
		String name = "embryo_lab";
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this);
		registerDefaultState(defaultBlockState().setValue(CRProperties.ACTIVE, false));
	}

	@Override
	public VoxelShape getShape(BlockState p_220053_1_, BlockGetter p_220053_2_, BlockPos p_220053_3_, CollisionContext p_220053_4_){
		return SHAPE;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new EmbryoLabTileEntity(pos, state);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
		builder.add(CRProperties.ACTIVE);
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand hand, BlockHitResult hit){
		BlockEntity te;
		if(!worldIn.isClientSide && (te = worldIn.getBlockEntity(pos)) instanceof EmbryoLabTileEntity){
			//Attempt to add the item in the offhand if there is a syringe in the main hand
			ItemStack held = playerIn.getItemInHand(hand);
			if(held.getItem() == CRItems.syringe){
				hand = InteractionHand.OFF_HAND;
				held = playerIn.getItemInHand(hand);

				ItemStack heldCopy = held.copy();
				ItemStack result = ((EmbryoLabTileEntity) te).addItem(held);
				//If the stack changed, assume we did something and shouldn't open the UI
				if(!held.isEmpty() && (!BlockUtil.sameItem(result, heldCopy) || result.getCount() != heldCopy.getCount())){
					playerIn.setItemInHand(hand, result);
					return InteractionResult.SUCCESS;
				}
			}

			//Didn't add an item. Open the UI
			NetworkHooks.openGui((ServerPlayer) playerIn, (MenuProvider) te, pos);
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving){
		BlockEntity te = world.getBlockEntity(pos);
		if(te instanceof Container && newState.getBlock() != state.getBlock()){
			Containers.dropContents(world, pos, (Container) te);
		}
		super.onRemove(state, world, pos, newState, isMoving);
	}

	@Override
	public RenderShape getRenderShape(BlockState state){
		return RenderShape.MODEL;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag advanced){
		tooltip.add(new TranslatableComponent("tt.crossroads.embryo_lab.desc"));
		tooltip.add(new TranslatableComponent("tt.crossroads.embryo_lab.ingr"));
		tooltip.add(new TranslatableComponent("tt.crossroads.embryo_lab.circuit"));
		tooltip.add(new TranslatableComponent("tt.crossroads.embryo_lab.quip").setStyle(MiscUtil.TT_QUIP));
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState p_149740_1_){
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos){
		return RedstoneUtil.clampToVanilla(read(world, pos, state));
	}

	@Override
	public float read(Level world, BlockPos pos, BlockState blockState){
		return blockState.getValue(CRProperties.ACTIVE) ? 1 : 0;
	}
}
