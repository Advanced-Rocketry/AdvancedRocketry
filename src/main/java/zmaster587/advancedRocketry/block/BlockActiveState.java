package zmaster587.advancedRocketry.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;

public class BlockActiveState extends Block {

	String activeTextureString;
	Class tileClass;
	
	public static final PropertyBool STATE = PropertyBool.create("start");
	
	public BlockActiveState(Material mat, TileEntity tile) {
		super(mat);
		tileClass = tile == null ? null : tile.getClass();
	}
	
	@Override
	public boolean hasTileEntity(IBlockState state) {
		return tileClass != null;
	}
	
	
}
