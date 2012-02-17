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

import info.somethingodd.OddItem.OddItem;
import info.somethingodd.OddItem.configuration.OddItemGroup;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Gordon Pettey (petteyg359@gmail.com)
 */
public class OddTransportConfiguration {
    private OddTransport oddTransport;
    protected ItemStack block;
    protected ItemStack create;
    protected ItemStack destroy;
    protected ItemStack use;
    protected Integer delay;
    protected Boolean consume;

    public OddTransportConfiguration(OddTransport oddTransport) {
        this.oddTransport = oddTransport;
    }

    protected void configure() {
        String[] filenames = {"config.yml"};
        try {
            initialConfig(filenames);
        } catch (Exception e) {
            oddTransport.log.warning("Exception writing initial configuration files: " + e.getMessage());
            e.printStackTrace();
        }
        YamlConfiguration defaultConfiguration = new YamlConfiguration();
        try {
            defaultConfiguration.load(oddTransport.getResource("OddTransport.yml"));
        } catch (Exception e) {
            oddTransport.log.warning(oddTransport.logPrefix + "Error loading default configuration! " + e.getMessage());
            e.printStackTrace();
        }
        File configurationFile = new File(oddTransport.getDataFolder() + File.separator + "OddTransport.yml");
        YamlConfiguration configuration = new YamlConfiguration();
        try {
            configuration.load(configurationFile);
        } catch (Exception e) {
            oddTransport.log.warning(oddTransport.logPrefix + "Error loading configuration file! " + e.getMessage());
            e.printStackTrace();
        }
        OddItemGroup items = OddItem.getItemGroup("oddtransport");
        this.block = items.get(0);
        this.create = items.get(1);
        this.destroy = items.get(2);
        this.use = items.get(3);
        this.delay = configuration.getInt("delay");
        this.consume = configuration.getBoolean("consume");
    }

    private void initialConfig(String[] filenames) throws IOException {
        for (String filename : filenames) {
            File file = new File(oddTransport.getDataFolder(), filename);
            if (!file.exists()) {
                BufferedReader src = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/" + filename)));
                BufferedWriter dst = new BufferedWriter(new FileWriter(file));
                try {
                    file.mkdirs();
                    file.createNewFile();
                    src = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/" + filename)));
                    dst = new BufferedWriter(new FileWriter(file));
                    String line = src.readLine();
                    while (line != null) {
                        dst.write(line + "\n");
                        line = src.readLine();
                    }
                    src.close();
                    dst.close();
                    oddTransport.log.info("Wrote default " + filename);
                } catch (IOException e) {
                    oddTransport.log.warning("Error writing default " + filename);
                } finally {
                    try {
                        src.close();
                        dst.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
    }
}
