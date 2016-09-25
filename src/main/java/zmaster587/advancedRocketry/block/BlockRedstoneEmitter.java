package zmaster587.advancedRocketry.block;

import zmaster587.advancedRocketry.tile.TileAtmosphereDetector;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.inventory.GuiHandler;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRedstoneEmitter extends Block {
	
	public static final PropertyBool POWERED = PropertyBool.create("powered");
	
	public BlockRedstoneEmitter(Material material,String activeIconName) {
		super(material);
		this.setDefaultState(this.getDefaultState().withProperty(POWERED, false));
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[]{POWERED});
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(POWERED, (meta & 8) == 8);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(POWERED) ? 8 : 0;
	}
	
	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}
	
	public void setState(World world, IBlockState bstate, BlockPos pos, boolean state) {
		world.setBlockState(pos, bstate.withProperty(POWERED, state));
	}
	
	public boolean getState(World world, IBlockState bstate, BlockPos pos) {
		return bstate.getValue(POWERED);
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos,
			IBlockState state, EntityPlayer player, EnumHand hand,
			ItemStack heldItem, EnumFacing side, float hitX, float hitY,
			float hitZ) {
		if(!world.isRemote) {
			player.openGui(LibVulpes.instance, GuiHandler.guiId.MODULARNOINV.ordinal(), world, pos.getX(), pos.getY(), pos.getZ());
		}
		return true;
	}
	
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileAtmosphereDetector();
	}
	
	@Override
	public int getStrongPower(IBlockState blockState, IBlockAccess blockAccess,
			BlockPos pos, EnumFacing side) {
		return blockState.getValue(POWERED) ? 15 : 0;
	}
	
	@Override
	public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess,
			BlockPos pos, EnumFacing side) {
		return blockState.getValue(POWERED) ? 15 : 0;
	}
	
	@Override
	public boolean canProvidePower(IBlockState state) {
		return true;
	}

}
