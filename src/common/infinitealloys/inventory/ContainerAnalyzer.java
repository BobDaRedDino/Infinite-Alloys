package infinitealloys.inventory;

import infinitealloys.InfiniteAlloys;
import infinitealloys.tile.TileEntityAnalyzer;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Slot;

public class ContainerAnalyzer extends ContainerMachine {

	public TileEntityAnalyzer inventory;

	public ContainerAnalyzer(InventoryPlayer inventoryPlayer, TileEntityAnalyzer tileEntity) {
		super(tileEntity);
		inventory = tileEntity;
		addSlotToContainer(new SlotAnalyzer(inventory, 0, 28, 58));
		addSlotToContainer(new SlotAnalyzer(inventory, 1, 172, 58));
		addSlotToContainer(new SlotAnalyzer(inventory, 2, 172, 33));
		addSlotToContainer(new SlotUpgrade(inventory, 3, 172, 8));
		for(int y = 0; y < 3; y++)
			for(int x = 0; x < 9; x++)
				addSlotToContainer(new Slot(inventoryPlayer, x + y * 9 + 9, 20 + x * 18, 84 + y * 18));
		for(int x = 0; x < 9; x++)
			addSlotToContainer(new Slot(inventoryPlayer, x, 20 + x * 18, 142));
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slot) {
		ItemStack itemstack = null;
		Slot stackInSlot = (Slot)this.inventorySlots.get(slot);
		if(stackInSlot != null && stackInSlot.getHasStack()) {
			ItemStack stackInSlotCopy = stackInSlot.getStack();
			itemstack = stackInSlotCopy.copy();
			if(slot < 3) {
				if(!mergeItemStack(stackInSlotCopy, 4, 39, false))
					return null;
			}
			if(slot > 3) {
				if(stackInSlotCopy.itemID == InfiniteAlloys.alloyIngot.shiftedIndex) {
					if(!mergeItemStack(stackInSlotCopy, 0, 1, false))
						return null;
				}
				else if(inventory.isUpgradeValid(stackInSlotCopy)) {
					if(!mergeItemStack(stackInSlotCopy, 3, 4, false))
						return null;
				}
				else if(slot > 3 && slot < 30) {
					if(!mergeItemStack(stackInSlotCopy, 30, 39, false))
						return null;
				}
				else if(slot >= 30)
					if(!mergeItemStack(stackInSlotCopy, 3, 30, false))
						return null;
			}
			if(stackInSlotCopy.stackSize == 0)
				stackInSlot.putStack((ItemStack)null);
			else
				stackInSlot.onSlotChanged();
			if(stackInSlotCopy.stackSize == itemstack.stackSize)
				return null;
			stackInSlot.onPickupFromSlot(player, stackInSlotCopy);
		}
		return itemstack;
	}
}