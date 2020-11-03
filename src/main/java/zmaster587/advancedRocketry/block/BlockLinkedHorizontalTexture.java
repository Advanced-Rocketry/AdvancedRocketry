package zmaster587.advancedRocketry.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;


public class BlockLinkedHorizontalTexture extends Block {

	public static final EnumProperty<IconNames> TYPE = EnumProperty.create("type", IconNames.class);
	
	//Mapping of side to names
	//Order is such that the side with a block can be represented as as bitmask where a side with a block is represented by a 0
	
	public BlockLinkedHorizontalTexture(Properties material) {
		super(material);
		this.setDefaultState(this.stateContainer.getBaseState().with(TYPE, IconNames.ALLEDGE));
	}
	
	@Override
	protected void fillStateContainer(Builder<Block, net.minecraft.block.BlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(TYPE);
	}
	
	@Override
	public net.minecraft.block.BlockState getStateAtViewpoint(net.minecraft.block.BlockState state, IBlockReader world,
			BlockPos pos, Vector3d viewpoint) {
		
		int offset = 0;

		if(world.getBlockState(pos.add(1,0,0)).getBlock() == this)
			offset |= 0x1;
		if(world.getBlockState(pos.add(0,0,-1)).getBlock() == this)
			offset |= 0x2;
		if(world.getBlockState(pos.add(-1,0,0)).getBlock() == this)
			offset |= 0x4;
		if(world.getBlockState(pos.add(0,0,1)).getBlock() == this)
			offset |= 0x8;
		
		return state.with(TYPE, IconNames.values()[offset]);
	}
	
	static enum IconNames implements IStringSerializable {
		ALLEDGE("all"),
		NOTRIGHTEDGE("nredge"),
		NOTTOPEDGE("ntedge"),
		TRCORNOR("trcorner"),
		NOTLEFTEDGE("nledge"),
		XCROSS("xcross"),
		TLCORNER("tlcorner"),
		BOTTOMEDGE("bottomedge"),
		NOTBOTTOMEDGE("nbedge"),
		BRCORNER("brcorner"),
		YCROSS("ycross"),
		LEFTEDGE("leftedge"),
		BLCORNER("blcorner"),
		TOPEDGE("topedge"),
		RIGHTEDGE("rightedge"),
		NOEDGE("noedge");
		
		private String suffix;
		IconNames(String suffix) {
			this.suffix = suffix;
		}
		//@Override
		public String func_176610_l() {
			return suffix;
		}

		@Override
		public String getString() {
			return null;
		}
	}
}