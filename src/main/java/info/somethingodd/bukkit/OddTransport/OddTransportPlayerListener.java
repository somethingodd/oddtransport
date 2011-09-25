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

import info.somethingodd.bukkit.OddItem.OddItem;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Gordon Pettey (petteyg359@gmail.com)
 */
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
        if (!OddItem.compare(item, oddTransport.block)) {
            return;
        }
        if (oddTransport.locations.get(location) == null && oddTransport.transporters.get(location) == null) {
            if (OddItem.compare(inHand, oddTransport.create)/* && player.hasPermission("oddtransport.create")*/) {
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
                    if (oddTransport.consume) OddItem.removeItem(player, oddTransport.create);
                }
            }
        } else {
            if (OddItem.compare(inHand, oddTransport.destroy)) {
                if (player.hasPermission("oddtransport.destroy") || oddTransport.transporters.get(location).equals(player)) {
                    Location l2 = oddTransport.locations.get(location);
                    oddTransport.locations.remove(location);
                    oddTransport.locations.remove(l2);
                    oddTransport.transporters.remove(location);
                    oddTransport.transporters.remove(l2);
                    player.sendMessage("Transport link between " + location.getX() + "," + location.getY() + "," + location.getZ() + " and " + l2.getX() + "," + l2.getY() + "," + l2.getZ() + " destroyed.");
                    if (oddTransport.consume) OddItem.removeItem(player, oddTransport.destroy);
                }
            } else if (OddItem.compare(inHand, oddTransport.use)) {
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
                    if (oddTransport.consume) OddItem.removeItem(player, oddTransport.use);
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
}
