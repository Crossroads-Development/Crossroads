package com.Da_Technomancer.crossroads.client.TESR;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.client.TESR.models.ModelGearIcositetragon;
import com.Da_Technomancer.crossroads.tileentities.rotary.LargeGearMasterTileEntity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class LargeGearRenderer extends TileEntitySpecialRenderer<LargeGearMasterTileEntity>{

	private static final ResourceLocation TEXTURE = new ResourceLocation(Main.MODID, "textures/model/gear_24.png");
	private static final ResourceLocation TEXTURE_RIM = new ResourceLocation(Main.MODID, "textures/model/gear_rim.png");
	private static final ModelGearIcositetragon MODEL = new ModelGearIcositetragon();
	
	@Override
	public void render(LargeGearMasterTileEntity gear, double x, double y, double z, float partialTicks, int destroyStage, float alpha){

		if(gear.getWorld().getBlockState(gear.getPos()).getBlock() != ModBlocks.largeGearMaster || !gear.getWorld().isBlockLoaded(gear.getPos(), false)){
			return;
		}

		IAxleHandler handler;
		GlStateManager.pushMatrix();
		GlStateManager.pushAttrib();
		GlStateManager.disableLighting();
		
		GlStateManager.translate(x + .5D, y + .5D, z + .5D);
		if(gear.hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.DOWN)){
			handler = gear.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.DOWN);
		}else if(gear.hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.UP)){
			GlStateManager.rotate(180F, 0.0F, 0.0F, 1.0F);
			handler = gear.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.UP);
		}else if(gear.hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.EAST)){
			GlStateManager.rotate(90F, 0.0F, 0.0F, 1.0F);
			handler = gear.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.EAST);
		}else if(gear.hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.WEST)){
			GlStateManager.rotate(270F, 0.0F, 0.0F, 1.0F);
			handler = gear.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.WEST);
		}else if(gear.hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.NORTH)){
			GlStateManager.rotate(90F, 1.0F, 0.0F, 0.0F);
			handler = gear.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.NORTH);
		}else{
			GlStateManager.rotate(270F, 1.0F, 0.0F, 0.0F);
			handler = gear.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.SOUTH);
		}
		if(handler != null){
			GlStateManager.rotate((float) handler.getAngle(), 0F, 1F, 0F);
			GlStateManager.scale(3, 1, 3);
			MODEL.render(TEXTURE, TEXTURE_RIM, gear.getMember().getColor());
		}
		
		GlStateManager.enableLighting();
		GlStateManager.popAttrib();
		GlStateManager.popMatrix();

	}
}
