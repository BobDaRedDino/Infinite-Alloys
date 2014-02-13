package infinitealloys.inventory;

import infinitealloys.tile.TEMEnergyStorage;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerESU extends ContainerMachine {

	public TEMEnergyStorage inventory;

	public ContainerESU(InventoryPlayer inventoryPlayer, TEMEnergyStorage tileEntity) {
		super(tileEntity, 10);
		inventory = tileEntity;

		for(int y = 0; y < 3; y++)
			for(int x = 0; x < 3; x++)
				addSlotToContainer(new SlotMachine(inventory, inventory.getID(), x + y * 3, 13 + x * 18, 22 + y * 18));

		initSlots(inventoryPlayer, 27, 94, 185, 40);
	}
}