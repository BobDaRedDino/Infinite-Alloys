package infinitealloys.core;

import infinitealloys.block.BlockMachine;
import infinitealloys.block.BlockOre;
import infinitealloys.block.Blocks;
import infinitealloys.handlers.EventHandler;
import infinitealloys.handlers.GfxHandler;
import infinitealloys.handlers.WorldGenHandler;
import infinitealloys.item.ItemAlloyBook;
import infinitealloys.item.ItemAlloyIngot;
import infinitealloys.item.ItemBlockIA;
import infinitealloys.item.ItemInternetWand;
import infinitealloys.item.ItemIngot;
import infinitealloys.item.ItemMulti;
import infinitealloys.item.ItemUpgrade;
import infinitealloys.item.Items;
import infinitealloys.tile.TEHelper;
import infinitealloys.tile.TEMAnalyzer;
import infinitealloys.tile.TEUComputer;
import infinitealloys.tile.TEMMetalForge;
import infinitealloys.tile.TEMPasture;
import infinitealloys.tile.TEMPrinter;
import infinitealloys.tile.TEMXray;
import infinitealloys.util.Consts;
import infinitealloys.util.Funcs;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.stats.Achievement;
import net.minecraftforge.common.AchievementPage;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class CommonProxy {

	private ItemStack[] alloys = new ItemStack[Consts.VALID_ALLOY_COUNT];
	private ItemStack[] upgrades = new ItemStack[Consts.UPGRADE_COUNT];
	public GfxHandler gfxHandler;

	public void initLocalization() {}

	public void initBlocks() {
		Blocks.ore = new BlockOre(Blocks.oreID).setHardness(3F).setUnlocalizedName("IAore");
		Blocks.machine = new BlockMachine(Blocks.machineID).setHardness(3F).setUnlocalizedName("IAmachine");
		GameRegistry.registerBlock(Blocks.ore, ItemBlockIA.class, "IAore");
		GameRegistry.registerBlock(Blocks.machine, ItemBlockIA.class, "IAmachine");
		OreDictionary.registerOre("oreZinc", new ItemStack(Blocks.ore, 1, 0));
		OreDictionary.registerOre("oreMagnesium", new ItemStack(Blocks.ore, 1, 1));
		OreDictionary.registerOre("oreScandium", new ItemStack(Blocks.ore, 1, 2));
		OreDictionary.registerOre("oreTantalum", new ItemStack(Blocks.ore, 1, 3));
		MinecraftForge.setBlockHarvestLevel(Blocks.ore, 0, "pickaxe", 1);
		MinecraftForge.setBlockHarvestLevel(Blocks.ore, 1, "pickaxe", 1);
		MinecraftForge.setBlockHarvestLevel(Blocks.ore, 2, "pickaxe", 1);
		MinecraftForge.setBlockHarvestLevel(Blocks.ore, 3, "pickaxe", 1);
		MinecraftForge.setBlockHarvestLevel(Blocks.ore, 4, "pickaxe", 2);
		MinecraftForge.setBlockHarvestLevel(Blocks.ore, 5, "pickaxe", 2);
		MinecraftForge.setBlockHarvestLevel(Blocks.ore, 6, "pickaxe", 2);
		MinecraftForge.setBlockHarvestLevel(Blocks.ore, 7, "pickaxe", 3);
		MinecraftForge.setBlockHarvestLevel(Blocks.machine, 0, "pickaxe", 0);
		MinecraftForge.setBlockHarvestLevel(Blocks.machine, 1, "pickaxe", 0);
		MinecraftForge.setBlockHarvestLevel(Blocks.machine, 2, "pickaxe", 0);
		MinecraftForge.setBlockHarvestLevel(Blocks.machine, 3, "pickaxe", 0);
	}

	public void initItems() {
		Items.multi = new ItemMulti(Items.multiID);
		Items.ingot = new ItemIngot(Items.ingotID);
		Items.alloyIngot = new ItemAlloyIngot(Items.alloyIngotID);
		Items.upgrade = new ItemUpgrade(Items.upgradeID);
		Items.internetWand = new ItemInternetWand(Items.internetWandID).setMaxStackSize(1);
		Items.alloyBook = new ItemAlloyBook(Items.alloyBookID).setMaxStackSize(1);
		OreDictionary.registerOre("ingotZinc", new ItemStack(Items.ingot));
		OreDictionary.registerOre("ingotMagnesium", new ItemStack(Items.ingot, 1, 1));
		OreDictionary.registerOre("ingotScandium", new ItemStack(Items.ingot, 1, 2));
		OreDictionary.registerOre("ingotTantalum", new ItemStack(Items.ingot, 1, 3));
		for(int i = 0; i < alloys.length; i++)
			alloys[i] = new ItemStack(Items.alloyIngot, 1, i + 1);
		for(int i = 0; i < upgrades.length; i++)
			upgrades[i] = new ItemStack(Items.upgrade, 1, (int)Math.pow(2D, i));
	}

	public void initRecipes() {
		addRecipeDict(new ItemStack(Items.multi), " W ", "CBC", " W ", 'B', "battery", 'C', "ingotCopper", 'W', "copperWire");
		addRecipeDict(new ItemStack(Items.multi, 1, 1), "CTC", "IWI", 'C', "ingotCopper", 'I', Item.ingotIron, 'T', "ingotTin", 'W', "copperWire");
		addRecipe(new ItemStack(Blocks.machine), "ASA", "WCG", "ABA", 'A', alloys[2], 'B', alloys[3], 'C', Items.multi, 'G', Block.thinGlass, 'S', Block.stoneButton, 'W', upgrades[8]); // Computer
		addRecipe(new ItemStack(Blocks.machine, 1, 1), "BDB", "BCB", "NNN", 'B', Block.brick, 'C', Items.multi, 'D', Item.doorIron, 'N', Block.netherrack); // Metal
																																							// Forge
		addRecipe(new ItemStack(Blocks.machine, 1, 2), " G ", "RCR", "III", 'C', Items.multi, 'G', Block.glowStone, 'I', Item.ingotIron, 'R', Item.redstone); // Analyzer
		addRecipe(new ItemStack(Blocks.machine, 1, 3), "APA", "BCB", "OIO", 'A', alloys[0], 'B', alloys[1], 'C', Items.multi, 'I', new ItemStack(Item.dyePowder, 1, 15), 'O', Block.obsidian, 'P',
				Block.pistonBase); // Printer
		addRecipe(new ItemStack(Blocks.machine, 1, 4), "ADA", "BCB", "EGE", 'A', alloys[4], 'B', alloys[5], 'C', Items.multi, 'D', Item.diamond, 'E', Item.enderPearl, 'G', Block.thinGlass); // X-ray
		addRecipeDict(upgrades[0], "AGA", " U ", "ACA", 'A', alloys[2], 'C', "basicCircuit", 'G', Item.ingotGold, 'U', new ItemStack(Items.multi, 1, 1)); // Speed
																																							// I
		addRecipeDict(upgrades[1], "ADA", " U ", "ACA", 'A', alloys[5], 'C', "eliteCircuit", 'D', Item.diamond, 'U', upgrades[0]); // Speed II
		addRecipeDict(upgrades[2], "AIA", " U ", "ACA", 'A', alloys[1], 'C', "basicCircuit", 'I', Item.shovelIron, 'U', new ItemStack(Items.multi, 1, 1)); // Efficiency
																																							// I
		addRecipeDict(upgrades[3], "AGA", " U ", "ACA", 'A', alloys[4], 'C', "advancedCircuit", 'G', Item.shovelGold, 'U', upgrades[2]); // Efficiency II
		addRecipeDict(upgrades[4], "ASA", " U ", "ACA", 'A', alloys[0], 'C', "basicCircuit", 'S', Block.chest, 'U', new ItemStack(Items.multi, 1, 1)); // Capacity
																																						// I
		addRecipeDict(upgrades[5], "ASA", " U ", "ACA", 'A', alloys[3], 'C', "advancedCircuit", 'S', Block.chest, 'U', upgrades[4]); // Capacity II
		addRecipeDict(upgrades[6], "AIA", " U ", "ACA", 'A', alloys[3], 'C', "basicCircuit", 'I', Item.swordIron, 'U', new ItemStack(Items.multi, 1, 1)); // Range
																																							// I
		addRecipeDict(upgrades[7], "AGA", " U ", "ACA", 'A', alloys[5], 'C', "eliteCircuit", 'G', Item.swordGold, 'U', upgrades[6]); // Range II
		addRecipeDict(upgrades[8], "AEA", " U ", "ACA", 'A', alloys[1], 'C', "basicCircuit", 'E', Item.enderPearl, 'U', new ItemStack(Items.multi, 1, 1)); // Wireless
		addRecipeDict(new ItemStack(Items.internetWand), " W ", "RSR", 'R', Item.redstone, 'S', "basicCircuit", 'W', upgrades[8]);
		addRecipe(new ItemStack(Items.alloyBook), "R", "B", 'B', Item.writableBook, 'R', Item.redstone);
		addSmelting(Blocks.ore.blockID, 0, new ItemStack(Items.ingot), 0.6F);
		addSmelting(Blocks.ore.blockID, 1, new ItemStack(Items.ingot, 1, 1), 0.6F);
		addSmelting(Blocks.ore.blockID, 2, new ItemStack(Items.ingot, 1, 2), 0.7F);
		addSmelting(Blocks.ore.blockID, 3, new ItemStack(Items.ingot, 1, 3), 0.7F);
		addSmelting(Blocks.ore.blockID, 4, new ItemStack(Items.ingot, 1, 4), 0.85F);
		addSmelting(Blocks.ore.blockID, 5, new ItemStack(Items.ingot, 1, 5), 0.85F);
		addSmelting(Blocks.ore.blockID, 6, new ItemStack(Items.ingot, 1, 6), 0.85F);
		addSmelting(Blocks.ore.blockID, 7, new ItemStack(Items.ingot, 1, 7), 1.0F);
	}

	public void initTileEntities() {
		GameRegistry.registerTileEntity(TEUComputer.class, "Computer");
		GameRegistry.registerTileEntity(TEMMetalForge.class, "MetalForge");
		GameRegistry.registerTileEntity(TEMAnalyzer.class, "Analyzer");
		GameRegistry.registerTileEntity(TEMPrinter.class, "Printer");
		GameRegistry.registerTileEntity(TEMXray.class, "Xray");
		GameRegistry.registerTileEntity(TEMPasture.class, "Pasture");
		TEHelper.addDetectable(Block.oreCoal, 1);
		TEHelper.addDetectable(Block.oreIron, 2);
		TEHelper.addDetectable(Block.oreGold, 6);
		TEHelper.addDetectable(Block.oreDiamond, 8);
		TEHelper.addDictDetectables("oreZinc", 3);
		TEHelper.addDictDetectables("oreMagnesium", 4);
		TEHelper.addDictDetectables("oreScandium", 5);
		TEHelper.addDictDetectables("oreTantalum", 6);
		TEHelper.addDetectable(Blocks.ore, 4, 7);
		TEHelper.addDetectable(Blocks.ore, 5, 8);
		TEHelper.addDetectable(Blocks.ore, 6, 9);
		TEHelper.addDetectable(Blocks.ore, 7, 10);
		TEHelper.addDictDetectables("oreCopper", 2);
		TEHelper.addDictDetectables("oreTin", 2);
	}

	public void initHandlers() {
		gfxHandler = new GfxHandler();
		EventHandler eventHandler = new EventHandler();
		MinecraftForge.EVENT_BUS.register(eventHandler);
		GameRegistry.registerCraftingHandler(eventHandler);
		GameRegistry.registerWorldGenerator(new WorldGenHandler());
		NetworkRegistry.instance().registerGuiHandler(InfiniteAlloys.instance, gfxHandler);
	}

	public void initAchievements() {
		InfiniteAlloys.achievements[0] = new Achievement(2000, "craftMetalForge", 0, 0, new ItemStack(Blocks.machine, 1, 1), null).registerAchievement();
		for(int i = 1; i <= Consts.VALID_ALLOY_COUNT; i++)
			InfiniteAlloys.achievements[i] = new Achievement(2000 + i, "smeltAlloy" + i, 2 * i, 0, new ItemStack(Items.alloyIngot, 1, i), InfiniteAlloys.achievements[i - 1]).registerAchievement();
		InfiniteAlloys.achPage = new AchievementPage("Infinite Alloys", InfiniteAlloys.achievements);
		AchievementPage.registerAchievementPage(InfiniteAlloys.achPage);
	}

	public void initRendering() {}

	protected void addName(Object obj, String... keys) {
		String name = "";
		for(String key : keys)
			name = name + Funcs.getLoc(key);
		LanguageRegistry.addName(obj, name);
	}

	private static void addRecipe(ItemStack result, Object... params) {
		GameRegistry.addRecipe(result, params);
	}

	private static void addRecipeDict(ItemStack result, Object... params) {
		GameRegistry.addRecipe(new ShapedOreRecipe(result, params));
	}

	private static void addSmelting(int inputID, int inputDamage, ItemStack output, float experience) {
		FurnaceRecipes.smelting().addSmelting(inputID, inputDamage, output, experience);
	}
}