// ====== nav 導覽列 ======
const navHTMLLoader = async () => {
  const resp = await fetch("../layout/nav/nav.html");
  const navHTML = await resp.text();
  $(".navbar").append(navHTML);
};
navHTMLLoader(); // 插入 HTML 片段
const navJSLoader = () => {
  $(".navbar").on("click", ".navbar-burger", (e) => {
    $(e.currentTarget).toggleClass("is-active");
    $(".navbar-menu").toggleClass("is-active");
  });
  $(".navbar").on("mouseenter mouseleave", ".navbar-item button", (e) => {
    $(e.target).toggleClass("is-focused");
  });
};
navJSLoader(); // 載入 JS 監聽事件
