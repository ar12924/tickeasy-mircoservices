package microservices.media.controller;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.net.URL;
import java.time.Duration;
import java.util.UUID;

@RestController
@RequestMapping("/api/media")
public class MediaController {

    @Autowired(required = false)
    private S3Presigner presigner;

    @Value("${app.s3.bucket:}")
    private String defaultBucket;

    private S3Presigner getPresigner() {
        if (presigner != null) return presigner;
        return S3Presigner.builder()
                .region(Region.of(System.getenv().getOrDefault("AWS_REGION", "ap-northeast-1")))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    @PostMapping("/presign-upload")
    public ResponseEntity<PresignResponse> presignUpload(@RequestBody PresignRequest req) {
        String bucket = (req.getBucket() != null && !req.getBucket().isEmpty()) ? req.getBucket() : (defaultBucket == null || defaultBucket.isEmpty() ? System.getenv().getOrDefault("S3_BUCKET", "") : defaultBucket);
        String key = (req.getKey() != null && !req.getKey().isEmpty()) ? req.getKey() : ("member-photos/" + UUID.randomUUID());
        var put = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(req.getContentType())
                .build();
        var presignReq = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .putObjectRequest(put)
                .build();
        URL url = getPresigner().presignPutObject(presignReq).url();
        return ResponseEntity.ok(new PresignResponse(url.toString(), key));
    }

    @PostMapping("/presign-download")
    public ResponseEntity<PresignResponse> presignDownload(@RequestBody PresignRequest req) {
        String bucket = (req.getBucket() != null && !req.getBucket().isEmpty()) ? req.getBucket() : (defaultBucket == null || defaultBucket.isEmpty() ? System.getenv().getOrDefault("S3_BUCKET", "") : defaultBucket);
        var get = GetObjectRequest.builder().bucket(bucket).key(req.getKey()).build();
        var presignReq = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .getObjectRequest(get)
                .build();
        URL url = getPresigner().presignGetObject(presignReq).url();
        return ResponseEntity.ok(new PresignResponse(url.toString(), req.getKey()));
    }

    @Data
    public static class PresignRequest {
        private String bucket;
        private String key;
        private String contentType;
    }

    @Data
    public static class PresignResponse {
        private final String url;
        private final String key;
    }
}


