package org.SitekickRemastered.utils;

import java.awt.*;
import java.awt.image.*;

/* This is originally from the SwingX java library. Cut it down cause all we need is Linear Burn / Subtract */


public final class BlendComposite implements Composite {

    /**
     * @author Romain Guy <romain.guy@mac.com>
     * @author Karl Schaefer (support and additional modes)
     */
    public enum BlendingMode {

        MULTIPLY {
            @Override
            void blend(int[] src, int[] dst, int[] result) {
                result[0] = (src[0] * dst[0] + 2) >> 8;
                result[1] = (src[1] * dst[1] + 2) >> 8;
                result[2] = (src[2] * dst[2] + 2) >> 8;
                result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
            }
        }, SUBTRACT {
            @Override
            void blend(int[] src, int[] dst, int[] result) {
                result[0] = Math.max(0, src[0] + dst[0] - 256);
                result[1] = Math.max(0, src[1] + dst[1] - 256);
                result[2] = Math.max(0, src[2] + dst[2] - 256);
                result[3] = Math.min(255, src[3] + dst[3] - (src[3] * dst[3]) / 255);
            }
        };


        /**
         * Blends the input colors into the result.
         *
         * @param src    the source RGBA
         * @param dst    the destination RGBA
         * @param result the result RGBA
         * @throws NullPointerException if any argument is {@code null}
         */
        abstract void blend(int[] src, int[] dst, int[] result);
    }


    public static final BlendComposite Multiply = new BlendComposite(BlendingMode.MULTIPLY);
    public static final BlendComposite Subtract = new BlendComposite(BlendingMode.SUBTRACT);

    private final float alpha;
    private final BlendingMode mode;


    private BlendComposite(BlendingMode mode) {
        this(mode, 1.0f);
    }


    private BlendComposite(BlendingMode mode, float alpha) {
        this.mode = mode;

        if (alpha < 0.0f || alpha > 1.0f) {
            throw new IllegalArgumentException("alpha must be comprised between 0.0f and 1.0f");
        }
        this.alpha = alpha;
    }


    /**
     * Returns the opacity of this composite. If no opacity has been defined,
     * 1.0 is returned.
     *
     * @return the alpha value, or opacity, of this object
     */
    public float getAlpha() {
        return alpha;
    }


    /**
     * Returns the blending mode of this composite.
     *
     * @return the blending mode used by this object
     */
    public BlendingMode getMode() {
        return mode;
    }


    @Override
    public int hashCode() {
        return Float.floatToIntBits(alpha) * 31 + mode.ordinal();
    }


    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BlendComposite bc)) {
            return false;
        }

        return mode == bc.mode && alpha == bc.alpha;
    }


    private static boolean isRgbColorModel(ColorModel cm) {
        if (cm instanceof DirectColorModel directCM && cm.getTransferType() == DataBuffer.TYPE_INT) {

            return directCM.getRedMask() == 0x00FF0000 && directCM.getGreenMask() == 0x0000FF00 && directCM.getBlueMask() == 0x000000FF && (directCM.getNumComponents() == 3 || directCM.getAlphaMask() == 0xFF000000);
        }

        return false;
    }


    private static boolean isBgrColorModel(ColorModel cm) {
        if (cm instanceof DirectColorModel directCM && cm.getTransferType() == DataBuffer.TYPE_INT) {

            return directCM.getRedMask() == 0x000000FF && directCM.getGreenMask() == 0x0000FF00 && directCM.getBlueMask() == 0x00FF0000 && (directCM.getNumComponents() == 3 || directCM.getAlphaMask() == 0xFF000000);
        }

        return false;
    }


    @Override
    public CompositeContext createContext(ColorModel srcColorModel, ColorModel dstColorModel, RenderingHints hints) {
        if (isRgbColorModel(srcColorModel) && isRgbColorModel(dstColorModel)) {
            return new BlendingRgbContext(this);
        }
        else if (isBgrColorModel(srcColorModel) && isBgrColorModel(dstColorModel)) {
            return new BlendingBgrContext(this);
        }

        throw new RasterFormatException("Incompatible color models:\n  " + srcColorModel + "\n  " + dstColorModel);
    }


    private static abstract class BlendingContext implements CompositeContext {

        protected final BlendComposite composite;


        private BlendingContext(BlendComposite composite) {
            this.composite = composite;
        }


        @Override
        public void dispose() {
        }
    }


    private static class BlendingRgbContext extends BlendingContext {

        private BlendingRgbContext(BlendComposite composite) {
            super(composite);
        }


        @Override
        public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {
            int width = Math.min(src.getWidth(), dstIn.getWidth());
            int height = Math.min(src.getHeight(), dstIn.getHeight());

            float alpha = composite.getAlpha();

            int[] result = new int[4];
            int[] srcPixel = new int[4];
            int[] dstPixel = new int[4];
            int[] srcPixels = new int[width];
            int[] dstPixels = new int[width];

            for (int y = 0; y < height; y++) {
                src.getDataElements(0, y, width, 1, srcPixels);
                dstIn.getDataElements(0, y, width, 1, dstPixels);
                for (int x = 0; x < width; x++) {
                    // pixels are stored as INT_ARGB
                    // our arrays are [R, G, B, A]
                    int pixel = srcPixels[x];
                    srcPixel[0] = (pixel >> 16) & 0xFF;
                    srcPixel[1] = (pixel >> 8) & 0xFF;
                    srcPixel[2] = (pixel) & 0xFF;
                    srcPixel[3] = (pixel >> 24) & 0xFF;

                    pixel = dstPixels[x];
                    dstPixel[0] = (pixel >> 16) & 0xFF;
                    dstPixel[1] = (pixel >> 8) & 0xFF;
                    dstPixel[2] = (pixel) & 0xFF;
                    dstPixel[3] = (pixel >> 24) & 0xFF;

                    composite.getMode().blend(srcPixel, dstPixel, result);

                    // mixes the result with the opacity
                    dstPixels[x] = ((int) (dstPixel[3] + (result[3] - dstPixel[3]) * alpha) & 0xFF) << 24 | ((int) (dstPixel[0] + (result[0] - dstPixel[0]) * alpha) & 0xFF) << 16 | ((int) (dstPixel[1] + (result[1] - dstPixel[1]) * alpha) & 0xFF) << 8 | (int) (dstPixel[2] + (result[2] - dstPixel[2]) * alpha) & 0xFF;
                }
                dstOut.setDataElements(0, y, width, 1, dstPixels);
            }
        }
    }


    private static class BlendingBgrContext extends BlendingContext {

        private BlendingBgrContext(BlendComposite composite) {
            super(composite);
        }


        @Override
        public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {
            int width = Math.min(src.getWidth(), dstIn.getWidth());
            int height = Math.min(src.getHeight(), dstIn.getHeight());

            float alpha = composite.getAlpha();

            int[] result = new int[4];
            int[] srcPixel = new int[4];
            int[] dstPixel = new int[4];
            int[] srcPixels = new int[width];
            int[] dstPixels = new int[width];

            for (int y = 0; y < height; y++) {
                src.getDataElements(0, y, width, 1, srcPixels);
                dstIn.getDataElements(0, y, width, 1, dstPixels);
                for (int x = 0; x < width; x++) {
                    // pixels are stored as INT_ABGR
                    // our arrays are [R, G, B, A]
                    int pixel = srcPixels[x];
                    srcPixel[0] = (pixel) & 0xFF;
                    srcPixel[1] = (pixel >> 8) & 0xFF;
                    srcPixel[2] = (pixel >> 16) & 0xFF;
                    srcPixel[3] = (pixel >> 24) & 0xFF;

                    pixel = dstPixels[x];
                    dstPixel[0] = (pixel) & 0xFF;
                    dstPixel[1] = (pixel >> 8) & 0xFF;
                    dstPixel[2] = (pixel >> 16) & 0xFF;
                    dstPixel[3] = (pixel >> 24) & 0xFF;

                    composite.getMode().blend(srcPixel, dstPixel, result);

                    // mixes the result with the opacity
                    dstPixels[x] = ((int) (dstPixel[3] + (result[3] - dstPixel[3]) * alpha) & 0xFF) << 24 | ((int) (dstPixel[0] + (result[0] - dstPixel[0]) * alpha) & 0xFF) | ((int) (dstPixel[1] + (result[1] - dstPixel[1]) * alpha) & 0xFF) << 8 | ((int) (dstPixel[2] + (result[2] - dstPixel[2]) * alpha) & 0xFF) << 16;
                }
                dstOut.setDataElements(0, y, width, 1, dstPixels);
            }
        }
    }
}