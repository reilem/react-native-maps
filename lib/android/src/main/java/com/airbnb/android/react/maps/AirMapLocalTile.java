package com.airbnb.android.react.maps;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class AirMapLocalTile extends AirMapFeature {

    class AIRMapLocalTileProvider implements TileProvider {
        private static final int BUFFER_SIZE = 16 * 1024;
        private int tileSize;
        private String fileTemplate;
        private String urlTemplate;
        private double[] currentTempRange;
        private double[] tempRange;


        public AIRMapLocalTileProvider(int tileSizet, String fileTemplate) {
            this.tileSize = tileSizet;
            this.fileTemplate = fileTemplate;
        }

        @Override
        public Tile getTile(int x, int y, int zoom) {
            byte[] image = readTileImage(x, y, zoom);
            return image == null ? TileProvider.NO_TILE : new Tile(this.tileSize, this.tileSize, image);
        }

        public void setFileTemplate(String fileTemplate) {
            this.fileTemplate = fileTemplate;
        }

        public void setUrlTemplate(String urlTemplate) {
            this.urlTemplate = urlTemplate;
        }

        public void setCurrentTempRange(double[] currentTempRange) {
            this.currentTempRange = currentTempRange;
        }

        public void setTempRange(double[] tempRange) {
            this.tempRange = tempRange;
        }

        public void setTileSize(int tileSize) {
            this.tileSize = tileSize;
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

    private TileOverlayOptions tileOverlayOptions;
    private TileOverlay tileOverlay;
    private AirMapLocalTile.AIRMapLocalTileProvider tileProvider;

    private String fileTemplate;
    private String urlTemplate;
    private double[] currentTempRange;
    private double[] tempRange;
    private float tileSize;
    private float zIndex;

    public AirMapLocalTile(Context context) {
        super(context);
    }

    public void setFileTemplate(String fileTemplate) {
        this.fileTemplate = fileTemplate;
        if (tileProvider != null) {
            this.tileProvider.setFileTemplate(fileTemplate);
        }
        if (tileOverlay != null) {
            this.tileOverlay.clearTileCache();
        }
    }

    public void setUrlTemplate(String urlTemplate) {
        this.urlTemplate = urlTemplate;
        if (tileProvider != null) {
            this.tileProvider.setUrlTemplate(urlTemplate);
        }
    }

    public void setTempRange(double[] tempRange) {
        this.tempRange = tempRange;
        if (tileProvider != null) {
            this.tileProvider.setTempRange(tempRange);
        }
    }

    public void setCurrentTempRange(double[] currentTempRange) {
        this.currentTempRange = currentTempRange;
        if (tileProvider != null) {
            this.tileProvider.setCurrentTempRange(currentTempRange);
        }
    }

    public void setZIndex(float zIndex) {
        this.zIndex = zIndex;
        if (tileOverlay != null) {
            this.tileOverlay.setZIndex(zIndex);
        }
    }

    public void setTileSize(float tileSize) {
        this.tileSize = tileSize;
        if (tileProvider != null) {
            this.tileProvider.setTileSize((int)tileSize);
        }
    }

    public TileOverlayOptions getTileOverlayOptions() {
        if (tileOverlayOptions == null) {
            this.tileOverlayOptions = createTileOverlayOptions();
        }
        return this.tileOverlayOptions;
    }

    private TileOverlayOptions createTileOverlayOptions() {
        TileOverlayOptions options = new TileOverlayOptions();
        options.zIndex(zIndex);
        this.tileProvider = new AirMapLocalTile.AIRMapLocalTileProvider((int)this.tileSize, this.fileTemplate);
        options.tileProvider(this.tileProvider);
        return options;
    }

    @Override
    public Object getFeature() {
        return this.tileOverlay;
    }

    @Override
    public void addToMap(GoogleMap map) {
        this.tileOverlay = map.addTileOverlay(getTileOverlayOptions());
    }

    @Override
    public void removeFromMap(GoogleMap map) {
        this.tileOverlay.remove();
    }
}
