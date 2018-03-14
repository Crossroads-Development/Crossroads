package com.Da_Technomancer.crossroads.gui;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.API.alchemy.AlchemyCore;
import com.Da_Technomancer.crossroads.API.alchemy.IDynamicReagent;
import com.Da_Technomancer.crossroads.API.alchemy.IElementReagent;
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

public class AlchemyChartGuiContainer extends GuiContainer{

	private static final int RADIUS = 128;
	private static final ResourceLocation BACKGROUND = new ResourceLocation(Main.MODID, "textures/gui/container/alchemy_chart_gui.png");
	private static final ResourceLocation NODE_TEXTURE = new ResourceLocation(Main.MODID, "textures/gui/container/nodes.png");

	private final ArrayList<Node> NODES = new ArrayList<Node>();

	public AlchemyChartGuiContainer(EntityPlayer player, World world){
		super(new AlchemyChartContainer(player, world));
		xSize = 300;
		ySize = 300;
	}

	@Override
	public void initGui(){
		super.initGui();
		NODES.clear();
		for(IElementReagent reag : AlchemyCore.ELEMENTAL_REAGS){
				Color rgbColor = reag.getAlignment().getTrueRGB();
				float[] hsbColor = Color.RGBtoHSB(rgbColor.getRed(), rgbColor.getGreen(), rgbColor.getBlue(), null);
				double theta = 2D * Math.PI * hsbColor[0];
				theta += Math.PI / 6D;
				NODES.add(new Node((int) (RADIUS * (hsbColor[1] * Math.cos(theta))), (int) (RADIUS * (hsbColor[1] * Math.sin(theta))), reag.getLevel(), reag));
			
		}
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
		for(Node node : NODES){
			node.drawNode(false);
		}
		for(Node node : NODES){
			node.drawNode(true);
		}
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
		private final IElementReagent reag;

		private Node(int xPos, int yPos, byte type, IElementReagent reag){
			this.xPos = xPos;
			this.yPos = yPos;
			this.type = type;
			this.reag = reag;
		}

		private void drawNode(boolean secondStage){
			if(secondStage){
				drawModalRectWithCustomSizedTexture(xPos + xSize / 2 - 8, yPos + ySize / 2 - 8, 16 * type, reag.getName().equals(IDynamicReagent.UNKNOWN_NAME) ? 16 : 0, 16, 16, 64, 64);
			}else{

				if(type != 0){
					drawConnect((int) (RADIUS * Math.sqrt(3) / 2D), RADIUS / 2, xPos, yPos, new Color(1, 0, 0, 80F / 255F));//Phel
					drawConnect((int) -(RADIUS * Math.sqrt(3) / 2D), RADIUS / 2, xPos, yPos, new Color(0, 1, 0, 80F / 255F));//Aeth
					drawConnect(0, -RADIUS, xPos, yPos, new Color(0, 0, 1, 80F / 255F));//Adam
					if(type == 2){
						if(reag.getSecondaryBase() != null){
							Color alignSecond = reag.getSecondaryBase().getAlignment().getTrueRGB();
							float[] hsbColor = Color.RGBtoHSB(alignSecond.getRed(), alignSecond.getGreen(), alignSecond.getBlue(), null);
							double theta = 2D * Math.PI * hsbColor[0];
							theta += Math.PI / 6D;
							drawConnect((int) (RADIUS * (hsbColor[1] * Math.cos(theta))), (int) (RADIUS * (hsbColor[1] * Math.sin(theta))), xPos, yPos, new Color((float) alignSecond.getRed() / 255F, (float) alignSecond.getGreen() / 255F, (float) alignSecond.getBlue() / 255F, 200F / 255F));
						}
					}
				}
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
