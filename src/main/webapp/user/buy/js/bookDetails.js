// ====== section 主內容 ======
const bookDetailsJSLoader = () => {
  $(".back").on("click", () => {
    location.href = "bookTickets.html";
  });
  $(".next").on("click", () => {
    location.href = "#";
  });
  $(".next").on("mouseenter mouseleave", (e) => {
    $(e.target).toggleClass("is-focused");
  });
};
bookDetailsJSLoader(); // 載入 JS 事件監聽
