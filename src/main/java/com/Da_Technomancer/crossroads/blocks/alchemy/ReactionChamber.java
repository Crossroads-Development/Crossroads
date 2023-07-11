package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.api.alchemy.IReagent;
import com.Da_Technomancer.crossroads.api.alchemy.ReagentMap;
import com.Da_Technomancer.crossroads.api.heat.HeatUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.essentials.api.ITickableTileEntity;
import com.Da_Technomancer.essentials.api.redstone.IReadable;
import com.Da_Technomancer.essentials.api.redstone.RedstoneUtil;
import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ReactionChamber extends BaseEntityBlock implements IReadable{

	private static final String TAG_NAME = "reagents";
	private final boolean crystal;

	public ReactionChamber(boolean crystal){
		super(CRBlocks.getGlassProperty().noOcclusion());
		this.crystal = crystal;
		String name = (crystal ? "crystal_" : "") + "reaction_chamber";
		CRBlocks.queueForRegister(name, this);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new ReactionChamberTileEntity(pos, state, !crystal);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> type){
		return ITickableTileEntity.createTicker(type, ReactionChamberTileEntity.TYPE);
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder){
		BlockEntity te = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
		if(te instanceof ReactionChamberTileEntity){
			ItemStack drop = new ItemStack(this.asItem(), 1);
			setReagents(drop, ((ReactionChamberTileEntity) te).getMap());
			return Lists.newArrayList(drop);
		}
		return super.getDrops(state, builder);
	}

	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack){
		if(stack.hasTag()){
			ReactionChamberTileEntity te = (ReactionChamberTileEntity) world.getBlockEntity(pos);
			te.writeContentNBT(stack);
		}
	}

	@Override
	public RenderShape getRenderShape(BlockState state){
		return RenderShape.MODEL;
	}

//	@Override
//	@OnlyIn(Dist.CLIENT)
//	public BlockRenderLayer getRenderLayer(){
//		return BlockRenderLayer.CUTOUT;
//	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand hand, BlockHitResult hit){
		if(!worldIn.isClientSide){
			BlockEntity te = worldIn.getBlockEntity(pos);
			if(te instanceof ReactionChamberTileEntity){
				playerIn.setItemInHand(hand, ((ReactionChamberTileEntity) te).rightClickWithItem(playerIn.getItemInHand(hand), playerIn.isShiftKeyDown(), playerIn, hand));
			}
		}
		return InteractionResult.SUCCESS;
	}

	/**
	 * Cache the result to minimize calls to this method.
	 * @param stack The glassware itemstack
	 * @return The contained reagents. Modifying the returned array does NOT write through to the ItemStack, use the setReagents method.
	 */
	@Nonnull
	public static ReagentMap getReagants(ItemStack stack){
		return stack.hasTag() ? ReagentMap.readFromNBT(stack.getTag().getCompound(TAG_NAME)) : new ReagentMap();
	}

	/**
	 * Call this as little as possible.
	 * @param stack The stack to store the reagents to
	 * @param reagents The reagents to store
	 */
	public void setReagents(ItemStack stack, ReagentMap reagents){
		if(!stack.hasTag()){
			stack.setTag(new CompoundTag());
		}

		CompoundTag nbt = new CompoundTag();
		stack.getTag().put(TAG_NAME, nbt);

		reagents.write(nbt);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag flagIn){
		ReagentMap stored = getReagants(stack);

		double temp = stored.getTempC();

		if(stored.getTotalQty() == 0){
			tooltip.add(Component.translatable("tt.crossroads.boilerplate.alchemy_empty"));
		}else{
			HeatUtil.addHeatInfo(tooltip, temp, Short.MIN_VALUE);
			int total = 0;
			for(IReagent type : stored.keySetReag()){
				int qty = stored.getQty(type);
				if(qty > 0){
					total++;
					if(total <= 4 || flagIn != TooltipFlag.Default.NORMAL){
						tooltip.add(Component.translatable("tt.crossroads.boilerplate.alchemy_content", type.getName(), qty));
					}else{
						break;
					}
				}
			}
			if(total > 4 && flagIn == TooltipFlag.Default.NORMAL){
				tooltip.add(Component.translatable("tt.crossroads.boilerplate.alchemy_excess", total - 4));
			}
		}

		tooltip.add(Component.translatable("tt.crossroads.reaction_chamber.power", ReactionChamberTileEntity.DRAIN));
		tooltip.add(Component.translatable("tt.crossroads.reaction_chamber.redstone", ReactionChamberTileEntity.CAPACITY));
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
	public float read(Level world, BlockPos pos, BlockState blockState){
		BlockEntity te = world.getBlockEntity(pos);
		if(te instanceof ReactionChamberTileEntity){
			return ((ReactionChamberTileEntity) te).getReds();
		}
		return 0;
	}
}
