const app = Vue.createApp({
  data() {
    return {
      // 1.1. 活動清單
      eventsList: [],
      // 2.1. 票券清單
      ticketList: [],
      // 3.1. 通知清單
      notificationList: [],
    };
  },
  methods: {
    // 1.2. 從後端 api 抓 event_info 資料
    async fetchEventInfo() {
      const url = `http://localhost:8080/maven-tickeasy-v1/index-search-event`;
      const resp = await fetch(url);
      const eventsData = await resp.json();
      eventsData.forEach((item, i) => {
        this.eventsList.push(item);
      });
    },
    // 2.2. 從後端 api 抓 buyer_ticket 資料
    async fetchBuyerTicket() {
      const url = `http://localhost:8080/maven-tickeasy-v1/index-search-ticket`;
      const resp = await fetch(url);
      const ticketData = await resp.json();
      ticketData.forEach((item, i) => {
        this.ticketList.push(item);
      });
    },
    // 3.2. 從後端 api 抓 member_notification 資料
    async fetchMemberNotification() {
      const url = `http://localhost:8080/maven-tickeasy-v1/index-search-notification`;
      const resp = await fetch(url);
      const notificationData = await resp.json();
      notificationData.forEach((item, i) => {
        this.notificationList.push(item);
      });
    },
    // 3.3. 計算與現在時點的時間差
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
    // 1.4. 將時間轉換為日期格式
    formatDate(dateString) {
      const date = new Date(dateString);
      const options = { year: "numeric", month: "short", day: "numeric" };
      return date.toLocaleDateString("en-US", options);
    },
  },
  // 1.3. 載入頁面時調用抓 api 方法
  created() {
    this.fetchEventInfo();
    this.fetchBuyerTicket();
    this.fetchMemberNotification();
  },
});
app.mount("#app");
