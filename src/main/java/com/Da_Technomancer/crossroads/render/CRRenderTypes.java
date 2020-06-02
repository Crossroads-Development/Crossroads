package com.Da_Technomancer.crossroads.render;

import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import org.lwjgl.opengl.GL11;

//Stores all the stitched textures and render types
public class CRRenderTypes extends RenderType{

	//Textures
	public static final ResourceLocation WINDMILL_TEXTURE = new ResourceLocation(Crossroads.MODID, "textures/models/wind_turbine_blade.png");
	public static final ResourceLocation BEAM_TEXTURE = new ResourceLocation(Crossroads.MODID, "textures/models/beam.png");

	//Types
	public static final RenderType BEAM_TYPE = RenderType.makeType("beam", DefaultVertexFormats.POSITION_COLOR_TEX, GL11.GL_QUADS, 256, false, true, RenderType.State.getBuilder().cull(RenderState.CULL_DISABLED).texture(new RenderState.TextureState(BEAM_TEXTURE, false, false)).build(false));


	public static void stitchTextures(TextureStitchEvent.Pre event){
		if(event.getMap().getTextureLocation().equals(AtlasTexture.LOCATION_BLOCKS_TEXTURE)) {
			event.addSprite(WINDMILL_TEXTURE);
		}
	}

	//This is a dummy constructor- we need to be in a subclass to access the protected fields
	//This constructor should never be called- everything is done statically
	private CRRenderTypes(){
		super("cr_dummy", DefaultVertexFormats.BLOCK, GL11.GL_QUADS, 256, false, false, () -> {}, () -> {});
		assert false;
	}
}
