package com.airbnb.android.react.maps;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    private int tileSize = 256;
    private float zIndex;

    private static final PresetColor[] magma;

    static {
        magma = new PresetColor[]{
                new PresetColor(0.0, new SimpleColor(1, 1, 6)),
                new PresetColor(0.25, new SimpleColor(72, 20, 97)),
                new PresetColor(0.5, new SimpleColor(176, 47, 76)),
                new PresetColor(0.75, new SimpleColor(243, 109, 24)),
                new PresetColor(1.0, new SimpleColor(249, 251, 147))
        };
    }

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

    public void setTileSize(int tileSize) {
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
            options.tileProvider(new AirMapLocalTile.AIRMapUrlTileProvider(this.tileSize, this.urlTemplate));
        } else if (onlineReady || this.fileTemplate != null) {
            options.tileProvider(new AirMapLocalTile.AIRMapLocalTileProvider(this.tileSize, this.fileTemplate, this.urlTemplate, this.maxTempRange, this.currentTempRange));
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
                this.tileOverlay.clearTileCache();
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
            try {
                if (this.fileTemplate != null) {
                    File file = new File(getTileFilename(x, y, zoom));
                    if (!file.exists()) return null;
                    if (this.maxTempRange != null && this.currentTempRange != null) {
                        return processBitmap(makeBitmapFromFile(file));
                    } else {
                        return getDataFromFile(file);
                    }
                } else if (this.urlTemplate != null) {
                    URL url = getTileURL(x, y, zoom);
                    if (this.maxTempRange != null && this.currentTempRange != null) {
                        return processBitmap(makeBitmapFromURL(url));
                    } else {
                        return getDataFromUrl(url);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
            return null;
        }

        private String getTileFilename(int x, int y, int zoom) {
            return parsePath(this.fileTemplate, x, y, zoom);
        }

        private URL getTileURL(int x, int y, int zoom) throws MalformedURLException {
            return new URL(parsePath(this.urlTemplate, x, y, zoom));
        }

        private String parsePath(String path, int x, int y, int zoom) {
            return path
                    .replace("{x}", Integer.toString(x))
                    .replace("{y}", Integer.toString((1 << zoom) - 1 - y))
                    .replace("{z}", Integer.toString(zoom));
        }

        private byte[] getDataFromFile(File file) throws IOException {
            InputStream in = new FileInputStream(file);
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            int nRead;
            byte[] data = new byte[BUFFER_SIZE];

            while ((nRead = in.read(data, 0, BUFFER_SIZE)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            byte[] result = buffer.toByteArray();
            buffer.close();
            return result;
        }

        private byte[] getDataFromUrl(URL url) throws IOException {
            InputStream in = url.openStream();
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            int nRead;
            byte[] data = new byte[BUFFER_SIZE];

            while ((nRead = in.read(data, 0, BUFFER_SIZE)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            byte[] result = buffer.toByteArray();
            buffer.close();
            return result;
        }

        private byte[] processBitmap(Bitmap bitmap) throws IOException {
            int[] pixels = getPixelsFromBitmap(bitmap);
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            double minTemp = this.maxTempRange[0];
            double maxTemp = this.maxTempRange[1];
            double currentMinTemp = this.currentTempRange[0];
            double currentMaxTemp = this.currentTempRange[1];
            int stepBase = this.tileSize * this.tileSize;
            int i = 0;
            for (int pixel : pixels) {
                int alpha = (pixel >> 24) & 0xFF;
                int red = (pixel >> 16) & 0x00FF;
                int green = (pixel >> 8) & 0x0000FF;
                int blue = pixel & 0x000000FF;

                if (alpha == 0) {
                    pixels[i++] = 0;
                } else {
                    double step = (maxTemp - minTemp) / stepBase;
                    double elevation = minTemp + (red * this.tileSize + green + blue) * step;
                    int color;
                    if (elevation < currentMinTemp) {
                        color = getColorForPercentage(0);
                    } else if (elevation > currentMaxTemp) {
                        color = getColorForPercentage(1);
                    } else {
                        double ratio = (elevation - currentMinTemp) / (currentMaxTemp - currentMinTemp);
                        color = getColorForPercentage(ratio);
                    }
                    pixels[i++] = (alpha << 24) + color;
                }
            }
            bitmap.setPixels(pixels, 0, this.tileSize, 0, 0, this.tileSize, this.tileSize);
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, buffer);
            buffer.flush();
            byte[] result = buffer.toByteArray();
            buffer.close();
            return result;
        }

        private Bitmap makeBitmapFromFile(File file) {
            return BitmapFactory.decodeFile(file.getAbsolutePath(), makeBitmapOptions());
        }

        private Bitmap makeBitmapFromURL(URL url) throws IOException {
            return BitmapFactory.decodeStream(url.openStream(), null, makeBitmapOptions());
        }

        private BitmapFactory.Options makeBitmapOptions() {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inMutable = true;
            return options;
        }

        private int[] getPixelsFromBitmap(Bitmap bitmap) {
            int[] pixels = new int[this.tileSize * this.tileSize];
            bitmap.getPixels(pixels, 0, this.tileSize, 0, 0, this.tileSize, this.tileSize);
            return pixels;
        }

        private int getColorForPercentage(double percent) {
            int index;
            for (index = 1; index < AirMapLocalTile.magma.length - 1; index++) {
                if (percent < AirMapLocalTile.magma[index].percent) break;
            }
            PresetColor lower = AirMapLocalTile.magma[index - 1];
            PresetColor upper = AirMapLocalTile.magma[index ];
            double rangePercent = (percent - lower.percent) / (upper.percent - lower.percent);
            double percentLower = 1 - rangePercent;
            int red = (int)Math.floor(lower.color.red * percentLower + upper.color.red * rangePercent);
            int green = (int)Math.floor(lower.color.green * percentLower + upper.color.green * rangePercent);
            int blue = (int)Math.floor(lower.color.blue * percentLower + upper.color.blue * rangePercent);
            return (red << 16) | (green << 8) | (blue);
        }
    }

    class AIRMapUrlTileProvider extends UrlTileProvider {
        private boolean disabled = false;
        private String urlTemplate;

        public AIRMapUrlTileProvider(int tileSize, String urlTemplate) {
            super(tileSize, tileSize);
            this.urlTemplate = urlTemplate;
        }

        @Override
        public synchronized URL getTileUrl(int x, int y, int zoom) {
            URL url = null;
            try {
                if (disabled) return url;
                url = new URL(this.urlTemplate
                        .replace("{x}", Integer.toString(x))
                        .replace("{y}", Integer.toString((1 << zoom) - 1 - y))
                        .replace("{z}", Integer.toString(zoom)));
            } catch (MalformedURLException e) {
                throw new AssertionError(e);
            }
            return url;
        }
    }

}

class PresetColor {
    final double percent;
    final SimpleColor color;
    PresetColor(double percent, SimpleColor color) {
        this.percent = percent;
        this.color = color;
    }
}

class SimpleColor {
    final int red;
    final int green;
    final int blue;
    SimpleColor(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }
}
