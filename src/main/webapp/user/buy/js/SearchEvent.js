const app = Vue.createApp({
  data() {
    return {
      // 1. 綁定響應屬性
      eventsList: [],
      searchWords: "",
    };
  },

  methods: {
    // 2. 從後端 api 抓資料
    async fetchEventInfo() {
      // fetch() 後端 api 查詢所有活動
      const url = `http://localhost:8080/maven-tickeasy-v1/search-event`;
      const resp = await fetch(url);
      // 將資料轉為 js 物件格式
      const eventsData = await resp.json();
      eventsData.forEach((item) => {
        // 將資料存入 data() 屬性
        this.eventsList.push(item);
      });
    },
    async searchClick() {
      const words = encodeURIComponent(this.searchWords);
      // fetch() 後端 api 以 "xxx" 查詢活動
      const url = `http://localhost:8080/maven-tickeasy-v1/search-event?keywords=${this.searchWords}`;
      const resp = await fetch(url);
      // 將資料轉為 js 物件格式
      const eventsData = await resp.json();
      // 清空陣列...
      this.eventsList.length = 0;
      // 將資料存入 data() 屬性
      eventsData.forEach((item) => {
        this.eventsList.push(item);
      });
    },
  },

  // 3. 生命週期 created 時就調用 fetch()
  created() {
    this.fetchEventInfo();
  },
});
app.mount("#app");
