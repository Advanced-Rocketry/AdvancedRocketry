package zmaster587.advancedRocketry.entity;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class EntityItemAbducted extends EntityItem {

	public EntityItemAbducted(World par1World, double par2, double par4,
			double par6, ItemStack par8ItemStack) {
		super(par1World, par2, par4, par6, par8ItemStack);
		this.noClip = true;
		this.motionX = 0;
		this.motionZ = 0;
	}

	/*@Override
	public void onUpdate() {
		ItemStack stack = this.getDataWatcher().getWatchableObjectItemStack(10);
        if (stack != null && stack.getItem() != null)
        {
            if (stack.getItem().onEntityItemUpdate(this))
            {
                return;
            }
        }
        
        //super.onEntityUpdate();
        
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        
        this.moveEntity(this.motionX, this.motionY, this.motionZ);

        ++this.age;

        ItemStack item = getDataWatcher().getWatchableObjectItemStack(10);

        if (!this.worldObj.isRemote && this.age >= lifespan)
        {
        	this.setDead();
        }

        if (item != null && item.stackSize <= 0)
        {
            this.setDead();
        }
	}
	
	@Override
    public boolean combineItems(EntityItem par1EntityItem)
    {
		return false;
    }
    public void onCollideWithPlayer(EntityPlayer par1EntityPlayer) {}*/
}
