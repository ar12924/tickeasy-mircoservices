const app = Vue.createApp({
  data() {
    return {
      // 1. 綁定響應屬性
      msg: "",
      indicatedId: "",
      selectedMember: null,
    };
  },
  methods: {
    // 2. 從後端 api 抓資料
    async fetchMemberInfo() {
      // fetch() 後端 api 會員查詢結果
      const url = `http://localhost:8080/maven-tickeasy-v1/search-member?id=${this.indicatedId}`;
      const resp = await fetch(url);
      // 將資料轉為 js 物件格式
      const membersData = await resp.json();
      // 將資料存入 data() 屬性
      this.selectedMember = membersData;
    },
    // 3. 定義按鈕的 click 事件
    searchClick() {
      if (this.indicatedId) {
        // 呼叫 methods 中的 fetchMemberInfo() 函數
        this.fetchMemberInfo();
      } else {
        this.msg = "請輸入資料";
      }
    },
  },
});
app.mount("#app");
