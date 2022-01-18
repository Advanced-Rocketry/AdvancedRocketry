package zmaster587.advancedRocketry.tile.multiblock;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.ParticleStatus;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.AdvancedRocketryParticleTypes;
import zmaster587.advancedRocketry.api.AdvancedRocketryTileEntityType;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.util.AudioRegistry;
import zmaster587.advancedRocketry.util.GravityHandler;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.inventory.modules.*;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.tile.multiblock.TileMultiPowerConsumer;
import zmaster587.libVulpes.util.ZUtils.RedstoneState;

import java.util.LinkedList;
import java.util.List;

public class TileAreaGravityController extends TileMultiPowerConsumer implements ISliderBar, IGuiCallback {

	int gravity;
	int progress;
	int radius;
	float currentProgress;
	double rotation;

	private ModuleRedstoneOutputButton redstoneControl;
	private RedstoneState state;
	private ModuleText targetGrav, textRadius;
	private ModuleBlockSideSelector sideSelectorModule;

	private static final Object[][][] structure = {
		{{null, null, null},
			{null, 'c', null},
			{null, null, null}},
			{{null, LibVulpesBlocks.blockAdvancedMachineStructure, null},
				{LibVulpesBlocks.blockAdvancedMachineStructure, 'P', LibVulpesBlocks.blockAdvancedMachineStructure},
				{null, LibVulpesBlocks.blockAdvancedMachineStructure, null}}
	};

	public TileAreaGravityController() {
		super(AdvancedRocketryTileEntityType.TILE_AREA_GRAVITY_CONTROLLER);
		//numGravPylons = new ModuleText(10, 25, "Number Of Thrusters: ", 0xaa2020);
		textRadius = new ModuleText(6, 82, LibVulpes.proxy.getLocalizedString("msg.gravitycontroller.radius") + "5", 0x202020);
		targetGrav = new ModuleText(6, 110, LibVulpes.proxy.getLocalizedString("msg.gravitycontroller.targetgrav"), 0x202020);
		sideSelectorModule = new ModuleBlockSideSelector(90, 15, this, LibVulpes.proxy.getLocalizedString("msg.gravitycontroller.none"), LibVulpes.proxy.getLocalizedString("msg.gravitycontroller.activeset"), LibVulpes.proxy.getLocalizedString("msg.gravitycontroller.activeadd"));

		redstoneControl = new ModuleRedstoneOutputButton(174, 4, "", this);
		state = RedstoneState.OFF;
		redstoneControl.setRedstoneState(state);
		radius = 5;
		toggleSwitch = new ModuleToggleSwitch(160, 5, "", this,  zmaster587.libVulpes.inventory.TextureResources.buttonToggleImage, 11, 26, getMachineEnabled());
	}

	@Override
	public SoundEvent getSound() {
		return AudioRegistry.gravityOhhh;
	}

	@Override
	public Object[][][] getStructure() {
		return structure;
	}

	@Override
	public List<ModuleBase> getModules(int id, PlayerEntity player) {
		List<ModuleBase> modules = new LinkedList<>();//super.getModules(id, player);
		modules.add(toggleSwitch);
		modules.add(new ModulePower(18, 20, getBatteries()));
		modules.add(sideSelectorModule);

		modules.add(redstoneControl);


		modules.add(new ModuleSlider(6, 120, 0, TextureResources.doubleWarningSideBarIndicator, this));
		modules.add(new ModuleSlider(6, 90, 1, TextureResources.doubleWarningSideBarIndicator, this));

		modules.add(new ModuleText(42, 20, LibVulpes.proxy.getLocalizedString("msg.gravitycontroller.targetdir.1") + "\n" + LibVulpes.proxy.getLocalizedString("msg.gravitycontroller.targetdir.2"), 0x202020));
		modules.add(targetGrav);
		modules.add(textRadius);
		updateText();
		return modules;
	}

	public int getRadius() {
		return radius + 10;
	}

	protected boolean isStateActive(RedstoneState state, boolean condition) {
		if(state == RedstoneState.INVERTED)
			return !condition;
		else if(state == RedstoneState.OFF)
			return true;
		return condition;
	}

	public double getArmRotation() {
		rotation = (rotation + 10f*currentProgress) % 360f;
		return rotation;
	}

	public double getGravityMultiplier() {
		return currentProgress/2f;
	}

	public void setGravityMultiplier(double multiplier) {gravity = (int)(multiplier * 100);}

	private void updateText() {
		if(world.isRemote) {
			textRadius.setText(String.format("%s%d",LibVulpes.proxy.getLocalizedString("msg.gravitycontroller.radius"), getRadius()));

			targetGrav.setText(String.format("%s %.2f/%.2f", LibVulpes.proxy.getLocalizedString("msg.gravitycontroller.targetgrav" ),currentProgress, gravity/100f));
		}
	}

	@Override
	public String getMachineName() {
		return getModularInventoryName();
	}
	
	@Override
	public boolean isRunning() {
		return getMachineEnabled() && isStateActive(state, world.getRedstonePowerFromNeighbors(getPos()) > 0);
	}

	@Override
	public void tick() {
		if(isRunning()) {
			if(!world.isRemote) {

				if(gravity == 0)
					gravity = 15;
				double targetGravity = gravity/100D;
				double angVel = currentProgress;
				double acc = 0.001;

				double difference = targetGravity - angVel;

				if(difference != 0) {
					double finalVel = angVel;
					if(difference < 0) {
						finalVel = angVel + Math.max(difference, -acc);
					}
					else if(difference > 0) {
						finalVel = angVel + Math.min(difference, acc);
					}

					currentProgress = (float)finalVel;
					markDirty();
					world.notifyBlockUpdate(pos, world.getBlockState(pos),  world.getBlockState(pos), 2);
				}

			} else
				updateText();

			List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(getPos()).grow(getRadius(), getRadius() , getRadius()));

			for(Entity e : entities) {
				boolean additive = true;
				boolean allowApply = false;
				e.fallDistance = 0;

				for(Direction dir : Direction.values()) {
					if(!(e instanceof PlayerEntity) || !((PlayerEntity)e).abilities.isFlying) {

						if(sideSelectorModule.getStateForSide(dir) != 0) {
							allowApply = true;
							if(sideSelectorModule.getStateForSide(dir)  == 1)
								additive = false;

							if(e instanceof LivingEntity) {
								e.setMotion(e.getMotion().add(dir.getXOffset()*GravityHandler.LIVING_OFFSET*currentProgress,
								dir.getYOffset()*GravityHandler.LIVING_OFFSET*currentProgress,
								dir.getZOffset()*GravityHandler.LIVING_OFFSET*currentProgress));
							} else if (e instanceof ItemEntity || e instanceof ArrowEntity) {
								e.setMotion(e.getMotion().add(dir.getXOffset()*GravityHandler.OTHER_OFFSET*currentProgress,
								dir.getYOffset()*GravityHandler.OTHER_OFFSET*currentProgress,
								dir.getZOffset()*GravityHandler.OTHER_OFFSET*currentProgress));
							}

							//Spawn particle effect
							//TODO: tornados for planets
							if(world.isRemote) {
								if(Minecraft.getInstance().gameSettings.particles == ParticleStatus.ALL)
									AdvancedRocketry.proxy.spawnParticle(AdvancedRocketryParticleTypes.fxGravityEffect, world, e.getPosX(), e.getPosY(), e.getPosZ(), .2f*dir.getXOffset()*currentProgress, .2f*dir.getYOffset()*currentProgress, .2f*dir.getZOffset()*currentProgress);
							}
						}
					}
				}

				//Only apply gravity if none of the directions are set and it's not a player in flight
				if(allowApply && !additive)
					e.setMotion(e.getMotion().add(0 , (e instanceof ItemEntity) ? GravityHandler.OTHER_OFFSET :  (e instanceof ArrowEntity) ? GravityHandler.ARROW_OFFSET : GravityHandler.LIVING_OFFSET + 0.005, 0));
			}
		}
		else if (currentProgress > 0) {
			currentProgress -= 0.01f;
			if(!world.isRemote) {
				markDirty();
				world.notifyBlockUpdate(pos, world.getBlockState(pos),  world.getBlockState(pos), 2);
			}
			else
				updateText();
		}
		else
			currentProgress = 0;
		//}
	}

	@Override
	public boolean shouldHideBlock(World world, BlockPos pos, BlockState tile) {return true;}

	@Override
	public String getModularInventoryName() {
		return "block.advancedrocketry.areagravitycontroller";
	}

	@Override
	public boolean canInteractWithContainer(PlayerEntity entity) {
		return true;
	}

	@Override
	public void writeDataToNetwork(PacketBuffer out, byte id) {
		super.writeDataToNetwork(out, id);
		if(id == 3) {
			out.writeShort(progress);
			out.writeShort(radius);
		}
		else if(id == 4) {
			for(int i = 0; i < 6; i++)
				out.writeByte(sideSelectorModule.getStateForSide(i));
		}
		else if(id == 5) {
			out.writeByte(state.ordinal());
		}
	}

	@Override
	public void readDataFromNetwork(PacketBuffer in, byte packetId,
			CompoundNBT nbt) {
		super.readDataFromNetwork(in, packetId, nbt);
		if(packetId == 3) {
			nbt.putShort("progress",  in.readShort());
			nbt.putShort("radius", in.readShort());
		}
		else if(packetId == 4) {
			byte[] bytes = new byte[6];
			for(int i = 0; i < 6; i++)
				bytes[i] = in.readByte();
			nbt.putByteArray("bytes", bytes);
		}
		else if(packetId == 5) {
			nbt.putByte("redstoneState", in.readByte());
		}
	}

	@Override
	public void useNetworkData(PlayerEntity player, Dist side, byte id,
			CompoundNBT nbt) {
		super.useNetworkData(player, side, id, nbt);

		if(id == 3) {
			setProgress(0, nbt.getShort("progress"));
			setProgress(1, nbt.getShort("radius"));
		}
		else if(id == 4) {
			byte[] bytes = nbt.getByteArray("bytes");
			for(int i = 0; i < 6; i++)
				sideSelectorModule.setStateForSide(i, bytes[i]);
		}
		else if(id == 5) {
			state = RedstoneState.values()[nbt.getByte("redstoneState")];
			redstoneControl.setRedstoneState(state);
		}
	}


	@Override
	protected void writeNetworkData(CompoundNBT nbt) {
		super.writeNetworkData(nbt);
		nbt.putShort("gravity", (short)gravity);
		nbt.putFloat("currGravity", currentProgress);
		nbt.putByte("redstoneState", (byte) state.ordinal());
		nbt.putShort("radius", (short)radius);
		sideSelectorModule.write(nbt);
	}

	@Override
	protected void readNetworkData(CompoundNBT nbt) {
		super.readNetworkData(nbt);
		gravity = nbt.getShort("gravity");
		currentProgress = nbt.getFloat("currGravity");
		progress = gravity -5;
		radius = nbt.getShort("radius");
		sideSelectorModule.readFromNBT(nbt);
		state = RedstoneState.values()[nbt.getByte("redstoneState")];
		redstoneControl.setRedstoneState(state);

	}

	@Override
	public void onInventoryButtonPressed(ModuleButton buttonId) {
		super.onInventoryButtonPressed(buttonId);
		if(buttonId == redstoneControl) {
			state = redstoneControl.getState();
			PacketHandler.sendToServer(new PacketMachine(this, (byte)5));
		}
	}

	@Override
	public float getNormallizedProgress(int id) {
		return getProgress(id)/(float)getTotalProgress(id);
	}

	@Override
	public void setProgress(int id, int progress) {

		if(id == 0) {
			this.progress = progress;
			gravity = progress + 5;
		}
		else
			radius = progress;
	}

	@Override
	public int getProgress(int id) {
		if(id == 0)
			return this.progress;
		else
			return radius;
	}

	@Override
	public int getTotalProgress(int id) {
		if(id == 0)
			return 190;
		else
			return 22;
	}

	@Override
	public void setTotalProgress(int id, int progress) {

	}

	@Override
	public void setProgressByUser(int id, int progress) {
		setProgress(id, progress);
		PacketHandler.sendToServer(new PacketMachine(this, (byte)3));
	}

	@Override
	public void onModuleUpdated(ModuleBase module) {
		PacketHandler.sendToServer(new PacketMachine(this, (byte)4));
	}

}
