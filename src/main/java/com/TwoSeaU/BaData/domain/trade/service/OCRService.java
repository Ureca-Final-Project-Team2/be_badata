package com.TwoSeaU.BaData.domain.trade.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.TwoSeaU.BaData.domain.trade.exception.TradeException;
import com.TwoSeaU.BaData.global.response.GeneralException;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.protobuf.ByteString;

@Service
public class OCRService {

	public Map<String, String> extractTextFromImageFile(final MultipartFile imageFile) throws Exception {
		final ByteString byteString = ByteString.readFrom(imageFile.getInputStream());

		final Image image = Image.newBuilder().setContent(byteString).build();
		final Feature feature = Feature.newBuilder().setType(Feature.Type.DOCUMENT_TEXT_DETECTION).build();

		final AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
			.addFeatures(feature)
			.setImage(image)
			.build();

		try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
			final List<AnnotateImageResponse> responses =
				client.batchAnnotateImages(List.of(request)).getResponsesList();

			for (AnnotateImageResponse res : responses) {
				if (res.hasError()) {
					throw new GeneralException(TradeException.OCR_PROCESSING_FAILED);
				}

				final String fullText = res.getTextAnnotationsList().get(0).getDescription();
				return parseGifticonInfo(fullText);
			}

			throw new GeneralException(TradeException.OCR_PROCESSING_FAILED);
		}
	}

	private Map<String, String> parseGifticonInfo(final String text) {
		final Map<String, String> result = new HashMap<>();
		final String[] lines = text.split("\n");

		String couponName = null;
		boolean couponFound = false;

		for (int i = 0; i < lines.length; i++) {
			final String line = lines[i].trim();

			if (line.contains("쿠폰쓰기") || line.contains("쿠폰 보내기") || line.contains("검색") ||
				line.contains("바꾸기") || line.contains("공유받은 쿠폰") || line.contains("구독 쿠폰")) {
				continue;
			}

			if (!couponFound && (line.contains("할인쿠폰") || (line.contains("쿠폰") && !line.matches(".*\\d+.*")))) {
				final String prevLine = (i > 0) ? lines[i - 1].trim() : "";
				if (!prevLine.isEmpty() && !prevLine.contains("쿠폰") && !prevLine.contains("쓰기")) {
					couponName = prevLine + " " + line;
				} else {
					couponName = line;
				}
				couponFound = true;
				result.put("couponName", couponName.trim());
				break;
			}
		}

		if (couponFound) {
			boolean expirationFound = false;
			boolean barcodeFound = false;

			for (int i = 0; i < lines.length; i++) {
				final String line = lines[i].trim();

				if (!expirationFound) {
					final Matcher dateMatcher = Pattern.compile("(20\\d{2}\\.\\d{2}\\.\\d{2})").matcher(line);
					if (dateMatcher.find()) {
						result.put("expirationDate", dateMatcher.group(1));
						expirationFound = true;
						continue;
					}
				}

				if (!barcodeFound) {
					final Matcher barcodeMatcher = Pattern.compile("\\b\\d{13,14}\\b").matcher(line);
					if (barcodeMatcher.find()) {
						result.put("barcode", barcodeMatcher.group());
						barcodeFound = true;
						continue;
					}
				}

				if (expirationFound && barcodeFound) {
					break;
				}
			}
		}

		return result;
	}
}