package infinitealloys;

import infinitealloys.block.Blocks;
import infinitealloys.item.Items;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.stats.Achievement;
import net.minecraftforge.common.AchievementPage;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(modid = "InfiniteAlloys", name = "Infinite Alloys", version = "0.1.1.2")
@NetworkMod(channels = { "InfiniteAlloys" }, clientSideRequired = true, serverSideRequired = false, packetHandler = infinitealloys.handlers.PacketHandler.class)
public class InfiniteAlloys {

	@Instance("InfiniteAlloys")
	public static InfiniteAlloys instance;
	@SidedProxy(clientSide = "infinitealloys.client.ClientProxy", serverSide = "infinitealloys.CommonProxy")
	public static CommonProxy proxy;
	public static boolean[] spawnOres = new boolean[References.metalCount];
	public static CreativeTabs tabIA;
	public static Achievement[] achievements = new Achievement[7];
	public static AchievementPage achPage;
	public WorldData worldData;

	@PreInit
	public void preInit(FMLPreInitializationEvent event) {
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		Blocks.oreID = config.getBlock("Ore", 3000).getInt();
		Blocks.machineID = config.getBlock("Machine", 3001).getInt();
		Items.multiID = config.getItem(Configuration.CATEGORY_ITEM, "MultiItem", 15000).getInt();
		Items.ingotID = config.getItem(Configuration.CATEGORY_ITEM, "Ingot", 15001).getInt();
		Items.alloyIngotID = config.getItem(Configuration.CATEGORY_ITEM, "AlloyIngot", 15002).getInt();
		Items.upgradeID = config.getItem(Configuration.CATEGORY_ITEM, "Upgrade", 15003).getInt();
		Items.gpsID = config.getItem(Configuration.CATEGORY_ITEM, "GPS", 15004).getInt();
		Items.alloyBookID = config.getItem(Configuration.CATEGORY_ITEM, "AlloyBook", 15005).getInt();
		int[] metalColors = { 0x858586, 0xd2cda3, 0xccc34f, 0xcde0ef, 0xae2305, 0x177c19, 0x141dce, 0x7800be };
		for(int i = 0; i < References.metalCount; i++)
			References.metalColors[i] = config.get("Metal Colors", References.metalNames[i], metalColors[i]).getInt();
		for(int i = 0; i < References.metalCount; i++)
			spawnOres[i] = config.get("World Gen", References.metalNames[i], true).getBoolean(true);
		config.save();
	}

	@Init
	public void load(FMLInitializationEvent event) {
		tabIA = new CreativeTabIA(CreativeTabs.getNextID(), "main");
		proxy.initLocalization();
		proxy.initBlocks();
		proxy.initItems();
		proxy.initRecipes();
		proxy.initTileEntities();
		proxy.initHandlers();
		proxy.initAchievements();
		proxy.initRendering();
	}

	@PostInit
	public void postInit(FMLPostInitializationEvent event) {}

	public static int intAtPos(int radix, int strlen, int n, int pos) {
		return Character.digit(addLeadingZeros(Integer.toString(n, radix), strlen).charAt(pos), radix);
	}

	public static double logn(int base, double num) {
		return Math.log(num) / Math.log(base);
	}

	public static String addLeadingZeros(String s, int finalSize) {
		s.trim();
		int length = s.length();
		for(int i = 0; i < finalSize - length; i++)
			s = "0" + s;
		return s;
	}

	public static String getStringLocalization(String key) {
		return LanguageRegistry.instance().getStringLocalization(key);
	}
}
