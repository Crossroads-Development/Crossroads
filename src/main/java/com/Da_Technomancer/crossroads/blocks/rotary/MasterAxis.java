package com.Da_Technomancer.crossroads.blocks.rotary;

import com.Da_Technomancer.crossroads.ModConfig;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.tileentities.rotary.MasterAxisTileEntity;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


public class MasterAxis extends BlockContainer{

	public static final PropertyDirection PROPERTYFACING = PropertyDirection.create("facing");

	public static Property speedTiers;

	public MasterAxis() {
		super(Material.IRON);
		setUnlocalizedName("masterAxis");
		setRegistryName("masterAxis");
		GameRegistry.register(this);
		GameRegistry.register(new ItemBlock(this).setRegistryName("masterAxis"));
		this.setCreativeTab(ModItems.tabCrossroads);
		this.setHardness(3);
		//as good a place to stick this as any I guess
		speedTiers = ModConfig.config.get("Rotary", "Speed Tiers", 4, "Higher value means smoother gear rotation, but more packets sent AKA lag. range 1-100 default 4", 1, 100);
	}

	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing blockFaceClickedOn, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
	{
		EnumFacing enumfacing = (placer == null) ? EnumFacing.NORTH : BlockPistonBase.getFacingFromEntity(pos, placer);
		return this.getDefaultState().withProperty(PROPERTYFACING, enumfacing);
	}

	@Override
	public int damageDropped(IBlockState state)
	{
		return 0;
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[] {PROPERTYFACING});
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		EnumFacing facing = EnumFacing.getFront(meta);
		return this.getDefaultState().withProperty(PROPERTYFACING, facing);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		EnumFacing facing = (EnumFacing)state.getValue(PROPERTYFACING);
		int facingbits = facing.getIndex();
		return facingbits;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new MasterAxisTileEntity(EnumFacing.getFront(meta));

	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}

	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
	}
	
	/**
	 * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
	 * blockstate.
	 */
	public IBlockState withRotation(IBlockState state, Rotation rot){
		return state.withProperty(PROPERTYFACING, rot.rotate((EnumFacing)state.getValue(PROPERTYFACING)));
	}

	/**
	 * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
	 * blockstate.
	 */
	public IBlockState withMirror(IBlockState state, Mirror mirrorIn){
		return state.withRotation(mirrorIn.toRotation((EnumFacing)state.getValue(PROPERTYFACING)));
	}

}
