package com.Da_Technomancer.crossroads.blocks.technomancy;

import com.Da_Technomancer.crossroads.api.technomancy.IGateway;
import com.Da_Technomancer.crossroads.api.templates.IInfoTE;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.ArrayList;

public class GatewayEdgeTileEntity extends BlockEntity implements IInfoTE{

	public static final BlockEntityType<GatewayEdgeTileEntity> TYPE = CRTileEntity.createType(GatewayEdgeTileEntity::new, CRBlocks.gatewayEdge);

	//These fields will be correct for any portion of a formed multiblock
	private BlockPos key = null;//The relative position of the top center of the multiblock. Null if this is not formedprivate Direction.Axis plane = null;//Legal values are null (unformed), x (for structure in x-y plane), and z (for structure in y-z plane). This should never by y

	public GatewayEdgeTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
	}

	@Override
	public void addInfo(ArrayList<Component> chat, Player player, BlockHitResult hit){
		if(key != null){
			//Non-top frames call the top for addInfo
			BlockEntity te = level.getBlockEntity(worldPosition.offset(key));
			if(te instanceof IGateway){
				((IGateway) te).addInfo(chat, player, hit);
			}
		}
	}

	public void reset(){
		key = null;
		setChanged();
//		clearCache();
	}

	public BlockPos getKey(){
		return key;
	}

	public void setKey(BlockPos newKey){
		key = newKey;
		setChanged();
	}

	//Multiblock management

	@Override
	public void load(CompoundTag nbt){
		super.load(nbt);

		//Generic
		key = nbt.contains("key") ? BlockPos.of(nbt.getLong("key")) : null;
	}

	@Override
	public void saveAdditional(CompoundTag nbt){
		super.saveAdditional(nbt);

		//Generic
		if(key != null){
			nbt.putLong("key", key.asLong());
		}

	}
}
