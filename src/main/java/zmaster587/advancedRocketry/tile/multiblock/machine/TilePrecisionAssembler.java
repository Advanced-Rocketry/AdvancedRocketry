package zmaster587.advancedRocketry.tile.multiblock.machine;

import java.util.List;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.api.LibVulpesItems;
import zmaster587.libVulpes.block.BlockMeta;
import zmaster587.libVulpes.client.util.ProgressBarImage;
import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.inventory.modules.IProgressBar;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleImage;
import zmaster587.libVulpes.inventory.modules.ModuleProgress;
import zmaster587.libVulpes.recipe.NumberedOreDictStack;
import zmaster587.libVulpes.recipe.RecipesMachine;
import zmaster587.libVulpes.tile.multiblock.TileMultiBlock;
import zmaster587.libVulpes.tile.multiblock.TileMultiblockMachine;
import zmaster587.libVulpes.util.IconResource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class TilePrecisionAssembler extends TileMultiblockMachine implements IModularInventory, IProgressBar {

	public static final Object structure[][][] = new Object[][][]{ {{LibVulpesBlocks.blockStructureBlock, LibVulpesBlocks.blockStructureBlock, LibVulpesBlocks.blockStructureBlock, LibVulpesBlocks.blockStructureBlock}, 
		{LibVulpesBlocks.blockStructureBlock, LibVulpesBlocks.blockStructureBlock, LibVulpesBlocks.blockStructureBlock, LibVulpesBlocks.blockStructureBlock},
		{LibVulpesBlocks.blockStructureBlock, LibVulpesBlocks.blockStructureBlock, LibVulpesBlocks.blockStructureBlock, LibVulpesBlocks.blockStructureBlock}},

		{{LibVulpesBlocks.blockStructureBlock, Blocks.glass, Blocks.glass, LibVulpesBlocks.blockStructureBlock},
			{LibVulpesBlocks.blockStructureBlock, Blocks.air, Blocks.air, LibVulpesBlocks.blockStructureBlock},
			{LibVulpesBlocks.blockStructureBlock, LibVulpesBlocks.blockStructureBlock, LibVulpesBlocks.blockStructureBlock, LibVulpesBlocks.blockStructureBlock}},

			{{'c', '*', '*', '*'},
				{'*', "blockCoil", "blockCoil", '*'},
				{'*', LibVulpesBlocks.motors, LibVulpesBlocks.motors, '*'}}};


	@Override
	public Object[][][] getStructure() {
		return structure;
	}

	@Override
	public void registerRecipes() {
		ItemStack userInterface = new ItemStack(AdvancedRocketryItems.itemMisc, 1,0);
		ItemStack basicCircuit = new ItemStack(AdvancedRocketryItems.itemIC, 1,0);
		ItemStack advancedCircuit = new ItemStack(AdvancedRocketryItems.itemIC, 1,2);
		ItemStack controlCircuitBoard =  new ItemStack(AdvancedRocketryItems.itemIC,1,3);
		ItemStack itemIOBoard = new ItemStack(AdvancedRocketryItems.itemIC,1,4);
		ItemStack liquidIOBoard = new ItemStack(AdvancedRocketryItems.itemIC,1,5);
		ItemStack trackingCircuit = new ItemStack(AdvancedRocketryItems.itemIC,1,1);
		ItemStack biomeChanger = new ItemStack(AdvancedRocketryItems.itemSatellitePrimaryFunction, 1, 5);
		ItemStack smallBattery = new ItemStack(LibVulpesItems.itemBattery,1,0);
		
		RecipesMachine.getInstance().addRecipe(TilePrecisionAssembler.class, new ItemStack(AdvancedRocketryItems.itemCircuitPlate,1,0), 900, 100, Items.gold_ingot, Items.redstone, "waferSilicon");
		RecipesMachine.getInstance().addRecipe(TilePrecisionAssembler.class, new ItemStack(AdvancedRocketryItems.itemCircuitPlate,1,1), 900, 100, Items.gold_ingot, Blocks.redstone_block, "waferSilicon");
		RecipesMachine.getInstance().addRecipe(TilePrecisionAssembler.class, new ItemStack(AdvancedRocketryItems.itemDataUnit, 1, 0), 500, 60, Items.emerald, basicCircuit, Items.redstone);
		RecipesMachine.getInstance().addRecipe(TilePrecisionAssembler.class, trackingCircuit, 900, 50, new ItemStack(AdvancedRocketryItems.itemCircuitPlate,1,0), Items.ender_eye, Items.redstone);
		RecipesMachine.getInstance().addRecipe(TilePrecisionAssembler.class, itemIOBoard, 200, 10, "plateSilicon", "plateGold", Items.redstone);
		RecipesMachine.getInstance().addRecipe(TilePrecisionAssembler.class, controlCircuitBoard, 200, 10, "plateSilicon", "plateCopper", Items.redstone);
		RecipesMachine.getInstance().addRecipe(TilePrecisionAssembler.class, liquidIOBoard, 200, 10, "plateSilicon", new ItemStack(Items.dye, 1, 4), basicCircuit, Items.redstone);
		RecipesMachine.getInstance().addRecipe(TilePrecisionAssembler.class, new ItemStack(AdvancedRocketryItems.itemUpgrade,1,0), 400, 1, Items.redstone, Blocks.redstone_torch, basicCircuit, controlCircuitBoard);
		RecipesMachine.getInstance().addRecipe(TilePrecisionAssembler.class, new ItemStack(AdvancedRocketryItems.itemUpgrade,1,1), 400, 1, Items.fire_charge, Items.diamond, advancedCircuit, controlCircuitBoard);
		RecipesMachine.getInstance().addRecipe(TilePrecisionAssembler.class, new ItemStack(AdvancedRocketryItems.itemUpgrade,1,2), 400, 1, LibVulpesBlocks.blockMotor, "rodTitanium", advancedCircuit, controlCircuitBoard);
		RecipesMachine.getInstance().addRecipe(TilePrecisionAssembler.class, new ItemStack(AdvancedRocketryItems.itemUpgrade,1,3), 400, 1, Items.leather_boots, Items.feather, advancedCircuit, controlCircuitBoard);
		RecipesMachine.getInstance().addRecipe(TilePrecisionAssembler.class, new ItemStack(AdvancedRocketryItems.itemUpgrade,1,4), 400, 1, LibVulpesItems.itemBattery, AdvancedRocketryItems.itemLens, advancedCircuit, controlCircuitBoard);
		RecipesMachine.getInstance().addRecipe(TilePrecisionAssembler.class, new ItemStack(AdvancedRocketryItems.itemAtmAnalyser), 1000, 1, smallBattery, advancedCircuit, "plateTin", AdvancedRocketryItems.itemLens,  userInterface);
		RecipesMachine.getInstance().addRecipe(TilePrecisionAssembler.class, new ItemStack(AdvancedRocketryItems.itemBiomeChanger), 1000, 1, smallBattery, advancedCircuit, "plateTin", trackingCircuit,  userInterface);
		RecipesMachine.getInstance().addRecipe(TilePrecisionAssembler.class, biomeChanger, 1000, 1, new NumberedOreDictStack("stickCopper", 2), "stickTitanium", new NumberedOreDictStack("waferSilicon", 2), advancedCircuit);
		RecipesMachine.getInstance().addRecipe(TilePrecisionAssembler.class, new ItemStack(AdvancedRocketryBlocks.blockLens,1,0), 100, 1, new ItemStack(Blocks.glass, 3), new NumberedOreDictStack("itemLens", 3), "stickIron");
		RecipesMachine.getInstance().addRecipe(TilePrecisionAssembler.class, new ItemStack(AdvancedRocketryItems.itemSpaceElevatorChip,1,0), 100, 1, trackingCircuit, AdvancedRocketryItems.itemSpaceStationChip);
		RecipesMachine.getInstance().addRecipe(TilePrecisionAssembler.class, new ItemStack(AdvancedRocketryItems.itemBeaconFinder,1,0), 100, 1, trackingCircuit, new ItemStack(AdvancedRocketryItems.itemUpgrade,1,4));
		
	}
	
	@Override
	public List<BlockMeta> getAllowableWildCardBlocks() {
		List<BlockMeta> list = super.getAllowableWildCardBlocks();

		list.add(new BlockMeta(LibVulpesBlocks.blockStructureBlock, BlockMeta.WILDCARD));
		list.addAll(TileMultiBlock.getMapping('O'));
		list.addAll(TileMultiBlock.getMapping('I'));
		list.addAll(TileMultiBlock.getMapping('P'));
		list.addAll(TileMultiBlock.getMapping('l'));
		list.addAll(TileMultiBlock.getMapping('L'));

		return list;
	}
	
	@Override
	public ResourceLocation getSound() {
		return TextureResources.sndPrecAss;
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return AxisAlignedBB.getBoundingBox(xCoord -4, yCoord -4, zCoord -4, xCoord + 4, yCoord + 4, zCoord + 4);
	}

	@Override
	public List<ModuleBase> getModules(int ID, EntityPlayer player) {
		List<ModuleBase> modules = super.getModules(ID, player);

		int yOffset = 16;
		int xOffset = 65;

		if(worldObj.isRemote)
			modules.add(new ModuleImage(xOffset, yOffset, new IconResource(132, 0, 53, 66, TextureResources.progressBars)));
		modules.add(new ModuleProgress(xOffset + 35, yOffset + 22, 1, new ProgressBarImage(167, 22, 13, 15, 54, 42, 13, 15, ForgeDirection.DOWN, TextureResources.progressBars), this));
		modules.add(new ModuleProgress(xOffset + 36, yOffset + 41, 2, new ProgressBarImage(168, 41, 11, 15, 67, 42, 11, 15, ForgeDirection.DOWN, TextureResources.progressBars), this));
		modules.add(new ModuleProgress(xOffset + 31, yOffset + 62, 3, new ProgressBarImage(163, 62, 21, 3, 90, 42, 21,  3, ForgeDirection.EAST, TextureResources.progressBars), this));

		return modules;
	}

	@Override
	public int getProgress(int id) {
		if(id == 0) {
			return super.getProgress(id);
		}
		if(id == 1) {
			return Math.min(currentTime, completionTime/3);
		}
		else if(id == 2) {
			int relativeTime = currentTime - (completionTime/3);
			return relativeTime >= 0 ? Math.min(relativeTime, completionTime/3) : 0;
		}

		int relativeTime = currentTime - (2*completionTime/3);

		return relativeTime >= 0 ? Math.min(relativeTime, completionTime/3) : 0; 
	}

	@Override
	public int getTotalProgress(int id) {
		if(id == 0)
			return super.getTotalProgress(id);
		return completionTime/3;
	}

	@Override
	public void setTotalProgress(int id, int progress) {
		if(id == 0)
			super.setTotalProgress(id, progress);
	}

	@Override
	public void setProgress(int id, int progress) {
		if(id == 0)
			super.setProgress(id, progress);
	}

	@Override
	public float getNormallizedProgress(int id) {
		return getProgress(id)/(float)getTotalProgress(id);
	}

	@Override
	public String getMachineName() {
		return "container.precisionassemblingmachine";
	}
}