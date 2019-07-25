package me.sebaarkadia.practice.scoreboard.sidebar;

import java.text.DecimalFormat;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;

import me.sebaarkadia.practice.Practice;
import me.sebaarkadia.practice.player.PlayerState;
import me.sebaarkadia.practice.scoreboard.SidebarEntry;
import me.sebaarkadia.practice.scoreboard.SidebarProvider;
import me.sebaarkadia.practice.settings.Settings;

public class EditingScoreboardProvider extends SidebarProvider
{
    private Practice plugin;
    
    public EditingScoreboardProvider(final Practice plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getTitle(final Player paramPlayer) {
        return EditingScoreboardProvider.SCOREBOARD_TITLE;
    }
    
    @Override
    public List<SidebarEntry> getLines(final Player player) {
        final List<SidebarEntry> lines = new ArrayList<SidebarEntry>();
        final Settings settings = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player.getUniqueId()).getSettings();
            lines.add(new SidebarEntry("§8§m------------------------"));
            if (PlayerState.EDITING != null) {
                lines.add(new SidebarEntry(String.valueOf(String.valueOf(settings.isPublicChat() ? "§7Use the anvil to save," : "§7Usa el yunque para guardar,"))));
                lines.add(new SidebarEntry(String.valueOf(String.valueOf(settings.isPublicChat() ? "§7load, and delete your" : "§7cargar, y borrar tus"))));
                lines.add(new SidebarEntry(String.valueOf(String.valueOf(settings.isPublicChat() ? "§7kits." : "§7kits."))));
                lines.add(new SidebarEntry(String.valueOf(String.valueOf(settings.isPublicChat() ? "§r" : "§r"))));
                lines.add(new SidebarEntry(String.valueOf(String.valueOf(settings.isPublicChat() ? "§7Once you are finished" : "§7Una vez hayas terminado"))));
                lines.add(new SidebarEntry(String.valueOf(String.valueOf(settings.isPublicChat() ? "§7with your kits, use" : "§7con tus kits, usa"))));
                lines.add(new SidebarEntry(String.valueOf(String.valueOf(settings.isPublicChat() ? "§7the door to return to" : "§7la puerta para volver al"))));
                lines.add(new SidebarEntry(String.valueOf(String.valueOf(settings.isPublicChat() ? "§7spawn." : "§7spawn"))));
            }
            if (player.isOp()) {
                final DecimalFormat df = new DecimalFormat("00.00");
                lines.add(new SidebarEntry("§r"));
                lines.add(new SidebarEntry("§f§oTPS§7: §r" + df.format(Practice.getInstance().getTps())));
            }
            else {
                lines.add(new SidebarEntry("§r"));
                lines.add(new SidebarEntry("§f§o" + Practice.getInstance().getConfig().getString("serverip")));
            }
            lines.add(new SidebarEntry("§8§m------------------------"));
            return lines;
        }
}
