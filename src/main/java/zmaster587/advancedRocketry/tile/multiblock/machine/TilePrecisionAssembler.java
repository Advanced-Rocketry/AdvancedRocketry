package zmaster587.advancedRocketry.tile.multiblock.machine;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.util.AudioRegistry;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.api.LibVulpesItems;
import zmaster587.libVulpes.api.material.Material;
import zmaster587.libVulpes.api.material.MaterialRegistry;
import zmaster587.libVulpes.block.BlockMeta;
import zmaster587.libVulpes.client.util.ProgressBarImage;
import zmaster587.libVulpes.inventory.modules.*;
import zmaster587.libVulpes.recipe.NumberedOreDictStack;
import zmaster587.libVulpes.recipe.RecipesMachine;
import zmaster587.libVulpes.tile.multiblock.TileMultiBlock;
import zmaster587.libVulpes.tile.multiblock.TileMultiblockMachine;
import zmaster587.libVulpes.util.IconResource;

import java.util.List;

public class TilePrecisionAssembler extends TileMultiblockMachine implements IModularInventory, IProgressBar {

	public static final Object structure[][][] = new Object[][][]{ {{LibVulpesBlocks.blockStructureBlock, LibVulpesBlocks.blockStructureBlock, LibVulpesBlocks.blockStructureBlock, LibVulpesBlocks.blockStructureBlock}, 
		{LibVulpesBlocks.blockStructureBlock, LibVulpesBlocks.blockStructureBlock, LibVulpesBlocks.blockStructureBlock, LibVulpesBlocks.blockStructureBlock},
		{LibVulpesBlocks.blockStructureBlock, LibVulpesBlocks.blockStructureBlock, LibVulpesBlocks.blockStructureBlock, LibVulpesBlocks.blockStructureBlock}},

		{{LibVulpesBlocks.blockStructureBlock, Blocks.GLASS, Blocks.GLASS, LibVulpesBlocks.blockStructureBlock},
			{LibVulpesBlocks.blockStructureBlock, Blocks.AIR, Blocks.AIR, LibVulpesBlocks.blockStructureBlock},
			{LibVulpesBlocks.blockStructureBlock, LibVulpesBlocks.blockStructureBlock, LibVulpesBlocks.blockStructureBlock, LibVulpesBlocks.blockStructureBlock}},

			{{'c', '*', '*', '*'},
				{'*', "blockCoil", "blockCoil", '*'},
				{'*', LibVulpesBlocks.motors, LibVulpesBlocks.motors, '*'}}};

	@Override
	public Object[][][] getStructure() {
		return structure;
	}
	
	@Override
	public float getTimeMultiplierForBlock(IBlockState state, TileEntity tile) {

		Material material = MaterialRegistry.getMaterialFromItemStack(new ItemStack(state.getBlock(),1, state.getBlock().getMetaFromState(state)));
		if(material == MaterialRegistry.getMaterialFromName("Gold"))
			return 0.9f;
		else if(material == MaterialRegistry.getMaterialFromName("Aluminum"))
			return 0.8f;
		else if(material == MaterialRegistry.getMaterialFromName("Titanium"))
			return 0.75f;
		else if(material == MaterialRegistry.getMaterialFromName("Iridium"))
			return 0.5f;

		return super.getTimeMultiplierForBlock(state, tile);
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
		
		RecipesMachine.getInstance().addRecipe(TilePrecisionAssembler.class, new ItemStack(AdvancedRocketryItems.itemCircuitPlate,1,0), 900, 100, "ingotGold", "dustRedstone", "waferSilicon");
		RecipesMachine.getInstance().addRecipe(TilePrecisionAssembler.class, new ItemStack(AdvancedRocketryItems.itemCircuitPlate,1,1), 900, 100, "ingotGold", Blocks.REDSTONE_BLOCK, "waferSilicon");
		RecipesMachine.getInstance().addRecipe(TilePrecisionAssembler.class, new ItemStack(AdvancedRocketryItems.itemDataUnit, 1, 0), 500, 60, "gemEmerald", basicCircuit, "dustRedstone");
		RecipesMachine.getInstance().addRecipe(TilePrecisionAssembler.class, trackingCircuit, 900, 50, new ItemStack(AdvancedRocketryItems.itemCircuitPlate,1,0), Items.ENDER_EYE, "dustRedstone");
		RecipesMachine.getInstance().addRecipe(TilePrecisionAssembler.class, itemIOBoard, 200, 10, "plateSilicon", "plateGold",  "dustRedstone");
		RecipesMachine.getInstance().addRecipe(TilePrecisionAssembler.class, controlCircuitBoard, 200, 10, "plateSilicon", "plateCopper", "dustRedstone");
		RecipesMachine.getInstance().addRecipe(TilePrecisionAssembler.class, liquidIOBoard, 200, 10, "plateSilicon", new ItemStack(Items.DYE, 1, 4), "dustRedstone");
		RecipesMachine.getInstance().addRecipe(TilePrecisionAssembler.class, new ItemStack(AdvancedRocketryItems.itemUpgrade,1,0), 400, 1, "dustRedstone", Blocks.REDSTONE_TORCH, basicCircuit, controlCircuitBoard);
		RecipesMachine.getInstance().addRecipe(TilePrecisionAssembler.class, new ItemStack(AdvancedRocketryItems.itemUpgrade,1,1), 400, 1, Items.FIRE_CHARGE, "gemDiamond", advancedCircuit, controlCircuitBoard);
		RecipesMachine.getInstance().addRecipe(TilePrecisionAssembler.class, new ItemStack(AdvancedRocketryItems.itemUpgrade,1,2), 400, 1, LibVulpesBlocks.blockMotor, "rodTitanium", advancedCircuit, controlCircuitBoard);
		RecipesMachine.getInstance().addRecipe(TilePrecisionAssembler.class, new ItemStack(AdvancedRocketryItems.itemUpgrade,1,3), 400, 1, Items.LEATHER_BOOTS, Items.FEATHER, advancedCircuit, controlCircuitBoard);
		RecipesMachine.getInstance().addRecipe(TilePrecisionAssembler.class, new ItemStack(AdvancedRocketryItems.itemUpgrade,1,4), 400, 1, LibVulpesItems.itemBattery, AdvancedRocketryItems.itemLens, advancedCircuit, controlCircuitBoard);
		RecipesMachine.getInstance().addRecipe(TilePrecisionAssembler.class, new ItemStack(AdvancedRocketryItems.itemAtmAnalyser), 1000, 1, smallBattery, advancedCircuit, "plateTin", AdvancedRocketryItems.itemLens,  userInterface);
		RecipesMachine.getInstance().addRecipe(TilePrecisionAssembler.class, new ItemStack(AdvancedRocketryItems.itemBiomeChanger), 1000, 1, smallBattery, advancedCircuit, "plateTin", trackingCircuit,  userInterface);
		RecipesMachine.getInstance().addRecipe(TilePrecisionAssembler.class, biomeChanger, 1000, 1, new NumberedOreDictStack("stickCopper", 2), "stickTitanium", new NumberedOreDictStack("waferSilicon", 2), advancedCircuit);
		RecipesMachine.getInstance().addRecipe(TilePrecisionAssembler.class, new ItemStack(AdvancedRocketryBlocks.blockLens,1,0), 100, 1, new ItemStack(Blocks.GLASS, 3), new NumberedOreDictStack("itemLens", 3), "stickIron");
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
	public AxisAlignedBB getRenderBoundingBox() {
		
		return new AxisAlignedBB(pos.add(-4,-4,-4), pos.add(4,4,4));
	}

	@Override
	public List<ModuleBase> getModules(int ID, EntityPlayer player) {
		List<ModuleBase> modules = super.getModules(ID, player);

		int yOffset = 16;
		int xOffset = 65;

		if(world.isRemote)
			modules.add(new ModuleImage(xOffset, yOffset, new IconResource(132, 0, 53, 66, TextureResources.progressBars)));
		modules.add(new ModuleProgress(xOffset + 35, yOffset + 22, 1, new ProgressBarImage(167, 22, 13, 15, 54, 42, 13, 15, EnumFacing.DOWN, TextureResources.progressBars), this));
		modules.add(new ModuleProgress(xOffset + 36, yOffset + 41, 2, new ProgressBarImage(168, 41, 11, 15, 67, 42, 11, 15, EnumFacing.DOWN, TextureResources.progressBars), this));
		modules.add(new ModuleProgress(xOffset + 31, yOffset + 62, 3, new ProgressBarImage(163, 62, 21, 3, 90, 42, 21,  3, EnumFacing.EAST, TextureResources.progressBars), this));

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
	public SoundEvent getSound() {
		return AudioRegistry.precAss;
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