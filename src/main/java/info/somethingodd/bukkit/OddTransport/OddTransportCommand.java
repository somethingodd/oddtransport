package info.somethingodd.bukkit.OddTransport;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class OddTransportCommand implements CommandExecutor {

   private OddTransport oddTransport = null;

    public OddTransportCommand(OddTransport oddTransport) {
        this.oddTransport = oddTransport;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 0:
                sender.sendMessage(oddTransport.logPrefix);
                return true;
            case 1:
                return true;
        }
        return false;
    }
}
