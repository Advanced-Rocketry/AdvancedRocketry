package zmaster587.advancedRocketry.tile.multiblock.orbitallaserdrill;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.entity.EntityLaserNode;
import zmaster587.advancedRocketry.event.BlockBreakEvent;

class MiningDrill extends AbstractDrill {

	private EntityLaserNode laser;
	private Vector3i ticketLaser;
	protected boolean finished;

	MiningDrill() {
		super();
		this.finished = false;
	}

	ItemStack[] performOperation() {

		ItemStack[] stacks = new ItemStack[0];

		for (int i = 0; i < 9; i++) {
			int x = (int) laser.getPosX() + (i % 3) - 1;
			int z = (int) laser.getPosZ() + (i / 3) - 1;
			BlockPos laserPos = new BlockPos(x, (int) laser.getPosY(), z);
			BlockState state = laser.world.getBlockState(laserPos);
			//Post an event to the eventbus to make protections easier
			BlockBreakEvent.LaserBreakEvent event = new BlockBreakEvent.LaserBreakEvent(x, (int) laser.getPosY(), z);
			MinecraftForge.EVENT_BUS.post(event);

			if (event.isCanceled())
				continue;

			if (state.getMaterial().isReplaceable() || state.getMaterial().isLiquid()) {
				laser.world.setBlockState(laserPos, Blocks.AIR.getDefaultState());
				continue;
			}

			List<ItemStack> items = Block.getDrops(state, (ServerWorld) laser.world, laserPos, laser.world.getTileEntity(laserPos));

			//TODO: may need to fix in later builds
			if (!state.getMaterial().isOpaque() || state.getBlock() == Blocks.BEDROCK)
				continue;


			if (items.isEmpty()) {
				laser.world.setBlockState(laserPos, Blocks.AIR.getDefaultState());
				continue;
			}

			stacks = new ItemStack[items.size()];
			stacks = items.toArray(stacks);

			laser.world.setBlockState(laserPos, Blocks.AIR.getDefaultState());
		}

		boolean blockInWay = false;
		do {

			if (laser.getPosY() < 1) {
				laser.remove();
				laser = null;
				finished = true;
				break;
			}

			laser.setPosition((int) laser.getPosX(), laser.getPosY() - 1, (int) laser.getPosZ());

			for (int i = 0; i < 9; i++) {
				int x = (int) laser.getPosX() + (i % 3) - 1;
				int z = (int) laser.getPosZ() + (i / 3) - 1;
				BlockPos laserPos = new BlockPos(x, (int) laser.getPosY(), z);
				BlockState state = laser.world.getBlockState(laserPos);

				if (!state.getMaterial().isOpaque() || state.getBlock() == Blocks.BEDROCK)
					continue;

				if (state == Blocks.AIR.getDefaultState() || state.getMaterial().isLiquid()) {
					laser.world.setBlockState(laserPos, Blocks.AIR.getDefaultState());
					continue;
				}

				if (state != Blocks.AIR.getDefaultState()) {
					blockInWay = true;
					break;
				}
			}
		} while (!blockInWay);

		return stacks;
	}

	boolean activate(World world, int x, int z) {
		
		ServerWorld worldServer = (ServerWorld)world;
		ticketLaser = new Vector3i(x>> 4, 0, z >> 4);
		worldServer.forceChunk(x >> 4, z >> 4, true);

		if (ticketLaser != null) {

			int y = 64;

			if(world.getChunk( new BlockPos(x,0,z) ).getStatus().isAtLeast(ChunkStatus.FULL)) {
				int current;
				for (int i = 0; i < 9; i++) {
					current = world.getHeight(Type.WORLD_SURFACE,x + (i % 3) - 1, z + (i / 3) - 1);
					if (current > y)
						y = current;
				}
			} else
				y = 255;

			laser = new EntityLaserNode(world, x, y, z);
			laser.markValid();
			laser.forceSpawn = true;
			world.addEntity(laser);
			return true;
		}
		return false;
	}

	void deactivate() {
		if (laser != null) {
			laser.remove();
			laser = null;
		}

		if(ticketLaser != null)
		{
			ServerWorld worldServer = (ServerWorld)laser.getEntityWorld();
			worldServer.forceChunk(ticketLaser.getX(), ticketLaser.getZ(), true);
			ticketLaser = null;
		}

		finished = false;
	}

	boolean isFinished() {
		return finished;
	}

	boolean needsRestart() {
		return this.laser == null || this.ticketLaser == null;
	}
}
