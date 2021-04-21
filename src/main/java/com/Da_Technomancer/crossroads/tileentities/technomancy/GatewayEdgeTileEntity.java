package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.technomancy.IGateway;
import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.registries.ObjectHolder;

import java.util.ArrayList;

@ObjectHolder(Crossroads.MODID)
public class GatewayEdgeTileEntity extends TileEntity implements IInfoTE{

	@ObjectHolder("gateway_edge")
	private static TileEntityType<GatewayEdgeTileEntity> type = null;

	//These fields will be correct for any portion of a formed multiblock
	private BlockPos key = null;//The relative position of the top center of the multiblock. Null if this is not formedprivate Direction.Axis plane = null;//Legal values are null (unformed), x (for structure in x-y plane), and z (for structure in y-z plane). This should never by y

	public GatewayEdgeTileEntity(){
		super(type);
	}

	@Override
	public void addInfo(ArrayList<ITextComponent> chat, PlayerEntity player, BlockRayTraceResult hit){
		if(key != null){
			//Non-top frames call the top for addInfo
			TileEntity te = level.getBlockEntity(worldPosition.offset(key));
			if(te instanceof IGateway){
				((IGateway) te).addInfo(chat, player, hit);
			}
		}
	}

	public void reset(){
		key = null;
		setChanged();
		clearCache();
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
	public void load(BlockState state, CompoundNBT nbt){
		super.load(state, nbt);

		//Generic
		key = nbt.contains("key") ? BlockPos.of(nbt.getLong("key")) : null;
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt){
		super.save(nbt);

		//Generic
		if(key != null){
			nbt.putLong("key", key.asLong());
		}

		return nbt;
	}
}
