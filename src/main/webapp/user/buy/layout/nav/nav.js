// ====== nav 導覽列 ======
const navHTMLLoader = async () => {
  const resp = await fetch("./layout/nav/nav.html");
  const navHTML = await resp.text();
  $(".navbar").append(navHTML);
};
const navJSLoader = () => {
  $(".navbar").on("click", ".navbar-burger", (e) => {
    $(e.currentTarget).toggleClass("is-active");
    $(".navbar-menu").toggleClass("is-active");
  });
  $(".navbar").on("mouseenter", ".navbar-item button", (e) => {
    $(e.target).addClass("is-focused");
  });
  $(".navbar").on("mouseleave", ".navbar-item button", (e) => {
    $(e.target).removeClass("is-focused");
  });
};
// 動態生成 HTML
navHTMLLoader();
// 事件委派方式，監聽後來出現的 HTML
navJSLoader();
