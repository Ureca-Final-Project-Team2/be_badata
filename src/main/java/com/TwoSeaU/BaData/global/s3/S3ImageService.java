package com.TwoSeaU.BaData.global.s3;

import com.TwoSeaU.BaData.global.exception.GlobalException;
import com.TwoSeaU.BaData.global.response.GeneralException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.util.Arrays;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class S3ImageService {

    private final AmazonS3Client amazonS3Client;
    private final static String[] extensionArr={"jpg","jpeg","bmp","gif","png"};

    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName;

    @Value("${cloud.aws.s3.bucketUrl}")
    private String url;


    //s3 저장
    public String saveImage(final MultipartFile file, final String directoryName,final String originalName) {
        try{

            final ObjectMetadata metadata = new ObjectMetadata();

            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getInputStream().available());

            final String storedName = generateStoreName(originalName);

            amazonS3Client.putObject(new PutObjectRequest(bucketName,directoryName+storedName,file.getInputStream(),metadata).withCannedAcl(
                    CannedAccessControlList.PublicRead));

            return amazonS3Client.getUrl(bucketName,directoryName+storedName).toString();

        }catch (Exception e){
            throw new GeneralException(GlobalException.INTERNAL_S3_ERROR);
        }
    }

    //s3 삭제
    public void deleteImage(final String fileRoute) {

        final int index = fileRoute.indexOf(url);
        final String fileName = fileRoute.substring(index + url.length() + 1);

        try {
            boolean isObjectExist = amazonS3Client.doesObjectExist(bucketName, fileName);
            if (isObjectExist) {
                amazonS3Client.deleteObject(bucketName, fileName);
            }
        } catch (Exception e) {
            throw new GeneralException(GlobalException.INTERNAL_S3_ERROR);
        }
    }


    //확장자 추출
    public String extractExtension(final String originalName){
        final int index=originalName.lastIndexOf('.');
        return originalName.substring(index+1);
    }

    //UUID 통한 accessUrl 생성 및 체크
    public String generateStoreName(final String originalName){
        final String extension=extractExtension(originalName);
        if(!checkValidation(extension)) throw new GeneralException(GlobalException.NOT_ALLOWABLE_EXTENSION);
        return UUID.randomUUID()+"."+extension;
    }


    //이미지 파일의 확장자를 통하여 유효한 이미지 파일인지 확인하는 메서드
    public boolean checkValidation(final String extension){
        return Arrays.stream(extensionArr).anyMatch(value->value.equals(extension));
    }







}

