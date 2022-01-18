package zmaster587.advancedRocketry.block.multiblock;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.block.*;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.properties.PistonType;
import net.minecraft.tileentity.PistonTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import zmaster587.libVulpes.interfaces.IRecipe;
import zmaster587.libVulpes.recipe.RecipesMachine;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class BlockSmallPlatePress extends PistonBlock {

	public BlockSmallPlatePress(Properties properties) {
		super(false, properties);
	}

	@Override
	@ParametersAreNonnullByDefault
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean isMoving) {
		if (!world.isRemote && world.getTileEntity(pos) == null)
		{
			this.checkForMove(world, pos, state);
		}
	}

	@Override
	@ParametersAreNonnullByDefault
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.getDefaultState().with(FACING, Direction.DOWN).with(EXTENDED, Boolean.FALSE);
	}

	@Override
	@ParametersAreNonnullByDefault
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (!world.isRemote) {
			this.checkForMove(world, pos, state);
		}
	}

	private boolean shouldBeExtended(World worldIn, BlockPos pos, Direction facing) {
		for (Direction enumfacing : Direction.values()) {
			if (enumfacing != facing && worldIn.isSidePowered(pos.offset(enumfacing), enumfacing)) {
				return true;
			}
		}

		if (worldIn.isSidePowered(pos, Direction.DOWN)) {
			return true;
		} else {
			BlockPos blockpos = pos.up();

			for (Direction enumfacing1 : Direction.values()) {
				if (enumfacing1 != Direction.DOWN && worldIn.isSidePowered(blockpos.offset(enumfacing1), enumfacing1)) {
					return true;
				}
			}

			return false;
		}
	}   

	private void checkForMove(World worldIn, BlockPos pos, BlockState state) {
		Direction enumfacing = Direction.DOWN;
		boolean flag = this.shouldBeExtended(worldIn, pos, enumfacing);

		ItemStack stack;
		if (flag && (stack = getRecipe(worldIn, pos, state)) != null && !state.get(EXTENDED)) {
			worldIn.setBlockState(pos.down(), Blocks.AIR.getDefaultState());

			if(!worldIn.isRemote)
				worldIn.addEntity(new ItemEntity(worldIn, pos.getX() + 0.5, pos.getY() - 0.5, pos.getZ() + 0.5, stack));
			if (new PistonBlockStructureHelper(worldIn, pos, Direction.DOWN, true).canMove()) {
				worldIn.addBlockEvent(pos, this, 0, enumfacing.getIndex());
			}
		} else if (!flag && state.get(EXTENDED)) {
			worldIn.addBlockEvent(pos, this, 1, enumfacing.getIndex());
		}
	}

	@Override
	@ParametersAreNonnullByDefault
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		if (!world.isRemote) {
			this.checkForMove(world, pos, state);
		}
	}


	private ItemStack getRecipe(World world, BlockPos pos, BlockState state) {
		if(world.isAirBlock(pos.add(0, -1, 0)))
			return ItemStack.EMPTY;

		BlockState state2 = world.getBlockState(pos.add(0, -1, 0));
		Block block = state2.getBlock();

		Item item = Item.getItemFromBlock(block);
		if(item.equals(Items.AIR))
			return null;


		ItemStack stackInWorld =  block.getItem(world, pos.add(0, -1, 0), state2);

		List<IRecipe> recipes = RecipesMachine.getInstance().getRecipes(this.getClass());
		ItemStack stack = ItemStack.EMPTY;

		for(IRecipe recipe : recipes) {
			for(ItemStack stack2 : recipe.getPossibleIngredients().get(0))
				if(stack2.isItemEqual(stackInWorld)) {
					stack = recipe.getOutput().get(0);
					break;
				}
		}


		if(world.getBlockState(pos.add(0,-2,0)).getBlock() == Blocks.OBSIDIAN)
			return stack;

		return ItemStack.EMPTY;
	}

	private boolean doMove(World worldIn, BlockPos pos, Direction directionIn, boolean extending) {
		BlockPos blockpos = pos.offset(directionIn);
		if (!extending && worldIn.getBlockState(blockpos).getBlock() == (Blocks.PISTON_HEAD)) {
			worldIn.setBlockState(blockpos, Blocks.AIR.getDefaultState(), 20);
		}

		PistonBlockStructureHelper pistonblockstructurehelper = new PistonBlockStructureHelper(worldIn, pos, directionIn, extending);
		if (!pistonblockstructurehelper.canMove()) {
			return false;
		} else {
			Map<BlockPos, BlockState> map = Maps.newHashMap();
			List<BlockPos> list = pistonblockstructurehelper.getBlocksToMove();
			List<BlockState> list1 = Lists.newArrayList();

			for (BlockPos blockpos1 : list) {
				BlockState blockstate = worldIn.getBlockState(blockpos1);
				list1.add(blockstate);
				map.put(blockpos1, blockstate);
			}

			List<BlockPos> list2 = pistonblockstructurehelper.getBlocksToDestroy();
			BlockState[] ablockstate = new BlockState[list.size() + list2.size()];
			Direction direction = extending ? directionIn : directionIn.getOpposite();
			int j = 0;

			for(int k = list2.size() - 1; k >= 0; --k) {
				BlockPos blockpos2 = list2.get(k);
				BlockState blockstate1 = worldIn.getBlockState(blockpos2);
				TileEntity tileentity = blockstate1.hasTileEntity() ? worldIn.getTileEntity(blockpos2) : null;
				spawnDrops(blockstate1, worldIn, blockpos2, tileentity);
				worldIn.setBlockState(blockpos2, Blocks.AIR.getDefaultState(), 18);
				ablockstate[j++] = blockstate1;
			}

			for(int l = list.size() - 1; l >= 0; --l) {
				BlockPos blockpos3 = list.get(l);
				BlockState blockstate5 = worldIn.getBlockState(blockpos3);
				blockpos3 = blockpos3.offset(direction);
				map.remove(blockpos3);
				worldIn.setBlockState(blockpos3, Blocks.MOVING_PISTON.getDefaultState().with(FACING, directionIn), 68);
				worldIn.setTileEntity(blockpos3, MovingPistonBlock.createTilePiston(list1.get(l), directionIn, extending, false));
				ablockstate[j++] = blockstate5;
			}

			if (extending) {
				PistonType pistontype =  PistonType.DEFAULT;
				BlockState blockstate4 = Blocks.PISTON_HEAD.getDefaultState().with(PistonHeadBlock.FACING, directionIn).with(PistonHeadBlock.TYPE, pistontype);
				BlockState blockstate6 = Blocks.MOVING_PISTON.getDefaultState().with(MovingPistonBlock.FACING, directionIn).with(MovingPistonBlock.TYPE, PistonType.DEFAULT);
				map.remove(blockpos);
				worldIn.setBlockState(blockpos, blockstate6, 68);
				worldIn.setTileEntity(blockpos, MovingPistonBlock.createTilePiston(blockstate4, directionIn, true, true));
			}

			BlockState blockstate3 = Blocks.AIR.getDefaultState();

			for(BlockPos blockpos4 : map.keySet()) {
				worldIn.setBlockState(blockpos4, blockstate3, 82);
			}

			for(Entry<BlockPos, BlockState> entry : map.entrySet()) {
				BlockPos blockpos5 = entry.getKey();
				BlockState blockstate2 = entry.getValue();
				blockstate2.updateDiagonalNeighbors(worldIn, blockpos5, 2);
				blockstate3.updateNeighbours(worldIn, blockpos5, 2);
				blockstate3.updateDiagonalNeighbors(worldIn, blockpos5, 2);
			}

			j = 0;

			for(int i1 = list2.size() - 1; i1 >= 0; --i1) {
				BlockState blockstate7 = ablockstate[j++];
				BlockPos blockpos6 = list2.get(i1);
				blockstate7.updateDiagonalNeighbors(worldIn, blockpos6, 2);
				worldIn.notifyNeighborsOfStateChange(blockpos6, blockstate7.getBlock());
			}

			for(int j1 = list.size() - 1; j1 >= 0; --j1) {
				worldIn.notifyNeighborsOfStateChange(list.get(j1), ablockstate[j++].getBlock());
			}

			if (extending) {
				worldIn.notifyNeighborsOfStateChange(blockpos, Blocks.PISTON_HEAD);
			}

			return true;
		}
	}

	/**
	 * Called on server when World#addBlockEvent is called. If server returns true, then also called on the client. On
	 * the Server, this may perform additional changes to the world, like pistons replacing the block with an extended
	 * base. On the client, the update may involve replacing tile entities or effects such as sounds or particles
	 * @deprecated call via {@link BlockState#receiveBlockEvent(World, BlockPos, int, int)} whenever possible.
	 * Implementing/overriding is fine.
	 */
	 public boolean eventReceived(BlockState state, World worldIn, BlockPos pos, int id, int param) {
		 Direction direction = state.get(FACING);
		 if (!worldIn.isRemote) {
			 boolean flag = this.shouldBeExtended(worldIn, pos, direction);
			 if (flag && (id == 1 || id == 2)) {
				 worldIn.setBlockState(pos, state.with(EXTENDED, Boolean.TRUE), 2);
				 return false;
			 }

			 if (!flag && id == 0) {
				 return false;
			 }
		 }

		 if (id == 0) {
			 if (net.minecraftforge.event.ForgeEventFactory.onPistonMovePre(worldIn, pos, direction, true)) return false;
			 if (!this.doMove(worldIn, pos, direction, true)) {
				 return false;
			 }

			 worldIn.setBlockState(pos, state.with(EXTENDED, Boolean.TRUE), 67);
			 worldIn.playSound(null, pos, SoundEvents.BLOCK_PISTON_EXTEND, SoundCategory.BLOCKS, 0.5F, worldIn.rand.nextFloat() * 0.25F + 0.6F);
		 } else if (id == 1 || id == 2) {
			 if (net.minecraftforge.event.ForgeEventFactory.onPistonMovePre(worldIn, pos, direction, false)) return false;
			 TileEntity tileentity1 = worldIn.getTileEntity(pos.offset(direction));
			 if (tileentity1 instanceof PistonTileEntity) {
				 ((PistonTileEntity)tileentity1).clearPistonTileEntity();
			 }

			 BlockState blockstate = Blocks.MOVING_PISTON.getDefaultState().with(MovingPistonBlock.FACING, direction).with(MovingPistonBlock.TYPE, PistonType.DEFAULT);
			 worldIn.setBlockState(pos, blockstate, 20);
			 worldIn.setTileEntity(pos, MovingPistonBlock.createTilePiston(this.getDefaultState().with(FACING, Direction.byIndex(param & 7)), direction, false, true));
			 worldIn.updateBlock(pos, blockstate.getBlock());
			 blockstate.updateNeighbours(worldIn, pos, 2);
			 worldIn.removeBlock(pos.offset(direction), false);


			 worldIn.playSound(null, pos, SoundEvents.BLOCK_PISTON_CONTRACT, SoundCategory.BLOCKS, 0.5F, worldIn.rand.nextFloat() * 0.15F + 0.6F);
		 }

		 net.minecraftforge.event.ForgeEventFactory.onPistonMovePost(worldIn, pos, direction, (id == 0));
		 return true;
	 }


	@Override
	@ParametersAreNonnullByDefault
	@OnlyIn(value=Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)  {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		Style style = Style.EMPTY.setFormatting(TextFormatting.DARK_GRAY).setFormatting(TextFormatting.ITALIC);
		
		tooltip.add( new TranslationTextComponent("machine.tooltip.smallplatepress").mergeStyle(style));
	}

}
