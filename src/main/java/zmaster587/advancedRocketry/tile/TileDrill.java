package zmaster587.advancedRocketry.tile;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import zmaster587.advancedRocketry.api.AdvancedRocketryTileEntityType;
import zmaster587.libVulpes.api.LibvulpesGuiRegistry;
import zmaster587.libVulpes.inventory.ContainerModular;
import zmaster587.libVulpes.inventory.GuiHandler;
import zmaster587.libVulpes.inventory.GuiHandler.guiId;
import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.inventory.modules.IToggleButton;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleButton;
import zmaster587.libVulpes.inventory.modules.ModuleToggleSwitch;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.util.INetworkMachine;
import zmaster587.libVulpes.util.ZUtils;

import java.util.LinkedList;
import java.util.List;

public class TileDrill extends TileEntity implements IModularInventory, IToggleButton, INetworkMachine {

	private float distanceExtended;
	private boolean extended;
	private ModuleToggleSwitch toggleSwitch;

	public TileDrill() {
		super(AdvancedRocketryTileEntityType.TILE_DRILL);
		distanceExtended = 0;
	}

	public float getDistanceExtended() {
		return distanceExtended;
	}

	public void setDistanceExtended(float distance) {
		this.distanceExtended = distance;
	}

	public boolean drillExtended() {
		return extended;
	}

	public void setDrillExtended(boolean value) {
		extended = value;
		distanceExtended = value ? 1f : 0f;
	}

	@Override
	public List<ModuleBase> getModules(int id, PlayerEntity player) {
		List<ModuleBase> modules = new LinkedList<ModuleBase>();

		modules.add(toggleSwitch = (ModuleToggleSwitch) new ModuleToggleSwitch(160, 5, "", this,  zmaster587.libVulpes.inventory.TextureResources.buttonToggleImage, 11, 26, drillExtended()).setAdditionalData(0));

		return modules;
	}

	@Override
	public String getModularInventoryName() {
		return "block.advancedrocketry.drill";
	}

	@Override
	public boolean canInteractWithContainer(PlayerEntity entity) {
		return true;
	}

	@Override
	public void onInventoryButtonPressed(ModuleButton buttonId) {

		if(buttonId == toggleSwitch) {
			this.setDrillExtended(toggleSwitch.getState());
			PacketHandler.sendToServer(new PacketMachine(this,(byte)0));
		}
	}

	@Override
	public void stateUpdated(ModuleBase module) {
		if(module == toggleSwitch)
			setDrillExtended(toggleSwitch.getState());
	}

	@Override
	public void writeDataToNetwork(PacketBuffer out, byte id) {
		out.writeBoolean(extended);

	}

	@Override
	public void readDataFromNetwork(PacketBuffer in, byte packetId,
			CompoundNBT nbt) {
		nbt.putBoolean("enabled", in.readBoolean());
	}

	@Override
	public void useNetworkData(PlayerEntity player, Dist side, byte id,
			CompoundNBT nbt) {
		setDrillExtended(nbt.getBoolean("enabled"));
		toggleSwitch.setToggleState(drillExtended());

		//Last ditch effort to update the toggle switch when it's flipped
		if(!world.isRemote)
			PacketHandler.sendToNearby(new PacketMachine(this, (byte)0), ZUtils.getDimensionIdentifier(world), pos, 64);

	}
	
	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		super.write(nbt);
		
		nbt.putFloat("extendAmt", distanceExtended);
		nbt.putBoolean("extended", extended);
		return nbt;
	}
	
	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);
		
		distanceExtended = nbt.getFloat("extendAmt");
		extended = nbt.getBoolean("extended");
		
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent(getModularInventoryName());
	}

	@Override
	public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) {
		return new ContainerModular(LibvulpesGuiRegistry.CONTAINER_MODULAR_TILE, id, player, getModules(getModularInvType().ordinal(), player), this, getModularInvType());
	}

	@Override
	public GuiHandler.guiId getModularInvType() {
		return guiId.MODULAR;
	}

}
