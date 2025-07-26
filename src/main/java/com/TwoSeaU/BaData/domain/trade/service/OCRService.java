package com.TwoSeaU.BaData.domain.trade.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.TwoSeaU.BaData.domain.trade.dto.OCRResult;
import com.TwoSeaU.BaData.domain.trade.entity.Partner;
import com.TwoSeaU.BaData.domain.trade.exception.TradeException;
import com.TwoSeaU.BaData.domain.trade.repository.PartnerRepository;
import com.TwoSeaU.BaData.global.response.GeneralException;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.protobuf.ByteString;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OCRService {

	private final PartnerRepository partnerRepository;

	public OCRResult extractTextFromImageFile(final MultipartFile imageFile) {
		try {
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

					if (res.getTextAnnotationsList().isEmpty()) {
						throw new GeneralException(TradeException.OCR_PROCESSING_FAILED);
					}
					final String fullText = res.getTextAnnotationsList().get(0).getDescription();

					return parseGifticonInfo(fullText);
				}

				throw new GeneralException(TradeException.OCR_PROCESSING_FAILED);
			}
		} catch (Exception e) {
			throw new GeneralException(TradeException.CANNOT_READ_FROM_IMAGE);
		}
	}

	private OCRResult parseGifticonInfo(final String text) {
		final Map<String, String> result = new HashMap<>();
		final String[] lines = text.split("\n");

		boolean couponFound = false;
		int startIndex = 0;

		for (int i = 0; i < lines.length; i++) {
			final String line = lines[i].trim();

			if (line.contains("쿠폰쓰기") || line.contains("쿠폰 보내기") || line.contains("검색") ||
				line.contains("바꾸기 가능 쿠폰") || line.contains("공유받은 쿠폰") || line.contains("구독 쿠폰") ||
				line.matches("쿠폰 .*\\d+.*")) {
				continue;
			}

			if (!couponFound && (line.contains("할인쿠폰") || (line.contains("쿠폰")))) {
				final StringBuilder nameBuilder = new StringBuilder();

				for(int j=i-2; j<i; j++) {
					if(j>=0 && !lines[j].isEmpty() && !lines[j].contains("쿠폰") && lines[j].length() >= 10) {
						nameBuilder.append(lines[j].trim());
					}
				}
				nameBuilder.append(line.trim());

				couponFound = true;
				final String couponName = nameBuilder.toString().trim();

				final List<Partner> partnerList = partnerRepository.findAll();
				final Optional<Partner> matchedPartner = partnerList.stream()
					.filter(p -> couponName.contains(p.getPartner()))
					.findFirst();

				result.put("couponName", couponName);

				if(matchedPartner.isPresent()) {
					Partner partner = matchedPartner.get();
					result.put("partner", partner.getPartner());
				} else {
					throw new GeneralException(TradeException.NOT_FOUND_GIFTICON_PARTNER);
				}

				startIndex = i;
				break;
			}
		}

		if (couponFound) {
			boolean expirationFound = false;
			boolean barcodeFound = false;

			for (int i = startIndex; i < lines.length; i++) {
				final String line = lines[i].trim();

				if (!expirationFound) {
					final Matcher dateMatcher = Pattern.compile("(20\\d{2}\\.\\d{2}\\.\\d{2})").matcher(line);
					if (dateMatcher.find()) {
						final String expirationDate = dateMatcher.group(1);

						DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
						LocalDate extractedDate = LocalDate.parse(expirationDate, formatter);
						LocalDate now = LocalDate.now();

						if(extractedDate.isBefore(now)) {
							throw new GeneralException(TradeException.EXPIRED_EXPIRATION_DATE);
						}

						result.put("expirationDate", expirationDate);
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
		return OCRResult.of(result.get("couponName"), result.get("partner"), result.get("expirationDate"), result.get("barcode"));
	}
}