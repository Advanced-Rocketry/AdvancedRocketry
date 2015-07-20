package zmaster587.advancedRocketry.world.gen;

import java.util.concurrent.Callable;

import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ReportedException;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.layer.GenLayer;
import zmaster587.advancedRocketry.api.AdvancedRocketryBiomes;

public abstract class GenLayerExtendedCompare extends GenLayer {

	public GenLayerExtendedCompare(long p_i2125_1_) {
		super(p_i2125_1_);
	}

	public static boolean compareBiomesById(final int biomeA, final int biomeB) {
		if (biomeA == biomeB)
		{
			return true;
		}
		else if (biomeA != BiomeGenBase.mesaPlateau_F.biomeID && biomeA != BiomeGenBase.mesaPlateau.biomeID)
		{
			try
			{
				return AdvancedRocketryBiomes.instance.getBiomeById(biomeA) != null && AdvancedRocketryBiomes.instance.getBiomeById(biomeB) != null ? AdvancedRocketryBiomes.instance.getBiomeById(biomeA).isEqualTo(AdvancedRocketryBiomes.instance.getBiomeById(biomeB)) : false;
			}
			catch (Throwable throwable)
			{
				CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Comparing biomes");
				CrashReportCategory crashreportcategory = crashreport.makeCategory("Biomes being compared");
				crashreportcategory.addCrashSection("Biome A ID", Integer.valueOf(biomeA));
				crashreportcategory.addCrashSection("Biome B ID", Integer.valueOf(biomeB));
				crashreportcategory.addCrashSectionCallable("Biome A", new Callable()
				{
					public String call()
					{
						return String.valueOf(AdvancedRocketryBiomes.instance.getBiomeById(biomeA));
					}
				});
				crashreportcategory.addCrashSectionCallable("Biome B", new Callable()
				{
					public String call()
					{
						return String.valueOf(AdvancedRocketryBiomes.instance.getBiomeById(biomeB));
					}
				});
				throw new ReportedException(crashreport);
			}
		}
		else
		{
			return biomeB == BiomeGenBase.mesaPlateau_F.biomeID || biomeB == BiomeGenBase.mesaPlateau.biomeID;
		}
	}
}
