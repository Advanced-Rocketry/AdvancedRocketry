package zmaster587.advancedRocketry.api.capability;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import zmaster587.advancedRocketry.api.IAtmosphere;
import zmaster587.advancedRocketry.api.armor.IProtectiveArmor;

import org.jetbrains.annotations.NotNull;

public class CapabilitySpaceArmor {

	@CapabilityInject(IProtectiveArmor.class)
	public static Capability<IProtectiveArmor> PROTECTIVEARMOR = null;

	public CapabilitySpaceArmor() {}



	public static void register() { CapabilityManager.INSTANCE.register(IProtectiveArmor.class, new Capability.IStorage<IProtectiveArmor>() 
			{
		@Override
		public void readNBT(Capability<IProtectiveArmor> capability,
				IProtectiveArmor instance, EnumFacing side, NBTBase nbt) {

		}
		@Override
		public NBTBase writeNBT(
				Capability<IProtectiveArmor> capability,
				IProtectiveArmor instance, EnumFacing side) {
			return null;
		}
			}, ((IProtectiveArmor) (atmosphere, stack, commitProtection) -> false).getClass());
	}

}
