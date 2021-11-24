package zmaster587.advancedRocketry.tile.multiblock.machine;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.AdvancedRocketryTileEntityType;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.util.AudioRegistry;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.material.Material;
import zmaster587.libVulpes.api.material.MaterialRegistry;
import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleProgress;
import zmaster587.libVulpes.inventory.modules.ModuleText;
import zmaster587.libVulpes.tile.multiblock.TileMultiblockMachine;
import zmaster587.libVulpes.util.ZUtils;

import java.util.List;

public class TileCrystallizer extends TileMultiblockMachine implements IModularInventory {

	
	
	public static final Object[][][] structure = { {{AdvancedRocketryBlocks.blockQuartzCrucible, AdvancedRocketryBlocks.blockQuartzCrucible, AdvancedRocketryBlocks.blockQuartzCrucible},
		{AdvancedRocketryBlocks.blockQuartzCrucible, AdvancedRocketryBlocks.blockQuartzCrucible, AdvancedRocketryBlocks.blockQuartzCrucible}},
		
		{{'O', 'c', 'I'}, 
			{'l', 'P', 'L'}}};
	
	Material[] coil;
	
	public TileCrystallizer() {
		super(AdvancedRocketryTileEntityType.TILE_CRYSTALLIZER);
		coil = new Material[2];
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
	public boolean shouldHideBlock(World world, BlockPos pos2, BlockState tile) {
		return true;
	}
	
	@Override
	public SoundEvent getSound() {
		return AudioRegistry.crystallizer;
	}
	
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(pos.add(-2,-2,-2), pos.add(2,2,2));
	}

	public boolean isGravityWithinBounds() {
		if (!(ARConfiguration.getCurrentConfig().crystalliserMaximumGravity.get() == 0)) {
			return ARConfiguration.getCurrentConfig().crystalliserMaximumGravity.get() > DimensionManager.getInstance().getDimensionProperties(ZUtils.getDimensionIdentifier(world)).gravitationalMultiplier;
		}
		return true;
	}

	@Override
	protected void onRunningPoweredTick() {
		if (isGravityWithinBounds()) {
			super.onRunningPoweredTick();
		}

	}

	@Override
	public List<ModuleBase> getModules(int ID, PlayerEntity player) {
		List<ModuleBase> modules = super.getModules(ID, player);

		modules.add(new ModuleProgress(100, 4, 0, TextureResources.crystallizerProgressBar, this));
		if (!isGravityWithinBounds()) {
			modules.add(new ModuleText(10, 75, LibVulpes.proxy.getLocalizedString("msg.crystalliser.gravityTooHigh"), 0xFF1b1b));
		}
		return modules;
	}

	@Override
	public String getMachineName() {
		return "block.advancedrocketry.crystallizer";
	}
}
