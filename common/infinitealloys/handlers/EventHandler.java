package infinitealloys.handlers;

import infinitealloys.block.Blocks;
import infinitealloys.core.InfiniteAlloys;
import infinitealloys.core.WorldData;
import infinitealloys.util.Consts;
import infinitealloys.util.Funcs;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.event.world.WorldEvent.Unload;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ICraftingHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class EventHandler implements ICraftingHandler {

	private String world;

	@ForgeSubscribe
	public void onWorldLoad(Load event) {
		if(Funcs.isClient())
			return;
		world = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(0).getChunkSaveLocation().getPath();
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(new FileInputStream(world + "/WorldData.dat"));
			InfiniteAlloys.instance.worldData = (WorldData)ois.readObject();
			System.out.println("Successfully loaded IA alloys");
		}
		catch(IOException e) {
			if(InfiniteAlloys.instance.worldData == null) {
				Random random = new Random();
				int[] validAlloys = new int[Consts.VALID_ALLOY_COUNT];
				for(int i = 0; i < Consts.VALID_ALLOY_COUNT; i++) {
					int metalCount = Consts.METAL_COUNT;
					byte[] alloyDigits = new byte[metalCount];
					for(int j = 0; j < metalCount; j++) {
						int min = Funcs.intAtPos(Consts.ALLOY_RADIX, metalCount, Consts.VALID_ALLOY_MINS[i], j);
						int max = Funcs.intAtPos(Consts.ALLOY_RADIX, metalCount, Consts.VALID_ALLOY_MAXES[i], j);
						alloyDigits[j] = (byte)(min + (max == min ? 0 : random.nextInt(max - min)));
					}
					String alloy = "";
					for(int digit : alloyDigits)
						alloy = alloy + digit;
					validAlloys[i] = new Integer(alloy);
					if(Loader.isModLoaded("mcp"))
						System.out.println("SPOILER ALERT! Alloy " + i + ": " + validAlloys[i]);
				}
				InfiniteAlloys.instance.worldData = new WorldData(validAlloys);
				System.out.println("Successfully generated IA alloys");
			}
		}
		catch(Exception e) {
			System.out.println("Error while deserializing Infinite Alloys world data");
			e.printStackTrace();
		}
		finally {
			try {
				if(ois != null)
					ois.close();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	@ForgeSubscribe
	public void onWorldUnload(Unload event) {
		if(InfiniteAlloys.instance.worldData == null || Funcs.isClient())
			return;
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(world + "/WorldData.dat"));
			oos.writeObject(InfiniteAlloys.instance.worldData);
		}
		catch(Exception e) {
			System.out.println("Error while serializing Infinite Alloys world data");
			e.printStackTrace();
		}
		finally {
			try {
				if(oos != null)
					oos.close();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		InfiniteAlloys.instance.worldData = null;
	}

	@ForgeSubscribe
	public void onEntityJoinWorld(EntityJoinWorldEvent e) {
		if(!(e.entity instanceof EntityPlayer) || Funcs.isClient())
			return;
		PacketDispatcher.sendPacketToPlayer(PacketHandler.getWorldDataPacket(), (Player)e.entity);
	}

	@Override
	public void onCrafting(EntityPlayer player, ItemStack item, IInventory craftMatrix) {
		if(item.itemID == Blocks.machine.blockID && item.getItemDamage() == 1) {
			System.out.println("Adding stat");
			player.addStat(InfiniteAlloys.achievements[0], 1);
		}
	}

	@Override
	public void onSmelting(EntityPlayer player, ItemStack item) {
	}
}