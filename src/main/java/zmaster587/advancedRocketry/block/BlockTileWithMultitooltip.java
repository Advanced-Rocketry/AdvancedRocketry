package zmaster587.advancedRocketry.block;

import java.util.List;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.block.BlockTile;
import zmaster587.libVulpes.inventory.GuiHandler;

import java.util.List;

public class BlockTileWithMultitooltip extends BlockTile {

	public BlockTileWithMultitooltip(Properties properties, GuiHandler.guiId guiId) {
		super(properties, guiId);
	}

	@Override
	@OnlyIn(value=Dist.CLIENT)
	public void addInformation(ItemStack stack, IBlockReader worldIn, List<ITextComponent> tooltip,
			ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		tooltip.add(new StringTextComponent(TextFormatting.DARK_GRAY + "" + TextFormatting.ITALIC + LibVulpes.proxy.getLocalizedString("machine.tooltip.multiblock")));
	}
}
