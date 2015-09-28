package zmaster587.advancedRocketry.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import zmaster587.advancedRocketry.tile.TileModelRenderRotatable;
import zmaster587.libVulpes.block.RotatableBlock;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockRotatableModel extends RotatableBlock {
	int modelID;
	
	public BlockRotatableModel(Material par2Material, int modelId) {
		super(par2Material);
		this.modelID = modelId;
	}

	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		return new TileModelRenderRotatable(modelID, getFront(metadata));
	}

	@Override
	public boolean hasTileEntity(int metadata) {
		return true;
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z,
			EntityPlayer player, int par6, float par7, float par8, float par9) {
		return false;
	}

	
	@Override
	public void registerBlockIcons(IIconRegister icons)
	{
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean renderAsNormalBlock()
	{
		return false;
	}

    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int p_149691_1_, int p_149691_2_)
    {
        return this.blockIcon;
    }
	
	@Override
	public boolean canRenderInPass(int pass) {
		return false;
	}
}
