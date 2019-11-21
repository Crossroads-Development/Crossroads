package com.Da_Technomancer.crossroads.tileentities.rotary.mechanisms;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.rotary.IAxisHandler;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.API.rotary.ISlaveAxisHandler;
import com.Da_Technomancer.crossroads.API.rotary.RotaryUtil;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.items.itemSets.GearFactory;
import com.Da_Technomancer.crossroads.render.TESR.models.ModelAxle;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MechanismClutch extends MechanismAxle{
	
	private final boolean inverted;

	public MechanismClutch(boolean inverted){
		this.inverted = inverted;
	}

	private static final VoxelShape[] SHAPES_CLUTCH = new VoxelShape[3];
	static{
		VoxelShape core = Block.makeCuboidShape(4, 4, 4, 12, 12, 12);
		SHAPES_CLUTCH[0] = VoxelShapes.or(SHAPES[0], core);
		SHAPES_CLUTCH[1] = VoxelShapes.or(SHAPES[1], core);
		SHAPES_CLUTCH[2] = VoxelShapes.or(SHAPES[2], core);
	}

	@Override
	public void onRedstoneChange(double prevValue, double newValue, GearFactory.GearMaterial mat, @Nullable Direction side, @Nullable Direction.Axis axis, double[] motData, MechanismTileEntity te){
		if((newValue == 0) ^ (prevValue == 0)){
			te.getWorld().playSound(null, te.getPos(), SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 0.3F, (newValue != 0) ^ inverted ? 0.6F : 0.5F);
			RotaryUtil.increaseMasterKey(true);
		}
	}

	@Override
	public double getCircuitSignal(GearFactory.GearMaterial mat, Direction.Axis axis, double[] motData, MechanismTileEntity te){
		return Math.abs(motData[0]);
	}

	@Override
	public boolean hasCap(Capability<?> cap, Direction capSide, GearFactory.GearMaterial mat, @Nullable Direction side, @Nullable Direction.Axis axis, MechanismTileEntity te){
		return cap == Capabilities.AXLE_CAPABILITY && side == null && capSide.getAxis() == axis && (te.redstoneIn != 0 ^ inverted || capSide.getAxisDirection() == Direction.AxisDirection.NEGATIVE);
	}

	@Override
	public void propogate(GearFactory.GearMaterial mat, @Nullable Direction side, @Nullable Direction.Axis axis, MechanismTileEntity te, MechanismTileEntity.SidedAxleHandler handler, IAxisHandler masterIn, byte key, double rotRatioIn, double lastRadius){
		//This mechanism should always be in the axle slot
		if(side != null){
			return;
		}

		if(rotRatioIn == 0){
			rotRatioIn = 1;
		}


		//If true, this has already been checked.
		if(key == handler.updateKey){
			//If true, there is rotation conflict.
			if(handler.rotRatio != rotRatioIn){
				masterIn.lock();
			}
			return;
		}

		if(masterIn.addToList(handler)){
			return;
		}

		handler.rotRatio = rotRatioIn;
		handler.updateKey = key;

		
		
		for(Direction.AxisDirection direct : Direction.AxisDirection.values()){
			if(direct == Direction.AxisDirection.POSITIVE && te.redstoneIn == 0 ^ inverted){
				continue;
			}

			Direction endDir = Direction.getFacingFromAxis(direct, axis);
			
			if(te.members[endDir.getIndex()] != null){
				//Do internal connection
				if(te.members[endDir.getIndex()].hasCap(Capabilities.AXLE_CAPABILITY, endDir, te.mats[endDir.getIndex()], endDir, axis, te)){
					te.axleHandlers[endDir.getIndex()].propogate(masterIn, key, rotRatioIn, 0, handler.renderOffset);
				}
			}else{
				//Connect externally
				TileEntity endTE = te.getWorld().getTileEntity(te.getPos().offset(endDir));
				Direction oEndDir = endDir.getOpposite();
				if(endTE != null){
					LazyOptional<IAxisHandler> axisOpt = endTE.getCapability(Capabilities.AXIS_CAPABILITY, oEndDir);
					if(axisOpt.isPresent()){
						axisOpt.orElseThrow(NullPointerException::new).trigger(masterIn, key);
					}

					LazyOptional<ISlaveAxisHandler> saxisOpt = endTE.getCapability(Capabilities.SLAVE_AXIS_CAPABILITY, oEndDir);
					if(saxisOpt.isPresent()){
						masterIn.addAxisToList(saxisOpt.orElseThrow(NullPointerException::new), oEndDir);
					}
					LazyOptional<IAxleHandler> axleOpt = endTE.getCapability(Capabilities.AXLE_CAPABILITY, oEndDir);
					if(axleOpt.isPresent()){
						axleOpt.orElseThrow(NullPointerException::new).propogate(masterIn, key, handler.rotRatio, 0, handler.renderOffset);
					}
				}
			}
		}
	}

	@Nonnull
	@Override
	public ItemStack getDrop(GearFactory.GearMaterial mat){
		return new ItemStack(inverted ? GearFactory.gearTypes.get(mat).getInvClutch() : GearFactory.gearTypes.get(mat).getClutch(), 1);
	}

	@Override
	public VoxelShape getBoundingBox(@Nullable Direction side, @Nullable Direction.Axis axis){
		return side != null || axis == null ? VoxelShapes.empty() : SHAPES_CLUTCH[axis.ordinal()];
	}

	private static final ResourceLocation RESOURCE_ENDS = new ResourceLocation(Crossroads.MODID, "textures/model/axle_end.png");
	private static final ResourceLocation RESOURCE_SIDE = new ResourceLocation(Crossroads.MODID, "textures/model/clutch.png");
	private static final ResourceLocation RESOURCE_SIDE_INV = new ResourceLocation(Crossroads.MODID, "textures/model/clutch_inv.png");

	@Override
	@OnlyIn(Dist.CLIENT)
	public void doRender(MechanismTileEntity te, float partialTicks, GearFactory.GearMaterial mat, @Nullable Direction side, @Nullable Direction.Axis axis){
		if(axis == null){
			return;
		}

		MechanismTileEntity.SidedAxleHandler handler = te.axleHandlers[6];

		GlStateManager.pushMatrix();
		GlStateManager.rotatef(axis == Direction.Axis.Y ? 0 : 90F, axis == Direction.Axis.Z ? 1 : 0, 0, axis == Direction.Axis.X ? -1 : 0);
		
		//Clutch mechanism
		BufferBuilder vb = Tessellator.getInstance().getBuffer();

		Minecraft.getInstance().textureManager.bindTexture(RESOURCE_ENDS);
		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(-0.25F, 0, -0.25F).tex(0, 0).endVertex();
		vb.pos(0.25F, 0, -0.25F).tex(1, 0).endVertex();
		vb.pos(0.25F, 0, 0.25F).tex(1, 1).endVertex();
		vb.pos(-0.25F, 0, 0.25F).tex(0, 1).endVertex();

		vb.pos(-0.25F, 0.4998F, 0.25F).tex(0, 1).endVertex();
		vb.pos(0.25F, 0.4998F, 0.25F).tex(1, 1).endVertex();
		vb.pos(0.25F, 0.4998F, -0.25F).tex(1, 0).endVertex();
		vb.pos(-0.25F, 0.4998F, -0.25F).tex(0, 0).endVertex();
		Tessellator.getInstance().draw();

		Minecraft.getInstance().textureManager.bindTexture(inverted ? RESOURCE_SIDE_INV : RESOURCE_SIDE);
		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(-0.25F, 0.4998F, -0.25F).tex(0, 1).endVertex();
		vb.pos(0.25F, 0.4998F, -0.25F).tex(1, 1).endVertex();
		vb.pos(0.25F, 0, -0.25F).tex(1, 0).endVertex();
		vb.pos(-0.25F, 0, -0.25F).tex(0, 0).endVertex();

		vb.pos(-0.25F, 0, 0.25F).tex(1, 0).endVertex();
		vb.pos(0.25F, 0, 0.25F).tex(0, 0).endVertex();
		vb.pos(0.25F, 0.4998F, 0.25F).tex(0, 1).endVertex();
		vb.pos(-0.25F, 0.4998F, 0.25F).tex(1, 1).endVertex();

		vb.pos(-0.25F, 0, 0.25F).tex(0, 0).endVertex();
		vb.pos(-0.25F, 0.4998F, 0.25F).tex(0, 1).endVertex();
		vb.pos(-0.25F, 0.4998F, -0.25F).tex(1, 1).endVertex();
		vb.pos(-0.25F, 0, -0.25F).tex(1, 0).endVertex();

		vb.pos(0.25F, 0.4998F, -0.25F).tex(0, 1).endVertex();
		vb.pos(0.25F, 0.4998F, 0.25F).tex(1, 1).endVertex();
		vb.pos(0.25F, 0, 0.25F).tex(1, 0).endVertex();
		vb.pos(0.25F, 0, -0.25F).tex(0, 0).endVertex();
		Tessellator.getInstance().draw();
		
		float angle = handler.getAngle(partialTicks);
		GlStateManager.rotatef(angle, 0F, 1F, 0F);
		ModelAxle.render(mat.getColor());
		GlStateManager.popMatrix();
	}
}
