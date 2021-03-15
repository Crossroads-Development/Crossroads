package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.API.alchemy.IReagent;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentMap;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.alchemy.ReactionChamberTileEntity;
import com.Da_Technomancer.essentials.blocks.redstone.IReadable;
import com.Da_Technomancer.essentials.blocks.redstone.RedstoneUtil;
import com.google.common.collect.Lists;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.nbt.CompoundNBT;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ReactionChamber extends ContainerBlock implements IReadable{

	private static final String TAG_NAME = "reagents";
	private final boolean crystal;

	public ReactionChamber(boolean crystal){
		super(CRBlocks.getGlassProperty().notSolid());
		this.crystal = crystal;
		String name = (crystal ? "crystal_" : "") + "reaction_chamber";
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new ReactionChamberTileEntity(!crystal);
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder){
		TileEntity te = builder.get(LootParameters.BLOCK_ENTITY);
		if(te instanceof ReactionChamberTileEntity){
			ItemStack drop = new ItemStack(this.asItem(), 1);
			setReagents(drop, ((ReactionChamberTileEntity) te).getMap());
			return Lists.newArrayList(drop);
		}
		return super.getDrops(state, builder);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack){
		if(stack.hasTag()){
			ReactionChamberTileEntity te = (ReactionChamberTileEntity) world.getTileEntity(pos);
			te.writeContentNBT(stack);
		}
	}

	@Override
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.MODEL;
	}

//	@Override
//	@OnlyIn(Dist.CLIENT)
//	public BlockRenderLayer getRenderLayer(){
//		return BlockRenderLayer.CUTOUT;
//	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		if(!worldIn.isRemote){
			TileEntity te = worldIn.getTileEntity(pos);
			if(te instanceof ReactionChamberTileEntity){
				playerIn.setHeldItem(hand, ((ReactionChamberTileEntity) te).rightClickWithItem(playerIn.getHeldItem(hand), playerIn.isSneaking(), playerIn, hand));
			}
		}
		return ActionResultType.SUCCESS;
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
			stack.setTag(new CompoundNBT());
		}

		CompoundNBT nbt = new CompoundNBT();
		stack.getTag().put(TAG_NAME, nbt);

		reagents.write(nbt);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag flagIn){
		ReagentMap stored = getReagants(stack);

		double temp = stored.getTempC();

		if(stored.getTotalQty() == 0){
			tooltip.add(new TranslationTextComponent("tt.crossroads.boilerplate.alchemy_empty"));
		}else{
			HeatUtil.addHeatInfo(tooltip, temp, Short.MIN_VALUE);
			int total = 0;
			for(IReagent type : stored.keySetReag()){
				int qty = stored.getQty(type);
				if(qty > 0){
					total++;
					if(total <= 4 || flagIn != ITooltipFlag.TooltipFlags.NORMAL){
						tooltip.add(new TranslationTextComponent("tt.crossroads.boilerplate.alchemy_content", type.getName(), qty));
					}else{
						break;
					}
				}
			}
			if(total > 4 && flagIn == ITooltipFlag.TooltipFlags.NORMAL){
				tooltip.add(new TranslationTextComponent("tt.crossroads.boilerplate.alchemy_excess", total - 4));
			}
		}

		tooltip.add(new TranslationTextComponent("tt.crossroads.reaction_chamber.power", ReactionChamberTileEntity.DRAIN));
		tooltip.add(new TranslationTextComponent("tt.crossroads.reaction_chamber.redstone", ReactionChamberTileEntity.CAPACITY));
	}

	@Override
	public boolean hasComparatorInputOverride(BlockState state){
		return true;
	}

	@Override
	public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos){
		return RedstoneUtil.clampToVanilla(read(worldIn, pos, blockState));
	}

	@Override
	public float read(World world, BlockPos pos, BlockState blockState){
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof ReactionChamberTileEntity){
			return ((ReactionChamberTileEntity) te).getReds();
		}
		return 0;
	}
}
