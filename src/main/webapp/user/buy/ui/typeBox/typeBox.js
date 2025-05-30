// API "票種表" 資料
let ticketTypeLst;
const ticketTypeQuery = async () => {
  // 取得假資料 (寫死在前端)
  const resp = await fetch("./data/typeSample.json");
  const data = await resp.json();
  ticketTypeLst = data;
  ticketTypeLst.forEach((eachType) => {
    // 動態生成 HTML
    typeBoxHTMLLoader(eachType);
  });
};
ticketTypeQuery();
// ------ 票種區塊 ------
const typeBoxHTMLLoader = async (eachType) => {
  const resp = await fetch("./ui/typeBox/typeBox.html");
  let typeBoxHTML = await resp.text();
  // 將資料 (eachType) 放入元素
  typeBoxHTML = typeBoxHTML
    .replace("{{typeName}}", eachType.name)
    .replace("{{price}}", eachType.price.toLocaleString());
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
// 事件委派方式，監聽後來出現的 HTML
typeBoxJSLoader();
