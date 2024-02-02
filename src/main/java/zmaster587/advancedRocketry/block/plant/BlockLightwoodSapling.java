package zmaster587.advancedRocketry.block.plant;

import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zmaster587.advancedRocketry.world.gen.WorldGenAlienTree;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.ParametersAreNullableByDefault;
import java.util.List;
import java.util.Random;

public class BlockLightwoodSapling extends BlockBush implements IGrowable {

    public static final PropertyInteger STAGE = PropertyInteger.create("stage", 0, 1);
    public String[] names = new String[]{"blueTree"};

    public BlockLightwoodSapling() {
        this.setDefaultState(this.blockState.getBaseState().withProperty(STAGE, 0));
    }

    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item item, CreativeTabs creativeTab, List<ItemStack> list) {
        list.add(new ItemStack(item, 1, 0));
    }


    public void generateTree(World world, BlockPos pos, IBlockState state,
                             Random random) {
        if (!net.minecraftforge.event.terraingen.TerrainGen.saplingGrowTree(world, random, pos))
            return;

        IBlockState l = this.getDefaultState();//world.getBlockState(pos);//world.getBlockMetadata(x, y, z) & 7;
        WorldGenerator object = new WorldGenAlienTree(true);
        int i1 = 0;
        int j1 = 0;

        if (!object.generate(world, random, pos.add(i1, 0, j1))) {

            world.setBlockState(pos, l, 4);

        }
    }

    @ParametersAreNullableByDefault
    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
        return true;
    }

    public boolean canUseBonemeal(World worldIn, @Nonnull Random rand, @Nullable BlockPos pos, @Nullable IBlockState state) {
        return (double) worldIn.rand.nextFloat() < 0.45D;
    }

    @ParametersAreNonnullByDefault
    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        if (state.getValue(STAGE) == 0) {
            worldIn.setBlockState(pos, state.cycleProperty(STAGE), 4);
        } else {
            this.generateTree(worldIn, pos, state, rand);
        }
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    @Nonnull
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(STAGE, (meta & 8) >> 3);
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state) {
        int i = 0;
        i = i | state.getValue(STAGE) << 3;
        return i;
    }

    @Nonnull
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, STAGE);
    }
}
