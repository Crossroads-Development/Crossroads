package com.Da_Technomancer.crossroads.blocks.electric;

import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.essentials.api.ILinkTE;
import com.Da_Technomancer.essentials.api.LinkHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

public class TeslaCoilTop extends BaseEntityBlock{

	private static final VoxelShape SHAPE = Shapes.or(box(4, 0, 4, 12, 8, 12), box(0, 8, 0, 16, 16, 16));
	public final TeslaCoilVariants variant;

	public TeslaCoilTop(TeslaCoilVariants variant){
		super(CRBlocks.getMetalProperty());
		this.variant = variant;
		String name = "tesla_coil_top_" + variant.toString();
		CRBlocks.toRegister.put(name, this);
		CRBlocks.blockAddQue(name, this);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context){
		return SHAPE;
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand hand, BlockHitResult hit){
		ItemStack heldItem = playerIn.getItemInHand(hand);
		if(LinkHelper.isLinkTool(heldItem)){
			BlockEntity te = worldIn.getBlockEntity(pos);
			if(!worldIn.isClientSide && te instanceof TeslaCoilTopTileEntity){
				LinkHelper.wrench((ILinkTE) te, heldItem, playerIn);
			}
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	@Override
	public RenderShape getRenderShape(BlockState state){
		return RenderShape.MODEL;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn){
		tooltip.add(Component.translatable("tt.crossroads.tesla_coil_top.range", variant.range));
		tooltip.add(Component.translatable("tt.crossroads.tesla_coil_top.fe", variant.joltAmt));
		tooltip.add(Component.translatable("tt.crossroads.tesla_coil_top.eff", (100 - variant.efficiency)));
		if(variant == TeslaCoilVariants.ATTACK){
			tooltip.add(Component.translatable("tt.crossroads.tesla_coil_top.att"));
		}else if(variant == TeslaCoilVariants.DECORATIVE){
			tooltip.add(Component.translatable("tt.crossroads.tesla_coil_top.decor"));
		}
		if(variant.joltAmt > TeslaCoilTileEntity.CAPACITY){
			tooltip.add(Component.translatable("tt.crossroads.tesla_coil_top.leyden"));
		}
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new TeslaCoilTopTileEntity(pos, state);
	}

//	Non-ticking TE
//	@Nullable
//	@Override
//	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> type){
//		return ITickableTileEntity.createTicker(type, TeslaCoilTopTileEntity.TYPE);
//	}

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
