// ====== footer 頁底列 ======
const footerHTMLLoader = async () => {
  const resp = await fetch("../layout/footer/footer.html");
  const footerHTML = await resp.text();
  $("footer").append(footerHTML);
};
footerHTMLLoader(); // 插入 HTML 片段
const footerJSLoader = () => {};
footerJSLoader(); // 載入 JS 監聽事件
