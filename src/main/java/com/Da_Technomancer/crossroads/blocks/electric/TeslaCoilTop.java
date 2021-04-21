package com.Da_Technomancer.crossroads.blocks.electric;

import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.electric.TeslaCoilTileEntity;
import com.Da_Technomancer.crossroads.tileentities.electric.TeslaCoilTopTileEntity;
import com.Da_Technomancer.essentials.tileentities.ILinkTE;
import com.Da_Technomancer.essentials.tileentities.LinkHelper;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

public class TeslaCoilTop extends ContainerBlock{

	private static final VoxelShape SHAPE = VoxelShapes.or(box(4, 0, 4, 12, 8, 12), box(0, 8, 0, 16, 16, 16));
	public final TeslaCoilVariants variant;

	public TeslaCoilTop(TeslaCoilVariants variant){
		super(CRBlocks.getMetalProperty());
		this.variant = variant;
		String name = "tesla_coil_top_" + variant.toString();
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context){
		return SHAPE;
	}

	@Override
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		ItemStack heldItem = playerIn.getItemInHand(hand);
		if(LinkHelper.isLinkTool(heldItem)){
			TileEntity te = worldIn.getBlockEntity(pos);
			if(!worldIn.isClientSide && te instanceof TeslaCoilTopTileEntity){
				LinkHelper.wrench((ILinkTE) te, heldItem, playerIn);
			}
			return ActionResultType.SUCCESS;
		}
		return ActionResultType.PASS;
	}

	@Override
	public BlockRenderType getRenderShape(BlockState state){
		return BlockRenderType.MODEL;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn){
		tooltip.add(new TranslationTextComponent("tt.crossroads.tesla_coil_top.range", variant.range));
		tooltip.add(new TranslationTextComponent("tt.crossroads.tesla_coil_top.fe", variant.joltAmt));
		tooltip.add(new TranslationTextComponent("tt.crossroads.tesla_coil_top.eff", (100 - variant.efficiency)));
		if(variant == TeslaCoilVariants.ATTACK){
			tooltip.add(new TranslationTextComponent("tt.crossroads.tesla_coil_top.att"));
		}else if(variant == TeslaCoilVariants.DECORATIVE){
			tooltip.add(new TranslationTextComponent("tt.crossroads.tesla_coil_top.decor"));
		}
		if(variant.joltAmt > TeslaCoilTileEntity.CAPACITY){
			tooltip.add(new TranslationTextComponent("tt.crossroads.tesla_coil_top.leyden"));
		}
	}

	@Nullable
	@Override
	public TileEntity newBlockEntity(IBlockReader worldIn){
		return new TeslaCoilTopTileEntity();
	}

	public enum TeslaCoilVariants{

		//Yep, it's an enum. Sorry addon makers- go bug me on discord if you need this changed
		NORMAL(1_000, 8, 98),
		ATTACK(1_000, 6, 0),
		DISTANCE(1_000, 32, 98),
		INTENSITY(10_000, 8, 98),
		EFFICIENCY(1_000, 8, 100),
		DECORATIVE(100, 0, 0);

		public final int joltAmt;
		public final int range;
		public final int efficiency;

		TeslaCoilVariants(int joltAmt, int range, int efficiency){
			this.joltAmt = joltAmt;
			this.range = range;
			this.efficiency = efficiency;
		}

		@Override
		public String toString(){
			return name().toLowerCase(Locale.US);
		}
	}
}
