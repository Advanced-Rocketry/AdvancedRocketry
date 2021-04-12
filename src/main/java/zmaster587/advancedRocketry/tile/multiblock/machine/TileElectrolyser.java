package zmaster587.advancedRocketry.tile.multiblock.machine;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import zmaster587.advancedRocketry.api.AdvancedRocketryFluids;
import zmaster587.advancedRocketry.api.AdvancedRocketryTileEntityType;
import zmaster587.advancedRocketry.api.Constants;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.util.AudioRegistry;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.api.material.Material;
import zmaster587.libVulpes.api.material.MaterialRegistry;
import zmaster587.libVulpes.block.BlockMeta;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleProgress;
import zmaster587.libVulpes.tile.multiblock.TileMultiBlock;
import zmaster587.libVulpes.tile.multiblock.TileMultiblockMachine;

import java.util.List;

public class TileElectrolyser extends TileMultiblockMachine {
	public static final Object[][][] structure = { 
		{{null,null,null},
		{'P', new ResourceLocation("forge","coils"),'P'}},
		
		{{'l', 'c', 'l'}, 
			{new BlockMeta(LibVulpesBlocks.blockStructureBlock), 'L', new BlockMeta(LibVulpesBlocks.blockStructureBlock)}},

	};
	
	public TileElectrolyser() {
		super(AdvancedRocketryTileEntityType.TILE_ELECTROLYSER);
	}
	
	@Override
	public Object[][][] getStructure() {
		return structure;
	}
	
	@Override
	public void registerRecipes() {
		//RecipesMachine.getInstance().addRecipe(TileElectrolyser.class, new Object[] {new FluidStack(AdvancedRocketryFluids.fluidOxygen, 100), new FluidStack(AdvancedRocketryFluids.fluidHydrogen, 100)}, 100, 20, new FluidStack(FluidRegistry.WATER, 10));
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
		return "block.advancedrocketry.electrolyser";
	}
}
