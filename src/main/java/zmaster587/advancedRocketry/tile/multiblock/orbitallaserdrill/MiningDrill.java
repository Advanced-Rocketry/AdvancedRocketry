package zmaster587.advancedRocketry.tile.multiblock.orbitallaserdrill;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.entity.EntityLaserNode;
import zmaster587.advancedRocketry.event.BlockBreakEvent;

class MiningDrill extends AbstractDrill {

	private EntityLaserNode laser;
	private ForgeChunkManager.Ticket ticketLaser;
	protected boolean finished;

	MiningDrill() {
		super();
		this.finished = false;
	}

	ItemStack[] performOperation() {

		ItemStack[] stacks = new ItemStack[0];

		for (int i = 0; i < 9; i++) {
			int x = (int) laser.posX + (i % 3) - 1;
			int z = (int) laser.posZ + (i / 3) - 1;
			BlockPos laserPos = new BlockPos(x, (int) laser.posY, z);
			IBlockState state = laser.world.getBlockState(laserPos);
			//Post an event to the eventbus to make protections easier
			BlockBreakEvent.LaserBreakEvent event = new BlockBreakEvent.LaserBreakEvent(x, (int) laser.posY, z);
			MinecraftForge.EVENT_BUS.post(event);

			if (event.isCanceled())
				continue;

			if (state == Blocks.AIR.getDefaultState() || state.getMaterial().isReplaceable() || state.getMaterial().isLiquid()) {
				laser.world.setBlockState(laserPos, AdvancedRocketryBlocks.blockLightSource.getDefaultState());
				continue;
			}

			NonNullList<ItemStack> items = NonNullList.create();
			state.getBlock().getDrops(items, laser.world, laserPos, state, 0);

			//TODO: may need to fix in later builds
			if (!state.getMaterial().isOpaque() || state.getBlock() == Blocks.BEDROCK)
				continue;


			if (items.isEmpty()) {
				laser.world.setBlockState(laserPos, AdvancedRocketryBlocks.blockLightSource.getDefaultState());
				continue;
			}

			stacks = new ItemStack[items.size()];
			stacks = items.toArray(stacks);

			laser.world.setBlockState(laserPos, AdvancedRocketryBlocks.blockLightSource.getDefaultState());
		}

		boolean blockInWay = false;
		do {

			if (laser.posY < 1) {
				laser.setDead();
				laser = null;
				finished = true;
				break;
			}

			laser.setPosition((int) laser.posX, laser.posY - 1, (int) laser.posZ);

			for (int i = 0; i < 9; i++) {
				int x = (int) laser.posX + (i % 3) - 1;
				int z = (int) laser.posZ + (i / 3) - 1;
				BlockPos laserPos = new BlockPos(x, (int) laser.posY, z);
				IBlockState state = laser.world.getBlockState(laserPos);

				if (!state.getMaterial().isOpaque() || state.getBlock() == Blocks.BEDROCK)
					continue;

				if (state == Blocks.AIR.getDefaultState() || state.getMaterial().isLiquid()) {
					laser.world.setBlockToAir(laserPos);
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
		ticketLaser = ForgeChunkManager.requestTicket(AdvancedRocketry.instance, world, ForgeChunkManager.Type.NORMAL);

		if (ticketLaser != null) {
			ForgeChunkManager.forceChunk(ticketLaser, new ChunkPos(x >> 4, z >> 4));

			int y = 64;

			if (world.getChunk(x >> 4, z >> 4).isLoaded()) {
				int current;
				for (int i = 0; i < 9; i++) {
					current = world.getTopSolidOrLiquidBlock(new BlockPos(x + (i % 3) - 1, 0xFF, z + (i / 3) - 1)).getY();
					if (current > y)
						y = current;
				}
			} else
				y = 255;

			laser = new EntityLaserNode(world, x, y, z);
			laser.markValid();
			laser.forceSpawn = true;
			world.spawnEntity(laser);
			return true;
		}
		return false;
	}

	void deactivate() {
		if (laser != null) {
			laser.setDead();
			laser = null;
		}

		if (ticketLaser != null)
			ForgeChunkManager.releaseTicket(ticketLaser);

		finished = false;
	}

	boolean isFinished() {
		return finished;
	}

	boolean needsRestart() {
		return this.laser == null || this.ticketLaser == null;
	}
}
