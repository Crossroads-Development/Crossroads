package com.Da_Technomancer.crossroads.render.TESR;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.render.TESR.models.ModelAxle;
import com.Da_Technomancer.crossroads.render.TESR.models.ModelGearIcositetragon;
import com.Da_Technomancer.crossroads.tileentities.rotary.LargeGearMasterTileEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.LazyOptional;

public class LargeGearRenderer extends TileEntityRenderer<LargeGearMasterTileEntity>{

	private static final ResourceLocation TEXTURE = new ResourceLocation(Crossroads.MODID, "textures/model/gear_24.png");
	private static final ResourceLocation TEXTURE_RIM = new ResourceLocation(Crossroads.MODID, "textures/model/gear_rim.png");
	private static final ModelGearIcositetragon MODEL = new ModelGearIcositetragon();

	@Override
	public void render(LargeGearMasterTileEntity gear, double x, double y, double z, float partialTicks, int destroyStage){
		if(gear.getWorld().getBlockState(gear.getPos()).getBlock() != CRBlocks.largeGearMaster || !gear.getWorld().isBlockLoaded(gear.getPos())){
			return;
		}

		GlStateManager.pushMatrix();
		GlStateManager.pushLightingAttributes();
		GlStateManager.disableLighting();


		GlStateManager.translated(x + .5D, y + .5D, z + .5D);

		Direction facing = gear.getFacing();
		LazyOptional<IAxleHandler> handler = gear.getCapability(Capabilities.AXLE_CAPABILITY, facing);
		float dirMult = facing.getAxisDirection() == Direction.AxisDirection.POSITIVE ? -1 : 1;

		switch(facing){
			case DOWN:
				break;
			case UP:
				GlStateManager.rotated(180F, 0.0F, 0.0F, 1.0F);
				break;
			case EAST:
				GlStateManager.rotated(90F, 0.0F, 0.0F, 1.0F);
				break;
			case WEST:
				GlStateManager.rotated(270F, 0.0F, 0.0F, 1.0F);
				break;
			case NORTH:
				GlStateManager.rotated(90F, 1.0F, 0.0F, 0.0F);
				break;
			case SOUTH:
				GlStateManager.rotated(270F, 1.0F, 0.0F, 0.0F);
				break;
		}

		if(handler.isPresent()){
			GlStateManager.rotated((float) handler.orElseThrow(NullPointerException::new).getAngle(partialTicks) * dirMult, 0F, 1F, 0F);
			GlStateManager.scalef(3, 1, 3);
			MODEL.render(TEXTURE, TEXTURE_RIM, gear.getMember().getColor());

			GlStateManager.scalef(1F / 3F, 1, 1F / 3F);
			if(gear.isRenderedOffset()){
				GlStateManager.rotated(-7.5F, 0, 1, 0);
			}
			ModelAxle.render(gear.getMember().getColor());
		}

		GlStateManager.enableLighting();
		GlStateManager.popAttributes();
		GlStateManager.popMatrix();
	}
}
