package zmaster587.advancedRocketry.satellite;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.ChunkCoordIntPair;
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
import zmaster587.advancedRocketry.tile.TileSpaceLaser;
import zmaster587.libVulpes.util.ZUtils;

public class SatelliteLaser extends SatelliteBase {

	private EntityLaserNode laser;
	private Ticket ticketLaser;
	private boolean finished, jammed;
	IInventory boundChest;

	public SatelliteLaser(IInventory boundChest) {
		this.boundChest = boundChest;
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
			ForgeChunkManager.forceChunk(ticketLaser, new ChunkCoordIntPair(x >> 4, z >> 4));
			
			int y = 64;
			
			if(world.getChunkFromBlockCoords(x, z).isChunkLoaded) {
				int current = 0;
				for(int i = 0; i < 9; i++) {
					current = world.getTopSolidOrLiquidBlock(x + (i % 3) - 1, z + (i / 3) - 1);
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
			world.spawnEntityInWorld(laser);
			return true;
		}
		return false;
	}

	public void performOperation() {
		for(int i = 0; i < 9; i++) {
			int x = (int)laser.posX + (i % 3) - 1;
			int z = (int)laser.posZ + (i / 3) - 1;

			Block dropBlock = laser.worldObj.getBlock(x, (int)laser.posY, z);//Block.blocksList[laser.worldObj.getBlockId(x, (int)laser.posY, z)];

			//Post an event to the eventbus to make protections easier
			LaserBreakEvent event = new LaserBreakEvent(x, (int)laser.posY, z);
			MinecraftForge.EVENT_BUS.post(event);

			if(event.isCanceled())
				continue;

			
			
			if(dropBlock == null || dropBlock.getMaterial().isReplaceable() ||  dropBlock.getMaterial().isLiquid()) {
				laser.worldObj.setBlock(x, (int)laser.posY, z, AdvancedRocketryBlocks.blockLightSource, 0, 3);
				continue;
			}

			ArrayList<ItemStack> items = dropBlock.getDrops(laser.worldObj, x, (int)laser.posY, z, laser.worldObj.getBlockMetadata(x, (int)laser.posY, z), 0);
			
			//TODO: may need to fix in later builds
			if(!dropBlock.getMaterial().isOpaque() || dropBlock == Blocks.bedrock)
				continue;



			//creator.performOperation();

			if(items.isEmpty()) {
				laser.worldObj.setBlock((int)laser.posX, (int)laser.posY, (int)laser.posZ, AdvancedRocketryBlocks.blockLightSource,0,3);
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
			laser.worldObj.setBlock(x, (int)laser.posY, z, AdvancedRocketryBlocks.blockLightSource,0,3);
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

				Block dropBlock = laser.worldObj.getBlock(x, (int)laser.posY, z); //Block.blocksList[laser.worldObj.getBlockId(x, (int)laser.posY, z)];

				if(!dropBlock.getMaterial().isOpaque() || dropBlock == Blocks.bedrock)
					continue;

				if(dropBlock == null ||  dropBlock.getMaterial().isLiquid()) {
					laser.worldObj.setBlockToAir(x, (int)laser.posY, z);
					continue;
				}

				if(dropBlock != null) {
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
	public boolean performAction(EntityPlayer player, World world, int x,
			int y, int z) {
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
