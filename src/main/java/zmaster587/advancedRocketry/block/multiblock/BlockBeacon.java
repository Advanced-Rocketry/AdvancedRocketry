package zmaster587.advancedRocketry.block.multiblock;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.AdvancedRocketryParticleTypes;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.tile.multiblock.TileBeacon;
import zmaster587.libVulpes.block.multiblock.BlockMultiblockMachine;
import zmaster587.libVulpes.inventory.GuiHandler;
import zmaster587.libVulpes.util.HashedBlockPosition;
import zmaster587.libVulpes.util.ZUtils;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

public class BlockBeacon extends BlockMultiblockMachine {

	public BlockBeacon(AbstractBlock.Properties property, GuiHandler.guiId guiId) {
		super(property, guiId);
	}

	@Override
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof TileBeacon && DimensionManager.getInstance().isDimensionCreated(world)) {
			DimensionManager.getInstance().getDimensionProperties(ZUtils.getDimensionIdentifier(world)).removeBeaconLocation(world,new HashedBlockPosition(pos));
		}
		super.onReplaced(state, world, pos, newState, isMoving);
	}

	@OnlyIn(value=Dist.CLIENT)
	@Override
	@ParametersAreNonnullByDefault
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		if(worldIn.getTileEntity(pos) instanceof TileBeacon && ((TileBeacon)worldIn.getTileEntity(pos)).getMachineEnabled()) {
			Direction enumfacing = stateIn.get(FACING);
			for(int i = 0; i < 10; i++)
				AdvancedRocketry.proxy.spawnParticle(ParticleTypes.INSTANT_EFFECT, worldIn,  pos.getX() - enumfacing.getXOffset() + worldIn.rand.nextDouble(), pos.getY() + 5 - worldIn.rand.nextDouble(), pos.getZ() - enumfacing.getZOffset() + worldIn.rand.nextDouble(), 0, 0, 0);
		}
	}
}
