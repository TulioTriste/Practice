package me.sebaarkadia.practice.runnables;

import org.bukkit.inventory.meta.ItemMeta;
import java.util.Arrays;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.sebaarkadia.practice.Practice;
import me.sebaarkadia.practice.manager.managers.InventoryManager;

public class UpdateInventory extends BukkitRunnable
{
    public void run() {
        for (final Player player : InventoryManager.guiUnranked) {
            if (player.getOpenInventory().getTopInventory() != null && player.getOpenInventory().getTopInventory().getTitle().startsWith("§8Unranked Queue")) {
                final int NoDebuff1 = (Practice.getInstance().getManagerHandler().getQueueManager().getQueuedForUnrankedQueue("NoDebuff") != null) ? 1 : 0;
                final int Debuff1 = (Practice.getInstance().getManagerHandler().getQueueManager().getQueuedForUnrankedQueue("Debuff") != null) ? 1 : 0;
                final int Soup1 = (Practice.getInstance().getManagerHandler().getQueueManager().getQueuedForUnrankedQueue("Soup") != null) ? 1 : 0;
                final int anvil1 = (Practice.getInstance().getManagerHandler().getQueueManager().getQueuedForUnrankedQueue("Sumo") != null) ? 1 : 0;
                final int Archer1 = (Practice.getInstance().getManagerHandler().getQueueManager().getQueuedForUnrankedQueue("Archer") != null) ? 1 : 0;
                final int builduhc1 = (Practice.getInstance().getManagerHandler().getQueueManager().getQueuedForUnrankedQueue("BuildUHC") != null) ? 1 : 0;
                final int combo1 = (Practice.getInstance().getManagerHandler().getQueueManager().getQueuedForUnrankedQueue("Combo") != null) ? 1 : 0;
                final int axe1 = (Practice.getInstance().getManagerHandler().getQueueManager().getQueuedForUnrankedQueue("Axe") != null) ? 1 : 0;
                final int NoDebuff2 = Practice.getInstance().getManagerHandler().getDuelManager().getUnRankedDuelsFromKit("NoDebuff");
                final int Debuff2 = Practice.getInstance().getManagerHandler().getDuelManager().getUnRankedDuelsFromKit("Debuff");
                final int soup2 = Practice.getInstance().getManagerHandler().getDuelManager().getUnRankedDuelsFromKit("Soup");
                final int anvil2 = Practice.getInstance().getManagerHandler().getDuelManager().getUnRankedDuelsFromKit("Sumo");
                final int Archer2 = Practice.getInstance().getManagerHandler().getDuelManager().getUnRankedDuelsFromKit("Archer");
                final int builduhc2 = Practice.getInstance().getManagerHandler().getDuelManager().getUnRankedDuelsFromKit("BuildUHC");
                final int combo2 = Practice.getInstance().getManagerHandler().getDuelManager().getUnRankedDuelsFromKit("Combo");
                final int axe2 = Practice.getInstance().getManagerHandler().getDuelManager().getUnRankedDuelsFromKit("Axe");
                final ItemStack item5 = new ItemStack(Material.POTION, (NoDebuff2 > 64) ? 64 : ((NoDebuff2 == 0) ? 1 : NoDebuff2), (short)8229);
                final ItemMeta itemm5 = item5.getItemMeta();
                itemm5.setDisplayName("§6NoDebuff");
                itemm5.setLore(Arrays.asList(ChatColor.RESET + "", "§eIn Fight§7: §f" + ChatColor.WHITE + NoDebuff2, "§eIn Queue§7: §f" + ChatColor.WHITE + NoDebuff1, ChatColor.RESET + ""));
                item5.setItemMeta(itemm5);
                final ItemStack debuff = new ItemStack(Material.POTION, (Debuff2 > 64) ? 64 : ((Debuff2 == 0) ? 1 : Debuff2), (short)8228);
                final ItemMeta debuffd = debuff.getItemMeta();
                debuffd.setDisplayName("§6Debuff");
                debuffd.setLore(Arrays.asList(ChatColor.RESET + "", "§eIn Fight§7: §f" + ChatColor.WHITE + Debuff2, "§eIn Queue§7: §f" + ChatColor.WHITE + Debuff1, ChatColor.RESET + ""));
                debuff.setItemMeta(debuffd);
                final ItemStack soup3 = new ItemStack(Material.MUSHROOM_SOUP, (soup2 > 64) ? 64 : ((soup2 == 0) ? 1 : soup2));
                final ItemMeta soupd = soup3.getItemMeta();
                soupd.setDisplayName("§6Soup");
                soupd.setLore(Arrays.asList(ChatColor.RESET + "", "§eIn Fight§7: §f" + ChatColor.WHITE + soup2, "§eIn Queue§7: §f" + ChatColor.WHITE + Soup1, ChatColor.RESET + ""));
                soup3.setItemMeta(soupd);
                final ItemStack anvil3 = new ItemStack(Material.ANVIL, (anvil2 > 64) ? 64 : ((anvil2 == 0) ? 1 : anvil2));
                final ItemMeta anvild = anvil3.getItemMeta();
                anvild.setDisplayName("§6Sumo");
                anvild.setLore(Arrays.asList(ChatColor.RESET + "", "§eIn Fight§7: §f" + ChatColor.WHITE + anvil2, "§eIn Queue§7: §f" + ChatColor.WHITE + anvil1, ChatColor.RESET + ""));
                anvil3.setItemMeta(anvild);
                final ItemStack archer = new ItemStack(Material.BOW, (Archer2 > 64) ? 64 : ((Archer2 == 0) ? 1 : Archer2));
                final ItemMeta archerd = archer.getItemMeta();
                archerd.setDisplayName("§6Archer");
                archerd.setLore(Arrays.asList(ChatColor.RESET + "", "§eIn Fight§7: §f" + ChatColor.WHITE + Archer2, "§eIn Queue§7: §f" + ChatColor.WHITE + Archer1, ChatColor.RESET + ""));
                archer.setItemMeta(archerd);
                final ItemStack builduhc3 = new ItemStack(Material.LAVA_BUCKET, (builduhc2 > 64) ? 64 : ((builduhc2 == 0) ? 1 : builduhc2));
                final ItemMeta builduhcd = builduhc3.getItemMeta();
                builduhcd.setDisplayName("§6BuildUHC");
                builduhcd.setLore(Arrays.asList(ChatColor.RESET + "", "§eIn Fight§7: §f" + ChatColor.WHITE + builduhc2, "§eIn Queue§7: §f" + ChatColor.WHITE + builduhc1, ChatColor.RESET + ""));
                builduhc3.setItemMeta(builduhcd);
                final ItemStack combo3 = new ItemStack(Material.RAW_FISH, (combo2 > 64) ? 64 : ((combo2 == 0) ? 1 : combo2), (short)3);
                final ItemMeta combod = combo3.getItemMeta();
                combod.setDisplayName("§6Combo");
                combod.setLore(Arrays.asList(ChatColor.RESET + "", "§eIn Fight§7: §f" + ChatColor.WHITE + combo2, "§eIn Queue§7: §f" + ChatColor.WHITE + combo1, ChatColor.RESET + ""));
                combo3.setItemMeta(combod);
                final ItemStack axe3 = new ItemStack(Material.IRON_AXE, (axe2 > 64) ? 64 : ((axe2 == 0) ? 1 : axe2));
                final ItemMeta axed = axe3.getItemMeta();
                axed.setDisplayName("§6Axe");
                axed.setLore(Arrays.asList(ChatColor.RESET + "", "§eIn Fight§7: §f" + ChatColor.WHITE + axe2, "§eIn Queue§7: §f" + ChatColor.WHITE + axe1, ChatColor.RESET + ""));
                axe3.setItemMeta(axed);
                player.getOpenInventory().getTopInventory().setItem(0, item5);
                player.getOpenInventory().getTopInventory().setItem(1, debuff);
                player.getOpenInventory().getTopInventory().setItem(2, soup3);
                player.getOpenInventory().getTopInventory().setItem(3, anvil3);
                player.getOpenInventory().getTopInventory().setItem(4, archer);
                player.getOpenInventory().getTopInventory().setItem(5, combo3);
                player.getOpenInventory().getTopInventory().setItem(6, axe3);
            }
            else {
                InventoryManager.guiUnranked.remove(player);
            }
        }
    }
}
