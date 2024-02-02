package zmaster587.advancedRocketry.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityDummy extends Entity {


    //Just a dummy so a player can sit on a chair
    public EntityDummy(World world) {
        super(world);
        this.noClip = true;
        this.height = 0f;

    }

    public EntityDummy(World world, double x, double y, double z) {
        this(world);
        setPosition(x, y, z);
    }

    @Override
    public boolean isInvisible() {
        return true;
    }

    @Override
    public boolean isInvisibleToPlayer(EntityPlayer player) {
        return true;
    }

    /**
     * Checks if the entity is in range to render by using the past in distance and comparing it to its average edge
     * length * 64 * renderDistanceWeight Args: distance
     */
    @SideOnly(Side.CLIENT)
    @Override
    public boolean isInRangeToRenderDist(double p_70112_1_) {
        return false;
    }


    @Override
    protected void entityInit() {

    }

    @Override
    public boolean shouldRiderSit() {
        return true;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound p_70037_1_) {

    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound p_70014_1_) {

    }


}
