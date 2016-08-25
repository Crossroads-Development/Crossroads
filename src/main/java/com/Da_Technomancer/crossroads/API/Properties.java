package com.Da_Technomancer.crossroads.API;

import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;

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
	public static final PropertyInteger TEXTURE = PropertyInteger.create("text", 0, 3);

}
