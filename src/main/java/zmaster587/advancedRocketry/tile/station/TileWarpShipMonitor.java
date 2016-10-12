package zmaster587.advancedRocketry.tile.station;

import io.netty.buffer.ByteBuf;

import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Predicate;

import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.achievements.ARAchivements;
import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.inventory.modules.ModulePlanetSelector;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.stations.SpaceObject;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.tile.multiblock.TileWarpCore;
import zmaster587.advancedRocketry.util.ITilePlanetSystemSelectable;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.client.util.IndicatorBarImage;
import zmaster587.libVulpes.client.util.ProgressBarImage;
import zmaster587.libVulpes.inventory.GuiHandler.guiId;
import zmaster587.libVulpes.inventory.modules.IButtonInventory;
import zmaster587.libVulpes.inventory.modules.IDataSync;
import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.inventory.modules.IProgressBar;
import zmaster587.libVulpes.inventory.modules.ISelectionNotify;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleButton;
import zmaster587.libVulpes.inventory.modules.ModuleImage;
import zmaster587.libVulpes.inventory.modules.ModuleProgress;
import zmaster587.libVulpes.inventory.modules.ModuleScaledImage;
import zmaster587.libVulpes.inventory.modules.ModuleSync;
import zmaster587.libVulpes.inventory.modules.ModuleText;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.util.HashedBlockPosition;
import zmaster587.libVulpes.util.INetworkMachine;
import zmaster587.libVulpes.util.IconResource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;

public class TileWarpShipMonitor extends TileEntity implements IModularInventory, ISelectionNotify, INetworkMachine, IButtonInventory, IProgressBar, IDataSync {

	protected ModulePlanetSelector container;
	private ModuleText canWarp;
	DimensionProperties dimCache;
	private SpaceObject station;
	ModuleScaledImage srcPlanetImg, dstPlanetImg, srcAtmo, dstAtmo;
	ModuleSync sync1, sync2, sync3;
	ModuleText srcPlanetText, dstPlanetText, warpFuel, status;
	int warpCost = -1;
	int dstPlanet, srcPlanet;


	public TileWarpShipMonitor() {

	}


	private SpaceObject getSpaceObject() {
		if(station == null && worldObj.provider.getDimension() == Configuration.spaceDimId) {
			ISpaceObject object = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(pos);
			if(object instanceof SpaceObject)
				station = (SpaceObject) object;
		}
		return station;
	}


	protected int getTravelCost() {
		if(getSpaceObject() != null) {
			DimensionProperties properties = getSpaceObject().getProperties().getParentProperties();

			DimensionProperties destProperties = DimensionManager.getInstance().getDimensionProperties(getSpaceObject().getDestOrbitingBody());
			while(destProperties.getParentProperties() != null && destProperties.getParentProperties().isMoon())
				destProperties = destProperties.getParentProperties();

			if((destProperties.isMoon() && destProperties.getParentPlanet() == properties.getId()) || (properties.isMoon() && properties.getParentPlanet() == destProperties.getId()))
				return 1;

			while(properties.isMoon())
				properties = properties.getParentProperties();

			//TODO: actual trig
			if(properties.getStar().getId() == destProperties.getStar().getId()) {
				double x1 = properties.orbitalDist*MathHelper.cos((float) properties.orbitTheta);
				double y1 = properties.orbitalDist*MathHelper.sin((float) properties.orbitTheta);
				double x2 = destProperties.orbitalDist*MathHelper.cos((float) destProperties.orbitTheta);
				double y2 = destProperties.orbitalDist*MathHelper.sin((float) destProperties.orbitTheta);

				return Math.max((int)Math.sqrt(Math.pow((x1 - x2),2) + Math.pow((y1 - y2),2)),1);

				//return Math.abs(properties.orbitalDist - destProperties.orbitalDist);
			}
		}
		return Integer.MAX_VALUE;
	}

	@Override
	public List<ModuleBase> getModules(int ID, EntityPlayer player) {
		List<ModuleBase> modules = new LinkedList<ModuleBase>();


		if(ID == guiId.MODULARNOINV.ordinal()) {

			//Don't keep recreating it otherwise data is stale
			if(sync1 == null) {
				sync1 = new ModuleSync(0, this);
				sync2 = new ModuleSync(1, this);
				sync3 = new ModuleSync(2, this);

			}
			modules.add(sync1);
			modules.add(sync2);
			modules.add(sync3);

			ISpaceObject station = getSpaceObject();
			boolean isOnStation = station != null;

			if(worldObj.isRemote)
				setPlanetModuleInfo();

			//Source planet
			int baseX = 10;
			int baseY = 20;
			int sizeX = 70;
			int sizeY = 70;

			if(worldObj.isRemote) {
				modules.add(new ModuleScaledImage(baseX,baseY,sizeX,sizeY, zmaster587.libVulpes.inventory.TextureResources.starryBG));
				modules.add(srcPlanetImg);


				modules.add(srcAtmo);

				modules.add(new ModuleText(baseX + 4, baseY + 4, "Orbiting:", 0xFFFFFF));
				modules.add(srcPlanetText);

				//Border
				modules.add(new ModuleScaledImage(baseX - 3,baseY,3,sizeY, TextureResources.verticalBar));
				modules.add(new ModuleScaledImage(baseX + sizeX, baseY, -3,sizeY, TextureResources.verticalBar));
				modules.add(new ModuleScaledImage(baseX,baseY,70,3, TextureResources.horizontalBar));
				modules.add(new ModuleScaledImage(baseX,baseY + sizeY - 3,70,-3, TextureResources.horizontalBar));
			}
			modules.add(new ModuleButton(baseX - 3, baseY + sizeY, 0, "Select Planet", this,  zmaster587.libVulpes.inventory.TextureResources.buttonBuild, sizeX + 6, 16));


			//Status text
			modules.add(new ModuleText(baseX, baseY + sizeY + 20, "Core Status:", 0x1b1b1b));
			boolean flag = isOnStation && getSpaceObject().getFuelAmount() >= getTravelCost() && getSpaceObject().hasUsableWarpCore();

			canWarp = new ModuleText(baseX, baseY + sizeY + 30, (isOnStation && getSpaceObject().getOrbitingPlanetId() == getSpaceObject().getDestOrbitingBody()) ? "Nowhere to go" : flag ? "Ready!" : "Not ready", flag ? 0x1baa1b : 0xFF1b1b);
			modules.add(canWarp);
			modules.add(new ModuleProgress(baseX, baseY + sizeY + 40, 10, new IndicatorBarImage(70, 58, 53, 8, 122, 58, 5, 8, EnumFacing.EAST, TextureResources.progressBars), this));
			modules.add(new ModuleText(baseX + 82, baseY + sizeY + 20, "Fuel Cost:", 0x1b1b1b));
			warpCost = getTravelCost();


			//DEST planet
			baseX = 94;
			baseY = 20;
			sizeX = 70;
			sizeY = 70;
			ModuleButton warp = new ModuleButton(baseX - 3, baseY + sizeY,1, "Warp!", this ,  zmaster587.libVulpes.inventory.TextureResources.buttonBuild, sizeX + 6, 16);

			modules.add(warp);

			if(dimCache == null && isOnStation && station.getOrbitingPlanetId() != SpaceObjectManager.WARPDIMID )
				dimCache = DimensionManager.getInstance().getDimensionProperties(station.getOrbitingPlanetId());

			if(worldObj.isRemote) {
				warpFuel.setText(flag ? String.valueOf(warpCost) : "N/A");
				modules.add(warpFuel);

				modules.add(new ModuleScaledImage(baseX,baseY,sizeX,sizeY, zmaster587.libVulpes.inventory.TextureResources.starryBG));
				if(dimCache != null) {

					if(worldObj.isRemote ) {
						modules.add(dstPlanetImg);
						modules.add(dstAtmo);

					}

					modules.add(new ModuleText(baseX + 4, baseY + 4, "Dest:", 0xFFFFFF));
					modules.add(dstPlanetText);


				}
				else {
					modules.add(new ModuleText(baseX + 4, baseY + 4, "Dest:", 0xFFFFFF));
					modules.add(dstPlanetText);
				}


				//Border
				modules.add(new ModuleScaledImage(baseX - 3,baseY,3,sizeY, TextureResources.verticalBar));
				modules.add(new ModuleScaledImage(baseX + sizeX, baseY, -3,sizeY, TextureResources.verticalBar));
				modules.add(new ModuleScaledImage(baseX,baseY,70,3, TextureResources.horizontalBar));
				modules.add(new ModuleScaledImage(baseX,baseY + sizeY - 3,70,-3, TextureResources.horizontalBar));
			}


		}
		else if (ID == guiId.MODULARFULLSCREEN.ordinal()) {
			//Open planet selector menu
			container = new ModulePlanetSelector(worldObj.provider.getDimension(), zmaster587.libVulpes.inventory.TextureResources.starryBG, this);
			container.setOffset(1000, 1000);
			modules.add(container);
		}
		return modules;
	}

	private void setPlanetModuleInfo() {

		ISpaceObject station = getSpaceObject();
		boolean isOnStation = station != null;
		ResourceLocation location;
		boolean hasAtmo = true;
		String planetName;

		if(isOnStation) {
			DimensionProperties properties = DimensionManager.getInstance().getDimensionProperties(station.getOrbitingPlanetId());
			location = properties.getPlanetIcon();
			hasAtmo = properties.hasAtmosphere();
			planetName = properties.getName();
		}
		else {
			location = DimensionManager.getInstance().getDimensionProperties(worldObj.provider.getDimension()).getPlanetIcon();
			planetName = DimensionManager.getInstance().getDimensionProperties(worldObj.provider.getDimension()).getName();

			if(planetName.isEmpty())
				planetName = "???";
		}

		boolean flag = isOnStation && getSpaceObject().getFuelAmount() >= warpCost && getSpaceObject().hasUsableWarpCore();

		if(canWarp != null)
			canWarp.setText(isOnStation && srcPlanet == dstPlanet ? "Nowhere to go" : flag ? "Ready!" : "Not ready");


		if(worldObj.isRemote) {
			if(srcPlanetImg == null ) {
				//Source planet
				int baseX = 10;
				int baseY = 20;
				int sizeX = 70;
				int sizeY = 70;

				srcPlanetImg = new ModuleScaledImage(baseX + 10,baseY + 10,sizeX - 20, sizeY - 20, location);
				srcAtmo = new ModuleScaledImage(baseX + 10,baseY + 10,sizeX - 20, sizeY - 20,0.4f, DimensionProperties.getAtmosphereResource());
				srcPlanetText = new ModuleText(baseX + 4, baseY + 16, "", 0xFFFFFF);
				warpFuel = new ModuleText(baseX + 82, baseY + sizeY + 30, "", 0x1b1b1b);

				//DEST planet
				baseX = 94;
				baseY = 20;
				sizeX = 70;
				sizeY = 70;

				dstPlanetImg = new ModuleScaledImage(baseX + 10,baseY + 10,sizeX - 20, sizeY - 20, location);
				dstAtmo = new ModuleScaledImage(baseX + 10,baseY + 10,sizeX - 20, sizeY - 20,0.4f, DimensionProperties.getAtmosphereResource());
				dstPlanetText = new ModuleText(baseX + 4, baseY + 16, "", 0xFFFFFF);

			}

			srcPlanetImg.setResourceLocation(location);
			srcAtmo.setVisible(hasAtmo);
			srcPlanetText.setText(planetName);


			warpFuel.setText(warpCost < Integer.MAX_VALUE ? String.valueOf(warpCost) : "N/A");




			DimensionProperties dstProps = null;
			if(isOnStation && station.getOrbitingPlanetId() != SpaceObjectManager.WARPDIMID )
				dstProps = DimensionManager.getInstance().getDimensionProperties(dstPlanet);

			if(dstProps != null) {
				hasAtmo = dstProps.hasAtmosphere();
				planetName = dstProps.getName();
				location = dstProps.getPlanetIcon();


				dstPlanetImg.setResourceLocation(location);
				dstAtmo.setVisible(hasAtmo);
				dstPlanetText.setText(planetName);

				dstPlanetImg.setVisible(true);
				dstAtmo.setVisible(true);


			}
			else {
				dstPlanetText.setText("???");
				dstPlanetImg.setVisible(false);
				dstAtmo.setVisible(false);
			}
		}
	}

	@Override
	public String getModularInventoryName() {
		return "tile.stationmonitor.name";
	}

	@Override
	public boolean canInteractWithContainer(EntityPlayer entity) {
		return true;
	}

	@Override
	public void onInventoryButtonPressed(int buttonId) {
		if(getSpaceObject() != null) {
			if(buttonId == 0)
				PacketHandler.sendToServer(new PacketMachine(this, (byte)0));
			else if(buttonId == 1) {
				PacketHandler.sendToServer(new PacketMachine(this, (byte)2));
			}
		}
	}

	@Override
	public void writeDataToNetwork(ByteBuf out, byte id) {
		if(id == 1 || id == 3)
			out.writeInt(container.getSelectedSystem());
	}

	//TODO fix warp controller not sending 

	@Override
	public void readDataFromNetwork(ByteBuf in, byte packetId,
			NBTTagCompound nbt) {
		if(packetId == 1 || packetId == 3)
			nbt.setInteger("id", in.readInt());
	}

	@Override
	public void useNetworkData(EntityPlayer player, Side side, byte id,
			NBTTagCompound nbt) {
		if(id == 0)
			player.openGui(LibVulpes.instance, guiId.MODULARFULLSCREEN.ordinal(), worldObj, this.getPos().getX(), this.getPos().getY(), this.getPos().getZ());
		else if(id == 1 || id == 3) {
			int dimId = nbt.getInteger("id");
			container.setSelectedSystem(dimId);
			selectSystem(dimId);

			//Update known planets
			markDirty();
			if(id == 3)
				player.openGui(LibVulpes.instance, guiId.MODULARNOINV.ordinal(), worldObj, this.getPos().getX(), this.getPos().getY(), this.getPos().getZ());
		}
		else if(id == 2) {
			final SpaceObject station = getSpaceObject();

			if(station != null && station.hasUsableWarpCore() && station.useFuel(getTravelCost()) != 0) {
				SpaceObjectManager.getSpaceManager().moveStationToBody(station, station.getDestOrbitingBody(), 200);

				for (EntityPlayer player2 : worldObj.getPlayers(EntityPlayer.class, new Predicate<EntityPlayer>() {
					public boolean apply(EntityPlayer input) {
						return SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(input.getPosition()) == station;
					};
				})) {
					player2.addStat(ARAchivements.givingItAllShesGot);
					if(!DimensionManager.hasReachedWarp)
						player2.addStat(ARAchivements.flightOfThePhoenix);
				}

				DimensionManager.hasReachedWarp = true;

				for(HashedBlockPosition vec : station.getWarpCoreLocations()) {
					TileEntity tile = worldObj.getTileEntity(vec.getBlockPos());
					if(tile != null && tile instanceof TileWarpCore) {
						((TileWarpCore)tile).onInventoryUpdated();
					}
				}
			}
		}
	}

	@Override
	public void onSelectionConfirmed(Object sender) {
		//Container Cannot be null at this time
		onSelected(sender);
		PacketHandler.sendToServer(new PacketMachine(this, (byte)3));
	}

	@Override
	public void onSelected(Object sender) {
		selectSystem(container.getSelectedSystem());
	}

	private void selectSystem(int id) {

		if(getSpaceObject().getOrbitingPlanetId() == SpaceObjectManager.WARPDIMID) {
			dimCache = null;
			//return;
		}

		if(id == SpaceObjectManager.WARPDIMID)
			dimCache = null;
		else {
			dimCache = DimensionManager.getInstance().getDimensionProperties(container.getSelectedSystem());

			ISpaceObject station = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(this.getPos());
			if(station != null) {
				station.setDestOrbitingBody(id);
			}
		}
	}

	@Override
	public void onSystemFocusChanged(Object sender) {
		PacketHandler.sendToServer(new PacketMachine(this, (byte)1));
	}


	@Override
	public float getNormallizedProgress(int id) {
		//Screw it, the darn thing will stop updating inv in certain circumstances
		if(worldObj.isRemote) {
			setPlanetModuleInfo();
		}

		return getProgress(id)/(float)getTotalProgress(id);
	}

	@Override
	public void setProgress(int id, int progress) {
		if(id == 10)
			if(getSpaceObject() != null)
				getSpaceObject().setFuelAmount(progress);
	}

	@Override
	public int getProgress(int id) {
		if(id == 10) {
			if(getSpaceObject() != null)
				return getSpaceObject().getFuelAmount();
		}

		if(id == 0)
			return 30;
		else if(id == 1)
			return 30;
		else if(id == 2)
			return (int) 30;
		return 0;
	}

	@Override
	public int getTotalProgress(int id) {
		if(id == 10) {
			if(getSpaceObject() != null)
				return getSpaceObject().getMaxFuelAmount();
		}
		if(dimCache == null)
			return 0;
		if(id == 0)
			return dimCache.getAtmosphereDensity()/2;
		else if(id == 1)
			return dimCache.orbitalDist/2;
		else if(id == 2)
			return (int) (dimCache.gravitationalMultiplier*50);

		return 0;
	}

	@Override
	public void setTotalProgress(int id, int progress) {
	}


	@Override
	public void setData(int id, int value) {
		//Id: 0, destination planet
		//Id: 1, source planet

		if(id == 2) {
			warpCost = value;
		}
		if(id == 1)
			srcPlanet = value;
		else if (id == 0)
			dstPlanet = value;
		setPlanetModuleInfo();
	}


	@Override
	public int getData(int id) {

		if(id == 2)
			return getTravelCost();

		ISpaceObject station = getSpaceObject();
		boolean isOnStation = station != null;
		if(isOnStation) {
			if(id == 1)
				return station.getOrbitingPlanetId();
			else //id == 1
				return station.getDestOrbitingBody();
		}

		return 0;
	}
}
