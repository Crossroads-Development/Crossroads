package com.Da_Technomancer.crossroads.render.TESR;

import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.tileentities.technomancy.HamsterWheelTileEntity;
import com.Da_Technomancer.crossroads.tileentities.alchemy.DynamoTileEntity;
import com.Da_Technomancer.crossroads.tileentities.fluid.RotaryPumpTileEntity;
import com.Da_Technomancer.crossroads.tileentities.rotary.SteamTurbineTileEntity;
import com.Da_Technomancer.crossroads.tileentities.heat.HeatingCrucibleTileEntity;
import com.Da_Technomancer.crossroads.tileentities.magic.*;
import com.Da_Technomancer.crossroads.tileentities.rotary.*;
import com.Da_Technomancer.crossroads.tileentities.rotary.mechanisms.MechanismTileEntity;
import com.Da_Technomancer.crossroads.tileentities.technomancy.*;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.client.registry.ClientRegistry;

/** Before anyone asks, the AA at the beginning of the name is so I can easily find it in the list of classes.*/
public class AAModTESR{

	public static void registerBlockRenderer(){

		ClientRegistry.bindTileEntitySpecialRenderer(MechanismTileEntity.class, new MechanismRenderer());
		reg(ModBlocks.rotaryPump);
		ClientRegistry.bindTileEntitySpecialRenderer(RotaryPumpTileEntity.class, new RotaryPumpRenderer());
		reg(ModBlocks.steamTurbine);
		ClientRegistry.bindTileEntitySpecialRenderer(SteamTurbineTileEntity.class, new SteamTurbineRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(LargeGearMasterTileEntity.class, new LargeGearRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(RotaryDrillTileEntity.class, new RotaryDrillRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(ArcaneExtractorTileEntity.class, new BeamRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(QuartzStabilizerTileEntity.class, new BeamRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(CrystallinePrismTileEntity.class, new BeamRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(ArcaneReflectorTileEntity.class, new BeamRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(LensHolderTileEntity.class, new BeamRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(BeamSplitterTileEntity.class, new BeamRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(BeamSplitterBasicTileEntity.class, new BeamRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(BeaconHarnessTileEntity.class, new BeaconHarnessRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(MultiplicationAxisTileEntity.class, new MultiplicationAxisRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(AdditionAxisTileEntity.class, new AdditionAxisRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(MechanicalBeamSplitterTileEntity.class, new BeamRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(HamsterWheelTileEntity.class, new HamsterWheelRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(GatewayFrameTileEntity.class, new GatewayFrameRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(MechanicalArmTileEntity.class, new MechanicalArmRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(HeatingCrucibleTileEntity.class, new HeatingCrucibleRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(DynamoTileEntity.class, new DynamoRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(PrototypingTableTileEntity.class, new PrototypingTableRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(ClockworkStabilizerTileEntity.class, new BeamRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(PrototypeTileEntity.class, new BeamRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(WindTurbineTileEntity.class, new WindTurbineRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(StampMillTileEntity.class, new StampMillRenderer());
	}

	private static void reg(Block block){
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(block), 0, new ModelResourceLocation(block.getRegistryName().toString(), "inventory"));
	}

}