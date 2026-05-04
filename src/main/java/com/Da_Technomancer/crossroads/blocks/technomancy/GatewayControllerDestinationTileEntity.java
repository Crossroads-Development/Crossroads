package com.Da_Technomancer.crossroads.blocks.technomancy;

import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.api.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.api.technomancy.GatewayAddress;
import com.Da_Technomancer.crossroads.api.technomancy.GatewaySavedData;
import com.Da_Technomancer.crossroads.api.technomancy.IGateway;
import com.Da_Technomancer.crossroads.api.technomancy.Location;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.ArrayList;

public class GatewayControllerDestinationTileEntity extends GatewayControllerAbstractTileEntity{

	public static final BlockEntityType<GatewayControllerDestinationTileEntity> TYPE = CRTileEntity.createType(GatewayControllerDestinationTileEntity::new, CRBlocks.gatewayControllerDestination);

	public GatewayControllerDestinationTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
	}

	@Override
	public void addInfo(ArrayList<Component> chat, Player player, BlockHitResult hit){
		if(isActive() && address != null){
			//Address of this gateway
			String[] names = new String[4];
			for(int i = 0; i < 4; i++){
				EnumBeamAlignments align = address.getEntry(i);
				if(align == null){
					align = EnumBeamAlignments.NO_MATCH;//Should never happen
				}
				names[i] = align.getLocalName(false);
			}
			chat.add(Component.translatable("tt.crossroads.gateway.chevron.address", names[0], names[1], names[2], names[3]));

			//Chevrons
			boolean dialed = chevrons[3] != null;
			for(int i = 0; i < 4; i++){
				if(dialed){
					names[i] = chevrons[i].getLocalName(false);
				}else{
					if(lastDialed.fullAddress()){
						names[i] = lastDialed.getEntry(i).getLocalName(false);
					}else{
						names[i] = MiscUtil.localize("tt.crossroads.gateway.chevron.none");
					}
				}
			}
			if(dialed){
				chat.add(Component.translatable("tt.crossroads.gateway.chevron.dialed", names[0], names[1], names[2], names[3]));
			}else{
				chat.add(Component.translatable("tt.crossroads.gateway.chevron.prev_dialed", names[0], names[1], names[2], names[3]));
			}
		}
	}

	public void redstoneInput(){
		//If we are not currently dialed to something,
		//connect to the previous gateway with a redstone signal
		if(chevrons[3] == null && lastDialed.fullAddress()){
			Location location = GatewaySavedData.lookupAddress((ServerLevel) level, lastDialed);
			IGateway otherGateway;
			MinecraftServer server = level.getServer();
			if(location != null && (otherGateway = GatewayAddress.evalTE(location, server)) != null){
				otherGateway.dialTo(address, true);//The other gateway assumes the cost
				dialTo(lastDialed, false);
			}else{
				//Invalid address; reset
				chevrons[0] = chevrons[1] = chevrons[2] = chevrons[3] = null;
				lastDialed = new GatewayAddress(chevrons);
				setChanged();
			}
		}
	}
}
