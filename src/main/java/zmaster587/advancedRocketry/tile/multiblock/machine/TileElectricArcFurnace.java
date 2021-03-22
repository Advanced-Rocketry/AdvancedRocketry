package zmaster587.advancedRocketry.tile.multiblock.machine;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundEvent;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.util.AudioRegistry;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.block.BlockMeta;
import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleProgress;
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
			{AdvancedRocketryBlocks.blockBlastBrick, "blockCoil",Blocks.AIR, "blockCoil",AdvancedRocketryBlocks.blockBlastBrick},
			{AdvancedRocketryBlocks.blockBlastBrick,Blocks.AIR,Blocks.AIR,Blocks.AIR,AdvancedRocketryBlocks.blockBlastBrick},
			{AdvancedRocketryBlocks.blockBlastBrick,Blocks.AIR, "blockCoil",Blocks.AIR,AdvancedRocketryBlocks.blockBlastBrick},
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

	@Override
	public List<BlockMeta> getAllowableWildCardBlocks() {
		List<BlockMeta> list = super.getAllowableWildCardBlocks();
		list.add(new BlockMeta(LibVulpesBlocks.blockHatch, 0));
		list.add(new BlockMeta(LibVulpesBlocks.blockHatch, 1));
		list.add(new BlockMeta(AdvancedRocketryBlocks.blockBlastBrick, -1));
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
	public List<ModuleBase> getModules(int ID, EntityPlayer player) {
		List<ModuleBase> modules = super.getModules(ID, player);

		modules.add(new ModuleProgress(80, 20, 0, TextureResources.arcFurnaceProgressBar, this));
		return modules;
	}
}
