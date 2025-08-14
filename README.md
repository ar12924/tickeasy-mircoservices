# TickEasy Microservices（Spring Boot + Spring Cloud + AWS）

本專案是將原本 `user/member` 模組抽離為微服務化架構，並新增通知、媒體、網關等服務。整體設計借鑒 `javamall-main/` 架構，但所有新程式碼均位於 `src/main/java/microservices/**` 與對應 `resources/**` 之下。

## 架構與服務列表

目標：雲端友善、可水平擴展、靜態資產直出、後端彼此鬆耦合。

現有服務：

- gateway-service（Spring Cloud Gateway）

  - 路由：
    - `/api/members/**`, `/user/member/**`, `/api/member-photos/**` → member-service
    - `/api/notify/**` → notify-service（站內通知 REST）
    - `/ws/**` → notify-service（WebSocket）
    - `/api/media/**` → media-service（S3 預簽名）
    - `/api/notifications/**` → notification-service（Email 發送）
  - 安全：保護 `/api/notify/**` 僅允許已登入使用者（JWT），其餘開放靜態資源與必要公開路徑
  - 重要環境變數：`MEMBER_URL`, `NOTIFY_URL`, `NOTIFY_WS_URL`, `MEDIA_URL`

- member-service

  - 功能：註冊、登入、基本資料編輯、Email 驗證、密碼重設
  - 資料庫：MySQL（Hibernate 5 + Flyway），Flyway 已加入 `V3__member_constraints.sql`（UNIQUE: USER_NAME / EMAIL，INDEX: PHOTO_KEY）
  - 安全：Spring Security + JWT（`JWT_SECRET`），BCrypt 密碼雜湊
  - 相容路徑：保留 `/user/member/**` 以支援既有頁面（建議逐步遷移至 `/api/members/**`）
  - 與其他服務整合：
    - 媒體：改採「前端預簽名 PUT 上傳」；後端僅存 `photoKey`
    - 通知（Email）：呼叫 notification-service 的 `/api/notifications/send`
  - 主要端點：
    - `POST /api/members/login`
    - `POST /api/members/register`（JSON，支援 `photoKey`）
    - `POST /api/members/edit`（JSON，支援 `photoKey`）
    - `GET /api/members/verify/**`, `POST /api/members/reset-password/**`

- media-service（S3）

  - 功能：產生 S3 預簽名網址
  - 端點：
    - `POST /api/media/presign-upload` → 回傳 `{ url, key }`（前端將檔案以 PUT 直傳 S3）
    - `POST /api/media/presign-download` → 回傳 `{ url, key }`
  - 重要環境變數：`AWS_REGION`, `S3_BUCKET`

- notification-service（Email）

  - 功能：集中處理 Email 寄送（由 member-service 觸發）
  - 端點：`POST /api/notifications/send`

- notify-service（站內通知 + WebSocket + 排程）
  - REST 端點：
    - `POST /api/notify/notification-list`（body：`{ memberId }`）
    - `POST /api/notify/notification-read`
    - `POST /api/notify/notification-unvisible`
    - `POST /api/notify/notification-clear-all`
    - `GET /api/notify/check-login`（暫時回傳 true，之後接 JWT）
  - WebSocket：`/ws?memberId=...`（經由 Gateway 轉發）
  - 排程：使用 Spring Scheduling（`@EnableScheduling`），每日定時寫入提醒類通知（活動前一天、收藏開賣/售罄、剩餘票數比例等）

預留與規劃中：

- auth-service（規劃中）：簽發/驗證 JWT、角色/權限、第三方登入

## 前端（靜態資產）

- 放置於 `src/main/resources/static/**`，不依賴 Node.js/Vue。
- 會員頁面：`static/user/member/**`（`register.js`, `edit.js` 已改用預簽名 PUT 上傳流程）
- 通知中心頁面：`static/user/notify/**`
  - AJAX 路徑已改為 `/api/notify/**`
  - WebSocket 連線改為 `/ws?memberId=...`（走 Gateway，自動 http/https → ws/wss）

### 圖片上傳（預簽名 PUT 流程）

1. 前端請求：`POST /api/media/presign-upload`，body：`{ contentType }`
2. 後端回傳：`{ url, key }`
3. 前端將檔案 `PUT` 至 `url`
4. 註冊/編輯 API 僅送出 `photoKey=key`（member-service 只存 key，不搬檔）

## 建置與執行

### 需求

- JDK 17
- Maven 3.8+
- MySQL 8.x（建議）

### 本機啟動（單一服務）

1. 匯入 IntelliJ IDEA。
2. 分別執行以下主類（建議設定不同埠）：

   - `GatewayServiceApplication`（例：`-Dserver.port=18080`）
   - `MemberServiceApplication`（例：`-Dserver.port=18081`）
   - `MediaServiceApplication`（例：`-Dserver.port=18082`）
   - `NotifyServiceApplication`（例：`-Dserver.port=18083`）
   - `NotificationServiceApplication`（例：`-Dserver.port=18084`）

3. 設定 Gateway 的環境變數（或在 Run/Debug 設定）：

```
MEMBER_URL=http://localhost:18081
MEDIA_URL=http://localhost:18082
NOTIFY_URL=http://localhost:18083
NOTIFY_WS_URL=ws://localhost:18083
```

4. 設定 member-service 的資料庫與安全：

```
SPRING_DATASOURCE_URL=jdbc:mysql://127.0.0.1:3306/microservice?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Taipei
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=你的密碼
JWT_SECRET=dev-secret-請於生產更換
```

5. 設定 media-service：

```
AWS_REGION=ap-northeast-1
S3_BUCKET=你的bucket名稱
```

6. 如需 Email 寄送（notification-service）：請設定 SMTP/SES 相關環境或 `application-*.yml`。

### 以 Maven 打包

```
mvn -DskipTests package
```

可於 IDEA 直接執行各服務主類，或以 `java -jar` 搭配 `-Dserver.port` 啟動。

## Docker 與 AWS

- 專案內含 `docker-compose.yml`（本機或 EC2 簡單部署）。
- EC2 初始化腳本：`scripts/ec2-init.sh`（安裝 Docker/Docker Compose/AWS CLI，並預留從 SSM 取參數再 `docker compose up`）。
- 建議最小化部署：
  - MySQL（RDS 或 EC2 容器）
  - member-service、media-service、notify-service、notification-service、gateway-service
  - 依需要調整 `MEMBER_URL` 等環境變數

## 資料庫與遷移

- 使用 Flyway 管理 schema：腳本位於 `src/main/resources/db/migration/`。
- 重要：`V3__member_constraints.sql` 新增 `UNIQUE (USER_NAME)`, `UNIQUE (EMAIL)`, `INDEX (PHOTO_KEY)`。

## 測試

- 成員 DAO 整合測試：`MemberDaoIntegrationTest`（Testcontainers/MySQL）。

## 主要端點速查

- Member

  - `POST /api/members/login`
  - `POST /api/members/register`（JSON；可含 `photoKey`）
  - `POST /api/members/edit`（JSON；可含 `photoKey`）
  - `GET /api/members/verify/**`, `POST /api/members/reset-password/**`
  - 相容：`/user/member/**`

- Media（S3）

  - `POST /api/media/presign-upload` → `{ url, key }`
  - `POST /api/media/presign-download` → `{ url, key }`

- Notify（站內）

  - `POST /api/notify/notification-list`
  - `POST /api/notify/notification-read`
  - `POST /api/notify/notification-unvisible`
  - `POST /api/notify/notification-clear-all`
  - `GET /api/notify/check-login`
  - WebSocket：`/ws?memberId=...`

- Notification（Email）
  - `POST /api/notifications/send`

## 注意事項

- 開發與本機測試建議關閉 Gateway 對 JWT 的強制要求，或為 Gateway/Member 設定相同的 JWT 驗證機制。
- 生產環境請妥善配置 `JWT_SECRET`、`S3_BUCKET`、`AWS_REGION`、DB 連線、郵件服務與 CORS 規則。
- 前端已移至 `src/main/resources/static/**`，避免 Node.js/Vue 依賴。
