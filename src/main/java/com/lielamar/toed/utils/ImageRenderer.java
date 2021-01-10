package com.lielamar.toed.utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;

public class ImageRenderer extends MapRenderer {

    private final SoftReference<BufferedImage> cachedImage;
    private boolean hasRendered = false;

    public ImageRenderer(InputStream resource) throws IOException {
        this.cachedImage = new SoftReference<>(this.getImage(resource));
    }

    @Override
    public void render(@Nonnull MapView view, @Nonnull MapCanvas canvas, @Nonnull Player player) {
        if(this.hasRendered)
            return;

        view.setScale(MapView.Scale.NORMAL);

        if(this.cachedImage != null && this.cachedImage.get() != null)
            canvas.drawImage(0, 0, this.cachedImage.get());
        else
            player.sendMessage(ChatColor.RED + "Could not render the image!");

        this.hasRendered = true;
    }

    private BufferedImage getImage(InputStream resource) throws IOException {
        BufferedImage image =  ImageIO.read(resource);

        return resize(image, new Dimension(128, 128));
    }

    private BufferedImage resize(final BufferedImage image, final Dimension size) {
        final BufferedImage resized = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g = resized.createGraphics();
        g.drawImage(image, 0, 0, size.width, size.height, null);
        g.dispose();
        return resized;
    }
}