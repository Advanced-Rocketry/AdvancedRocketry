package zmaster587.advancedRocketry.tile;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
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
	public void tick() {

		if(world.getGameTime() % 5 == 0) {
			if(world.isBlockPowered(getPos())) {
                if(extensionRange < MAX_RANGE) {
					if(extensionRange == 0)
						extensionRange = 1;

					BlockState state = world.getBlockState(getPos());
					if(state.getBlock() == AdvancedRocketryBlocks.blockForceFieldProjector) {
						Direction facing = BlockFullyRotatable.getFront(state);
						BlockPos nextPos = pos.offset(facing, extensionRange);
						if(world.getBlockState(nextPos).isReplaceable(Fluids.WATER)) {
							world.setBlockState(nextPos, AdvancedRocketryBlocks.blockForceField.getDefaultState());
							extensionRange++;
						} else if(world.getBlockState(nextPos).getBlock() == AdvancedRocketryBlocks.blockForceField) {
							extensionRange++;
						}
					}
				}
			}
			else if(extensionRange > 0) {

				BlockState state = world.getBlockState(getPos());
				if(state.getBlock() == AdvancedRocketryBlocks.blockForceFieldProjector) {
					Direction facing = BlockFullyRotatable.getFront(state);
					BlockPos nextPos = pos.offset(facing, extensionRange);

					if(world.getBlockState(nextPos).getBlock() == AdvancedRocketryBlocks.blockForceField)
						world.removeBlock(nextPos, false);
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
