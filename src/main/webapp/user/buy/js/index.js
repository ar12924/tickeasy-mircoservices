const app = Vue.createApp({
  data() {
    return {
      // 1. 活動清單
      eventPayload: {
        successful: false,
        message: "",
        count: -1,
        data: [],
      },
      // 2. 票券清單
      ticketList: [],
      // 3. 通知清單
      notificationList: [],
      // 4. 搜尋關鍵字
      searchKeyword: "",
      // 5. 每個分頁顯示筆數
      pageSize: 3,
      // 6. 當前頁數
      currentPage: 1,
    };
  },
  methods: {
    // 1. 從後端 api 抓 event_info 資料
    async fetchEventInfo() {
      const url = `http://localhost:8080/maven-tickeasy-v1/search-event?keyword=${this.searchKeyword}&pageNumber=${this.currentPage}&pageSize=${this.pageSize}`;
      const resp = await fetch(url);
      const body = await resp.json();
      this.eventPayload = body;
    },
    // 2. 從後端 api 抓 buyer_ticket 資料
    async fetchBuyerTicket() {
      const url = `http://localhost:8080/maven-tickeasy-v1/search-ticket`;
      const resp = await fetch(url);
      const body = await resp.json();
      this.ticketList = body;
    },
    // 3. 從後端 api 抓 member_notification 資料
    async fetchMemberNotification() {
      const url = `http://localhost:8080/maven-tickeasy-v1/search-notification`;
      const resp = await fetch(url);
      const body = await resp.json();
      this.notificationList = body;
    },
    // 4. 計算與現在時點的時間差
    timeAgo(dateTimeString) {
      const past = new Date(dateTimeString).getTime();
      const now = Date.now();
      const seconds = Math.floor((now - past) / 1000);
      let interval = Math.floor(seconds / 31536000);

      if (interval >= 1) {
        return interval + " 年前";
      }
      interval = Math.floor(seconds / 2592000);
      if (interval >= 1) {
        return interval + " 個月前";
      }
      interval = Math.floor(seconds / 86400);
      if (interval >= 1) {
        return interval + " 天前";
      }
      interval = Math.floor(seconds / 3600);
      if (interval >= 1) {
        return interval + " 小時前";
      }
      interval = Math.floor(seconds / 60);
      if (interval >= 1) {
        return interval + " 分鐘前";
      }
      return "剛剛";
    },
    // 5. 將時間轉換為日期格式
    formatDate(dateString) {
      const date = new Date(dateString);
      const options = { year: "numeric", month: "short", day: "numeric" };
      return date.toLocaleDateString("en-US", options);
    },
    // 6. 點擊搜尋按鈕，跳轉 + 關鍵字存入URL?後方參數
    searchClick() {
      if (this.searchKeyword) {
        window.location.href = `search.html?keyword=${this.searchKeyword}`;
      } else {
        window.location.href = `search.html`;
      }
    },
  },
  // 1. 載入頁面時調用抓 api 方法
  created() {
    this.fetchEventInfo();
    this.fetchBuyerTicket();
    this.fetchMemberNotification();
  },
});
app.mount("#app");
