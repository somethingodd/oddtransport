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
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author Gordon Pettey (petteyg359@gmail.com)
 */
public class OddTransport extends JavaPlugin {
    protected Logger log;
	protected String logPrefix;
    protected Map<Player, Integer> queuedTransports;
    protected Map<Location, Player> transporters;
    protected Map<Location, Location> locations;
    protected OddTransportCommand oddTransportCommand;
    protected OddTransportListener oddTransportListener;
    protected OddTransportConfiguration oddTransportConfiguration;

    @Override
    public void onDisable() {
        queuedTransports = null;
        transporters = null;
        locations = null;
        oddTransportCommand = null;
        oddTransportListener = null;
        log.info(logPrefix + "disabled");
        logPrefix = null;
        log = null;
    }

    @Override
    public void onEnable() {
        logPrefix = "[" + getDescription().getName() + "] ";
        oddTransportCommand = new OddTransportCommand(this);
        oddTransportListener = new OddTransportListener(this);
        oddTransportConfiguration = new OddTransportConfiguration(this);
        oddTransportConfiguration.configure();
        getCommand("oddtransport").setExecutor(oddTransportCommand);
        getServer().getPluginManager().registerEvents(oddTransportListener, this);
        transporters = Collections.synchronizedMap(new HashMap<Location, Player>());
        queuedTransports = Collections.synchronizedMap(new HashMap<Player, Integer>());
        locations = Collections.synchronizedMap(new HashMap<Location, Location>());
        log.info(logPrefix + getDescription().getVersion() + " enabled");
    }
}
