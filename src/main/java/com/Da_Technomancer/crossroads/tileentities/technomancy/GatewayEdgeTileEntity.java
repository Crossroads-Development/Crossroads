package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.Crossroads;
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
			TileEntity te = world.getTileEntity(pos.add(key));
			if(te instanceof GatewayFrameTileEntity){
				((GatewayFrameTileEntity) te).addInfo(chat, player, hit);
			}
		}
	}

	public void reset(){
		key = null;
		markDirty();
		updateContainingBlockInfo();
	}

	public void setKey(BlockPos newKey){
		key = newKey;
		markDirty();
	}

	public float getCircuitRead(){
		if(key == null){
			return 0;
		}
		TileEntity srcTE = world.getTileEntity(key);
		if(srcTE instanceof GatewayFrameTileEntity){
			EnumBeamAlignments[] chev = ((GatewayFrameTileEntity) srcTE).chevrons;
			for(int i = 0; i < chev.length; i++){
				if(chev[i] == null){
					return i;
				}
			}
			return chev.length;
		}
		return 0;
	}

	//Multiblock management

	/**
	 * Called when this block is broken. Disassembles the rest of the multiblock if formed
	 */
	public void dismantle(){
		if(key != null){
			//The rest of the multiblock asks the head to dismantle
			TileEntity te = world.getTileEntity(pos.add(key));
			if(te instanceof GatewayFrameTileEntity){
				((GatewayFrameTileEntity) te).dismantle();
			}
		}
	}

	@Override
	public void read(CompoundNBT nbt){
		super.read(nbt);

		//Generic
		key = nbt.contains("key") ? BlockPos.fromLong(nbt.getLong("key")) : null;
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);

		//Generic
		if(key != null){
			nbt.putLong("key", key.toLong());
		}

		return nbt;
	}
}
