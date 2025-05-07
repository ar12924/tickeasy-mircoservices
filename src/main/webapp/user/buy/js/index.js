const app = Vue.createApp({
  data() {
    return {
      // 1. 綁定響應屬性
      eventsList: [],
    };
  },
  methods: {
    // 2. 從後端 api 抓 event_info 資料
    async fetchEventInfo() {
      const url = `http://localhost:8080/maven-tickeasy-v1/search-event`;
      const resp = await fetch(url);
      const eventsData = await resp.json();
      eventsData.forEach((item, i) => {
        this.eventsList.push(item);
      });
    },
    // 4. 將時間轉換為日期格式
    formatDate(dateString) {
      const date = new Date(dateString);
      const options = { year: "numeric", month: "short", day: "numeric" };
      return date.toLocaleDateString("en-US", options);
    },
  },
  // 3. 載入頁面時調用抓 api 方法
  created() {
    this.fetchEventInfo();
  },
});
app.mount("#app");
