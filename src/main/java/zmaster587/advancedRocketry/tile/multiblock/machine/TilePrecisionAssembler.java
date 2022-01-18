package zmaster587.advancedRocketry.tile.multiblock.machine;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.api.AdvancedRocketryTileEntityType;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.util.AudioRegistry;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.api.material.Material;
import zmaster587.libVulpes.api.material.MaterialRegistry;
import zmaster587.libVulpes.block.BlockMeta;
import zmaster587.libVulpes.client.util.ProgressBarImage;
import zmaster587.libVulpes.inventory.modules.*;
import zmaster587.libVulpes.tile.multiblock.TileMultiBlock;
import zmaster587.libVulpes.tile.multiblock.TileMultiblockMachine;
import zmaster587.libVulpes.util.IconResource;

import java.util.List;

public class TilePrecisionAssembler extends TileMultiblockMachine implements IModularInventory, IProgressBar {

	public static final Object[][][] structure = new Object[][][]{ {{LibVulpesBlocks.blockMachineStructure, LibVulpesBlocks.blockMachineStructure, LibVulpesBlocks.blockMachineStructure, LibVulpesBlocks.blockMachineStructure},
		{LibVulpesBlocks.blockMachineStructure, LibVulpesBlocks.blockMachineStructure, LibVulpesBlocks.blockMachineStructure, LibVulpesBlocks.blockMachineStructure},
		{LibVulpesBlocks.blockMachineStructure, LibVulpesBlocks.blockMachineStructure, LibVulpesBlocks.blockMachineStructure, LibVulpesBlocks.blockMachineStructure}},

		{{LibVulpesBlocks.blockMachineStructure, Blocks.GLASS, Blocks.GLASS, LibVulpesBlocks.blockMachineStructure},
			{LibVulpesBlocks.blockMachineStructure, Blocks.AIR, Blocks.AIR, LibVulpesBlocks.blockMachineStructure},
			{LibVulpesBlocks.blockMachineStructure, LibVulpesBlocks.blockMachineStructure, LibVulpesBlocks.blockMachineStructure, LibVulpesBlocks.blockMachineStructure}},

			{{'c', '*', '*', '*'},
				{'*', new ResourceLocation("forge","coils"), new ResourceLocation("forge","coils"), '*'},
				{'*', LibVulpesBlocks.motors, LibVulpesBlocks.motors, '*'}}};

				
				public TilePrecisionAssembler() {
					super(AdvancedRocketryTileEntityType.TILE_PRECISION_ASSEMBLER);
				}
				
	@Override
	public Object[][][] getStructure() {
		return structure;
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
	public List<BlockMeta> getAllowableWildCardBlocks() {
		List<BlockMeta> list = super.getAllowableWildCardBlocks();

		list.add(new BlockMeta(LibVulpesBlocks.blockMachineStructure, BlockMeta.WILDCARD));
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
	public List<ModuleBase> getModules(int ID, PlayerEntity player) {
		List<ModuleBase> modules = super.getModules(ID, player);

		int yOffset = 16;
		int xOffset = 65;

		if(world.isRemote)
			modules.add(new ModuleImage(xOffset, yOffset, new IconResource(132, 0, 53, 66, TextureResources.progressBars)));
		modules.add(new ModuleProgress(xOffset + 35, yOffset + 22, 1, new ProgressBarImage(167, 22, 13, 15, 54, 42, 13, 15, Direction.DOWN, TextureResources.progressBars), this));
		modules.add(new ModuleProgress(xOffset + 36, yOffset + 41, 2, new ProgressBarImage(168, 41, 11, 15, 67, 42, 11, 15, Direction.DOWN, TextureResources.progressBars), this));
		modules.add(new ModuleProgress(xOffset + 31, yOffset + 62, 3, new ProgressBarImage(163, 62, 21, 3, 90, 42, 21,  3, Direction.EAST, TextureResources.progressBars), this));

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
	public boolean shouldHideBlock(World world, BlockPos pos, BlockState tile) {
		TileEntity tileEntity = world.getTileEntity(pos);
		return tileEntity instanceof TilePrecisionAssembler;
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
		return "block.advancedrocketry.precisionassembler";
	}
}