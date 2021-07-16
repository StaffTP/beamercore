package me.hulipvp.hcf.hooks.server.protocollib;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.FieldAccessException;
import com.comphenix.protocol.reflect.StructureModifier;
import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.listeners.GlassListener;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class PLib1_7 {

    public PLib1_7() {
        try {
            ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
            protocolManager.addPacketListener(new PacketAdapter(HCF.getInstance(), ListenerPriority.NORMAL, PacketType.Play.Client.BLOCK_PLACE) {
                @Override
                public void onPacketReceiving(PacketEvent event) {
                    StructureModifier<Integer> modifier = event.getPacket().getIntegers();
                    Player player = event.getPlayer();

                    try {
                        int face = modifier.read(0);
                        if(face == 255)
                            return;

                        Location clickedBlock = new Location(player.getWorld(), modifier.read(0), modifier.read(1), modifier.read(2));
                        if(GlassListener.hasGlass(player, clickedBlock)) {
                            Location placedLocation = clickedBlock.clone();
                            switch(face) {
                                case 2:
                                    placedLocation.add(0, 0, -1);
                                    break;
                                case 3:
                                    placedLocation.add(0, 0, 1);
                                    break;
                                case 4:
                                    placedLocation.add(-1, 0, 0);
                                    break;
                                case 5:
                                    placedLocation.add(1, 0, 0);
                                    break;
                                default:
                                    return;
                            }

                            event.setCancelled(true);

                            if(!GlassListener.hasGlass(player, clickedBlock)) {
                                event.setCancelled(true);
                                player.sendBlockChange(placedLocation, Material.AIR, (byte) 0);
                            }
                        }
                    } catch(FieldAccessException ex) { }
                }
            });

            protocolManager.addPacketListener(new PacketAdapter(HCF.getInstance(), ListenerPriority.NORMAL, PacketType.Play.Client.BLOCK_DIG) {
                @Override
                public void onPacketReceiving(PacketEvent event) {
                    try {
                        StructureModifier<Integer> modifier = event.getPacket().getIntegers();

                        int status = modifier.read(4);
                        if(status == 0 || status == 2) {
                            Player player = event.getPlayer();
                            int x = modifier.read(0), y = modifier.read(1), z = modifier.read(2);
                            Location location = new Location(player.getWorld(), x, y, z);
                            if(GlassListener.hasGlass(player, location)) {
                                event.setCancelled(true);
                                player.sendBlockChange(location, 95, (byte) 14);
                            }
                        }
                    } catch(FieldAccessException ex) { }
                }
            });
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }
}
