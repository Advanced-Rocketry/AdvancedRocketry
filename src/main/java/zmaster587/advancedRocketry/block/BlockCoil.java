package zmaster587.advancedRocketry.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockCoil extends BlockMetalBlock {
	String side, poles;
	IIcon sideIcon, polesIcon;
	
	public BlockCoil(Material mat, String side, String poles) {
		super(mat);
		this.side = side;
		this.poles = poles;
	}
	
	@Override
	public void registerBlockIcons(IIconRegister register) {
		sideIcon = register.registerIcon(side);
		polesIcon = register.registerIcon(poles);
	}
	
	@Override
	public IIcon getIcon(int side, int meta) {
		return (ForgeDirection.getOrientation(side) == ForgeDirection.UP || ForgeDirection.getOrientation(side) == ForgeDirection.DOWN) ? polesIcon : sideIcon;
	}
}
