package zmaster587.advancedRocketry.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.BooleanProperty;
import net.minecraft.tileentity.TileEntityType;

public class BlockActiveState extends Block {

	String activeTextureString;
	TileEntityType<?> tileClass;
	
	public static final BooleanProperty STATE = BooleanProperty.create("start");
	
	public BlockActiveState(Properties mat, TileEntityType<?> tile) {
		super(mat);
		tileClass = tile;
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return tileClass != null;
	}
	
	
}
