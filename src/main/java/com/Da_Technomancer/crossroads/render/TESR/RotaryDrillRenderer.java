package com.Da_Technomancer.crossroads.render.TESR;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.rotary.RotaryDrill;
import com.Da_Technomancer.crossroads.render.TESR.models.ModelDrill;
import com.Da_Technomancer.crossroads.tileentities.rotary.RotaryDrillTileEntity;
import com.Da_Technomancer.essentials.blocks.EssentialsProperties;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.LazyOptional;

public class RotaryDrillRenderer extends TileEntityRenderer<RotaryDrillTileEntity>{

	private static final ModelDrill model = new ModelDrill();
	private static final ResourceLocation texture = new ResourceLocation(Crossroads.MODID, "textures/model/drill.png");

	@Override
	public void render(RotaryDrillTileEntity drill, double x, double y, double z, float partialTicks, int destroyStage){
		if(!drill.getWorld().isBlockLoaded(drill.getPos())){
			return;
		}

		BlockState state = drill.getWorld().getBlockState(drill.getPos());
		LazyOptional<IAxleHandler> axle = drill.getCapability(Capabilities.AXLE_CAPABILITY, null);

		if(!(state.getBlock() instanceof RotaryDrill) || !axle.isPresent()){
			return;
		}

		GlStateManager.pushMatrix();
		GlStateManager.translated(x, y, z);

		Direction dir = state.get(EssentialsProperties.FACING);

		switch(dir){
			case DOWN:{
				GlStateManager.rotated(180F, 0.0F, 0.0F, 1.0F);
				GlStateManager.translated(-.5F, -1.5F, .5F);
				break;
			}
			case UP:{
				GlStateManager.translated(.5F, -.5F, .5F);
				break;
			}
			case EAST:{
				GlStateManager.rotated(270F, 0.0F, 0.0F, 1.0F);
				GlStateManager.translated(-.5F, -.5F, .5F);
				break;
			}
			case WEST:{
				GlStateManager.rotated(90F, 0.0F, 0.0F, 1.0F);
				GlStateManager.translated(.5F, -1.5F, .5F);
				break;
			}
			case NORTH:{
				GlStateManager.rotated(270F, 1.0F, 0.0F, 0.0F);
				GlStateManager.translated(.5F, -1.5F, .5F);
				break;
			}
			case SOUTH:{
				GlStateManager.rotated(90F, 1.0F, 0.0F, 0.0F);
				GlStateManager.translated(.5F, -.5F, -.5F);
				break;
			}
		}
		GlStateManager.rotated(180F, 0.0F, 0.0F, 1.0F);
		GlStateManager.translated(0F, -2F, 0F);
		GlStateManager.rotated(-axle.orElseThrow(NullPointerException::new).getAngle(partialTicks) * dir.getAxisDirection().getOffset(), 0F, 1F, 0F);
		Minecraft.getInstance().textureManager.bindTexture(texture);
		if(drill.isGolden()){
			GlStateManager.color3f(1F, 1F, 0.15F);
		}
		model.render();
		if(drill.isGolden()){
			GlStateManager.color3f(1, 1, 1);
		}
		GlStateManager.popMatrix();

	}

}
