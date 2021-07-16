package me.hulipvp.hcf.hooks.server.protocollib;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.FieldAccessException;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.EnumWrappers;
import me.hulipvp.hcf.HCF;
import me.hulipvp.hcf.listeners.GlassListener;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class PLib1_8 {

    public PLib1_8() {
        try {
            ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
            protocolManager.addPacketListener(new PacketAdapter(HCF.getInstance(), ListenerPriority.NORMAL, PacketType.Play.Client.BLOCK_PLACE) {
                @Override
                public void onPacketReceiving(PacketEvent event) {
                    StructureModifier<Integer> modifier = event.getPacket().getIntegers();
                    StructureModifier<BlockPosition> blockModifiers = event.getPacket().getBlockPositionModifier();
                    Player player = event.getPlayer();

                    try {
                        int face = modifier.read(0);
                        if(face == 255)
                            return;

                        BlockPosition blockPosition = blockModifiers.read(0);
                        Location clickedBlock = new Location(player.getWorld(), blockPosition.getX(), blockPosition.getY(), blockPosition.getZ());
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
                    } catch(FieldAccessException ex) {
                        ex.printStackTrace();
                    }
                }
            });

            protocolManager.addPacketListener(new PacketAdapter(HCF.getInstance(), ListenerPriority.NORMAL, PacketType.Play.Client.BLOCK_DIG) {
                @Override
                public void onPacketReceiving(PacketEvent event) {
                    try {
                        StructureModifier<BlockPosition> blockModifiers = event.getPacket().getBlockPositionModifier();
                        StructureModifier<EnumWrappers.PlayerDigType> digTypeModifiers = event.getPacket().getPlayerDigTypes();

                        EnumWrappers.PlayerDigType digType = digTypeModifiers.read(0);
                        if(digType.name().contains("BLOCK")) {
                            Player player = event.getPlayer();
                            BlockPosition blockPosition = blockModifiers.read(0);
                            if (player != null && blockPosition != null) {
                                Location clickedBlock = new Location(player.getWorld(), blockPosition.getX(), blockPosition.getY(), blockPosition.getZ());
                                if (GlassListener.hasGlass(player, clickedBlock)) {
                                    event.setCancelled(true);
                                    player.sendBlockChange(clickedBlock, 95, (byte) 14);
                                }
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
