package zmaster587.advancedRocketry.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class EntityItemAbducted extends Entity {

    private static final DataParameter<ItemStack> ITEM = EntityDataManager.createKey(EntityItem.class, DataSerializers.ITEM_STACK);

    private int lifespan;
    private int age = 0;
    private EntityItem itemEntity;

    public EntityItemAbducted(World par1World, double par2, double par4,
                              double par6, @Nonnull ItemStack par8ItemStack) {
        super(par1World);

        this.setEntityItemStack(par8ItemStack);
        setPosition(par2, par4, par6);
        this.setSize(0.25F, 0.25F);
        this.noClip = true;
        this.lifespan = 200;
        this.motionX = 0;
        this.motionY = 2;
        this.motionZ = 0;
    }

    public EntityItemAbducted(World world) {
        super(world);
        this.noClip = true;
        this.lifespan = 200;
        this.motionX = 0;
        this.motionY = 2;
        this.motionZ = 0;
    }

    public int getLifespan() {
        return lifespan;
    }

    public int getAge() {
        return age;
    }

    protected void entityInit() {
        this.getDataManager().register(ITEM, ItemStack.EMPTY);
    }

    @Override
    public void onUpdate() {
        ItemStack stack = this.getDataManager().get(ITEM);

        if (this.getEntityItem().isEmpty()) {
            this.setDead();
        }
        //super.onEntityUpdate();

        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);

        ++this.age;

        if (!this.world.isRemote && this.age >= lifespan) {
            this.setDead();
        }

        if (!stack.isEmpty() && stack.getCount() <= 0) {
            this.setDead();
        }
    }

    /**
     * Returns the ItemStack corresponding to the Entity (Note: if no item exists, will log an error but still return an
     * ItemStack containing Block.stone)
     */
    @Nonnull
    public ItemStack getEntityItem() {
        ItemStack itemstack = this.getDataManager().get(ITEM);

        if (itemstack.isEmpty()) {
            return new ItemStack(Blocks.STONE);
        } else {
            return itemstack;
        }
    }

    /**
     * Sets the ItemStack for this entity
     */
    public void setEntityItemStack(@Nonnull ItemStack stack) {
        this.getDataManager().set(ITEM, stack);
        this.getDataManager().setDirty(ITEM);
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound p_70014_1_) {
        p_70014_1_.setShort("Age", (short) this.age);
        p_70014_1_.setInteger("Lifespan", lifespan);


        if (!this.getEntityItem().isEmpty()) {
            p_70014_1_.setTag("Item", this.getEntityItem().writeToNBT(new NBTTagCompound()));
        }
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound p_70037_1_) {
        this.age = p_70037_1_.getShort("Age");


        NBTTagCompound nbttagcompound1 = p_70037_1_.getCompoundTag("Item");
        this.setEntityItemStack(new ItemStack(nbttagcompound1));

        ItemStack stack = this.getDataManager().get(ITEM);

        if (stack.isEmpty() || stack.getCount() <= 0) {
            this.setDead();
        }

        if (p_70037_1_.hasKey("Lifespan")) {
            lifespan = p_70037_1_.getInteger("Lifespan");
        }
    }

    public EntityItem getItemEntity() {
        if (itemEntity == null) {
            itemEntity = new EntityItem(world, this.posX, this.posY, this.posZ, getEntityItem());
        }
        return itemEntity;
    }
}
