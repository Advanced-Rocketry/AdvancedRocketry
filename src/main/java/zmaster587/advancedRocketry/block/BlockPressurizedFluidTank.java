package zmaster587.advancedRocketry.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext.Builder;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fml.network.NetworkHooks;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.item.ItemBlockFluidTank;
import zmaster587.advancedRocketry.tile.TileFluidTank;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.inventory.GuiHandler.guiId;

import java.util.LinkedList;
import java.util.List;

public class BlockPressurizedFluidTank extends Block {

	private static VoxelShape bb = VoxelShapes.create(.0625, 0, 0.0625, 0.9375, 1, 0.9375);
	
	public BlockPressurizedFluidTank(Properties material) {
		super(material);
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player,
			Hand handIn, BlockRayTraceResult hit) {
		if(!world.isRemote)
		{
			TileEntity te = world.getTileEntity(pos);
			if(te != null)
				NetworkHooks.openGui((ServerPlayerEntity)player, (INamedContainerProvider)te, pos);
		}
		return ActionResultType.SUCCESS;
	}
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new TileFluidTank((int) (64000*Math.pow(2,0)));
	}
	
	@Override
	public List<ItemStack> getDrops(BlockState state, Builder builder) {
		return new LinkedList<ItemStack>();
	}
	
	@Override
	public void harvestBlock(World world, PlayerEntity player, BlockPos pos,
			BlockState state, TileEntity te, ItemStack stack) {
		
		TileEntity tile = te;//world.getTileEntity(pos);

		if(tile != null && tile instanceof TileFluidTank) {
			LazyOptional<IFluidHandler> cap = ((TileFluidTank)tile).getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, Direction.DOWN);
			
			IFluidHandler fluid = cap.orElse(null);

			ItemStack itemstack = new ItemStack(AdvancedRocketryBlocks.blockPressureTank);
			
			((ItemBlockFluidTank)itemstack.getItem()).fill(itemstack, fluid.drain(Integer.MAX_VALUE, FluidAction.SIMULATE));
			
			ItemEntity entityitem;

			int j1 = world.rand.nextInt(21) + 10;
			float f = world.rand.nextFloat() * 0.8F + 0.1F;
			float f1 = world.rand.nextFloat() * 0.8F + 0.1F;
			float f2 = world.rand.nextFloat() * 0.8F + 0.1F;

			itemstack.setCount(1);
			entityitem = new ItemEntity(world, (double)((float)pos.getX() + f), (double)((float)pos.getY() + f1), (double)((float)pos.getZ() + f2), itemstack.copy());
			float f3 = 0.05F;
			entityitem.setMotion((double)((float)world.rand.nextGaussian() * f3),
							(double)((float)world.rand.nextGaussian() * f3 + 0.2F),
							(double)((float)world.rand.nextGaussian() * f3));

			if (itemstack.hasTag())
			{
				entityitem.getItem().setTag((CompoundNBT)itemstack.getTag().copy());
			}
			world.addEntity(entityitem);
		}
		
		super.harvestBlock(world, player, pos, state, te, stack);
	}
	
	@Override
	public boolean isSideInvisible(BlockState state, BlockState adjacentBlockState, Direction side) {
		
		if(side.getYOffset() != 0) {
			if(adjacentBlockState.getBlock() == this)
				return false;
		}
		
		return super.isSideInvisible(state, adjacentBlockState, side);
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return bb;
	}

	
	@Override
	public void onNeighborChange(BlockState state, IWorldReader world, BlockPos pos, BlockPos neighbor) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof TileFluidTank)
			((TileFluidTank)tile).onAdjacentBlockUpdated(Direction.getFacingFromVector(neighbor.getX() - pos.getX(), neighbor.getY() - pos.getY(), neighbor.getZ() - pos.getZ()));
	}
}
