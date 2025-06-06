// ====== 票種欄區塊 ======
const typeBoxHTMLLoader = async ({ categoryName, price }) => {
  const resp = await fetch("./ui/typeBox/typeBox.html");
  const typeBoxHTML = $(await resp.text());
  typeBoxHTML.find(".type-name").text(`${categoryName}`); // 附加顯示文字
  typeBoxHTML.find(".type-price").text(`NT$ ${price}`);
  $(".type-container").append(typeBoxHTML); // 插入整塊元素
};
const ticketTypeQuery = async () => {
  const resp = await fetch(
    "http://localhost:8080/maven-tickeasy-v1/user/buy/book-tickets?eventId=1"
  ); // api 取得 "票種表" 資料
  const data = await resp.json();
  const ticketTypeLst = data;
  for (const ticketType of ticketTypeLst) {
    await typeBoxHTMLLoader(ticketType); // 插入 HTML 片段
  }
};
ticketTypeQuery(); // 抓取 "票種表" 資料，並顯示於 HTML 片段

const typeBoxJSLoader = () => {
  $(document).on("click", ".substract", (e) => {
    const control = $(e.target).parent();
    const input = control.next().find("input");
    let count = input.val();
    if (count > 0) {
      count--;
      input.val(count);
    }
  });
  $(document).on("click", ".add", (e) => {
    const control = $(e.target).parent();
    const input = control.prev().find("input");
    let count = input.val();
    count++;
    input.val(count);
  });
};
typeBoxJSLoader(); // 載入 JS 事件監聽
