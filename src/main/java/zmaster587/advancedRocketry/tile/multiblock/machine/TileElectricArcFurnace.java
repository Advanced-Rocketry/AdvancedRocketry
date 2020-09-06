package zmaster587.advancedRocketry.tile.multiblock.machine;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.AdvancedRocketryTileEntityType;
import zmaster587.advancedRocketry.api.Constants;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.util.AudioRegistry;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.api.material.AllowedProducts;
import zmaster587.libVulpes.api.material.Material;
import zmaster587.libVulpes.api.material.MaterialRegistry;
import zmaster587.libVulpes.block.BlockMeta;
import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleProgress;
import zmaster587.libVulpes.recipe.NumberedOreDictStack;
import zmaster587.libVulpes.recipe.RecipesMachine;
import zmaster587.libVulpes.tile.multiblock.TileMultiblockMachine;

import java.util.List;

public class TileElectricArcFurnace extends TileMultiblockMachine implements IModularInventory {


	public static final Object[][][] structure = { 
		{	{null,null,null,null,null},
			{null,'P',AdvancedRocketryBlocks.blockBlastBrick,'P',null},
			{null,AdvancedRocketryBlocks.blockBlastBrick,AdvancedRocketryBlocks.blockBlastBrick,AdvancedRocketryBlocks.blockBlastBrick,null},
			{null,AdvancedRocketryBlocks.blockBlastBrick,'P',AdvancedRocketryBlocks.blockBlastBrick,null},
			{null,null,null,null,null},
		},

		{	{null,AdvancedRocketryBlocks.blockBlastBrick,AdvancedRocketryBlocks.blockBlastBrick,AdvancedRocketryBlocks.blockBlastBrick,null},
			{AdvancedRocketryBlocks.blockBlastBrick, new ResourceLocation(Constants.modId, "blockcoil"),Blocks.AIR, new ResourceLocation(Constants.modId,"blockcoil"),AdvancedRocketryBlocks.blockBlastBrick},
			{AdvancedRocketryBlocks.blockBlastBrick,Blocks.AIR,Blocks.AIR,Blocks.AIR,AdvancedRocketryBlocks.blockBlastBrick},
			{AdvancedRocketryBlocks.blockBlastBrick,Blocks.AIR, new ResourceLocation(Constants.modId,"blockcoil"),Blocks.AIR,AdvancedRocketryBlocks.blockBlastBrick},
			{null,AdvancedRocketryBlocks.blockBlastBrick,AdvancedRocketryBlocks.blockBlastBrick,AdvancedRocketryBlocks.blockBlastBrick,null},
		},

		{	{AdvancedRocketryBlocks.blockBlastBrick,AdvancedRocketryBlocks.blockBlastBrick,AdvancedRocketryBlocks.blockBlastBrick,AdvancedRocketryBlocks.blockBlastBrick,AdvancedRocketryBlocks.blockBlastBrick},
			{AdvancedRocketryBlocks.blockBlastBrick,Blocks.AIR,Blocks.AIR,Blocks.AIR,AdvancedRocketryBlocks.blockBlastBrick},
			{AdvancedRocketryBlocks.blockBlastBrick,Blocks.AIR,Blocks.AIR,Blocks.AIR,AdvancedRocketryBlocks.blockBlastBrick},
			{AdvancedRocketryBlocks.blockBlastBrick,Blocks.AIR,Blocks.AIR,Blocks.AIR,AdvancedRocketryBlocks.blockBlastBrick},
			{AdvancedRocketryBlocks.blockBlastBrick,AdvancedRocketryBlocks.blockBlastBrick,AdvancedRocketryBlocks.blockBlastBrick,AdvancedRocketryBlocks.blockBlastBrick,AdvancedRocketryBlocks.blockBlastBrick},
		},

		{	{AdvancedRocketryBlocks.blockBlastBrick,'*','c','*',AdvancedRocketryBlocks.blockBlastBrick},
			{'*',AdvancedRocketryBlocks.blockBlastBrick,AdvancedRocketryBlocks.blockBlastBrick,AdvancedRocketryBlocks.blockBlastBrick,'*'},
			{'*',AdvancedRocketryBlocks.blockBlastBrick,AdvancedRocketryBlocks.blockBlastBrick,AdvancedRocketryBlocks.blockBlastBrick, '*'},
			{'*',AdvancedRocketryBlocks.blockBlastBrick,AdvancedRocketryBlocks.blockBlastBrick,AdvancedRocketryBlocks.blockBlastBrick, '*'},
			{AdvancedRocketryBlocks.blockBlastBrick,'*','*','*',AdvancedRocketryBlocks.blockBlastBrick},
		},

		{	{AdvancedRocketryBlocks.blockBlastBrick,AdvancedRocketryBlocks.blockBlastBrick,AdvancedRocketryBlocks.blockBlastBrick,AdvancedRocketryBlocks.blockBlastBrick,AdvancedRocketryBlocks.blockBlastBrick},
			{AdvancedRocketryBlocks.blockBlastBrick,AdvancedRocketryBlocks.blockBlastBrick,AdvancedRocketryBlocks.blockBlastBrick,AdvancedRocketryBlocks.blockBlastBrick,AdvancedRocketryBlocks.blockBlastBrick},
			{AdvancedRocketryBlocks.blockBlastBrick,AdvancedRocketryBlocks.blockBlastBrick,AdvancedRocketryBlocks.blockBlastBrick,AdvancedRocketryBlocks.blockBlastBrick,AdvancedRocketryBlocks.blockBlastBrick},
			{AdvancedRocketryBlocks.blockBlastBrick,AdvancedRocketryBlocks.blockBlastBrick,AdvancedRocketryBlocks.blockBlastBrick,AdvancedRocketryBlocks.blockBlastBrick,AdvancedRocketryBlocks.blockBlastBrick},
			{AdvancedRocketryBlocks.blockBlastBrick,AdvancedRocketryBlocks.blockBlastBrick,AdvancedRocketryBlocks.blockBlastBrick,AdvancedRocketryBlocks.blockBlastBrick,AdvancedRocketryBlocks.blockBlastBrick},
		}

	};

	public TileElectricArcFurnace() {
		super(AdvancedRocketryTileEntityType.TILE_ARC_FURNACE);
	}
	
	@Override
	public List<BlockMeta> getAllowableWildCardBlocks() {
		List<BlockMeta> list = super.getAllowableWildCardBlocks();
		list.add(new BlockMeta(LibVulpesBlocks.blockItemInputHatch));
		list.add(new BlockMeta(LibVulpesBlocks.blockItemOutputHatch));
		list.add(new BlockMeta(AdvancedRocketryBlocks.blockBlastBrick));
		return list;
	}

	@Override
	protected void integrateTile(TileEntity tile) {
		super.integrateTile(tile);
	}

	@Override
	public Object[][][] getStructure() {
		return structure;
	}

	@Override
	public SoundEvent getSound() {
		return AudioRegistry.electricArcFurnace;
	}

	@Override
	public String getMachineName() {
		return "tile.electricArcFurnace.name";
	}

	@Override
	public float getTimeMultiplierForBlock(BlockState state, TileEntity tile) {

		Material material = MaterialRegistry.getMaterialFromItemStack(new ItemStack(state.getBlock(),1));
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
	public List<ModuleBase> getModules(int ID, PlayerEntity player) {
		// TODO Auto-generated method stub
		List<ModuleBase> modules = super.getModules(ID, player);

		modules.add(new ModuleProgress(80, 20, 0, TextureResources.arcFurnaceProgressBar, this));
		return modules;
	}
}
