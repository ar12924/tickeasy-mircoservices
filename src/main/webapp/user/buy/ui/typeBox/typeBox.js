// ------ 票種區塊 ------
const typeBoxHTMLLoader = async ({ categoryName, price, capacity }) => {
  const resp = await fetch("./ui/typeBox/typeBox.html");
  let typeBoxHTML = await resp.text();
  typeBoxHTML = typeBoxHTML
    .replace("{{ typeName }}", categoryName)
    .replace("{{ price }}", price.toLocaleString()); // 將資料放入元素
  $(".type-container").append(typeBoxHTML);
};
let ticketTypeLst;
const ticketTypeQuery = async () => {
  const resp = await fetch(
    "http://localhost:8080/maven-tickeasy-v1/user/buy/ticket-types?eventId=1"
  ); // 取得 "票種表" 資料(api: buy/ticket-types?eventId=1)
  const data = await resp.json();
  ticketTypeLst = data;
  ticketTypeLst.forEach((eachType) => {
    typeBoxHTMLLoader(eachType); // 插入 HTML 片段
  });
};
ticketTypeQuery();
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
