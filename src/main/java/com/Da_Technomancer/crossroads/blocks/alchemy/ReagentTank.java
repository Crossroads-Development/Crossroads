package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.API.alchemy.IReagent;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentMap;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.tileentities.alchemy.ReagentTankTileEntity;
import com.Da_Technomancer.essentials.blocks.redstone.IReadable;
import com.Da_Technomancer.essentials.blocks.redstone.RedstoneUtil;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
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

public class ReagentTank extends ContainerBlock implements IReadable{

	private static final String TAG_NAME = "reagents";
	private final boolean crystal;

	public ReagentTank(boolean crystal){
		super(Properties.create(Material.GLASS).hardnessAndResistance(0.5F).sound(SoundType.GLASS));
		this.crystal = crystal;
		String name = (crystal ? "crystal_" : "") + "reagent_tank";
		setRegistryName(name);
		CrossroadsBlocks.toRegister.add(this);
		CrossroadsBlocks.blockAddQue(this);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new ReagentTankTileEntity(!crystal);
	}

	@Override
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.MODEL;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BlockRenderLayer getRenderLayer(){
		return BlockRenderLayer.CUTOUT;
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
			tooltip.add(new TranslationTextComponent("tt.crossroads.boilerplate.temp_k", CRConfig.formatVal(temp), CRConfig.formatVal(HeatUtil.toKelvin(temp))));
			int total = 0;
			for(IReagent type : stored.keySet()){
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

		tooltip.add(new TranslationTextComponent("tt.crossroads.reagent_tank.redstone", ReagentTankTileEntity.CAPACITY));
	}

	@Override
	public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, TileEntity te, ItemStack stackIn){
		if(!(te instanceof ReagentTankTileEntity)){
			super.harvestBlock(worldIn, player, pos, state, te, stackIn);
		}else{
			player.addExhaustion(0.005F);
			ItemStack stack = new ItemStack(this, 1);
			setReagents(stack, ((ReagentTankTileEntity) te).getMap());
			spawnAsEntity(worldIn, pos, stack);
		}
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack){
		if(stack.hasTag()){
			ReagentTankTileEntity te = (ReagentTankTileEntity) world.getTileEntity(pos);
			te.writeContentNBT(stack.getTag());
		}
	}

	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		TileEntity te = worldIn.getTileEntity(pos);
		if(te instanceof ReagentTankTileEntity){
			if(!worldIn.isRemote){
				playerIn.setHeldItem(hand, ((ReagentTankTileEntity) te).rightClickWithItem(playerIn.getHeldItem(hand), playerIn.isSneaking(), playerIn, hand));
			}
			return true;
		}
		return false;
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
		if(te instanceof ReagentTankTileEntity){
			return ((ReagentTankTileEntity) te).getReds();
		}
		return 0;
	}
}
