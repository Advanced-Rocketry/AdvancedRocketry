package zmaster587.advancedRocketry.tile.station;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import zmaster587.advancedRocketry.api.AdvancedRocketryTileEntityType;
import zmaster587.advancedRocketry.api.Constants;
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
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.LibvulpesGuiRegistry;
import zmaster587.libVulpes.inventory.ContainerModular;
import zmaster587.libVulpes.inventory.GuiHandler;
import zmaster587.libVulpes.inventory.GuiHandler.guiId;
import zmaster587.libVulpes.inventory.modules.*;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.util.INetworkMachine;
import zmaster587.libVulpes.util.ZUtils.RedstoneState;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class TileHolographicPlanetSelector extends TileEntity implements ITickableTileEntity ,IButtonInventory, IModularInventory, ISliderBar, INetworkMachine {


	private List<EntityUIPlanet> entities;
	private List<EntityUIStar> starEntities;
	private EntityUIPlanet centeredEntity;
	private EntityUIPlanet selectedPlanet;
	private EntityUIStar currentStar;
	private EntityUIButton backButton;
	private StellarBody currentStarBody;

	private ModuleRedstoneOutputButton redstoneControl;
	private RedstoneState state;
	private ResourceLocation selectedId;
	private float onTime;
	private ModuleText targetGrav;
	private float size;
	private static final byte SCALEPACKET = 0;
	private static final byte STATEUPDATE = 1;
	private boolean allowUpdate = true;  //Hack to get around the delay in entity position
	private boolean stellarMode;

	public TileHolographicPlanetSelector() {
		super(AdvancedRocketryTileEntityType.TILE_HOLOGRAM);
		entities = new LinkedList<>();
		starEntities = new LinkedList<>();
		targetGrav = new ModuleText(6, 45, LibVulpes.proxy.getLocalizedString("msg.planetholo.size"), 0x202020);
		selectedPlanet = null;
		stellarMode = false;
		selectedId = Constants.INVALID_PLANET;
		onTime = 1f;
		size = 0.02f;
		redstoneControl = (ModuleRedstoneOutputButton) new ModuleRedstoneOutputButton(174, 4, "", this).setAdditionalData(1);
		state = RedstoneState.OFF;
		redstoneControl.setRedstoneState(state);
	}

	@Override
	public void remove() {
		super.remove();
		cleanup();
	}

	private void cleanup() {
		for(EntityUIPlanet planet : entities) {
			planet.remove();
		}
		entities.clear();

		for(EntityUIStar star : starEntities) star.remove();
		starEntities.clear();

		selectedPlanet = null;
		centeredEntity = null;
		//currentStarBody = null;
		selectedId = Constants.INVALID_PLANET;


		if(currentStar != null) {
			currentStar.remove();
			currentStar = null;
		}
		if(backButton != null) {
			backButton.remove();
			backButton = null;
		}
	}

	public boolean isEnabled() {
		boolean powered = world.getRedstonePowerFromNeighbors(getPos()) > 0;
		return (!powered && state == RedstoneState.INVERTED) || (powered && state == RedstoneState.ON) || state == RedstoneState.OFF;
	}

	@Override
	public void tick() {
		if(!world.isRemote) {
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
					else {
						if(!starEntities.isEmpty()) {
							float phaseInc = 4 * 360f / starEntities.size();
							float phase = 0;
							for(EntityUIStar entity : starEntities) {
								double deltaX, deltaY;
								deltaX = (entity.getStarProperties().getStarSeparation()*MathHelper.cos(phase)*0.05);
								deltaY = (entity.getStarProperties().getStarSeparation()*MathHelper.sin(phase)*0.05);

								entity.setPosition(this.pos.getX() + .5 + getInterpHologramSize()*deltaX, this.pos.getY() + 1, this.pos.getZ() + .5 + getInterpHologramSize()*deltaY);
								entity.setScale(getInterpHologramSize()*entity.getStarProperties().getSize());
								phase += phaseInc;
							}
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

	public void selectSystem(ResourceLocation id) {

		if(DimensionManager.getInstance().isStar(id)) {
			if(stellarMode) {
				if(selectedId != id) {
					for(EntityUIStar entity : starEntities) {
						if(entity.getPlanetID() == id) {
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
					currentStarBody = DimensionManager.getInstance().getStar(id);
					rebuildSystem();
					selectedId = Constants.INVALID_PLANET;
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

	public void selectSystemWithoutTargeting(ResourceLocation id) {
		if (DimensionManager.getInstance().isStar(id)) {
			if (stellarMode) {
				if (selectedId != id) {
					for (EntityUIStar entity : starEntities) {
						if (entity.getPlanetID() == id) {
							entity.setSelected(true);
							selectedPlanet = entity;
						} else
							entity.setSelected(false);
					}
					selectedId = id;
				} else {
					stellarMode = false;
					currentStarBody = DimensionManager.getInstance().getStar(id);
					rebuildSystem();
					selectedId = Constants.INVALID_PLANET;
				}
			}

		} else {

			if (selectedPlanet != null && selectedPlanet.getPlanetID() == id) {
				centeredEntity = selectedPlanet;
				stellarMode = false;
				rebuildSystem();
			} else
				for (EntityUIPlanet entity : entities) {
					if (entity.getPlanetID() == id) {
						entity.setSelected(true);
						selectedPlanet = entity;
					} else
						entity.setSelected(false);
				}
		}
	}


	private void rebuildSystem() {
		onTime = 0;
		for(EntityUIPlanet entity : entities)
			entity.remove();

		for(EntityUIStar body : starEntities)
			body.remove();

		starEntities.clear();
		entities.clear();
		selectedPlanet = null;

		if(backButton == null) {
			backButton = new EntityUIButton(world, DimensionManager.overworldProperties.getId(), this);
			backButton.setPosition(this.pos.getX() + .5, this.pos.getY() + 1.5, this.pos.getZ() + .5);
			this.getWorld().addEntity(backButton);
		}

		if(!stellarMode) {
			List<IDimensionProperties> planetList = currentStarBody == null ? DimensionManager.getInstance().getStar(new ResourceLocation(Constants.STAR_NAMESPACE, "0")).getPlanets() : currentStarBody.getPlanets();
			if(centeredEntity != null) {
				planetList = new LinkedList<>();
				planetList.add(centeredEntity.getProperties());

				for(ResourceLocation id : centeredEntity.getProperties().getChildPlanets())
					planetList.add(DimensionManager.getInstance().getDimensionProperties(id));

				if(currentStar != null) {
					currentStar.remove();
					currentStar = null;
				}

			}
			else {
				if(currentStarBody == null)
					currentStarBody = DimensionManager.getInstance().getStar(new ResourceLocation(Constants.STAR_NAMESPACE, "0"));
				currentStar = new EntityUIStar(world, currentStarBody, this, this.pos.getX() + .5, this.pos.getY() + 1, this.pos.getZ() + .5);
				this.getWorld().addEntity(currentStar);

				//Spawn substars
				if(currentStarBody.getSubStars() != null && !currentStarBody.getSubStars().isEmpty()) {
					float phaseInc = 360f / currentStarBody.getSubStars().size();
					float phase = 0;
					int count = 0;
					Collection<StellarBody> starList = currentStarBody.getSubStars();
					for(StellarBody body : starList) {

						double deltaX, deltaY;
						deltaX =  (body.getStarSeparation()*MathHelper.cos(phase)*0.05);
						deltaY =  (body.getStarSeparation()*MathHelper.sin(phase)*0.05);
						EntityUIStar entity = new EntityUIStar(world, body, count++, this, this.pos.getX() + .5 + deltaX, this.pos.getY() + 1, this.pos.getZ() + .5 + deltaY);

						this.getWorld().addEntity(entity);
						starEntities.add(entity);
						phase += phaseInc;
					}
				}
			}

			for(IDimensionProperties properties : planetList) {
				EntityUIPlanet entity = new EntityUIPlanet(world, (DimensionProperties)properties, this, this.pos.getX() + .5, this.pos.getY() + 1, this.pos.getZ() + .5);
				//entity.setPositionPolar(this.pos.getX() + .5, this.pos.getY() + 10, this.pos.getZ() + .5,  ((DimensionProperties)properties).orbitalDist/100f, ( (DimensionProperties)properties).orbitTheta);
				this.getWorld().addEntity(entity);
				entities.add(entity);

				if(centeredEntity != null && properties == centeredEntity.getProperties())
					centeredEntity = entity;
			}
		}
		else {

			if(currentStar != null) {
				currentStar.remove();
				currentStar = null;
			}

			Collection<StellarBody> starList = DimensionManager.getInstance().getStars();

			for(StellarBody body : starList) {
				EntityUIStar entity = new EntityUIStar(world, body, this, this.pos.getX() + .5, this.pos.getY() + 1, this.pos.getZ() + .5);

				this.getWorld().addEntity(entity);
				starEntities.add(entity);
			}
		}
		//Hack to delay position updates by a tick
		allowUpdate = false;
	}


	@Override
	public List<ModuleBase> getModules(int id, PlayerEntity player) {
		List<ModuleBase> modules = new LinkedList<>();

		modules.add(targetGrav);
		modules.add(new ModuleSlider(6, 60, 0, TextureResources.doubleWarningSideBarIndicator, this));
		modules.add(redstoneControl);

		updateText();
		return modules;
	}

	private void updateText() {
		if(world.isRemote) {

			//numThrusters.setText("Number Of Thrusters: 0");
			targetGrav.setText(String.format("%s %f", LibVulpes.proxy.getLocalizedString("msg.planetholo.size"), getHologramSize()));
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
		return "block.advancedrocketry.holographicplanetselector";
	}

	@Override
	public boolean canInteractWithContainer(PlayerEntity entity) {
		return true;
	}

	@Override
	public float getNormallizedProgress(int id) {
		return size;
	}

	@Override
	public void setProgress(int id, int progress) { size = progress/100f; }

	@Override
	public int getProgress(int id) {
		return (int)(size * 100);
	}

	@Override
	public int getTotalProgress(int id) {
		return 100;
	}

	@Override
	public void setTotalProgress(int id, int progress) { }

	@Override
	public void setProgressByUser(int id, int progress) {
		size = progress/100f;
		PacketHandler.sendToServer(new PacketMachine(this, SCALEPACKET));
		updateText();
	}

	public ResourceLocation getCurrentPlanetID () {return selectedPlanet.getPlanetID();}

	public ResourceLocation getCurrentStarID() {return currentStar.getPlanetID();}

	@Override
	public void writeDataToNetwork(PacketBuffer out, byte id) {
		if(id == SCALEPACKET) {
			out.writeFloat(size);
		}
		if(id == STATEUPDATE) {
			out.writeByte(state.ordinal());
		}

	}

	@Override
	public void readDataFromNetwork(PacketBuffer in, byte packetId,
			CompoundNBT nbt) {
		if(packetId == SCALEPACKET) {
			nbt.putFloat("scale", in.readFloat());
		}
		else if(packetId == STATEUPDATE) {
			nbt.putByte("state", in.readByte());
		}

	}

	@Override
	public void useNetworkData(PlayerEntity player, Dist side, byte id,
			CompoundNBT nbt) {
		if(id == SCALEPACKET) {
			size = nbt.getFloat("scale");
		}
		else if (id == STATEUPDATE) {
			state = RedstoneState.values()[nbt.getByte("state")];
		}
	}

	public void onInventoryButtonPressed(ModuleButton button) {
		
		int buttonId = (int)button.getAdditionalData();
		
		//Back button
		if(buttonId == 0) {
			if(currentStar != null)
				stellarMode = true;
			selectedPlanet = null; centeredEntity = null;
			rebuildSystem();
		}
		else if(buttonId == 1) {
			state = redstoneControl.getState();
			PacketHandler.sendToServer(new PacketMachine(this, STATEUPDATE));
		}
	}

	@Nonnull
	@Override
	@ParametersAreNonnullByDefault
	public CompoundNBT write(CompoundNBT compound) {
		compound = super.write(compound);
		state.write(compound);

		return compound;
	}

	@Override
	@ParametersAreNonnullByDefault
	public void read(BlockState blkstate, CompoundNBT compound) {
		super.read(blkstate, compound);
		state = RedstoneState.createFromNBT(compound);
		redstoneControl.setRedstoneState(state);
	}

	@Nonnull
	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent(getModularInventoryName());
	}

	@Override
	@ParametersAreNonnullByDefault
	public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) {
		return new ContainerModular(LibvulpesGuiRegistry.CONTAINER_MODULAR_TILE, id, player, getModules(getModularInvType().ordinal(), player), this, getModularInvType());
	}

	@Override
	public GuiHandler.guiId getModularInvType() {
		return guiId.MODULAR;
	}
}