package zmaster587.advancedRocketry.api;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemReed;
import zmaster587.advancedRocketry.Inventory.GuiHandler;
import zmaster587.advancedRocketry.block.BlockBasic;
import zmaster587.advancedRocketry.block.BlockBlastBrick;
import zmaster587.advancedRocketry.block.BlockBlastFurnace;
import zmaster587.advancedRocketry.block.BlockFuelingStation;
import zmaster587.advancedRocketry.block.BlockLaser;
import zmaster587.advancedRocketry.block.BlockLightSource;
import zmaster587.advancedRocketry.block.BlockLinkedHorizontalTexture;
import zmaster587.advancedRocketry.block.BlockMissionControl;
import zmaster587.advancedRocketry.block.BlockMultimine;
import zmaster587.advancedRocketry.block.BlockPlanetSoil;
import zmaster587.advancedRocketry.block.BlockQuartzCrucible;
import zmaster587.advancedRocketry.block.BlockRocketMotor;
import zmaster587.advancedRocketry.block.BlockSeat;
import zmaster587.advancedRocketry.block.BlockTank;
import zmaster587.advancedRocketry.block.BlockrocketBuilder;
import zmaster587.advancedRocketry.block.multiblock.BlockHatch;
import zmaster587.advancedRocketry.block.multiblock.BlockMultiblockMachine;
import zmaster587.advancedRocketry.block.multiblock.BlockMultiblockPlaceHolder;
import zmaster587.advancedRocketry.block.multiblock.BlockRFBattery;
import zmaster587.advancedRocketry.block.plant.BlockAlienLeaves;
import zmaster587.advancedRocketry.block.plant.BlockAlienSapling;
import zmaster587.advancedRocketry.block.plant.BlockAlienWood;
import zmaster587.advancedRocketry.item.ItemBlockMeta;
import zmaster587.advancedRocketry.item.ItemIngredient;
import zmaster587.advancedRocketry.tile.multiblock.TileCrystallizer;
import zmaster587.advancedRocketry.tile.multiblock.TileCuttingMachine;
import zmaster587.advancedRocketry.tile.multiblock.TilePrecisionAssembler;

public class AdvRocketryBlocks {
	public static BlockMissionControl blockMissionComp;

	public static BlockLaser blockSpaceLaser;
	public static BlockMultiblockMachine blockPrecisionAssembler;
	public static BlockBlastFurnace blockBlastFurnace;
	public static BlockBlastBrick blockBlastBrick;
	public static BlockQuartzCrucible blockQuartzCrucible;
	public static BlockMultiblockMachine blockCrystallizer;
	public static BlockMultiblockMachine blockCuttingMachine;
	public static Block blockPlaceHolder;
	public static Block blockHatch;
	public static Block blockRFBattery;
	public static Block blockStructureBlock;

	public static Block launchpad;
	public static Block structureTower;
	public static Block rocketBuilder;
	public static Block genericSeat;
	public static Block blockEngine;
	public static Block blockFuelTank;
	public static Block blockFuelingStation;
	public static Block blockMoonTurf, blockHotTurf;
	public static Block blockMultiMineOre;
	public static BlockLightSource blockLightSource;
	public static Block blockAlienWood, blockAlienLeaves, blockAlienSapling;
	
	public static void initBlocks() {
		launchpad = new BlockLinkedHorizontalTexture(Material.rock).setBlockName("pad").setCreativeTab(CreativeTabs.tabTransport).setBlockTextureName("advancedrocketry:rocketPad");//.setBlockTextureName("advancedrocketry:rocketLaunchPad");//new BlockMulti(Material.rock).setNames(new String[] {"concrete", "launchtower"});
		structureTower = new BlockBasic(Material.rock).setBlockName("structuretower").setCreativeTab(CreativeTabs.tabTransport).setBlockTextureName("advancedrocketry:structuretower");
		rocketBuilder = (BlockrocketBuilder) new BlockrocketBuilder(Material.rock).setBlockName("rocketAssembler").setCreativeTab(CreativeTabs.tabTransport);
		genericSeat = new BlockSeat(Material.circuits).setBlockName("seat").setCreativeTab(CreativeTabs.tabTransport).setBlockTextureName("minecraft:wool_colored_silver");
		blockEngine = new BlockRocketMotor(Material.rock).setBlockName("rocket").setCreativeTab(CreativeTabs.tabTransport);
		blockFuelTank = new BlockTank(Material.rock).setBlockName("fuelTank").setCreativeTab(CreativeTabs.tabTransport);
		blockFuelingStation = new BlockFuelingStation(Material.rock).setBlockName("fuelStation").setCreativeTab(CreativeTabs.tabTransport);
		
		blockMoonTurf = new BlockPlanetSoil().setHardness(0.5F).setStepSound(Block.soundTypeGravel).setBlockName("turf").setBlockTextureName("advancedrocketry:moon_turf");
		blockHotTurf = new BlockPlanetSoil().setMapColor(MapColor.netherrackColor).setHardness(0.5F).setStepSound(Block.soundTypeGravel).setBlockName("hotDryturf").setBlockTextureName("advancedrocketry:hotdry_turf");
		
		blockMultiMineOre = new BlockMultimine().setBlockName("multimine");
		blockHatch = new BlockHatch().setBlockName("hatch").setCreativeTab(CreativeTabs.tabTransport);
		blockPlaceHolder = new BlockMultiblockPlaceHolder().setBlockName("placeHolder").setBlockTextureName("advancedrocketry:machineGeneric");
		blockRFBattery = new BlockRFBattery().setBlockName("rfBattery").setBlockTextureName("advancedrocketry:batteryRF").setCreativeTab(CreativeTabs.tabTransport);
		blockStructureBlock = new BlockBasic(Material.rock).setBlockName("structureMachine").setBlockTextureName("advancedrocketry:structureBlock").setCreativeTab(CreativeTabs.tabTransport);
		blockAlienWood = new BlockAlienWood().setBlockName("log").setBlockTextureName("advancedrocketry:log");
		blockAlienLeaves = new BlockAlienLeaves().setBlockName("leaves2").setBlockTextureName("leaves");
		blockAlienSapling = new BlockAlienSapling().setBlockName("sapling").setBlockTextureName("advancedrocketry:sapling");
		
		blockMissionComp = new BlockMissionControl();
		blockLightSource = new BlockLightSource();
		blockSpaceLaser = new BlockLaser();
		blockBlastFurnace = new BlockBlastFurnace();
		blockBlastBrick = new BlockBlastBrick();
		blockQuartzCrucible = new BlockQuartzCrucible();

		blockPrecisionAssembler = (BlockMultiblockMachine)new BlockMultiblockMachine(TilePrecisionAssembler.class, GuiHandler.guiId.Assembler.ordinal()).setBlockName("precisionAssemblingMachine");
		blockPrecisionAssembler.setFrontTexture("advancedrocketry:PrecisionAssemblerFront", "advancedrocketry:PrecisionAssemblerFront_Active");
		blockPrecisionAssembler.setSideTexture("advancedrocketry:machineGeneric");

		blockCuttingMachine = (BlockMultiblockMachine)new BlockMultiblockMachine(TileCuttingMachine.class, GuiHandler.guiId.CuttingMachine.ordinal()).setBlockName("cuttingMachine");
		blockCuttingMachine.setFrontTexture("advancedrocketry:CuttingMachine", "advancedrocketry:CuttingMachine_active");
		blockCuttingMachine.setSideTexture("advancedrocketry:machineGeneric");

		blockCrystallizer = (BlockMultiblockMachine) new BlockMultiblockMachine(TileCrystallizer.class, GuiHandler.guiId.CRYSTALLIZER.ordinal()).setBlockName("Crystallizer");
		blockCrystallizer.setSideTexture("Advancedrocketry:Crystallizer", "Advancedrocketry:Crystallizer_active");
		blockCrystallizer.setTopTexture("Advancedrocketry:machineGeneric");
		
		GameRegistry.registerBlock(launchpad, "launchpad");
		GameRegistry.registerBlock(rocketBuilder, "rocketBuilder");
		GameRegistry.registerBlock(structureTower, "structureTower");
		GameRegistry.registerBlock(genericSeat, "seat");
		GameRegistry.registerBlock(blockEngine, "rocketmotor");
		GameRegistry.registerBlock(blockFuelTank, "fuelTank");
		GameRegistry.registerBlock(blockFuelingStation, "fuelingStation");
		GameRegistry.registerBlock(blockMoonTurf, "moonTurf");
		GameRegistry.registerBlock(blockHotTurf, "blockHotTurf");
		GameRegistry.registerBlock(blockMultiMineOre, "blockMultiMineOre");
		GameRegistry.registerBlock(blockHatch, ItemBlockMeta.class, "blockHatch");
		GameRegistry.registerBlock(blockPlaceHolder, "blockPlaceholder");
		GameRegistry.registerBlock(blockRFBattery, "rfBattery");
		GameRegistry.registerBlock(blockStructureBlock, "blockStructureBlock");
		GameRegistry.registerBlock(blockMissionComp, "controlComp");
		GameRegistry.registerBlock(blockSpaceLaser, "laserController");
		GameRegistry.registerBlock(blockPrecisionAssembler, "precisionassemblingmachine");
		GameRegistry.registerBlock(blockBlastFurnace, "blastFurnace");
		GameRegistry.registerBlock(blockBlastBrick, "utilBlock");
		GameRegistry.registerBlock(blockQuartzCrucible, "quartzcrucible");
		GameRegistry.registerBlock(blockCrystallizer, "crystallizer");
		GameRegistry.registerBlock(blockCuttingMachine, "cuttingMachine");
		GameRegistry.registerBlock(blockAlienWood, "alienWood");
		GameRegistry.registerBlock(blockAlienLeaves, "alienLeaves");
		GameRegistry.registerBlock(blockAlienSapling, "alienSapling");
	}
}
