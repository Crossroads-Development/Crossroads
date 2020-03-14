package com.Da_Technomancer.crossroads.blocks.fluid;

import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.fluid.FluidTankTileEntity;
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
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;

public class FluidTank extends ContainerBlock implements IReadable{

	public FluidTank(){
		super(Properties.create(Material.IRON).sound(SoundType.METAL).hardnessAndResistance(3));
		String name = "fluid_tank";
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new FluidTankTileEntity();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced){
		CompoundNBT nbt = stack.getTag();
		if(nbt != null && nbt.contains("FluidName")){
			FluidStack fStack = FluidStack.loadFluidStackFromNBT(stack.getTag());
			tooltip.add(new TranslationTextComponent("tt.crossroads.fluid_tank", fStack.getAmount(), fStack.getDisplayName().getFormattedText()));
		}
	}

	@Override
	public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, TileEntity te, ItemStack stackIn){
		if(!(te instanceof FluidTankTileEntity)|| ((FluidTankTileEntity) te).getContent().isEmpty()){
			super.harvestBlock(worldIn, player, pos, state, te, stackIn);
		}else{
			player.addExhaustion(0.005F);
			ItemStack stack = new ItemStack(this.asItem(), 1);
			stack.setTag(((FluidTankTileEntity) te).getContent().writeToNBT(new CompoundNBT()));
			spawnAsEntity(worldIn, pos, stack);
		}
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack){
		if(stack.hasTag()){
			FluidTankTileEntity te = (FluidTankTileEntity) world.getTileEntity(pos);
			te.setContent(FluidStack.loadFluidStackFromNBT(stack.getTag()));
		}
	}

	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		if(!worldIn.isRemote){
			TileEntity te;
			if(FluidUtil.getFluidHandler(playerIn.getHeldItem(Hand.MAIN_HAND)).isPresent()){
				//Tanks be clicked on with buckets/equivalent
				return FluidUtil.interactWithFluidHandler(playerIn, hand, worldIn, pos, null);
			}else if((te = worldIn.getTileEntity(pos)) instanceof INamedContainerProvider){
				NetworkHooks.openGui((ServerPlayerEntity) playerIn, (INamedContainerProvider) te, pos);
			}
		}
		return true;
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
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.MODEL;
	}

	@Override
	public float read(World world, BlockPos pos, BlockState blockState){
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof FluidTankTileEntity){
			return 15F * (float) ((FluidTankTileEntity) te).getContent().getAmount() / FluidTankTileEntity.CAPACITY;
		}
		return 0;
	}
}
