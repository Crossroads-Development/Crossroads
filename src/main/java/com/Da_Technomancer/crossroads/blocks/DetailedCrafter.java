package com.Da_Technomancer.crossroads.blocks;

import com.Da_Technomancer.crossroads.gui.container.DetailedCrafterContainer;
import io.netty.buffer.Unpooled;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;

public class DetailedCrafter extends Block{

	private static final VoxelShape SHAPE = Shapes.or(box(1, 0, 1, 15, 7, 15), box(0, 7, 0, 16, 15, 16));

	public DetailedCrafter(){
		super(CRBlocks.getMetalProperty());
		String name = "detailed_crafter";
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn){
		tooltip.add(new TranslatableComponent("tt.crossroads.detailed_crafter.desc"));
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context){
		return SHAPE;
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand hand, BlockHitResult hit){
		if(!worldIn.isClientSide){
//			StoreNBTToClient.syncNBTToClient((ServerPlayerEntity) playerIn);//Sync player path data to client
			NetworkHooks.openGui((ServerPlayer) playerIn, new DetailedCrafterProvider(pos), buf -> {buf.writeBoolean(false); buf.writeBlockPos(pos);});
		}
		return InteractionResult.SUCCESS;
	}

	private static class DetailedCrafterProvider implements MenuProvider{

		private final BlockPos pos;

		private DetailedCrafterProvider(BlockPos pos){
			this.pos = pos;
		}

		@Override
		public Component getDisplayName(){
			return new TranslatableComponent("container.detailed_crafter");
		}

		@Nullable
		@Override
		public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player){
			FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
			buf.writeBoolean(false);//Encode that this is a physical table
			buf.writeBlockPos(pos);
			return new DetailedCrafterContainer(id, playerInv, buf);
		}
	}
}
