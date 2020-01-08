package com.Da_Technomancer.crossroads.API;

import com.Da_Technomancer.crossroads.API.alchemy.EnumTransferMode;
import com.Da_Technomancer.crossroads.blocks.heat.HeatCable;
import com.Da_Technomancer.crossroads.items.alchemy.AbstractGlassware;
import com.Da_Technomancer.crossroads.tileentities.technomancy.MathAxisTileEntity;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.util.Direction;

public class CRProperties{

//	public static final UnlistedPropertyBooleanSixArray CONNECT = new UnlistedPropertyBooleanSixArray("connect");
//	public static final UnlistedPropertyIntegerSixArray CONNECT_MODE = new UnlistedPropertyIntegerSixArray("connect_mode");
//	public static final UnlistedPropertyIntegerSixArray PORT_TYPE = new UnlistedPropertyIntegerSixArray("port_type");
	public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
	public static final IntegerProperty FULLNESS = IntegerProperty.create("fullness", 0, 3);
	public static final EnumProperty<Direction.Axis> HORIZ_AXIS = EnumProperty.create("horiz_axis", Direction.Axis.class, (Direction.Axis axis) -> axis != null && axis.isHorizontal());

	public static final EnumProperty<HeatCable.Conductors> CONDUCTOR = EnumProperty.create("skin", HeatCable.Conductors.class);
	public static final BooleanProperty CRYSTAL = BooleanProperty.create("crystal");
	public static final DirectionProperty HORIZ_FACING = DirectionProperty.create("horiz_facing", (Direction side) -> side != null && side.getAxis() != Direction.Axis.Y);
	public static final EnumProperty<AbstractGlassware.GlasswareTypes> CONTAINER_TYPE = EnumProperty.create("container_type", AbstractGlassware.GlasswareTypes.class);
	public static final EnumProperty<MathAxisTileEntity.Arrangement> ARRANGEMENT = EnumProperty.create("arrangement", MathAxisTileEntity.Arrangement.class);

	public static final BooleanProperty DOWN = BooleanProperty.create("down");
	public static final BooleanProperty UP = BooleanProperty.create("up");
	public static final BooleanProperty NORTH = BooleanProperty.create("north");
	public static final BooleanProperty SOUTH = BooleanProperty.create("south");
	public static final BooleanProperty WEST = BooleanProperty.create("west");
	public static final BooleanProperty EAST = BooleanProperty.create("east");

	@SuppressWarnings("unchecked")
	public static final EnumProperty<EnumTransferMode>[] CONDUIT_SIDES = new EnumProperty[] {EnumProperty.create("down", EnumTransferMode.class), EnumProperty.create("up", EnumTransferMode.class), EnumProperty.create("north", EnumTransferMode.class), EnumProperty.create("south", EnumTransferMode.class), EnumProperty.create("west", EnumTransferMode.class), EnumProperty.create("east", EnumTransferMode.class)};

}
