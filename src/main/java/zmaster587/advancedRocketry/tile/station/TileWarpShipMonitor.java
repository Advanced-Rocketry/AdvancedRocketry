package zmaster587.advancedRocketry.tile.station;

import io.netty.buffer.ByteBuf;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import zmaster587.advancedRocketry.achievements.ARAchivements;
import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.api.DataStorage.DataType;
import zmaster587.advancedRocketry.api.dimension.IDimensionProperties;
import zmaster587.advancedRocketry.api.dimension.solar.StellarBody;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.inventory.modules.ModuleData;
import zmaster587.advancedRocketry.inventory.modules.ModulePanetImage;
import zmaster587.advancedRocketry.inventory.modules.ModulePlanetSelector;
import zmaster587.advancedRocketry.inventory.IPlanetDefiner;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.item.ItemData;
import zmaster587.advancedRocketry.item.ItemPlanetIdentificationChip;
import zmaster587.advancedRocketry.network.PacketSpaceStationInfo;
import zmaster587.advancedRocketry.stations.SpaceObject;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.tile.multiblock.TileWarpCore;
import zmaster587.advancedRocketry.util.IDataInventory;
import zmaster587.advancedRocketry.world.util.MultiData;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.client.util.IndicatorBarImage;
import zmaster587.libVulpes.inventory.GuiHandler;
import zmaster587.libVulpes.inventory.GuiHandler.guiId;
import zmaster587.libVulpes.inventory.modules.IButtonInventory;
import zmaster587.libVulpes.inventory.modules.IDataSync;
import zmaster587.libVulpes.inventory.modules.IGuiCallback;
import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.inventory.modules.IProgressBar;
import zmaster587.libVulpes.inventory.modules.ISelectionNotify;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleButton;
import zmaster587.libVulpes.inventory.modules.ModuleProgress;
import zmaster587.libVulpes.inventory.modules.ModuleScaledImage;
import zmaster587.libVulpes.inventory.modules.ModuleSlotArray;
import zmaster587.libVulpes.inventory.modules.ModuleSync;
import zmaster587.libVulpes.inventory.modules.ModuleTab;
import zmaster587.libVulpes.inventory.modules.ModuleText;
import zmaster587.libVulpes.inventory.modules.ModuleTexturedSlotArray;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.util.BlockPosition;
import zmaster587.libVulpes.util.EmbeddedInventory;
import zmaster587.libVulpes.util.INetworkMachine;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;

public class TileWarpShipMonitor extends TileEntity implements IModularInventory, ISelectionNotify, INetworkMachine, IButtonInventory, IProgressBar, IDataSync, IGuiCallback, IDataInventory, IPlanetDefiner {


	protected ModulePlanetSelector container;
	private ModuleText canWarp;
	DimensionProperties dimCache;
	private SpaceObject station;
	private static final int ARTIFACT_BEGIN_RANGE = 4, ARTIFACT_END_RANGE = 7;
	ModulePanetImage srcPlanetImg, dstPlanetImg;
	ModuleSync sync1, sync2, sync3;
	ModuleText srcPlanetText, dstPlanetText, warpFuel, status, warpCapacity;
	int warpCost = -1;
	int dstPlanet, srcPlanet;
	private ModuleTab tabModule;
	private static final byte TAB_SWITCH = 4, STORE_DATA = 10, LOAD_DATA = 20, SEARCH = 5, PROGRAMFROMCHIP = 6;
	private MultiData data;
	private EmbeddedInventory inv;
	private static final int DISTANCESLOT = 0, MASSSLOT = 1, COMPOSITION = 2, PLANETSLOT = 3, MAX_PROGRESS = 1000;
	private ModuleProgress programmingProgress;
	private int progress;

	public TileWarpShipMonitor() {
		tabModule = new ModuleTab(4,0,0,this, 3, new String[]{LibVulpes.proxy.getLocalizedString("msg.warpmon.tab.warp"), LibVulpes.proxy.getLocalizedString("msg.warpmon.tab.data"), LibVulpes.proxy.getLocalizedString("msg.warpmon.tab.tracking")}, new ResourceLocation[][] { TextureResources.tabWarp, TextureResources.tabData, TextureResources.tabPlanetTracking} );
		data = new MultiData();
		data.setMaxData(10000);
		inv = new EmbeddedInventory(9);
		programmingProgress = new ModuleProgress(35, 80, 3, TextureResources.terraformProgressBar, this);
		progress = -1;
	}


	private SpaceObject getSpaceObject() {
		if(station == null && worldObj.provider.dimensionId == Configuration.spaceDimId) {
			ISpaceObject object = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(xCoord, zCoord);
			if(object instanceof SpaceObject)
				station = (SpaceObject) object;
		}
		return station;
	}


	protected int getTravelCost() {
		if(getSpaceObject() != null) {
			DimensionProperties properties = getSpaceObject().getProperties().getParentProperties();
			//properties.orbitalDist = 1;
			DimensionProperties destProperties = DimensionManager.getInstance().getDimensionProperties(getSpaceObject().getDestOrbitingBody());

			if(properties == DimensionManager.defaultSpaceDimensionProperties)
				return Integer.MAX_VALUE;

			if(destProperties.getStar() != properties.getStar())
				return 500;

			while(destProperties.getParentProperties() != null && destProperties.isMoon())
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
	public int addData(int maxAmount, DataType type, ForgeDirection dir,
			boolean commit) {
		return data.addData(maxAmount, type, dir, commit);
	}

	@Override
	public int extractData(int maxAmount, DataType type, ForgeDirection dir,
			boolean commit) {
		return data.extractData(maxAmount, type, dir, commit);
	}

	@Override
	public List<ModuleBase> getModules(int ID, EntityPlayer player) {
		List<ModuleBase> modules = new LinkedList<ModuleBase>();

		if(ID == guiId.MODULARNOINV.ordinal()) {

			//Front page
			if(tabModule.getTab() == 0) {
				modules.add(tabModule);
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

					
					ModuleText text = new ModuleText(baseX + 4, baseY + 4, "Orbiting:", 0xFFFFFF);
					text.setAlwaysOnTop(true);
					modules.add(text);
					
					modules.add(srcPlanetText);

					//Border
					modules.add(new ModuleScaledImage(baseX - 3,baseY,3,sizeY, TextureResources.verticalBar));
					modules.add(new ModuleScaledImage(baseX + sizeX, baseY, -3,sizeY, TextureResources.verticalBar));
					modules.add(new ModuleScaledImage(baseX,baseY,70,3, TextureResources.horizontalBar));
					modules.add(new ModuleScaledImage(baseX,baseY + sizeY - 3,70,-3, TextureResources.horizontalBar));
				}
				modules.add(new ModuleButton(baseX - 3, baseY + sizeY, 0, LibVulpes.proxy.getLocalizedString("msg.warpmon.selectplanet"), this,  zmaster587.libVulpes.inventory.TextureResources.buttonBuild, sizeX + 6, 16));


				//Status text
				modules.add(new ModuleText(baseX, baseY + sizeY + 20, LibVulpes.proxy.getLocalizedString("msg.warpmon.corestatus"), 0x1b1b1b));
				boolean flag = isOnStation && getSpaceObject().getFuelAmount() >= getTravelCost() && getSpaceObject().hasUsableWarpCore();
				flag = flag && !(isOnStation && (getSpaceObject().getDestOrbitingBody() == -1 || getSpaceObject().getOrbitingPlanetId() == getSpaceObject().getDestOrbitingBody()));
				boolean artifactFlag = (dimCache != null && meetsArtifactReq(dimCache));
				canWarp = new ModuleText(baseX, baseY + sizeY + 30, (isOnStation && (getSpaceObject().getDestOrbitingBody() == -1 || getSpaceObject().getOrbitingPlanetId() == getSpaceObject().getDestOrbitingBody())) ? LibVulpes.proxy.getLocalizedString("msg.warpmon.nowhere") : 
					(!artifactFlag ? LibVulpes.proxy.getLocalizedString("msg.warpmon.missingart") : (flag ? LibVulpes.proxy.getLocalizedString("msg.warpmon.ready") : LibVulpes.proxy.getLocalizedString("msg.warpmon.notready"))), flag && artifactFlag ? 0x1baa1b : 0xFF1b1b);
				modules.add(canWarp);
				modules.add(new ModuleProgress(baseX, baseY + sizeY + 40, 10, new IndicatorBarImage(70, 58, 53, 8, 122, 58, 5, 8, ForgeDirection.EAST, TextureResources.progressBars), this));
				//modules.add(new ModuleText(baseX + 82, baseY + sizeY + 20, "Fuel Cost:", 0x1b1b1b));

				warpCost = getTravelCost();
				
				


				//DEST planet
				baseX = 94;
				baseY = 20;
				sizeX = 70;
				sizeY = 70;
				ModuleButton warp = new ModuleButton(baseX - 3, baseY + sizeY,1, LibVulpes.proxy.getLocalizedString("msg.warpmon.warp"), this ,  zmaster587.libVulpes.inventory.TextureResources.buttonBuild, sizeX + 6, 16);

				modules.add(warp);

				if(dimCache == null && isOnStation && station.getOrbitingPlanetId() != SpaceObjectManager.WARPDIMID )
					dimCache = DimensionManager.getInstance().getDimensionProperties(station.getOrbitingPlanetId());

				if(!worldObj.isRemote && isOnStation) {
					PacketHandler.sendToPlayer(new PacketSpaceStationInfo(getSpaceObject().getId(), getSpaceObject()), player);
				}

				if(worldObj.isRemote) {
					warpFuel.setText(LibVulpes.proxy.getLocalizedString("msg.warpmon.fuelcost") + (flag ? String.valueOf(warpCost) : LibVulpes.proxy.getLocalizedString("msg.warpmon.na")));
					warpCapacity.setText(LibVulpes.proxy.getLocalizedString("msg.warpmon.fuel") + (isOnStation ? getSpaceObject().getFuelAmount() : LibVulpes.proxy.getLocalizedString("msg.warpmon.na")));
					modules.add(warpFuel);
					modules.add(warpCapacity);

					if(dimCache != null && worldObj.isRemote) {
						modules.add(dstPlanetImg);
					}
					
					ModuleText text = new ModuleText(baseX + 4, baseY + 4, LibVulpes.proxy.getLocalizedString("msg.warpmon.dest"), 0xFFFFFF);
					text.setAlwaysOnTop(true);
					modules.add(text);
					modules.add(dstPlanetText);


					//Border
					modules.add(new ModuleScaledImage(baseX - 3,baseY,3,sizeY, TextureResources.verticalBar));
					modules.add(new ModuleScaledImage(baseX + sizeX, baseY, -3,sizeY, TextureResources.verticalBar));
					modules.add(new ModuleScaledImage(baseX,baseY,70,3, TextureResources.horizontalBar));
					modules.add(new ModuleScaledImage(baseX,baseY + sizeY - 3,70,-3, TextureResources.horizontalBar));
				}
			}
			else if(tabModule.getTab() == 1) {
				modules.add(tabModule);
				modules.add(new ModuleData(35, 20, 0, this, data.getDataStorageForType(DataType.DISTANCE)));
				modules.add(new ModuleData(75, 20, 1, this, data.getDataStorageForType(DataType.MASS)));
				modules.add(new ModuleData(115, 20, 2, this, data.getDataStorageForType(DataType.COMPOSITION)));
			}
			else {
				modules.add(tabModule);
				modules.add(new ModuleText(65, 20, LibVulpes.proxy.getLocalizedString("msg.warpmon.artifact"), 0x202020));
				modules.add(new ModuleSlotArray(30, 35, this, 4, 5));
				modules.add(new ModuleSlotArray(55, 60, this, 5, 6));
				modules.add(new ModuleSlotArray(80, 35, this, 6, 7));
				modules.add(new ModuleSlotArray(105, 60, this, 6, 7));
				modules.add(new ModuleSlotArray(130, 35, this, 7, 8));

				modules.add(new ModuleButton(50, 117, 3, LibVulpes.proxy.getLocalizedString("msg.warpmon.search"), this,  zmaster587.libVulpes.inventory.TextureResources.buttonBuild, LibVulpes.proxy.getLocalizedString("msg.warpmon.datareq"), 100, 10));
				modules.add(new ModuleButton(50, 127, 4, LibVulpes.proxy.getLocalizedString("msg.warpmon.chip"), this, zmaster587.libVulpes.inventory.TextureResources.buttonBuild,100, 10));
				modules.add(new ModuleTexturedSlotArray(30, 120, this, 3, 4, TextureResources.idChip));
				modules.add(programmingProgress);
			}
		}
		else if (ID == guiId.MODULARFULLSCREEN.ordinal()) {
			//Open planet selector menu
			SpaceObject station = getSpaceObject();
			int starId = 0;
			if(station != null)
				starId = station.getProperties().getParentProperties().getStar().getId();
			container = new ModulePlanetSelector(starId, zmaster587.libVulpes.inventory.TextureResources.starryBG, this, this, true);
			container.setOffset(1000, 1000);
			modules.add(container);
		}
		return modules;
	}

	private void setPlanetModuleInfo() {

		ISpaceObject station = getSpaceObject();
		boolean isOnStation = station != null;
		DimensionProperties location;
		boolean hasAtmo = true;
		String planetName;

		if(isOnStation) {
			DimensionProperties properties = DimensionManager.getInstance().getDimensionProperties(station.getOrbitingPlanetId());
			location = properties;
			hasAtmo = properties.hasAtmosphere();
			planetName = properties.getName();
		}
		else {
			location = DimensionManager.getInstance().getDimensionProperties(worldObj.provider.dimensionId);
			planetName = DimensionManager.getInstance().getDimensionProperties(worldObj.provider.dimensionId).getName();

			if(planetName.isEmpty())
				planetName = "???";
		}

		boolean flag = isOnStation && getSpaceObject().getFuelAmount() >= warpCost && getSpaceObject().hasUsableWarpCore();

		if(canWarp != null) {
			flag = flag && !(isOnStation && (getSpaceObject().getDestOrbitingBody() == -1 || getSpaceObject().getOrbitingPlanetId() == getSpaceObject().getDestOrbitingBody()));
			boolean artifactFlag = (dimCache != null && meetsArtifactReq(dimCache));
			canWarp.setText(isOnStation && (getSpaceObject().getDestOrbitingBody() == -1 || getSpaceObject().getOrbitingPlanetId() == getSpaceObject().getDestOrbitingBody()) ? LibVulpes.proxy.getLocalizedString("msg.warpmon.nowhere") : 
				(!artifactFlag ? LibVulpes.proxy.getLocalizedString("msg.warpmon.missingart") : (flag ? LibVulpes.proxy.getLocalizedString("msg.warpmon.ready") : LibVulpes.proxy.getLocalizedString("msg.warpmon.notready"))));
			canWarp.setColor(flag && artifactFlag ? 0x1baa1b : 0xFF1b1b);
		}

		if(worldObj.isRemote) {
			if(srcPlanetImg == null ) {
				//Source planet
				int baseX = 10;
				int baseY = 20;
				int sizeX = 65;
				int sizeY = 65;

				srcPlanetImg = new ModulePanetImage(baseX + 10,baseY + 10,sizeX - 20, location);
				srcPlanetText = new ModuleText(baseX + 4, baseY + 56, "", 0xFFFFFF);
				srcPlanetText.setAlwaysOnTop(true);
				warpFuel = new ModuleText(baseX + 82, baseY + sizeY + 25, "", 0x1b1b1b);
				warpCapacity = new ModuleText(baseX + 82, baseY + sizeY + 35, "", 0x1b1b1b);

				//DEST planet
				baseX = 94;
				baseY = 20;
				sizeX = 65;
				sizeY = 65;

				dstPlanetImg = new ModulePanetImage(baseX + 10,baseY + 10,sizeX - 20, location);
				dstPlanetText = new ModuleText(baseX + 4, baseY + 56, "", 0xFFFFFF);
				dstPlanetText.setAlwaysOnTop(true);

			}

			srcPlanetImg.setDimProperties(location);
			srcPlanetText.setText(planetName);


			warpFuel.setText(LibVulpes.proxy.getLocalizedString("msg.warpmon.fuelcost") + (warpCost < Integer.MAX_VALUE ? String.valueOf(warpCost) : LibVulpes.proxy.getLocalizedString("msg.warpmon.na")));
			warpCapacity.setText(LibVulpes.proxy.getLocalizedString("msg.warpmon.fuel") + (isOnStation ? ((SpaceObject)station).getFuelAmount() : LibVulpes.proxy.getLocalizedString("msg.warpmon.na")));



			DimensionProperties dstProps = null;
			if(isOnStation && station.getOrbitingPlanetId() != SpaceObjectManager.WARPDIMID )
				dstProps = DimensionManager.getInstance().getDimensionProperties(dstPlanet);

			if(dstProps != null) {
				hasAtmo = dstProps.hasAtmosphere();
				planetName = dstProps.getName();
				location = dstProps;


				dstPlanetImg.setDimProperties(location);
				dstPlanetText.setText(planetName);

				dstPlanetImg.setVisible(true);

			}
			else {
				dstPlanetText.setText("???");
				dstPlanetImg.setVisible(false);
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
			else if(buttonId == 3) {
				PacketHandler.sendToServer(new PacketMachine(this, (byte)SEARCH));
			}
			else if(buttonId == 4) {
				PacketHandler.sendToServer(new PacketMachine(this, (byte)PROGRAMFROMCHIP));
			}
		}
	}

	@Override
	public void writeDataToNetwork(ByteBuf out, byte id) {
		if(id == 1 || id == 3)
			out.writeInt(container.getSelectedSystem());
		else if(id == TAB_SWITCH)
			out.writeShort(tabModule.getTab());
		else if(id >= 10 && id < 20) {
			out.writeByte(id - 10);
		}
		else if(id >= 20 && id < 30) {
			out.writeByte(id - 20);
		}
	}

	//TODO fix warp controller not sending 

	@Override
	public void readDataFromNetwork(ByteBuf in, byte packetId,
			NBTTagCompound nbt) {
		if(packetId == 1 || packetId == 3)
			nbt.setInteger("id", in.readInt());
		else if(packetId == TAB_SWITCH)
			nbt.setShort("tab", in.readShort());
		else if(packetId >= 10 && packetId < 20) {
			nbt.setByte("id", (byte)(in.readByte() - 10));
		}
		else if(packetId >= 20 && packetId < 30) {
			nbt.setByte("id", (byte)(in.readByte() - 20));
		}
	}

	@Override
	public void useNetworkData(EntityPlayer player, Side side, byte id,
			NBTTagCompound nbt) {
		if(id == 0)
			player.openGui(LibVulpes.instance, guiId.MODULARFULLSCREEN.ordinal(), worldObj, this.xCoord, this.yCoord, this.zCoord);
		else if(id == 1 || id == 3) {
			int dimId = nbt.getInteger("id");

			if(isPlanetKnown(DimensionManager.getInstance().getDimensionProperties(dimId))) {
				container.setSelectedSystem(dimId);
				selectSystem(dimId);
			}

			//Update known planets
			markDirty();
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			if(id == 3)
				player.openGui(LibVulpes.instance, guiId.MODULARNOINV.ordinal(), worldObj, this.xCoord, this.yCoord, this.zCoord);
		}
		else if(id == 2) {
			SpaceObject station = getSpaceObject();

			if(station != null && station.hasUsableWarpCore() && station.useFuel(getTravelCost()) != 0 && meetsArtifactReq(DimensionManager.getInstance().getDimensionProperties(station.getDestOrbitingBody()))) {
				SpaceObjectManager.getSpaceManager().moveStationToBody(station, station.getDestOrbitingBody(), Math.max(Math.min(getTravelCost()*5, 5000),0));


				for (Object plr : worldObj.playerEntities) {

					EntityPlayer player2 = (EntityPlayer)plr;
					if(SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords((int)player2.posX, (int)player2.posZ) == station) {
						player2.triggerAchievement(ARAchivements.givingItAllShesGot);
						if(!DimensionManager.hasReachedWarp)
							player2.triggerAchievement(ARAchivements.flightOfThePhoenix);
					}
				}

				DimensionManager.hasReachedWarp = true;

				for(BlockPosition vec : station.getWarpCoreLocations()) {
					TileEntity tile = worldObj.getTileEntity(vec.x, vec.y, vec.z);
					if(tile != null && tile instanceof TileWarpCore) {
						((TileWarpCore)tile).onInventoryUpdated();
					}
				}
			}
		}
		else if(id == TAB_SWITCH && !worldObj.isRemote) {
			tabModule.setTab(nbt.getShort("tab"));
			player.openGui(LibVulpes.instance, GuiHandler.guiId.MODULARNOINV.ordinal(), worldObj, xCoord, yCoord, zCoord);
		}
		else if(id >= 10 && id < 20) {
			storeData(nbt.getByte("id") + 10);
		}
		else if(id >= 20 && id < 30) {
			loadData(nbt.getByte("id") + 20);
		}
		else if(id == SEARCH) {
			if(progress == -1 && data.getDataAmount(DataType.COMPOSITION) >= 100 && 
					data.getDataAmount(DataType.DISTANCE) >= 100 &&
					data.getDataAmount(DataType.MASS) >= 100)
				progress = 0;
		}
		else if(id == PROGRAMFROMCHIP) {
			SpaceObject obj = getSpaceObject();
			if(obj != null) {
				ItemStack stack = getStackInSlot(PLANETSLOT);
				if(stack != null && stack.getItem() instanceof ItemPlanetIdentificationChip) {
					if(DimensionManager.getInstance().isDimensionCreated(((ItemPlanetIdentificationChip)stack.getItem()).getDimensionId(stack)));
					obj.discoverPlanet(((ItemPlanetIdentificationChip)stack.getItem()).getDimensionId(stack));
				}
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		inv.writeToNBT(compound);
		data.writeToNBT(compound);
		compound.setInteger("progress", progress);
		super.writeToNBT(compound);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		inv.readFromNBT(compound);
		data.readFromNBT(compound);
		progress = compound.getInteger("progress");
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
		if(getSpaceObject().getOrbitingPlanetId() == SpaceObjectManager.WARPDIMID || id == SpaceObjectManager.WARPDIMID)
			dimCache = null;
		else {
			dimCache = DimensionManager.getInstance().getDimensionProperties(container.getSelectedSystem());

			ISpaceObject station = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(this.xCoord, this.zCoord);
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
		if(id == 10) {
			if(getSpaceObject() != null)
				getSpaceObject().setFuelAmount(progress);
		}
		else if(id == 3) {
			this.progress = progress;
		}
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
		else if(id == 3) {
			return progress == -1 ? 0 : progress;
		}
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
		else if(id == 3) {
			return MAX_PROGRESS;
		}

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

	@Override
	public void onModuleUpdated(ModuleBase module) {
		//ReopenUI on server
		PacketHandler.sendToServer(new PacketMachine(this, TAB_SWITCH));
	}


	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return inv.getStackInSlotOnClosing(slot);
	}


	@Override
	public String getInventoryName() {
		return getModularInventoryName();
	}


	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}


	@Override
	public void openInventory() {
		inv.openInventory();
	}


	@Override
	public void closeInventory() {
		inv.closeInventory();
	}
	
	@Override
	public int getSizeInventory() {
		return inv.getSizeInventory();
	}


	@Override
	public ItemStack getStackInSlot(int index) {
		return inv.getStackInSlot(index);
	}


	@Override
	public ItemStack decrStackSize(int index, int count) {
		return inv.decrStackSize(index, count);
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		inv.setInventorySlotContents(index, stack);

	}


	@Override
	public int getInventoryStackLimit() {
		return inv.getInventoryStackLimit();
	}


	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return inv.isItemValidForSlot(index, stack);
	}

	@Override
	public void loadData(int id) {
		ItemStack stack = null;
		
		//Use an unused datatype for now
		DataType type = DataType.HUMIDITY;
		
		if(id == 0) 
		{
			stack = inv.getStackInSlot(DISTANCESLOT);
			type = DataType.DISTANCE;
		}
		else if (id == 1)
		{
			stack = inv.getStackInSlot(MASSSLOT);
			type = DataType.MASS;
		}
		else if(id == 2)
		{
			stack = inv.getStackInSlot(COMPOSITION);
			type = DataType.COMPOSITION;
		}

		if(stack != null && stack.getItem() instanceof ItemData) {
			ItemData item = (ItemData) stack.getItem();
			if(item.getDataType(stack) == type)
				item.removeData(stack, this.addData(item.getData(stack), item.getDataType(stack), ForgeDirection.UP, true), type);
		}

		if(worldObj.isRemote) {
			PacketHandler.sendToServer(new PacketMachine(this, (byte)(LOAD_DATA + id)));
		}
	}


	@Override
	public void storeData(int id) {
		ItemStack stack = null;
		DataType type = null;
		if(id == 0) {
			stack = inv.getStackInSlot(DISTANCESLOT);
			type = DataType.DISTANCE;
		}
		else if (id == 1) {
			stack = inv.getStackInSlot(MASSSLOT);
			type = DataType.MASS;
		}
		else if(id == 2) {
			stack = inv.getStackInSlot(COMPOSITION);
			type = DataType.COMPOSITION;
		}

		if(stack != null && stack.getItem() instanceof ItemData) {
			ItemData item = (ItemData) stack.getItem();
			data.extractData(item.addData(stack, data.getDataAmount(type), type), type, ForgeDirection.UP, true);
		}

		if(worldObj.isRemote) {
			PacketHandler.sendToServer(new PacketMachine(this, (byte)(STORE_DATA + id)));
		}
	}

	private boolean meetsArtifactReq(DimensionProperties properties) {
		//Make sure we have all the artifacts
		
		if(properties.getRequiredArtifacts().isEmpty())
			return true;
		
		List<ItemStack> list = new LinkedList<ItemStack>(properties.getRequiredArtifacts());
		for(int i = ARTIFACT_BEGIN_RANGE; i <= ARTIFACT_END_RANGE; i++) {
			ItemStack stack2 = getStackInSlot(i);
			if(stack2 != null) {
				Iterator<ItemStack> itr = list.iterator();
				while(itr.hasNext()) {
					ItemStack stackInList = itr.next();
					if(stackInList.getItem().equals(stack2.getItem()) && stackInList.getItemDamage() == stack2.getItemDamage()
							&& ItemStack.areItemStackTagsEqual(stackInList, stack2) && stack2.stackSize >= stackInList.stackSize)
						itr.remove();
				}
			}
		}
		
		return list.isEmpty();
	}
	
	@Override
	public void updateEntity() {
		if(!worldObj.isRemote && progress != -1) {
			progress++;
			if(progress >= MAX_PROGRESS) {
				//Do the thing
				SpaceObject obj = getSpaceObject();
				if(Math.abs(worldObj.rand.nextInt()) % Configuration.planetDiscoveryChance == 0 && obj != null) {
					ItemStack stack = getStackInSlot(PLANETSLOT);
					if(stack != null && stack.getItem() instanceof ItemPlanetIdentificationChip) {
						ItemPlanetIdentificationChip item = (ItemPlanetIdentificationChip)stack.getItem();
						List<Integer> unknownPlanets = new LinkedList<Integer>();
						
						//Check to see if any planets with artifacts can be discovered
						for(int id : DimensionManager.getInstance().getLoadedDimensions()) {
							DimensionProperties props = DimensionManager.getInstance().getDimensionProperties(id);
							if(!isPlanetKnown(props) && !props.getRequiredArtifacts().isEmpty()) {
								//If all artifacts are met, then add
								if(meetsArtifactReq(props))
									unknownPlanets.add(id);
							}
						}

						//if there are not any planets requiring artifacts then get the regular planets
						if(unknownPlanets.isEmpty()) {
							for(int id : DimensionManager.getInstance().getLoadedDimensions()) {
								DimensionProperties props = DimensionManager.getInstance().getDimensionProperties(id);
								if(!isPlanetKnown(props) && props.getRequiredArtifacts().isEmpty()) {
									unknownPlanets.add(id);
								}
							}
						}

						if(!unknownPlanets.isEmpty()) {
							int newId = (int)(worldObj.rand.nextFloat()*unknownPlanets.size());
							newId = unknownPlanets.get(newId);
							item.setDimensionId(stack, newId);
							obj.discoverPlanet(newId);
						}
					}
				}
				data.extractData(100, DataType.COMPOSITION, ForgeDirection.UP, true);
				data.extractData(100, DataType.DISTANCE, ForgeDirection.UP, true);
				data.extractData(100, DataType.MASS, ForgeDirection.UP, true);

				progress = -1;
			}
		}

	}


	@Override
	public boolean isPlanetKnown(IDimensionProperties properties) {
		SpaceObject obj = getSpaceObject();
		if(obj != null)
			return obj.isPlanetKnown(properties);
		return false;
	}


	@Override
	public boolean isStarKnown(StellarBody body) {
		SpaceObject obj = getSpaceObject();
		if(obj != null)
			return obj.isStarKnown(body);
		return false;
	}
}
