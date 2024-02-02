package zmaster587.advancedRocketry.block;

import net.minecraft.block.BlockTorch;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.atmosphere.AtmosphereHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.ParametersAreNullableByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlockTorchUnlit extends BlockTorch {

    public BlockTorchUnlit() {
        this.setTickRandomly(true);
        this.setCreativeTab(null);
    }

    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public ItemStack getPickBlock(IBlockState state, @Nullable RayTraceResult target,
                                  World world, BlockPos pos, @Nullable EntityPlayer player) {
        return ARConfiguration.getCurrentConfig().dropExTorches ? super.getPickBlock(state, target, world, pos, player) : new ItemStack(Blocks.TORCH);
    }

    @Override
    @Nonnull
    @ParametersAreNullableByDefault
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos,
                                    IBlockState state, int fortune) {
        ArrayList<ItemStack> ret = new ArrayList<>();


        ret.add(new ItemStack(ARConfiguration.getCurrentConfig().dropExTorches ? AdvancedRocketryBlocks.blockUnlitTorch : Blocks.TORCH));

        return ret;
    }

    @Override
    public boolean onBlockActivated(@Nonnull World world, @Nonnull BlockPos pos,
                                    @Nonnull IBlockState state, @Nonnull EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY,
                                    float hitZ) {

        if (!player.getHeldItem(EnumHand.MAIN_HAND).isEmpty()) {
            Item item = player.getHeldItem(EnumHand.MAIN_HAND).getItem();
            AtmosphereHandler atmhandler = AtmosphereHandler.getOxygenHandler(world.provider.getDimension());

            if (atmhandler != null
                    && !world.isRemote
                    && !item.equals(Items.AIR)
                    && atmhandler.getAtmosphereType(pos).allowsCombustion()
                    && (item == Item.getItemFromBlock(Blocks.TORCH)
                    || item == Items.FLINT_AND_STEEL
                    || item == Items.FIRE_CHARGE)) {

                world.setBlockState(pos, Blocks.TORCH.getDefaultState().withProperty(FACING, state.getValue(FACING)));

                return true;
            }
        }

        return true;
    }


    @Override
    public void randomDisplayTick(IBlockState stateIn, World worldIn,
                                  BlockPos pos, Random rand) {
        //Do nothing
    }

}
