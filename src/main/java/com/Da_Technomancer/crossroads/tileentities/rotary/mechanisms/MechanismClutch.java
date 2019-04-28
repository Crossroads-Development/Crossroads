package com.Da_Technomancer.crossroads.tileentities.rotary.mechanisms;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.rotary.RotaryUtil;
import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.items.itemSets.GearFactory;
import com.Da_Technomancer.crossroads.render.TESR.models.ModelAxle;
import com.Da_Technomancer.crossroads.API.rotary.IAxisHandler;
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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MechanismClutch extends MechanismAxle{
	
	private final boolean inverted;

	public MechanismClutch(boolean inverted){
		this.inverted = inverted;
	}

	private static final AxisAlignedBB[] BOUNDING_BOXES = new AxisAlignedBB[3];
	static{
		BOUNDING_BOXES[0] = new AxisAlignedBB(0, 0.25D, 0.25D, 1, 0.75D, 0.75D);//X
		BOUNDING_BOXES[1] = new AxisAlignedBB(0.25D, 0, 0.25D, 0.75D, 1, 0.75D);//Y
		BOUNDING_BOXES[2] = new AxisAlignedBB(0.25D, 0.25D, 0, 0.75D, 0.75D, 1);//Z
	}

	@Override
	public void onRedstoneChange(double prevValue, double newValue, GearFactory.GearMaterial mat, @Nullable EnumFacing side, @Nullable EnumFacing.Axis axis, double[] motData, MechanismTileEntity te){
		if((newValue == 0) ^ (prevValue == 0)){
			te.getWorld().playSound(null, te.getPos(), SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 0.3F, (newValue != 0) ^ inverted ? 0.6F : 0.5F);
			RotaryUtil.increaseMasterKey(true);
		}
	}

	@Override
	public double getRatiatorSignal(GearFactory.GearMaterial mat, EnumFacing.Axis axis, double[] motData, MechanismTileEntity te){
		return Math.abs(motData[0]);
	}

	@Override
	public boolean hasCap(Capability<?> cap, EnumFacing capSide, GearFactory.GearMaterial mat, @Nullable EnumFacing side, @Nullable EnumFacing.Axis axis, MechanismTileEntity te){
		return cap == Capabilities.AXLE_CAPABILITY && side == null && capSide.getAxis() == axis && (te.redstoneIn != 0 ^ inverted || capSide.getAxisDirection() == EnumFacing.AxisDirection.NEGATIVE);
	}

	@Override
	public void propogate(GearFactory.GearMaterial mat, @Nullable EnumFacing side, @Nullable EnumFacing.Axis axis, MechanismTileEntity te, MechanismTileEntity.SidedAxleHandler handler, IAxisHandler masterIn, byte key, double rotRatioIn, double lastRadius){
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

		
		
		for(EnumFacing.AxisDirection direct : EnumFacing.AxisDirection.values()){
			if(direct == EnumFacing.AxisDirection.POSITIVE && te.redstoneIn == 0 ^ inverted){
				continue;
			}

			EnumFacing endDir = EnumFacing.getFacingFromAxis(direct, axis);
			
			if(te.members[endDir.getIndex()] != null){
				//Do internal connection
				if(te.members[endDir.getIndex()].hasCap(Capabilities.AXLE_CAPABILITY, endDir, te.mats[endDir.getIndex()], endDir, axis, te)){
					te.axleHandlers[endDir.getIndex()].propogate(masterIn, key, rotRatioIn, 0, handler.renderOffset);
				}
			}else{
				//Connect externally
				TileEntity endTE = te.getWorld().getTileEntity(te.getPos().offset(endDir));
				EnumFacing oEndDir = endDir.getOpposite();
				if(endTE != null){
					if(endTE.hasCapability(Capabilities.AXIS_CAPABILITY, oEndDir)){
						endTE.getCapability(Capabilities.AXIS_CAPABILITY, oEndDir).trigger(masterIn, key);
					}

					if(endTE.hasCapability(Capabilities.SLAVE_AXIS_CAPABILITY, oEndDir)){
						masterIn.addAxisToList(endTE.getCapability(Capabilities.SLAVE_AXIS_CAPABILITY, oEndDir), oEndDir);
					}

					if(endTE.hasCapability(Capabilities.AXLE_CAPABILITY, oEndDir)){
						endTE.getCapability(Capabilities.AXLE_CAPABILITY, oEndDir).propogate(masterIn, key, handler.rotRatio, 0, handler.renderOffset);
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
	public AxisAlignedBB getBoundingBox(@Nullable EnumFacing side, @Nullable EnumFacing.Axis axis){
		return side != null || axis == null ? Block.NULL_AABB : BOUNDING_BOXES[axis.ordinal()];
	}

	private static final ResourceLocation RESOURCE_ENDS = new ResourceLocation(Main.MODID, "textures/model/axle_end.png");
	private static final ResourceLocation RESOURCE_SIDE = new ResourceLocation(Main.MODID, "textures/model/clutch.png");
	private static final ResourceLocation RESOURCE_SIDE_INV = new ResourceLocation(Main.MODID, "textures/model/clutch_inv.png");

	@Override
	@SideOnly(Side.CLIENT)
	public void doRender(MechanismTileEntity te, float partialTicks, GearFactory.GearMaterial mat, @Nullable EnumFacing side, @Nullable EnumFacing.Axis axis){
		if(axis == null){
			return;
		}

		MechanismTileEntity.SidedAxleHandler handler = te.axleHandlers[6];

		GlStateManager.pushMatrix();
		GlStateManager.rotate(axis == EnumFacing.Axis.Y ? 0 : 90F, axis == EnumFacing.Axis.Z ? 1 : 0, 0, axis == EnumFacing.Axis.X ? -1 : 0);
		
		//Clutch mechanism
		BufferBuilder vb = Tessellator.getInstance().getBuffer();

		Minecraft.getMinecraft().renderEngine.bindTexture(RESOURCE_ENDS);
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

		Minecraft.getMinecraft().renderEngine.bindTexture(inverted ? RESOURCE_SIDE_INV : RESOURCE_SIDE);
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
		GlStateManager.rotate(angle, 0F, 1F, 0F);
		ModelAxle.render(mat.getColor());
		GlStateManager.popMatrix();
	}
}
