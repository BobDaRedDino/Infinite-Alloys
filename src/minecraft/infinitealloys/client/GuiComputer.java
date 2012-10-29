package infinitealloys.client;

import java.lang.Character;
import java.util.ArrayList;
import infinitealloys.ContainerMachine;
import infinitealloys.IAValues;
import infinitealloys.TileEntityComputer;
import infinitealloys.TileEntityMachine;
import infinitealloys.handlers.PacketHandler;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import cpw.mods.fml.common.network.PacketDispatcher;
import net.minecraft.client.Minecraft;
import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiTextField;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.Vec3;
import net.minecraft.src.World;

public class GuiComputer extends GuiMachine {

	public TileEntityComputer tec;
	private ArrayList<GuiMachineButton> machineButtons = new ArrayList<GuiMachineButton>();
	private GuiTextField xInput, yInput, zInput;
	private GuiButton addMachine;

	public GuiComputer(InventoryPlayer inventoryPlayer, TileEntityComputer tileEntity) {
		super(tileEntity, new ContainerMachine(inventoryPlayer, tileEntity));
		xSize = 176;
		ySize = 176;
		tec = tileEntity;
	}

	@Override
	public void initGui() {
		super.initGui();
		controlList.add(addMachine = new GuiButton(0, width / 2 + 44, height / 2 - 83, 32, 20, "Add"));
		xInput = new GuiTextField(mc.fontRenderer, width / 2 - 80, height / 2 - 81, 30, 16);
		yInput = new GuiTextField(mc.fontRenderer, width / 2 - 38, height / 2 - 81, 30, 16);
		zInput = new GuiTextField(mc.fontRenderer, width / 2 + 4, height / 2 - 81, 30, 16);
		xInput.setText("X");
		yInput.setText("Y");
		zInput.setText("Z");
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTick) {
		super.drawScreen(mouseX, mouseY, partialTick);
		xInput.drawTextBox();
		yInput.drawTextBox();
		zInput.drawTextBox();
		machineButtons.clear();
		for(int i = 0; i < tec.networkCoords.size(); i++) {
			Vec3 coords = tec.networkCoords.get(i);
			machineButtons.add(new GuiMachineButton(width / 2 - 73 + i % 5 * 24, height / 2 - 60 + i / 5 * 24, (int)coords.xCoord, (int)coords.yCoord, (int)coords.zCoord));
			machineButtons.get(i).drawButton(mc, mouseX, mouseY);
		}
		boolean full = machineButtons.size() < tec.networkCapacity;
		addMachine.enabled = full;
		xInput.func_82265_c(full);
		yInput.func_82265_c(full);
		zInput.func_82265_c(full);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		int k = mc.renderEngine.getTexture(IAValues.TEXTURE_PATH + "guicomputer.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(k);
		int left = (width - xSize) / 2;
		int top = (height - ySize) / 2;
		drawTexturedModalRect(left, top, 0, 0, xSize, ySize);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		for(GuiMachineButton button : machineButtons) {
			if(button.mousePressed(mouseX, mouseY)) {
				World world = Minecraft.getMinecraft().theWorld;
				EntityPlayer player = Minecraft.getMinecraft().thePlayer;
				int x = button.blockX;
				int y = button.blockY;
				int z = button.blockZ;
				Block.blocksList[world.getBlockId(x, y, z)].onBlockActivated(world, x, y, z, player, 0, 0, 0, 0);
				PacketDispatcher.sendPacketToServer(PacketHandler.getComputerPacketOpenGui(x, y, z));
				return;
			}
		}
		xInput.mouseClicked(mouseX, mouseY, mouseButton);
		yInput.mouseClicked(mouseX, mouseY, mouseButton);
		zInput.mouseClicked(mouseX, mouseY, mouseButton);

		if(xInput.isFocused() && xInput.getText().equals("X"))
			xInput.setText("");
		else if(xInput.getText().equals(""))
			xInput.setText("X");

		if(yInput.isFocused() && yInput.getText().equals("Y"))
			yInput.setText("");
		else if(yInput.getText().equals(""))
			yInput.setText("Y");

		if(zInput.isFocused() && zInput.getText().equals("Z"))
			zInput.setText("");
		else if(zInput.getText().equals(""))
			zInput.setText("Z");

	}

	@Override
	protected void keyTyped(char key, int eventKey) {
		super.keyTyped(key, eventKey);
		if(!Character.isDigit(key) && eventKey != Keyboard.KEY_BACK)
			return;
		xInput.textboxKeyTyped(key, eventKey);
		yInput.textboxKeyTyped(key, eventKey);
		zInput.textboxKeyTyped(key, eventKey);
	}

	@Override
	public void actionPerformed(GuiButton button) {
		if(button.id == 0) {
			try {
				int x = new Integer(xInput.getText());
				int y = new Integer(yInput.getText());
				int z = new Integer(zInput.getText());
				tec.addMachine(mc.thePlayer, x, y, z);
				PacketDispatcher.sendPacketToServer(PacketHandler.getComputerPacketAddMachine(tec.xCoord, tec.yCoord, tec.zCoord, x, y, z));
			}
			catch(NumberFormatException e) {
			}
		}
	}
}
