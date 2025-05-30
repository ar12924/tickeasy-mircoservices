// ====== footer 頁底列 ======
const footerHTMLLoader = async () => {
  const resp = await fetch("./layout/footer/footer.html");
  const footerHTML = await resp.text();
  $("footer").append(footerHTML);
};
const footerJSLoader = () => {};
// 動態生成 HTML
footerHTMLLoader();
// 事件委派方式，監聽後來出現的 HTML
footerJSLoader();
