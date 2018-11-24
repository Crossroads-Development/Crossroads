package com.Da_Technomancer.crossroads.render.TESR;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.render.TESR.models.ModelAxle;
import com.Da_Technomancer.essentials.shared.IAxleHandler;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.render.TESR.models.ModelGearIcositetragon;
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

		GlStateManager.pushMatrix();
		GlStateManager.pushAttrib();
		GlStateManager.disableLighting();


		GlStateManager.translate(x + .5D, y + .5D, z + .5D);

		EnumFacing facing = gear.getFacing();
		IAxleHandler handler = gear.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, facing);
		float dirMult = facing.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE ? -1 : 1;

		switch(facing){
			case DOWN:
				break;
			case UP:
				GlStateManager.rotate(180F, 0.0F, 0.0F, 1.0F);
				break;
			case EAST:
				GlStateManager.rotate(90F, 0.0F, 0.0F, 1.0F);
				break;
			case WEST:
				GlStateManager.rotate(270F, 0.0F, 0.0F, 1.0F);
				break;
			case NORTH:
				GlStateManager.rotate(90F, 1.0F, 0.0F, 0.0F);
				break;
			case SOUTH:
				GlStateManager.rotate(270F, 1.0F, 0.0F, 0.0F);
				break;
		}

		if(handler != null){
			GlStateManager.rotate((float) handler.getAngle() * dirMult, 0F, 1F, 0F);
			ModelAxle.render(gear.getMember().getColor());
			GlStateManager.scale(3, 1, 3);
			MODEL.render(TEXTURE, TEXTURE_RIM, gear.getMember().getColor());
		}

		GlStateManager.enableLighting();
		GlStateManager.popAttrib();
		GlStateManager.popMatrix();
	}
}
