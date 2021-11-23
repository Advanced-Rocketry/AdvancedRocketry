package zmaster587.advancedRocketry.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;


public class BlockLinkedHorizontalTexture extends Block {
	public static final EnumProperty<IconNames> TYPE = EnumProperty.create("type", IconNames.class);

	//Mapping of side to names
	//Order is such that the side with a block can be represented as as bitmask where a side with a block is represented by a 0

	public BlockLinkedHorizontalTexture(Properties material) {
		super(material);
		this.setDefaultState(this.stateContainer.getBaseState().with(TYPE, IconNames.ALLEDGE));
	}

	@Override
	@ParametersAreNonnullByDefault
	protected void fillStateContainer(Builder<Block, net.minecraft.block.BlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(TYPE);
	}

	@Override
	public BlockState getStateAtViewpoint(BlockState state, IBlockReader world,
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

	public BlockState getStateForPlacement(BlockItemUseContext context) {
		BlockPos blockpos = context.getPos();
		World world = context.getWorld();
		
		int offset = 0;

		if(world.getBlockState(blockpos.add(1,0,0)).getBlock() == this)
			offset |= 0x1;
		if(world.getBlockState(blockpos.add(0,0,-1)).getBlock() == this)
			offset |= 0x2;
		if(world.getBlockState(blockpos.add(-1,0,0)).getBlock() == this)
			offset |= 0x4;
		if(world.getBlockState(blockpos.add(0,0,1)).getBlock() == this)
			offset |= 0x8;

		return this.getDefaultState().with(TYPE, IconNames.values()[offset]);
	}

	/**
	 * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
	 * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
	 * returns its solidified counterpart.
	 * Note that this method should ideally consider only the specific face passed in.
	 */
	@Nonnull
	@ParametersAreNonnullByDefault
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		
		int state = stateIn.get(TYPE).ordinal();
		int facingFlag = 0;
		
		if(facing.getXOffset() == 1)
			facingFlag = 0x1;
		else if(facing.getXOffset() == -1)
			facingFlag = 0x4;
		else if(facing.getZOffset() == 1)
			facingFlag = 0x8;
		else if(facing.getZOffset() == -1)
			facingFlag = 0x2;

		if(facingState.getBlock() == stateIn.getBlock())
			state |= facingFlag;
		else
			state &= ~facingFlag;
		
		return this.getDefaultState().with(TYPE, IconNames.values()[state]);
	}

	enum IconNames implements IStringSerializable {
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
		@Nonnull
		@Override
		public String getString() {
			return suffix;
		}

	}
}