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

    public OddTransportPlayerListener (OddTransport oddTransport) {
        this.oddTransport = oddTransport;
        locations = new ConcurrentHashMap<Player, Location>();
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.hasBlock() || !event.hasItem())
            return;
        Player player = event.getPlayer();
        ItemStack inHand = player.getItemInHand();
        ItemStack item = new ItemStack(event.getClickedBlock().getType(), 1, event.getClickedBlock().getData());
        Location location = event.getClickedBlock().getLocation();
        if (!itemStackEquals(item, oddTransport.block))
            return;
        if (itemStackEquals(inHand, oddTransport.use) && player.hasPermission("oddtransport.use")) {
            if (oddTransport.transporters.get(location) == null)
                return;
            if (!oddTransport.transporters.get(location).equals(player) && !player.hasPermission("oddtransport.use.other"))
                return;
            Integer queued = oddTransport.queuedTransports.get(player);
            if (queued != null)
                oddTransport.getServer().getScheduler().cancelTask(queued);
            Location to = oddTransport.locations.get(location);
            to.setY(to.getY() + 1);
            player.sendMessage("Teleport commencing in " + oddTransport.delay + " seconds...");
            queued = oddTransport.getServer().getScheduler().scheduleSyncDelayedTask(oddTransport, new OddTransportTransportTask(to, player), oddTransport.delay * 20);
            oddTransport.queuedTransports.put(player, queued);
        } else if (itemStackEquals(inHand, oddTransport.create) && player.hasPermission("oddtransport.create")) {
            if (oddTransport.transporters.get(location) != null) {
                player.sendMessage(oddTransport.logPrefix + "There is already a transporter here.");
                return;
            }
            Location location2 = locations.get(player);
            if (location2 != null) {
                oddTransport.transporters.put(location, player);
                oddTransport.transporters.put(location2, player);
                oddTransport.locations.put(location, location2);
                oddTransport.locations.put(location2, location);
                locations.remove(player);
                player.sendMessage("Transporter linked: " + location2.getX() + "," + location2.getY() + "," + location2.getZ() + " to " + location.getX() + "," + location.getY() + "," + location.getZ() + ".");
                return;
            }
            locations.put(player, location);
            player.sendMessage("Transporter ready at " + location.getX() + "," + location.getY() + "," + location.getZ() + ". Select another transporter to link.");

        } else if (itemStackEquals(inHand, oddTransport.destroy) && player.hasPermission("oddtransport.destroy")) {
            if (oddTransport.transporters.get(location) == null) {
                return;
            }
            if (oddTransport.locations.get(location) != player && !player.hasPermission("oddtransport.destroy.other")) {
                player.sendMessage("You do not have permission to destroy this transporter.");
                return;
            }
            oddTransport.locations.remove(location);
            oddTransport.transporters.remove(oddTransport.transporters.get(location));
            oddTransport.transporters.remove(location);
        }
    }

    @Override
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Integer queue = oddTransport.queuedTransports.get(player);
        if (queue != null) {
            oddTransport.getServer().getScheduler().cancelTask(queue);
            player.sendMessage("Transport cancelled.");
        }
    }

    private boolean itemStackEquals(ItemStack a, ItemStack b) {
        if (a.getTypeId() != b.getTypeId())
            return false;
        if (a.getDurability() != b.getDurability())
            return false;
        return true;
    }
}
