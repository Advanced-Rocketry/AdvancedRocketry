package zmaster587.advancedRocketry.block;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;


public class BlockLinkedHorizontalTexture extends Block {

	enum icons {
		ALLEDGE(""),
		NOTRIGHTEDGE("nrEdge"),
		NOTTOPEDGE("ntEdge"),
		BLCORNER("blCorner"),
		NOTLEFTEDGE("nlEdge"),
		XCROSS("xCross"),
		BRCORNER("brCorner"),
		BOTTOMEDGE("bottomEdge"),
		NOTBOTTOMEDGE("nbEdge"),
		TLCORNER("tlCorner"),
		YCROSS("yCross"),
		LEFTEDGE("leftEdge"),
		TRCORNOR("trCorner"),
		TOPEDGE("topEdge"),
		RIGHTEDGE("rightEdge"),
		NOEDGE("noEdge");
		//SIDE("side");
		
		
		private IIcon icon;
		private String suffix;
		icons(String suffix) {
			this.suffix = suffix;
		}
		
		public IIcon getIcon() {
			return icon;
		}
	}
	
	public BlockLinkedHorizontalTexture(Material material) {
		super(material);
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
		if(world.getBlock(x, y, z + 1) == this)
			offset |= 0x2;
		if(world.getBlock(x-1, y, z) == this)
			offset |= 0x4;
		if(world.getBlock(x, y, z-1) == this)
			offset |= 0x8;
		
		world.setBlockMetadataWithNotify(x, y, z, offset, 2);
	}
	
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
    	//Top or bottom
        if(side < 2) return icons.values()[meta].icon;
        else return icons.XCROSS.icon;
    }
	
	@Override
	public void registerBlockIcons(IIconRegister iconReg) {
		for(icons i : icons.values()) {
			i.icon = iconReg.registerIcon(this.getTextureName() + "_" + i.suffix);
		}
	}
}