package com.sparta.backend.workspace.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;
import com.sparta.backend.workspace.exception.S3ErrorCode;
import com.sparta.backend.workspace.exception.S3Exception;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import java.net.URL;    //Url,Path

@RequiredArgsConstructor
@Component
@Slf4j
public class S3ImageService {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName;

    public String upload(MultipartFile image) {
        //입력받은 이미지 파일이 빈 파일인지 검증
        if(image.isEmpty() || Objects.isNull(image.getOriginalFilename())){
            throw new S3Exception(S3ErrorCode.EMPTY_FILE_EXCEPTION);
        }
        //uploadImage를 호출하여 S3에 저장된 이미지의 public url을 반환한다.
        return this.uploadImage(image);
    }

    private String uploadImage(MultipartFile image) {
        this.validateImageFileExtention(image.getOriginalFilename());   //validateImageFileExtention호출해서 확장자 명이 올바른지 확인함.
        try {
            return this.uploadImageToS3(image); //uploadImageToS3() 호출하여 이미지를 S3에 업로드하고, S3에 저장된 이미지의 public url을 받아서 서비스 로직에 반환한다.
        } catch (IOException e) {
            throw new S3Exception(S3ErrorCode.IO_EXCEPTION_ON_IMAGE_UPLOAD);
        }
    }
    //filename을 받아서 파일 확장자가 jpg, jpeg, png, gif 중에 속하는지 검증한다.
    private void validateImageFileExtention(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            throw new S3Exception(S3ErrorCode.NO_FILE_EXTENTION);
        }

        String extention = filename.substring(lastDotIndex + 1).toLowerCase();
        List<String> allowedExtentionList = Arrays.asList("jpg", "jpeg", "png", "gif");

        if (!allowedExtentionList.contains(extention)) {
            throw new S3Exception(S3ErrorCode.INVALID_FILE_EXTENTION);
        }
    }
    //직접적으로 S3에 업로드하는 메서드
    private String uploadImageToS3(MultipartFile image) throws IOException {
        String originalFilename = image.getOriginalFilename(); //원본 파일 명
        String extention = originalFilename.substring(originalFilename.lastIndexOf(".")); //확장자 명

        String s3FileName = UUID.randomUUID().toString().substring(0, 10) + originalFilename; //변경된 파일 명

        InputStream is = image.getInputStream(); //파일의 입력 스트림을 얻는다??
        byte[] bytes = IOUtils.toByteArray(is); //image를 byte[]로 변환

        ObjectMetadata metadata = new ObjectMetadata(); //metadata 생성
        metadata.setContentType("image/" + extention);
        metadata.setContentLength(bytes.length);

        //S3에 요청할 때 사용할 byteInputStream 생성
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

        try{
            // 추가된 로그
            log.info("Uploading to S3 with bucket: {} and file name: {}", bucketName, s3FileName);
            //S3로 putObject 할 때 사용할 요청 객체
            //생성자 : bucket 이름, 파일 명, byteInputStream, metadata
            PutObjectRequest putObjectRequest =
                    new PutObjectRequest(bucketName, s3FileName, byteArrayInputStream, metadata);
//                            .withCannedAcl(CannedAccessControlList.PublicRead);
// 이 부분때문에 오류남, 버킷 설정이 acl을 지원하지 않아서 그렇다는데, 그래서 버킷 정책을 변경해줌(퍼블릭 읽기 권한을 부여하기 위해 S3 버킷 정책을 사용함)
//버킷 정책에서 권한을 부여하기 때문에 위 코드처럼 추가적인 ACL을 사용하는 요청은 필요없음.

            //실제로 S3에 이미지 데이터를 넣는 부분이다.
            amazonS3.putObject(putObjectRequest); // put image to S3
            // 추가된 로그
            log.info("Upload successful");
        }catch (Exception e){
            // 추가된 로그
            log.error("Error uploading to S3", e);
            throw new S3Exception(S3ErrorCode.PUT_OBJECT_EXCEPTION);
        }finally {
            byteArrayInputStream.close();
            is.close();
        }

        return amazonS3.getUrl(bucketName, s3FileName).toString();
    }
    //이미지 삭제 / 이미지의 public url을 이용하여 S3에서 해당 이미지를 제거하는 메서드
    public void deleteImageFromS3(String imageAddress){
        String key = getKeyFromImageAddress(imageAddress);  //getKeyFromImageAddress을 호출해서 삭제에 필요한 key를 얻는다.
        try{
            amazonS3.deleteObject(new DeleteObjectRequest(bucketName, key));
        }catch (Exception e){
            throw new S3Exception(S3ErrorCode.IO_EXCEPTION_ON_IMAGE_DELETE);
        }
    }

    private String getKeyFromImageAddress(String imageAddress){
        try{
            URL url = new URL(imageAddress);
            String decodingKey = URLDecoder.decode(url.getPath(), "UTF-8");
            return decodingKey.substring(1); // 맨 앞의 '/' 제거
        }catch (MalformedURLException | UnsupportedEncodingException e){
            throw new S3Exception(S3ErrorCode.IO_EXCEPTION_ON_IMAGE_DELETE);
        }
    }
}






