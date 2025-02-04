package misat11.bw.commands;

import static misat11.lib.lang.I18n.i18n;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import misat11.bw.Main;

public class BwCommandsExecutor implements CommandExecutor, TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
		List<String> completionList = new ArrayList<>();
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (args.length == 1) {
				for (BaseCommand c : Main.getCommands().values()) {
					if (c.hasPermission(player)) {
						completionList.add(c.getName());
					}
				}
			} else if (args.length > 1) {
				ArrayList<String> arguments = new ArrayList<String>(Arrays.asList(args));
				arguments.remove(0);
				BaseCommand bCommand = Main.getCommands().get(args[0]);
				if (bCommand != null) {
					if (bCommand.hasPermission(player)) {
						bCommand.completeTab(completionList, sender, arguments);
					}
				}
			}
		}
		List<String> finalCompletionList = new ArrayList<>();
		StringUtil.copyPartialMatches(args[args.length - 1], completionList, finalCompletionList);
		return finalCompletionList;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 0) {
			args = new String[] {"help"};
		}

		String command = args[0];
		ArrayList<String> arguments = new ArrayList<String>(Arrays.asList(args));
		arguments.remove(0);

		BaseCommand bCommand = Main.getCommands().get(command.toLowerCase());
		
		if (bCommand == null) {
			sender.sendMessage(i18n("unknown_command"));
			return true;
		}
		
		if (sender instanceof ConsoleCommandSender) {
			if (!bCommand.isConsoleCommand()) {
				sender.sendMessage("§cConsole can't use this command!");
				return true;
			}
		}
		
		if (!bCommand.hasPermission(sender)) {
			sender.sendMessage(i18n("no_permissions"));
			return true;
		}

		boolean result = bCommand.execute(sender, arguments);
		
		if (!result) {
			sender.sendMessage(i18n("unknown_usage"));
		}
		
		return true;
	}

}
