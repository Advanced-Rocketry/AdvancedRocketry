package zmaster587.advancedRocketry.block.plant;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.IStringSerializable;

public class BlockLightwoodPlanks extends Block {

    public static final PropertyEnum<BlockLightwoodPlanks.EnumType> VARIANT = PropertyEnum.create("variant", BlockLightwoodPlanks.EnumType.class);

    public BlockLightwoodPlanks() {
        super(Material.WOOD);
        this.setLightLevel(4);
        this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, BlockLightwoodPlanks.EnumType.ALIEN));
    }

    /**
     * Gets the metadata of the item this Block can drop. This method is called when the block gets destroyed. It
     * returns the metadata of the dropped item based on the old metadata of the block.
     */
    public int damageDropped(IBlockState state) {
        return state.getValue(VARIANT).getMetadata();
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(VARIANT, BlockLightwoodPlanks.EnumType.byMetadata(meta));
    }

    /**
     * Get the MapColor for this Block and the given BlockState
     */
    public MapColor getMapColor(IBlockState state) {
        return state.getValue(VARIANT).getMapColor();
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state) {
        return state.getValue(VARIANT).getMetadata();
    }

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, VARIANT);
    }

    public enum EnumType implements IStringSerializable {
        ALIEN(0, "alien", "alien", MapColor.LAPIS);

        private static final BlockLightwoodPlanks.EnumType[] META_LOOKUP = new BlockLightwoodPlanks.EnumType[values().length];

        static {
            for (BlockLightwoodPlanks.EnumType blockalienplank$enumtype : values()) {
                META_LOOKUP[blockalienplank$enumtype.getMetadata()] = blockalienplank$enumtype;
            }
        }

        private final int meta;
        private final String name;
        private final String unlocalizedName;
        /**
         * The color that represents this entry on a map.
         */
        private final MapColor mapColor;

        EnumType(int metaIn, String nameIn, String unlocalizedNameIn, MapColor mapColorIn) {
            this.meta = metaIn;
            this.name = nameIn;
            this.unlocalizedName = unlocalizedNameIn;
            this.mapColor = mapColorIn;
        }

        public static BlockLightwoodPlanks.EnumType byMetadata(int meta) {
            if (meta < 0 || meta >= META_LOOKUP.length) {
                meta = 0;
            }

            return META_LOOKUP[meta];
        }

        public int getMetadata() {
            return this.meta;
        }

        /**
         * The color which represents this entry on a map.
         */
        public MapColor getMapColor() {
            return this.mapColor;
        }

        public String toString() {
            return this.name;
        }

        public String getName() {
            return this.name;
        }

        public String getUnlocalizedName() {
            return this.unlocalizedName;
        }
    }
}
