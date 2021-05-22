package com.Da_Technomancer.crossroads.API;

import com.Da_Technomancer.crossroads.API.alchemy.EnumTransferMode;
import com.Da_Technomancer.crossroads.blocks.heat.HeatCable;
import com.Da_Technomancer.crossroads.blocks.technomancy.TemporalAccelerator;
import com.Da_Technomancer.crossroads.items.alchemy.AbstractGlassware;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.util.Direction;

public class CRProperties extends ESProperties{

	public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
	public static final IntegerProperty FULLNESS = IntegerProperty.create("fullness", 0, 3);
	public static final IntegerProperty SOLID_FULLNESS = IntegerProperty.create("solid_fullness", 0, 3);
	public static final EnumProperty<Direction.Axis> HORIZ_AXIS = EnumProperty.create("horiz_axis", Direction.Axis.class, (Direction.Axis axis) -> axis != null && axis.isHorizontal());

	public static final EnumProperty<HeatCable.Conductors> CONDUCTOR = EnumProperty.create("skin", HeatCable.Conductors.class);
	public static final BooleanProperty CRYSTAL = BooleanProperty.create("crystal");
	public static final DirectionProperty HORIZ_FACING = DirectionProperty.create("horiz_facing", (Direction side) -> side != null && side.getAxis() != Direction.Axis.Y);
	public static final EnumProperty<AbstractGlassware.GlasswareTypes> CONTAINER_TYPE = EnumProperty.create("container_type", AbstractGlassware.GlasswareTypes.class);
//	public static final EnumProperty<MathAxisTileEntity.Arrangement> ARRANGEMENT = EnumProperty.create("arrangement", MathAxisTileEntity.Arrangement.class);
	public static final EnumProperty<TemporalAccelerator.Mode> ACCELERATOR_TARGET = EnumProperty.create("accel_target", TemporalAccelerator.Mode.class);
	public static final IntegerProperty POWER_LEVEL = IntegerProperty.create("power_level", 0, 2);//Used for changing visual model on the beam splitter and redstone axis; Beam Splitter: 0 is no signal, 2 for signal >= 15, 1 for signal in (0, 15); Redstone Axis: 0: No signal, 1: Signal > 0, 2: Signal < 0
	public static final IntegerProperty RATE_SIGNED = IntegerProperty.create("rate", 0, 4);//Used for changing animation speed on stirling engine; 2: no animation; 3: slow forward; 4: fast forward; 1: slow reverse; 0: fast reverse
	public static final IntegerProperty LAYERS = IntegerProperty.create("layers", 1, 4);//Used for density plates
	public static final IntegerProperty AGE_3 = IntegerProperty.create("age", 1, 3);//Used for wheezeworts

	//Individual properties for each direction- for blocks with a conduit-like shape
	public static final BooleanProperty[] HAS_MATCH_SIDES = new BooleanProperty[] {BooleanProperty.create("down_b"), BooleanProperty.create("up_b"), BooleanProperty.create("north_b"), BooleanProperty.create("south_b"), BooleanProperty.create("west_b"), BooleanProperty.create("east_b")};

	//Only both-none connections
	@SuppressWarnings("unchecked")
	public static final EnumProperty<EnumTransferMode>[] CONDUIT_SIDES_BASE = new EnumProperty[6];
	//All connection types
	@SuppressWarnings("unchecked")
	public static final EnumProperty<EnumTransferMode>[] CONDUIT_SIDES_FULL = new EnumProperty[6];
	//All one-directional (non BOTH) types
	@SuppressWarnings("unchecked")
	public static final EnumProperty<EnumTransferMode>[] CONDUIT_SIDES_SINGLE = new EnumProperty[6];

	static{
		for(Direction dir : Direction.values()){
			int ind = dir.get3DDataValue();
			CONDUIT_SIDES_BASE[ind] = EnumProperty.create(dir.getName(), EnumTransferMode.class, mode -> mode == EnumTransferMode.NONE || mode == EnumTransferMode.BOTH);
			CONDUIT_SIDES_FULL[ind] = EnumProperty.create(dir.getName(), EnumTransferMode.class);
			CONDUIT_SIDES_SINGLE[ind] = EnumProperty.create(dir.getName(), EnumTransferMode.class, mode -> mode != EnumTransferMode.BOTH);
		}
	}
}
