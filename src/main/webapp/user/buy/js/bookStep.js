// ====== section 主內容 ======
// ------ 上/下一步按鈕區塊 ------
$(".back").on("click", () => {
  location.href = "https://www.google.com";
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
  location.href = "bookStep2.html";
});
