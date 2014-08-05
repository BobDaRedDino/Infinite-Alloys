package infinitealloys.item;

import infinitealloys.util.Consts;
import infinitealloys.util.EnumMachine;
import infinitealloys.util.Funcs;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBlockMachine extends ItemBlock {

	public ItemBlockMachine(Block block) {
		super(block);
		setHasSubtypes(true);
	}

	@Override
	public int getMetadata(int i) {
		return i;
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack) {
		if(itemstack.getItemDamage() < Consts.MACHINE_COUNT)
			return "tile.IA" + EnumMachine.values()[itemstack.getItemDamage()].getName();
		return super.getUnlocalizedName(itemstack);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean b) {
		// If the item has stored data, display it
		if(itemstack.hasTagCompound())
			for(String field : EnumMachine.values()[itemstack.getItemDamage()].getPersistentFields())
				list.add(Funcs.getLoc("machine.fields." + field) + ": " + itemstack.getTagCompound().getInteger(field));
	}
}
