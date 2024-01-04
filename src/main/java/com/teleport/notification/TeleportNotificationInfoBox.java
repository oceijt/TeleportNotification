package com.teleport.notification;

import java.awt.Color;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.infobox.InfoBox;
import net.runelite.client.ui.overlay.infobox.InfoBoxPriority;

public class TeleportNotificationInfoBox extends InfoBox {

    private final TeleportNotificationPlugin plugin;
    private final TeleportNotificationConfig config;

    @Inject
    public TeleportNotificationInfoBox(Client client, TeleportNotificationPlugin plugin, TeleportNotificationConfig config)
    {
        super(null, plugin);
        this.plugin = plugin;
        this.config = config;
        setPriority(InfoBoxPriority.MED);
    }

    @Override
    public String getText() {
        String str;
        if (config.timerUnit() == TimerUnit.TICKS)
        {
            str = String.valueOf(plugin.afkTimeRemaining);
        }
        else if (config.timerUnit() == TimerUnit.DECIMALS)
        {
            str = to_mmss_precise_short(plugin.afkTimeRemaining);
        }
        else
        {
            str = to_mmss(plugin.afkTimeRemaining);
        }
        return str;
    }

    @Override
    public Color getTextColor() {
        if (config.warningTicks() > plugin.afkTimeRemaining) {
            return Color.RED;
        }
        return Color.WHITE;
    }

    private static String to_mmss(int ticks)
    {
        int m = ticks / 100;
        int s = (ticks - m * 100) * 6 / 10;
        return m + (s < 10 ? ":0" : ":") + s;
    }

    private static String to_mmss_precise_short(int ticks)
    {
        int min = ticks / 100;
        int tmp = (ticks - min * 100) * 6;
        int sec = tmp / 10;
        int sec_tenth = tmp - sec * 10;
        return min + (sec < 10 ? ":0" : ":") + sec + "." +
                sec_tenth;
    }
}
