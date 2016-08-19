package zmaster587.advancedRocketry.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockSolarPanel extends Block {
	
	public BlockSolarPanel(Material p_i45394_1_) {
		super(p_i45394_1_);
	}

	protected IIcon sides, top, bottom;
	
	@Override
	public void registerBlockIcons(IIconRegister icons) {
		//super.registerBlockIcons(icon);
		top = icons.registerIcon("AdvancedRocketry:solar");
		sides = icons.registerIcon("AdvancedRocketry:panelSide");
		bottom = icons.registerIcon("libvulpes:machineGeneric");
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int dir, int meta) {
		
		ForgeDirection side = ForgeDirection.getOrientation(dir);
		
		if(side == ForgeDirection.UP)
			return this.top;
		else if(side == ForgeDirection.DOWN)
			return this.bottom;
		else
			return this.sides;
	}
}
