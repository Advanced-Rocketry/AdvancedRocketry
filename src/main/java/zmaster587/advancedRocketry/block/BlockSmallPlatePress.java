package zmaster587.advancedRocketry.block;

import com.google.common.collect.Lists;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.block.*;
import net.minecraft.block.state.BlockPistonStructureHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.interfaces.IRecipe;
import zmaster587.libVulpes.recipe.RecipesMachine;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

public class BlockSmallPlatePress extends BlockPistonBase {

    public BlockSmallPlatePress() {
        super(false);
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        if (!world.isRemote && world.getTileEntity(pos) == null) {
            this.checkForMove(world, pos, state);
        }
    }


    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos,
                                            EnumFacing facing, float hitX, float hitY, float hitZ, int meta,
                                            EntityLivingBase placer) {
        return this.getDefaultState().withProperty(FACING, EnumFacing.DOWN).withProperty(EXTENDED, Boolean.FALSE);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state,
                                EntityLivingBase placer, @Nonnull ItemStack stack) {
        if (!world.isRemote) {
            this.checkForMove(world, pos, state);
        }
    }

    private boolean shouldBeExtended(World worldIn, BlockPos pos, EnumFacing facing) {
        for (EnumFacing enumfacing : EnumFacing.values()) {
            if (enumfacing != facing && worldIn.isSidePowered(pos.offset(enumfacing), enumfacing)) {
                return true;
            }
        }

        if (worldIn.isSidePowered(pos, EnumFacing.DOWN)) {
            return true;
        } else {
            BlockPos blockpos = pos.up();

            for (EnumFacing enumfacing1 : EnumFacing.values()) {
                if (enumfacing1 != EnumFacing.DOWN && worldIn.isSidePowered(blockpos.offset(enumfacing1), enumfacing1)) {
                    return true;
                }
            }

            return false;
        }
    }

    private void checkForMove(World worldIn, BlockPos pos, IBlockState state) {
        EnumFacing enumfacing = EnumFacing.DOWN;
        boolean flag = this.shouldBeExtended(worldIn, pos, enumfacing);

        ItemStack stack;
        if (flag && !(stack = getRecipe(worldIn, pos, state)).isEmpty() && !state.getValue(EXTENDED)) {
            worldIn.setBlockToAir(pos.down());

            if (!worldIn.isRemote)
                worldIn.spawnEntity(new EntityItem(worldIn, pos.getX() + 0.5, pos.getY() - 0.5, pos.getZ() + 0.5, stack));
            if ((new BlockPistonStructureHelper(worldIn, pos, enumfacing, true)).canMove()) {
                worldIn.addBlockEvent(pos, this, 0, enumfacing.getIndex());
            }
        } else if (!flag && state.getValue(EXTENDED)) {
            worldIn.addBlockEvent(pos, this, 1, enumfacing.getIndex());
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public void neighborChanged(IBlockState state, World world, BlockPos pos,
                                Block blockIn, BlockPos fromPos) {
        if (!world.isRemote) {
            this.checkForMove(world, pos, state);
        }
    }

    @Nonnull
    private ItemStack getRecipe(World world, BlockPos pos, IBlockState state) {
        if (world.isAirBlock(pos.add(0, -1, 0)))
            return ItemStack.EMPTY;

        IBlockState state2 = world.getBlockState(pos.add(0, -1, 0));
        Block block = state2.getBlock();

        Item item = Item.getItemFromBlock(block);
        if (item.equals(Items.AIR))
            return ItemStack.EMPTY;

        ItemStack stackInWorld = new ItemStack(item, 1, block.getMetaFromState(state2));

        List<IRecipe> recipes = RecipesMachine.getInstance().getRecipes(this.getClass());
        ItemStack stack = ItemStack.EMPTY;

        for (IRecipe recipe : recipes) {
            for (@Nonnull ItemStack stack2 : recipe.getIngredients().get(0))
                if (stack2.isItemEqual(stackInWorld)) {
                    stack = recipe.getOutput().get(0);
                    break;
                }
        }


        if (world.getBlockState(pos.add(0, -2, 0)).getBlock() == Blocks.OBSIDIAN)
            return stack;

        return ItemStack.EMPTY;
    }

    private boolean doMove(World worldIn, BlockPos pos, EnumFacing direction, boolean extending) {
        if (!extending) {
            worldIn.setBlockToAir(pos.offset(direction));
        }

        BlockPistonStructureHelper blockpistonstructurehelper = new BlockPistonStructureHelper(worldIn, pos, direction, extending);

        if (!blockpistonstructurehelper.canMove()) {
            return false;
        } else {
            List<BlockPos> list = blockpistonstructurehelper.getBlocksToMove();
            List<IBlockState> list1 = Lists.newArrayList();

            for (BlockPos blockPos : list) {
                list1.add(worldIn.getBlockState(blockPos).getActualState(worldIn, blockPos));
            }

            List<BlockPos> list2 = blockpistonstructurehelper.getBlocksToDestroy();
            int k = list.size() + list2.size();
            IBlockState[] aiblockstate = new IBlockState[k];
            EnumFacing enumfacing = extending ? direction : direction.getOpposite();

            for (int j = list2.size() - 1; j >= 0; --j) {
                BlockPos blockpos1 = list2.get(j);
                IBlockState iblockstate = worldIn.getBlockState(blockpos1);
                // Forge: With our change to how snowballs are dropped this needs to disallow to mimic vanilla behavior.
                float chance = iblockstate.getBlock() instanceof BlockSnow ? -1.0f : 1.0f;
                iblockstate.getBlock().dropBlockAsItemWithChance(worldIn, blockpos1, iblockstate, chance, 0);
                worldIn.setBlockToAir(blockpos1);
                --k;
                aiblockstate[k] = iblockstate;
            }

            for (int l = list.size() - 1; l >= 0; --l) {
                BlockPos blockpos3 = list.get(l);
                IBlockState iblockstate2 = worldIn.getBlockState(blockpos3);
                worldIn.setBlockState(blockpos3, Blocks.AIR.getDefaultState(), 2);
                blockpos3 = blockpos3.offset(enumfacing);
                worldIn.setBlockState(blockpos3, Blocks.PISTON_EXTENSION.getDefaultState().withProperty(FACING, direction), 4);
                worldIn.setTileEntity(blockpos3, BlockPistonMoving.createTilePiston(list1.get(l), direction, extending, false));
                --k;
                aiblockstate[k] = iblockstate2;
            }

            BlockPos blockpos2 = pos.offset(direction);

            if (extending) {
                BlockPistonExtension.EnumPistonType blockpistonextension$enumpistontype = BlockPistonExtension.EnumPistonType.DEFAULT;
                IBlockState iblockstate3 = Blocks.PISTON_HEAD.getDefaultState().withProperty(BlockPistonExtension.FACING, direction).withProperty(BlockPistonExtension.TYPE, blockpistonextension$enumpistontype);
                IBlockState iblockstate1 = Blocks.PISTON_EXTENSION.getDefaultState().withProperty(BlockPistonMoving.FACING, direction).withProperty(BlockPistonMoving.TYPE, BlockPistonExtension.EnumPistonType.DEFAULT);
                worldIn.setBlockState(blockpos2, iblockstate1, 4);
                worldIn.setTileEntity(blockpos2, BlockPistonMoving.createTilePiston(iblockstate3, direction, true, false));
            }

            for (int i1 = list2.size() - 1; i1 >= 0; --i1) {
                worldIn.notifyNeighborsOfStateChange(list2.get(i1), aiblockstate[k++].getBlock(), true);
            }

            for (int j1 = list.size() - 1; j1 >= 0; --j1) {
                worldIn.notifyNeighborsOfStateChange(list.get(j1), aiblockstate[k++].getBlock(), true);
            }

            if (extending) {
                worldIn.notifyNeighborsOfStateChange(blockpos2, Blocks.PISTON_HEAD, true);
                worldIn.notifyNeighborsOfStateChange(pos, this, true);
            }

            return true;
        }
    }

    public boolean eventReceived(IBlockState state, World worldIn, @Nonnull BlockPos pos, int id, int param) {
        EnumFacing enumfacing = EnumFacing.DOWN;

        if (!worldIn.isRemote) {
            boolean flag = this.shouldBeExtended(worldIn, pos, enumfacing);

            if (flag && id == 1) {
                worldIn.setBlockState(pos, state.withProperty(EXTENDED, Boolean.TRUE), 2);
                return false;
            }

            if (!flag && id == 0) {
                return false;
            }
        }

        if (id == 0) {
            if (!this.doMove(worldIn, pos, enumfacing, true)) {
                return false;
            }

            worldIn.setBlockState(pos, state.withProperty(EXTENDED, Boolean.TRUE), 2);
            worldIn.playSound(null, pos, SoundEvents.BLOCK_PISTON_EXTEND, SoundCategory.BLOCKS, 0.5F, worldIn.rand.nextFloat() * 0.25F + 0.6F);
        } else if (id == 1) {

            worldIn.setBlockState(pos, Blocks.PISTON_EXTENSION.getDefaultState().withProperty(BlockPistonMoving.FACING, enumfacing).withProperty(BlockPistonMoving.TYPE, BlockPistonExtension.EnumPistonType.DEFAULT), 3);
            worldIn.setTileEntity(pos, BlockPistonMoving.createTilePiston(this.getStateFromMeta(param), enumfacing, false, true));


            worldIn.playSound(null, pos, SoundEvents.BLOCK_PISTON_CONTRACT, SoundCategory.BLOCKS, 0.5F, worldIn.rand.nextFloat() * 0.15F + 0.6F);
        }

        return true;
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(@Nonnull ItemStack stack, World player, List<String> tooltip, ITooltipFlag advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(ChatFormatting.DARK_GRAY + "" + ChatFormatting.ITALIC + LibVulpes.proxy.getLocalizedString("machine.tooltip.smallplatepress"));
    }

}
