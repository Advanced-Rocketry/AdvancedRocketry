package zmaster587.advancedRocketry.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;


public class BlockLinkedHorizontalTexture extends Block {

	enum iconNames {
		ALLEDGE(""),
		NOTRIGHTEDGE("nrEdge"),
		NOTTOPEDGE("ntEdge"),
		TRCORNOR("trCorner"),
		NOTLEFTEDGE("nlEdge"),
		XCROSS("xCross"),
		TLCORNER("tlCorner"),
		BOTTOMEDGE("bottomEdge"),
		NOTBOTTOMEDGE("nbEdge"),
		BRCORNER("brCorner"),
		YCROSS("yCross"),
		LEFTEDGE("leftEdge"),
		BLCORNER("blCorner"),
		TOPEDGE("topEdge"),
		RIGHTEDGE("rightEdge"),
		NOEDGE("noEdge");
		//SIDE("side");
		
		private String suffix;
		iconNames(String suffix) {
			this.suffix = suffix;
		}
	}
	
	private IIcon icons[] = new IIcon[16];
	
	public BlockLinkedHorizontalTexture(Material material) {
		super(material);
	}
	
	@Override
	public void onBlockAdded(World world, int x,
			int y, int z) {


	}
	
	@Override
	public void onBlockPlacedBy(World world, int x,
			int y, int z, EntityLivingBase p_149689_5_,
			ItemStack p_149689_6_) {
		// TODO Auto-generated method stub
		super.onBlockPlacedBy(world, x, y, z,
				p_149689_5_, p_149689_6_);
		//right bit 1,	 1
		//top, bit 2, 	 2
		// left bit 3,	 4
		// bottom bit 4	 8
		// if true then it's not an edge
		int offset = 0;
		
		if(world.getBlock(x + 1, y, z) == this)
			offset |= 0x1;
		if(world.getBlock(x, y, z - 1) == this)
			offset |= 0x2;
		if(world.getBlock(x-1, y, z) == this)
			offset |= 0x4;
		if(world.getBlock(x, y, z+1) == this)
			offset |= 0x8;
		
		world.setBlockMetadataWithNotify(x, y, z, offset, 2);
	}
	
	@Override
	public void onNeighborBlockChange(World world, int x,
			int y, int z, Block block) {
		
		//right bit 1,	 1
		//top, bit 2, 	 2
		// left bit 3,	 4
		// bottom bit 4	 8
		// if true then it's not an edge
		int offset = 0;
		
		if(world.getBlock(x + 1, y, z) == this)
			offset |= 0x1;
		if(world.getBlock(x, y, z - 1) == this)
			offset |= 0x2;
		if(world.getBlock(x-1, y, z) == this)
			offset |= 0x4;
		if(world.getBlock(x, y, z+1) == this)
			offset |= 0x8;
		
		world.setBlockMetadataWithNotify(x, y, z, offset, 2);
	}
	
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
    	//Top or bottom
        if(side < 2) 
        	return icons[meta];
        else 
        	return icons[iconNames.XCROSS.ordinal()];
    }
	
	@Override
	public void registerBlockIcons(IIconRegister iconReg) {
		for(iconNames i : iconNames.values()) {
			icons[i.ordinal()] = iconReg.registerIcon(this.getTextureName() + "_" + i.suffix);
		}
	}
}