package com.Da_Technomancer.crossroads.blocks;

import com.Da_Technomancer.crossroads.gui.container.DetailedCrafterContainer;
import io.netty.buffer.Unpooled;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;

public class DetailedCrafter extends Block{

	public DetailedCrafter(){
		super(Properties.create(Material.IRON).hardnessAndResistance(3));
		String name = "detailed_crafter";
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this);
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn){
		tooltip.add(new TranslationTextComponent("tt.crossroads.detailed_crafter.desc"));
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		if(!worldIn.isRemote){
//			StoreNBTToClient.syncNBTToClient((ServerPlayerEntity) playerIn);//Sync player path data to client
			NetworkHooks.openGui((ServerPlayerEntity) playerIn, new DetailedCrafterProvider(pos), buf -> {buf.writeBoolean(false); buf.writeBlockPos(pos);});
		}
		return ActionResultType.SUCCESS;
	}

	private static class DetailedCrafterProvider implements INamedContainerProvider{

		private final BlockPos pos;

		private DetailedCrafterProvider(BlockPos pos){
			this.pos = pos;
		}

		@Override
		public ITextComponent getDisplayName(){
			return new TranslationTextComponent("container.detailed_crafter");
		}

		@Nullable
		@Override
		public Container createMenu(int id, PlayerInventory playerInv, PlayerEntity player){
			PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
			buf.writeBoolean(false);//Encode that this is a physical table
			buf.writeBlockPos(pos);
			return new DetailedCrafterContainer(id, playerInv, buf);
		}
	}
}
