package zmaster587.advancedRocketry.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.inventory.GuiHandler;
import zmaster587.advancedRocketry.inventory.modules.ModuleOreMapper;
import zmaster587.advancedRocketry.satellite.SatelliteOreMapping;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.inventory.modules.ModuleBase;

import java.util.LinkedList;
import java.util.List;

public class ItemOreScanner extends Item implements IModularInventory {


	@Override
	public void addInformation(ItemStack stack, World player,
			List list, ITooltipFlag arg5) {
		
		SatelliteBase sat = DimensionManager.getInstance().getSatellite(this.getSatelliteID(stack));
		
		SatelliteOreMapping mapping = null;
		if(sat instanceof SatelliteOreMapping)
			mapping = (SatelliteOreMapping)sat;
		
		if(!stack.hasTagCompound())
			list.add(LibVulpes.proxy.getLocalizedString("msg.unprogrammed"));
		else if(mapping == null)
			list.add(LibVulpes.proxy.getLocalizedString("msg.itemorescanner.nosat"));
		else if(mapping.getDimensionId() == player.provider.getDimension()) {
			list.add(LibVulpes.proxy.getLocalizedString("msg.connected"));
			list.add(LibVulpes.proxy.getLocalizedString("msg.itemorescanner.maxzoom") + mapping.getZoomRadius());
			list.add(LibVulpes.proxy.getLocalizedString("msg.itemorescanner.filter") + mapping.canFilterOre());
		}
		else
			list.add(LibVulpes.proxy.getLocalizedString("msg.notconnected"));

		super.addInformation(stack, player, list, arg5);
	}
	
	public void setSatelliteID(ItemStack stack, long id) {
		NBTTagCompound nbt;
		if(!stack.hasTagCompound())
			nbt = new NBTTagCompound();
		else
			nbt = stack.getTagCompound();
		
		nbt.setLong("id", id);
		stack.setTagCompound(nbt);
	}

	public long getSatelliteID(ItemStack stack) {
		NBTTagCompound nbt;
		if(!stack.hasTagCompound())
			return -1;
		
		nbt = stack.getTagCompound();
		
		return nbt.getLong("id");
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
		ItemStack stack = playerIn.getHeldItem(hand);
		if(!playerIn.world.isRemote && !stack.isEmpty())
		{
			int satelliteId = (int)getSatelliteID(stack);
			
			SatelliteBase satellite = DimensionManager.getInstance().getSatellite(satelliteId);
			
			if(satellite != null && (satellite instanceof SatelliteOreMapping) && satellite.getDimensionId() == worldIn.provider.getDimension())
				playerIn.openGui(AdvancedRocketry.instance, GuiHandler.guiId.OreMappingSatellite.ordinal(), worldIn, (int)playerIn.getPosition().getX(), (int)getSatelliteID(stack), (int)playerIn.getPosition().getZ());

		}
			
		return super.onItemRightClick(worldIn, playerIn, hand);
	}
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer playerIn,
			World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing,
			float hitX, float hitY, float hitZ) {
		if(!playerIn.world.isRemote && hand == EnumHand.MAIN_HAND)
		{
			ItemStack stack = playerIn.getHeldItem(hand);
			if(!playerIn.world.isRemote && !stack.isEmpty())
			{
				int satelliteId = (int)getSatelliteID(stack);
				
				SatelliteBase satellite = DimensionManager.getInstance().getSatellite(satelliteId);
				
				if(satellite != null && (satellite instanceof SatelliteOreMapping) && satellite.getDimensionId() == worldIn.provider.getDimension())
					playerIn.openGui(AdvancedRocketry.instance, GuiHandler.guiId.OreMappingSatellite.ordinal(), worldIn, (int)playerIn.getPosition().getX(), (int)getSatelliteID(stack), (int)playerIn.getPosition().getZ());

			}
		}
		return super.onItemUse(playerIn, worldIn, pos, hand, facing, hitX, hitY,
				hitZ);
	}


	public void interactSatellite(SatelliteBase satellite,EntityPlayer player, World world, BlockPos pos) {
		satellite.performAction(player, world, pos);
	}

	@Override
	public List<ModuleBase> getModules(int id, EntityPlayer player) {
		List<ModuleBase> modules = new LinkedList<ModuleBase>();
		modules.add(new ModuleOreMapper(0, 0));
		return modules;
	}

	@Override
	public String getModularInventoryName() {
		return null;
	}

	@Override
	public boolean canInteractWithContainer(EntityPlayer entity) {
		return true;
	}

}
