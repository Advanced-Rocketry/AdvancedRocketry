package zmaster587.advancedRocketry.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import zmaster587.advancedRocketry.tile.TileFluidTank;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.inventory.GuiHandler.guiId;
import zmaster587.libVulpes.tile.multiblock.hatch.TileFluidHatch;
import zmaster587.libVulpes.util.IAdjBlockUpdate;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockPressurizedFluidTank extends Block {

	IIcon top;

	public BlockPressurizedFluidTank(Material material) {
		super(material);
		this.setBlockTextureName("advancedrocketry:liquidTank");
		this.setBlockBounds(0.05f, 0, 0.05f, 0.95f, 1, 0.95f);
	}

	@Override
	public boolean hasTileEntity(int metadata) {
		this.setBlockBounds(0.05f, 0, 0.05f, 0.95f, 1, 0.95f);
		return true;
	}
	
	@Override
	public boolean onBlockActivated(World world, int x,
			int y, int z, EntityPlayer player,
			int p_149727_6_, float p_149727_7_, float p_149727_8_,
			float p_149727_9_) {

		
		if(!world.isRemote)
			player.openGui(LibVulpes.instance, guiId.MODULAR.ordinal(), world, x, y, z);
		return true;
	}

	@Override
	public void registerBlockIcons(IIconRegister reg) {
		super.registerBlockIcons(reg);

		top = reg.registerIcon("advancedrocketry:machineGeneric");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int dir, int meta) {

		ForgeDirection side = ForgeDirection.getOrientation(dir);
		if(side.offsetY != 0)
			return top;
		return super.getIcon(dir, meta);
	}

	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		return new TileFluidTank((int) (64000*Math.pow(2,metadata)));
	}
	
	@Override
	public boolean shouldSideBeRendered(IBlockAccess world,
			int x, int y, int z, int side) {

		
		if(ForgeDirection.values()[side].offsetY != 0) {
			if(world.getBlock(x, y, z) == this)
			return false;
		}
		
		return super.shouldSideBeRendered(world, x, y, z, side);
	}
	
	@Override
	public void onNeighborBlockChange(World world, int x,
			int y, int z, Block block) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if(tile instanceof IAdjBlockUpdate)
			((IAdjBlockUpdate)tile).onAdjacentBlockUpdated();
	}
	
	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@Override
	public boolean isBlockNormalCube() {
		return false;
	}
}
