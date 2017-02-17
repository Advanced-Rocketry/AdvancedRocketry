package zmaster587.advancedRocketry.tile.station;

import io.netty.buffer.ByteBuf;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.relauncher.Side;
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

public class TilePlanetaryHologram extends TileEntity implements ITickable,IButtonInventory, IModularInventory, ISliderBar, INetworkMachine {

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
		boolean powered = worldObj.isBlockIndirectlyGettingPowered(getPos()) > 0;
		return (!powered && state == RedstoneState.INVERTED) || (powered && state == RedstoneState.ON) || state == RedstoneState.OFF;
	}

	@Override
	public void update() {
		if(!worldObj.isRemote) {
			if(isEnabled()) {
				
				if(onTime < 1)
					onTime += .2f/getHologramSize();//0.02f;
				
				if(allowUpdate) {
					for(EntityUIPlanet entity : entities) {
						DimensionProperties properties = entity.getProperties();
						if(entity != centeredEntity)
							entity.setPositionPolar(this.pos.getX() + .5, this.pos.getY() + 1, this.pos.getZ() + .5, getInterpHologramSize()*(.1+ properties.orbitalDist/100f), properties.orbitTheta);
						entity.setScale(getInterpHologramSize());
					}

					if(stellarMode) {
						for(EntityUIStar entity : starEntities) {
							entity.setPosition(this.pos.getX() + .5 + getInterpHologramSize()*entity.getStarProperties().getPosX()/100f, this.pos.getY() + 1, this.pos.getZ() + .5 + getInterpHologramSize()*entity.getStarProperties().getPosZ()/100f);
							entity.setScale(getInterpHologramSize());
						}
					}

					if(currentStar != null) {
						currentStar.setScale(getInterpHologramSize());
						currentStar.setPositionPolar(this.pos.getX() + .5, this.pos.getY() + 1, this.pos.getZ() + .5, 0, 0);
					}

					if(centeredEntity != null) {
						centeredEntity.setPositionPolar(this.pos.getX() + .5, this.pos.getY() + 1, this.pos.getZ() + .5, 0, 0);
					}

					if(entities.isEmpty() && starEntities.isEmpty()) {
						rebuildSystem();
					}

					if(backButton != null) {
						backButton.setPosition(this.pos.getX() + .5, this.pos.getY() + 1.5 + getInterpHologramSize()/10f, this.pos.getZ() + .5);
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
			ISpaceObject station = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(this.getPos());
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
			backButton.setPosition(this.pos.getX() + .5, this.pos.getY() + 1.5, this.pos.getZ() + .5);
			this.getWorld().spawnEntityInWorld(backButton);
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
				currentStar = new EntityUIStar(worldObj, currentStarBody, this, this.pos.getX() + .5, this.pos.getY() + 1, this.pos.getZ() + .5);
				this.getWorld().spawnEntityInWorld(currentStar);
			}

			for(IDimensionProperties properties : planetList) {
				EntityUIPlanet entity = new EntityUIPlanet(worldObj, (DimensionProperties)properties, this, this.pos.getX() + .5, this.pos.getY() + 1, this.pos.getZ() + .5);
				//entity.setPositionPolar(this.pos.getX() + .5, this.pos.getY() + 10, this.pos.getZ() + .5,  ((DimensionProperties)properties).orbitalDist/100f, ( (DimensionProperties)properties).orbitTheta);
				this.getWorld().spawnEntityInWorld(entity);
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
				EntityUIStar entity = new EntityUIStar(worldObj, body, this, this.pos.getX() + .5, this.pos.getY() + 1, this.pos.getZ() + .5);

				this.getWorld().spawnEntityInWorld(entity);
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
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound = super.writeToNBT(compound);
		state.writeToNBT(compound);
		
		return compound;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		state = RedstoneState.createFromNBT(compound);
		redstoneControl.setRedstoneState(state);
	}
}