package zmaster587.advancedRocketry.asm;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

import java.util.Map;

@TransformerExclusions(value = {"zmaster587.advancedRocketry.asm.ClassTransformer"})
@MCVersion("1.12.2")
public class AdvancedRocketryPlugin implements IFMLLoadingPlugin {

	public AdvancedRocketryPlugin() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public String[] getASMTransformerClass() {
		return new String[] {ClassTransformer.class.getName()};
	}

	@Override
	public String getModContainerClass() {
		return "zmaster587.advancedRocketry.asm.ModContainer";
	}

	@Override
	public String getSetupClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
		
	}

	@Override
	public String getAccessTransformerClass() {
		// TODO Auto-generated method stub
		return null;
	}

}
