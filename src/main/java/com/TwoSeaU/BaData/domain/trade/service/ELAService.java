package com.TwoSeaU.BaData.domain.trade.service;

import com.TwoSeaU.BaData.domain.trade.dto.ELAResult;
import com.TwoSeaU.BaData.domain.trade.dto.SuspiciousRegion;
import com.TwoSeaU.BaData.domain.trade.exception.TradeException;
import com.TwoSeaU.BaData.global.response.GeneralException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

@Service
public class ELAService {
    private static final double QUALITY_FACTOR = 0.90;
    private static final int SUSPICIOUS_THRESHOLD = 1;
    private static final int BLOCK_SIZE = 8;

    public ELAResult analyzeImage(final MultipartFile file) {
        try {
            final BufferedImage originalImage = ImageIO.read(file.getInputStream());

            final BufferedImage recompressedImage = recompressJPEG(originalImage);

            final BufferedImage differenceImage = calculateDifference(originalImage, recompressedImage);

            final List<SuspiciousRegion> suspiciousRegions = detectSuspiciousRegions(differenceImage);

            final double manipulationScore = calculateManipulationScore(differenceImage);

            int tot = ((differenceImage.getWidth() - BLOCK_SIZE) / BLOCK_SIZE) * ((differenceImage.getHeight() - BLOCK_SIZE) / BLOCK_SIZE);

            return new ELAResult(suspiciousRegions, manipulationScore, (suspiciousRegions.size() / (double) tot) * 100);

        } catch (IOException e) {
            throw new GeneralException(TradeException.ELA_IMAGE_PROCESSING_FAILED);
        }
    }

    private BufferedImage recompressJPEG(final BufferedImage image) throws IOException {
        final File tempFile = File.createTempFile("ela_temp", ".jpg");

        try {
            ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
            ImageWriteParam param = writer.getDefaultWriteParam();
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality((float) QUALITY_FACTOR);

            try (ImageOutputStream ios = ImageIO.createImageOutputStream(tempFile)) {
                writer.setOutput(ios);
                writer.write(null, new javax.imageio.IIOImage(image, null, null), param);
            }

            writer.dispose();

            return ImageIO.read(tempFile);
        }
        catch (Exception e) {
            throw new GeneralException(TradeException.ELA_IMAGE_PROCESSING_FAILED);
        }
        finally {
            tempFile.deleteOnExit();
        }
    }

    private BufferedImage calculateDifference(final BufferedImage original, final BufferedImage recompressed) {
        final int width = original.getWidth();
        final int height = original.getHeight();
        final BufferedImage difference = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color origColor = new Color(original.getRGB(x, y));
                Color recompColor = new Color(recompressed.getRGB(x, y));

                int rDiff = Math.abs(origColor.getRed() - recompColor.getRed());
                int gDiff = Math.abs(origColor.getGreen() - recompColor.getGreen());
                int bDiff = Math.abs(origColor.getBlue() - recompColor.getBlue());

                int avgDiff = (rDiff + gDiff + bDiff) / 3;

                difference.setRGB(x, y, new Color(avgDiff, avgDiff, avgDiff).getRGB());
            }
        }

        return difference;
    }

    private List<SuspiciousRegion> detectSuspiciousRegions(final BufferedImage difference) {
        final List<SuspiciousRegion> regions = new ArrayList<>();
        final int width = difference.getWidth();
        final int height = difference.getHeight();

        for (int y = 0; y < height - BLOCK_SIZE; y += BLOCK_SIZE) {
            for (int x = 0; x < width - BLOCK_SIZE; x += BLOCK_SIZE) {
                double avgDifference = calculateBlockAverage(difference, x, y, BLOCK_SIZE);

                if (avgDifference > SUSPICIOUS_THRESHOLD) {
                    regions.add(SuspiciousRegion.of(x, y, BLOCK_SIZE, BLOCK_SIZE, avgDifference));
                }
            }
        }
        return regions;
    }

    private double calculateBlockAverage(final BufferedImage image, final int startX, final int startY, final int blockSize) {
        double sum = 0;
        int count = 0;

        for (int y = startY; y < startY + blockSize && y < image.getHeight(); y++) {
            for (int x = startX; x < startX + blockSize && x < image.getWidth(); x++) {
                Color color = new Color(image.getRGB(x, y));
                sum += color.getRed();
                count++;
            }
        }

        return count > 0 ? sum / count : 0;
    }

    private static double calculateManipulationScore(BufferedImage difference) {
        long totalDifference = 0;
        int width = difference.getWidth();
        int height = difference.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = new Color(difference.getRGB(x, y));
                totalDifference += color.getRed();
            }
        }

        double avgDifference = (double) totalDifference / (width * height);
        return Math.min(100, (avgDifference / 255.0) * 100);
    }
}
