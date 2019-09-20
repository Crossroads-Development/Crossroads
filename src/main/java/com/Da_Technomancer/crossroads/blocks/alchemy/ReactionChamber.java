package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.alchemy.AlchemyCore;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentStack;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.items.CrossroadsItems;
import com.Da_Technomancer.crossroads.tileentities.alchemy.ReactionChamberTileEntity;
import net.minecraft.block.*;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.block.BlockRenderType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ReactionChamber extends ContainerBlock{

	private final boolean crystal;

	public ReactionChamber(boolean crystal){
		super(Material.GLASS);
		this.crystal = crystal;
		String name = (crystal ? "crystal_" : "") + "reaction_chamber";
		setTranslationKey(name);
		setRegistryName(name);
		setHardness(.5F);
		setCreativeTab(CrossroadsItems.TAB_CROSSROADS);
		setSoundType(SoundType.GLASS);
		CrossroadsBlocks.toRegister.add(this);
		CrossroadsBlocks.blockAddQue(this, 0, new ModelResourceLocation(Crossroads.MODID + ':' + name, "inventory"));
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new ReactionChamberTileEntity(!crystal);
	}

	@Override
	public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, TileEntity te, ItemStack stackIn){
		if(!(te instanceof ReactionChamberTileEntity)){
			super.harvestBlock(worldIn, player, pos, state, te, stackIn);
		}else{
			player.addExhaustion(0.005F);
			ItemStack stack = new ItemStack(Item.getItemFromBlock(this), 1);
			stack.put(((ReactionChamberTileEntity) te).getContentNBT());
			spawnAsEntity(worldIn, pos, stack);
		}
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack){
		if(stack.hasTag()){
			ReactionChamberTileEntity te = (ReactionChamberTileEntity) world.getTileEntity(pos);
			te.writeContentNBT(stack.getTag());
		}
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

	@Override
	public boolean isOpaqueCube(BlockState state){
		return false;
	}

	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		if(!worldIn.isRemote){
			TileEntity te = worldIn.getTileEntity(pos);
			if(te instanceof ReactionChamberTileEntity){
				playerIn.setHeldItem(hand, ((ReactionChamberTileEntity) te).rightClickWithItem(playerIn.getHeldItem(hand), playerIn.isSneaking(), playerIn, hand));
			}
		}
		return true;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced){
		if(stack.hasTag()){
			double am = 0;

			for(String key : stack.getTag().getKeySet()){
				if(!key.startsWith("qty_")){
					continue;
				}
				int qty = stack.getTag().getInt(key);
				am += qty;
				tooltip.add(new ReagentStack(AlchemyCore.REAGENTS.get(key.substring(4)), qty).toString());
			}

			tooltip.add("Temp: " + MiscUtil.betterRound(HeatUtil.toCelcius(stack.getTag().getDouble("heat") / am), 3));
		}
		tooltip.add("Consumes: -10FE/t (Optional)");
	}
}
