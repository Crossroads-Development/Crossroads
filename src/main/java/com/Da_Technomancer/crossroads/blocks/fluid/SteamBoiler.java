package com.Da_Technomancer.crossroads.blocks.fluid;

import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.tileentities.fluid.SteamBoilerTileEntity;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;
import java.util.List;

public class SteamBoiler extends BlockContainer{

	public SteamBoiler(){
		super(Material.IRON);
		String name = "steam_boiler";
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(ModItems.TAB_CROSSROADS);
		setHardness(3);
		setSoundType(SoundType.METAL);
		ModBlocks.toRegister.add(this);
		ModBlocks.blockAddQue(this);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return new SteamBoilerTileEntity();
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState blockstate){
		ItemStack stack = world.getTileEntity(pos).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).getStackInSlot(0);
		InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
		super.breakBlock(world, pos, blockstate);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
		if(!worldIn.isRemote){
			TileEntity te = worldIn.getTileEntity(pos);
			if(te instanceof SteamBoilerTileEntity){

				if(playerIn == null || !FluidUtil.interactWithFluidHandler(playerIn, hand, worldIn, pos, null)){
					int saltContent = ((SteamBoilerTileEntity) te).inventory.getCount();
					if(saltContent != 0){
						((SteamBoilerTileEntity) te).inventory = ItemStack.EMPTY;
						te.markDirty();
						ItemStack gotten = MiscOp.getOredictStack("dustSalt", saltContent);
						if(!playerIn.inventory.addItemStackToInventory(gotten)){
							EntityItem dropped = playerIn.dropItem(gotten, false);
							dropped.setNoPickupDelay();
							dropped.setOwner(playerIn.getName());
						}
					}
				}
			}
		}
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced){
		tooltip.add("Produces: 100mB/t steam while running");
		tooltip.add("Produces: 1 salt/t while running with normal water");
		tooltip.add("Consumes: 100mB/t [distilled] water while running");
		tooltip.add("Consumes: -5°C/t while above 100°C and contains any [distilled] water");
	}
}
