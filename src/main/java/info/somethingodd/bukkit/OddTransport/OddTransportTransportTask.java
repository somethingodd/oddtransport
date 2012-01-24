/* This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package info.somethingodd.bukkit.OddTransport;

import org.bukkit.Location;
import org.bukkit.entity.Animals;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;

/**
 * @author Gordon Pettey (petteyg359@gmail.com)
 */
public class OddTransportTransportTask implements Runnable {

    private Location location;
    private Player player;
    private OddTransportListener oddTransportListener;

    public OddTransportTransportTask(Location location, Player player, OddTransportListener oddTransportListener) {
        this.location = new Location(location.getWorld(), location.getX(), location.getY() + 1, location.getZ());
        this.player = player;
        this.oddTransportListener = oddTransportListener;
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
        oddTransportListener.queuedTransports.remove(player);
        player.teleport(location);
    }
}
