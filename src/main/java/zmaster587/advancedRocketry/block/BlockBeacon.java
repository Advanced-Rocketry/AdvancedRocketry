package zmaster587.advancedRocketry.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.tile.multiblock.TileBeacon;
import zmaster587.libVulpes.block.multiblock.BlockMultiblockMachine;
import zmaster587.libVulpes.tile.multiblock.TileMultiBlock;
import zmaster587.libVulpes.util.HashedBlockPosition;

import java.util.Random;

public class BlockBeacon extends BlockMultiblockMachine {

	public BlockBeacon(AbstractBlock.Properties property, TileEntityType<?> tileClass, int guiId) {
		super(property, tileClass, guiId);
	}

	@Override
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof TileBeacon && DimensionManager.getInstance().isDimensionCreated(world.provider.getDimension())) {
			DimensionManager.getInstance().getDimensionProperties(world).removeBeaconLocation(world,new HashedBlockPosition(pos));
		}
		super.onReplaced(state, world, pos, newState, isMoving);
	}

	@OnlyIn(value=Dist.CLIENT)
	public void randomDisplayTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand)
	{
		if(worldIn.getTileEntity(pos) instanceof TileBeacon && ((TileBeacon)worldIn.getTileEntity(pos)).getMachineEnabled()) {
			Direction enumfacing = (Direction)stateIn.get(FACING);
			for(int i = 0; i < 10; i++)
				AdvancedRocketry.proxy.spawnParticle("reddust", worldIn,  pos.getX() +- enumfacing.getXOffset() + worldIn.rand.nextDouble(), pos.getY() + 5 - worldIn.rand.nextDouble(), pos.getZ() - enumfacing.getZOffset() + worldIn.rand.nextDouble(), 0, 0, 0);
		}
	}
}
