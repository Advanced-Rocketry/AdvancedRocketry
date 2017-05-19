package zmaster587.advancedRocketry.satellite;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.advancedRocketry.entity.EntityLaserNode;
import zmaster587.advancedRocketry.event.BlockBreakEvent.LaserBreakEvent;
import zmaster587.libVulpes.util.ZUtils;

public class SatelliteLaser extends SatelliteLaserNoDrill {

	private EntityLaserNode laser;
	private Ticket ticketLaser;
	protected boolean finished;

	public SatelliteLaser(IInventory boundChest) {
		super(boundChest);
		finished = false;
	}

	public boolean isAlive() {
		return laser != null && !laser.isDead;
	}
	
	public boolean isFinished() {
		return finished;
	}
	
	public boolean getJammed() { return jammed; }
	
	public void setJammed(boolean newJam) { jammed = newJam; }
	
	public void deactivateLaser() {
		if(laser != null) {
			laser.setDead();
			laser = null;
		}
		
		if(ticketLaser != null)
			ForgeChunkManager.releaseTicket(ticketLaser);
		
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
		ticketLaser = ForgeChunkManager.requestTicket(AdvancedRocketry.instance, world, Type.NORMAL);
		
		if(ticketLaser != null) {
			ForgeChunkManager.forceChunk(ticketLaser, new ChunkPos(x >> 4, z >> 4));
			
			int y = 64;
			
			if(world.getChunkFromChunkCoords(x >> 4, z >> 4).isLoaded()) {
				int current = 0;
				for(int i = 0; i < 9; i++) {
					current = world.getTopSolidOrLiquidBlock(new BlockPos(x + (i % 3) - 1, 0xFF, z + (i / 3) - 1)).getY();
					if(current > y)
						y = current;
				}
				if(y < 1)
					y = 255;
			}
			else
				y = 255;
			
			laser = new EntityLaserNode(world, x, y, z);
			laser.forceSpawn = true;
			world.spawnEntity(laser);
			return true;
		}
		return false;
	}

	public void performOperation() {
		for(int i = 0; i < 9; i++) {
			int x = (int)laser.posX + (i % 3) - 1;
			int z = (int)laser.posZ + (i / 3) - 1;
			
			BlockPos laserPos = new BlockPos(x, (int)laser.posY, z);

			 IBlockState state = laser.world.getBlockState(laserPos);//Block.blocksList[laser.worldObj.getBlockId(x, (int)laser.posY, z)];
			 Block dropBlock;
			//Post an event to the eventbus to make protections easier
			LaserBreakEvent event = new LaserBreakEvent(x, (int)laser.posY, z);
			MinecraftForge.EVENT_BUS.post(event);

			if(event.isCanceled())
				continue;

			
			
			if(state == Blocks.AIR.getDefaultState() || state.getMaterial().isReplaceable() ||  state.getMaterial().isLiquid()) {
				laser.world.setBlockState(laserPos, AdvancedRocketryBlocks.blockLightSource.getDefaultState());
				continue;
			}

			List<ItemStack> items = state.getBlock().getDrops(laser.world, laserPos, state, 0);
			
			//TODO: may need to fix in later builds
			if(!state.getMaterial().isOpaque() || state.getBlock() == Blocks.BEDROCK)
				continue;



			//creator.performOperation();

			if(items.isEmpty()) {
				laser.world.setBlockState(laserPos, AdvancedRocketryBlocks.blockLightSource.getDefaultState());
				continue;
			}

			/*for(ItemStack stack : items) { 
				EntityItem e = new EntityItem(this.worldObj, x, (int)this.posY, z, stack);

				//Don't let anyone pick it up
				e.delayBeforeCanPickup = Integer.MAX_VALUE;
				e.motionX = 0;
				e.motionY = 4;
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

			if(laser.posY < 1) {
				laser.setDead();
				laser = null;
				finished = true;
				break;
			}

			laser.setPosition((int)laser.posX, laser.posY - 1, (int)laser.posZ);

			for(int i = 0; i < 9; i++){
				int x = (int)laser.posX + (i % 3) - 1;
				int z = (int)laser.posZ + (i / 3) - 1;
				
				BlockPos laserPos = new BlockPos(x, (int)laser.posY, z);

				IBlockState state = laser.world.getBlockState(laserPos);
				
				if(!state.getMaterial().isOpaque() || state.getBlock() == Blocks.BEDROCK)
					continue;

				if(state == Blocks.AIR.getDefaultState() ||  state.getMaterial().isLiquid()) {
					laser.world.setBlockToAir(laserPos);
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
	public boolean performAction(EntityPlayer player, World world, BlockPos pos) {
		performOperation();
		return false;
	}

	@Override
	public double failureChance() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setBoolean("finished", finished);
		nbt.setBoolean("jammed", jammed);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
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
