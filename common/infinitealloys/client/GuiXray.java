package infinitealloys.client;

import infinitealloys.core.InfiniteAlloys;
import infinitealloys.handlers.PacketHandler;
import infinitealloys.inventory.ContainerXray;
import infinitealloys.tile.TileEntityXray;
import infinitealloys.util.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import cpw.mods.fml.common.network.PacketDispatcher;

public class GuiXray extends GuiMachine {

	private TileEntityXray tex;

	/** The scroll bar (width is for the scrolling block) */
	private final Rectangle SCROLL_BAR = new Rectangle(172, 49, 12, 96);

	/** The number of the first displayed line of blocks. Min is 0, max is num of rows - number on screen (5) */
	private int scrollPos = 0;
	private GuiBlockButton[] blockButtons = new GuiBlockButton[0];
	private GuiButton searchButton;
	/** TileEntityXray.searchingClient, used to checking if searching just finished */
	private boolean wasSearching;

	public GuiXray(InventoryPlayer inventoryPlayer, TileEntityXray tileEntity) {
		super(196, 238, tileEntity, new ContainerXray(inventoryPlayer, tileEntity), "xray");
		tex = tileEntity;
		progressBar.setLocation(54, 5);
	}

	@Override
	public void initGui() {
		super.initGui();
		buttonList.add(searchButton = new GuiButton(1, width / 2 - 30, height / 2 - 92, 80, 20, "Search"));
		setButtons();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		searchButton.enabled = tex.inventoryStacks[0] != null;
		if(blockButtons.length <= 20)
			drawTexturedModalRect(SCROLL_BAR.x, SCROLL_BAR.y, SCROLL_OFF.x, SCROLL_OFF.y, SCROLL_OFF.width, SCROLL_OFF.height);
		else
			drawTexturedModalRect(SCROLL_BAR.x, SCROLL_BAR.y + (int)((float)(SCROLL_BAR.height - 15) / (float)(blockButtons.length / 4 - 4) * scrollPos),
					SCROLL_ON.x, SCROLL_ON.y, SCROLL_ON.width, SCROLL_ON.height);
		if(wasSearching && !tex.searchingClient)
			setButtons();
		wasSearching = tex.searchingClient;
		for(int i = scrollPos * 4; i < blockButtons.length && i < scrollPos * 4 + 20; i++)
			blockButtons[i].drawButton();
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		if(mouseButton == 0) {
			for(int i = 0; i < blockButtons.length; i++) {
				if(blockButtons[i].mousePressed(mouseX - topLeft.x, mouseY - topLeft.y)) {
					if(tex.selectedButton >= 0)
						blockButtons[tex.selectedButton].activated = false;
					if(tex.selectedButton != i) {
						tex.selectedButton = i;
						blockButtons[i].activated = true;
						InfiniteAlloys.proxy.gfxHandler.xrayBlocks.clear();
						for(Point block : tex.getDetectedBlocks()) {
							if(block.y == blockButtons[i].getYValue()) {
								block.x += tex.xCoord;
								block.z += tex.zCoord;
								InfiniteAlloys.proxy.gfxHandler.xrayBlocks.add(block);
							}
						}
					}
					else {
						tex.selectedButton = -1;
						InfiniteAlloys.proxy.gfxHandler.xrayBlocks.clear();
					}
				}
			}
			setButtons();
			if(mouseInZone(mouseX, mouseY, topLeft.x + 172, topLeft.y + 40, 14, 8))
				scroll(-1);
			else if(mouseInZone(mouseX, mouseY, topLeft.x + 172, topLeft.y + 147, 14, 8))
				scroll(1);
		}
	}

	@Override
	public void handleMouseInput() {
		super.handleMouseInput();
		int scrollAmt = Mouse.getEventDWheel();
		// Scroll one line up or down based on the movement, if the list is long enough to need scrolling
		if(blockButtons.length > 20)
			scroll(scrollAmt > 0 ? -1 : scrollAmt < 0 ? 1 : 0);
	}

	private void setButtons() {
		if(tex.inventoryStacks[0] == null || tex.searchingClient)
			blockButtons = new GuiBlockButton[0];
		else {
			int[] blockCounts = new int[tem.yCoord];
			ArrayList<Integer> levels = new ArrayList<Integer>();
			// Go through each detected block
			for(Point block : tex.getDetectedBlocks())
				// For each block if there hasn't been a block for that y-level yet, at that y to the list
				if(blockCounts[block.y]++ == 0)
					levels.add(block.y);
			blockButtons = new GuiBlockButton[levels.size()];
			for(int i = 0; i < blockButtons.length; i++)
				blockButtons[i] = new GuiBlockButton(mc, itemRenderer, i % 4 * 40 + 9, (i / 4 - scrollPos) * 20 + 50, tex.inventoryStacks[0].itemID, blockCounts[levels.get(i)],
						tex.inventoryStacks[0].getItemDamage(), levels.get(i));
			if(tex.selectedButton != -1)
				blockButtons[tex.selectedButton].activated = true;
		}
	}

	/** Scroll the block list the specified amount of lines. Positive is down, negative is up. */
	private void scroll(int lines) {
		if(lines > 0 && scrollPos < blockButtons.length / 4 - 4 || lines < 0 && scrollPos > 0)
			scrollPos += lines;
		setButtons();
	}

	@Override
	public void actionPerformed(GuiButton button) {
		super.actionPerformed(button);
		if(button.id == 1) {
			tex.selectedButton = -1;
			InfiniteAlloys.proxy.gfxHandler.xrayBlocks.clear();
			tex.shouldSearch = true;
			PacketDispatcher.sendPacketToServer(PacketHandler.getPacketSearch(tex.xCoord, tex.yCoord, tex.zCoord));
		}
	}
}