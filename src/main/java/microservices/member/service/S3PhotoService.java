package microservices.member.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.net.URL;
import java.time.Duration;
import java.util.UUID;

@Service
public class S3PhotoService {

    @Value("${app.s3.bucket:}")
    private String bucket;

    private final org.springframework.web.client.RestTemplate restTemplate;

    public S3PhotoService(org.springframework.web.client.RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String uploadBytes(byte[] bytes, String contentType) {
        // 建議前端改走預簽名 PUT；此方法暫保留原簽名 key 產生
        return "member-photos/" + UUID.randomUUID();
    }

    public URL generateReadUrl(String key, Duration ttl) {
        try {
            var mediaEndpoint = System.getenv().getOrDefault("MEDIA_PRESIGN_DOWNLOAD", "http://localhost:8080/api/media/presign-download");
            var body = new java.util.HashMap<String, Object>();
            body.put("bucket", bucket);
            body.put("key", key);
            var resp = restTemplate.postForEntity(mediaEndpoint, body, java.util.Map.class);
            Object url = resp.getBody() != null ? resp.getBody().get("url") : null;
            return url != null ? new URL(url.toString()) : null;
        } catch (Exception e) {
            return null;
        }
    }
}


