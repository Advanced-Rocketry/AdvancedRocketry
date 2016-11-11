package zmaster587.advancedRocketry.tile.multiblock.machine;

import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.util.AudioRegistry;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.block.BlockMeta;
import zmaster587.libVulpes.interfaces.IRecipe;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleProgress;
import zmaster587.libVulpes.recipe.RecipesMachine;
import zmaster587.libVulpes.tile.multiblock.TileMultiBlock;
import zmaster587.libVulpes.tile.multiblock.TileMultiblockMachine;

public class TileChemicalReactor extends TileMultiblockMachine {
	public static final Object[][][] structure = { 
		{{null, 'c',null},
		{'L', 'I','L'}},
		
		{{'P', AdvancedRocketryBlocks.blockMotor, 'P'}, 
			{'l', new BlockMeta(LibVulpesBlocks.blockStructureBlock), 'O'}},

	};

	@Override
	public boolean shouldHideBlock(World world, BlockPos pos, IBlockState tile) {
		TileEntity tileEntity = world.getTileEntity(pos);
		
		return !TileMultiBlock.getMapping('P').contains(new BlockMeta(tile.getBlock(), BlockMeta.WILDCARD)) && tileEntity != null && !(tileEntity instanceof TileChemicalReactor);
	
	}
	
	@Override
	public Object[][][] getStructure() {
		return structure;
	}
	
	@Override
	public SoundEvent getSound() {
		return AudioRegistry.rollingMachine;
	}

	@Override
	public int getSoundDuration() {
		return 30;
	}
	
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(pos.add(-2,-2,-2), pos.add(2,2,2));
	}

	@Override
	public List<ModuleBase> getModules(int ID, EntityPlayer player) {
		List<ModuleBase> modules = super.getModules(ID, player);

		modules.add(new ModuleProgress(100, 4, 0, TextureResources.crystallizerProgressBar, this));
		return modules;
	}

	@Override
	public String getMachineName() {
		return "tile.chemreactor.name";
	}
}
