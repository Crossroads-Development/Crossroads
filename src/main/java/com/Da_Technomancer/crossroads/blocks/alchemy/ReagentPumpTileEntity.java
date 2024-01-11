package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.api.Capabilities;
import com.Da_Technomancer.crossroads.api.alchemy.EnumMatterPhase;
import com.Da_Technomancer.crossroads.api.alchemy.EnumTransferMode;
import com.Da_Technomancer.crossroads.api.alchemy.IChemicalHandler;
import com.Da_Technomancer.crossroads.api.alchemy.ReagentHolderTE;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.apache.commons.lang3.tuple.Pair;
import org.joml.Vector3f;

import java.util.Arrays;

public class ReagentPumpTileEntity extends ReagentHolderTE{

	public static final BlockEntityType<ReagentPumpTileEntity> TYPE = CRTileEntity.createType(ReagentPumpTileEntity::new, CRBlocks.reagentPumpCrystal, CRBlocks.reagentPumpGlass);

	private static final Pair<Vector3f, Vector3f>[] RENDER_SHAPE_EDGE = new Pair[6];
	static{
		RENDER_SHAPE_EDGE[0] = Pair.of(new Vector3f(7F/16F, 0, 7F/16F), new Vector3f((16F-7F)/16F, 7F/16F, (16F-7F)/16F));
		RENDER_SHAPE_EDGE[1] = Pair.of(new Vector3f(7F/16F, (16F-7F)/16F, 7F/16F), new Vector3f((16F-7F)/16F, 1, (16F-7F)/16F));
		RENDER_SHAPE_EDGE[2] = Pair.of(new Vector3f(7F/16F, 7F/16F, 0), new Vector3f((16F-7F)/16F, (16F-7F)/16F, 7F/16F));
		RENDER_SHAPE_EDGE[3] = Pair.of(new Vector3f(7F/16F, 7F/16F, (16F-7F)/16), new Vector3f((16F-7F)/16F, (16F-7F)/16F, 1));
		RENDER_SHAPE_EDGE[4] = Pair.of(new Vector3f(0, 7F/16F, 7F/16F), new Vector3f(7F/16F, (16F-7F)/16F, (16F-7F)/16F));
		RENDER_SHAPE_EDGE[5] = Pair.of(new Vector3f((16F-7F)/16, 7F/16F, 7F/16F), new Vector3f(1, (16F-7F)/16F, (16F-7F)/16F));
	}
	@SuppressWarnings("unchecked")//Darn Java, not being able to verify arrays of parameterized types. Bah Humbug!
	protected final LazyOptional<IChemicalHandler>[] neighCache = new LazyOptional[] {LazyOptional.empty(), LazyOptional.empty(), LazyOptional.empty(), LazyOptional.empty(), LazyOptional.empty(), LazyOptional.empty()};

	public ReagentPumpTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
	}

	public ReagentPumpTileEntity(BlockPos pos, BlockState state, boolean glass){
		super(TYPE, pos, state, glass);
	}

	@Override
	protected void performTransfer(){
		performTransfer(true);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side){
		if(cap == Capabilities.CHEMICAL_CAPABILITY){
			return (LazyOptional<T>) chemOpt;
		}
		return super.getCapability(cap, side);
	}

	@Override
	protected EnumTransferMode[] getModes(){
		EnumTransferMode[] output = {EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.INPUT, EnumTransferMode.INPUT, EnumTransferMode.INPUT, EnumTransferMode.INPUT};
		boolean outUp = getBlockState().getValue(CRProperties.ACTIVE);
		if(outUp){
			output[Direction.UP.get3DDataValue()] = EnumTransferMode.OUTPUT;
			output[Direction.DOWN.get3DDataValue()] = EnumTransferMode.INPUT;
		}else{
			output[Direction.UP.get3DDataValue()] = EnumTransferMode.INPUT;
			output[Direction.DOWN.get3DDataValue()] = EnumTransferMode.OUTPUT;
		}
		return output;
	}

	@Override
	public Pair<Vector3f, Vector3f>[] getRenderVolumes(){
		for(EnumMatterPhase phase : EnumMatterPhase.values()){
			if(colorDataOnClient[phase.ordinal()] != null && getBlockState().getBlock() instanceof ReagentPump){
				Pair<Vector3f, Vector3f>[] result = Arrays.copyOf(RENDER_SHAPE_EDGE, RENDER_SHAPE_EDGE.length);
				BlockState state = getBlockState();
				for(int i = 2; i < 6; i++){
					if(!state.getValue(CRProperties.HAS_MATCH_SIDES[i])){
						result[i] = null;
					}
				}
				if(state.getValue(CRProperties.ACTIVE)){
					result[Direction.DOWN.get3DDataValue()] = null;
				}else{
					result[Direction.UP.get3DDataValue()] = null;
				}
				return result;
			}
		}
		return new Pair[0];
	}
}
