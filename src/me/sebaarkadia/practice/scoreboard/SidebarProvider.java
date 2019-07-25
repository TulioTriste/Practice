package me.sebaarkadia.practice.scoreboard;

import java.util.List;
import org.bukkit.entity.Player;
import com.google.common.base.Strings;

import me.sebaarkadia.practice.Practice;
import me.sebaarkadia.practice.util.Color;

import org.bukkit.ChatColor;

public abstract class SidebarProvider
{
    public static String SCOREBOARD_TITLE;
    protected static String STRAIGHT_LINE;
    
    static {
        SidebarProvider.SCOREBOARD_TITLE = Color.translate(Practice.getInstance().getConfig().getString("Scoreboard.Title"));
        SidebarProvider.STRAIGHT_LINE = String.valueOf(ChatColor.STRIKETHROUGH.toString()) + Strings.repeat("-", 256).substring(0, 10);
    }
    
    public abstract String getTitle(final Player p0);
    
    public abstract List<SidebarEntry> getLines(final Player p0);
}
