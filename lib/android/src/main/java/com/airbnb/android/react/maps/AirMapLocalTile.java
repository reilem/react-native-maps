package com.airbnb.android.react.maps;

import android.content.Context;
import android.util.Log;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.*;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

public class AirMapLocalTile extends AirMapFeature {

    private TileOverlayOptions tileOverlayOptions;
    private TileOverlay tileOverlay;
    private GoogleMap map;

    private String fileTemplate;
    private String urlTemplate;
    private double[] currentTempRange;
    private double[] maxTempRange;
    private float tileSize;
    private float zIndex;

    public AirMapLocalTile(Context context) {
        super(context);
    }

    public void setFileTemplate(String fileTemplate) {
        this.fileTemplate = fileTemplate;
        this.updateTileOverlayOptions();
    }

    public void setUrlTemplate(String urlTemplate) {
        this.urlTemplate = urlTemplate;
        this.updateTileOverlayOptions();
    }

    public void setMaxTempRange(double[] maxTempRange) {
        this.maxTempRange = maxTempRange;
        this.updateTileOverlayOptions();
    }

    public void setCurrentTempRange(double[] currentTempRange) {
        this.currentTempRange = currentTempRange;
        this.updateTileOverlayOptions();
    }

    public void setZIndex(float zIndex) {
        this.zIndex = zIndex;
        this.updateTileOverlayOptions();
    }

    public void setTileSize(float tileSize) {
        this.tileSize = tileSize;
        this.updateTileOverlayOptions();
    }

    public TileOverlayOptions getTileOverlayOptions() {
        if (tileOverlayOptions == null) {
            this.updateTileOverlayOptions();
        }
        return this.tileOverlayOptions;
    }

    private void updateTileOverlayOptions() {
        TileOverlayOptions options = new TileOverlayOptions();
        options.zIndex(this.zIndex);
        boolean onlineReady = this.urlTemplate != null && this.fileTemplate == null;
        if (onlineReady && (this.currentTempRange == null || this.maxTempRange == null)) {
            options.tileProvider(new AirMapLocalTile.AIRMapUrlTileProvider(256, this.urlTemplate));
        } else if (onlineReady || this.fileTemplate != null) {
            options.tileProvider(new AirMapLocalTile.AIRMapLocalTileProvider(256, this.fileTemplate, this.urlTemplate, this.maxTempRange, this.currentTempRange));
        }
        this.tileOverlayOptions = options;
        this.updateTileOverlay();
    }

    @Override
    public Object getFeature() {
        return this.tileOverlay;
    }

    @Override
    public void addToMap(GoogleMap map) {
        this.map = map;
        this.updateTileOverlay();
    }

    @Override
    public void removeFromMap(GoogleMap map) {
        this.tileOverlay.remove();
    }

    private void updateTileOverlay() {
        if (this.map != null) {
            if (this.tileOverlay != null) {
                this.tileOverlay.remove();
            }
            this.tileOverlay = map.addTileOverlay(getTileOverlayOptions());
        }
    }

    class AIRMapLocalTileProvider implements TileProvider {
        private static final int BUFFER_SIZE = 16 * 1024;
        private int tileSize;
        private String fileTemplate;
        private String urlTemplate;
        private double[] currentTempRange;
        private double[] maxTempRange;


        public AIRMapLocalTileProvider(int tileSizet, String fileTemplate, String urlTemplate, double[] maxTempRange, double[] currentTempRange) {
            this.tileSize = tileSizet;
            this.fileTemplate = fileTemplate;
            this.urlTemplate = urlTemplate;
            this.maxTempRange = maxTempRange;
            this.currentTempRange = currentTempRange;
        }

        @Override
        public Tile getTile(int x, int y, int zoom) {
            byte[] image = readTileImage(x, y, zoom);
            return image == null ? TileProvider.NO_TILE : new Tile(this.tileSize, this.tileSize, image);
        }

        private byte[] readTileImage(int x, int y, int zoom) {
            InputStream in = null;
            ByteArrayOutputStream buffer = null;
            File file = new File(getTileFilename(x, y, zoom));

            try {
                in = new FileInputStream(file);
                buffer = new ByteArrayOutputStream();

                int nRead;
                byte[] data = new byte[BUFFER_SIZE];

                while ((nRead = in.read(data, 0, BUFFER_SIZE)) != -1) {
                    buffer.write(data, 0, nRead);
                }
                buffer.flush();

                return buffer.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                return null;
            } finally {
                if (in != null) try { in.close(); } catch (Exception ignored) {}
                if (buffer != null) try { buffer.close(); } catch (Exception ignored) {}
            }
        }

        private String getTileFilename(int x, int y, int zoom) {
            String s = this.fileTemplate
                    .replace("{x}", Integer.toString(x))
                    .replace("{y}", Integer.toString((1 << zoom) - 1 - y))
                    .replace("{z}", Integer.toString(zoom));
            return s;
        }
    }

    class AIRMapUrlTileProvider extends UrlTileProvider {
        private String urlTemplate;

        public AIRMapUrlTileProvider(int tileSize, String urlTemplate) {
            super(tileSize, tileSize);
            this.urlTemplate = urlTemplate;
        }

        @Override
        public synchronized URL getTileUrl(int x, int y, int zoom) {
            String s = this.urlTemplate
                    .replace("{x}", Integer.toString(x))
                    .replace("{y}", Integer.toString((1 << zoom) - 1 - y))
                    .replace("{z}", Integer.toString(zoom));
            URL url;
            try {
                url = new URL(s);
            } catch (MalformedURLException e) {
                throw new AssertionError(e);
            }
            return url;
        }
    }
}
