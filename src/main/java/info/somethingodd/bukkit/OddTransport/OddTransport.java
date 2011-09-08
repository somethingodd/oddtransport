package info.somethingodd.bukkit.OddTransport;

import info.somethingodd.bukkit.OddItem.OddItem;
import info.somethingodd.bukkit.OddItem.OddItemGroup;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

public class OddTransport extends JavaPlugin {
    private final String configurationFile = "plugins" + File.separator + "OddTransport.yml";
    private Logger log = null;
	private PluginDescriptionFile info = null;
    protected String logPrefix = null;
    protected ItemStack block = null;
    protected ItemStack create = null;
    protected ItemStack destroy = null;
    protected ItemStack use = null;
    protected Integer delay = null;
    protected Boolean consume = null;
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
        try {
            OddItemGroup items = OddItem.getItemGroup("OddTransport");
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
        } catch (Exception e) {
            log.info(logPrefix + "OddItem not available.");
        }
        if (this.block == null) {
            try {
                block = OddItem.getItemStack(configuration.getString("items.block", ""));
            } catch (IllegalArgumentException e) {
                block = new ItemStack(Material.getMaterial("LAPIS_BLOCK"), 1, (short) 0);
            }
        }
        if (this.create == null) {
            try {
                create = OddItem.getItemStack(configuration.getString("items.create", ""));
            } catch (IllegalArgumentException e) {
                create = new ItemStack(Material.getMaterial("LAPIS_ORE"), 1, (short) 0);
            }
        }
        if (this.destroy == null) {
            try {
                destroy = OddItem.getItemStack(configuration.getString("items.destroy", ""));
            } catch (IllegalArgumentException e) {
                destroy = new ItemStack(Material.getMaterial("DIRT"), 1, (short) 0);
            }
        }
        if (this.use == null) {
            try {
                use = OddItem.getItemStack(configuration.getString("items.use", ""));
            } catch (IllegalArgumentException e) {
                use = new ItemStack(Material.getMaterial("AIR"), 1, (short) 0);
            }
        }
        this.delay = configuration.getInt("delay", 3);
        this.consume = configuration.getBoolean("consume", true);
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
