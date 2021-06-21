package zmaster587.advancedRocketry.asm;

<<<<<<< HEAD
/*import com.google.common.eventbus.EventBus;
=======
>>>>>>> origin/feature/nuclearthermalrockets
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.Collections;


public class ModContainer extends DummyModContainer {
	
	//ModContainer Class adapted from SackCastellon
	public ModContainer()
	{		
		super(new ModMetadata());
		
		System.out.println("********* CoreDummyContainer. OK");
		
		ModMetadata meta = getMetadata();
		
		meta.modId = "advancedrocketrycore";
		meta.name = "Advanced Rocketry Core";
		meta.version = "1";
		meta.credits = "Created by Zmaster587";
		meta.authorList = Collections.singletonList("Zmaster587");
		meta.description = "ASM handler for AR";
		meta.url = "";
		meta.updateUrl = "";
		meta.screenshots = new String[0];
		meta.logoFile = "";
	}

	public boolean registerBus(EventBus bus, LoadController controller)
	{
		System.out.println("********* registerBus. OK");
		bus.register(this);
		return true;
	}
	
	@EventHandler
	public void modConstruction(FMLConstructionEvent event) {}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {}

	@EventHandler
	public void load(FMLInitializationEvent event) {}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {}
}*/
