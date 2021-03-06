package infinitealloys.client.gui;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;

import infinitealloys.item.IAItems;
import infinitealloys.tile.TEEMetalForge;
import infinitealloys.util.Consts;
import infinitealloys.util.EnumAlloy;
import infinitealloys.util.EnumMetal;
import infinitealloys.util.EnumUpgrade;
import infinitealloys.util.Funcs;

public final class GuiMetalForge extends GuiElectric {

  private final TEEMetalForge temf;

  public GuiMetalForge(InventoryPlayer inventoryPlayer, TEEMetalForge tileEntity) {
    super(176, 216, inventoryPlayer, tileEntity);
    temf = tileEntity;
    progressBar.setLocation(31, 14);
    networkIcon = new Point(9, 15);
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY) {
    super.drawGuiContainerBackgroundLayer(partialTick, mouseX, mouseY);

    // If there is an alloy available and selected..
    if (temf.recipeAlloyID >= 0) {
      // Render the item icon for the alloy.
      itemRender.renderItemIntoGUI(new ItemStack(IAItems.alloyIngot, 1, temf.recipeAlloyID + 1),
                                   topLeft.x + 40, topLeft.y + 52);
    }

    // For each metal...
    for (int i = 0; i < Consts.METAL_COUNT; i++) {
      // Render the ingot for the metal into the GUI
      itemRender.renderItemIntoGUI(new ItemStack(IAItems.ingot, 1, i),
                                   topLeft.x + i % 4 * 18 + 66, topLeft.y + i / 4 * 18 + 43);
    }

    GL11.glDisable(GL11.GL_DEPTH_TEST);
    GL11.glDisable(GL11.GL_LIGHTING);
    GL11.glColor4f(1F, 1F, 1F, 1F);
    for (int i = 0; i < Consts.METAL_COUNT; i++) {
      fontRendererObj.drawStringWithShadow(
          (temf.recipeAlloyID < 0 ? 0 : EnumAlloy.getMetalAmt(temf.recipeAlloyID, i)) + "",
          topLeft.x + i % 4 * 18 + 77, topLeft.y + i / 4 * 18 + 52, 0xffffff);
    }
  }

  @Override
  public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    GL11.glDisable(GL11.GL_DEPTH_TEST);
    GL11.glDisable(GL11.GL_LIGHTING);

    // For each metal, if help is disabled and the mouse is over it, draw the metal's name
    if (!helpEnabled) {
      for (int i = 0; i < Consts.METAL_COUNT; i++) {
        if (Funcs.pointInZone(mouseX, mouseY,
                              topLeft.x + i % 4 * 18 + 65, topLeft.y + i / 4 * 18 + 42, 18, 18)) {
          new GuiTextBox(mouseX - topLeft.x, mouseY - topLeft.y, new ColoredText(
              Funcs.getLoc("metal." + EnumMetal.values()[i].name + ".name"), 0xffffff)).draw();
        }
      }
    }

    GL11.glEnable(GL11.GL_LIGHTING);
    GL11.glEnable(GL11.GL_DEPTH_TEST);
  }

  @Override
  protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
    super.mouseClicked(mouseX, mouseY, mouseButton);

    // If the preset selection slot was clicked, adjust its value accordingly
    if (Funcs.pointInZone(mouseX, mouseY, topLeft.x + 39, topLeft.y + 51, 18, 18)) {
      if (mouseButton == 0) { // Left-click
        // If there is an unlocked alloy above this one, select it
        if (temf.recipeAlloyID + 1 < tem.getUpgradeTier(EnumUpgrade.ALLOY)) {
          temf.recipeAlloyID++;
        }
      } else if (mouseButton == 1) { // Right-click
        // If there is an alloy below this one (it's not the first), select it
        if (temf.recipeAlloyID > 0) {
          temf.recipeAlloyID--;
        }
      }

      temf.syncToServer(); // Sync the new recipe to the server
    }
  }
}
