package ca.xera.bungee.queue.bungee.listeners;

import ca.xera.bungee.queue.bungee.XeraBungeeQueue;
import ca.xera.bungee.queue.bungee.utils.ChatUtils;
import ca.xera.bungee.queue.bungee.utils.Config;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.ArrayList;
import java.util.List;

public final class XeraListener implements Listener {
    private final XeraBungeeQueue plugin;

    public XeraListener(XeraBungeeQueue plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPreLogin(PreLoginEvent ple) {
        if (!ple.getConnection().getName().matches(Config.REGEX)) {
            ple.setCancelReason(ChatUtils.parseToComponent(Config.REGEXMESSAGE.replaceAll("%regex%", Config.REGEX)));
            ple.setCancelled(true);
        }
    }

    @EventHandler
    public void onKick(ServerKickEvent event) {
        if (Config.ENABLEKICKMESSAGE)
            event.setKickReasonComponent(ChatUtils.parseToComponent(Config.KICKMESSAGE));
    }

    @EventHandler
    public void onPing(ProxyPingEvent event) {
        ServerPing.Protocol protocol;
        ServerPing.Players players;

        if (Config.CUSTOMPROTOCOLENABLE) {
            ServerPing.Protocol provided = event.getResponse().getVersion();

            provided.setName(ChatUtils.parseToString(Config.CUSTOMPROTOCOL));

            protocol = provided;
        } else {
            protocol = event.getResponse().getVersion();
        }

        if (Config.SERVERPINGINFOENABLE) {
            List<ServerPing.PlayerInfo> info = new ArrayList<>();

            Config.SERVERPINGINFO.forEach(str -> info.add(
                    new ServerPing.PlayerInfo(ChatUtils.parseToString(str),
                            String.valueOf(Config.SERVERPINGINFO.indexOf(str) - 1))));

            players = new ServerPing.Players(Config.QUEUESERVERSLOTS, plugin.getProxy().getOnlineCount(), info.toArray(new ServerPing.PlayerInfo[0]));
        } else {
            players = event.getResponse().getPlayers();
        }

        ServerPing ping = new ServerPing(protocol, players, event.getResponse().getDescriptionComponent(), event.getResponse().getFaviconObject());
        event.setResponse(ping);
    }
}
