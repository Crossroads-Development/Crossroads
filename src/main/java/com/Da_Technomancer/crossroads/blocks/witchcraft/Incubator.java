package com.Da_Technomancer.crossroads.blocks.witchcraft;

import com.Da_Technomancer.crossroads.API.templates.TEBlock;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.witchcraft.IncubatorTileEntity;
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
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;

public class Incubator extends TEBlock implements IReadable{

	public Incubator(){
		super(CRBlocks.getMetalProperty());
		String name = "incubator";
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new IncubatorTileEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> type){
		return ITickableTileEntity.createTicker(type, IncubatorTileEntity.TYPE);
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand hand, BlockHitResult hit){
		BlockEntity te;
		if(!worldIn.isClientSide && (te = worldIn.getBlockEntity(pos)) instanceof MenuProvider){
			NetworkHooks.openGui((ServerPlayer) playerIn, (MenuProvider) te, pos);
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag advanced){
		tooltip.add(new TranslatableComponent("tt.crossroads.incubator.desc"));
		tooltip.add(new TranslatableComponent("tt.crossroads.incubator.heating", IncubatorTileEntity.MIN_TEMP, IncubatorTileEntity.MAX_TEMP, IncubatorTileEntity.MARGIN));
		tooltip.add(new TranslatableComponent("tt.crossroads.incubator.redstone"));
	}

	@Override
	public float read(Level world, BlockPos pos, BlockState blockState){
		BlockEntity te = world.getBlockEntity(pos);
		if(te instanceof IncubatorTileEntity){
			return (float) ((IncubatorTileEntity) te).getTargetTemp();
		}
		return 0;
	}
}
