package zmaster587.advancedRocketry.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zmaster587.libVulpes.block.INamedMetaBlock;

public class BlockCrystal extends Block implements INamedMetaBlock {
	
	public final static PropertyEnum<EnumCrystal> CRYSTALPROPERTY = PropertyEnum.create("type", EnumCrystal.class);
	
    public BlockCrystal()
    {
        super(Material.GLASS);
        this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
        this.setSoundType(SoundType.GLASS);
        this.setDefaultState(this.getDefaultState().withProperty(CRYSTALPROPERTY, EnumCrystal.AMETHYST));
    }
	
    @Override
    public IBlockState getStateFromMeta(int meta) {
    	return this.getDefaultState().withProperty(CRYSTALPROPERTY, EnumCrystal.values()[meta]);
    }
    
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {CRYSTALPROPERTY});
    }
    
    @Override
    public int getMetaFromState(IBlockState state) {
    	return state.getValue(CRYSTALPROPERTY).ordinal();
    }
    
    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }
    @Override
	public String getUnlocalizedName(int itemDamage) {
		return  "tile." + names[itemDamage];
	}
    
    
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.TRANSLUCENT;
    }
    
	@Override
	public void getSubBlocks(CreativeTabs tab,
			NonNullList<ItemStack> list) {
		for(int i = 0; i < colors.length; i++) {
			list.add(new ItemStack(this, 1, i));
		}
	}
	
	@Override
	public int damageDropped(IBlockState state) {
		return getMetaFromState(state);
	}
	
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState blockState,
			IBlockAccess world, BlockPos pos, EnumFacing side) {
       
        EnumFacing dir = side;//side.getOpposite();
        IBlockState blockState2 = world.getBlockState(pos.offset(dir));
       
        return  blockState.equals(blockState2) ? false : super.shouldSideBeRendered(blockState, world, pos, side);
   
	}
    
	private static final int colors[] =  {0xb23fff, 0x3333ff, 0x00ff00, 0xff3434, 0xffff34, 0xff9400};
	private static final String names[] =  {"amethyst", "sapphire", "emerald", "ruby", "citrine", "wulfentite"};
	public static final int numMetas = colors.length;
	
    public enum EnumCrystal implements IStringSerializable
    {
        AMETHYST(0, 0xb23fff, "amethyst", MapColor.PURPLE),
        SAPPHIRE(1, 0x3333ff, "sapphire", MapColor.BLUE),
        EMERALD(2, 0x00ff00, "emerald", MapColor.GREEN),
        RUBY(3, 0xff3434, "ruby", MapColor.RED),
        CITRINE(4, 0xffff34, "citrine", MapColor.YELLOW),
        WULFENTITE(5, 0xff9400, "wulfentite", MapColor.YELLOW);

        private final int meta;
        private final String name;
        private final MapColor mapColor;
        private final int color;

        private EnumCrystal(int meta, int color, String name, MapColor mapColorIn)
        {
        	this.color = color;
            this.meta = meta;
            this.name = name;
            this.mapColor = mapColorIn;
        }

        public int getMetadata()
        {
            return this.meta;
        }

        public String getUnlocalizedName()
        {
            return this.name;
        }

        public int getColor() {
        	return color;
        }
        
        public MapColor getMapColor()
        {
            return this.mapColor;
        }

        public String toString()
        {
            return this.name;
        }

        public String getName()
        {
            return this.name;
        }
    }
}
