package zmaster587.advancedRocketry.block;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.client.ClientProxy;
import zmaster587.libVulpes.block.BlockTile;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.render.RenderHelper;

public class BlockTransciever extends BlockTile implements ISimpleBlockRenderingHandler {

	private static AxisAlignedBB bb[] = {AxisAlignedBB.getBoundingBox(.25, .25, .75, .75, .75, 1),
		AxisAlignedBB.getBoundingBox(.25, .25, 0, .75, .75, 0.25),
		AxisAlignedBB.getBoundingBox(.75, .25, .25, 1, .75, .75),
		AxisAlignedBB.getBoundingBox(0, .25, .25, 0.25, .75, .75)};
	
	public BlockTransciever(Class<? extends TileEntity> tileClass, int guiId) {
		super(tileClass, guiId);
		
	}
	
	
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world,
			int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		
		if( meta > 2 || meta < 6)
			return;
		
		AxisAlignedBB newBB = bb[meta - 2];
		
		setBlockBounds((float)newBB.minX, (float)newBB.minY, (float)newBB.minZ, (float)newBB.maxX, (float)newBB.maxY, (float)newBB.maxZ);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean renderAsNormalBlock()
	{
		return false;
	}

	
	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderType() {
		return ClientProxy.transcieverRenderType;
	}

	@Override
	public boolean isOpaqueCube() {return false;}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderBlockPass()
	{
		return 0;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void renderInventoryBlock(Block block, int metadata, int modelId,
			RenderBlocks renderer) {
		
		render(0,0,0, 2);
	}


	@Override
	@SideOnly(Side.CLIENT)
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z,
			Block block, int modelId, RenderBlocks renderer) {
		
		int meta = world.getBlockMetadata(x, y, z);

		
		return render(x,y,z, meta);
	}
	
	@SideOnly(Side.CLIENT)
	public boolean render(int x, int y, int z, int meta)
	{
		if( meta < 2 || meta > 6)
			return false;
		
		IIcon glowTex = AdvancedRocketryBlocks.blockTransciever.getBlockTextureFromSide(0);
        float minU = glowTex.getInterpolatedU(4);
        float maxU = glowTex.getInterpolatedU(12);

        float minV = glowTex.getInterpolatedV(4);
        float maxV = glowTex.getInterpolatedV(12);
		
		AxisAlignedBB newBB = bb[meta - 2];
		
		
		RenderHelper.renderCubeWithUV(Tessellator.instance, newBB.minX + x, newBB.minY + y, newBB.minZ + z, newBB.maxX + x, newBB.maxY + y, newBB.maxZ + z, minU, maxU, minV, maxV);
		return true;
	}
	


	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldRender3DInInventory(int modelId) {
		return false;
	}


	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderId() {
		return ClientProxy.transcieverRenderType;
	}
	
}
