package org.SitekickRemastered.utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

/* Found online through GitHub. Modified it so it can do Three way gradients */


/** @author Gerrit Grunwald <han.solo at muenster.de> */
public final class BiLinearGradientPaint implements java.awt.Paint {

    private static final float INT_TO_FLOAT_CONST = 1f / 255f;
    private final java.awt.Rectangle BOUNDS;
    private final Paint COLOR_00;
    private final Paint COLOR_10;
    private final Paint COLOR_01;
    private final Paint COLOR_11;
    private final float FRACTION_X_STEPSIZE;
    private final float FRACTION_Y_STEPSIZE;
    private int titleBarHeight;


    /**
     * Enhanced constructor which takes bounds of the objects SHAPE to fill and the four
     * colors we need to create the bilinear interpolated gradient
     *
     * @param COLOR_00 The color in the lower left corner of the square
     * @param COLOR_10 The color in the lower right corner of the square
     * @param COLOR_01 The color in the upper left corner of the square
     * @param COLOR_11 The color in the upper right corner of the square
     */
    public BiLinearGradientPaint(final BufferedImage image, final Paint COLOR_00, final Paint COLOR_10, final Paint COLOR_01, final Paint COLOR_11) throws IllegalArgumentException {
        // Set the values
        this.BOUNDS = image.getData().getBounds();
        this.COLOR_00 = COLOR_00;
        this.COLOR_10 = COLOR_10;
        this.COLOR_01 = COLOR_01;
        this.COLOR_11 = COLOR_11;
        this.FRACTION_X_STEPSIZE = 1.0f / (BOUNDS.getBounds().width);
        this.FRACTION_Y_STEPSIZE = 1.0f / (BOUNDS.getBounds().height);
        if (COLOR_01 != null)
            this.titleBarHeight = -1;
        else
            this.titleBarHeight = (BOUNDS.getBounds().height / 5);
    }


    /**
     * Enhanced constructor which takes bounds of the objects SHAPE to fill and the four
     * colors we need to create the bilinear interpolated gradient
     *
     * @param COLOR_00 The color in the lower middle corner of the square
     * @param COLOR_01 The color in the upper left corner of the square
     * @param COLOR_11 The color in the upper right corner of the square
     */
    public BiLinearGradientPaint(final BufferedImage image, final Paint COLOR_00, final Paint COLOR_01, final Paint COLOR_11) throws IllegalArgumentException {
        this(image, COLOR_00, COLOR_01, null, COLOR_11);
    }


    @Override
    public java.awt.PaintContext createContext(final java.awt.image.ColorModel COLOR_MODEL, final java.awt.Rectangle DEVICE_BOUNDS, final java.awt.geom.Rectangle2D USER_BOUNDS, final java.awt.geom.AffineTransform TRANSFORM, final java.awt.RenderingHints HINTS) {
        return new BiLinearGradientPaintContext();
    }


    @Override
    public int getTransparency() {
        return java.awt.Transparency.TRANSLUCENT;
    }


    private final class BiLinearGradientPaintContext implements java.awt.PaintContext {

        public BiLinearGradientPaintContext() {
        }


        /**
         * Returns the interpolated color that you get if you multiply the delta between
         * color2 and color1 with the given fraction (for each channel). The fraction should
         * be a value between 0 and 1.
         *
         * @param COLOR1   The first color as integer in the hex format 0xALPHA RED GREEN BLUE, e.g. 0xFF00FF00 for a pure green
         * @param COLOR2   The second color as integer in the hex format 0xALPHA RED GREEN BLUE e.g. 0xFFFF0000 for a pure red
         * @param fraction The fraction between those two colors that we would like to get e.g. 0.5f will result in the color 0xFF808000
         * @return the interpolated color between color1 and color2 calculated by the given fraction
         */
        private Color interpolateColor(final Color COLOR1, final Color COLOR2, float fraction) {
            fraction = Math.min(fraction, 1f);
            fraction = Math.max(fraction, 0f);

            final float RED1 = COLOR1.getRed() * INT_TO_FLOAT_CONST;
            final float GREEN1 = COLOR1.getGreen() * INT_TO_FLOAT_CONST;
            final float BLUE1 = COLOR1.getBlue() * INT_TO_FLOAT_CONST;
            final float ALPHA1 = COLOR1.getAlpha() * INT_TO_FLOAT_CONST;

            final float RED2 = COLOR2.getRed() * INT_TO_FLOAT_CONST;
            final float GREEN2 = COLOR2.getGreen() * INT_TO_FLOAT_CONST;
            final float BLUE2 = COLOR2.getBlue() * INT_TO_FLOAT_CONST;
            final float ALPHA2 = COLOR2.getAlpha() * INT_TO_FLOAT_CONST;

            final float DELTA_RED = RED2 - RED1;
            final float DELTA_GREEN = GREEN2 - GREEN1;
            final float DELTA_BLUE = BLUE2 - BLUE1;
            final float DELTA_ALPHA = ALPHA2 - ALPHA1;

            float red = RED1 + (DELTA_RED * fraction);
            float green = GREEN1 + (DELTA_GREEN * fraction);
            float blue = BLUE1 + (DELTA_BLUE * fraction);
            float alpha = ALPHA1 + (DELTA_ALPHA * fraction);

            red = Math.min(red, 1f);
            red = Math.max(red, 0f);
            green = Math.min(green, 1f);
            green = Math.max(green, 0f);
            blue = Math.min(blue, 1f);
            blue = Math.max(blue, 0f);
            alpha = Math.min(alpha, 1f);
            alpha = Math.max(alpha, 0f);

            return new Color(red, green, blue, alpha);
        }


        /**
         * Returns the color calculated by a bilinear interpolation by the two fractions in x and y direction.
         * To get the color of the point defined by FRACTION_X and FRACTION_Y with in the rectangle defined by the
         * for given colors we first calculate the interpolated color between COLOR_00 and COLOR_10 (x-direction) with
         * the given FRACTION_X. After that we calculate the interpolated color between COLOR_01 and COLOR_11 (x-direction)
         * with the given FRACTION_X. Now we interpolate between the two results of the former calculations (y-direction)
         * with the given FRACTION_Y.
         *
         * @param COLOR_00   The color in the lower left corner of the square
         * @param COLOR_10   The color in the lower right corner of the square
         * @param COLOR_01   The color in the upper left corner of the square
         * @param COLOR_11   The color in the upper right corner of the square
         * @param FRACTION_X The fraction of the point in x direction (between COLOR_00 and COLOR_10 or COLOR_01 and COLOR_11) range: 0.0f .. 1.0f
         * @param FRACTION_Y The fraction of the point in y direction (between COLOR_00 and COLOR_01 or COLOR_10 and COLOR_11) range: 0.0f .. 1.0f
         * @return the color of the point defined by fraction_x and fraction_y in the square defined by the for colors
         */
        private Color bilinearInterpolateColor(final Paint COLOR_00, final Paint COLOR_10, final Paint COLOR_01, final Paint COLOR_11, final float FRACTION_X, final float FRACTION_Y) {
            final Color INTERPOLATED_COLOR_X1 = interpolateColor((Color) COLOR_00, (Color) COLOR_10, FRACTION_X);
            final Color INTERPOLATED_COLOR_X2 = interpolateColor((Color) COLOR_01, (Color) COLOR_11, FRACTION_X);
            return interpolateColor(INTERPOLATED_COLOR_X1, INTERPOLATED_COLOR_X2, FRACTION_Y);
        }


        private Color bilinearInterpolateColor(final Paint COLOR_00, final Paint COLOR_01, final Paint COLOR_11, final float FRACTION_X, final float FRACTION_Y) {
            final Color INTERPOLATED_COLOR_X1 = interpolateColor((Color) COLOR_00, (Color) COLOR_01, FRACTION_X);
            return interpolateColor(INTERPOLATED_COLOR_X1, (Color) COLOR_11, FRACTION_Y);
        }


        @Override
        public void dispose() {
        }


        @Override
        public java.awt.image.ColorModel getColorModel() {
            return java.awt.image.ColorModel.getRGBdefault();
        }


        @Override
        public java.awt.image.Raster getRaster(final int X, final int Y, final int TILE_WIDTH, final int TILE_HEIGHT) {
            // Get the offset given by the height of the titlebar
            if (titleBarHeight == -1) {
                titleBarHeight = Y;
            }

            // Create raster for given colormodel
            final WritableRaster RASTER = getColorModel().createCompatibleWritableRaster(TILE_WIDTH, TILE_HEIGHT);

            // Create data array with place for red, green, blue and alpha values
            final int[] DATA = new int[(TILE_WIDTH * TILE_HEIGHT * 4)];
            Color currentColor;

            float fraction_x = (X - BOUNDS.x) * FRACTION_X_STEPSIZE;
            float fraction_y = (Y - BOUNDS.y - titleBarHeight) * FRACTION_Y_STEPSIZE;

            fraction_x = Math.min(fraction_x, 1f);
            fraction_y = Math.min(fraction_y, 1f);

            for (int tileY = 0; tileY < TILE_HEIGHT; tileY++) {
                for (int tileX = 0; tileX < TILE_WIDTH; tileX++) {
                    if (COLOR_01 != null)
                        currentColor = bilinearInterpolateColor(COLOR_00, COLOR_10, COLOR_01, COLOR_11, fraction_x, fraction_y);
                    else
                        currentColor = bilinearInterpolateColor(COLOR_00, COLOR_10, COLOR_11, fraction_x, fraction_y);

                    fraction_x += FRACTION_X_STEPSIZE;
                    fraction_x = Math.min(fraction_x, 1f);

                    // Fill data array with calculated color values
                    final int BASE = (tileY * TILE_WIDTH + tileX) * 4;
                    DATA[BASE + 0] = currentColor.getRed();
                    DATA[BASE + 1] = currentColor.getGreen();
                    DATA[BASE + 2] = currentColor.getBlue();
                    DATA[BASE + 3] = currentColor.getAlpha();
                }
                fraction_x = (X - BOUNDS.x) * FRACTION_X_STEPSIZE;
                fraction_y += FRACTION_Y_STEPSIZE;
                fraction_y = Math.min(fraction_y, 1f);
            }

            // Fill the raster with the data
            RASTER.setPixels(0, 0, TILE_WIDTH, TILE_HEIGHT, DATA);

            return RASTER;
        }
    }


    @Override
    public String toString() {
        return "BiLinearGradientPaint";
    }
}