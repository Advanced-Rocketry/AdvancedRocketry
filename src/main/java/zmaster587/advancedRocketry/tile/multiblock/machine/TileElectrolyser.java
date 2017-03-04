package zmaster587.advancedRocketry.tile.multiblock.machine;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.util.AudioRegistry;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.api.material.Material;
import zmaster587.libVulpes.api.material.MaterialRegistry;
import zmaster587.libVulpes.block.BlockMeta;
import zmaster587.libVulpes.interfaces.IRecipe;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleProgress;
import zmaster587.libVulpes.recipe.RecipesMachine;
import zmaster587.libVulpes.tile.multiblock.TileMultiBlock;
import zmaster587.libVulpes.tile.multiblock.TileMultiblockMachine;

public class TileElectrolyser extends TileMultiblockMachine {
	public static final Object[][][] structure = { 
		{{null,null,null},
		{'P', "blockCoil",'P'}},
		
		{{'l', 'c', 'l'}, 
			{new BlockMeta(LibVulpesBlocks.blockStructureBlock), 'L', new BlockMeta(LibVulpesBlocks.blockStructureBlock)}},

	};
	
	@Override
	public Object[][][] getStructure() {
		return structure;
	}
	
	@Override
	public float getTimeMultiplierForBlock(IBlockState state, TileEntity tile) {

		Material material = MaterialRegistry.getMaterialFromItemStack(new ItemStack(state.getBlock(),1, state.getBlock().getMetaFromState(state)));
		if(material == MaterialRegistry.getMaterialFromName("Gold"))
			return 0.9f;
		else if(material == MaterialRegistry.getMaterialFromName("Aluiminum"))
			return 0.8f;
		else if(material == MaterialRegistry.getMaterialFromName("Titanium"))
			return 0.75f;
		else if(material == MaterialRegistry.getMaterialFromName("Iriudium"))
			return 0.5f;

		return super.getTimeMultiplierForBlock(state, tile);
	}
	
	@Override
	public boolean shouldHideBlock(World world, BlockPos pos2, IBlockState tile) {
		TileEntity tileEntity = world.getTileEntity(pos2);
		return !TileMultiBlock.getMapping('P').contains(new BlockMeta(tile.getBlock(), BlockMeta.WILDCARD)) && tileEntity != null && !(tileEntity instanceof TileElectrolyser);
	}
	
	
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(pos.add(-2,-2,-2), pos.add(2,2,2));
	}
	
	@Override
	public SoundEvent getSound() {
		// TODO Auto-generated method stub
		return AudioRegistry.electrolyser;
	}


	@Override
	public List<ModuleBase> getModules(int ID, EntityPlayer player) {
		List<ModuleBase> modules = super.getModules(ID, player);

		modules.add(new ModuleProgress(100, 4, 0, TextureResources.crystallizerProgressBar, this));
		return modules;
	}

	@Override
	public String getMachineName() {
		return "tile.electrolyser.name";
	}
}
