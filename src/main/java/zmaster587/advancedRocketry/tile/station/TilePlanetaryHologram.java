package zmaster587.advancedRocketry.tile.station;

import io.netty.buffer.ByteBuf;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import zmaster587.advancedRocketry.api.dimension.IDimensionProperties;
import zmaster587.advancedRocketry.api.dimension.solar.StellarBody;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.entity.EntityUIButton;
import zmaster587.advancedRocketry.entity.EntityUIPlanet;
import zmaster587.advancedRocketry.entity.EntityUIStar;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.libVulpes.inventory.modules.IButtonInventory;
import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.inventory.modules.ISliderBar;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleRedstoneOutputButton;
import zmaster587.libVulpes.inventory.modules.ModuleSlider;
import zmaster587.libVulpes.inventory.modules.ModuleText;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.util.INetworkMachine;
import zmaster587.libVulpes.util.ZUtils.RedstoneState;

public class TilePlanetaryHologram extends TileEntity implements IButtonInventory, IModularInventory, ISliderBar, INetworkMachine {

	private List<EntityUIPlanet> entities;
	private List<EntityUIStar> starEntities;
	private EntityUIPlanet centeredEntity;
	private EntityUIPlanet selectedPlanet;
	private EntityUIStar currentStar;
	private EntityUIButton backButton;
	private StellarBody currentStarBody;

	private ModuleRedstoneOutputButton redstoneControl;
	private RedstoneState state;
	private int selectedId;
	private float onTime;
	private ModuleText targetGrav;
	private float size;
	private static final byte SCALEPACKET = 0;
	private static final byte STATEUPDATE = 1;
	private boolean allowUpdate = true;  //Hack to get around the delay in entity position
	private boolean stellarMode;

	public TilePlanetaryHologram() {
		entities = new LinkedList<EntityUIPlanet>();
		starEntities = new LinkedList<EntityUIStar>();
		targetGrav = new ModuleText(6, 45, "Hologram Size:", 0x202020);
		selectedPlanet = null;
		stellarMode = false;
		selectedId = -1;
		onTime = 1f;
		size = 0.02f;
		redstoneControl = new ModuleRedstoneOutputButton(174, 4, 1, "", this);
		state = RedstoneState.OFF;
		redstoneControl.setRedstoneState(state);
	}

	@Override
	public void invalidate() {
		super.invalidate();
		cleanup();
	}

	private void cleanup() {
		for(EntityUIPlanet planet : entities) {
			planet.setDead();
		}
		entities.clear();

		for(EntityUIStar star : starEntities) star.setDead();
		starEntities.clear();

		selectedPlanet = null;
		centeredEntity = null;
		//currentStarBody = null;
		selectedId = -1;


		if(currentStar != null) {
			currentStar.setDead();
			currentStar = null;
		}
		if(backButton != null) {
			backButton.setDead();
			backButton = null;
		}
	}

	public boolean isEnabled() {
		boolean powered = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
		return (!powered && state == RedstoneState.INVERTED) || (powered && state == RedstoneState.ON) || state == RedstoneState.OFF;
	}

	@Override
	public boolean canUpdate() {
		return true;
	}
	
	@Override
	public void updateEntity() {
		if(!worldObj.isRemote) {
			if(isEnabled()) {

				if(onTime < 1)
					onTime += .5f/getHologramSize();//0.02f;

				if(allowUpdate) {
					for(EntityUIPlanet entity : entities) {
						DimensionProperties properties = entity.getProperties();
						if(entity != centeredEntity)
							entity.setPositionPolar(this.xCoord + .5, this.yCoord + 1, this.zCoord + .5, getInterpHologramSize()*(.1 + properties.orbitalDist/100f), properties.orbitTheta);
						entity.setScale(getInterpHologramSize());
					}

					if(stellarMode) {
						for(EntityUIStar entity : starEntities) {
							entity.setPosition(this.xCoord + .5 + getInterpHologramSize()*entity.getStarProperties().getPosX()/100f, this.yCoord + 1, this.zCoord + .5 + getInterpHologramSize()*entity.getStarProperties().getPosZ()/100f);
							entity.setScale(getInterpHologramSize());
						}
					}
					else {
						if(!starEntities.isEmpty()) {
							float phaseInc = 4*360/starEntities.size();
							float phase = 0;
							for(EntityUIStar entity : starEntities) {
								double deltaX, deltaY;
								deltaX = (entity.getStarProperties().getStarSeperation()*MathHelper.cos(phase)*0.01);
								deltaY = (entity.getStarProperties().getStarSeperation()*MathHelper.sin(phase)*0.01);

								entity.setPosition(xCoord + .5 + getInterpHologramSize()*deltaX, yCoord + 1, zCoord + .5 + getInterpHologramSize()*deltaY);
								entity.setScale(getInterpHologramSize()*entity.getStarProperties().getSize());
								phase += phaseInc;
							}
						}
					}

					if(currentStar != null) {
						currentStar.setScale(getInterpHologramSize());
						currentStar.setPositionPolar(this.xCoord + .5, this.yCoord + 1, this.zCoord + .5, 0, 0);
					}

					if(centeredEntity != null) {
						centeredEntity.setPositionPolar(this.xCoord + .5, this.yCoord + 1, this.zCoord + .5, 0, 0);
					}

					if(entities.isEmpty() && starEntities.isEmpty()) {
						rebuildSystem();
					}

					if(backButton != null) {
						backButton.setPosition(this.xCoord + .5, this.yCoord + 1.5 + getInterpHologramSize()/10f, this.zCoord + .5);
					}
				}
				else
					allowUpdate = true;
			} else { //isenabled
				if(backButton != null )
					cleanup();
			}
		}
	}

	public void selectSystem(int id) {

		if(id >= EntityUIStar.starIDoffset) {
			if(stellarMode) {
				if(selectedId != id) {
					for(EntityUIStar entity : starEntities) {
						if(entity.getPlanetID() + EntityUIStar.starIDoffset == id) {
							entity.setSelected(true);
							selectedPlanet = entity;
						}
						else
							entity.setSelected(false);
					}
					selectedId = id;
				}
				else {
					stellarMode = false;
					currentStarBody = DimensionManager.getInstance().getStar(id - EntityUIStar.starIDoffset);
					rebuildSystem();
					selectedId = -1;
				}
			}

		}
		else {
			ISpaceObject station = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(this.xCoord, this.zCoord);
			if(station != null) {
				station.setDestOrbitingBody(id);

				if(selectedPlanet != null && selectedPlanet.getPlanetID() == id) {
					centeredEntity = selectedPlanet;
					stellarMode = false;
					rebuildSystem();
				}
				else
					for(EntityUIPlanet entity : entities) {
						if(entity.getPlanetID() == id) {
							entity.setSelected(true);
							selectedPlanet = entity;
						}
						else
							entity.setSelected(false);
					}
			}
		}
	}

	private void rebuildSystem() {
		onTime = 0;
		for(EntityUIPlanet entity : entities)
			entity.setDead();

		for(EntityUIStar body : starEntities)
			body.setDead();

		starEntities.clear();
		entities.clear();
		selectedPlanet = null;

		if(backButton == null) {
			backButton = new EntityUIButton(worldObj, 0, this);
			backButton.setPosition(this.xCoord + .5, this.yCoord + 1.5, this.zCoord + .5);
			this.worldObj.spawnEntityInWorld(backButton);
		}

		if(!stellarMode) {
			List<IDimensionProperties> planetList = currentStarBody == null ? DimensionManager.getSol().getPlanets() : currentStarBody.getPlanets();
			if(centeredEntity != null) {
				planetList = new LinkedList<IDimensionProperties>();
				planetList.add(centeredEntity.getProperties());

				for(int id : centeredEntity.getProperties().getChildPlanets())
					planetList.add(DimensionManager.getInstance().getDimensionProperties(id));

				if(currentStar != null) {
					currentStar.setDead();
					currentStar = null;
				}

			}
			else {
				if(currentStarBody == null)
					currentStarBody = DimensionManager.getSol();
				currentStar = new EntityUIStar(worldObj, currentStarBody, this, xCoord + .5, yCoord + 1, zCoord + .5);
				this.worldObj.spawnEntityInWorld(currentStar);

				//Spawn substars
				if(currentStarBody.getSubStars() != null && !currentStarBody.getSubStars().isEmpty()) {
					float phaseInc = 360/currentStarBody.getSubStars().size();
					float phase = 0;
					int count = 0;
					Collection<StellarBody> starList = currentStarBody.getSubStars();
					for(StellarBody body : starList) {

						int deltaX, deltaY;
						deltaX = (int)(body.getStarSeperation()*MathHelper.cos(phase)*0.05);
						deltaY = (int)(body.getStarSeperation()*MathHelper.sin(phase)*0.05);
						EntityUIStar entity = new EntityUIStar(worldObj, body, count++, this, xCoord + .5 + deltaX, yCoord + 1, zCoord + .5 + deltaY);

						this.worldObj.spawnEntityInWorld(entity);
						starEntities.add(entity);
						phase += phaseInc;
					}
				}
			}

			for(IDimensionProperties properties : planetList) {
				EntityUIPlanet entity = new EntityUIPlanet(worldObj, (DimensionProperties)properties, this, this.xCoord + .5, this.yCoord + 1, this.zCoord + .5);
				//entity.setPositionPolar(this.xCoord + .5, this.yCoord + 10, this.zCoord + .5,  ((DimensionProperties)properties).orbitalDist/100f, ( (DimensionProperties)properties).orbitTheta);
				this.worldObj.spawnEntityInWorld(entity);
				entities.add(entity);

				if(centeredEntity != null && properties == centeredEntity.getProperties())
					centeredEntity = entity;
			}
		}
		else {

			if(currentStar != null) {
				currentStar.setDead();
				currentStar = null;
			}

			Collection<StellarBody> starList = DimensionManager.getInstance().getStars();

			for(StellarBody body : starList) {
				EntityUIStar entity = new EntityUIStar(worldObj, body, this, this.xCoord + .5, this.yCoord + 1, this.zCoord + .5);

				this.worldObj.spawnEntityInWorld(entity);
				starEntities.add(entity);
			}
		}
		//Hack to delay position updates by a tick
		allowUpdate = false;
	}


	@Override
	public List<ModuleBase> getModules(int id, EntityPlayer player) {
		List<ModuleBase> modules = new LinkedList<ModuleBase>();

		modules.add(targetGrav);
		modules.add(new ModuleSlider(6, 60, 0, TextureResources.doubleWarningSideBarIndicator, (ISliderBar)this));
		modules.add(redstoneControl);

		updateText();
		return modules;
	}

	private void updateText() {
		if(worldObj.isRemote) {

			//numThrusters.setText("Number Of Thrusters: 0");
			targetGrav.setText(String.format("Hologram Size: %f", getHologramSize()));
		}
	}

	private float getHologramSize() {
		return (size*10 + 0.8f);
	}

	private float getInterpHologramSize() {
		return getHologramSize()*onTime;
	}

	@Override
	public String getModularInventoryName() {
		return "tile.planetHoloSelector.name";
	}

	@Override
	public boolean canInteractWithContainer(EntityPlayer entity) {
		return true;
	}

	@Override
	public float getNormallizedProgress(int id) {
		return size;
	}

	@Override
	public void setProgress(int id, int progress) {
		size = progress/100f;

	}

	@Override
	public int getProgress(int id) {
		return (int)(size*100);
	}

	@Override
	public int getTotalProgress(int id) {
		return 100;
	}

	@Override
	public void setTotalProgress(int id, int progress) {

	}

	@Override
	public void setProgressByUser(int id, int progress) {
		size = progress/100f;
		PacketHandler.sendToServer(new PacketMachine(this, SCALEPACKET));
		updateText();
	}

	@Override
	public void writeDataToNetwork(ByteBuf out, byte id) {
		if(id == SCALEPACKET) {
			out.writeFloat(size);
		}
		if(id == STATEUPDATE) {
			out.writeByte(state.ordinal());
		}

	}

	@Override
	public void readDataFromNetwork(ByteBuf in, byte packetId,
			NBTTagCompound nbt) {
		if(packetId == SCALEPACKET) {
			nbt.setFloat("scale", in.readFloat());
		}
		else if(packetId == STATEUPDATE) {
			nbt.setByte("state", in.readByte());
		}

	}

	@Override
	public void useNetworkData(EntityPlayer player, Side side, byte id,
			NBTTagCompound nbt) {
		if(id == SCALEPACKET) {
			size = nbt.getFloat("scale");
		}
		else if (id == STATEUPDATE) {
			state = RedstoneState.values()[nbt.getByte("state")];
		}
	}

	public void onInventoryButtonPressed(int buttonId) {
		//Back button
		if(buttonId == 0) {
			if(currentStar != null)
				stellarMode = true;
			selectedPlanet = null; centeredEntity = null;
			rebuildSystem();
		}
		else if(buttonId == 1) {
			state = redstoneControl.getState();
			PacketHandler.sendToServer(new PacketMachine(this, (byte)STATEUPDATE));
		}
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		state.writeToNBT(nbt);
		
		return new S35PacketUpdateTileEntity(xCoord,yCoord,zCoord,0, nbt);
	}
	
	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		super.onDataPacket(net, pkt);
		
		
		state = RedstoneState.createFromNBT(pkt.func_148857_g());
		redstoneControl.setRedstoneState(state);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		state.writeToNBT(compound);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		state = RedstoneState.createFromNBT(compound);
		redstoneControl.setRedstoneState(state);
	}
}