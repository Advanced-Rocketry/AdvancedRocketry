package zmaster587.advancedRocketry.tile.multiblock.machine;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.AdvancedRocketryTileEntityType;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.util.AudioRegistry;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleProgress;
import zmaster587.libVulpes.tile.multiblock.TileMultiblockMachine;

import java.util.List;

public class TileCuttingMachine extends TileMultiblockMachine implements IModularInventory {

	private static final Object[][][] structure = new Object[][][]{			
		{{'I', 'c', 'O'}, 
			{LibVulpesBlocks.motors, AdvancedRocketryBlocks.blockSawBlade, 'P'}}};


	public TileCuttingMachine() {
		super(AdvancedRocketryTileEntityType.TILE_CUTTING_MACHINE);
	}

	@Override
	public Object[][][] getStructure() {
		return structure;
	}

	@Override
	public boolean shouldHideBlock(World world, BlockPos pos, BlockState tile) {
		return true;
	}
	
	@Override
	public void tick() {
		super.tick();

		if(isRunning() && world.getGameTime() % 10 == 0) {
			Direction back = RotatableBlock.getFront(world.getBlockState(pos)).getOpposite();

			float xCoord = this.getPos().getX() + (0.5f*back.getXOffset()); 
			float zCoord = this.getPos().getZ() + (0.5f*back.getZOffset());

			for(LivingEntity entity : world.getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB(xCoord, this.getPos().getY() + 1, zCoord, xCoord + 1, this.getPos().getY() + 1.5f, zCoord + 1))) {
				entity.attackEntityFrom(DamageSource.CACTUS, 1f);
			}
		}
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(pos.add(-2,-2,-2), pos.add(2,2,2));
	}

	@Override
	public SoundEvent getSound() {
		return AudioRegistry.cuttingMachine;
	}

	@Override
	public String getMachineName() {
		return "block.advancedrocketry.cuttingmachine";
	}

	@Override
	public List<ModuleBase> getModules(int ID, PlayerEntity player) {
		List<ModuleBase> modules = super.getModules(ID, player);
		modules.add(new ModuleProgress(80, 20, 0, TextureResources.cuttingMachineProgressBar, this));

		return modules;
	}
}