package me.hsgamer.minestomskinsrestorer;

import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerPluginMessageEvent;
import net.minestom.server.extensions.Extension;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class MinestomSkinsRestorer extends Extension {
    private final EventNode<Event> node = EventNode.all("skinsrestorer-change");

    @Override
    public void initialize() {
        node.addListener(PlayerPluginMessageEvent.class, event -> {
            String channel = event.getIdentifier();
            if (!channel.equals("sr:skinchange")) return;
            try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getMessage()))) {
                String subChannel = in.readUTF();
                if (subChannel.equalsIgnoreCase("SkinUpdate")) {
                    // noinspection unused
                    String name = in.readUTF();
                    String value = in.readUTF();
                    String signature = in.readUTF();
                    event.getPlayer().setSkin(new PlayerSkin(value, signature));
                }
            } catch (IOException e) {
                getLogger().warn("Error while reading skin change packet", e);
            }
        });
        MinecraftServer.getGlobalEventHandler().addChild(node);
    }

    @Override
    public void terminate() {
        MinecraftServer.getGlobalEventHandler().removeChild(node);
    }
}
