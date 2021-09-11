package zmaster587.advancedRocketry.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.item.ItemBlockFluidTank;
import zmaster587.advancedRocketry.tile.TileFluidTank;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.inventory.GuiHandler.guiId;
import zmaster587.libVulpes.tile.multiblock.hatch.TileFluidHatch;
import zmaster587.libVulpes.util.FluidUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.ParametersAreNullableByDefault;
import java.util.LinkedList;
import java.util.List;

public class BlockPressurizedFluidTank extends Block {

	private static final AxisAlignedBB bb = new AxisAlignedBB(.0625, 0, 0.0625, 0.9375, 1, 0.9375);
	
	public BlockPressurizedFluidTank(Material material) {
		super(material);
		isBlockContainer = true;
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		TileEntity tile = world.getTileEntity(pos);

		//Do some fancy fluid stuff
		if (FluidUtils.containsFluid(player.getHeldItem(hand))) {
			FluidUtil.interactWithFluidHandler(player, hand, ((TileFluidHatch) tile).getFluidTank());
		} else if(!world.isRemote)
			player.openGui(LibVulpes.instance, guiId.MODULAR.ordinal(), world, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}

	@Override
	@ParametersAreNullableByDefault
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileFluidTank((int) (64000 * ARConfiguration.getCurrentConfig().blockTankCapacity));
	}
	
	@Override
	@Nonnull
	@ParametersAreNullableByDefault
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos,
			IBlockState state, int fortune) {
		return new LinkedList<>();
	}
	
	@Override
	@ParametersAreNonnullByDefault
	public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, @Nonnull ItemStack stack) {

		if(te instanceof TileFluidTank) {
			IFluidHandler fluid = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.DOWN);


			ItemStack itemstack = new ItemStack(AdvancedRocketryBlocks.blockPressureTank);
			
			((ItemBlockFluidTank)itemstack.getItem()).fill(itemstack, fluid.drain(Integer.MAX_VALUE, false));
			
			EntityItem entityitem;

			int j1 = world.rand.nextInt(21) + 10;
			float f = world.rand.nextFloat() * 0.8F + 0.1F;
			float f1 = world.rand.nextFloat() * 0.8F + 0.1F;
			float f2 = world.rand.nextFloat() * 0.8F + 0.1F;

			itemstack.setCount(1);
			entityitem = new EntityItem(world, (float)pos.getX() + f, (float)pos.getY() + f1, (float)pos.getZ() + f2, new ItemStack(itemstack.getItem(), 1, 0));
			float f3 = 0.05F;
			entityitem.motionX = (float)world.rand.nextGaussian() * f3;
			entityitem.motionY = (float)world.rand.nextGaussian() * f3 + 0.2F;
			entityitem.motionZ = (float)world.rand.nextGaussian() * f3;

			if (itemstack.hasTagCompound())
			{
				entityitem.getItem().setTagCompound(itemstack.getTagCompound().copy());
			}
			world.spawnEntity(entityitem);
		}
		
		super.harvestBlock(world, player, pos, state, te, stack);
	}
	
	@Override
	@ParametersAreNonnullByDefault
	public boolean shouldSideBeRendered(IBlockState blockState,
			IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		
		if(side.getFrontOffsetY() != 0) {
			if(blockAccess.getBlockState(pos).getBlock() == this)
			return true;
		}
		
		return super.shouldSideBeRendered(blockState, blockAccess, pos, side);
	}
	
	@Override
	@Nonnull
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source,
			BlockPos pos) {
		return bb;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public void onNeighborChange(IBlockAccess world, BlockPos pos,
			BlockPos neighbor) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof TileFluidTank)
			((TileFluidTank)tile).onAdjacentBlockUpdated(EnumFacing.getFacingFromVector(neighbor.getX() - pos.getX(), neighbor.getY() - pos.getY(), neighbor.getZ() - pos.getZ()));
	}
	
	@Override
	@Nonnull
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
	
	@Override
	public boolean isBlockNormalCube(IBlockState state) {
		return false;
	}
}
