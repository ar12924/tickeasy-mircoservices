// ------ 入場者表單區塊 ------
const customBoxHTMLLoader = async () => {
  const resp = await fetch("./ui/customBox/customBox.html");
  let customBoxHTML = await resp.text();
  $(".custom-container").append(customBoxHTML);
};
customBoxHTMLLoader(); // 插入 HTML 片段
