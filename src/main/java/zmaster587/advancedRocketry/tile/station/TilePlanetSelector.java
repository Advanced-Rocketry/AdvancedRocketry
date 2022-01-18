package zmaster587.advancedRocketry.tile.station;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.util.Constants.NBT;


import zmaster587.advancedRocketry.api.AdvancedRocketryTileEntityType;
import zmaster587.advancedRocketry.api.Constants;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.inventory.modules.ModulePlanetSelector;
import zmaster587.advancedRocketry.util.ITilePlanetSystemSelectable;
import zmaster587.libVulpes.api.LibvulpesGuiRegistry;
import zmaster587.libVulpes.inventory.ContainerModular;
import zmaster587.libVulpes.inventory.GuiHandler;
import zmaster587.libVulpes.inventory.TextureResources;
import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.inventory.modules.IProgressBar;
import zmaster587.libVulpes.inventory.modules.ISelectionNotify;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.tile.TilePointer;
import zmaster587.libVulpes.util.INetworkMachine;
import zmaster587.libVulpes.util.ZUtils;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.LinkedList;
import java.util.List;

public class TilePlanetSelector extends TilePointer implements ISelectionNotify, IModularInventory, IProgressBar, INetworkMachine {

	protected ModulePlanetSelector container;
	DimensionProperties dimCache;

	int[] cachedProgressValues;

	public TilePlanetSelector() {
		super(AdvancedRocketryTileEntityType.TILE_PLANET_SELECTOR);
		cachedProgressValues = new int[] { -1, -1, -1};
	}

	@Override
	public void onSelectionConfirmed(Object sender) {

		//Container Cannot be null at this time
		TileEntity tile = getMasterBlock();
		if(tile instanceof ITilePlanetSystemSelectable) {
			((ITilePlanetSystemSelectable)tile).setSelectedPlanetId(container.getSelectedSystem());
		}
		onSelected(sender);
	}

	@Override
	public void onSelected(Object sender) {

		selectSystem(container.getSelectedSystem());

		PacketHandler.sendToServer(new PacketMachine(this, (byte)0));
	}

	private void selectSystem(ResourceLocation id) {
		if(Constants.INVALID_PLANET.equals(id))
			dimCache = null;
		else
			dimCache = DimensionManager.getInstance().getDimensionProperties(container.getSelectedSystem());
	}

	@Override
	public List<ModuleBase> getModules(int ID, PlayerEntity player) {

		List<ModuleBase> modules = new LinkedList<>();

        DimensionProperties props = DimensionManager.getEffectiveDimId(ZUtils.getDimensionIdentifier(player.world), new BlockPos(player.getPositionVec()));
		container = new ModulePlanetSelector((props != null ? props.getStarId() : DimensionManager.getInstance().getStar(new ResourceLocation(Constants.STAR_NAMESPACE, "0")).getId()), TextureResources.starryBG, this, true);
		container.setOffset(1000, 1000);
		modules.add(container);

		//Transfer discovery values
		if(!world.isRemote) {
			markDirty();
		}

		return modules;
	}

	@Override
	public String getModularInventoryName() {
		return "block.advancedrocketry.planetselector";
	}

	@Override
	public boolean canInteractWithContainer(PlayerEntity entity) {
		return true;
	}

	@Override
	public float getNormallizedProgress(int id) {
		return 0;
	}

	@Override
	public void setProgress(int id, int progress) {
		cachedProgressValues[id] = progress;
	}

	@Override
	public int getProgress(int id) {

		if(!world.isRemote) {
			return 25; /*
			if(getMasterBlock() != null) {

				ItemStack stack = ((ITilePlanetSystemSelectable)getMasterBlock()).getChipWithId(container.getSelectedSystem());

				if(!stack.isEmpty()) {

					DataType data;
					if(id == 0)
						data = DataType.ATMOSPHEREDENSITY;
					else if(id == 1)
						data = DataType.DISTANCE;
					else //if(id == 2)
						data = DataType.MASS;


					int dataAmt = ((ItemPlanetIdentificationChip)stack.getItem()).getData(stack, data);

					if(dataAmt != 0)
						return (int)(certaintyDataValue/(float)dataAmt);
				}
			}*/
		}
		else {
			return cachedProgressValues[id];
		}

		//return 400;
	}

	@Override
	public int getTotalProgress(int id) {
		if(dimCache == null)
			return 50;
		if(id == 0)
			return dimCache.getAtmosphereDensity()/16;
		else if(id == 1)
			return dimCache.orbitalDist/16;
		else //if(id == 2)
			return (int) (dimCache.gravitationalMultiplier*50);
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT comp = new CompoundNBT();

		writeToNBTHelper(comp);
		writeAdditionalNBT(comp);
		return new SUpdateTileEntityPacket(pos, 0, comp);
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {

		super.onDataPacket(net, pkt);
		readAdditionalNBT(pkt.getNbtCompound());
	}

	public void writeAdditionalNBT(CompoundNBT nbt) {
		if(getMasterBlock() != null) {
			List<ResourceLocation> list = ((ITilePlanetSystemSelectable)getMasterBlock()).getVisiblePlanets();

			ListNBT nbtList = new ListNBT();
			
			for(ResourceLocation planet : list)
			{
				nbtList.add(StringNBT.valueOf(planet.toString()));
			}
			

			nbt.put("visiblePlanets", nbtList);
		}

	}

	public void readAdditionalNBT(CompoundNBT nbt) {
		if(container != null) {
			ListNBT intArray = nbt.getList("visiblePlanets", NBT.TAG_STRING);
			for(int i = 0; i <  intArray.size(); i++)
				container.setPlanetAsKnown(new ResourceLocation(intArray.getString(i)));
		}
	}

	@Override
	public void setTotalProgress(int id, int progress) {

	}

	@Override
	public void writeDataToNetwork(PacketBuffer out, byte id) {
		if(id == 0)
			out.writeResourceLocation(container.getSelectedSystem());
	}

	@Override
	public void readDataFromNetwork(PacketBuffer in, byte packetId,
			CompoundNBT nbt) {
		if(packetId == 0)
			nbt.putString("id", in.readResourceLocation().toString());
	}

	@Override
	public void useNetworkData(PlayerEntity player, Dist side, byte id,
			CompoundNBT nbt) {
		if(id == 0) {
			ResourceLocation dimId = new ResourceLocation(nbt.getString("id"));
			container.setSelectedSystem(dimId);
			selectSystem(dimId);

			//Update known planets
			markDirty();
		}
	}

	@Override
	public void onSystemFocusChanged(Object sender) {
		PacketHandler.sendToServer(new PacketMachine(this, (byte)0));
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
		return GuiHandler.guiId.MODULARFULLSCREEN;
	}
}
