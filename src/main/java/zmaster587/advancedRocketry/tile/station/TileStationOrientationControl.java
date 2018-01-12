package zmaster587.advancedRocketry.tile.station;

import io.netty.buffer.ByteBuf;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.relauncher.Side;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.network.PacketStationUpdate;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.world.provider.WorldProviderSpace;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.inventory.modules.ISliderBar;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleSlider;
import zmaster587.libVulpes.inventory.modules.ModuleText;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.util.INetworkMachine;

public class TileStationOrientationControl extends TileEntity implements ITickable, IModularInventory, INetworkMachine, ISliderBar {

	int numRotationsPerHour[];
	int progress[];

	private ModuleText moduleAngularVelocity, numThrusters, maxAngularAcceleration, targetRotations;

	public TileStationOrientationControl() {
		moduleAngularVelocity = new ModuleText(6, 15, LibVulpes.proxy.getLocalizedString("msg.stationorientctrl.alt"), 0xaa2020);
		//numThrusters = new ModuleText(10, 25, "Number Of Thrusters: ", 0xaa2020);
		targetRotations = new ModuleText(6, 25, LibVulpes.proxy.getLocalizedString("msg.stationorientctrl.tgtalt"), 0x202020);
		progress = new int[3];
		numRotationsPerHour = new int[3];

		progress[0] = getTotalProgress(0)/2;
		progress[1] = getTotalProgress(1)/2;
		progress[2] = getTotalProgress(2)/2;
	}

	@Override
	public List<ModuleBase> getModules(int id, EntityPlayer player) {
		List<ModuleBase> modules = new LinkedList<ModuleBase>();
		modules.add(moduleAngularVelocity);
		//modules.add(numThrusters);
		//modules.add(maxAngularAcceleration);
		modules.add(targetRotations);
		
		modules.add(new ModuleText(10, 54, "X:", 0x202020));
		modules.add(new ModuleText(10, 69, "Y:", 0x202020)); //AYYYY
		
		modules.add(new ModuleSlider(24, 50, 0, TextureResources.doubleWarningSideBarIndicator, (ISliderBar)this));
		modules.add(new ModuleSlider(24, 65, 1, TextureResources.doubleWarningSideBarIndicator, (ISliderBar)this));
		//modules.add(new ModuleSlider(24, 35, 2, TextureResources.doubleWarningSideBarIndicator, (ISliderBar)this));

		updateText();
		return modules;
	}

	private void updateText() {
		if(worldObj.isRemote) {
			ISpaceObject object = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(pos);
			if(object != null) {
				moduleAngularVelocity.setText(String.format("%s%.1f %.1f %.1f", LibVulpes.proxy.getLocalizedString("msg.stationorientctrl.alt"), 72000D*object.getDeltaRotation(EnumFacing.EAST), 72000D*object.getDeltaRotation(EnumFacing.UP), 7200D*object.getDeltaRotation(EnumFacing.NORTH)));
				//maxAngularAcceleration.setText(String.format("Maximum Angular Acceleration: %.1f", 7200D*object.getMaxRotationalAcceleration()));
			}

			//numThrusters.setText("Number Of Thrusters: 0");

			targetRotations.setText(String.format("%s%d %d %d", LibVulpes.proxy.getLocalizedString("msg.stationorientctrl.tgtalt"), numRotationsPerHour[0], numRotationsPerHour[1], numRotationsPerHour[2]));
		}
	}

	@Override
	public void update() {

		if(this.worldObj.provider instanceof WorldProviderSpace) {
			if(!worldObj.isRemote) {
				ISpaceObject object = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(pos);
				boolean update = false;

				if(object != null) {
					EnumFacing dirs[] = { EnumFacing.EAST, EnumFacing.UP, EnumFacing.NORTH };
					for(int i = 0; i < 3; i++) {
						double targetAngularVelocity = numRotationsPerHour[i]/72000D;
						double angVel = object.getDeltaRotation(dirs[i]);
						double acc = object.getMaxRotationalAcceleration();

						double difference = targetAngularVelocity - angVel;

						if(difference != 0) {
							double finalVel = angVel;
							if(difference < 0) {
								finalVel = angVel + Math.max(difference, -acc);
							}
							else if(difference > 0) {
								finalVel = angVel + Math.min(difference, acc);
							}

							object.setDeltaRotation(finalVel, dirs[i]);
							update = true;
						}
					}
					
					if(!worldObj.isRemote && update) {
						//PacketHandler.sendToNearby(new PacketStationUpdate(object, PacketStationUpdate.Type.ROTANGLE_UPDATE), this.worldObj.provider.dimensionId, this.xCoord, this.yCoord, this.zCoord, 1024);
						PacketHandler.sendToAll(new PacketStationUpdate(object, PacketStationUpdate.Type.ROTANGLE_UPDATE));
					}
				}
				else
					updateText();
			}
			else
				updateText();
		}
	}
	@Override
	public String getModularInventoryName() {
		return "tile.orientationControl.name";
	}

	@Override
	public boolean canInteractWithContainer(EntityPlayer entity) {
		return true;
	}

	@Override
	public void writeDataToNetwork(ByteBuf out, byte id) {
		if(id == 0) {
			out.writeShort(progress[0]);
			out.writeShort(progress[1]);
			out.writeShort(progress[2]);
		}
	}

	@Override
	public void readDataFromNetwork(ByteBuf in, byte packetId,
			NBTTagCompound nbt) {
		if(packetId == 0) {
			setProgress(0, in.readShort());
			setProgress(1, in.readShort());
			setProgress(2, in.readShort());
		}
	}

	@Override
	public void useNetworkData(EntityPlayer player, Side side, byte id,
			NBTTagCompound nbt) {

	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setShort("numRotationsX", (short)numRotationsPerHour[0]);
		nbt.setShort("numRotationsY", (short)numRotationsPerHour[1]);
		nbt.setShort("numRotationsZ", (short)numRotationsPerHour[2]);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		numRotationsPerHour[0] = nbt.getShort("numRotationsX");
		progress[0] = numRotationsPerHour[0] + getTotalProgress(0)/2;

		numRotationsPerHour[1] = nbt.getShort("numRotationsX");
		progress[1] = numRotationsPerHour[1] + getTotalProgress(1)/2;

		numRotationsPerHour[2] = nbt.getShort("numRotationsX");
		progress[2] = numRotationsPerHour[2] + getTotalProgress(2)/2;
	}


	@Override
	public float getNormallizedProgress(int id) {
		return getProgress(id)/(float)getTotalProgress(id);
	}

	@Override
	public void setProgress(int id, int progress) {

		this.progress[id] = progress;
		numRotationsPerHour[id] = (progress - getTotalProgress(id)/2);
	}

	@Override
	public int getProgress(int id) {
		return this.progress[id];
	}

	@Override
	public int getTotalProgress(int id) {
		return 120;
	}

	@Override
	public void setTotalProgress(int id, int progress) {

	}

	@Override
	public void setProgressByUser(int id, int progress) {
		setProgress(id, progress);
		PacketHandler.sendToServer(new PacketMachine(this, (byte)0));
	}
}
