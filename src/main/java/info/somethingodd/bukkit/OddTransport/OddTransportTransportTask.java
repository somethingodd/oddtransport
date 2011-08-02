package info.somethingodd.bukkit.OddTransport;

import org.bukkit.Location;
import org.bukkit.entity.*;

public class OddTransportTransportTask implements Runnable {

    private Location location;
    private Player player;

    public OddTransportTransportTask(Location location, Player player) {
        this.location = location;
        this.player = player;
    }

    public void run() {
        player.sendMessage("Transporting.");
        for (LivingEntity entity : location.getWorld().getLivingEntities()) {
            if (entity.getLocation().equals(location)) {
                String blocker = "";
                if (entity instanceof Player) {
                    blocker = ((Player) entity).getName();
                } else if (entity instanceof Monster) {
                    blocker = "a " + entity.getClass().getName().toLowerCase();
                }
                player.sendMessage("Exit blocked by " + blocker + ".");
                return;
            }
        }
        player.teleport(location);
    }
}
