package zmaster587.advancedRocketry.block;

import java.util.LinkedList;
import java.util.List;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.tile.TileFluidTank;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.inventory.GuiHandler.guiId;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class BlockPressurizedFluidTank extends Block {

	private static AxisAlignedBB bb = new AxisAlignedBB(.0625, 0, 0.0625, 0.9375, 1, 0.9375);

	public BlockPressurizedFluidTank(Material material) {
		super(material);
		isBlockContainer = true;
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos,
			IBlockState state, EntityPlayer player, EnumHand hand,
			ItemStack heldItem, EnumFacing side, float hitX, float hitY,
			float hitZ) {

		if(!world.isRemote)
			player.openGui(LibVulpes.instance, guiId.MODULAR.ordinal(), world, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileFluidTank((int) (64000*Math.pow(2,0)));
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos,
			IBlockState state, int fortune) {
		return new LinkedList<ItemStack>();
	}
	
	@Override
	public void harvestBlock(World world, EntityPlayer player, BlockPos pos,
			IBlockState state, TileEntity te, ItemStack stack) {
		
		TileEntity tile = te;//world.getTileEntity(pos);

		if(tile != null && tile instanceof TileFluidTank) {
			IFluidHandler fluid = ((TileFluidTank)tile).getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.DOWN);


			ItemStack itemstack = new ItemStack(AdvancedRocketryBlocks.blockPressureTank);
			IFluidHandler fluidItem = itemstack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.DOWN);

			fluidItem.fill(fluid.drain(Integer.MAX_VALUE, false), true);
			
			EntityItem entityitem;

			int j1 = world.rand.nextInt(21) + 10;
			float f = world.rand.nextFloat() * 0.8F + 0.1F;
			float f1 = world.rand.nextFloat() * 0.8F + 0.1F;
			float f2 = world.rand.nextFloat() * 0.8F + 0.1F;

			itemstack.stackSize = 1;
			entityitem = new EntityItem(world, (double)((float)pos.getX() + f), (double)((float)pos.getY() + f1), (double)((float)pos.getZ() + f2), new ItemStack(itemstack.getItem(), 1, 0));
			float f3 = 0.05F;
			entityitem.motionX = (double)((float)world.rand.nextGaussian() * f3);
			entityitem.motionY = (double)((float)world.rand.nextGaussian() * f3 + 0.2F);
			entityitem.motionZ = (double)((float)world.rand.nextGaussian() * f3);

			if (itemstack.hasTagCompound())
			{
				entityitem.getEntityItem().setTagCompound((NBTTagCompound)itemstack.getTagCompound().copy());
			}
			world.spawnEntityInWorld(entityitem);
		}
		
		super.harvestBlock(world, player, pos, state, te, stack);
	}
	
	@Override
	public boolean shouldSideBeRendered(IBlockState blockState,
			IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {

		if(side.getFrontOffsetY() != 0) {
			if(blockAccess.getBlockState(pos).getBlock() == this)
				return true;
		}

		return super.shouldSideBeRendered(blockState, blockAccess, pos, side);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source,
			BlockPos pos) {
		return bb;
	}

	@Override
	public void onNeighborChange(IBlockAccess world, BlockPos pos,
			BlockPos neighbor) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof TileFluidTank)
			((TileFluidTank)tile).onAdjacentBlockUpdated(EnumFacing.getFacingFromVector(neighbor.getX() - pos.getX(), neighbor.getY() - pos.getY(), neighbor.getZ() - pos.getZ()));
	}

	@Override
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
