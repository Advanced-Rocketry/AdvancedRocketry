package zmaster587.advancedRocketry.entity;

import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityLaserNode extends Entity {

	boolean chunkReloaded = false, firstLoad = false;

	//Used to make sure the emitter is still loaded and so we can send blocks back to the emitter
	//Also we don't want the chunk loading with the laser being there without an emitter it will cause a crash
	//private TileSpaceLaser creator;

	public EntityLaserNode(World par1World) {
		super(par1World);
		ignoreFrustumCheck = true;
		noClip = true;
	}

	public EntityLaserNode(World world, double x, double y, double z) {
		this(world);
		this.posX = x;
		this.posY = y;
		this.posZ = z;
	}

	@Override
	protected void entityInit() {
	}

	@Override
	public boolean isEntityInvulnerable() { return true;}
	
	@Override
	public void setDead() {
		super.setDead();
		this.cleanUp();
	}

	//TODO: make safe
	/**Removes all the lightblocks created by the laser
	 * For the love of all things good... do NOT call this twice
	 */
	public void cleanUp() {
		if(!this.worldObj.isRemote)
			new Thread(cleanThread).run();
	}

	Runnable cleanThread = new Runnable() {
		@Override
		public void run() {
			for(int h = 0; h < worldObj.getHeight(); h++) {
				for(int i = 0; i < 9; i++) {
					int x = (int)posX + (i % 3) - 1;
					int z = (int)posZ + (i / 3) - 1;

					if(worldObj.getBlock(x, h, z) == AdvancedRocketryBlocks.blockLightSource)
						worldObj.setBlockToAir(x, h, z);
				}
			}
		}
	};
	
	@Override
	public void onUpdate() {

		if(chunkReloaded) {
			this.setDead();

			return;
		}	

		super.onUpdate();
		
		if(this.worldObj.isRemote)	{
			if(Minecraft.getMinecraft().gameSettings.particleSetting < 2){
				final double spread = 3;
				final double initialSpeed = .5;
				for(int i = 0; i < (Minecraft.getMinecraft().gameSettings.particleSetting == 0 ? 20 : 5); i++)
					this.worldObj.spawnParticle("fireworksSpark", this.posX + (this.rand.nextDouble()*spread) - (spread/2), this.posY, this.posZ + (this.rand.nextDouble()*spread) - (spread/2), initialSpeed * this.rand.nextDouble() - (initialSpeed/2), initialSpeed * this.rand.nextDouble() * 20 + initialSpeed, initialSpeed * this.rand.nextDouble() - (initialSpeed/2));


				//this.worldObj.spawnParticle("tilecrack_" + this.worldObj.getBlockId((int)this.posX, (int)this.posY - 1, (int)this.posZ) + "_" + 0, this.posX + (this.rand.nextDouble()*spread) - (spread/2), this.posY + 5, this.posZ + (this.rand.nextDouble()*spread) - (spread/2), initialSpeed * this.rand.nextDouble(), initialSpeed * this.rand.nextDouble() * 20 + initialSpeed, initialSpeed * this.rand.nextDouble() - (initialSpeed/2));
				this.worldObj.spawnParticle("hugeexplosion", this.posX + (this.rand.nextDouble()*spread) - (spread/2), this.posY, this.posZ + (this.rand.nextDouble()*spread) - (spread/2), initialSpeed * this.rand.nextDouble(), initialSpeed * this.rand.nextDouble() * 4 + initialSpeed, initialSpeed * this.rand.nextDouble() - (initialSpeed/2));
			}
			//TODO: use sound setting
			this.worldObj.playSoundEffect(this.posX, this.posY, this.posZ, "advancedRocketry:sound.laserDrill", 1.0f, (1.0F + (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.2F) * 0.7F);
			
		}
	}

	@Override
	public void onChunkLoad() {
		setDead();
	}

	@SideOnly(Side.CLIENT)
	@Override
	/**
	 * Checks if the entity is in range to render by using the past in distance and comparing it to its average edge
	 * length * 64 * renderDistanceWeight Args: distance
	 */
	public boolean isInRangeToRenderDist(double par1)
	{
		//double d1 = this.boundingBox.getAverageEdgeLength();
		//d1 *= 4096.0D * this.renderDistanceWeight;
		return par1 < 16777216D;
	}


	//Dont need to write anything
	@Override
	protected void readEntityFromNBT(NBTTagCompound nbttagcompound) {

	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		// TODO Auto-generated method stub

	}
}
