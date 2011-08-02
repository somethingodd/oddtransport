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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

public class OddTransport extends JavaPlugin {
    private final String configurationFile = "plugins" + File.separator + "OddTransport.yml";
    private Logger log;
	private PluginDescriptionFile info;
    private Configuration configuration;
    private OddItem oddItem;
    protected String logPrefix;
    protected ItemStack block;
    protected ItemStack create;
    protected ItemStack destroy;
    protected ItemStack use;
    protected int delay;
    protected ConcurrentMap<Player, Integer> queuedTransports;
    protected ConcurrentMap<Location, Player> transporters;
    protected ConcurrentMap<Location, Location> locations;
    protected OddTransportCommand oddTransportCommand;
    protected OddTransportPlayerListener oddTransportPlayerListener;

    protected void configure() {
        File configurationFile = new File(this.configurationFile);
        if (!configurationFile.exists())
            writeConfig();
        configuration = new Configuration(configurationFile);
        configuration.load();
        try {
            block = oddItem.getItemStack(configuration.getString("items.block", "LAPIS_BLOCK"));
        } catch (IllegalArgumentException e) {
            block = new ItemStack(Material.LAPIS_BLOCK, 1, (short) 0);
        }
        try {
            create = oddItem.getItemStack(configuration.getString("items.create", "LAPIS_ORE"));
        } catch (IllegalArgumentException e) {
            create = new ItemStack(Material.LAPIS_ORE, 1, (short) 0);
        }
        try {
            destroy = oddItem.getItemStack(configuration.getString("items.destroy", "DIRT"));
        } catch (IllegalArgumentException e) {
            destroy = new ItemStack(Material.DIRT, 1, (short) 0);
        }
        try {
            use = oddItem.getItemStack(configuration.getString("items.use", "AIR"));
        } catch (IllegalArgumentException e) {
            use = new ItemStack(Material.AIR, 1, (short) 0);
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
