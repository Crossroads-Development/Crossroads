package com.Da_Technomancer.crossroads.gui;

import com.Da_Technomancer.crossroads.API.alchemy.*;
import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.gui.container.AlchemyChartContainer;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class AlchemyChartGuiContainer extends GuiContainer{

	private static final int RADIUS = 136;
	private static final ResourceLocation BACKGROUND = new ResourceLocation(Main.MODID, "textures/gui/container/alchemy_chart_gui.png");
	private static final ResourceLocation NODE_TEXTURE = new ResourceLocation(Main.MODID, "textures/gui/container/nodes.png");

	private final ArrayList<Node> NODES = new ArrayList<>();

	public AlchemyChartGuiContainer(EntityPlayer player, World world){
		super(new AlchemyChartContainer(player, world));
		xSize = 300;
		ySize = 300;
	}

	@Override
	public void initGui(){
		super.initGui();
		NODES.clear();
		placeNode(AlchemyCore.REAGENTS.get(EnumReagents.PHELOSTOGEN.id()), Color.RED, 0);
		placeNode(AlchemyCore.REAGENTS.get(EnumReagents.AETHER.id()), Color.GREEN, 0);
		placeNode(AlchemyCore.REAGENTS.get(EnumReagents.ADAMANT.id()), Color.BLUE, 0);
		for(IElementReagent reag : AlchemyCore.ELEMENTAL_REAGS){
			Color rgbColor = reag.getColor(EnumMatterPhase.GAS);
			placeNode(reag, rgbColor, 1);
		}
	}

	private void placeNode(IReagent reag, Color col, int type){
		float[] hsbColor = Color.RGBtoHSB(col.getRed(), col.getGreen(), col.getBlue(), null);
		double theta = 2D * Math.PI * hsbColor[0];
		theta += Math.PI / 6D;
		NODES.add(new Node((int) (RADIUS * (hsbColor[1] * Math.cos(theta))), (int) (RADIUS * (hsbColor[1] * Math.sin(theta))), type, reag));
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks){
		drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		for(Node node : NODES){
			if(Math.abs(mouseX - node.xPos - xSize / 2 - guiLeft) <= 8 && Math.abs(mouseY - node.yPos - ySize / 2 - guiTop) <= 8){
				drawHoveringText(ImmutableList.of(node.reag.getName()), mouseX, mouseY, fontRenderer);
				break;
			}
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
		GlStateManager.color(1, 1, 1);
		mc.getTextureManager().bindTexture(BACKGROUND);
		int i = (width - xSize) / 2;
		int j = (height - ySize) / 2;
		drawModalRectWithCustomSizedTexture(i, j, 0, 0, xSize, ySize, 300, 300);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
		mc.getTextureManager().bindTexture(NODE_TEXTURE);
		GlStateManager.enableBlend();
		for(int stage = 0; stage < 2; stage++){
			for(Node node : NODES){
				node.drawNode(stage == 1);
			}
		}
		GlStateManager.disableBlend();
		GlStateManager.color(1, 1, 1);
	}

	@Override
	protected void mouseClicked(int x, int y, int button) throws IOException {
		super.mouseClicked(x, y, button);
	}

	@Override
	protected void keyTyped(char key, int keyCode) throws IOException{
		super.keyTyped(key, keyCode);
	}

	private class Node{
		//From center in pixels
		private final int xPos;
		private final int yPos;
		//0: primary, 1: secondary, 2: tertiary
		private final byte type;
		//0~DYNAMIC_REAGENT_COUNT
		private final IReagent reag;

		private Node(int xPos, int yPos, int type, IReagent reag){
			this.xPos = xPos;
			this.yPos = yPos;
			this.type = (byte) type;
			this.reag = reag;
		}

		private void drawNode(boolean secondStage){
			Color col = reag.getColor(EnumMatterPhase.GAS);
			if(secondStage){
				GlStateManager.color((float) col.getRed() / 255F, (float) col.getGreen() / 255F, (float) col.getBlue() / 255F);
				drawModalRectWithCustomSizedTexture(xPos + xSize / 2 - 8, yPos + ySize / 2 - 8, 16 * type, 0, 16, 16, 64, 64);
			}else if(type != 0){
				drawConnect((int) (RADIUS * Math.sqrt(3) / 2D), RADIUS / 2, xPos, yPos, new Color(1, 0, 0, col.getRed() / 255F / 2F));//Phel
				drawConnect((int) -(RADIUS * Math.sqrt(3) / 2D), RADIUS / 2, xPos, yPos, new Color(0, 1, 0, col.getGreen() / 255F / 2F));//Aeth
				drawConnect(0, -RADIUS, xPos, yPos, new Color(0, 0, 1, col.getBlue() / 255F / 2F));//Adam
//				if(type == 2){
//					if(reag.getSecondaryBase() != null){
//						Color alignSecond = reag.getSecondaryBase().getAlignment().getTrueRGB();
//						float[] hsbColor = Color.RGBtoHSB(alignSecond.getRed(), alignSecond.getGreen(), alignSecond.getBlue(), null);
//						double theta = 2D * Math.PI * hsbColor[0];
//						theta += Math.PI / 6D;
//						drawConnect((int) (RADIUS * (hsbColor[1] * Math.cos(theta))), (int) (RADIUS * (hsbColor[1] * Math.sin(theta))), xPos, yPos, new Color((float) alignSecond.getRed() / 255F, (float) alignSecond.getGreen() / 255F, (float) alignSecond.getBlue() / 255F, 200F / 255F));
//					}
//				}
			}
		}

		private void drawConnect(int xStart, int yStart, int xEnd, int yEnd, Color col){
			GlStateManager.color((float) col.getRed() / 255F, (float) col.getGreen() / 255F, (float) col.getBlue() / 255F, (float) col.getAlpha() / 255F);

			xStart += xSize / 2;
			xEnd += xSize / 2;
			yStart += ySize / 2;
			yEnd += ySize / 2;

			Vec3d start = new Vec3d(xEnd - xStart, yEnd - yStart, 0);
			Vec3d cross = start.crossProduct(new Vec3d(0, 0, 1D));
			cross = cross.normalize().scale(8);

			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder buf = tessellator.getBuffer();
			buf.begin(7, DefaultVertexFormats.POSITION_TEX);
			buf.pos(xEnd - cross.x, yEnd - cross.y, 0D).tex(0D, 0.5D).endVertex();
			buf.pos(xEnd + cross.x, yEnd + cross.y, 0D).tex(0D, 0.75D).endVertex();
			buf.pos(xStart + cross.x, yStart + cross.y, 0D).tex(1D, 0.75D).endVertex();
			buf.pos(xStart - cross.x, yStart - cross.y, 0D).tex(1D, 0.5D).endVertex();
			tessellator.draw();

			GlStateManager.color(1, 1, 1, 1);
		}
	}
}
