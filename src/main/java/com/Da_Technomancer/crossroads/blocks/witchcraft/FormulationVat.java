package com.Da_Technomancer.crossroads.blocks.witchcraft;

import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.witchcraft.FormulationVatTileEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;

public class FormulationVat extends ContainerBlock{

	public FormulationVat(){
		super(CRBlocks.getMetalProperty());
		String name = "formulation_vat";
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this);
	}

	@Override
	public TileEntity newBlockEntity(IBlockReader worldIn){
		return new FormulationVatTileEntity();
	}

	@Override
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		TileEntity te;
		if(!worldIn.isClientSide && (te = worldIn.getBlockEntity(pos)) instanceof INamedContainerProvider){
			NetworkHooks.openGui((ServerPlayerEntity) playerIn, (INamedContainerProvider) te, pos);
		}
		return ActionResultType.SUCCESS;
	}

	@Override
	public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving){
		InventoryHelper.dropContents(world, pos, (IInventory) world.getBlockEntity(pos));
		super.onRemove(state, world, pos, newState, isMoving);
	}

	@Override
	public BlockRenderType getRenderShape(BlockState state){
		return BlockRenderType.MODEL;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced){
		tooltip.add(new TranslationTextComponent("tt.crossroads.formulation_vat.desc"));
		for(int i = 0; i < FormulationVatTileEntity.TEMP_TIERS.length; i++){
			tooltip.add(new TranslationTextComponent("tt.crossroads.formulation_vat.tier", FormulationVatTileEntity.TEMP_TIERS[i], FormulationVatTileEntity.SPEED_MULT[i], FormulationVatTileEntity.HEAT_DRAIN[i]));
		}
	}
}
