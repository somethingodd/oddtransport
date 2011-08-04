package info.somethingodd.bukkit.OddTransport;

import info.somethingodd.bukkit.OddItem.OddItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import java.io.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

public class OddTransport extends JavaPlugin {
    private final String configurationFile = "plugins" + File.separator + "OddTransport.yml";
    private Logger log = null;
	private PluginDescriptionFile info = null;
    private OddItem oddItem = null;
    protected String logPrefix = null;
    protected ItemStack block = null;
    protected ItemStack create = null;
    protected ItemStack destroy = null;
    protected ItemStack use = null;
    protected Integer delay = null;
    protected ConcurrentMap<Player, Integer> queuedTransports = null;
    protected ConcurrentMap<Location, Player> transporters = null;
    protected ConcurrentMap<Location, Location> locations = null;
    protected OddTransportCommand oddTransportCommand = null;
    protected OddTransportPlayerListener oddTransportPlayerListener = null;

    protected void configure() {
        File configurationFile = new File(this.configurationFile);
        if (!configurationFile.exists())
            writeConfig();
        Configuration configuration = new Configuration(configurationFile);
        configuration.load();
        if (oddItem != null) {
            List<ItemStack> items = oddItem.getItemGroup("OddTransport");
            switch (items.size()) {
                case 4:
                    this.use = items.get(3);
                case 3:
                    this.destroy = items.get(2);
                case 2:
                    this.create = items.get(1);
                case 1:
                    this.block = items.get(0);
            }
        }
        if (this.block == null) {
            try {
                block = oddItem.getItemStack(configuration.getString("items.block", ""));
            } catch (IllegalArgumentException e) {
                block = new ItemStack(Material.getMaterial("LAPIS_BLOCK"), 1, (short) 0);
            }
        }
        if (this.create == null) {
            try {
                create = oddItem.getItemStack(configuration.getString("items.create", ""));
            } catch (IllegalArgumentException e) {
                create = new ItemStack(Material.getMaterial("LAPIS_ORE"), 1, (short) 0);
            }
        }
        if (this.destroy == null) {
            try {
                destroy = oddItem.getItemStack(configuration.getString("items.destroy", ""));
            } catch (IllegalArgumentException e) {
                destroy = new ItemStack(Material.getMaterial("DIRT"), 1, (short) 0);
            }
        }
        if (this.use == null) {
            try {
                use = oddItem.getItemStack(configuration.getString("items.use", ""));
            } catch (IllegalArgumentException e) {
                use = new ItemStack(Material.getMaterial("AIR"), 1, (short) 0);
            }
        }
        this.delay = configuration.getInt("delay", 3);
    }

    @Override
    public void onDisable() {
        log.info(logPrefix + "disabled");
    }

    @Override
    public void onEnable() {
        oddTransportCommand = new OddTransportCommand(this);
        oddTransportPlayerListener = new OddTransportPlayerListener(this);
        getCommand("oddtransport").setExecutor(oddTransportCommand);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_INTERACT, oddTransportPlayerListener, Event.Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_MOVE, oddTransportPlayerListener, Event.Priority.Normal, this);
        transporters = new ConcurrentHashMap<Location, Player>();
        queuedTransports = new ConcurrentHashMap<Player, Integer>();
        locations = new ConcurrentHashMap<Location, Location>();
        Plugin p;
        p = getServer().getPluginManager().getPlugin("OddItem");
        if (p != null) {
            oddItem = (OddItem) p;
            log.info(logPrefix + "Found OddItem");
        }
        log.info(logPrefix + info.getVersion() + " enabled");
        configure();
    }

    @Override
    public void onLoad() {
        info = getDescription();
        log = getServer().getLogger();
        logPrefix = "[" + info.getName() + "] ";
    }

    private void writeConfig() {
        try {
            BufferedReader i = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/OddTransport.yml")));
            BufferedWriter o = new BufferedWriter(new FileWriter(configurationFile));
            String line = i.readLine();
            while (line != null) {
                o.write(line + System.getProperty("line.separator"));
                line = i.readLine();
            }
            o.close();
            i.close();
        } catch (Exception e) {
            log.severe(logPrefix + "Error writing configuration.");
            e.printStackTrace();
        } finally {
            log.info(logPrefix + "Wrote default config");
        }
    }

}
