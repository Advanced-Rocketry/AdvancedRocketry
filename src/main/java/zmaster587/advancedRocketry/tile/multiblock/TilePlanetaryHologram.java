package zmaster587.advancedRocketry.tile.multiblock;

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
import zmaster587.libVulpes.inventory.modules.ModuleSlider;
import zmaster587.libVulpes.inventory.modules.ModuleText;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.util.INetworkMachine;

public class TilePlanetaryHologram extends TileEntity implements ITickable, IModularInventory, ISliderBar, INetworkMachine {

	List<EntityUIPlanet> entities;
	List<EntityUIStar> starEntities;
	EntityUIPlanet centeredEntity;
	EntityUIPlanet selectedPlanet;
	EntityUIStar currentStar;
	EntityUIButton backButton;
	StellarBody currentStarBody;
	
	int selectedId;
	private ModuleText targetGrav;
	private float size;
	private static final byte SCALEPACKET = 0;
	private boolean allowUpdate = true;  //Hack to get around the delay in entity position
	private boolean stellarMode;

	public TilePlanetaryHologram() {
		entities = new LinkedList<EntityUIPlanet>();
		starEntities = new LinkedList<EntityUIStar>();
		targetGrav = new ModuleText(6, 45, "Hologram Size:", 0x202020);
		selectedPlanet = null;
		stellarMode = false;
		selectedId = -1;
	}

	@Override
	public void invalidate() {
		super.invalidate();
		for(EntityUIPlanet planet : entities) {
			planet.setDead();
		}
		entities.clear();

		for(EntityUIStar star : starEntities) star.setDead();
		starEntities.clear();

		if(currentStar != null) {
			currentStar.setDead();
			currentStar = null;
		}
		if(backButton != null) {
			backButton.setDead();
			backButton = null;
		}
	}

	@Override
	public void update() {
		if(!worldObj.isRemote) {
			if(allowUpdate) {
				for(EntityUIPlanet entity : entities) {
					DimensionProperties properties = entity.getProperties();
					if(entity != centeredEntity)
						entity.setPositionPolar(this.pos.getX() + .5, this.pos.getY() + 1, this.pos.getZ() + .5, getHologramSize()*properties.orbitalDist/100f, properties.orbitTheta);
					entity.setScale(getHologramSize());
				}

				if(stellarMode) {
					for(EntityUIStar entity : starEntities) {
						entity.setPosition(this.pos.getX() + .5 + getHologramSize()*entity.getStarProperties().getPosX()/100f, this.pos.getY() + 1, this.pos.getZ() + .5 + getHologramSize()*entity.getStarProperties().getPosZ()/100f);
						entity.setScale(getHologramSize());
					}
				}

				if(currentStar != null) {
					currentStar.setScale(getHologramSize());
					currentStar.setPositionPolar(this.pos.getX() + .5, this.pos.getY() + 1, this.pos.getZ() + .5, 0, 0);
				}

				if(centeredEntity != null) {
					centeredEntity.setPositionPolar(this.pos.getX() + .5, this.pos.getY() + 1, this.pos.getZ() + .5, 0, 0);
				}

				if(entities.isEmpty() && starEntities.isEmpty()) {
					rebuildSystem();
				}

				if(backButton != null) {
					backButton.setPosition(this.pos.getX() + .5, this.pos.getY() + 1.5 + getHologramSize()/10f, this.pos.getZ() + .5);
				}
			}
			else
				allowUpdate = true;
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
		return size*10 + 0.8f;
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

	}

	@Override
	public void readDataFromNetwork(ByteBuf in, byte packetId,
			NBTTagCompound nbt) {
		if(packetId == SCALEPACKET) {
			nbt.setFloat("scale", in.readFloat());
		}

	}

	@Override
	public void useNetworkData(EntityPlayer player, Side side, byte id,
			NBTTagCompound nbt) {
		if(id == SCALEPACKET) {
			size = nbt.getFloat("scale");
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
	}
}