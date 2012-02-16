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
package info.somethingodd.OddTransport;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author Gordon Pettey (petteyg359@gmail.com)
 */
public class OddTransportCommand implements CommandExecutor {

   private OddTransport oddTransport = null;

    public OddTransportCommand(OddTransport oddTransport) {
        this.oddTransport = oddTransport;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 1:
                if (args[0].equals("info")) {
                    sender.sendMessage(oddTransport.logPrefix + "block create destroy use");
                    sender.sendMessage(oddTransport.logPrefix + oddTransport.oddTransportConfiguration.block.getType().toString()+";"+ oddTransport.oddTransportConfiguration.block.getDurability() + " " + oddTransport.oddTransportConfiguration.create.getType().toString() + ";" + oddTransport.oddTransportConfiguration.create.getDurability() + " " + oddTransport.oddTransportConfiguration.destroy.getType().toString() + ";" + oddTransport.oddTransportConfiguration.destroy.getDurability() + " " + oddTransport.oddTransportConfiguration.use.getType().toString() + ";" + oddTransport.oddTransportConfiguration.use.getDurability());

                } else if (args[0].equals("list")) {
                    Set<Entry<Location, Player>> es = oddTransport.transporters.entrySet();
                    Set<Location> l = new HashSet<Location>();
                    for (Entry<Location, Player> e : es) {
                        if (e.getValue().equals(sender))
                            l.add(e.getKey());
                    }
                    sender.sendMessage(oddTransport.logPrefix + l.toString());
                }
                return true;
        }
        return false;
    }
}
