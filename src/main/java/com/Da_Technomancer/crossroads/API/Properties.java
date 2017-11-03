package com.Da_Technomancer.crossroads.API;

import com.Da_Technomancer.crossroads.API.alchemy.EnumContainerType;

import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.util.EnumFacing;

public class Properties{

	public static final UnlistedPropertyBooleanSixArray CONNECT = new UnlistedPropertyBooleanSixArray("connect");
	public static final UnlistedPropertyIntegerSixArray CONNECT_MODE = new UnlistedPropertyIntegerSixArray("connect_mode");
	public static final UnlistedPropertyIntegerSixArray PORT_TYPE = new UnlistedPropertyIntegerSixArray("port_type");
	public static final PropertyBool LIGHT = PropertyBool.create("light");
	/**
	 * This property should be removed, except removing it will cause all existing fluid tanks to disappear. This property will be fully removed in the next world-breaking update. 
	 */
	@Deprecated
	public static final PropertyInteger REDSTONE = PropertyInteger.create("redstone", 0, 15);
	public static final PropertyBool REDSTONE_BOOL = PropertyBool.create("redstone_bool");
	public static final PropertyDirection FACING = PropertyDirection.create("facing");
	public static final PropertyInteger FULLNESS = PropertyInteger.create("fullness", 0, 3);
	/**true means X axis, false means Z axis*/
	public static final PropertyBool ORIENT = PropertyBool.create("orient");
	/** Depending on context: 
	 * 0: copper, 1: molten copper, 2: cobblestone, 3: lava
	 * 0: copper 1: iron 2: quartz 3: diamond
	 */
	public static final PropertyInteger TEXTURE_4 = PropertyInteger.create("text", 0, 3);
	/**0 = none, 1 = ruby, 2 = emerald, 3 = diamond, 4 = pure quartz, 5 = luminescent quartz, 6 = void crystal */
	public static final PropertyInteger TEXTURE_7 = PropertyInteger.create("text_seven", 0, 6);
	/**0 = wheat, 1 = potato, 2 = carrots, 3 = beetroot, 4 = oak, 5 = birch, 6 = spruce, 7 = jungle, 8 = acacia, 9 = dark oak*/
	public static final PropertyInteger PLANT = PropertyInteger.create("plant", 0, 9);
	public static final PropertyBool HEAD = PropertyBool.create("head");
	public static final PropertyEnum<EnumFacing.Axis> AXIS = PropertyEnum.create("axis", EnumFacing.Axis.class);
	public static final PropertyInteger TEMP_CHANGE = PropertyInteger.create("temp_change", 0, 4);
	public static final PropertyEnum<EnumContainerType> CONTAINER_TYPE = PropertyEnum.create("container_type", EnumContainerType.class);
	public static final PropertyDirection HORIZONTAL_FACING = PropertyDirection.create("horiz_facing", (EnumFacing side) -> side.getAxis() != EnumFacing.Axis.Y);
}
