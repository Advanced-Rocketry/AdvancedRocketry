package zmaster587.advancedRocketry.block;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.block.BlockTile;

import java.util.List;

public class BlockTileWithMultitooltip extends BlockTile {

	public BlockTileWithMultitooltip(Class<? extends TileEntity> tileClass, int guiId) {
		super(tileClass, guiId);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World player, List<String> tooltip, ITooltipFlag advanced) {
		super.addInformation(stack, player, tooltip, advanced);
		tooltip.add(ChatFormatting.DARK_GRAY + "" + ChatFormatting.ITALIC + LibVulpes.proxy.getLocalizedString("machine.tooltip.multiblock"));
	}
}
