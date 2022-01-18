package zmaster587.advancedRocketry.tile.multiblock.machine;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.api.AdvancedRocketryTileEntityType;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.util.AudioRegistry;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.block.BlockMeta;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleProgress;
import zmaster587.libVulpes.tile.multiblock.TileMultiblockMachine;

import java.util.List;

public class TileCentrifuge extends TileMultiblockMachine {
	public static final Object[][][] structure = {

			{{Blocks.AIR, Blocks.AIR, Blocks.AIR},
					{LibVulpesBlocks.blockAdvancedMachineStructure, LibVulpesBlocks.blockAdvancedMachineStructure, Blocks.AIR},
					{LibVulpesBlocks.blockAdvancedMachineStructure, LibVulpesBlocks.blockAdvancedMachineStructure, Blocks.AIR}},

			{{Blocks.AIR, Blocks.AIR, Blocks.AIR},
					{LibVulpesBlocks.blockAdvancedMachineStructure, LibVulpesBlocks.blockAdvancedMachineStructure, Blocks.AIR},
					{LibVulpesBlocks.blockAdvancedMachineStructure, LibVulpesBlocks.blockAdvancedMachineStructure, Blocks.AIR}},

			{{'c', Blocks.AIR, Blocks.AIR},
					{LibVulpesBlocks.blockAdvancedMachineStructure, LibVulpesBlocks.blockAdvancedMachineStructure, Blocks.AIR},
					{LibVulpesBlocks.blockAdvancedMachineStructure, LibVulpesBlocks.blockAdvancedMachineStructure, Blocks.AIR}},

			{   {'P','L', 'l'},
				{LibVulpesBlocks.motors,'O', new BlockMeta(LibVulpesBlocks.blockMachineStructure)},
			  {new BlockMeta(LibVulpesBlocks.blockMachineStructure), new BlockMeta(LibVulpesBlocks.blockMachineStructure), 'l'}},

	};

	public TileCentrifuge() {
		super(AdvancedRocketryTileEntityType.TILE_CENTRIFUGE);
	}

	@Override
	public Object[][][] getStructure() {
		return structure;
	}


	@Override
	public boolean shouldHideBlock(World world, BlockPos pos2, BlockState tile) {
		return true;
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(pos.add(-2,-2,-2), pos.add(2,2,2));
	}

	@Override
	public SoundEvent getSound() {
		return AudioRegistry.electrolyser;
	}


	@Override
	public List<ModuleBase> getModules(int ID, PlayerEntity player) {
		List<ModuleBase> modules = super.getModules(ID, player);

		modules.add(new ModuleProgress(100, 4, 0, TextureResources.crystallizerProgressBar, this));
		return modules;
	}

	@Override
	public String getMachineName() {
		return "block.advancedrocketry.centrifuge";
	}
}
