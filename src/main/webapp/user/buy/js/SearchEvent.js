const app = Vue.createApp({
  data() {
    return {
      // 1. 綁定響應屬性
      eventsList: [],
    };
  },

  methods: {
    // 2. 從後端 api 抓資料
    async fetchEventInfo() {
      // fetch() 後端 api 會員查詢結果
      const url = `http://localhost:8080/maven-tickeasy-v1/search-event`;
      const resp = await fetch(url);
      // 將資料轉為 js 物件格式
      const eventsData = await resp.json();
      eventsData.forEach((event) => {
        // 將資料存入 data() 屬性
        this.eventsList.push(event);
      });
    },
  },

  // 3. 生命週期 created 時就調用 fetch()
  created() {
    this.fetchEventInfo();
  },
});
app.mount("#app");
