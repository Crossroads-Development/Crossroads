package com.Da_Technomancer.crossroads.blocks.fluid;

import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.essentials.api.redstone.IReadable;
import com.Da_Technomancer.essentials.api.redstone.RedstoneUtil;
import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;

public class FluidTank extends BaseEntityBlock implements IReadable{

	public FluidTank(){
		super(CRBlocks.getMetalProperty());
		String name = "fluid_tank";
		CRBlocks.queueForRegister(name, this);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new FluidTankTileEntity(pos, state);
	}

//	Ticking not utilized
//	@Nullable
//	@Override
//	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> type){
//		return ITickableTileEntity.createTicker(type, FluidTankTileEntity.TYPE);
//	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag advanced){
		FluidStack fStack = getFluidOnItem(stack);
		if(!fStack.isEmpty()){
			tooltip.add(Component.translatable("tt.crossroads.fluid_tank", fStack.getAmount(), fStack.getDisplayName().getString()));
		}
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder){
		BlockEntity te = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
		if(te instanceof FluidTankTileEntity){
			ItemStack drop = new ItemStack(this.asItem(), 1);
			((FluidTankTileEntity) te).getContent().writeToNBT(drop.getOrCreateTag());
			return Lists.newArrayList(drop);
		}
		return super.getDrops(state, builder);
	}

	private FluidStack getFluidOnItem(ItemStack stack){
		FluidStack nbtFluid = FluidStack.loadFluidStackFromNBT(stack.getTag());
		if(nbtFluid.isEmpty()){
			nbtFluid = FluidStack.loadFluidStackFromNBT(stack.getOrCreateTagElement("BlockEntityTag").getCompound("fluid_0"));
		}
		return nbtFluid;
	}

	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack){
		if(stack.hasTag()){
			FluidTankTileEntity te = (FluidTankTileEntity) world.getBlockEntity(pos);
			te.setContent(getFluidOnItem(stack));
		}
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand hand, BlockHitResult hit){
		if(!worldIn.isClientSide){
			BlockEntity te;
			if(FluidUtil.getFluidHandler(playerIn.getItemInHand(InteractionHand.MAIN_HAND)).isPresent()){
				//Tanks be clicked on with buckets/equivalent
				return FluidUtil.interactWithFluidHandler(playerIn, hand, worldIn, pos, null) ? InteractionResult.SUCCESS : InteractionResult.FAIL;
			}else if((te = worldIn.getBlockEntity(pos)) instanceof MenuProvider){
				NetworkHooks.openScreen((ServerPlayer) playerIn, (MenuProvider) te, pos);
			}
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state){
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos){
		return RedstoneUtil.clampToVanilla(read(worldIn, pos, blockState));
	}

	@Override
	public RenderShape getRenderShape(BlockState state){
		return RenderShape.MODEL;
	}

	@Override
	public float read(Level world, BlockPos pos, BlockState blockState){
		BlockEntity te = world.getBlockEntity(pos);
		if(te instanceof FluidTankTileEntity){
			return ((FluidTankTileEntity) te).getContent().getAmount();
		}
		return 0;
	}
}
