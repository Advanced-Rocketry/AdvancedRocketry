package zmaster587.advancedRocketry.tile.atmosphere;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import zmaster587.advancedRocketry.api.AdvancedRocketryTileEntityType;
import zmaster587.advancedRocketry.api.IAtmosphere;
import zmaster587.advancedRocketry.api.atmosphere.AtmosphereRegister;
import zmaster587.advancedRocketry.atmosphere.AtmosphereHandler;
import zmaster587.advancedRocketry.atmosphere.AtmosphereType;
import zmaster587.advancedRocketry.block.BlockRedstoneEmitter;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.LibvulpesGuiRegistry;
import zmaster587.libVulpes.inventory.ContainerModular;
import zmaster587.libVulpes.inventory.GuiHandler;
import zmaster587.libVulpes.inventory.GuiHandler.guiId;
import zmaster587.libVulpes.inventory.modules.*;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.util.INetworkMachine;
import zmaster587.libVulpes.util.ZUtils;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class TileAtmosphereDetector extends TileEntity implements ITickableTileEntity, IModularInventory, IButtonInventory, INetworkMachine {

	private IAtmosphere atmosphereToDetect;

	public TileAtmosphereDetector() {
		super(AdvancedRocketryTileEntityType.TILE_ATMOSPHERE_DETECTOR);
		atmosphereToDetect = AtmosphereType.AIR;
	}


	@Override
	public void tick() {
		if(!world.isRemote && world.getServer().getServerTime() % 10 == 0) {
			BlockState state = world.getBlockState(pos);
			boolean detectedAtm = false;

			if(AtmosphereHandler.getOxygenHandler(ZUtils.getDimensionIdentifier(world)) == null) {
				detectedAtm = atmosphereToDetect == AtmosphereType.AIR;
			} else {
				for(Direction  direction : Direction.values()) {
					detectedAtm = ((world.getBlockState(pos.offset(direction)).getShape(world, pos.offset(direction)) != VoxelShapes.fullCube()) && atmosphereToDetect == AtmosphereHandler.getOxygenHandler(world).getAtmosphereType(pos.offset(direction)));
					if(detectedAtm) break;
				}
			}

			if(((BlockRedstoneEmitter)state.getBlock()).getState(world, state, pos) != detectedAtm) {
				((BlockRedstoneEmitter)state.getBlock()).setState(world, state, pos, detectedAtm);
			}
		}
	}
	
	@Override
	public List<ModuleBase> getModules(int id, PlayerEntity player) {
		List<ModuleBase> modules = new LinkedList<>();
		List<ModuleBase> btns = new LinkedList<>();

		Iterator<IAtmosphere> atmIter = AtmosphereRegister.getInstance().getAtmosphereList().iterator();

		int i = 0;
		while(atmIter.hasNext()) {
			IAtmosphere atm = atmIter.next();
			btns.add(new ModuleButton(60, 4 + i*24, LibVulpes.proxy.getLocalizedString(atm.getUnlocalizedName()), this,  zmaster587.libVulpes.inventory.TextureResources.buttonBuild).setAdditionalData(atm));
			i++;
		}

		ModuleContainerPanYOnly panningContainer = new ModuleContainerPanYOnly(5, 20, btns, new LinkedList<>(), zmaster587.libVulpes.inventory.TextureResources.starryBG, 160, 100, 0, 500);
		modules.add(panningContainer);
		return modules;
	}

	@Override
	public String getModularInventoryName() {
		return "block.advancedrocketry.atmospheredetector";
	}

	@Override
	public boolean canInteractWithContainer(PlayerEntity entity) {
		return true;
	}

	@Override
	public void onInventoryButtonPressed(ModuleButton buttonId) {
		atmosphereToDetect = (IAtmosphere)buttonId.getAdditionalData();
		PacketHandler.sendToServer(new PacketMachine(this, (byte)0));
	}

	@Override
	public void writeDataToNetwork(PacketBuffer out, byte id) {
		//Send the unlocalized name over the net to reduce chances of foulup due to client/server inconsistencies
		if(id == 0) {
			PacketBuffer buf = new PacketBuffer(out);
			buf.writeShort(atmosphereToDetect.getUnlocalizedName().length());
			buf.writeString(atmosphereToDetect.getUnlocalizedName());
		}
	}

	@Override
	public void readDataFromNetwork(PacketBuffer in, byte packetId,
			CompoundNBT nbt) {
		if(packetId == 0) {
			PacketBuffer buf = new PacketBuffer(in);
			nbt.putString("uName", buf.readString(buf.readShort()));
		}
	}

	@Override
	public void useNetworkData(PlayerEntity player, Dist side, byte id,
			CompoundNBT nbt) {
		if(id == 0) {
			String name = nbt.getString("uName");
			atmosphereToDetect = AtmosphereRegister.getInstance().getAtmosphere(name);
		}
	}

	@Nonnull
	@Override
	@ParametersAreNonnullByDefault
	public CompoundNBT write(CompoundNBT nbt) {
		super.write(nbt);

		nbt.putString("atmName", atmosphereToDetect.getUnlocalizedName());
		return nbt;
	}

	@Override
	@ParametersAreNonnullByDefault
	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);

		atmosphereToDetect = AtmosphereRegister.getInstance().getAtmosphere(nbt.getString("atmName"));
	}

	@Nonnull
	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent(getModularInventoryName());
	}

	@ParametersAreNonnullByDefault
	@Override
	public Container createMenu(int id, PlayerInventory playerInv, PlayerEntity player) {
		GuiHandler.guiId ID = getModularInvType();
		return new ContainerModular(LibvulpesGuiRegistry.CONTAINER_MODULAR_TILE, id, player, this.getModules(getModularInvType().ordinal(), player), this, ID);
	}

	@Override
	public GuiHandler.guiId getModularInvType() {
		return guiId.MODULARNOINV;
	}
}
