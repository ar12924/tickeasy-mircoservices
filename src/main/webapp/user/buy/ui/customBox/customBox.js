// ------ 入場者表單區塊 ------
const customBoxHTMLLoader = async () => {
  const resp = await fetch("./ui/customBox/customBox.html");
  let customBoxHTML = await resp.text();
  $(".custom-container").append(customBoxHTML);
};
// 動態加入 HTML
customBoxHTMLLoader();
