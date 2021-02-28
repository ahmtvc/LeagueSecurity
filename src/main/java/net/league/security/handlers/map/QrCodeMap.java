package net.league.security.handlers.map;

import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.awt.*;

@AllArgsConstructor
public class QrCodeMap extends MapRenderer {

    private Image qrCodeImage;

    @Override
    public void render(MapView view, MapCanvas canvas, Player player) {
        canvas.drawImage(0, 0, this.qrCodeImage);
    }
}
