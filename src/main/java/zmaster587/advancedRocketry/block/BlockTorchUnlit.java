package zmaster587.advancedRocketry.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TorchBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootContext.Builder;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.atmosphere.AtmosphereHandler;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlockTorchUnlit extends TorchBlock {

	public BlockTorchUnlit(Properties properties) {
		super(properties, null);
	}

	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos,
			PlayerEntity player) {
		return ARConfiguration.getCurrentConfig().dropExTorches.get() ? super.getPickBlock(state, target, world, pos, player) : new ItemStack(Blocks.TORCH);
	}
	
	@Nonnull
	@Override
	@ParametersAreNonnullByDefault
	public List<ItemStack> getDrops(BlockState state, Builder builder) {
		ArrayList<ItemStack> ret = new ArrayList<>();

		ret.add(new ItemStack(ARConfiguration.getCurrentConfig().dropExTorches.get() ? AdvancedRocketryBlocks.blockUnlitTorch : Blocks.TORCH));

		return ret;
	}

	
	
	@Nonnull
	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		player.getHeldItem(Hand.MAIN_HAND);
		Item item = player.getHeldItem(Hand.MAIN_HAND).getItem();
		if(!world.isRemote && item != Items.AIR && AtmosphereHandler.getOxygenHandler(world).getAtmosphereType(pos).allowsCombustion() && (item == Item.getItemFromBlock(Blocks.TORCH) || item == Items.FLINT_AND_STEEL || item == Items.FIRE_CHARGE)) {

			world.setBlockState(pos, Blocks.TORCH.getDefaultState());

			return ActionResultType.SUCCESS;
		}

		return ActionResultType.SUCCESS;
	}

	@Override
	@ParametersAreNonnullByDefault
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
	}
	

}
