package com.Da_Technomancer.crossroads.tileentities.rotary.mechanisms;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.rotary.*;
import com.Da_Technomancer.crossroads.items.itemSets.GearFactory;
import com.Da_Technomancer.crossroads.render.TESR.models.ModelGearOctagon;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MechanismToggleGear extends MechanismSmallGear{

	private final boolean inverted;

	public MechanismToggleGear(boolean inverted){
		this.inverted = inverted;
	}

	@Override
	public void onRedstoneChange(double prevValue, double newValue, GearFactory.GearMaterial mat, @Nullable Direction side, @Nullable Direction.Axis axis, double[] motData, MechanismTileEntity te){
		if((newValue == 0) ^ (prevValue == 0)){
			te.getWorld().playSound(null, te.getPos(), SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 0.3F, (newValue != 0) ^ inverted ? 0.6F : 0.5F);
			RotaryUtil.increaseMasterKey(true);
		}
	}

	@Override
	public boolean hasCap(Capability<?> cap, Direction capSide, GearFactory.GearMaterial mat, @Nullable Direction side, @Nullable Direction.Axis axis, MechanismTileEntity te){
		return ((cap == Capabilities.COG_CAPABILITY && (te.redstoneIn != 0 ^ inverted)) || cap == Capabilities.AXLE_CAPABILITY) && side == capSide;
	}

	@Override
	public void propogate(GearFactory.GearMaterial mat, @Nullable Direction side, @Nullable Direction.Axis axis, MechanismTileEntity te, MechanismTileEntity.SidedAxleHandler handler, IAxisHandler masterIn, byte key, double rotRatioIn, double lastRadius){
		//This mechanism should never be in the axle slot
		if(side == null){
			return;
		}

		if(lastRadius != 0){
			rotRatioIn *= lastRadius * 2D;
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


		TileEntity sideTE = te.getWorld().getTileEntity(te.getPos().offset(side));

		//Don't connect via cogs if disabled
		if((te.redstoneIn != 0) ^ inverted){
			//Other internal gears
			for(int i = 0; i < 6; i++){
				if(i != side.getIndex() && i != side.getOpposite().getIndex() && te.members[i] != null && te.members[i].hasCap(Capabilities.COG_CAPABILITY, Direction.byIndex(i), te.mats[i], Direction.byIndex(i), te.axleAxis, te)){
					te.axleHandlers[i].propogate(masterIn, key, RotaryUtil.getDirSign(side, Direction.byIndex(i)) * handler.rotRatio, .5D, !handler.renderOffset);
				}
			}

			for(int i = 0; i < 6; i++){
				if(i != side.getIndex() && i != side.getOpposite().getIndex()){
					Direction facing = Direction.byIndex(i);
					// Adjacent gears
					TileEntity adjTE = te.getWorld().getTileEntity(te.getPos().offset(facing));
					if(adjTE != null){
						LazyOptional<ICogHandler> cogOpt;
						if((cogOpt = adjTE.getCapability(Capabilities.COG_CAPABILITY, side)).isPresent()){
							cogOpt.orElseThrow(NullPointerException::new).connect(masterIn, key, -handler.rotRatio, .5D, facing.getOpposite(), handler.renderOffset);
						}else if((cogOpt = adjTE.getCapability(Capabilities.COG_CAPABILITY, facing.getOpposite())).isPresent()){
							//Check for large gears
							cogOpt.orElseThrow(NullPointerException::new).connect(masterIn, key, RotaryUtil.getDirSign(side, facing) * handler.rotRatio, .5D, side, handler.renderOffset);
						}
					}

					// Diagonal gears
					TileEntity diagTE = te.getWorld().getTileEntity(te.getPos().offset(facing).offset(side));
					LazyOptional<ICogHandler> cogOpt;
					if(diagTE != null && (cogOpt = diagTE.getCapability(Capabilities.COG_CAPABILITY, facing.getOpposite())).isPresent() && RotaryUtil.canConnectThrough(te.getWorld(), te.getPos().offset(facing), facing.getOpposite(), side)){
						cogOpt.orElseThrow(NullPointerException::new).connect(masterIn, key, -RotaryUtil.getDirSign(side, facing) * handler.rotRatio, .5D, side.getOpposite(), handler.renderOffset);
					}

					if(sideTE != null && (cogOpt = sideTE.getCapability(Capabilities.COG_CAPABILITY, facing)).isPresent()){
						cogOpt.orElseThrow(NullPointerException::new).connect(masterIn, key, -RotaryUtil.getDirSign(side, facing) * rotRatioIn, .5D, side.getOpposite(), handler.renderOffset);
					}
				}
			}
		}

		//Connected block
		if(sideTE != null){
			LazyOptional<IAxisHandler> axisOpt = sideTE.getCapability(Capabilities.AXIS_CAPABILITY, side.getOpposite());
			if(axisOpt.isPresent()){
				axisOpt.orElseThrow(NullPointerException::new).trigger(masterIn, key);
			}
			LazyOptional<ISlaveAxisHandler> saxisOpt = sideTE.getCapability(Capabilities.SLAVE_AXIS_CAPABILITY, side.getOpposite());
			if(saxisOpt.isPresent()){
				masterIn.addAxisToList(saxisOpt.orElseThrow(NullPointerException::new), side.getOpposite());
			}
			LazyOptional<IAxleHandler> axleOpt = sideTE.getCapability(Capabilities.AXLE_CAPABILITY, side.getOpposite());
			if(axleOpt.isPresent()){
				axleOpt.orElseThrow(NullPointerException::new).propogate(masterIn, key, handler.rotRatio, 0, handler.renderOffset);
			}
		}

		//Axle slot
		if(te.axleAxis == side.getAxis() && te.members[6] != null && te.members[6].hasCap(Capabilities.AXLE_CAPABILITY, side, te.mats[6], null, te.axleAxis, te)){
			te.axleHandlers[6].propogate(masterIn, key, handler.rotRatio, 0, handler.renderOffset);
		}
	}

	@Nonnull
	@Override
	public ItemStack getDrop(GearFactory.GearMaterial mat){
		return new ItemStack(inverted ? GearFactory.gearTypes.get(mat).getInvToggleGear() : GearFactory.gearTypes.get(mat).getToggleGear(), 1);
	}

	private final float sHalf = 7F / (16F * (1F + (float) Math.sqrt(2F)));
	private final float sHalfT = .5F / (1F + (float) Math.sqrt(2F));

	@Override
	@OnlyIn(Dist.CLIENT)
	public void doRender(MechanismTileEntity te, float partialTicks, GearFactory.GearMaterial mat, @Nullable Direction side, @Nullable Direction.Axis axis){
		if(side == null){
			return;
		}

		MechanismTileEntity.SidedAxleHandler handler = te.axleHandlers[side.getIndex()];

		GlStateManager.pushMatrix();
		GlStateManager.rotatef(side == Direction.DOWN ? 0 : side == Direction.UP ? 180F : side == Direction.NORTH || side == Direction.EAST ? 90F : -90F, side.getAxis() == Direction.Axis.Z ? 1 : 0, 0, side.getAxis() == Direction.Axis.Z ? 0 : 1);
		float angle = handler.getAngle(partialTicks);
		GlStateManager.translatef(0, -0.4375F, 0);
		GlStateManager.rotatef((float) -side.getAxisDirection().getOffset() * angle, 0F, 1F, 0F);

		float top = 0.0625F;//-.375F;

		if(inverted){
			BufferBuilder vb = Tessellator.getInstance().getBuffer();
			Minecraft.getInstance().textureManager.bindTexture(ModelGearOctagon.RESOURCE);
			GlStateManager.color3f(1, 0, 0);

			float radius = 2F / 16F;

			vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			vb.pos(-radius, top + 0.001F, radius).tex(.5F - radius, .5F + radius).endVertex();
			vb.pos(radius, top + 0.001F, radius).tex(.5F + radius, .5F + radius).endVertex();
			vb.pos(radius, top + 0.001F, -radius).tex(.5F + radius, .5F - radius).endVertex();
			vb.pos(-radius, top + 0.001F, -radius).tex(.5F - radius, .5F - radius).endVertex();
			Tessellator.getInstance().draw();
		}

		if(te.redstoneIn != 0 ^ inverted){
			ModelGearOctagon.render(mat.getColor());
		}else{
			//Render without prongs
			float lHalf = .4375F;

			float lHalfT = .5F;
			float tHeight = 1F / 16F;

			Minecraft.getInstance().textureManager.bindTexture(ModelGearOctagon.RESOURCE);
			BufferBuilder vb = Tessellator.getInstance().getBuffer();

			GlStateManager.color3f(mat.getColor().getRed() / 255F, mat.getColor().getGreen() / 255F, mat.getColor().getBlue() / 255F);

			vb.begin(GL11.GL_POLYGON, DefaultVertexFormats.POSITION_TEX);
			vb.pos(sHalf, top, -lHalf).tex(.5F + sHalfT, .5F - (-lHalfT)).endVertex();
			vb.pos(-sHalf, top, -lHalf).tex(.5F + -sHalfT, .5F - (-lHalfT)).endVertex();
			vb.pos(-lHalf, top, -sHalf).tex(.5F + -lHalfT, .5F - (-sHalfT)).endVertex();
			vb.pos(-lHalf, top, sHalf).tex(.5F + -lHalfT, .5F - (sHalfT)).endVertex();
			vb.pos(-sHalf, top, lHalf).tex(.5F + -sHalfT, .5F - (lHalfT)).endVertex();
			vb.pos(sHalf, top, lHalf).tex(.5F + sHalfT, .5F - (lHalfT)).endVertex();
			vb.pos(lHalf, top, sHalf).tex(.5F + lHalfT, .5F - (sHalfT)).endVertex();
			vb.pos(lHalf, top, -sHalf).tex(.5F + lHalfT, .5F - (-sHalfT)).endVertex();
			Tessellator.getInstance().draw();

			vb.begin(GL11.GL_POLYGON, DefaultVertexFormats.POSITION_TEX);
			vb.pos(lHalf, -top, -sHalf).tex(.5F + lHalfT, .5F - (-sHalfT)).endVertex();
			vb.pos(lHalf, -top, sHalf).tex(.5F + lHalfT, .5F - (sHalfT)).endVertex();
			vb.pos(sHalf, -top, lHalf).tex(.5F + sHalfT, .5F - (lHalfT)).endVertex();
			vb.pos(-sHalf, -top, lHalf).tex(.5F + -sHalfT, .5F - (lHalfT)).endVertex();
			vb.pos(-lHalf, -top, sHalf).tex(.5F + -lHalfT, .5F - (sHalfT)).endVertex();
			vb.pos(-lHalf, -top, -sHalf).tex(.5F + -lHalfT, .5F - (-sHalfT)).endVertex();
			vb.pos(-sHalf, -top, -lHalf).tex(.5F + -sHalfT, .5F - (-lHalfT)).endVertex();
			vb.pos(sHalf, -top, -lHalf).tex(.5F + sHalfT, .5F - (-lHalfT)).endVertex();
			Tessellator.getInstance().draw();

			GlStateManager.color3f((mat.getColor().getRed() - 130F) / 255F, (mat.getColor().getGreen() - 130F) / 255F, (mat.getColor().getBlue() - 130F) / 255F);

			vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			vb.pos(lHalf, -top, sHalf).tex(1F, .5F + -sHalfT).endVertex();
			vb.pos(lHalf, -top, -sHalf).tex(1F, .5F + sHalfT).endVertex();
			vb.pos(lHalf, top, -sHalf).tex(1F - tHeight, .5F + sHalfT).endVertex();
			vb.pos(lHalf, top, sHalf).tex(1F - tHeight, .5F + -sHalfT).endVertex();
			//Tessellator.getInstance().draw();

			//vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			vb.pos(-lHalf, top, sHalf).tex(tHeight, .5F + -sHalfT).endVertex();
			vb.pos(-lHalf, top, -sHalf).tex(tHeight, .5F + sHalfT).endVertex();
			vb.pos(-lHalf, -top, -sHalf).tex(0, .5F + sHalfT).endVertex();
			vb.pos(-lHalf, -top, sHalf).tex(0, .5F + -sHalfT).endVertex();
			//Tessellator.getInstance().draw();

			//vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			vb.pos(sHalf, top, lHalf).tex(.5F + sHalfT, 0).endVertex();
			vb.pos(-sHalf, top, lHalf).tex(.5F + -sHalfT, 0).endVertex();
			vb.pos(-sHalf, -top, lHalf).tex(.5F + -sHalfT, tHeight).endVertex();
			vb.pos(sHalf, -top, lHalf).tex(.5F + sHalfT, tHeight).endVertex();
			//Tessellator.getInstance().draw();

			//vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			vb.pos(sHalf, -top, -lHalf).tex(.5F + sHalfT, 1F - tHeight).endVertex();
			vb.pos(-sHalf, -top, -lHalf).tex(.5F + -sHalfT, 1F - tHeight).endVertex();
			vb.pos(-sHalf, top, -lHalf).tex(.5F + -sHalfT, 1).endVertex();
			vb.pos(sHalf, top, -lHalf).tex(.5F + sHalfT, 1).endVertex();
			//Tessellator.getInstance().draw();

			//vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			vb.pos(sHalf, top, -lHalf).tex(.5F + sHalfT, .5F - -lHalfT).endVertex();
			vb.pos(lHalf, top, -sHalf).tex(.5F + lHalfT, .5F - -sHalfT).endVertex();
			vb.pos(lHalf, -top, -sHalf).tex(.5F + lHalfT, .5F - -sHalfT).endVertex();
			vb.pos(sHalf, -top, -lHalf).tex(.5F + sHalfT, .5F - -lHalfT).endVertex();
			//Tessellator.getInstance().draw();

			//vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			vb.pos(-sHalf, -top, -lHalf).tex(.5F + -sHalfT, .5F - -lHalfT).endVertex();
			vb.pos(-lHalf, -top, -sHalf).tex(.5F + -lHalfT, .5F - -sHalfT).endVertex();
			vb.pos(-lHalf, top, -sHalf).tex(.5F + -lHalfT, .5F - -sHalfT).endVertex();
			vb.pos(-sHalf, top, -lHalf).tex(.5F + -sHalfT, .5F - -lHalfT).endVertex();
			//Tessellator.getInstance().draw();


			//vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			vb.pos(sHalf, -top, lHalf).tex(.5F + sHalfT, .5F - lHalfT).endVertex();
			vb.pos(lHalf, -top, sHalf).tex(.5F + lHalfT, .5F - sHalfT).endVertex();
			vb.pos(lHalf, top, sHalf).tex(.5F + lHalfT, .5F - sHalfT).endVertex();
			vb.pos(sHalf, top, lHalf).tex(.5F + sHalfT, .5F - lHalfT).endVertex();
			//Tessellator.getInstance().draw();

			//vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			vb.pos(-sHalf, top, lHalf).tex(.5F + -sHalfT, .5F - lHalfT).endVertex();
			vb.pos(-lHalf, top, sHalf).tex(.5F + -lHalfT, .5F - sHalfT).endVertex();
			vb.pos(-lHalf, -top, sHalf).tex(.5F + -lHalfT, .5F - sHalfT).endVertex();
			vb.pos(-sHalf, -top, lHalf).tex(.5F + -sHalfT, .5F - lHalfT).endVertex();
			Tessellator.getInstance().draw();
		}

		GlStateManager.popMatrix();
	}
}
