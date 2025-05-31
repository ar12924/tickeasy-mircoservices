// ====== section 主內容 ======
const bookTicketsJSLoader = () => {
  $(".update").on("mouseenter mouseleave", (e) => {
    $(e.target).closest(".update").toggleClass("is-focused");
  });
  // ------ 上/下一步按鈕區塊 ------
  $(".back").on("click", () => {
    location.href = "https://www.google.com";
  });
  $(".next").on("mouseenter mouseleave", (e) => {
    $(e.target).toggleClass("is-focused");
  });
  $(".next").on("click", () => {
    // post 東西出去(under constructing...)
    const inputsValues = $(".count")
      .map((index, el) => {
        const coreParent = $(el).closest(".level");
        const typeName = coreParent.find(".type-name").text();
        const typePrice = coreParent.find(".type-price").text();
        // 模擬 json 格式
        return {
          typeCount: $(el).val(),
          typeName: typeName,
          typePrice: typePrice,
        };
      })
      .get();
    console.log(inputsValues);
    location.href = "bookDetails.html";
  });
};
bookTicketsJSLoader(); // 載入 JS 事件監聽
