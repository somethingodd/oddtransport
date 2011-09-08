package info.somethingodd.bukkit.OddTransport;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class OddTransportPlayerListener extends PlayerListener {

    private OddTransport oddTransport;
    private ConcurrentMap<Player, Location> locations = null;
    protected ConcurrentMap<Player, Integer> queuedTransports;

    public OddTransportPlayerListener (OddTransport oddTransport) {
        this.oddTransport = oddTransport;
        locations = new ConcurrentHashMap<Player, Location>();
        queuedTransports = new ConcurrentHashMap<Player, Integer>();
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.hasBlock() || !event.hasItem())
            return;
        Player player = event.getPlayer();
        ItemStack inHand = player.getItemInHand();
        ItemStack item = new ItemStack(event.getClickedBlock().getType(), 1, event.getClickedBlock().getData());
        Location location = event.getClickedBlock().getLocation();
        if (!itemStackEquals(item, oddTransport.block)) {
            return;
        }
        if (oddTransport.locations.get(location) == null && oddTransport.transporters.get(location) == null) {
            if (itemStackEquals(inHand, oddTransport.create)/* && player.hasPermission("oddtransport.create")*/) {
                oddTransport.transporters.put(location, player);
                Location linkLoc = locations.get(player);
                if (linkLoc == null) {
                    locations.put(player, location);
                    player.sendMessage(oddTransport.logPrefix + "Transporter created at " + location.getX() + "," + location.getY() + "," + location.getZ() + ".");
                } else {
                    oddTransport.locations.put(location, linkLoc);
                    oddTransport.locations.put(linkLoc, location);
                    locations.remove(player);
                    player.sendMessage(oddTransport.logPrefix + "Transporter linked from " + location.getX() + "," + location.getY() + "," + location.getZ() + " to " + linkLoc.getX() + "," + linkLoc.getY() + "," + linkLoc.getZ() + ".");
                }
            }
        } else {
            if (itemStackEquals(inHand, oddTransport.destroy)) {
                if (player.hasPermission("oddtransport.destroy") || oddTransport.transporters.get(location).equals(player)) {
                    Location l2 = oddTransport.locations.get(location);
                    oddTransport.locations.remove(location);
                    oddTransport.locations.remove(l2);
                    oddTransport.transporters.remove(location);
                    oddTransport.transporters.remove(l2);
                    player.sendMessage("Transport link between " + location.getX() + "," + location.getY() + "," + location.getZ() + " and " + l2.getX() + "," + l2.getY() + "," + l2.getZ() + " destroyed.");
                }
            } else if (itemStackEquals(inHand, oddTransport.use)) {
                if (oddTransport.transporters.get(location).equals(player) || player.hasPermission("oddtransport.use.other")) {
                    Location l2 = oddTransport.locations.get(location);
                    player.sendMessage(oddTransport.logPrefix + "Transporting to " + l2.getX() + "," + l2.getY() + "," + l2.getZ() + " in " + oddTransport.delay + " seconds...");
                    Integer queue = queuedTransports.get(player);
                    if (queue != null) {
                        oddTransport.getServer().getScheduler().cancelTask(queue);
                        player.sendMessage("Transport cancelled.");
                        queuedTransports.remove(player);
                    }
                    queue = oddTransport.getServer().getScheduler().scheduleSyncDelayedTask(oddTransport, new OddTransportTransportTask(l2, player, this), oddTransport.delay * 20);
                    queuedTransports.put(player, queue);
                }
            }
        }
    }

    @Override
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockY() == event.getTo().getBlockY() && event.getFrom().getBlockZ() == event.getTo().getBlockZ())
            return;
        Player player = event.getPlayer();
        Integer queue = queuedTransports.get(player);
        if (queue != null) {
            queuedTransports.remove(player);
            oddTransport.getServer().getScheduler().cancelTask(queue);
            player.sendMessage("Transport cancelled.");
        }
    }

    private boolean itemStackEquals(ItemStack a, ItemStack b) {
        return (a.getTypeId() == b.getTypeId() && a.getDurability() == b.getDurability());
    }
}
