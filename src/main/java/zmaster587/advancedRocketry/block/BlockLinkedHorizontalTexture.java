package zmaster587.advancedRocketry.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;


public class BlockLinkedHorizontalTexture extends Block {

    public static final PropertyEnum<IconNames> TYPE = PropertyEnum.create("type", IconNames.class);

    //Mapping of side to names
    //Order is such that the side with a block can be represented as as bitmask where a side with a block is represented by a 0

    public BlockLinkedHorizontalTexture(Material material) {
        super(material);
        this.setDefaultState(this.getDefaultState().withProperty(TYPE, IconNames.ALLEDGE));
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(TYPE, IconNames.values()[meta]);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TYPE).ordinal();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TYPE);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world,
                                      BlockPos pos) {

        int offset = 0;

        if (world.getBlockState(pos.add(1, 0, 0)).getBlock() == this)
            offset |= 0x1;
        if (world.getBlockState(pos.add(0, 0, -1)).getBlock() == this)
            offset |= 0x2;
        if (world.getBlockState(pos.add(-1, 0, 0)).getBlock() == this)
            offset |= 0x4;
        if (world.getBlockState(pos.add(0, 0, 1)).getBlock() == this)
            offset |= 0x8;

        return state.withProperty(TYPE, IconNames.values()[offset]);
    }

    enum IconNames implements IStringSerializable {
        ALLEDGE("all"),
        NOTRIGHTEDGE("nredge"),
        NOTTOPEDGE("ntedge"),
        TRCORNOR("trcorner"),
        NOTLEFTEDGE("nledge"),
        XCROSS("xcross"),
        TLCORNER("tlcorner"),
        BOTTOMEDGE("bottomedge"),
        NOTBOTTOMEDGE("nbedge"),
        BRCORNER("brcorner"),
        YCROSS("ycross"),
        LEFTEDGE("leftedge"),
        BLCORNER("blcorner"),
        TOPEDGE("topedge"),
        RIGHTEDGE("rightedge"),
        NOEDGE("noedge");

        private String suffix;

        IconNames(String suffix) {
            this.suffix = suffix;
        }

        @Override
        public String getName() {
            return suffix;
        }
    }
}