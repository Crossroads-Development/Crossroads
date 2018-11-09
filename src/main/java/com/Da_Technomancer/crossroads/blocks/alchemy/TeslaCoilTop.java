package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.tileentities.alchemy.TeslaCoilTileEntity;
import com.Da_Technomancer.essentials.EssentialsConfig;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class TeslaCoilTop extends Block{

	public TeslaCoilTop(){
		super(Material.IRON);
		String name = "tesla_coil_top";
		setUnlocalizedName(name);
		setRegistryName(name);
		setHardness(2);
		setSoundType(SoundType.METAL);
		ModBlocks.toRegister.add(this);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
		ItemStack heldItem = playerIn.getHeldItem(hand);
		if(EssentialsConfig.isWrench(heldItem, worldIn.isRemote)){
			if(!worldIn.isRemote){
				if(playerIn.isSneaking()){
					playerIn.sendMessage(new TextComponentString("Clearing tesla coil links."));
					TileEntity te = worldIn.getTileEntity(pos.offset(EnumFacing.DOWN));
					if(te instanceof TeslaCoilTileEntity){
						((TeslaCoilTileEntity) te).linked = new BlockPos[3];
					}
				}else if(heldItem.hasTagCompound() && heldItem.getTagCompound().hasKey("c_link")){
					BlockPos prev = BlockPos.fromLong(heldItem.getTagCompound().getLong("c_link"));
					TileEntity te = worldIn.getTileEntity(prev);
					if(te instanceof TeslaCoilTileEntity){
						BlockPos[] links = ((TeslaCoilTileEntity) te).linked;
						if(prev.distanceSq(pos.offset(EnumFacing.DOWN)) <= TeslaCoilTileEntity.RANGE * TeslaCoilTileEntity.RANGE){
							for(int i = 0; i < 3; i++){
								if(links[i] == null){
									links[i] = pos.offset(EnumFacing.DOWN).subtract(prev);
									playerIn.sendMessage(new TextComponentString("Linked coil at " + prev + " to send to " + pos.offset(EnumFacing.DOWN) + "."));
									heldItem.getTagCompound().removeTag("c_link");
									return true;
								}
							}
							playerIn.sendMessage(new TextComponentString("All 3 links already occupied; Canceling linking."));
							heldItem.getTagCompound().removeTag("c_link");
						}else{
							playerIn.sendMessage(new TextComponentString("Out of range; Canceling linking."));
							heldItem.getTagCompound().removeTag("c_link");
						}
					}else{
						playerIn.sendMessage(new TextComponentString("Invalid pair; Canceling linking."));
						heldItem.getTagCompound().removeTag("c_link");
					}
				}else{
					if(!heldItem.hasTagCompound()){
						heldItem.setTagCompound(new NBTTagCompound());
					}

					TileEntity te = worldIn.getTileEntity(pos.offset(EnumFacing.DOWN));
					if(te instanceof TeslaCoilTileEntity){
						heldItem.getTagCompound().setLong("c_link", pos.offset(EnumFacing.DOWN).toLong());
						playerIn.sendMessage(new TextComponentString("Beginning linking."));
					}
				}
			}
			return true;
		}
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player){
		return new ItemStack(ModBlocks.teslaCoil, 1);
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune){
		return null;
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos){
		if(!(worldIn.getBlockState(pos.offset(EnumFacing.DOWN)).getBlock() instanceof TeslaCoil)){
			worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
		}
	}

	@Override
	public boolean isOpaqueCube(IBlockState state){
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state){
		return false;
	}
	
	@Override
	public EnumPushReaction getMobilityFlag(IBlockState state){
		return EnumPushReaction.BLOCK;
	}
}
