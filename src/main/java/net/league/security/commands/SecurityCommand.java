package net.league.security.commands;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import net.league.security.LeagueSecurity;
import net.league.security.handlers.Handler;
import net.league.security.handlers.map.QrCodeMap;
import net.league.security.handlers.player.PlayerData;
import net.league.security.language.Language;
import net.league.security.utilities.Utilities;
import net.league.security.utilities.chat.CC;
import net.league.security.utilities.item.ItemBuilder;
import net.league.security.utilities.time.TimeFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.map.MapView;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class SecurityCommand extends Handler implements CommandExecutor {

    public SecurityCommand(LeagueSecurity plugin) {
        super(plugin);
        this.plugin.getCommand("2fa").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Bukkit.getScheduler().runTask(this.plugin, () -> {
            if (!(sender instanceof Player)) {
                sender.sendMessage(CC.translate("&cFor player use only!"));
                return;
            }

            Player player = (Player) sender;
            PlayerData playerData = this.plugin.getPlayerDataHandler().getPlayerData().get(player.getUniqueId());

            if (playerData.isSetupSecurity() && !playerData.isVerify()) {
                GoogleAuthenticator authenticator = new GoogleAuthenticator();
                GoogleAuthenticatorKey key = authenticator.createCredentials();

                try {
                    URL url = new URL(Utilities.getQrImageURL(key.getKey()));
                    BufferedImage image = ImageIO.read(url);

                    ItemBuilder item = new ItemBuilder(Material.MAP);
                    item.setName("&aQR Security Code");
                    item.addLoreLine("&7Security QR Code");

                    MapView view = Bukkit.createMap(player.getWorld());
                    view.getRenderers().clear();
                    view.addRenderer(new QrCodeMap(image));

                    item.setDurability(view.getId());

                    playerData.setLastItem(player.getItemInHand());
                    playerData.setLastItemSlot(Utilities.getHotbarSlotOfItem(player.getItemInHand(), player));
                    player.setItemInHand(item.toItemStack());

                    playerData.setVerify(true);
                    playerData.setKey(key.getKey());
                    playerData.save();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }

            if (!playerData.isVerify()) {
                player.sendMessage(Language.NO_NEED_TO_VERIFY.toString());
                return;
            }

            if (args.length == 0) {
                player.sendMessage(CC.translate("&cUse: /2fa <code>"));
                return;
            }

            int code;

            try {
                code = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                player.sendMessage(Language.NUMBER_FORMAT_WRONG.toString());
                return;
            }

            if (!Utilities.checkCode(playerData.getKey(), code)) {
                player.sendMessage(Language.WRONG_KEY.toString());
                return;
            }

            if (playerData.isSetupSecurity()) {
                if (playerData.getLastItemSlot() != -1) {
                    player.getInventory().setItem(playerData.getLastItemSlot(), playerData.getLastItem());
                }
            }

            Utilities.removeQrMapFromInventory(player);

            playerData.setSetupSecurity(false);
            playerData.setVerify(false);

            player.sendMessage(Language.VERIFIED.toString());

            long next = TimeFormatUtils.parseTime(this.plugin.getConfiguration().getString("time-between-next-auth"));

            playerData.setNextAuth(next == -1 ? TimeFormatUtils.parseTime("5h") + System.currentTimeMillis() : next + System.currentTimeMillis());
            playerData.save();
        });
        return false;
    }
}
