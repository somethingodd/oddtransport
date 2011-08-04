package info.somethingodd.bukkit.OddTransport;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

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
                    sender.sendMessage(oddTransport.logPrefix + oddTransport.block.getType().toString()+";"+oddTransport.block.getDurability() + " " + oddTransport.create.getType().toString() + ";" + oddTransport.create.getDurability() + " " + oddTransport.destroy.getType().toString() + ";" + oddTransport.destroy.getDurability() + " " + oddTransport.use.getType().toString() + ";" + oddTransport.use.getDurability());

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
