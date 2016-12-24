package com.Da_Technomancer.crossroads.API;

import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.util.EnumFacing;

public class Properties{

	public static final UnlistedPropertyBooleanSixArray CONNECT = new UnlistedPropertyBooleanSixArray("connect");
	public static final PropertyBool LIGHT = PropertyBool.create("light");
	public static final PropertyInteger REDSTONE = PropertyInteger.create("redstone", 0, 15);
	public static final PropertyBool REDSTONE_BOOL = PropertyBool.create("redstone_bool");
	public static final PropertyDirection FACING = PropertyDirection.create("facing");
	public static final PropertyInteger FULLNESS = PropertyInteger.create("fullness", 0, 3);
	/**true means X axis, false means Z axis*/
	public static final PropertyBool ORIENT = PropertyBool.create("orient");
	/**0 = copper, 1 = molten copper, 2 = cobble, 3 = lava*/
	public static final PropertyInteger TEXTURE_4 = PropertyInteger.create("text", 0, 3);
	/**0 = none, 1 = ruby, 2 = emerald, 3 = diamond, 4 = pure quartz, 5 = luminescent quartz, 5 = void crystal */
	public static final PropertyInteger TEXTURE_7 = PropertyInteger.create("text_seven", 0, 6);
	/**0 = wheat, 1 = potato, 2 = carrots, 3 = beetroot, 4 = oak, 5 = birch, 6 = spruce, 7 = jungle, 8 = acacia, 9 = dark oak*/
	public static final PropertyInteger PLANT = PropertyInteger.create("plant", 0, 9);
	public static final PropertyBool HEAD = PropertyBool.create("head");
	public static final PropertyEnum<EnumFacing.Axis> AXIS = PropertyEnum.create("axis", EnumFacing.Axis.class);
}
