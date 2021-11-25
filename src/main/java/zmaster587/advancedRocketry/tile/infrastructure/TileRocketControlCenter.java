package zmaster587.advancedRocketry.tile.infrastructure;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.AdvancedRocketryTileEntityType;
import zmaster587.advancedRocketry.api.EntityRocketBase;
import zmaster587.advancedRocketry.api.IInfrastructure;
import zmaster587.advancedRocketry.api.IMission;
import zmaster587.advancedRocketry.api.fuel.FuelRegistry;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.entity.EntityRocket;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.LibvulpesGuiRegistry;
import zmaster587.libVulpes.client.util.IndicatorBarImage;
import zmaster587.libVulpes.client.util.ProgressBarImage;
import zmaster587.libVulpes.interfaces.ILinkableTile;
import zmaster587.libVulpes.inventory.ContainerModular;
import zmaster587.libVulpes.inventory.GuiHandler;
import zmaster587.libVulpes.inventory.modules.*;
import zmaster587.libVulpes.items.ItemLinker;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.tile.IComparatorOverride;
import zmaster587.libVulpes.util.IAdjBlockUpdate;
import zmaster587.libVulpes.util.INetworkMachine;
import zmaster587.libVulpes.util.ZUtils.RedstoneState;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.LinkedList;
import java.util.List;

public class TileRocketControlCenter extends TileEntity  implements IModularInventory, ITickableTileEntity, IAdjBlockUpdate, IInfrastructure, ILinkableTile, INetworkMachine, IButtonInventory, IProgressBar, IComparatorOverride {

	EntityRocketBase linkedRocket;
	IMission mission;
	ModuleText missionText;
	RedstoneState state;
	ModuleRedstoneOutputButton redstoneControl;

	int rocketHeight;
	int velocity;
	int fuelLevel, maxFuelLevel;

	public TileRocketControlCenter() {
		super(AdvancedRocketryTileEntityType.TILE_ROCKET_CONTROL_CENTER);
		mission = null;
		missionText = new ModuleText(20, 90, LibVulpes.proxy.getLocalizedString("msg.monitoringstation.missionprogressna"), 0x2b2b2b);
		redstoneControl = new ModuleRedstoneOutputButton(174, 4, "", this);
		state = RedstoneState.ON;
	}

	@Override
	public void remove() {
		super.remove();

		if(linkedRocket != null) {
			linkedRocket.unlinkInfrastructure(this);
			unlinkRocket();
		}
		if(mission != null) {
			mission.unlinkInfrastructure(this);
			unlinkMission();
		}
	}
	
	public boolean getEquivalentPower() {
		if(state == RedstoneState.OFF)
			return false;

		boolean state2 = world.getRedstonePowerFromNeighbors(pos) > 0;

		if(state == RedstoneState.INVERTED)
			state2 = !state2;
		return state2;
	}

	@Override
	public void onAdjacentBlockUpdated() {
		if(!world.isRemote && getEquivalentPower() && linkedRocket != null) {
			linkedRocket.prepareLaunch();
		}
	}

	@Override
	public int getMaxLinkDistance() {
		return 300000;
	}


	public void tick() {
		if (!world.isRemote) {
			if (linkedRocket instanceof EntityRocket) {
				if ((int)(15 * ((EntityRocket) linkedRocket).getRelativeHeightFraction()) != (int)(15 * ((EntityRocket) linkedRocket).getPreviousRelativeHeightFraction())) {
					markDirty();
				}
			}
		}
	}


	@Override
	@ParametersAreNonnullByDefault
	public boolean onLinkStart(ItemStack item, TileEntity entity, PlayerEntity player, World world) {
		ItemLinker.setMasterCoords(item, getPos());

		if(player.world.isRemote)
			Minecraft.getInstance().ingameGUI.getChatGUI().printChatMessage(new TranslationTextComponent("%s %s", new TranslationTextComponent("msg.monitoringstation.link"), ": " + getPos().getX() + " " + getPos().getY() + " " + getPos().getZ()));
		return true;
	}

	@Override
	@ParametersAreNonnullByDefault
	public boolean onLinkComplete(ItemStack item, TileEntity entity,
			PlayerEntity player, World world) {
		if(player.world.isRemote)
			Minecraft.getInstance().ingameGUI.getChatGUI().printChatMessage(new TranslationTextComponent("msg.linker.error.firstmachine"));
		return false;
	}

	@Override
	public void unlinkRocket() {
		linkedRocket = null;
	}

	@Override
	public boolean disconnectOnLiftOff() {
		return false;
	}

	@Override
	public boolean linkRocket(EntityRocketBase rocket) {
		this.linkedRocket = rocket;
		return true;
	}
	
	@Nonnull
	@Override
	public CompoundNBT getUpdateTag() {
		return write(new CompoundNBT());
	}

	@Override
	@ParametersAreNonnullByDefault
	public void read(BlockState blkstate, CompoundNBT nbt) {
		super.read(blkstate, nbt);
		
		state = RedstoneState.values()[nbt.getByte("redstoneState")];
		redstoneControl.setRedstoneState(state);


		if(nbt.contains("missionID")) {
			long id = nbt.getLong("missionID");
			ResourceLocation dimid = new ResourceLocation(nbt.getString("missionDimId"));

			SatelliteBase sat = DimensionManager.getInstance().getSatellite(id);

			if(sat instanceof IMission)
				mission = (IMission)sat;
		}
	}

	@Nonnull
	@Override
	@ParametersAreNonnullByDefault
	public CompoundNBT write(CompoundNBT nbt) {
		super.write(nbt);
		nbt.putByte("redstoneState", (byte) state.ordinal());
		if(mission != null) {
			nbt.putLong("missionID", mission.getMissionId());
			nbt.putString("missionDimId", mission.getOriginatingDimention().toString());
		}
		return nbt;
	}

	@Override
	public void writeDataToNetwork(PacketBuffer out, byte id) {
		if(id == 1)
			out.writeLong(mission == null ? -1 : mission.getMissionId());
		else if(id == 2)
			out.writeByte(state.ordinal());
	}

	@Override
	public void readDataFromNetwork(PacketBuffer in, byte packetId,
			CompoundNBT nbt) {
		if(packetId == 1) {
			nbt.putLong("id", in.readLong());
		}
		else if(packetId == 2) {
			nbt.putByte("state", in.readByte());
		}
	}

	@Override
	public void useNetworkData(PlayerEntity player, Dist side, byte id,
			CompoundNBT nbt) {
		if(id == 1) {
			long idNum = nbt.getLong("id");
			if(idNum == -1) {
				mission = null;
				setMissionText();
			}
			else {
				SatelliteBase base = DimensionManager.getInstance().getSatellite(idNum);

				if(base instanceof IMission) {
					mission = (IMission)base;
					setMissionText();
				}
			}
		}
		else if(id == 2) {
			state = RedstoneState.values()[nbt.getByte("state")];
			redstoneControl.setRedstoneState(state);
		}
		if(id == 100) {
			if(linkedRocket != null)
				linkedRocket.prepareLaunch();
		}
	}

	@Override
	public List<ModuleBase> getModules(int ID, PlayerEntity player) {

		LinkedList<ModuleBase> modules = new LinkedList<>();

		modules.add(new ModuleButton(20, 40, "Launch!", this,  zmaster587.libVulpes.inventory.TextureResources.buttonBuild));
		modules.add(new ModuleProgress(98, 4, 0, new IndicatorBarImage(2, 7, 12, 81, 17, 0, 6, 6, 1, 0, Direction.UP, TextureResources.rocketHud), this));
		modules.add(new ModuleProgress(120, 14, 1, new IndicatorBarImage(2, 95, 12, 71, 17, 0, 6, 6, 1, 0, Direction.UP, TextureResources.rocketHud), this));
		modules.add(new ModuleProgress(142, 14, 2, new ProgressBarImage(2, 173, 12, 71, 17, 6, 3, 69, 1, 1, Direction.UP, TextureResources.rocketHud), this));

		modules.add(redstoneControl);
		setMissionText();

		modules.add(missionText);
		modules.add(new ModuleProgress(30, 110, 3, TextureResources.progressToMission, this));
		modules.add(new ModuleProgress(30, 120, 4, TextureResources.workMission, this));
		modules.add(new ModuleProgress(30, 130, 5, TextureResources.progressFromMission, this));

		if(!world.isRemote) {
			PacketHandler.sendToPlayer(new PacketMachine(this, (byte)1), player);
		}

		return modules;
	}

	private void setMissionText() {
		if(mission != null) {
			int time = mission.getTimeRemainingInSeconds();
			int seconds = time % 60;
			int minutes = (time/60) % 60;
			int hours = time/3600;
			
			missionText.setText(((SatelliteBase)mission).getName() + " " + LibVulpes.proxy.getLocalizedString("msg.monitoringstation.progress") + String.format("\n%02dhr:%02dm:%02ds", hours, minutes, seconds));
		}
		else
			missionText.setText(LibVulpes.proxy.getLocalizedString("msg.monitoringstation.missionprogressna"));
	}
	
	@Override
	public void onInventoryButtonPressed(ModuleButton buttonId) {
		
		if(buttonId == redstoneControl) {
			state = redstoneControl.getState();
			PacketHandler.sendToServer(new PacketMachine(this, (byte)2));
		}
		else
			PacketHandler.sendToServer(new PacketMachine(this, (byte)100) );if(buttonId == redstoneControl) {
			state = redstoneControl.getState();
			PacketHandler.sendToServer(new PacketMachine(this, (byte)2));
		}
		else
			PacketHandler.sendToServer(new PacketMachine(this, (byte)100) );
	}

	@Override
	public String getModularInventoryName() {
		return "block.advancedrocketry.rocketcontrolcenter";
	}

	@Override
	public float getNormallizedProgress(int id) {
		if(id == 1) {
			return Math.max(Math.min(0.5f + (getProgress(id)/(float)getTotalProgress(id)), 1), 0f);
		}
		else if(id == 3) {
			if(mission == null)
				return 0f;
			return (float) Math.min(3f*mission.getProgress(this.world), 1f);
		}
		else if(id == 4) {
			if(mission == null)
				return 0f;
			return (float) Math.min(Math.max( 3f*(mission.getProgress(this.world) - 0.333f), 0f), 1f);
		}
		else if(id == 5) {
			if(mission == null)
				return 0f;
			return (float) Math.min(Math.max( 3f*(mission.getProgress(this.world) - 0.666f), 0f), 1f);
		}
		
		//keep text updated
		if(world.isRemote && mission != null)
			setMissionText();
		
		return getProgress(id)/(float)getTotalProgress(id);
	}

	@Override
	public void setProgress(int id, int progress) {
		if(id == 0)
			rocketHeight = progress;
		else if(id == 1)
			velocity = progress;
		else if(id == 2)
			fuelLevel = progress;
	}

	@Override
	public int getProgress(int id) {
		//Try to keep client synced with server, this also allows us to put the monitor on a different world altogether
		if(world.isRemote)
			if(mission != null && id == 0)
				return getTotalProgress(id);
			else if(id == 0)
				return rocketHeight;
			else if(id == 1)
				return velocity;
			else if(id == 2)
				return fuelLevel;

		if(linkedRocket == null)
			return 0;
		if(id == 0)
			return (int)linkedRocket.getPosY();
		else if(id == 1)
			return (int)(linkedRocket.getMotion().y*100);
		else if (id == 2)
			return (linkedRocket.getRocketFuelType() == FuelRegistry.FuelType.LIQUID_BIPROPELLANT) ? linkedRocket.getFuelAmount(linkedRocket.getRocketFuelType()) + linkedRocket.getFuelAmount(FuelRegistry.FuelType.LIQUID_OXIDIZER) : linkedRocket.getFuelAmount(linkedRocket.getRocketFuelType());

		return 0;
	}

	@Override
	public int getTotalProgress(int id) {
		if(id == 0)
			return ARConfiguration.getCurrentConfig().orbit.get();
		else if(id == 1)
			return 200;
		else if(id == 2)
			if(world.isRemote)
				return maxFuelLevel;
			else if(linkedRocket == null)
				return 0;
		    else
		    	return (linkedRocket.getRocketFuelType() == FuelRegistry.FuelType.LIQUID_BIPROPELLANT) ? linkedRocket.getFuelCapacity(linkedRocket.getRocketFuelType()) + linkedRocket.getFuelCapacity(FuelRegistry.FuelType.LIQUID_OXIDIZER): linkedRocket.getFuelCapacity(linkedRocket.getRocketFuelType());
		return 1;
	}

	@Override
	public void setTotalProgress(int id, int progress) {
		//Should only become an issue if configs are desynced or fuel
		if(id == 2)
			maxFuelLevel = progress;
	}

	@Override
	public boolean canInteractWithContainer(PlayerEntity entity) {
		return true;
	}

	@Override
	public boolean linkMission(IMission misson) {
		this.mission = misson;
		PacketHandler.sendToNearby(new PacketMachine(this, (byte)1), world, getPos(), 16);
		return true;
	}

	@Override
	public void unlinkMission() {
		mission = null;
		setMissionText();
		PacketHandler.sendToNearby(new PacketMachine(this, (byte)1), world, getPos(), 16);
	}

	@Override
	public boolean canRenderConnection() {
		return false;
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
	@ParametersAreNonnullByDefault
	public GuiHandler.guiId getModularInvType() {
		return GuiHandler.guiId.MODULARNOINV;
	}

	public int getComparatorOverride() {
		if (linkedRocket instanceof EntityRocket) {
			return (int) (15 * ((EntityRocket) linkedRocket).getRelativeHeightFraction());
		}
		return 0;
	}

}
