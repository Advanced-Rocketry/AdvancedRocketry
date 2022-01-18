package zmaster587.advancedRocketry.item.tools;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.satellite.SatelliteOreMapping;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.util.ZUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.LinkedList;
import java.util.List;

public class ItemOreScanner extends Item {


	public ItemOreScanner(Properties properties) {
		super(properties);
	}

	@Override
	@ParametersAreNonnullByDefault
	public void addInformation(@Nonnull ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag arg5) {
		
		SatelliteBase sat = DimensionManager.getInstance().getSatellite(this.getSatelliteID(stack));
		
		SatelliteOreMapping mapping = null;
		if(sat instanceof SatelliteOreMapping)
			mapping = (SatelliteOreMapping)sat;
		
		if(!stack.hasTag())
			list.add( new TranslationTextComponent("msg.unprogrammed"));
		else if(mapping == null)
			list.add( new TranslationTextComponent("msg.ore_scanner.nosat"));
		else if(mapping.getDimensionId().get() == ZUtils.getDimensionIdentifier(world)) {
			list.add( new TranslationTextComponent("msg.connected"));
			list.add(new StringTextComponent(LibVulpes.proxy.getLocalizedString("msg.ore_scanner.maxzoom") + mapping.getZoomRadius()));
			list.add(new StringTextComponent(LibVulpes.proxy.getLocalizedString("msg.ore_scanner.filter") + mapping.canFilterOre()));
		}
		else
			list.add( new TranslationTextComponent("msg.notconnected"));

		super.addInformation(stack, world, list, arg5);
	}
	
	public void setSatelliteID(@Nonnull ItemStack stack, long id) {
		CompoundNBT nbt;
		if(!stack.hasTag())
			nbt = new CompoundNBT();
		else
			nbt = stack.getTag();
		
		nbt.putLong("id", id);
		stack.setTag(nbt);
	}

	public long getSatelliteID(@Nonnull ItemStack stack) {
		CompoundNBT nbt;
		if(!stack.hasTag())
			return -1;
		
		nbt = stack.getTag();
		
		return nbt.getLong("id");
	}
	
	@Override
	@Nonnull
	@ParametersAreNonnullByDefault
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand hand) {
		ItemStack stack = playerIn.getHeldItem(hand);
		if(!playerIn.world.isRemote && !stack.isEmpty()) {
			int satelliteId = (int)getSatelliteID(stack);
			
			SatelliteBase satellite = DimensionManager.getInstance().getSatellite(satelliteId);

			if((satellite instanceof SatelliteOreMapping) && satellite.getDimensionId().get() == ZUtils.getDimensionIdentifier(worldIn))
				satellite.performAction(playerIn, worldIn, new BlockPos(playerIn.getPositionVec()));
		}
			
		return super.onItemRightClick(worldIn, playerIn, hand);
	}
	
	@Override
	@Nonnull
	public ActionResultType onItemUse(ItemUseContext context) {
		PlayerEntity playerIn = context.getPlayer();
		Hand hand = context.getHand();
		World worldIn = context.getWorld();
		
		if(!playerIn.world.isRemote && hand == Hand.MAIN_HAND) {
			ItemStack stack = playerIn.getHeldItem(hand);
			if(!playerIn.world.isRemote && !stack.isEmpty()) {
				int satelliteId = (int)getSatelliteID(stack);
				
				SatelliteBase satellite = DimensionManager.getInstance().getSatellite(satelliteId);

				if((satellite instanceof SatelliteOreMapping) && satellite.getDimensionId().get() == ZUtils.getDimensionIdentifier(worldIn))
					satellite.performAction(playerIn, worldIn, new BlockPos(playerIn.getPositionVec()));

			}
		}
		return super.onItemUse(context);
	}


	public void interactSatellite(SatelliteBase satellite,PlayerEntity player, World world, BlockPos pos) {
		satellite.performAction(player, world, pos);
	}

	public List<ModuleBase> getModules(int id, PlayerEntity player) {
		List<ModuleBase> modules = new LinkedList<>();
		//modules.add(new ModuleOreMapper(0, 0));
		return modules;
	}

}
