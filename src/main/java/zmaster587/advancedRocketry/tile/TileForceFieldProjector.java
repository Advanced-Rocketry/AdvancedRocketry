package zmaster587.advancedRocketry.tile;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.AdvancedRocketryTileEntityType;
import zmaster587.libVulpes.block.BlockFullyRotatable;

public class TileForceFieldProjector extends TileEntity implements ITickableTileEntity {

	private short extensionRange;
	private final short MAX_RANGE = 32;

<<<<<<< HEAD
	public TileForceFieldProjector() {
		super(AdvancedRocketryTileEntityType.TILE_FORCE_FIELD_PROJECTOR);
=======
    public TileForceFieldProjector() {
>>>>>>> origin/feature/nuclearthermalrockets
		extensionRange = 0;
	}


	public void destroyField(Direction facing) {
		while(extensionRange > 0) {
			BlockPos nextPos = pos.offset(facing, extensionRange);

			if(world.getBlockState(nextPos).getBlock() == AdvancedRocketryBlocks.blockForceField)
				world.removeBlock(nextPos, false);
			extensionRange--;
		}
	}

	@Override
<<<<<<< HEAD
	public void tick() {

		if(world.getGameTime() % 5 == 0) {
=======
	public void update() {
		if(world.getTotalWorldTime() % 5 == 0) {
>>>>>>> origin/feature/nuclearthermalrockets
			if(world.isBlockPowered(getPos())) {
                if(extensionRange <= MAX_RANGE) {
					if(extensionRange == 0)
						extensionRange = 1;

<<<<<<< HEAD
					BlockState state = world.getBlockState(getPos());
=======
					IBlockState state = world.getBlockState(getPos());

>>>>>>> origin/feature/nuclearthermalrockets
					if(state.getBlock() == AdvancedRocketryBlocks.blockForceFieldProjector) {
						Direction facing = BlockFullyRotatable.getFront(state);
						BlockPos nextPos = pos.offset(facing, extensionRange);
<<<<<<< HEAD
						if(world.getBlockState(nextPos).isReplaceable(Fluids.WATER)) {
=======

						if(world.getBlockState(nextPos).getBlock().isReplaceable(world, nextPos) ) {
>>>>>>> origin/feature/nuclearthermalrockets
							world.setBlockState(nextPos, AdvancedRocketryBlocks.blockForceField.getDefaultState());
						}

						if(world.getBlockState(nextPos).getBlock() == AdvancedRocketryBlocks.blockForceField) {
							extensionRange++;
						}
					}
				}
			}
			else if(extensionRange > 0) {
<<<<<<< HEAD

				BlockState state = world.getBlockState(getPos());
				if(state.getBlock() == AdvancedRocketryBlocks.blockForceFieldProjector) {
					Direction facing = BlockFullyRotatable.getFront(state);
					BlockPos nextPos = pos.offset(facing, extensionRange);

					if(world.getBlockState(nextPos).getBlock() == AdvancedRocketryBlocks.blockForceField)
						world.removeBlock(nextPos, false);
=======
				IBlockState state = world.getBlockState(getPos());

				if(state.getBlock() == AdvancedRocketryBlocks.blockForceFieldProjector) {
					EnumFacing facing = BlockFullyRotatable.getFront(state);
					BlockPos nextPos;

					//check to make sure the force field that was deleted last time stayed deleted
					if(extensionRange < MAX_RANGE) {
						nextPos = pos.offset(facing, extensionRange + 1);

						if(world.getBlockState(nextPos).getBlock() == AdvancedRocketryBlocks.blockForceField) {
							world.setBlockToAir(nextPos);
							AdvancedRocketry.logger.warn("Force field projector at " + pos + " found a force field block at " + nextPos + " that didn't stay deleted on retraction! Attempting deletion again.");
							return;
						}
					}
					else if(extensionRange > MAX_RANGE)
						extensionRange = MAX_RANGE;

					nextPos = pos.offset(facing, extensionRange);

					if(world.getBlockState(nextPos).getBlock() == AdvancedRocketryBlocks.blockForceField) {
						world.setBlockToAir(nextPos);
					}

>>>>>>> origin/feature/nuclearthermalrockets
					extensionRange--;
				}
			}
		}
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		nbt.putShort("ext", extensionRange);
		return super.write(nbt);
	}

	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		extensionRange = nbt.getShort("ext");
		super.read(state, nbt);
	}

}
