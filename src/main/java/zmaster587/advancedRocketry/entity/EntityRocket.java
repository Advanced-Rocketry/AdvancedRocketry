package zmaster587.advancedRocketry.entity;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.relauncher.Side;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.Inventory.GuiHandler;
import zmaster587.advancedRocketry.Inventory.GuiHandler.guiId;
import zmaster587.advancedRocketry.api.IInfrastructure;
import zmaster587.advancedRocketry.api.FuelRegistry.FuelType;
import zmaster587.advancedRocketry.entity.fx.RocketFx;
import zmaster587.advancedRocketry.network.PacketEntity;
import zmaster587.advancedRocketry.network.PacketHandler;
import zmaster587.advancedRocketry.stats.StatsRocket;
import zmaster587.advancedRocketry.util.Configuration;
import zmaster587.advancedRocketry.util.StorageChunk;
import zmaster587.advancedRocketry.world.TeleporterNoPortal;
import zmaster587.libVulpes.interfaces.INetworkEntity;
import zmaster587.libVulpes.item.ItemLinker;
import zmaster587.libVulpes.util.Vector3F;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.Constants.NBT;

public class EntityRocket extends Entity implements INetworkEntity {

	
	//Stores the blocks and tiles that make up the rocket
	public StorageChunk storage;
	
	//Stores other info about the rocket such as fuel and acceleration properties
	public StatsRocket stats;

	//true if the rocket is on decent
	private boolean isInOrbit;
	//True if the rocket isn't on the ground
	private boolean isInFlight;
	
	//Linked list containing Objects implementing IInfrastructure
	private LinkedList<IInfrastructure> connectedInfrastructure;

	//stores the coordinates of infrastructures, used for when the world loads/saves
	private LinkedList<Vector3F<Integer>> infrastructureCoords;

	public enum PacketType {
		RECIEVENBT,
		SENDINTERACT,
		REQUESTNBT,
		FORCEMOUNT,
		LAUNCH,
		DECONSTRUCT,
		OPENGUI
	}

	public EntityRocket(World p_i1582_1_) {
		super(p_i1582_1_);
		isInOrbit = false;
		stats = new StatsRocket();
		isInFlight = false;
		connectedInfrastructure = new LinkedList<IInfrastructure>();
		infrastructureCoords = new LinkedList<Vector3F<Integer>>();
	}

	public EntityRocket(World world, StorageChunk storage, StatsRocket stats, double x, double y, double z) {
		this(world);
		this.stats = stats;
		this.setPosition(x, y, z);
		this.storage = storage;
		initFromBounds();
		isInFlight = false;
	}

	
	@Override
	public AxisAlignedBB getBoundingBox() {
		if(storage != null) {
			return this.boundingBox;
		}
		return null;
	}
	
	public int getFuelAmount() {
		int amount = dataWatcher.getWatchableObjectInt(17);
		stats.setFuelAmount(FuelType.LIQUID,amount);
		return amount;
	}

	public void setFuelAmount(int amt) {
		dataWatcher.updateObject(17, amt);
	}

	public int getFuelCapacity() {
		return stats.getFuelCapacity(FuelType.LIQUID);
	}

	@Override
	public void setEntityId(int id){
		super.setEntityId(id);
		//Ask server for nbt data
		if(worldObj.isRemote) {
			PacketHandler.sendToServer(new PacketEntity(this, (byte)PacketType.REQUESTNBT.ordinal()));
		}
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	
	public void linkInfrastructure(IInfrastructure tile) {
		tile.linkRocket(this);
		connectedInfrastructure.add(tile);
	}

	public boolean isInFlight() {
		return this.dataWatcher.getWatchableObjectByte(16) == 1;
	}

	public void setInFlight(boolean inflight) {
		this.isInFlight = inflight;
		this.dataWatcher.updateObject(16, new Byte(isInFlight ? (byte)1 : (byte)0));
	}

	@Override
	protected void entityInit() {
		this.dataWatcher.addObject(16, new Byte(isInFlight ? (byte)1 : (byte)0));
		this.dataWatcher.addObject(17, new Integer(0));
	}

	//Set the size and position of the rocket from storage
	public void initFromBounds() {		
		if(storage != null) {
			this.setSize(Math.max(storage.getSizeX(), storage.getSizeZ()), storage.getSizeY());
			this.setPosition(this.posX, this.posY, this.posZ);
		}
	}

	@Override
	public boolean interactFirst(EntityPlayer player) {
		//Actual interact code needs to be moved to a packet receive on the server

		ItemStack heldItem = player.getHeldItem();

		//Handle linkers
		if(heldItem != null && heldItem.getItem() instanceof ItemLinker) {
			if(ItemLinker.isSet(heldItem)) {
				if(this.getDistanceSq(ItemLinker.getMasterX(heldItem), this.posY, ItemLinker.getMasterZ(heldItem)) < 100) {
					TileEntity tile = this.worldObj.getTileEntity(ItemLinker.getMasterX(heldItem), ItemLinker.getMasterY(heldItem), ItemLinker.getMasterZ(heldItem));

					if(tile instanceof IInfrastructure) {
						if(!connectedInfrastructure.contains(tile)) {

							linkInfrastructure((IInfrastructure)tile);
							if(worldObj.isRemote)
								Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage((new ChatComponentText("Linked Sucessfully")));
							ItemLinker.resetPosition(heldItem);

							return true;
						}
						else if(worldObj.isRemote)
							Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage((new ChatComponentText("Already linked!")));
					}
					else if(worldObj.isRemote)
						Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage((new ChatComponentText("This cannot be linked to a rocket!")));
				}
				else if(worldObj.isRemote)
					Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage((new ChatComponentText("The object you are trying to link is too far away")));
			}
			else if(worldObj.isRemote)
				Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage((new ChatComponentText("Nothing to be linked")));
			return false;
		}

		//If player is holding shift open GUI
		if(player.isSneaking()) {
			player.openGui(AdvancedRocketry.instance, GuiHandler.guiId.Rocket.ordinal(), player.worldObj, this.getEntityId(), 0, Math.min(storage.getSizeInventory(), 27));
		}

		if(worldObj.isRemote && !player.isSneaking()) {
			PacketHandler.sendToServer(new PacketEntity(this, (byte)PacketType.SENDINTERACT.ordinal()));
		}
		return true;

	}

	public void onRocketMount(EntityPlayer player) {

		if(!worldObj.isRemote) {
			//player.openGui(AdvancedRocketry.instance, guiId.Rocket.ordinal(), player.worldObj, this.getEntityId(), 0, 0);
		}
	}

	public boolean isBurningFuel() {
		return true;//this.stats.getFuelAmount(FuelType.LIQUID) > 0 && (!(this.riddenByEntity instanceof EntityPlayer) || !isInOrbit || ((EntityPlayer)this.riddenByEntity).moveForward > 0);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if(isInFlight()) {
			boolean burningFuel = isBurningFuel();

			if(burningFuel) {
				//Burn the rocket fuel
				setFuelAmount(getFuelAmount() - stats.getFuelRate(FuelType.LIQUID));

				//Spawn in the particle effects for the engines
				if(!worldObj.isRemote  && (this.motionY > 0 || (riddenByEntity instanceof EntityPlayer && ((EntityPlayer)riddenByEntity).moveForward > 0))) {
					for(Vector3F<Float> vec : stats.getEngineLocations()) {

						for(int i = 0; i < 4; i++) {
							RocketFx fx = new RocketFx(worldObj, this.posX + vec.x, this.posY + vec.y - 0.75, this.posZ +vec.z,(this.rand.nextFloat() - 0.5f)/8f,-.75 + this.motionY,(this.rand.nextFloat() - 0.5f)/8f);
							Minecraft.getMinecraft().effectRenderer.addEffect(fx);
						}
					}
				}
			}

			if(this.riddenByEntity instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer)this.riddenByEntity;

				//Hackish crap to make clients mount entities immediately after server transfer
				if(!worldObj.isRemote && this.ticksExisted == 20) {
					if(player instanceof EntityPlayer)
						PacketHandler.sendToPlayer(new PacketEntity((INetworkEntity)this,(byte)PacketType.FORCEMOUNT.ordinal()), player);

				}

				//if the player holds the forward key then decelerate
				if(isInOrbit && burningFuel)
					this.motionY -= this.motionY*player.moveForward/75f;
				this.velocityChanged = true;
			}

			//If out of fuel or descending then accelerate downwards
			if(isInOrbit || !burningFuel) {
				this.motionY = Math.min(this.motionY - 0.001, 1);
			} else
				this.motionY = Math.min(this.motionY + 0.001, 1);

			double lastPosY = this.posY;

			this.moveEntity(0, this.motionY, 0);

			//Check to see if it's landed
			if(lastPosY + this.motionY != this.posY) {
				this.setInFlight(false);
				this.isInOrbit = false;
			}


			//TODO: unhardcode destination
			if(this.posY > Configuration.orbit && !this.worldObj.isRemote) {
				this.motionY = -this.motionY;
				isInOrbit = true;
				this.travelToDimension(this.worldObj.provider.dimensionId ==2 ? 0 : 2);
			}
		}
	}


	public void launch() {
		setInFlight(true);
		Iterator<IInfrastructure> connectedTiles = connectedInfrastructure.iterator();

		//Disconnect things linked to the rocket on liftoff
		while(connectedTiles.hasNext()) {
			IInfrastructure i = connectedTiles.next();
			if(i.disconnectOnLiftOff()) {
				i.unlinkRocket();
				connectedTiles.remove();
			}
		}
	}

	public void deconstructRocket() {

		//Dismount player
		if(this.riddenByEntity != null) {
			this.riddenByEntity.mountEntity(null);
			this.riddenByEntity.setPosition(((int)this.riddenByEntity.posX) + 0.5f, this.riddenByEntity.posY, ((int)this.riddenByEntity.posZ) + 0.5f);
		}
		
		//paste the rocket into the world as blocks
		storage.pasteInWorld(this.worldObj, (int)(this.posX - storage.getSizeX()/2f), (int)this.posY, (int)(this.posZ - storage.getSizeX()/2f));
		this.setDead();
	}

	@Override
	public void setDead() {
		super.setDead();

		//unlink any connected tiles
		Iterator<IInfrastructure> connectedTiles = connectedInfrastructure.iterator();
		while(connectedTiles.hasNext()) {
			connectedTiles.next().unlinkRocket();
			connectedTiles.remove();
		}

	}


	//Relink connected tiles on load
	@Override
	public void onChunkLoad() {
		super.onChunkLoad();

		
		ListIterator<Vector3F<Integer>> itr = infrastructureCoords.listIterator();
		while(itr.hasNext()) {
			Vector3F<Integer> temp = itr.next();

			TileEntity tile = this.worldObj.getTileEntity(temp.x, temp.y, temp.z);
			if(tile instanceof IInfrastructure) {
				this.linkInfrastructure((IInfrastructure)tile);
				this.connectedInfrastructure.add((IInfrastructure) tile);
				itr.remove();
			}
		}
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {

		isInOrbit = nbt.getBoolean("orbit");
		stats.readFromNBT(nbt);
		setFuelAmount(stats.getFuelAmount(FuelType.LIQUID));

		setInFlight(nbt.getBoolean("flight"));

		if(nbt.hasKey("data"))
		{
			if(storage == null) 
				storage = new StorageChunk();

			storage.readFromNBT(nbt.getCompoundTag("data"));
		}

		NBTTagList tagList = nbt.getTagList("infrastructure", 10);
		for (int i = 0; i < tagList.tagCount(); i++) {
			int coords[] = tagList.getCompoundTagAt(i).getIntArray("loc");

			//If called on server causes recursive loop, use hackish workaround with tempcoords and onChunkLoad if on server
			if(worldObj.isRemote) {

				TileEntity tile = this.worldObj.getTileEntity(coords[0], coords[1], coords[2]);
				if(tile instanceof IInfrastructure)
					this.linkInfrastructure((IInfrastructure)tile);

			}
			else
				infrastructureCoords.add(new Vector3F<Integer>(coords[0], coords[1], coords[2]));
		}
	}

	@Override
	public void readDataFromNetwork(ByteBuf in, byte packetId,
			NBTTagCompound nbt) {
		if(packetId == PacketType.OPENGUI.ordinal()) {
			nbt.setInteger("slotPos", in.readInt());
		}
	}

	
	@Override
	public void travelToDimension(int newDimId)
	{
		if (!this.worldObj.isRemote && !this.isDead)
		{

			double x = posX, z = posZ;

			Entity rider = this.riddenByEntity;

			this.worldObj.theProfiler.startSection("changeDimension");
			MinecraftServer minecraftserver = MinecraftServer.getServer();
			int j = this.dimension;
			WorldServer worldserver = minecraftserver.worldServerForDimension(j);
			WorldServer worldserver1 = minecraftserver.worldServerForDimension(newDimId);
			this.dimension = newDimId;

			this.worldObj.removeEntity(this);
			this.isDead = false;
			this.worldObj.theProfiler.startSection("reposition");

			//transfer the rocket to the other dim without creating a nether portal
			minecraftserver.getConfigurationManager().transferEntityToWorld(this, j, worldserver, worldserver1,new TeleporterNoPortal(worldserver1));
			this.worldObj.theProfiler.endStartSection("reloading");
			Entity entity = EntityList.createEntityByName(EntityList.getEntityString(this), worldserver1);

			if (entity != null)
			{
				entity.copyDataFrom(this, true);

				entity.forceSpawn = true;
				worldserver1.spawnEntityInWorld(entity);
				entity.setLocationAndAngles(x, 500, z, this.rotationYaw, this.rotationPitch);

				this.isDead = true;

				if(rider != null) {
					//Transfer the player if applicable
					minecraftserver.getConfigurationManager().transferPlayerToDimension((EntityPlayerMP)rider, newDimId, new TeleporterNoPortal(worldserver1));

					rider.setLocationAndAngles(x, 500, z, this.rotationYaw, this.rotationPitch);

					rider.mountEntity(entity);
				}

			}

			this.worldObj.theProfiler.endSection();
			worldserver.resetUpdateEntityTick();
			worldserver1.resetUpdateEntityTick();
			this.worldObj.theProfiler.endSection();
		}
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {

		nbt.setBoolean("orbit", isInOrbit);
		nbt.setBoolean("flight", isInFlight());
		stats.writeToNBT(nbt);

		if(storage != null) {
			NBTTagCompound blocks = new NBTTagCompound();
			storage.writeToNBT(blocks);
			nbt.setTag("data", blocks);
		}

		//TODO handle non tile Infrastructure
		NBTTagList itemList = new NBTTagList();
		for(int i = 0; i < connectedInfrastructure.size(); i++)
		{
			IInfrastructure inf = connectedInfrastructure.get(i);

			if(inf instanceof TileEntity) {
				TileEntity ent = (TileEntity)inf;
				NBTTagCompound tag = new NBTTagCompound();
				tag.setIntArray("loc", new int[] {ent.xCoord, ent.yCoord, ent.zCoord});
				itemList.appendTag(tag);
			}
		}
		nbt.setTag("infrastructure", itemList);
	}

	@Override
	public void writeDataToNetwork(ByteBuf out, byte id) {

		if(id == PacketType.OPENGUI.ordinal()) {
			out.writeInt(storage.getInvPos());
		}
	}

	@Override
	public void useNetworkData(EntityPlayer player, Side side, byte id,
			NBTTagCompound nbt) {


		if(id == PacketType.RECIEVENBT.ordinal()) {
			this.readEntityFromNBT(nbt);
			initFromBounds();
		}
		else if(id == PacketType.DECONSTRUCT.ordinal()) {
			deconstructRocket();
		}
		else if(id == PacketType.SENDINTERACT.ordinal()) {

			if(this.riddenByEntity == null && !player.isSneaking()){
				player.mountEntity(this);
				this.onRocketMount(player);
			}
			else if(this.riddenByEntity == player || player.isSneaking()) {
				deconstructRocket();

			}
		}
		else if(id == PacketType.REQUESTNBT.ordinal()) {
			if(storage != null) {

				NBTTagCompound nbtdata = new NBTTagCompound();

				this.writeToNBT(nbtdata);
				PacketHandler.sendToPlayer(new PacketEntity((INetworkEntity)this, (byte)PacketType.RECIEVENBT.ordinal(), nbtdata), player);

			}
		}
		else if(id == PacketType.FORCEMOUNT.ordinal()) { //Used for pesky dimension transfers
			player.mountEntity(this);
			this.onRocketMount(player);
		}
		else if(id == PacketType.LAUNCH.ordinal()) {
			this.launch();
		}
		else if(id == PacketType.OPENGUI.ordinal()) {
			this.storage.setInvPos(nbt.getInteger("slotPos"));
		}
	}

	public void updateRiderPosition()
	{
		if (this.riddenByEntity != null )
		{
			if(this.storage != null) {
				this.riddenByEntity.setPosition(this.posX  + stats.getSeatX(), this.posY + stats.getSeatY() + 1.5 , this.posZ + stats.getSeatZ() );
			}
			else
				this.riddenByEntity.setPosition(this.posX , this.posY , this.posZ );
		}
	}
}
