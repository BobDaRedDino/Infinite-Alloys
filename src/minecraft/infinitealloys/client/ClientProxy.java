package infinitealloys.client;

import infinitealloys.CommonProxy;
import infinitealloys.InfiniteAlloys;
import infinitealloys.References;
import infinitealloys.TileEntityAnalyzer;
import infinitealloys.TileEntityComputer;
import infinitealloys.TileEntityMetalForge;
import infinitealloys.TileEntityPrinter;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.src.Block;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.ItemStack;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntityRenderer;
import net.minecraftforge.client.MinecraftForgeClient;

public class ClientProxy extends CommonProxy implements ISimpleBlockRenderingHandler {

	public static int renderId;
	private TileEntityComputer tec = new TileEntityComputer();
	private TileEntityMetalForge temf = new TileEntityMetalForge();
	private TileEntityPrinter tep = new TileEntityPrinter();

	@Override
	public void initLocalization() {
		for(String langFile : References.langFiles)
			LanguageRegistry.instance().loadLocalization(getClass().getResource(langFile), langFile.substring(langFile.lastIndexOf('/') + 1, langFile.lastIndexOf('.')), true);
	}

	@Override
	public void initBlocks() {
		super.initBlocks();
		for(int i = 0; i < References.metalCount; i++)
			addName(new ItemStack(InfiniteAlloys.ore, 0, i), "metal." + References.metalNames[i] + ".name", "tile.iaOre.name");
		addName(new ItemStack(InfiniteAlloys.machine, 1, 0), "machine.computer.name");
		addName(new ItemStack(InfiniteAlloys.machine, 1, 1), "machine.metalforge.name");
		addName(new ItemStack(InfiniteAlloys.machine, 1, 2), "machine.analyzer.name");
		addName(new ItemStack(InfiniteAlloys.machine, 1, 3), "machine.printer.name");
	}

	@Override
	public void initItems() {
		super.initItems();
		for(int i = 0; i < References.metalCount; i++)
			addName(new ItemStack(InfiniteAlloys.ingot, 0, i), "metal." + References.metalNames[i] + ".name", "item.iaIngot.name");
		addName(new ItemStack(InfiniteAlloys.alloyIngot), "item.iaAlloyIngot.name");
		addName(new ItemStack(InfiniteAlloys.upgrade), "item.iaUpgrade.name");
		addName(new ItemStack(InfiniteAlloys.gps), "item.iaGps.name");
		addName(new ItemStack(InfiniteAlloys.alloyBook), "item.iaAlloyBook.name");
	}

	@Override
	public void initRendering() {
		MinecraftForgeClient.preloadTexture(References.TEXTURE_PATH + "tex.png");
		MinecraftForgeClient.preloadTexture(References.TEXTURE_PATH + "computer.png");
		MinecraftForgeClient.preloadTexture(References.TEXTURE_PATH + "gui/computer.png");
		MinecraftForgeClient.preloadTexture(References.TEXTURE_PATH + "gui/metalforge.png");
		MinecraftForgeClient.preloadTexture(References.TEXTURE_PATH + "gui/analyzer.png");
		renderId = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(renderId, this);

	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
		GL11.glRotatef(-90F, 0F, 1F, 0F);
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		TileEntity te = null;
		switch(metadata) {
			case 0:
				te = tec;
				break;
			case 1:
				te = temf;
				break;
			case 3:
				te = tep;
				break;
		}
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		return true;
	}

	@Override
	public boolean shouldRender3DInInventory() {
		return true;
	}

	@Override
	public int getRenderId() {
		return renderId;
	}
}
