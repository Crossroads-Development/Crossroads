package com.Da_Technomancer.crossroads.render;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

//Stores all the stitched textures and render types
public class CRRenderTypes extends RenderType{

	//Textures

	//Stitched to block atlas
	public static final ResourceLocation WINDMILL_TEXTURE = new ResourceLocation(Crossroads.MODID, "models/wind_turbine_blade");
	public static final ResourceLocation DRILL_TEXTURE = new ResourceLocation("block/iron_block");
	public static final ResourceLocation NODE_GIMBAL_TEXTURE = new ResourceLocation(Crossroads.MODID, "models/gimbal");
	public static final ResourceLocation COPSHOWIUM_TEXTURE = new ResourceLocation(Crossroads.MODID, "block/block_copshowium");
	public static final ResourceLocation QUARTZ_TEXTURE = new ResourceLocation(Crossroads.MODID, "block/block_pure_quartz");
	public static final ResourceLocation CAST_IRON_TEXTURE = new ResourceLocation(Crossroads.MODID, "block/block_cast_iron");
	public static final ResourceLocation AXLE_ENDS_TEXTURE = new ResourceLocation(Crossroads.MODID, "models/axle_end");
	public static final ResourceLocation AXLE_SIDE_TEXTURE = new ResourceLocation(Crossroads.MODID, "models/axle");
	public static final ResourceLocation HAMSTER_TEXTURE = new ResourceLocation(Crossroads.MODID, "models/hamster");
	public static final ResourceLocation GEAR_8_TEXTURE = new ResourceLocation(Crossroads.MODID, "models/gear_oct");
	public static final ResourceLocation GEAR_8_RIM_TEXTURE = new ResourceLocation(Crossroads.MODID, "models/gear_oct_rim");
	public static final ResourceLocation GEAR_8_TOOTH_TEXTURE = new ResourceLocation(Crossroads.MODID, "models/gear_oct_tooth");
	public static final ResourceLocation GEAR_24_TEXTURE = new ResourceLocation(Crossroads.MODID, "models/gear_24");
	public static final ResourceLocation GEAR_24_RIM_TEXTURE = new ResourceLocation(Crossroads.MODID, "models/gear_24_rim");
	public static final ResourceLocation GEAR_24_TOOTH_TEXTURE = new ResourceLocation(Crossroads.MODID, "models/gear_24_tooth");
	public static final ResourceLocation CLUTCH_END_TEXTURE = new ResourceLocation(Crossroads.MODID, "models/clutch_end");
	public static final ResourceLocation CLUTCH_SIDE_TEXTURE = new ResourceLocation(Crossroads.MODID, "models/clutch_side");
	public static final ResourceLocation CLUTCH_SIDE_INVERTED_TEXTURE = new ResourceLocation(Crossroads.MODID, "models/clutch_side_inv");
	public static final ResourceLocation GATEWAY_TEXTURE = new ResourceLocation(Crossroads.MODID, "models/gateway");
	public static final ResourceLocation AXLE_MOUNT_TEXTURE = new ResourceLocation(Crossroads.MODID, "models/axle_mount");
	public static final ResourceLocation AXLE_MOUNT_OCT_TEXTURE = new ResourceLocation(Crossroads.MODID, "models/axle_mount_oct");
	public static final ResourceLocation AXLE_MOUNT_RIM_TEXTURE = new ResourceLocation(Crossroads.MODID, "models/axle_mount_rim");
	public static final ResourceLocation BRONZE_TEXTURE = new ResourceLocation(Crossroads.MODID, "block/block_bronze");
	public static final ResourceLocation BEAM_CANNON_BARREL_TEXTURE = new ResourceLocation(Crossroads.MODID, "models/beam_cannon");
	public static final ResourceLocation ITEM_CANNON_BARREL_TEXTURE = new ResourceLocation(Crossroads.MODID, "models/item_cannon");
	public static final ResourceLocation EMBRYO_TEXTURE = new ResourceLocation(Crossroads.MODID, "models/embryo");
	public static final ResourceLocation VILLAGER_BRAIN_TEXTURE = new ResourceLocation(Crossroads.MODID, "models/villager_brain");
	public static final ResourceLocation REAGENT_TEXTURE = new ResourceLocation(Crossroads.MODID, "models/reagent_fill");

	//Stitched to beam atlas
	public static final ResourceLocation BEAM_TEXTURE = new ResourceLocation(Crossroads.MODID, "textures/models/beam.png");

	//Stitched to flux sink atlas
	public static final ResourceLocation FLUX_SINK_TEXTURE = new ResourceLocation(Crossroads.MODID, "textures/models/flux_sink.png");

	//Stitched to area-of-effect atlas
	public static final ResourceLocation AREA_OVERLAY_TEXTURE = new ResourceLocation(Crossroads.MODID, "textures/models/field.png");

	//Stitched to flux transfer type
	public static final ResourceLocation FLUX_EXTRUSION_TEXTURE = new ResourceLocation(Crossroads.MODID, "textures/models/flux_extrusion.png");

	//Stitched to beam info overlay atlas
	public static final ResourceLocation BEAM_INFO_TEXTURE = new ResourceLocation(Crossroads.MODID, "textures/gui/beam_info.png");

	//Stitched to the flame core atlas
	public static final ResourceLocation FLAME_CORE_TEXTURE = new ResourceLocation(Crossroads.MODID, "textures/entities/flame.png");


	//Types
	public static final RenderType BEAM_TYPE = RenderType.create("cr_beam", DefaultVertexFormat.POSITION_COLOR_TEX, VertexFormat.Mode.QUADS, 256, false, true, RenderType.CompositeState.builder().setShaderState(RenderStateShard.POSITION_COLOR_TEX_SHADER).setCullState(RenderStateShard.NO_CULL).setTextureState(new RenderStateShard.TextureStateShard(BEAM_TEXTURE, false, false)).setTransparencyState(CRConfig.beamTransparent.get() ? TransparencyStateShard.ADDITIVE_TRANSPARENCY : TransparencyStateShard.NO_TRANSPARENCY).createCompositeState(false));
	public static final RenderType FLUX_SINK_TYPE = RenderType.create("cr_flux_sink", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 256, false, true, RenderType.CompositeState.builder().setShaderState(RenderStateShard.POSITION_COLOR_TEX_LIGHTMAP_SHADER).setCullState(RenderStateShard.NO_CULL).setTextureState(new RenderStateShard.TextureStateShard(FLUX_SINK_TEXTURE, false, false)).setLightmapState(RenderStateShard.LIGHTMAP).setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY).createCompositeState(false));
	public static final RenderType AREA_OVERLAY_TYPE = RenderType.create("cr_area_overlay", DefaultVertexFormat.POSITION_COLOR_TEX, VertexFormat.Mode.QUADS, 256, false, true, RenderType.CompositeState.builder().setShaderState(RenderStateShard.POSITION_COLOR_TEX_SHADER).setCullState(RenderStateShard.NO_CULL).setTextureState(new RenderStateShard.TextureStateShard(AREA_OVERLAY_TEXTURE, false, false)).setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY).createCompositeState(false));
	public static final RenderType ELECTRIC_ARC_TYPE = RenderType.create("cr_electric_arc", DefaultVertexFormat.POSITION_COLOR_LIGHTMAP, VertexFormat.Mode.QUADS, 256, false, true, RenderType.CompositeState.builder().setShaderState(RenderStateShard.POSITION_COLOR_LIGHTMAP_SHADER).setCullState(RenderStateShard.NO_CULL).setLightmapState(RenderStateShard.LIGHTMAP).createCompositeState(false));
	public static final RenderType FLUX_TRANSFER_TYPE = RenderType.create("cr_flux_extrusion", DefaultVertexFormat.POSITION_COLOR_TEX, VertexFormat.Mode.QUADS, 256, false, true, RenderType.CompositeState.builder().setShaderState(RenderStateShard.POSITION_COLOR_TEX_SHADER).setCullState(RenderStateShard.NO_CULL).setTextureState(new RenderStateShard.TextureStateShard(FLUX_EXTRUSION_TEXTURE, false, false)).setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY).createCompositeState(false));
	public static final RenderType BEAM_INFO_TYPE = RenderType.create("cr_beam_info", DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 256, false, true, RenderType.CompositeState.builder().setShaderState(RenderStateShard.POSITION_TEX_SHADER).setCullState(RenderStateShard.NO_CULL).setTextureState(new RenderStateShard.TextureStateShard(BEAM_INFO_TEXTURE, false, false)).createCompositeState(false));
	public static final RenderType FLAME_CORE_TYPE = RenderType.create("cr_flame_core", DefaultVertexFormat.POSITION_COLOR_TEX, VertexFormat.Mode.QUADS, 256, false, true, RenderType.CompositeState.builder().setShaderState(RenderStateShard.POSITION_COLOR_TEX_SHADER).setCullState(RenderStateShard.NO_CULL).setTextureState(new RenderStateShard.TextureStateShard(FLAME_CORE_TEXTURE, false, false)).setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY).createCompositeState(false));
	public static final RenderType STABLE_TRANSLUCENT_TYPE = RenderType.create("cr_stable_translucent", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 262144, false, true, RenderType.CompositeState.builder().setLightmapState(RenderStateShard.LIGHTMAP).setShaderState(RenderType.RENDERTYPE_TRANSLUCENT_NO_CRUMBLING_SHADER).setTextureState(RenderStateShard.BLOCK_SHEET_MIPPED).setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY).setOutputState(RenderStateShard.TRANSLUCENT_TARGET).createCompositeState(true));

	//This is a dummy constructor- we need to be in a subclass to access the protected fields
	//This constructor should never be called- everything is done statically
	private CRRenderTypes(){
		super("cr_dummy", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 256, false, false, () -> {}, () -> {});
		assert false;
	}
}
