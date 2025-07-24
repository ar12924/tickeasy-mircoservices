# maven-tickeasy-v1

## 專案部署步驟

---

### 1. clone 專案

- 從 GitHub clone 專案至本地端:
    
    ```bash
    git clone https://github.com/willyos0113/maven-tickeasy-v1
    ```
    
- 或可使用其他第三方軟體

### **2. 設定 java mail**

- 進入 `src/main/resources` 目錄
- 手動新增 [`application.properties`](http://application.properties) 檔案，配置 java mail 相關設定值
    
    (專案包含 javaMail 寄件模組，為保護寄件信箱隱私，已將設定檔移除 git 追蹤)
    

### **3. 設定 SQL 資料庫**

- 建立 MySQL 容器
    
    ```bash
    docker run --name mysql-vic-demo -e MYSQL_ROOT_PASSWORD=123456 -e TZ=Asia/Taipei -p 3306:3306 -d mysql:8.0.36 mysqld --lower_case_table_names=1
    ```
    
- 使用腳本檔案 `tickeasy1140708.sql` 建立資料庫與測試資料

### **4. 設定 Redis 資料庫**

- 建立 Redis 容器
    
    ```bash
    docker run -d --name redis-stack -p 6379:6379 -p 8001:8001 -e REDIS_ARGS="--requirepass mypassword --appendonly yes" redis/redis-stack:7.2.0-v12
    ```
    
- Redis 容器建立後無需額外設定，保持空白狀態即可
- 以上步驟完成後，啟動 Tomcat 伺服器即可執行專案

## 注意事項

---

### 1. 郵件設定

- `application.properties` 為範例設定檔，非正式可發信的信箱設定。如需測試郵件功能，請聯絡專案成員或自行準備測試用信箱

### 2. 容器設定

- MySQL 與 Redis 均透過 Docker 容器建立，程式連線參數需與容器設定一致才能正常存取資料庫
