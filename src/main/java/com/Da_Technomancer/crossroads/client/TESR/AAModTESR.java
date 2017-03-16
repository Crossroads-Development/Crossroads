package com.Da_Technomancer.crossroads.client.TESR;

import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.tileentities.HamsterWheelTileEntity;
import com.Da_Technomancer.crossroads.tileentities.fluid.RotaryPumpTileEntity;
import com.Da_Technomancer.crossroads.tileentities.fluid.SteamTurbineTileEntity;
import com.Da_Technomancer.crossroads.tileentities.magic.ArcaneExtractorTileEntity;
import com.Da_Technomancer.crossroads.tileentities.magic.ArcaneReflectorTileEntity;
import com.Da_Technomancer.crossroads.tileentities.magic.BeaconHarnessTileEntity;
import com.Da_Technomancer.crossroads.tileentities.magic.BeamSplitterBasicTileEntity;
import com.Da_Technomancer.crossroads.tileentities.magic.BeamSplitterTileEntity;
import com.Da_Technomancer.crossroads.tileentities.magic.CrystallinePrismTileEntity;
import com.Da_Technomancer.crossroads.tileentities.magic.LensHolderTileEntity;
import com.Da_Technomancer.crossroads.tileentities.magic.QuartzStabilizerTileEntity;
import com.Da_Technomancer.crossroads.tileentities.rotary.AxleTileEntity;
import com.Da_Technomancer.crossroads.tileentities.rotary.LargeGearMasterTileEntity;
import com.Da_Technomancer.crossroads.tileentities.rotary.RotaryDrillTileEntity;
import com.Da_Technomancer.crossroads.tileentities.rotary.SidedGearHolderTileEntity;
import com.Da_Technomancer.crossroads.tileentities.rotary.ToggleGearTileEntity;
import com.Da_Technomancer.crossroads.tileentities.technomancy.AdditionAxisTileEntity;
import com.Da_Technomancer.crossroads.tileentities.technomancy.BackCounterGearTileEntity;
import com.Da_Technomancer.crossroads.tileentities.technomancy.CounterGearTileEntity;
import com.Da_Technomancer.crossroads.tileentities.technomancy.GatewayFrameTileEntity;
import com.Da_Technomancer.crossroads.tileentities.technomancy.MechanicalBeamSplitterTileEntity;
import com.Da_Technomancer.crossroads.tileentities.technomancy.MultiplicationAxisTileEntity;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.client.registry.ClientRegistry;

/** Before anyone asks, the AA at the beginning of the name is so I can easily find it in the list of classes.*/
public class AAModTESR{

	public static void registerBlockRenderer(){

		ClientRegistry.bindTileEntitySpecialRenderer(SidedGearHolderTileEntity.class, new SidedGearHolderRenderer());
		reg(ModBlocks.rotaryPump);
		ClientRegistry.bindTileEntitySpecialRenderer(RotaryPumpTileEntity.class, new RotaryPumpRenderer());
		reg(ModBlocks.steamTurbine);
		ClientRegistry.bindTileEntitySpecialRenderer(SteamTurbineTileEntity.class, new SteamTurbineRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(LargeGearMasterTileEntity.class, new LargeGearRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(RotaryDrillTileEntity.class, new RotaryDrillRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(ToggleGearTileEntity.class, new ToggleGearRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(ArcaneExtractorTileEntity.class, new BeamRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(QuartzStabilizerTileEntity.class, new BeamRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(CrystallinePrismTileEntity.class, new BeamRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(ArcaneReflectorTileEntity.class, new BeamRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(LensHolderTileEntity.class, new BeamRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(BeamSplitterTileEntity.class, new BeamRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(BeamSplitterBasicTileEntity.class, new BeamRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(AxleTileEntity.class, new AxleRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(BeaconHarnessTileEntity.class, new BeaconHarnessRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(MultiplicationAxisTileEntity.class, new MultiplicationAxisRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(CounterGearTileEntity.class, new CounterGearRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(BackCounterGearTileEntity.class, new BackCounterGearRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(AdditionAxisTileEntity.class, new AdditionAxisRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(MechanicalBeamSplitterTileEntity.class, new BeamRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(HamsterWheelTileEntity.class, new HamsterWheelRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(GatewayFrameTileEntity.class, new GatewayFrameRenderer());
	}

	public static void reg(Block block){
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(block), 0, new ModelResourceLocation(block.getRegistryName().toString(), "inventory"));
	}

}