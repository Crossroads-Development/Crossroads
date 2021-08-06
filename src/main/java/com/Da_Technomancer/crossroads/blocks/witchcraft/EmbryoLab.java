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
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.OptionalDispenseBehavior;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
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

public class EmbryoLab extends ContainerBlock implements IReadable{

	public static final OptionalDispenseBehavior DISPENSE_ONTO_EMBRYO_LAB = new OptionalDispenseBehavior(){

		@Override
		protected ItemStack execute(IBlockSource source, ItemStack stack){
			World world = source.getLevel();
			if(!world.isClientSide()){
				BlockPos pos = source.getPos().relative(source.getBlockState().getValue(DispenserBlock.FACING));
				TileEntity te = world.getBlockEntity(pos);
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
	public TileEntity newBlockEntity(IBlockReader worldIn){
		return new EmbryoLabTileEntity();
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder){
		builder.add(CRProperties.ACTIVE);
	}

	@Override
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		TileEntity te;
		if(!worldIn.isClientSide && (te = worldIn.getBlockEntity(pos)) instanceof EmbryoLabTileEntity){
			//Attempt to add the item in the offhand if there is a syringe in the main hand
			ItemStack held = playerIn.getItemInHand(hand);
			if(held.getItem() == CRItems.syringe){
				hand = Hand.OFF_HAND;
				held = playerIn.getItemInHand(hand);

				ItemStack heldCopy = held.copy();
				ItemStack result = ((EmbryoLabTileEntity) te).addItem(held);
				//If the stack changed, assume we did something and shouldn't open the UI
				if(!BlockUtil.sameItem(result, heldCopy) || result.getCount() != heldCopy.getCount()){
					playerIn.setItemInHand(hand, result);
					return ActionResultType.SUCCESS;
				}
			}

			//Didn't add an item. Open the UI
			NetworkHooks.openGui((ServerPlayerEntity) playerIn, (INamedContainerProvider) te, pos);
		}
		return ActionResultType.SUCCESS;
	}

	@Override
	public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving){
		TileEntity te = world.getBlockEntity(pos);
		if(te instanceof IInventory && newState.getBlock() != state.getBlock()){
			InventoryHelper.dropContents(world, pos, (IInventory) te);
		}
		super.onRemove(state, world, pos, newState, isMoving);
	}

	@Override
	public BlockRenderType getRenderShape(BlockState state){
		return BlockRenderType.MODEL;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced){
		tooltip.add(new TranslationTextComponent("tt.crossroads.embryo_lab.desc"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.embryo_lab.ingr"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.embryo_lab.circuit"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.embryo_lab.quip").setStyle(MiscUtil.TT_QUIP));
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState p_149740_1_){
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState state, World world, BlockPos pos){
		return RedstoneUtil.clampToVanilla(read(world, pos, state));
	}

	@Override
	public float read(World world, BlockPos pos, BlockState blockState){
		return blockState.getValue(CRProperties.ACTIVE) ? 1 : 0;
	}
}
