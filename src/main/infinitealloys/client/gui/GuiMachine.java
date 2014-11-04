package infinitealloys.client.gui;

import infinitealloys.block.BlockMachine;
import infinitealloys.client.EnumHelp;
import infinitealloys.item.IAItems;
import infinitealloys.network.MessageOpenGui;
import infinitealloys.tile.TEMComputer;
import infinitealloys.tile.TileEntityElectric;
import infinitealloys.tile.TileEntityMachine;
import infinitealloys.util.Consts;
import infinitealloys.util.Funcs;
import infinitealloys.util.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public abstract class GuiMachine extends GuiContainer {

	// The position for each item in the texture sheet extras.png
	static final Rectangle TAB_LEFT_OFF = new Rectangle(0, 0, 24, 24);
	static final Rectangle TAB_LEFT_ON = new Rectangle(24, 0, 28, 24);
	static final Rectangle TAB_RIGHT_OFF = new Rectangle(52, 0, 29, 24);
	static final Rectangle TAB_RIGHT_ON = new Rectangle(81, 0, 28, 24);
	static final Rectangle PROGRESS_BAR = new Rectangle(109, 0, 108, 18);
	static final Rectangle SCROLL_ON = new Rectangle(217, 0, 12, 15);
	static final Rectangle SCROLL_OFF = new Rectangle(229, 0, 12, 15);
	static final Rectangle UP_ARROW = new Rectangle(0, 24, 16, 16);
	static final Rectangle DOWN_ARROW = new Rectangle(16, 24, 16, 16);
	static final Rectangle CHECK = new Rectangle(32, 24, 16, 16);
	static final Rectangle BLOCK_BG_OFF = new Rectangle(48, 24, 36, 18);
	static final Rectangle SELECTED_OVERLAY = new Rectangle(48, 24, 36, 18);
	static final Rectangle BLOCK_BG_ON = new Rectangle(84, 24, 36, 18);
	static final Rectangle NETWORK_ICON = new Rectangle(0, 40, 16, 16);
	static final Rectangle SCROLL_BAR = new Rectangle(172, 51, 12, 96);

	/** The texture resource for the texture item */
	static final ResourceLocation extras = Funcs.getGuiTexture("extras");
	/** The background texture */
	protected ResourceLocation background;

	/** Coordinates of the top-left corner of the GUI */
	protected java.awt.Point topLeft = new java.awt.Point();

	protected TileEntityMachine tem;
	protected infinitealloys.util.Point controllingComputer = new infinitealloys.util.Point();
	protected GuiMachineTab computerTab;
	protected final List<GuiMachineTab> machineTabs = new ArrayList<GuiMachineTab>();
	/** Coordinates of the network icon, which shows network statuses when hovered over */
	protected java.awt.Point networkIcon;
	/** When help is enabled, slots get a colored outline and a mouse-over description */
	protected boolean helpEnabled;

	public GuiMachine(int xSize, int ySize, InventoryPlayer inventoryPlayer, TileEntityMachine tileEntity) {
		super(tileEntity.getEnumMachine().getContainer(inventoryPlayer, tileEntity));
		this.xSize = xSize;
		this.ySize = ySize;
		tem = tileEntity;
		background = Funcs.getGuiTexture(tem.getEnumMachine().getName());
	}

	@Override
	public void initGui() {
		super.initGui();
		topLeft.setLocation((width - xSize) / 2, (height - ySize) / 2);
		buttonList.add(new GuiButton(0, width - 20, 0, 20, 20, "?")); // The button to enable/disable help
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTick) {
		super.drawScreen(mouseX, mouseY, partialTick);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		// Draw the upgrade list if the mouse is over the upgrade slot and help is disabled
		Slot slot = inventorySlots.getSlot(tem.upgradeSlotIndex);
		if(!helpEnabled && Funcs.mouseInZone(mouseX, mouseY, slot.xDisplayPosition + topLeft.x, slot.yDisplayPosition + topLeft.y, 16, 16)) {
			List<ColoredLine> lines = new ArrayList<ColoredLine>();
			lines.add(new ColoredLine(Funcs.getLoc("general.upgrades"), 0xffffff));

			for(int i = 0; i < Consts.UPGRADE_TYPE_COUNT; i++)
				if(tem.getUpgradeTier(i) > 0)
					lines.add(new ColoredLine(Funcs.getLoc("item.ia" + IAItems.upgrades[i].name + tem.getUpgradeTier(i) + ".name"), 0xaaaaaa));

			drawTextBox(mouseX, mouseY, lines.toArray(new ColoredLine[lines.size()]));
		}

		// Draw the network info if the mouse is over the network icon and help is disabled
		if(!helpEnabled && networkIcon != null && Funcs.mouseInZone(mouseX, mouseY, topLeft.x + networkIcon.x, topLeft.y + networkIcon.y, NETWORK_ICON.width, NETWORK_ICON.height))
			// Draw a text box with a line for each network show its status and information
			drawTextBox(mouseX, mouseY, getNetworkStatuses());

		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_LIGHTING);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY) {
		Funcs.bindTexture(background);
		drawTexturedModalRect(topLeft.x, topLeft.y, 0, 0, xSize, ySize);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		Funcs.bindTexture(extras);
		GL11.glPushMatrix();

		// Draw the network icon if this GUI has one
		if(networkIcon != null)
			Funcs.drawTexturedModalRect(this, networkIcon.x, networkIcon.y, NETWORK_ICON); // Draw the network icon

		// Draw the tabs of other machines on the network if this machine is connected to a computer
		machineTabs.clear();
		if(tem.computerHost != null) {
			TEMComputer tec = (TEMComputer)Funcs.getTileEntity(mc.theWorld, tem.computerHost);
			computerTab = new GuiMachineTab(mc, itemRender, -24, 6, tec, true, tem.coords().equals(tem.computerHost));
			computerTab.drawButton();
			if(Funcs.mouseInZone(mouseX, mouseY, topLeft.x + computerTab.xPos, topLeft.y + computerTab.yPos, computerTab.width, computerTab.height))
				drawTextBox(mouseX - topLeft.x, mouseY - topLeft.y, new ColoredLine(Funcs.getLoc("tile.ia" + computerTab.tem.getEnumMachine().getName() + ".name"), 0xffffff),
						new ColoredLine(computerTab.tem.coords().toString(), 0xffffff));
			Point[] clients = tec.getClients();
			for(int i = 0; i < clients.length; i++) {
				machineTabs.add(new GuiMachineTab(mc, itemRender, i / 5 * 197 - 24, i % 5 * 25 + 36, (TileEntityElectric)Funcs.getTileEntity(mc.theWorld, clients[i]),
						i / 5 == 0, clients[i].equals(tem.coords())));
				machineTabs.get(i).drawButton();
				if(Funcs.mouseInZone(mouseX, mouseY, topLeft.x + machineTabs.get(i).xPos, topLeft.y + machineTabs.get(i).yPos, machineTabs.get(i).width, machineTabs.get(i).height))
					drawTextBox(mouseX - topLeft.x, mouseY - topLeft.y, new ColoredLine(Funcs.getLoc("tile.ia" + machineTabs.get(i).tem.getEnumMachine().getName() + ".name"), 0xffffff),
							new ColoredLine(machineTabs.get(i).tem.coords().toString(), 0xffffff));
			}
		}

		// Draw the help dialogue and shade the help zone if help is enabled and the mouse is over a help zone
		if(helpEnabled) {
			EnumHelp hoveredZone = null; // The help zone that the mouse is over to render to dialogue later, null if mouse is not over a zone\
			for(EnumHelp help : tem.getEnumMachine().getHelpBoxes()) {
				// Draw zone outline, add alpha to make the rectangles opaque
				drawRect(help.x, help.y, help.x + help.w, help.y + 1, 0xff000000 + help.color); // Top of outline box
				drawRect(help.x, help.y + help.h, help.x + help.w, help.y + help.h - 1, 0xff000000 + help.color); // Bottom of outline box
				drawRect(help.x, help.y, help.x + 1, help.y + help.h - 1, 0xff000000 + help.color); // Left side of outline box
				drawRect(help.x + help.w - 1, help.y, help.x + help.w, help.y + help.h, 0xff000000 + help.color); // Right side of outline box

				// Set hoveredZone to this zone if it hasn't been set already and the mouse is over this zone
				if(hoveredZone == null && Funcs.mouseInZone(mouseX, mouseY, topLeft.x + help.x, topLeft.y + help.y, help.w, help.h))
					hoveredZone = help;
			}

			if(hoveredZone != null) {
				// Fill in the zone with an smaller 4th hex pair for less alpha
				drawRect(hoveredZone.x, hoveredZone.y, hoveredZone.x + hoveredZone.w, hoveredZone.y + hoveredZone.h, 0x60000000 + hoveredZone.color);

				// Draw text box with help info
				List<ColoredLine> lines = new ArrayList<ColoredLine>();
				lines.add(new ColoredLine(Funcs.getLoc("machineHelp." + hoveredZone.name + ".title"), 0xffffff));
				for(String s : Funcs.getLoc("machineHelp." + hoveredZone.name + ".info").split("/n"))
					lines.add(new ColoredLine(s, 0xaaaaaa));
				drawTextBox(mouseX - topLeft.x, mouseY - topLeft.y, lines.toArray(new ColoredLine[lines.size()]));
			}
		}
		GL11.glPopMatrix();

		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_LIGHTING);
	}

	protected void drawTextBox(int x, int y, ColoredLine... lines) {
		// Set the width of the box to the length of the longest line
		int boxWidth = 0;
		for(ColoredLine line : lines)
			boxWidth = Math.max(boxWidth, fontRendererObj.getStringWidth(line.text));

		// This is from vanilla, I have no idea what it does, other than make it work
		x += 12;
		y -= 12;
		int var9 = 8;
		if(lines.length > 1)
			var9 += 2 + (lines.length - 1) * 10;
		int var10 = -267386864;
		drawGradientRect(x - 3, y - 4, x + boxWidth + 3, y - 3, var10, var10);
		drawGradientRect(x - 3, y + var9 + 3, x + boxWidth + 3, y + var9 + 4, var10, var10);
		drawGradientRect(x - 3, y - 3, x + boxWidth + 3, y + var9 + 3, var10, var10);
		drawGradientRect(x - 4, y - 3, x - 3, y + var9 + 3, var10, var10);
		drawGradientRect(x + boxWidth + 3, y - 3, x + boxWidth + 4, y + var9 + 3, var10, var10);
		int var11 = 1347420415;
		int var12 = (var11 & 16711422) >> 1 | var11 & -16777216;
		drawGradientRect(x - 3, y - 3 + 1, x - 3 + 1, y + var9 + 3 - 1, var11, var12);
		drawGradientRect(x + boxWidth + 2, y - 3 + 1, x + boxWidth + 3, y + var9 + 3 - 1, var11, var12);
		drawGradientRect(x - 3, y - 3, x + boxWidth + 3, y - 3 + 1, var11, var11);
		drawGradientRect(x - 3, y + var9 + 2, x + boxWidth + 3, y + var9 + 3, var12, var12);

		for(int i = 0; i < lines.length; i++)
			fontRendererObj.drawStringWithShadow(lines[i].text, x, y + i * 10 + (i == 0 ? 0 : 2), lines[i].color);
		zLevel = 0F;
		itemRender.zLevel = 0F;
	}

	@Override
	public void actionPerformed(GuiButton button) {
		if(button.id == 0)
			helpEnabled = !helpEnabled;
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		World world = Minecraft.getMinecraft().theWorld;
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;

		// Was the network tab of the controlling computer clicked? Go to that computer
		if(computerTab != null && computerTab.mousePressed(mouseX - topLeft.x, mouseY - topLeft.y)) {
			if(!tem.coords().equals(computerTab.tem.coords())) {
				((BlockMachine)world.getBlock(computerTab.tem.xCoord, computerTab.tem.yCoord, computerTab.tem.zCoord)).openGui(world, player, computerTab.tem);
				Funcs.sendPacketToServer(new MessageOpenGui(computerTab.tem.coords()));
			}
			return;
		}

		// Was the network tab of another machine clicked? Go to that machine
		for(GuiMachineTab tab : machineTabs) {
			if(tab.mousePressed(mouseX - topLeft.x, mouseY - topLeft.y)) {
				if(!tem.coords().equals(tab.tem.coords())) {
					((BlockMachine)world.getBlock(tab.tem.xCoord, tab.tem.yCoord, tab.tem.zCoord)).openGui(world, player, tab.tem);
					Funcs.sendPacketToServer(new MessageOpenGui(tab.tem.coords()));
				}
				return;
			}
		}
	}

	protected abstract ColoredLine[] getNetworkStatuses();

	public static class ColoredLine {
		/** The line's text */
		String text;
		/** The line's hexadecimal color */
		int color;

		protected ColoredLine(String text, int color) {
			this.text = text;
			this.color = color;
		}
	}
}