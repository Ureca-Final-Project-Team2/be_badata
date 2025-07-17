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

    public ELAResult analyzeImage(MultipartFile file) {
        try {
            BufferedImage originalImage = ImageIO.read(file.getInputStream());

            BufferedImage recompressedImage = recompressJPEG(originalImage);

            BufferedImage differenceImage = calculateDifference(originalImage, recompressedImage);

            List<SuspiciousRegion> suspiciousRegions = detectSuspiciousRegions(differenceImage);

            double manipulationScore = calculateManipulationScore(differenceImage);

            return new ELAResult(suspiciousRegions, manipulationScore);

        } catch (IOException e) {
            throw new GeneralException(TradeException.ELA_IMAGE_PROCESSING_FAILED);
        }
    }

    private BufferedImage recompressJPEG(BufferedImage image) throws IOException {
        File tempFile = File.createTempFile("ela_temp", ".jpg");
        tempFile.deleteOnExit();

        ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
        ImageWriteParam param = writer.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality((float) QUALITY_FACTOR);

        // 이미지 저장
        try (ImageOutputStream ios = ImageIO.createImageOutputStream(tempFile)) {
            writer.setOutput(ios);
            writer.write(null, new javax.imageio.IIOImage(image, null, null), param);
        }
        writer.dispose();

        return ImageIO.read(tempFile);
    }

    private BufferedImage calculateDifference(BufferedImage original, BufferedImage recompressed) {
        int width = original.getWidth();
        int height = original.getHeight();
        BufferedImage difference = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

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

    private List<SuspiciousRegion> detectSuspiciousRegions(BufferedImage difference) {
        List<SuspiciousRegion> regions = new ArrayList<>();
        int width = difference.getWidth();
        int height = difference.getHeight();

        int blockSize = 8;
        for (int y = 0; y < height - blockSize; y += blockSize) {
            for (int x = 0; x < width - blockSize; x += blockSize) {
                double avgDifference = calculateBlockAverage(difference, x, y, blockSize);

                if (avgDifference > SUSPICIOUS_THRESHOLD) {
                    regions.add(SuspiciousRegion.of(x, y, blockSize, blockSize, avgDifference));
                }
            }
        }

        return regions;
    }

    private double calculateBlockAverage(BufferedImage image, int startX, int startY, int blockSize) {
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

    private double calculateManipulationScore(BufferedImage difference) {
        int width = difference.getWidth();
        int height = difference.getHeight();
        int modifiedPixels = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = new Color(difference.getRGB(x, y));
                int grayValue = color.getRed();

                if (grayValue > 1) {
                    modifiedPixels++;
                }
            }
        }

        if (modifiedPixels > 0) {
            double modifiedRatio = (double) modifiedPixels / (width * height);
            return Math.min(100, 50 + (modifiedRatio * 1000));
        }

        return 0;
    }
}
