package com.Da_Technomancer.crossroads.tileentities.rotary.mechanisms;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.rotary.GearTypes;
import com.Da_Technomancer.crossroads.API.rotary.RotaryUtil;
import com.Da_Technomancer.crossroads.CommonProxy;
import com.Da_Technomancer.crossroads.render.TESR.models.ModelGearOctagon;
import com.Da_Technomancer.crossroads.items.itemSets.GearFactory;
import com.Da_Technomancer.essentials.shared.IAxisHandler;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MechanismToggleGear implements IMechanism{

	private final boolean inverted;

	public MechanismToggleGear(boolean inverted){
		this.inverted = inverted;
	}

	@Override
	public void onRedstoneChange(double prevValue, double newValue, GearTypes mat, @Nullable EnumFacing side, @Nullable EnumFacing.Axis axis, double[] motData, MechanismTileEntity te){
		if((newValue == 0) ^ (prevValue == 0)){
			te.getWorld().playSound(null, te.getPos(), SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 0.3F, (newValue != 0) ^ inverted ? 0.6F : 0.5F);
			CommonProxy.masterKey++;
		}
	}

	@Override
	public double getInertia(GearTypes mat, @Nullable EnumFacing side, @Nullable EnumFacing.Axis axis){
		// assume each gear is 1/8 of a cubic meter and has a radius of 1/2 meter.
		// mass is rounded to make things nicer for everyone
		return 0.125D * MiscUtil.betterRound(mat.getDensity() / 8, 1);// .125 because r*r/2 so .5*.5/2
	}

	@Override
	public boolean hasCap(Capability<?> cap, EnumFacing capSide, GearTypes mat, @Nullable EnumFacing side, @Nullable EnumFacing.Axis axis, MechanismTileEntity te){
		return ((cap == Capabilities.COG_HANDLER_CAPABILITY && (te.redstoneIn != 0 ^ inverted)) || cap == Capabilities.AXLE_HANDLER_CAPABILITY) && side == capSide;
	}

	@Override
	public void propogate(GearTypes mat, @Nullable EnumFacing side, @Nullable EnumFacing.Axis axis, MechanismTileEntity te, MechanismTileEntity.SidedAxleHandler handler, IAxisHandler masterIn, byte key, double rotRatioIn, double lastRadius){
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

		if(handler.updateKey == 0){
			handler.resetAngle();
		}
		handler.updateKey = key;


		//Connected block
		TileEntity sideTE = te.getWorld().getTileEntity(te.getPos().offset(side));
		if(sideTE != null){
			if(sideTE.hasCapability(Capabilities.AXIS_HANDLER_CAPABILITY, side.getOpposite())){
				sideTE.getCapability(Capabilities.AXIS_HANDLER_CAPABILITY, side.getOpposite()).trigger(masterIn, key);
			}
			if(sideTE.hasCapability(Capabilities.SLAVE_AXIS_HANDLER_CAPABILITY, side.getOpposite())){
				masterIn.addAxisToList(sideTE.getCapability(Capabilities.SLAVE_AXIS_HANDLER_CAPABILITY, side.getOpposite()), side.getOpposite());
			}
			if(sideTE.hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, side.getOpposite())){
				sideTE.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, side.getOpposite()).propogate(masterIn, key, handler.rotRatio, 0);
			}
		}

		//Axle slot
		if(te.axleAxis == side.getAxis() && te.members[6] != null && te.members[6].hasCap(Capabilities.AXLE_HANDLER_CAPABILITY, side, te.mats[6], null, te.axleAxis, te)){
			te.axleHandlers[6].propogate(masterIn, key, handler.rotRatio, 0);
		}

		if((te.redstoneIn == 0) ^ inverted){
			return;//Don't connect via cogs if disabled
		}

		//Other internal gears
		for(int i = 0; i < 6; i++){
			if(i != side.getIndex() && i != side.getOpposite().getIndex() && te.members[i] != null && te.members[i].hasCap(Capabilities.COG_HANDLER_CAPABILITY, EnumFacing.getFront(i), te.mats[i], EnumFacing.getFront(i), te.axleAxis, te)){
				te.axleHandlers[i].propogate(masterIn, key, RotaryUtil.getDirSign(side, EnumFacing.getFront(i)) * handler.rotRatio, .5D);
			}
		}

		for(int i = 0; i < 6; ++i){
			if(i != side.getIndex() && i != side.getOpposite().getIndex()){
				EnumFacing facing = EnumFacing.getFront(i);
				// Adjacent gears
				TileEntity adjTE = te.getWorld().getTileEntity(te.getPos().offset(facing));
				if(adjTE != null && adjTE.hasCapability(Capabilities.COG_HANDLER_CAPABILITY, side)){
					adjTE.getCapability(Capabilities.COG_HANDLER_CAPABILITY, side).connect(masterIn, key, -handler.rotRatio, .5D);
				}

				// Diagonal gears
				TileEntity diagTE = te.getWorld().getTileEntity(te.getPos().offset(facing).offset(side));
				if(diagTE != null && diagTE.hasCapability(Capabilities.COG_HANDLER_CAPABILITY, facing.getOpposite()) && RotaryUtil.canConnectThrough(te.getWorld(), te.getPos().offset(facing), facing.getOpposite(), side)){
					diagTE.getCapability(Capabilities.COG_HANDLER_CAPABILITY, facing.getOpposite()).connect(masterIn, key, RotaryUtil.getDirSign(side, facing.getOpposite()) * handler.rotRatio, .5D);
				}
			}
		}
	}

	@Nonnull
	@Override
	public ItemStack getDrop(GearTypes mat){
		return new ItemStack(GearFactory.TOGGLE_GEARS[mat.ordinal()], 1);
	}

	@Override
	public AxisAlignedBB getBoundingBox(@Nullable EnumFacing side, @Nullable EnumFacing.Axis axis){
		return side == null ? Block.NULL_AABB : MechanismSmallGear.BOUNDING_BOXES[side.getIndex()];
	}

	private final float sHalf = 7F / (16F * (1F + (float) Math.sqrt(2F)));
	private final float sHalfT = .5F / (1F + (float) Math.sqrt(2F));

	@Override
	@SideOnly(Side.CLIENT)
	public void doRender(MechanismTileEntity te, float partialTicks, GearTypes mat, @Nullable EnumFacing side, @Nullable EnumFacing.Axis axis){
		if(side == null){
			return;
		}

		MechanismTileEntity.SidedAxleHandler handler = te.axleHandlers[side.getIndex()];

		GlStateManager.pushMatrix();
		GlStateManager.rotate(side == EnumFacing.DOWN ? 0 : side == EnumFacing.UP ? 180F : side == EnumFacing.NORTH || side == EnumFacing.EAST ? 90F : -90F, side.getAxis() == EnumFacing.Axis.Z ? 1 : 0, 0, side.getAxis() == EnumFacing.Axis.Z ? 0 : 1);
		float angle = (float) (handler.getNextAngle() - handler.getAngle());
		angle *= partialTicks;
		angle += handler.getAngle();
		GlStateManager.translate(0, -0.4375F, 0);
		GlStateManager.rotate((float) -side.getAxisDirection().getOffset() * angle, 0F, 1F, 0F);

		float top = 0.0625F;//-.375F;

		if(inverted){
			BufferBuilder vb = Tessellator.getInstance().getBuffer();
			Minecraft.getMinecraft().renderEngine.bindTexture(ModelGearOctagon.RESOURCE);
			GlStateManager.color(1, 0, 0);

			float radius = 1F / 16F;

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

			Minecraft.getMinecraft().renderEngine.bindTexture(ModelGearOctagon.RESOURCE);
			BufferBuilder vb = Tessellator.getInstance().getBuffer();

			GlStateManager.color(mat.getColor().getRed() / 255F, mat.getColor().getGreen() / 255F, mat.getColor().getBlue() / 255F);

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

			GlStateManager.color((mat.getColor().getRed() - 130F) / 255F, (mat.getColor().getGreen() - 130F) / 255F, (mat.getColor().getBlue() - 130F) / 255F);

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
