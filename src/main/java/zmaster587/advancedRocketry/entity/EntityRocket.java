package zmaster587.advancedRocketry.entity;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import zmaster587.advancedRocketry.network.PacketEntity;
import zmaster587.advancedRocketry.network.PacketHandler;
import zmaster587.advancedRocketry.util.RocketStorageChunk;
import zmaster587.advancedRocketry.util.StorageChunk;
import zmaster587.libVulpes.interfaces.INetworkEntity;
import net.minecraft.client.renderer.culling.Frustrum;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class EntityRocket extends Entity implements INetworkEntity {

	public RocketStorageChunk storage;
	private float playerXOffset, playerYOffset, playerZOffset;
	
	public enum PacketType {
		RECIEVENBT,
		SENDINTERACT,
		REQUESTNBT
	}

	public EntityRocket(World p_i1582_1_) {
		super(p_i1582_1_);
	}

	public EntityRocket(World world, RocketStorageChunk storage, double x, double y, double z) {
		this(world);
		this.setPosition(x, y, z);
		this.storage = storage;
		initFromBounds();
	}

	public void setPlayerOffset(int x, int y ,int z) {
		playerXOffset = x;
		playerYOffset = y;
		playerZOffset = z;
	}

	@Override
	public AxisAlignedBB getBoundingBox() {
		// TODO Auto-generated method stub
		if(storage != null) {

			return this.boundingBox;

		}return null;
	}
	
	@Override
	public AxisAlignedBB getCollisionBox(Entity p_70114_1_) {
		return null;
	}
	
	@Override
	public boolean canBeCollidedWith() {
		// TODO Auto-generated method stub
		return true;
	}
	@Override
	protected void entityInit() {


	}
	
	public void setEntityId(int id){
		super.setEntityId(id);
		//Ask server for nbt data
		if(worldObj.isRemote) {
			PacketHandler.sendToServer(new PacketEntity(this, (byte)PacketType.REQUESTNBT.ordinal()));
		}
	}

	@Override
	public boolean interactFirst(EntityPlayer player) {
		//this.setDead();
		
		//Actual interact code needs to be moved to a packet receive on the server
		
		if(worldObj.isRemote) {
			PacketHandler.sendToServer(new PacketEntity(this, (byte)PacketType.SENDINTERACT.ordinal()));
		}
		return true;

	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {

		if(nbt.hasKey("data"))
		{
			if(storage == null) 
				storage = new RocketStorageChunk();

			storage.readFromNBT(nbt.getCompoundTag("data"));

		}

	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {

		if(storage != null) {
			NBTTagCompound blocks = new NBTTagCompound();
			storage.writeToNBT(blocks);
			nbt.setTag("data", blocks);
		}

	}

	@Override
	public void writeDataToNetwork(ByteBuf out, byte id) {

	}

	@Override
	public void readDataFromNetwork(ByteBuf in, byte packetId,
			NBTTagCompound nbt) {
		// TODO Auto-generated method stub

	}

	@Override
	public void useNetworkData(EntityPlayer player, Side side, byte id,
			NBTTagCompound nbt) {


		if(id == PacketType.RECIEVENBT.ordinal()) {
			this.readEntityFromNBT(nbt);
			//this.boundingBox.setBB(getBoundingBox());
			initFromBounds();
		}
		if(id == PacketType.SENDINTERACT.ordinal()) {
			
			if(this.riddenByEntity == null){
				player.mountEntity(this);
			}
			else if(this.riddenByEntity == player) {
				player.mountEntity(null);
				player.setPosition(((int)player.posX) + 0.5f, player.posY, ((int)player.posZ) + 0.5f);
				storage.pasteInWorld(this.worldObj, (int)(this.posX - storage.getSizeX()/2f), (int)this.posY, (int)(this.posZ - storage.getSizeX()/2f));
				this.setDead();
			}
		}
		if(id == PacketType.REQUESTNBT.ordinal()) {
			if(storage != null) {
				
				NBTTagCompound nbtdata = new NBTTagCompound();
				
				this.writeToNBT(nbtdata);
				PacketHandler.sendToPlayer(new PacketEntity((INetworkEntity)this, (byte)PacketType.RECIEVENBT.ordinal(), nbtdata), player);
			
			}
		}
	}

	public void updateRiderPosition()
	{
		if (this.riddenByEntity != null )
		{
			if(this.storage != null) {
				this.riddenByEntity.setPosition(this.posX  + (-storage.getSizeX()/2f + storage.lastSeatX +0.5), this.posY + storage.lastSeatY + 1.5 , this.posZ + (-storage.getSizeZ()/2f + storage.lastSeatZ +0.5));
			}
			else
				this.riddenByEntity.setPosition(this.posX , this.posY , this.posZ );
		}
	}

	public void initFromBounds() {


		if(storage != null) {

			//this.boundingBox.setBB(AxisAlignedBB.getBoundingBox(this.posX - halfX, this.posY - halfY, this.posZ -halfZ, this.posX + halfX , this.posY + halfY, this.posZ + halfZ));
			this.setSize(Math.max(storage.getSizeX(), storage.getSizeZ()), storage.getSizeY());
			//this.ySize = (float)(this.boundingBox.maxY - this.boundingBox.minY);

			this.setPosition(this.posX, this.posY, this.posZ);
		}
	}
}
