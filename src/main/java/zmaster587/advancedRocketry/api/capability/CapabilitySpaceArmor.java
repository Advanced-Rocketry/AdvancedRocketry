package zmaster587.advancedRocketry.api.capability;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import zmaster587.advancedRocketry.api.armor.IProtectiveArmor;

public class CapabilitySpaceArmor {

	@CapabilityInject(IProtectiveArmor.class)
	public static Capability<IProtectiveArmor> PROTECTIVEARMOR = null;

	public CapabilitySpaceArmor() {}


	public static IProtectiveArmor generateArmor()
	{
		return (atmosphere, stack, commitProtection) -> false;
	}

	public static void register() { CapabilityManager.INSTANCE.register(IProtectiveArmor.class, new Capability.IStorage<IProtectiveArmor>() 
			{
				@Override
				public INBT writeNBT(Capability<IProtectiveArmor> capability, IProtectiveArmor instance, Direction side) {
					// TODO Auto-generated method stub
					return null;
				}
				@Override
				public void readNBT(Capability<IProtectiveArmor> capability, IProtectiveArmor instance, Direction side,
						INBT nbt) {
					// TODO Auto-generated method stub
				}
			}, CapabilitySpaceArmor::generateArmor );
	}

}
