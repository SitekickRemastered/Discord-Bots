package org.SitekickRemastered.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class CreatePFP {

    /**
     * This function builds the image. It applies all modifications step by step.
     *
     * @return - Returns the completed picture
     */
    public static BufferedImage buildImage() throws Exception {

        User cu = Helpers.currentUser;

        Paint colour = cu.getBodyColour();
        String eyeImage = cu.getEyePic();
        Paint customEyeColour = cu.getCustomEyeColour();

        // Clear section lists if the user selected stuff
        if (colour != null)
            cu.clearBSL();

        if (!eyeImage.equals("CustomEyes.png") || customEyeColour != null)
            cu.clearESL();

        // Get all the images needed
        BufferedImage bg = ImageIO.read(CreatePFP.class.getResource("/pfpImages/background.png"));
        BufferedImage body = ImageIO.read(CreatePFP.class.getResource("/pfpImages/body.png"));
        BufferedImage eyes = ImageIO.read(CreatePFP.class.getResource("/pfpImages/eyes/" + eyeImage));
        BufferedImage ti = ImageIO.read(CreatePFP.class.getResource("/pfpImages/top_image.png"));
        BufferedImage pfp = new BufferedImage(bg.getWidth(), bg.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = pfp.createGraphics();
        AffineTransform old = g.getTransform();
        g.drawImage(bg, 0, 0, null);

        // Translate first so the inputted values aren't modified by the rotation
        applyZoom(cu, g);
        applyRotation(cu, g);
        applyFlip(cu, g);
        applyTranslation(cu, g);

        // If the bodySectionList isn't empty, then we build the body using the sections instead of dyeing the body.
        if (!cu.getBSL().isEmpty())
            applyBodySections(cu, g);
        else {
            body = dyeImage(body, colour, BlendComposite.Subtract);
            g.drawImage(body, 0, 0, null);
        }

        // If the current eyes are hollow eyes, get rid of messed up transparency stuff and multiply instead of burn
        if (eyeImage.equals("HollowEyes.png"))
            eyes = fixShit(eyes, dyeImage(eyes, colour, BlendComposite.Multiply));

        // If the current eyes are custom eyes, get rid of messed up transparency and use linear burn
        if (eyeImage.equals("CustomEyes.png"))
            eyes = fixShit(eyes, dyeImage(eyes, customEyeColour, BlendComposite.Subtract));

        // If the user uses the deadkick preset, fix the eyes and subtract a specific colour
        if (cu.getDeadkick())
            eyes = fixShit(eyes, dyeImage(eyes, new Color(136, 117, 60), BlendComposite.Subtract));

        // If the eyeSectionList isn't empty, then we build the eyes using the sections instead of dyeing the eyes.
        if (!cu.getESL().isEmpty())
            applyEyeSections(cu, g);
        else {
            g.drawImage(eyes, 0, 0, null);

            // Add the shine to the custom eyes
            if (eyeImage.equals("CustomEyes.png"))
                g.drawImage(ImageIO.read(CreatePFP.class.getResource("/pfpImages/eyes/shine.png")), 0, 0, null);
        }

        // Apply the old transformation information so the next transformations are easier to do.
        g.setTransform(old);
        g.drawImage(ti, 0, 0, null);

        if (cu.getDeadkick())
            cu.resetDefault();

        return pfp;
    }


    /**
     * This function dyes the image, so it has the correct colour given.
     * If we're dyeing a body, we use Linear Burn, otherwise we use Multiply.
     *
     * @param image  - The image that is to be coloured
     * @param colour - The color that the image will be changed to
     * @param type   - The type of Blend Composite we will use - either Linear Burn or Multiply
     * @return - returns the dyed image.
     */
    public static BufferedImage dyeImage(BufferedImage image, Paint colour, BlendComposite type) {
        BufferedImage dyed = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = dyed.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.setComposite(type);
        g.setPaint(colour);
        g.fillRect(0, 0, image.getWidth(), image.getHeight());
        g.dispose();
        return dyed;
    }


    /**
     * This function takes two images, and copies the transparent pixels from one to the other.
     * This is so hollow eyes and sections don't break and look bad.
     *
     * @param mask - The image you are copying transparent pixels from
     * @param img  - The image you want to put transparent pixels in
     * @return - Returns the image with transparent pixels.
     */
    public static BufferedImage fixShit(BufferedImage mask, BufferedImage img) {
        int width = mask.getWidth();
        int height = mask.getHeight();

        BufferedImage processed = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int rgb = mask.getRGB(x, y);
                int maskAlpha = alpha(rgb);
                int imgColor = img.getRGB(x, y);
                if (maskAlpha < 255)
                    processed.setRGB(x, y, maskAlpha(imgColor, maskAlpha));
                else
                    processed.setRGB(x, y, imgColor);
            }
        }

        return processed;
    }


    public static int maskAlpha(int rgb, int alpha) {
        return (0x00ffffff & rgb) + ((alpha) << 24);
    }


    public static int alpha(int rgb) {
        return (0xff & (rgb >> 24));
    }


    public static void applyZoom(User cu, Graphics2D g) {
        if (cu.getZA() != 0) {
            double imageScale = (1 + (cu.getZA() * 0.01));
            double newImageSize = Helpers.imageSize * imageScale;
            int xyDifference = (int) ((Helpers.imageSize - newImageSize) / 2);
            g.scale(imageScale, imageScale);
            g.translate(xyDifference, xyDifference);
        }
    }


    public static void applyRotation(User cu, Graphics2D g) {
        if (cu.getRA() != 0)
            g.rotate(Math.toRadians(360 - (cu.getRA() % 360)), Helpers.imageSize / 2, Helpers.imageSize / 2);
    }


    public static void applyFlip(User cu, Graphics2D g) {
        if (cu.getFlipType() != 0) {

            //Flip Horizontally
            if (cu.getFlipType() == 1) {
                g.translate(Helpers.imageSize, 0);
                g.scale(-1, 1);
            }

            //Flip Vertically
            else if (cu.getFlipType() == 2) {
                g.translate(0, Helpers.imageSize);
                g.scale(1, -1);
            }

            //Flip Both Horizontally and Vertically
            else {
                g.translate(Helpers.imageSize, Helpers.imageSize);
                g.scale(-1, -1);
            }
        }
    }


    public static void applyTranslation(User cu, Graphics2D g) {
        if (cu.getTA()[0] != 0 || cu.getTA()[1] != 0)
            g.translate(cu.getTA()[0], cu.getTA()[1]);
    }


    public static void applyBodySections(User cu, Graphics2D g) throws IOException {
        BufferedImage s1 = ImageIO.read(CreatePFP.class.getResource("/pfpImages/s1.png"));
        BufferedImage s2 = ImageIO.read(CreatePFP.class.getResource("/pfpImages/s2.png"));
        BufferedImage s3 = ImageIO.read(CreatePFP.class.getResource("/pfpImages/s3.png"));
        BufferedImage s4 = ImageIO.read(CreatePFP.class.getResource("/pfpImages/s4.png"));
        BufferedImage s5 = ImageIO.read(CreatePFP.class.getResource("/pfpImages/s5.png"));
        BufferedImage lines = ImageIO.read(CreatePFP.class.getResource("/pfpImages/lines.png"));
        BufferedImage outline = ImageIO.read(CreatePFP.class.getResource("/pfpImages/outline.png"));
        s1 = fixShit(s1, dyeImage(s1, cu.getBSL().get(0), BlendComposite.Subtract));
        s2 = fixShit(s2, dyeImage(s2, cu.getBSL().get(1), BlendComposite.Subtract));
        s3 = fixShit(s3, dyeImage(s3, cu.getBSL().get(2), BlendComposite.Subtract));
        s4 = fixShit(s4, dyeImage(s4, cu.getBSL().get(3), BlendComposite.Subtract));
        s5 = fixShit(s5, dyeImage(s5, cu.getBSL().get(4), BlendComposite.Subtract));
        lines = fixShit(lines, dyeImage(lines, cu.getBSL().get(5), BlendComposite.Subtract));
        g.drawImage(s1, 0, 0, null);
        g.drawImage(s2, 0, 0, null);
        g.drawImage(s3, 0, 0, null);
        g.drawImage(s4, 0, 0, null);
        g.drawImage(s5, 0, 0, null);

        // This is for deadkick ONLY
        if (cu.getDeadkick()) {
            lines = ImageIO.read(CreatePFP.class.getResource("/pfpImages/deadkickLines.png"));
            BufferedImage ear = ImageIO.read(CreatePFP.class.getResource("/pfpImages/deadkickEar.png"));
            BufferedImage shadow = ImageIO.read(CreatePFP.class.getResource("/pfpImages/deadkickShadow.png"));
            g.drawImage(shadow, 0, 0, null);
            g.drawImage(ear, 0, 0, null);
        }

        g.drawImage(lines, 0, 0, null);
        g.drawImage(outline, 0, 0, null);

    }


    public static void applyEyeSections(User cu, Graphics2D g) throws IOException {
        BufferedImage s1 = ImageIO.read(CreatePFP.class.getResource("/pfpImages/eyes/s1.png"));
        BufferedImage s2 = ImageIO.read(CreatePFP.class.getResource("/pfpImages/eyes/s2.png"));
        BufferedImage s3 = ImageIO.read(CreatePFP.class.getResource("/pfpImages/eyes/s3.png"));
        BufferedImage s4 = ImageIO.read(CreatePFP.class.getResource("/pfpImages/eyes/s4.png"));
        BufferedImage shine = ImageIO.read(CreatePFP.class.getResource("/pfpImages/eyes/shine.png"));
        BufferedImage outline = ImageIO.read(CreatePFP.class.getResource("/pfpImages/eyes/outline.png"));
        s1 = fixShit(s1, dyeImage(s1, cu.getESL().get(0), BlendComposite.Subtract));
        s2 = fixShit(s2, dyeImage(s2, cu.getESL().get(1), BlendComposite.Subtract));
        s3 = fixShit(s3, dyeImage(s3, cu.getESL().get(2), BlendComposite.Subtract));
        s4 = fixShit(s4, dyeImage(s4, cu.getESL().get(3), BlendComposite.Subtract));
        shine = fixShit(shine, dyeImage(shine, cu.getESL().get(4), BlendComposite.Subtract));
        g.drawImage(s1, 0, 0, null);
        g.drawImage(s2, 0, 0, null);
        g.drawImage(s3, 0, 0, null);
        g.drawImage(s4, 0, 0, null);
        g.drawImage(shine, 0, 0, null);
        g.drawImage(outline, 0, 0, null);
    }
}

