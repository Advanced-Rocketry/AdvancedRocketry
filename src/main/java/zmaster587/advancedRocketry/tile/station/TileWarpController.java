package zmaster587.advancedRocketry.tile.station;

import com.google.common.base.Predicate;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import zmaster587.advancedRocketry.advancements.ARAdvancements;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.Constants;
import zmaster587.advancedRocketry.api.DataStorage.DataType;
import zmaster587.advancedRocketry.api.dimension.IDimensionProperties;
import zmaster587.advancedRocketry.api.dimension.solar.StellarBody;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.inventory.IPlanetDefiner;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.inventory.modules.ModuleData;
import zmaster587.advancedRocketry.inventory.modules.ModulePlanetImage;
import zmaster587.advancedRocketry.inventory.modules.ModulePlanetSelector;
import zmaster587.advancedRocketry.item.ItemData;
import zmaster587.advancedRocketry.item.ItemPlanetIdentificationChip;
import zmaster587.advancedRocketry.network.PacketSpaceStationInfo;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.stations.SpaceStationObject;
import zmaster587.advancedRocketry.tile.multiblock.TileWarpCore;
import zmaster587.advancedRocketry.util.IDataInventory;
import zmaster587.advancedRocketry.world.util.MultiData;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.client.util.IndicatorBarImage;
import zmaster587.libVulpes.inventory.GuiHandler;
import zmaster587.libVulpes.inventory.GuiHandler.guiId;
import zmaster587.libVulpes.inventory.modules.*;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.util.EmbeddedInventory;
import zmaster587.libVulpes.util.HashedBlockPosition;
import zmaster587.libVulpes.util.INetworkMachine;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;

public class TileWarpController extends TileEntity implements ITickable, IModularInventory, ISelectionNotify, INetworkMachine, IButtonInventory, IProgressBar, IDataSync, IGuiCallback, IDataInventory, IPlanetDefiner {

	protected ModulePlanetSelector container;
	private ModuleText canWarp;
	private DimensionProperties dimCache;
	private SpaceStationObject station;
	private static final int ARTIFACT_BEGIN_RANGE = 4, ARTIFACT_END_RANGE = 8;
	private ModulePlanetImage srcPlanetImg, dstPlanetImg;
	private ModuleSync sync1, sync2, sync3;
	private ModuleText srcPlanetText, dstPlanetText, warpFuel, status, warpCapacity;
	private int warpCost = -1;
	private int dstPlanet, srcPlanet;
	private ModuleTab tabModule;
	private static final byte TAB_SWITCH = 4, STORE_DATA = 10, LOAD_DATA = 20, SEARCH = 5, PROGRAMFROMCHIP = 6;
	private MultiData data;
	private EmbeddedInventory inv;
	private static final int DISTANCESLOT = 0, MASSSLOT = 1, COMPOSITION = 2, PLANETSLOT = 3, MAX_PROGRESS = 1000;
	private ModuleProgress programmingProgress;
	private int progress;

	public TileWarpController() {
		tabModule = new ModuleTab(4,0,0,this, 3, new String[]{LibVulpes.proxy.getLocalizedString("msg.warpmon.tab.warp"), LibVulpes.proxy.getLocalizedString("msg.warpmon.tab.data"), LibVulpes.proxy.getLocalizedString("msg.warpmon.tab.tracking")}, new ResourceLocation[][] { TextureResources.tabWarp, TextureResources.tabData, TextureResources.tabPlanetTracking} );
		data = new MultiData();
		data.setMaxData(10000);
		inv = new EmbeddedInventory(9);
		programmingProgress = new ModuleProgress(35, 80, 3, TextureResources.terraformProgressBar, this);
		progress = -1;
	}


	private SpaceStationObject getSpaceObject() {
		if(station == null && world.provider.getDimension() == ARConfiguration.getCurrentConfig().spaceDimId) {
			ISpaceObject spaceObject = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(pos);
			if(spaceObject instanceof SpaceStationObject)
				station = (SpaceStationObject) spaceObject;
		}
		return station;
	}


	protected int getTravelCost() {
		if(getSpaceObject() != null) {
			DimensionProperties properties = getSpaceObject().getProperties().getParentProperties();

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

	public int getTravelCostToDimension(int destinationID) {
		if(getSpaceObject() != null) {
			DimensionProperties properties = getSpaceObject().getProperties().getParentProperties();

			DimensionProperties destProperties = DimensionManager.getInstance().getDimensionProperties(destinationID);

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
	public int addData(int maxAmount, DataType type, EnumFacing dir,
			boolean commit) {
		return data.addData(maxAmount, type, dir, commit);
	}

	@Override
	public int extractData(int maxAmount, DataType type, EnumFacing dir,
			boolean commit) {
		return data.extractData(maxAmount, type, dir, commit);
	}

	@Override
	public List<ModuleBase> getModules(int ID, EntityPlayer player) {
		List<ModuleBase> modules = new LinkedList<>();

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

				if(world.isRemote)
					setPlanetModuleInfo();

				//Source planet
				int baseX = 10;
				int baseY = 20;
				int sizeX = 70;
				int sizeY = 70;

				if(world.isRemote) {
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
				flag = flag && !(getSpaceObject().getDestOrbitingBody() == Constants.INVALID_PLANET || getSpaceObject().getOrbitingPlanetId() == getSpaceObject().getDestOrbitingBody());
				boolean artifactFlag = (dimCache != null && meetsArtifactReq(dimCache));

				canWarp = new ModuleText(baseX, baseY + sizeY + 30,
						(isOnStation && getSpaceObject().isAnchored()) ? LibVulpes.proxy.getLocalizedString("msg.warpmon.anchored") :
						((isOnStation && (getSpaceObject().getDestOrbitingBody() == Constants.INVALID_PLANET || getSpaceObject().getOrbitingPlanetId() == getSpaceObject().getDestOrbitingBody())) ? LibVulpes.proxy.getLocalizedString("msg.warpmon.nowhere") :
						(!artifactFlag ? LibVulpes.proxy.getLocalizedString("msg.warpmon.missingart") :
						(flag ? LibVulpes.proxy.getLocalizedString("msg.warpmon.ready") :
						LibVulpes.proxy.getLocalizedString("msg.warpmon.notready")))), flag && artifactFlag && !getSpaceObject().isAnchored() ? 0x1baa1b : 0xFF1b1b);
				modules.add(canWarp);
				modules.add(new ModuleProgress(baseX, baseY + sizeY + 40, 10, new IndicatorBarImage(70, 58, 53, 8, 122, 58, 5, 8, EnumFacing.EAST, TextureResources.progressBars), this));
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

				if(!world.isRemote && isOnStation) {
					PacketHandler.sendToPlayer(new PacketSpaceStationInfo(getSpaceObject().getId(), getSpaceObject()), player);
				}


				if(world.isRemote) {
					warpFuel.setText(LibVulpes.proxy.getLocalizedString("msg.warpmon.fuelcost") + (flag ? String.valueOf(warpCost) : LibVulpes.proxy.getLocalizedString("msg.warpmon.na")));
					warpCapacity.setText(LibVulpes.proxy.getLocalizedString("msg.warpmon.fuel") + (isOnStation ? getSpaceObject().getFuelAmount() : LibVulpes.proxy.getLocalizedString("msg.warpmon.na")));
					modules.add(warpFuel);
					modules.add(warpCapacity);

					modules.add(new ModuleScaledImage(baseX,baseY,sizeX,sizeY, zmaster587.libVulpes.inventory.TextureResources.starryBG));
					
					if(dimCache != null && world.isRemote) {
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
				modules.add(new ModuleSlotArray(105, 60, this, 7, 8));
				modules.add(new ModuleSlotArray(130, 35, this, 8, 9));

				modules.add(new ModuleButton(50, 117, 3, LibVulpes.proxy.getLocalizedString("msg.warpmon.search"), this,  zmaster587.libVulpes.inventory.TextureResources.buttonBuild, LibVulpes.proxy.getLocalizedString("msg.warpmon.datareq"), 100, 10));
				modules.add(new ModuleButton(50, 127, 4, LibVulpes.proxy.getLocalizedString("msg.warpmon.chip"), this, zmaster587.libVulpes.inventory.TextureResources.buttonBuild,100, 10));
				modules.add(new ModuleTexturedSlotArray(30, 120, this, 3, 4, TextureResources.idChip));
				modules.add(programmingProgress);
			}
		}
		else if (ID == guiId.MODULARFULLSCREEN.ordinal()) {
			//Open planet selector menu
			SpaceStationObject station = getSpaceObject();
			int starId = 0;
			if(station != null)
				starId = station.getProperties().getParentProperties().getStar().getId();
			container = new ModulePlanetSelector(starId, zmaster587.libVulpes.inventory.TextureResources.starryBG, this, this, true);
			container.setOffset(1000, 1000);
			container.setAllowStarSelection(true);
			modules.add(container);
		}

		return modules;
	}

	private void setPlanetModuleInfo() {

		SpaceStationObject station = getSpaceObject();
		boolean isOnStation = station != null;
		DimensionProperties location;
		String planetName;

		if(isOnStation) {
			DimensionProperties properties = DimensionManager.getInstance().getDimensionProperties(station.getOrbitingPlanetId());
			location = properties;
			planetName = properties.getName();
		}
		else {
			location = DimensionManager.getInstance().getDimensionProperties(world.provider.getDimension());
			planetName = DimensionManager.getInstance().getDimensionProperties(world.provider.getDimension()).getName();

			if(planetName.isEmpty())
				planetName = "???";
		}

		boolean flag = isOnStation && getSpaceObject().getFuelAmount() >= warpCost && getSpaceObject().hasUsableWarpCore();

		if(canWarp != null) {
			flag = flag && !(getSpaceObject().getDestOrbitingBody() == Constants.INVALID_PLANET || getSpaceObject().getOrbitingPlanetId() == getSpaceObject().getDestOrbitingBody());
			boolean artifactFlag = (dimCache != null && meetsArtifactReq(dimCache));
			
			canWarp.setText(
				(isOnStation && getSpaceObject().isAnchored()) ? LibVulpes.proxy.getLocalizedString("msg.warpmon.anchored") :
				(isOnStation && (getSpaceObject().getDestOrbitingBody() == Constants.INVALID_PLANET || getSpaceObject().getOrbitingPlanetId() == getSpaceObject().getDestOrbitingBody()) ? LibVulpes.proxy.getLocalizedString("msg.warpmon.nowhere") :
				(!artifactFlag ? LibVulpes.proxy.getLocalizedString("msg.warpmon.missingart") : 
				(flag ? LibVulpes.proxy.getLocalizedString("msg.warpmon.ready") : LibVulpes.proxy.getLocalizedString("msg.warpmon.notready")))));
			canWarp.setColor(flag && artifactFlag && !getSpaceObject().isAnchored() ? 0x1baa1b : 0xFF1b1b);
		}


		if(world.isRemote) {
			if(srcPlanetImg == null ) {
				//Source planet
				int baseX = 10;
				int baseY = 20;
				int sizeX = 65;
				int sizeY = 65;

				srcPlanetImg = new ModulePlanetImage(baseX + 10,baseY + 10,sizeX - 20, location);
				srcPlanetText = new ModuleText(baseX + 4, baseY + 56, "", 0xFFFFFF);
				srcPlanetText.setAlwaysOnTop(true);
				warpFuel = new ModuleText(baseX + 100, baseY + sizeY + 25, "", 0x1b1b1b);
				warpCapacity = new ModuleText(baseX + 100, baseY + sizeY + 35, "", 0x1b1b1b);

				//DEST planet
				baseX = 94;
				baseY = 20;
				sizeX = 65;
				sizeY = 65;

				dstPlanetImg = new ModulePlanetImage(baseX + 10,baseY + 10,sizeX - 20, location);
				dstPlanetText = new ModuleText(baseX + 4, baseY + 56, "", 0xFFFFFF);
				dstPlanetText.setAlwaysOnTop(true);

			}

			srcPlanetImg.setDimProperties(location);
			srcPlanetText.setText(planetName);


			warpFuel.setText(LibVulpes.proxy.getLocalizedString("msg.warpmon.fuelcost") + (warpCost < Integer.MAX_VALUE ? String.valueOf(warpCost) : LibVulpes.proxy.getLocalizedString("msg.warpmon.na")));
			warpCapacity.setText(LibVulpes.proxy.getLocalizedString("msg.warpmon.fuel") + (isOnStation ? station.getFuelAmount() : LibVulpes.proxy.getLocalizedString("msg.warpmon.na")));



			DimensionProperties dstProps = null;
			if(isOnStation && station.getOrbitingPlanetId() != SpaceObjectManager.WARPDIMID )
				dstProps = DimensionManager.getInstance().getDimensionProperties(dstPlanet);

			if(dstProps != null) {
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
				PacketHandler.sendToServer(new PacketMachine(this, SEARCH));
			}
			else if(buttonId == 4) {
				PacketHandler.sendToServer(new PacketMachine(this, PROGRAMFROMCHIP));
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
			player.openGui(LibVulpes.instance, guiId.MODULARFULLSCREEN.ordinal(), world, this.getPos().getX(), this.getPos().getY(), this.getPos().getZ());
		else if(id == 1 || id == 3) {
			int dimId = nbt.getInteger("id");

			if(isPlanetKnown(DimensionManager.getInstance().getDimensionProperties(dimId))) {
				container.setSelectedSystem(dimId);
				selectSystem(dimId);
			}

			//Update known planets
			markDirty();
			if(id == 3)
				player.openGui(LibVulpes.instance, guiId.MODULARNOINV.ordinal(), world, this.getPos().getX(), this.getPos().getY(), this.getPos().getZ());
		}
		else if(id == 2) {
			final SpaceStationObject station = getSpaceObject();

			if(station != null && !station.isAnchored() && station.hasUsableWarpCore() && station.useFuel(getTravelCost()) != 0 && meetsArtifactReq(DimensionManager.getInstance().getDimensionProperties(station.getDestOrbitingBody()))) {
				SpaceObjectManager.getSpaceManager().moveStationToBody(station, station.getDestOrbitingBody(), Math.max(Math.min(getTravelCost()*5, 5000),0));

				for (EntityPlayer player2 : world.getPlayers(EntityPlayer.class, new Predicate<EntityPlayer>() {
					public boolean apply(EntityPlayer input) {
						return SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(input.getPosition()) == station;
					}
				})) {
					ARAdvancements.ALL_SHE_GOT.trigger((EntityPlayerMP) player2);
					if(!DimensionManager.hasReachedWarp)
						ARAdvancements.FLIGHT_OF_PHOENIX.trigger((EntityPlayerMP) player2);
				}

				DimensionManager.hasReachedWarp = true;

				for(HashedBlockPosition vec : station.getWarpCoreLocations()) {
					TileEntity tile = world.getTileEntity(vec.getBlockPos());
					if(tile instanceof TileWarpCore) {
						((TileWarpCore)tile).onInventoryUpdated();
					}
				}
			}
		}
		else if(id == TAB_SWITCH && !world.isRemote) {
			tabModule.setTab(nbt.getShort("tab"));
			player.openGui(LibVulpes.instance, GuiHandler.guiId.MODULARNOINV.ordinal(), getWorld(), pos.getX(), pos.getY(), pos.getZ());
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
			SpaceStationObject spaceStationObject = getSpaceObject();
			if(spaceStationObject != null) {
				ItemStack stack = getStackInSlot(PLANETSLOT);
				if(!stack.isEmpty() && stack.getItem() instanceof ItemPlanetIdentificationChip) {
					if(DimensionManager.getInstance().isDimensionCreated(((ItemPlanetIdentificationChip)stack.getItem()).getDimensionId(stack)))
						spaceStationObject.discoverPlanet(((ItemPlanetIdentificationChip)stack.getItem()).getDimensionId(stack));
				}
			}
		}
	}

	@Override
	@Nonnull
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		inv.writeToNBT(compound);
		data.writeToNBT(compound);
		compound.setInteger("progress", progress);
		return super.writeToNBT(compound);
	}

	@Override
	@Nonnull
	public NBTTagCompound getUpdateTag() {
		return writeToNBT(new NBTTagCompound());
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
		if(world.isRemote) {
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
			return 30;
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
	public int getSizeInventory() {
		return inv.getSizeInventory();
	}


	@Override
	@Nonnull
	public ItemStack getStackInSlot(int index) {
		return inv.getStackInSlot(index);
	}


	@Override
	@Nonnull
	public ItemStack decrStackSize(int index, int count) {
		return inv.decrStackSize(index, count);
	}


	@Override
	@Nonnull
	public ItemStack removeStackFromSlot(int index) {
		return inv.removeStackFromSlot(index);
	}


	@Override
	public void setInventorySlotContents(int index, @Nonnull ItemStack stack) {
		inv.setInventorySlotContents(index, stack);

	}


	@Override
	public int getInventoryStackLimit() {
		return inv.getInventoryStackLimit();
	}


	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public boolean isEmpty() {
		return inv.isEmpty();
	}

	@Override
	public void openInventory(EntityPlayer player) {
		inv.openInventory(player);

	}


	@Override
	public void closeInventory(EntityPlayer player) {
		inv.closeInventory(player);

	}


	@Override
	public boolean isItemValidForSlot(int index, @Nonnull ItemStack stack) {
		return inv.isItemValidForSlot(index, stack);
	}


	@Override
	public int getField(int id) {
		return 0;
	}


	@Override
	public void setField(int id, int value) {

	}


	@Override
	public int getFieldCount() {
		return 0;
	}


	@Override
	public void clear() {

	}


	@Override
	@Nonnull
	public String getName() {
		return getModularInventoryName();
	}


	@Override
	public boolean hasCustomName() {
		return false;
	}


	@Override
	public void loadData(int id) {
		ItemStack stack = ItemStack.EMPTY;
		
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

		
		
		if(!stack.isEmpty() && stack.getItem() instanceof ItemData) {
			ItemData item = (ItemData) stack.getItem();
			if(item.getDataType(stack) == type)
				item.removeData(stack, this.addData(item.getData(stack), item.getDataType(stack), EnumFacing.UP, true), type);
		}

		if(world.isRemote) {
			PacketHandler.sendToServer(new PacketMachine(this, (byte)(LOAD_DATA + id)));
		}
	}


	@Override
	public void storeData(int id) {
		ItemStack stack = ItemStack.EMPTY;
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

		if(!stack.isEmpty() && stack.getItem() instanceof ItemData) {
			ItemData item = (ItemData) stack.getItem();
			data.extractData(item.addData(stack, data.getDataAmount(type), type), type, EnumFacing.UP, true);
		}

		if(world.isRemote) {
			PacketHandler.sendToServer(new PacketMachine(this, (byte)(STORE_DATA + id)));
		}
	}

	private boolean meetsArtifactReq(DimensionProperties properties) {
		//Make sure we have all the artifacts
		
		if(properties.getRequiredArtifacts().isEmpty())
			return true;
		
		List<ItemStack> list = new LinkedList<>(properties.getRequiredArtifacts());
		for(int i = ARTIFACT_BEGIN_RANGE; i <= ARTIFACT_END_RANGE; i++) {
			ItemStack stack2 = getStackInSlot(i);
			if(!stack2.isEmpty()) {
				list.removeIf(stackInList -> stackInList.getItem().equals(stack2.getItem()) && stackInList.getItemDamage() == stack2.getItemDamage()
						&& ItemStack.areItemStackTagsEqual(stackInList, stack2) && stack2.getCount() >= stackInList.getCount());
			}
		}
		
		return list.isEmpty();
	}

	public boolean itemListContainsRequiredArtifacts(List<ItemStack> items, DimensionProperties properties) {
		if(properties.getRequiredArtifacts().isEmpty()) return true;

		List<ItemStack> list = new LinkedList<>(properties.getRequiredArtifacts());
		boolean hasArtifacts = true;

		for (ItemStack item : items) {
			boolean foundArtifact = false;
			for (ItemStack item2 : list) {
				if(item.getItem() == item2.getItem() && item.getItemDamage() == item2.getItemDamage() && ItemStack.areItemStackTagsEqual(item, item2) && item.getCount() >= item2.getCount()) {
	                foundArtifact = true;
				}
			}
			hasArtifacts = foundArtifact;
		}
		return hasArtifacts;
	}
	
	@Override
	public void update() {
		if(!world.isRemote && progress != -1) {
			progress++;
			if(progress >= MAX_PROGRESS) {
				//Do the thing
				SpaceStationObject spaceStationObject = getSpaceObject();
				if(Math.abs(world.rand.nextInt()) % ARConfiguration.getCurrentConfig().planetDiscoveryChance == 0 && spaceStationObject != null) {
					ItemStack stack = getStackInSlot(PLANETSLOT);
					if(!stack.isEmpty() && stack.getItem() instanceof ItemPlanetIdentificationChip) {
						ItemPlanetIdentificationChip item = (ItemPlanetIdentificationChip)stack.getItem();
						List<Integer> unknownPlanets = new LinkedList<>();
						
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
							int newId = (int)(world.rand.nextFloat()*unknownPlanets.size());
							newId = unknownPlanets.get(newId);
							item.setDimensionId(stack, newId);
							spaceStationObject.discoverPlanet(newId);
						}
					}
				}
				data.extractData(100, DataType.COMPOSITION, EnumFacing.UP, true);
				data.extractData(100, DataType.DISTANCE, EnumFacing.UP, true);
				data.extractData(100, DataType.MASS, EnumFacing.UP, true);

				progress = -1;
			}
		}

	}


	@Override
	public boolean isPlanetKnown(IDimensionProperties properties) {
		SpaceStationObject spaceStationObject = getSpaceObject();
		if(spaceStationObject != null)
			return spaceStationObject.isPlanetKnown(properties);
		return false;
	}


	@Override
	public boolean isStarKnown(StellarBody body) {
		SpaceStationObject spaceStationObject = getSpaceObject();
		if(spaceStationObject != null)
			return spaceStationObject.isStarKnown(body);
		return false;
	}
}
