# TickEasy 微服務架構（Spring Boot + Spring Cloud + AWS）

一個基於微服務架構的線上購票平台，使用 Spring Boot、Spring Cloud、MySQL、Redis 建構，並部署於 AWS 雲端服務，包含 S3、ECS、EC2、RDS 和 VPC。

🌟 專案概述
本專案展示如何設計、建構和部署一個可擴展的微服務化購票平台。使用者可以：

- 會員註冊/登入（JWT 安全認證）
- 瀏覽活動和票券
- 加入購物車
- 下單購票
- 接收即時通知（WebSocket）
- 管理個人資料和頭像

🛠️ 使用技術
| 層級 | 技術 |
|------|------|
| 前端 | JSP + JSTL + jQuery + Bootstrap |
| 後端 | Spring Boot + Spring Cloud + REST API + JWT |
| 資料庫 | MySQL (AWS RDS) + Redis |
| 容器 | Docker + AWS ECR |
| 雲端 | AWS S3, ECS (EC2 Launch Type), RDS, VPC |
| 通知 | WebSocket + Email (JavaMailSender) |
| 部署 | Docker Compose + AWS Console + AWS CLI |

⚙️ 架構圖
以下是雲端架構圖，展示不同 AWS 服務如何與應用程式整合：

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │   Gateway       │    │   Services      │
│   (JSP/JS)     │◄──►│   Service       │◄──►│   (Member,      │
│                 │    │   (Port 18080)  │    │    Notify,      │
└─────────────────┘    └─────────────────┘    │    Media,       │
                                              │    Auth)        │
                                              └─────────────────┘
                                                       │
                                                       ▼
                                              ┌─────────────────┐
                                              │   Database      │
                                              │   MySQL + Redis │
                                              └─────────────────┘
```

🗃️ 微服務架構
應用程式採用微服務架構，以下是各服務的詳細說明：

## 服務列表

### 1. Gateway Service（API 網關）

- **端口**: 18080
- **功能**: 路由轉發、安全驗證、負載平衡
- **路由配置**:
  - `/api/members/**` → member-service
  - `/api/notify/**` → notify-service
  - `/ws/**` → notify-service (WebSocket)
  - `/api/media/**` → media-service
  - `/api/notifications/**` → notification-service

### 2. Member Service（會員服務）

- **端口**: 18081
- **功能**: 會員註冊、登入、資料管理、Email 驗證
- **主要端點**:
  - `POST /api/members/register` - 會員註冊
  - `POST /api/members/login` - 會員登入
  - `POST /api/members/edit` - 資料編輯
  - `GET /api/members/verify` - 帳號驗證
  - `POST /api/members/reset-password` - 密碼重設

### 3. Media Service（媒體服務）

- **端口**: 18082
- **功能**: S3 預簽名 URL 生成
- **主要端點**:
  - `POST /api/media/presign-upload` - 生成上傳 URL
  - `POST /api/media/presign-download` - 生成下載 URL

### 4. Notify Service（站內通知服務）

- **端口**: 18083
- **功能**: 站內通知、WebSocket 即時通訊、排程提醒
- **主要端點**:
  - `POST /api/notify/notification-list` - 通知列表
  - `POST /api/notify/notification-read` - 標記已讀
  - `POST /api/notify/notification-unvisible` - 隱藏通知
  - `POST /api/notify/notification-clear-all` - 清空通知
- **WebSocket**: `/ws?memberId=...`
- **排程功能**: 每日定時提醒（活動提醒、開賣提醒、售罄提醒）

### 5. Notification Service（Email 通知服務）

- **端口**: 18084
- **功能**: 集中處理 Email 發送
- **主要端點**:
  - `POST /api/notifications/send` - 發送通知

### 6. Auth Service（認證服務）

- **功能**: JWT 簽發/驗證、角色權限管理
- **主要端點**:
  - `POST /auth/token` - 簽發 JWT
  - `POST /auth/introspect` - 驗證 JWT

🗂️ 專案結構

```
tickeasy-microservices/
├── src/main/java/microservices/
│   ├── gateway/           # API 網關服務
│   │   ├── config/        # 路由和安全配置
│   │   └── GatewayApplication.java
│   ├── member/            # 會員服務
│   │   ├── controller/    # REST API 控制器
│   │   ├── service/       # 業務邏輯層
│   │   ├── dao/          # 資料存取層
│   │   ├── vo/           # 值物件
│   │   └── MemberServiceApplication.java
│   ├── media/             # 媒體服務
│   │   ├── controller/    # S3 預簽名控制器
│   │   └── MediaServiceApplication.java
│   ├── notify/            # 站內通知服務
│   │   ├── controller/    # 通知 API 控制器
│   │   ├── websocket/     # WebSocket 處理器
│   │   ├── service/       # 通知業務邏輯
│   │   └── NotifyServiceApplication.java
│   ├── notification/      # Email 通知服務
│   │   ├── controller/    # 通知發送控制器
│   │   ├── service/       # 通知發送邏輯
│   │   └── NotificationServiceApplication.java
│   └── auth/              # 認證服務
│       ├── controller/    # JWT 控制器
│       └── AuthServiceApplication.java
├── src/main/resources/
│   ├── static/            # 前端靜態資產
│   │   └── user/         # 使用者介面
│   ├── db/migration/      # 資料庫遷移腳本
│   └── application-*.yml  # 各服務配置檔案
├── docker-compose.yml     # 本地開發環境
├── Dockerfile            # 容器化配置
└── README.md
```

📋 前置需求

- Java JDK ≥11
- Maven ≥3.8
- MySQL ≥8.0
- Redis ≥6.0
- AWS CLI v2 配置
- Docker ≥20.10
- Docker Compose ≥2.0

📌 本地執行方式

### 1. 使用 Docker Compose（推薦）

```bash
# 啟動所有服務
docker-compose up -d

# 查看服務狀態
docker-compose ps

# 查看日誌
docker-compose logs -f [service-name]
```

### 2. 個別服務啟動

```bash
# 1. 啟動 MySQL 和 Redis
docker-compose up mysql redis -d

# 2. 啟動各微服務（建議設定不同端口）
java -jar -Dserver.port=18081 member-service.jar
java -jar -Dserver.port=18082 media-service.jar
java -jar -Dserver.port=18083 notify-service.jar
java -jar -Dserver.port=18084 notification-service.jar
java -jar -Dserver.port=18080 gateway-service.jar
```

### 3. 環境變數配置

```bash
# Gateway Service
MEMBER_URL=http://localhost:18081
MEDIA_URL=http://localhost:18082
NOTIFY_URL=http://localhost:18083
NOTIFY_WS_URL=ws://localhost:18083

# Member Service
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/tickeasy
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=your_password
JWT_SECRET=your_jwt_secret

# Media Service
AWS_REGION=ap-northeast-1
S3_BUCKET=your_s3_bucket
```

☁️ AWS 部署流程

### 前端部署 → Amazon S3

```bash
# 建置前端資產
mvn clean package

# 同步到 S3
aws s3 sync src/main/resources/static/ s3://your-bucket-name/ --acl public-read

# 配置為靜態網站
aws s3 website s3://your-bucket-name/ --index-document index.html
```

### 後端部署 → ECS via ECR

```bash
# 建置 Docker 映像
docker build -t tickeasy-member-service .
docker tag tickeasy-member-service <aws_id>.dkr.ecr.ap-northeast-1.amazonaws.com/tickeasy-member

# 推送到 ECR
aws ecr get-login-password | docker login --username AWS --password-stdin <aws_id>.dkr.ecr.ap-northeast-1.amazonaws.com
docker push <aws_id>.dkr.ecr.ap-northeast-1.amazonaws.com/tickeasy-member

# 在 ECS 中建立任務定義
# 包含環境變數：DB_PASSWORD, JWT_SECRET, AWS_REGION, S3_BUCKET
```

### 資料庫部署 → Amazon RDS (MySQL)

- 設定私有子網路
- 端口 3306 僅開放給 ECS 安全群組
- 透過 SSH EC2 實例手動初始化

### Redis 部署 → ElastiCache

- 設定私有子網路
- 配置安全群組
- 設定記憶體和網路參數

## 🚀 主要功能特色

### 會員管理

- ✅ 註冊登入（JWT 認證）
- ✅ 資料編輯和頭像上傳
- ✅ Email 驗證和密碼重設
- ✅ 權限管理和角色控制

### 通知系統

- ✅ 即時 WebSocket 通知
- ✅ Email 通知發送
- ✅ 排程提醒（活動、開賣、售罄）
- ✅ 通知分類和管理

### 媒體管理

- ✅ S3 預簽名上傳/下載
- ✅ 圖片壓縮和優化
- ✅ CDN 整合

### 安全機制

- ✅ JWT Token 認證
- ✅ Spring Security 整合
- ✅ 密碼 BCrypt 雜湊
- ✅ CORS 和 CSRF 防護

## 📊 監控和日誌

### 健康檢查端點

- `GET /actuator/health` - 服務健康狀態
- `GET /actuator/info` - 服務資訊
- `GET /actuator/metrics` - 效能指標

### 日誌配置

- 使用 Log4j2 進行日誌管理
- 各服務獨立日誌配置
- 結構化日誌格式

## 🔧 開發和測試

### 單元測試

```bash
# 執行所有測試
mvn test

# 執行特定服務測試
mvn test -pl microservices-member
```

### 整合測試

- 使用 Testcontainers 進行資料庫測試
- 各服務 API 端點測試
- WebSocket 連線測試

### 效能測試

- API 響應時間測試
- 並發使用者測試
- 資料庫查詢效能測試

## 📈 擴展性考量

### 水平擴展

- 各微服務可獨立擴展
- 使用負載平衡器分散流量
- 資料庫讀寫分離

### 快取策略

- Redis 快取熱門資料
- 本地快取減少網路延遲
- 快取失效策略

### 容錯機制

- 服務降級和熔斷器
- 重試機制和超時設定
- 監控和告警系統

## 📞 聯絡資訊

- **專案維護者**: TickEasy 開發團隊
- **技術支援**: 請提交 Issue 或 Pull Request
- **文件更新**: 定期更新技術文檔和部署指南

---

_本專案展示了一個完整的微服務架構購票平台，從設計到部署的全流程實作。_
