package zmaster587.advancedRocketry.satellite;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.entity.EntityLaserNode;
import zmaster587.advancedRocketry.event.BlockBreakEvent.LaserBreakEvent;
import zmaster587.libVulpes.util.ZUtils;

import java.util.List;

public class SatelliteLaser extends SatelliteLaserNoDrill {

	private EntityLaserNode laser;
	protected boolean finished;

	public SatelliteLaser(IInventory boundChest) {
		super(boundChest);
		finished = false;
	}

	public boolean isAlive() {
		return laser != null && laser.isAlive();
	}
	
	public boolean isFinished() {
		return finished;
	}
	
	Vector3i ticketLaser = null;
	
	public boolean getJammed() { return jammed; }
	
	public void setJammed(boolean newJam) { jammed = newJam; }
	
	public void deactivateLaser() {
		if(laser != null) {
			laser.remove();
			laser = null;
		}
		
		if(ticketLaser != null)
		{
			ServerWorld worldServer = (ServerWorld)world;
			worldServer.forceChunk(ticketLaser.getX(), ticketLaser.getZ(), true);
			ticketLaser = null;
		}
		
		finished = false;
	}

	/**
	 * creates the laser and begins mining.  This can
	 * fail if the chunk cannot be force loaded
	 * @param world world to spawn the laser into
	 * @param x x coord
	 * @param z z coord
	 * @return whether creating the laser is successful
	 */
	public boolean activateLaser(World world, int x, int z) {
		
		if(world.isRemote)
			return false;
		
		ServerWorld worldServer = (ServerWorld)world;
		ticketLaser = new Vector3i(x>> 4, 0, z >> 4);
		worldServer.forceChunk(x >> 4, z >> 4, true);
		
		if(ticketLaser != null) 
		{
			
			int y = 64;
			
			if(world.getChunk( new BlockPos(x,0,z) ).getStatus().isAtLeast(ChunkStatus.FULL)) {
				int current = 0;
				for(int i = 0; i < 9; i++) {
					current = world.getHeight(Type.WORLD_SURFACE, x + (i % 3) - 1, z + (i / 3) - 1);
					if(current > y)
						y = current;
				}
				if(y < 1)
					y = 255;
			}
			else
				y = 255;
			
			laser = new EntityLaserNode(world, x, y, z);
			laser.markValid();
			laser.forceSpawn = true;
			world.addEntity(laser);
			return true;
		}
		return false;
	}

	public void performOperation() {
		for(int i = 0; i < 9; i++) {
			int x = (int)laser.getPosX() + (i % 3) - 1;
			int z = (int)laser.getPosZ() + (i / 3) - 1;
			
			BlockPos laserPos = new BlockPos(x, (int)laser.getPosY(), z);

			 BlockState state = laser.world.getBlockState(laserPos);//Block.blocksList[laser.worldObj.getBlockId(x, (int)laser.posY, z)];
			 Block dropBlock;
			//Post an event to the eventbus to make protections easier
			LaserBreakEvent event = new LaserBreakEvent(x, (int)laser.getPosY(), z);
			MinecraftForge.EVENT_BUS.post(event);

			if(event.isCanceled())
				continue;

			
			
			if(state == Blocks.AIR.getDefaultState() || state.getMaterial().isReplaceable() ||  state.getMaterial().isLiquid()) {
				laser.world.setBlockState(laserPos, AdvancedRocketryBlocks.blockLightSource.getDefaultState());
				continue;
			}

			LootContext.Builder builder = new LootContext.Builder((ServerWorld) laser.world);
			state.getDrops(builder);
			
			List<ItemStack> items = state.getDrops(builder);
			
			//TODO: may need to fix in later builds
			if(!state.getMaterial().isOpaque() || state.getBlock() == Blocks.BEDROCK)
				continue;



			//creator.performOperation();

			if(items.isEmpty()) {
				laser.world.setBlockState(laserPos, AdvancedRocketryBlocks.blockLightSource.getDefaultState());
				continue;
			}

			/*for(ItemStack stack : items) { 
				ItemEntity e = new ItemEntity(this.worldObj, x, (int)this.posY, z, stack);

				//Don't let anyone pick it up
				e.delayBeforeCanPickup = Integer.MAX_VALUE;
				e.motionX = 0;
				e.getMotion().y = 4;
				e.motionZ = 0;
				e.posX = (int)this.posX;
				e.posY = (int)this.posY + 1;
				e.posZ = (int)this.posZ;
				e.noClip = true;
				e.age = 5940;
				this.worldObj.spawnEntityInWorld(e);
			}*/

			if(boundChest != null){
				ItemStack stacks[] = new ItemStack[items.size()];
				
				stacks = items.toArray(stacks);

				ZUtils.mergeInventory(stacks, boundChest);

				if(!ZUtils.isInvEmpty(stacks)) {
					//TODO: drop extra items
					this.deactivateLaser();
					this.jammed = true;
					return;
				}
			}
			laser.world.setBlockState(laserPos, AdvancedRocketryBlocks.blockLightSource.getDefaultState());
			//laser.worldObj.setBlockToAir(x, (int)laser.posY, z);
		}

		boolean blockInWay = false;
		do {

			if(laser.getPosY() < 1) {
				laser.remove();
				laser = null;
				finished = true;
				break;
			}

			laser.setPosition((int)laser.getPosX(), laser.getPosY() - 1, (int)laser.getPosZ());

			for(int i = 0; i < 9; i++){
				int x = (int)laser.getPosX() + (i % 3) - 1;
				int z = (int)laser.getPosZ() + (i / 3) - 1;
				
				BlockPos laserPos = new BlockPos(x, (int)laser.getPosY(), z);

				BlockState state = laser.world.getBlockState(laserPos);
				
				if(!state.getMaterial().isOpaque() || state.getBlock() == Blocks.BEDROCK)
					continue;

				if(state == Blocks.AIR.getDefaultState() ||  state.getMaterial().isLiquid()) {
					laser.world.removeBlock(laserPos, false);
					continue;
				}

				if(state != Blocks.AIR.getDefaultState()) {
					blockInWay = true;
					break;
				}
			}
		} while (!blockInWay);
	}

	@Override
	public String getInfo(World world) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Laser";
	}

	@Override
	public boolean performAction(PlayerEntity player, World world, BlockPos pos) {
		performOperation();
		return false;
	}

	@Override
	public double failureChance() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToNBT(CompoundNBT nbt) {
		nbt.putBoolean("finished", finished);
		nbt.putBoolean("jammed", jammed);
	}

	@Override
	public void readFromNBT(CompoundNBT nbt) {
		finished = nbt.getBoolean("finished");
		jammed = nbt.getBoolean("jammed");
	}

	@Override
	public boolean canTick() {
		return false;
	}

	@Override
	public void tickEntity() {
	}
}
