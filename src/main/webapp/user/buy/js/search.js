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
      // 2. 綁定搜尋輸入框的值
      searchKeyword: "",
      // 3. 當前頁數
      currentPage: 1,
      // 4. 每頁資料個數
      pageSize: 6,
    };
  },
  computed: {
    // 1. 最大頁數
    maxPage() {
      if (this.eventPayload.count % this.pageSize === 0) {
        return Math.floor(this.eventPayload.count / this.pageSize);
      } else {
        return Math.floor(this.eventPayload.count / this.pageSize) + 1;
      }
    },
    // 2. 分頁頁數陣列
    pageNumberArr() {
      const pageNumber = this.maxPage;
      return (arr = Array.from(
        { length: pageNumber },
        (_, index) => index + 1
      ));
    },
  },
  methods: {
    // 1. 從後端 api 抓 event_info 資料
    async fetchEventInfo() {
      const url = `http://localhost:8080/maven-tickeasy-v1/search-event?keyword=${this.searchKeyword}&pageNumber=${this.currentPage}`;
      const resp = await fetch(url);
      const body = await resp.json();
      this.eventPayload = body;
    },
    // 2. 將時間轉換為日期格式
    formatDate(dateString) {
      const date = new Date(dateString);
      const options = { year: "numeric", month: "short", day: "numeric" };
      return date.toLocaleDateString("en-US", options);
    },
    // 3. 使用者點擊搜尋列
    searchClick() {
      if (this.searchKeyword) {
        window.location.href = `search.html?keyword=${this.searchKeyword}`;
      } else {
        window.location.href = `search.html`;
      }
    },
    // 4. 使用者點擊前一頁、後一頁或頁籤時觸發
    tabPrev() {
      if (this.currentPage > 1) {
        this.currentPage--;
        window.location.href = `search.html?keyword=${this.searchKeyword}&page=${this.currentPage}`;
      }
    },
    tabNext() {
      if (this.currentPage < this.maxPage) {
        this.currentPage++;
        window.location.href = `search.html?keyword=${this.searchKeyword}&page=${this.currentPage}`;
      }
    },
    tabClick(e) {
      this.currentPage = parseInt(e.target.textContent, 10);
      window.location.href = `search.html?keyword=${this.searchKeyword}&page=${this.currentPage}`;
    },
  },
  created() {
    // 1. 取得 URL 後方參數(關鍵字, 頁數)
    const urlSearchParams = new URLSearchParams(window.location.search);
    const params = Object.fromEntries(urlSearchParams.entries());
    // 2. 防止 URL 後方參數載入當下為 undefined
    if (params.keyword !== undefined) {
      this.searchKeyword = params.keyword;
    }
    if (params.page !== undefined) {
      this.currentPage = parseInt(params.page, 10);
    }
    // 3. 執行 method 中的 fetch() 函式
    this.fetchEventInfo();
  },
});
app.mount("#app");
