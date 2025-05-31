let ticketTypeLst;
const ticketTypeQuery = async () => {
  const resp = await fetch(
    "http://localhost:8080/maven-tickeasy-v1/buy/ticket-types?eventId=1"
  ); // 取得 "票種表" 資料(api: buy/ticket-types?eventId=1)
  const data = await resp.json();
  ticketTypeLst = data;
  ticketTypeLst.forEach((eachType) => {
    typeBoxHTMLLoader(eachType); // 動態生成 HTML
  });
};
ticketTypeQuery();
// ------ 票種區塊 ------
const typeBoxHTMLLoader = async ({ categoryName, price, capacity }) => {
  const resp = await fetch("./ui/typeBox/typeBox.html");
  let typeBoxHTML = await resp.text();
  typeBoxHTML = typeBoxHTML
    .replace("{{ typeName }}", categoryName)
    .replace("{{ price }}", price.toLocaleString()); // 將資料放入元素
  $(".type-container").append(typeBoxHTML);
};

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
typeBoxJSLoader(); // 事件委派方式，監聽後來出現的 HTML
