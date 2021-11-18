package zmaster587.advancedRocketry.block;

import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.atmosphere.AtmosphereHandler;

public class BlockTorchUnlitWall extends BlockTorchUnlit {

	   private static final Map<Direction, VoxelShape> SHAPES = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, Block.makeCuboidShape(5.5D, 3.0D, 11.0D, 10.5D, 13.0D, 16.0D), Direction.SOUTH, Block.makeCuboidShape(5.5D, 3.0D, 0.0D, 10.5D, 13.0D, 5.0D), Direction.WEST, Block.makeCuboidShape(11.0D, 3.0D, 5.5D, 16.0D, 13.0D, 10.5D), Direction.EAST, Block.makeCuboidShape(0.0D, 3.0D, 5.5D, 5.0D, 13.0D, 10.5D)));

	   public BlockTorchUnlitWall(AbstractBlock.Properties p_i241193_1_) {
	      super(p_i241193_1_);
	      this.setDefaultState(this.stateContainer.getBaseState().with(WallTorchBlock.HORIZONTAL_FACING, Direction.NORTH));
	   }

	   /**
	    * Returns the unlocalized name of the block with "tile." appended to the front.
	    */
	   @Nonnull
	   public String getTranslationKey() {
	      return this.asItem().getTranslationKey();
	   }

	   @Nonnull
	   @ParametersAreNonnullByDefault
	   public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
	      return func_220289_j(state);
	   }

	   public static VoxelShape func_220289_j(BlockState p_220289_0_) {
	      return SHAPES.get(p_220289_0_.get(WallTorchBlock.HORIZONTAL_FACING));
	   }

	   @ParametersAreNonnullByDefault
	   public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
		   return hasEnoughSolidSide(worldIn, pos.down(), Direction.UP);
	   }
	   
	   @Nullable
	   public BlockState getStateForPlacement(BlockItemUseContext context) {
	      BlockState blockstate = this.getDefaultState();
	      IWorldReader iworldreader = context.getWorld();
	      BlockPos blockpos = context.getPos();
	      Direction[] adirection = context.getNearestLookingDirections();

	      for(Direction direction : adirection) {
		         if (direction.getAxis().isHorizontal()) {
		            Direction direction1 = direction.getOpposite();
		            blockstate = blockstate.with(WallTorchBlock.HORIZONTAL_FACING, direction1);
		            if (blockstate.isValidPosition(iworldreader, blockpos)) {
		               return blockstate;
		            }
		         }
		      }

		      return null;
	   }
	   
		@Nonnull
        @Override
		public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
			player.getHeldItem(Hand.MAIN_HAND);
			Item item = player.getHeldItem(Hand.MAIN_HAND).getItem();
			if(!world.isRemote && item != Items.AIR && AtmosphereHandler.getOxygenHandler(world).getAtmosphereType(pos).allowsCombustion() && (item == Item.getItemFromBlock(Blocks.TORCH) ||
					item == Items.FLINT_AND_STEEL ||
					item == Items.FIRE_CHARGE)) {

				world.setBlockState(pos, Blocks.WALL_TORCH.getDefaultState().with(
						WallTorchBlock.HORIZONTAL_FACING, state.get(WallTorchBlock.HORIZONTAL_FACING)));

				return ActionResultType.SUCCESS;
			}

			return ActionResultType.SUCCESS;
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
	      return facing.getOpposite() == stateIn.get(WallTorchBlock.HORIZONTAL_FACING) && !stateIn.isValidPosition(worldIn, currentPos) ? Blocks.AIR.getDefaultState() : stateIn;
	   }

	   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
	      builder.add(WallTorchBlock.HORIZONTAL_FACING);
	   }
}
