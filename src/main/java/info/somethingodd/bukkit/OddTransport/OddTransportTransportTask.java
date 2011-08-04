package info.somethingodd.bukkit.OddTransport;

import org.bukkit.Location;
import org.bukkit.entity.Animals;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;

public class OddTransportTransportTask implements Runnable {

    private Location location;
    private Player player;
    private OddTransportPlayerListener oddTransportPlayerListener;

    public OddTransportTransportTask(Location location, Player player, OddTransportPlayerListener oddTransportPlayerListener) {
        this.location = new Location(location.getWorld(), location.getX(), location.getY() + 1, location.getZ());
        this.player = player;
        this.oddTransportPlayerListener = oddTransportPlayerListener;
    }

    public void run() {
        location.setY(location.getY() + 1);
        player.sendMessage("Transporting...");
        for (LivingEntity entity : location.getWorld().getLivingEntities()) {
            if (entity.getLocation().equals(location)) {
                String blocker = "";
                if (entity instanceof Player) {
                    blocker = ((Player) entity).getName();
                } else if (entity instanceof Monster || entity instanceof Animals) {
                    blocker = "a " + entity.getClass().getName().toLowerCase();
                }
                player.sendMessage("Destination blocked by " + blocker + ".");
                return;
            }
        }
        oddTransportPlayerListener.queuedTransports.remove(player);
        player.teleport(location);
    }
}
