package com.Da_Technomancer.crossroads.API;

import com.Da_Technomancer.crossroads.tileentities.technomancy.MathAxisTileEntity;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.EnumFacing;

public class Properties{

	public static final UnlistedPropertyBooleanSixArray CONNECT = new UnlistedPropertyBooleanSixArray("connect");
	public static final UnlistedPropertyIntegerSixArray CONNECT_MODE = new UnlistedPropertyIntegerSixArray("connect_mode");
	public static final UnlistedPropertyIntegerSixArray PORT_TYPE = new UnlistedPropertyIntegerSixArray("port_type");
	public static final PropertyBool ACTIVE = PropertyBool.create("active");
	public static final PropertyInteger FULLNESS = PropertyInteger.create("fullness", 0, 3);
	/**
	 * @deprecated Use HORIZ_AXIS instead
	 * true means X axis, false means Z axis
	 */
	@Deprecated
	public static final PropertyBool ORIENT = PropertyBool.create("orient");
	public static final PropertyEnum<EnumFacing.Axis> HORIZ_AXIS = PropertyEnum.create("horiz_axis", EnumFacing.Axis.class, (EnumFacing.Axis axis) -> axis != null && axis.isHorizontal());
	/**
	 * 0: copper 1: iron 2: quartz 3: diamond
	 */
	public static final PropertyInteger TEXTURE_4 = PropertyInteger.create("text", 0, 3);
	public static final PropertyBool CRYSTAL = PropertyBool.create("crystal");
	public static final PropertyDirection HORIZ_FACING = PropertyDirection.create("horiz_facing", (EnumFacing side) -> side != null && side.getAxis() != EnumFacing.Axis.Y);
	public static final PropertyBool CONTAINER_TYPE = PropertyBool.create("container_type");
	public static final PropertyEnum<EnumDyeColor> COLOR = PropertyEnum.create("color", EnumDyeColor.class);
	public static final PropertyEnum<MathAxisTileEntity.Arrangement> ARRANGEMENT = PropertyEnum.create("arrangement", MathAxisTileEntity.Arrangement.class);
}
