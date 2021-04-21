package com.Da_Technomancer.crossroads.blocks.rotary;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.rotary.RotaryUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.rotary.WindTurbineTileEntity;
import com.Da_Technomancer.essentials.ESConfig;
import com.Da_Technomancer.essentials.blocks.redstone.IReadable;
import com.Da_Technomancer.essentials.blocks.redstone.RedstoneUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
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
import net.minecraftforge.common.Tags;

import javax.annotation.Nullable;
import java.util.List;

public class WindTurbine extends ContainerBlock implements IReadable{

	public WindTurbine(){
		super(Properties.of(Material.WOOD).strength(2));
		String name = "wind_turbine";
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this);
	}

	@Override
	public TileEntity newBlockEntity(IBlockReader worldIn){
		return new WindTurbineTileEntity(true);
	}

	@Override
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		ItemStack heldItem = playerIn.getItemInHand(hand);
		if(ESConfig.isWrench(heldItem)){
			if(!worldIn.isClientSide){
				worldIn.setBlockAndUpdate(pos, state.setValue(CRProperties.HORIZ_FACING, state.getValue(CRProperties.HORIZ_FACING).getClockWise()));
				RotaryUtil.increaseMasterKey(true);
			}
			return ActionResultType.SUCCESS;
		}else if(Tags.Items.DYES.contains(heldItem.getItem())){
			TileEntity te = worldIn.getBlockEntity(pos);
			if(te instanceof WindTurbineTileEntity){
				if(!worldIn.isClientSide){
					((WindTurbineTileEntity) te).dyeBlade(heldItem);
				}
				return ActionResultType.SUCCESS;
			}
		}
		return ActionResultType.PASS;
	}

	@Override
	public BlockRenderType getRenderShape(BlockState state){
		return BlockRenderType.MODEL;
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context){
		return defaultBlockState().setValue(CRProperties.HORIZ_FACING, context.getHorizontalDirection());
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder){
		builder.add(CRProperties.HORIZ_FACING);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced){
		tooltip.add(new TranslationTextComponent("tt.crossroads.wind_turbine.desc"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.wind_turbine.power", WindTurbineTileEntity.LOW_POWER, WindTurbineTileEntity.HIGH_POWER, (WindTurbineTileEntity.LOW_POWER + WindTurbineTileEntity.HIGH_POWER) / 2D));
		tooltip.add(new TranslationTextComponent("tt.crossroads.wind_turbine.limits", WindTurbineTileEntity.MAX_SPEED));
		tooltip.add(new TranslationTextComponent("tt.crossroads.wind_turbine.env"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.boilerplate.inertia", WindTurbineTileEntity.INERTIA));
	}

	@Override
	public float read(World world, BlockPos pos, BlockState state){
		TileEntity te = world.getBlockEntity(pos);
		if(te instanceof WindTurbineTileEntity){
			return ((WindTurbineTileEntity) te).getRedstoneOutput();
		}else{
			return 0;
		}
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state){
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState state, World world, BlockPos pos){
		return RedstoneUtil.clampToVanilla(read(world, pos, state));
	}
}
