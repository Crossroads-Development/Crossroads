package com.Da_Technomancer.crossroads.render.TESR;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.blocks.rotary.RotaryDrill;
import com.Da_Technomancer.crossroads.tileentities.rotary.RotaryDrillTileEntity;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.LazyOptional;
import org.lwjgl.opengl.GL11;

public class RotaryDrillRenderer extends TileEntityRenderer<RotaryDrillTileEntity>{

	private static final ResourceLocation TEXTURE_DRILL = new ResourceLocation("textures/block/iron_block.png");

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
		GlStateManager.translated(x + 0.5F, y + 0.5F, z + 0.5F);
		GlStateManager.disableLighting();//

		//Rotate to face dir
//		GlStateManager.translated(0.5D, 0.5D, 0.5D);
		Direction dir = state.get(ESProperties.FACING);
		if(dir == Direction.DOWN){
			GlStateManager.rotated(180, 0, 0, 1);
		}else if(dir != Direction.UP){
			GlStateManager.rotated(dir.getHorizontalAngle(), 0, 1, 0);
			GlStateManager.rotated(90, 1, 0, 0);
		}
//		GlStateManager.translated(-0.5D, -0.5D, -0.5D);

		//Rotate w/ gear angle
		GlStateManager.rotated(axle.orElseThrow(NullPointerException::new).getAngle(partialTicks) * dir.getAxisDirection().getOffset(), 0F, 1F, 0F);

		Minecraft.getInstance().textureManager.bindTexture(TEXTURE_DRILL);
		if(drill.isGolden()){
			GlStateManager.color3f(1F, 1F, 0.15F);//Color it yellow
		}

		//Render the drill
		BufferBuilder vb = Tessellator.getInstance().getBuffer();

		//Grid aligned layers
		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		
		renderLayer(vb, -8, 10);
		renderLayer(vb, -2, 6);
		renderLayer(vb, 4, 2);
		Tessellator.getInstance().draw();

		//45* aligned layers
		GlStateManager.pushMatrix();
		GlStateManager.rotated(45, 0, 1, 0);
		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		renderLayer(vb, -5, 8);
		renderLayer(vb, 1, 4);

		Tessellator.getInstance().draw();
		GlStateManager.popMatrix();

		if(drill.isGolden()){
			GlStateManager.color3f(1, 1, 1);//Restore color
		}

		GlStateManager.enableLighting();//
		GlStateManager.popMatrix();
	}

	private static void renderLayer(BufferBuilder vb, float bottom, float width){
		bottom /= 16F;
		float height = 3F / 16F;
		float top = bottom + height;
		width /= 16F;

		float start = -width / 2;
		float end = width / 2;
		//Top
		vb.pos(start, top, start).tex(0, 0).endVertex();
		vb.pos(start, top, end).tex(0, width).endVertex();
		vb.pos(end, top, end).tex(width, width).endVertex();
		vb.pos(end, top, start).tex(width, 0).endVertex();

		//Bottom
		vb.pos(start, bottom, start).tex(0, 0).endVertex();
		vb.pos(end, bottom, start).tex(width, 0).endVertex();
		vb.pos(end, bottom, end).tex(width, width).endVertex();
		vb.pos(start, bottom, end).tex(0, width).endVertex();
		
		//side
		vb.pos(start, bottom, start).tex(0, 0).endVertex();
		vb.pos(start, top, start).tex(0, height).endVertex();
		vb.pos(end, top, start).tex(width, height).endVertex();
		vb.pos(end, bottom, start).tex(width, 0).endVertex();

		//side
		vb.pos(start, bottom, end).tex(0, 0).endVertex();
		vb.pos(end, bottom, end).tex(width, 0).endVertex();
		vb.pos(end, top, end).tex(width, height).endVertex();
		vb.pos(start, top, end).tex(0, height).endVertex();

		//side
		vb.pos(start, bottom, end).tex(0, 0).endVertex();
		vb.pos(start, top, end).tex(height, 0).endVertex();
		vb.pos(start, top, start).tex(height, width).endVertex();
		vb.pos(start, bottom, start).tex(0, width).endVertex();

		//side
		vb.pos(end, bottom, end).tex(0, 0).endVertex();
		vb.pos(end, bottom, start).tex(0, width).endVertex();
		vb.pos(end, top, start).tex(height, width).endVertex();
		vb.pos(end, top, end).tex(height, 0).endVertex();
	}
}
