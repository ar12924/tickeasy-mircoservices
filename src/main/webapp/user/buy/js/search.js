const app = Vue.createApp({
  data() {
    return {
      // 1.1. 活動清單
      eventPayload: {
        successful: false,
        message: "",
        data: [],
      },
      // 1.5. 綁定 input 輸入值
      searchKeyword: "",
    };
  },
  methods: {
    // 1.2. 從後端 api 抓 event_info 資料
    async fetchEventInfo(searchKeyword) {
      const url = `http://localhost:8080/maven-tickeasy-v1/index-search-event?keyword=${searchKeyword}`;
      console.log(url);
      const resp = await fetch(url);
      const body = await resp.json();
      this.eventPayload = body;
    },
    // 1.3. 將時間轉換為日期格式
    formatDate(dateString) {
      const date = new Date(dateString);
      const options = { year: "numeric", month: "short", day: "numeric" };
      return date.toLocaleDateString("en-US", options);
    },
    // 1.4. 使用者點擊搜尋列
    searchClick() {
      if (this.searchKeyword) {
        window.location.href = `search.html?keyword=${this.searchKeyword}`;
      } else {
        window.location.href = `search.html`;
      }
    },
  },
  created() {
    // 1.6. 自URL?後方取得關鍵字
    const urlSearchParams = new URLSearchParams(window.location.search);
    const params = Object.fromEntries(urlSearchParams.entries());
    // URL後方沒有參數，會回傳 undefined
    if (params.keyword !== undefined) {
      this.searchKeyword = params.keyword;
    }
    this.fetchEventInfo(this.searchKeyword);
  },
});
app.mount("#app");
