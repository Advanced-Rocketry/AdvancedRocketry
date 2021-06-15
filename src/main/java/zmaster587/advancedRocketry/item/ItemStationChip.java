package zmaster587.advancedRocketry.item;

import com.mojang.realmsclient.gui.ChatFormatting;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.inventory.modules.ModuleStellarBackground;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.inventory.GuiHandler;
import zmaster587.libVulpes.inventory.modules.*;
import zmaster587.libVulpes.network.INetworkItem;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketItemModifcation;
import zmaster587.libVulpes.util.Vector3F;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.naming.directory.NoSuchAttributeException;
import java.util.LinkedList;
import java.util.List;

/**
 * MetaData corresponds to the id
 */
public class ItemStationChip extends ItemIdWithName implements IModularInventory, IButtonInventory, INetworkItem {

	private static final String uuidIdentifier = "UUID";
	private static final String SELECTION_ID = "selectionId";
	private static final String DESTINATION = "dests";
	private static final String TMPNAME = "TmpName";

	private static final int BUTTON_ID_CLEAR = 0;
	private static final int BUTTON_ID_DELETE = 1;
	private static final int BUTTON_ID_ADD = 2;
	private static final int BUTTON_ID_OFFSET = 5;

	public ItemStationChip() {
		//setMaxDamage(Integer.MAX_VALUE);
		setHasSubtypes(true);
	}

	@Override
	@ParametersAreNonnullByDefault
	@Nonnull
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
		ItemStack stack = playerIn.getHeldItem(hand);
		if(!playerIn.world.isRemote && !stack.isEmpty() && playerIn.isSneaking())
			playerIn.openGui(LibVulpes.instance, GuiHandler.guiId.MODULARCENTEREDFULLSCREEN.ordinal(), worldIn, -1, -1, -1);

		return super.onItemRightClick(worldIn, playerIn, hand);
	}

	@Override
	public List<ModuleBase> getModules(int ID, EntityPlayer player) {
		List<ModuleBase> modules = new LinkedList<>();
		final int offset_all = 96;

		ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);

		if(!stack.isEmpty()) {
			modules.add(new ModuleStellarBackground(0, 0, zmaster587.libVulpes.inventory.TextureResources.starryBG));


			List<ModuleBase> list2 = new LinkedList<>();
			ModuleButton btnAdd = new ModuleButton(172-offset_all, 18*2+28, BUTTON_ID_ADD, LibVulpes.proxy.getLocalizedString("msg.label.add"), this, zmaster587.advancedRocketry.inventory.TextureResources.buttonGeneric, 128, 18);
			ModuleButton btnClear = new ModuleButton(172-offset_all, 18*4+28, BUTTON_ID_CLEAR, LibVulpes.proxy.getLocalizedString("msg.label.clear"), this, zmaster587.advancedRocketry.inventory.TextureResources.buttonGeneric, 128, 18);
			ModuleButton btnDelete = new ModuleButton(172-offset_all, 18*3+28, BUTTON_ID_DELETE, LibVulpes.proxy.getLocalizedString("msg.label.delete"), this, zmaster587.advancedRocketry.inventory.TextureResources.buttonGeneric, 128, 18);

			modules.add(btnClear);
			modules.add(btnDelete);
			modules.add(btnAdd);

			// Get effective dimension
			int dimId = DimensionManager.getEffectiveDimId(player.world, new BlockPos(player)).getId();
			List<LandingLocation> list = getLandingLocations(stack, dimId);

			int selectedId = getSelectionId(stack, dimId);
			int i = 0;
			ModuleButton button;
			for( LandingLocation pos : list) 
			{
				button = new ModuleButton(0, i*18, i + BUTTON_ID_OFFSET, pos.toString(), this, zmaster587.advancedRocketry.inventory.TextureResources.buttonGeneric, 128, 18);
				list2.add(button);

				if (i == selectedId)
					button.setColor(0xFF22FF22);
				else
					button.setColor(0xFFFF2222);

				i++;
			}

			ModuleContainerPan pan = new ModuleContainerPan(25-offset_all, 50, list2, new LinkedList<>(), null, 512, 256, 0, -48, 258, 256);
			modules.add(pan);
		}

		return modules;
	}

	@Override
	public String getModularInventoryName() {
		return "item.stationChip.name";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onInventoryButtonPressed(int buttonId) {
		ItemStack stack = Minecraft.getMinecraft().player.getHeldItem(EnumHand.MAIN_HAND);
		if(!stack.isEmpty() && stack.getItem() == this) {
			PacketHandler.sendToServer(new PacketItemModifcation(this, Minecraft.getMinecraft().player, (byte)(buttonId)));
		}
	}


	private void setTempName(@Nonnull ItemStack stack, String string)
	{
		if(stack.hasTagCompound())
			stack.getTagCompound().setString(TMPNAME, string);
	}

	private String getTempName(@Nonnull ItemStack stack)
	{
		if(stack.hasTagCompound())
			return stack.getTagCompound().getString(TMPNAME);
		return "";
	}

	@Override
	public void writeDataToNetwork(ByteBuf out, byte id, @Nonnull ItemStack stack) {
		if(id == BUTTON_ID_ADD)
		{
			String str = getTempName(stack);
			byte[] byteArray = str.getBytes();
			short len = (short)byteArray.length;
			out.writeShort(len);
			out.writeBytes(byteArray, 0, len);
		}

	}

	@Override
	public void readDataFromNetwork(ByteBuf in, byte id, NBTTagCompound nbt, @Nonnull ItemStack stack) {
		if(id == BUTTON_ID_ADD)
		{
			short len = in.readShort();
			byte[] byteArray = new byte[len];
			in.readBytes(byteArray, 0, len);
			nbt.setString(TMPNAME, new String(byteArray));
		}
	}

	@Override
	public void useNetworkData(EntityPlayer player, Side side, byte id, NBTTagCompound nbt, @Nonnull ItemStack stack) {
		if(!player.world.isRemote)
		{
			int dimId = DimensionManager.getEffectiveDimId(player.world, new BlockPos(player)).getId();
			if(id >= BUTTON_ID_OFFSET)
			{
				setSelectionId(stack, dimId, id-BUTTON_ID_OFFSET);
			}
			else if(id == BUTTON_ID_DELETE)
			{
				int selection = getSelectionId(stack, dimId);

				//Can't delete "Last"
				if(selection > 0)
				{
					List<LandingLocation> locs = getLandingLocations(stack, dimId);
					if(selection < locs.size())
						locs.remove(selection);
					setLandingLocations(stack, dimId, locs);
				}				
			}
			else if(id == BUTTON_ID_CLEAR)
			{
				//Can't delete "Last"
				List<LandingLocation> locs = getLandingLocations(stack, dimId);
				List<LandingLocation> locs2 = new LinkedList<>();
				locs2.add(locs.get(0));
				setLandingLocations(stack, dimId, locs2);
			}
			else if(id == BUTTON_ID_ADD)
			{
				// this will be false if on a space station, do not set on space station
				if(player.getEntityWorld().provider.getDimension() == dimId) {
					List<LandingLocation> locs = getLandingLocations(stack, dimId);
					BlockPos pos = player.getPosition();
					locs.add(new LandingLocation(nbt.getString(TMPNAME), pos.getX(), pos.getY(), pos.getZ()));
					setLandingLocations(stack, dimId, locs);
				}
			}
			//Re-open the UI
			player.closeScreen();
			player.openGui(AdvancedRocketry.instance, GuiHandler.guiId.MODULARFULLSCREEN.ordinal(), player.world, -1, -1, -1);
		}
	}

	@Override
	public boolean canInteractWithContainer(EntityPlayer player) {
		return player.getHeldItem(player.getActiveHand()).getItem() == this;
	}

	public int getSelectionId(@Nonnull ItemStack stack, int dimid)
	{
		if(stack.hasTagCompound()) {
			NBTTagCompound nbt = stack.getTagCompound();
			if(nbt.hasKey("dimid" + dimid)) {
				nbt = nbt.getCompoundTag("dimid" + dimid);
				int size = getLandingLocations(stack, dimid).size();
				int selectedId = nbt.getInteger(SELECTION_ID);
				return size > selectedId ? selectedId : 0;
			}
		}
		return 0;
	}

	public void setSelectionId(@Nonnull ItemStack stack, int dimid, int slotId)
	{
		if(stack.hasTagCompound()) {
			NBTTagCompound nbt = stack.getTagCompound();
			if(nbt.hasKey("dimid" + dimid)) {
				nbt = nbt.getCompoundTag("dimid" + dimid);
				nbt.setInteger(SELECTION_ID, slotId);
			}
		}
	}

	public List<LandingLocation> getLandingLocations(@Nonnull ItemStack stack, int dimid)
	{
		List<LandingLocation> retList = new LinkedList<>();

		if(stack.hasTagCompound()) {
			NBTTagCompound nbt = stack.getTagCompound();
			if(nbt.hasKey("dimid" + dimid)) {
				nbt = nbt.getCompoundTag("dimid" + dimid);
				NBTTagList destList = nbt.getTagList(DESTINATION, NBT.TAG_COMPOUND);

				///XXX: Backwards compat
				if(nbt.hasKey("x"))
				{
					float x,y,z;
					x = nbt.getFloat("x");
					y = nbt.getFloat("y");
					z = nbt.getFloat("z");
					nbt.removeTag("x");
					nbt.removeTag("y");
					nbt.removeTag("z");

					List<LandingLocation> list2 = getLandingLocations(stack, dimid);
					list2.add(0,new LandingLocation("Last", x,y,z));
					setLandingLocations(stack, dimid, list2);

				}

				for(NBTBase tag : destList) {
					try {
						retList.add(LandingLocation.loadFromNBT((NBTTagCompound)tag));
					} catch (NoSuchAttributeException e) {
						AdvancedRocketry.logger.warn("Attempting to load a Landing location for planet " + dimid + " but chip appears to have malformed data");
					}
				}
			}
		}
		return retList;
	}

	public void setLandingLocations(@Nonnull ItemStack stack, int dimid, List<LandingLocation> locations)
	{
		if(stack.hasTagCompound()) {
			NBTTagCompound stackNBT = stack.getTagCompound();
			NBTTagCompound nbt;
			String tagName = "dimid" + dimid;
			if(stackNBT.hasKey(tagName)) 
				nbt = stackNBT.getCompoundTag("dimid" + dimid);
			else
				nbt = new NBTTagCompound();

			NBTTagList destList;
			destList = new NBTTagList();

			for(LandingLocation loc : locations)
			{
				NBTTagCompound nbtTag = new NBTTagCompound();
				loc.saveToNBT(nbtTag);
				destList.appendTag(nbtTag);
			}

			nbt.setTag(DESTINATION, destList);
			stackNBT.setTag(tagName, nbt);
		}
	}

	public void setTakeoffCoords(@Nonnull ItemStack stack, Vector3F<Float> pos, int dimid, int slot) {
		setTakeoffCoords(stack, pos.x, pos.y, pos.z, dimid, slot);
	}

	public void setTakeoffCoords(@Nonnull ItemStack stack, float x, float y, float z, int dimid, int slot) {
		NBTTagCompound nbt;

		if(stack.hasTagCompound()) 
			nbt = stack.getTagCompound();
		else 
			nbt = new NBTTagCompound();

		LandingLocation landingLoc = new LandingLocation("Last", x,y,z);

		List<LandingLocation> landingLocList = getLandingLocations(stack, dimid);

		if(landingLocList.isEmpty() || slot >= landingLocList.size())
			landingLocList.add(landingLoc);
		else
			landingLocList.set(slot, landingLoc);

		setLandingLocations(stack, dimid, landingLocList);
	}

	/**
	 * @param stack
	 * @param dimid
	 * @return Vector3F containing the takeoff coords or null if there is none
	 */
	public LandingLocation getTakeoffCoords(@Nonnull ItemStack stack, int dimid) {
		if(stack.hasTagCompound()) {
			NBTTagCompound nbt = stack.getTagCompound();
			if(nbt.hasKey("dimid" + dimid)) {
				List<LandingLocation> landingLocList = getLandingLocations(stack, dimid);
				int id = getSelectionId(stack, dimid);
				LandingLocation loc;
				if(landingLocList.isEmpty())
					return null;
				else if(id < landingLocList.size() && id > 0)
					loc = landingLocList.get(id);
				else
					loc = landingLocList.get(0);


				return loc;
			}
		}
		return null;
	}

	public static int getUUID(@Nonnull ItemStack stack) {
		if(stack.hasTagCompound())
			return stack.getTagCompound().getInteger(uuidIdentifier);
		return 0;
	}

	public static void setUUID(@Nonnull ItemStack stack, int uuid) {
		NBTTagCompound nbt;
		if(stack.hasTagCompound())
			nbt = stack.getTagCompound();
		else
			nbt = new NBTTagCompound();

		nbt.setInteger(uuidIdentifier,uuid);
		stack.setTagCompound(nbt);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(@Nonnull ItemStack stack, World player, List<String> list, ITooltipFlag bool) {
		if(getUUID(stack) == 0)
			list.add(ChatFormatting.GRAY + LibVulpes.proxy.getLocalizedString("msg.unprogrammed"));
		else {
			list.add(ChatFormatting.GREEN + LibVulpes.proxy.getLocalizedString("msg.stationchip.sation") + getUUID(stack));
			super.addInformation(stack, player, list, bool);
			if(player.provider.getDimension() == ARConfiguration.getCurrentConfig().spaceDimId) {
				Entity p = Minecraft.getMinecraft().player;
				ISpaceObject spaceObject = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(p.getPosition());

				if(spaceObject != null) {
					LandingLocation loc = getTakeoffCoords(stack, spaceObject.getOrbitingPlanetId());
					if(loc != null) {
						Vector3F<Float> vec = loc.location;
						list.add("Name: " + loc.name);
						list.add("X: " + vec.x);
						list.add("Z: " + vec.z);
					}
					else {
						list.add("Name: N/A");
						list.add("X: N/A");
						list.add("Z: N/A");
					}
				}
			}
			else {
				LandingLocation loc = getTakeoffCoords(stack, player.provider.getDimension());
				if(loc != null) {
					Vector3F<Float> vec = loc.location;
					list.add("Name: " + loc.name);
					list.add("X: " + vec.x);
					list.add("Z: " + vec.z);
				}
				else {
					list.add("Name: N/A");
					list.add("X: N/A");
					list.add("Z: N/A");
				}
			}
			list.add(ChatFormatting.DARK_GRAY + LibVulpes.proxy.getLocalizedString("item.stationchip.openmenu"));
		}
	}

	public static class LandingLocation
	{
		public String name;
		public Vector3F<Float> location;

		public LandingLocation(String name, Vector3F<Float> location)
		{
			this.name = name;
			this.location = location;
		}

		public LandingLocation(String name, float x, float y, float z)
		{
			this.name = name;
			this.location = new Vector3F<>(x, y, z);
		}

		public LandingLocation()
		{
			this.name = "";
			this.location = new Vector3F<>(0f, 0f, 0f);
		}

		@Override
		public String toString() {
			return String.format("%s: %.0f, %.0f", name, location.x, location.z);
		}

		static LandingLocation loadFromNBT(NBTTagCompound nbt)  throws NoSuchAttributeException {
			String name = nbt.getString("name");
			Vector3F<Float> vec = new Vector3F<>(nbt.getFloat("x"), nbt.getFloat("y"), nbt.getFloat("z"));

			return new LandingLocation(name, vec);
		}

		void saveToNBT(NBTTagCompound nbt)
		{
			nbt.setString("name", this.name);
			nbt.setFloat("x", this.location.x);
			nbt.setFloat("y", this.location.y);
			nbt.setFloat("z", this.location.z);
		}
	}
}
