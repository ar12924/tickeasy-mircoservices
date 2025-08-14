# ECS 部署指引（樣板）

1. 建立 ECR 並推送映像：member/media/notify/gateway/notification
2. 建立 Task Role 權限：
   - member：讀 SSM/Secrets，必要時發信；
   - media：S3 PutObject/GetObject；
   - notify：DB、（可選）SQS/Redis；
   - gateway：無敏感資源；
3. 將 `taskdef-*.json` 中 `<account>/<region>` 替換為實際值，並以 `aws ecs register-task-definition` 註冊。
4. 服務（Service）掛 ALB Target Group，健康檢查 `/actuator/health`。
5. ADOT Collector 以 sidecar 方式加入所有 Task，容器加：
   - 環境變數 `OTEL_SERVICE_NAME=<service>`
   - 參數 `-javaagent:/otel/opentelemetry-javaagent.jar -Dotel.exporter.otlp.endpoint=http://127.0.0.1:4317`
   - 並將 `adot-collector-config.yaml` 掛載至 collector 容器。
