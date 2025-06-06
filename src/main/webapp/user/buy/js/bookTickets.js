// ====== section 主內容 ======
const bookTicketsJSLoader = () => {
  $(".update").on("mouseenter mouseleave", (e) => {
    $(e.target).closest(".update").toggleClass("is-focused");
  });
  // ====== 上/下一步按鈕區塊 ======
  $(".back").on("click", () => {
    location.href = "https://www.google.com";
  });
  $(".next").on("mouseenter mouseleave", (e) => {
    $(e.target).toggleClass("is-focused");
  });
  $(".next").on("click", () => {
    const inputsValues = $(".type-quantity") // 抓取頁面數值
      .map((index, el) => {
        const parentNode = $(el).closest(".level");
        const categoryName = parentNode.find(".type-name").text();
        const price = parentNode
          .find(".type-price")
          .text()
          .replace(/[^0-9.]/g, ""); // 過濾非數字符號
        return {
          quantity: $(el).val(),
          categoryName,
          price,
        };
      })
      .get();
    const reqBody = inputsValues;
    bookTicketsSave(JSON.stringify(reqBody)); // post 票種/數量給 Redis
    location.href = "bookDetails.html";
  });
};
bookTicketsJSLoader(); // 載入 JS 事件監聽

const bookTicketsSave = async (reqBody) => {
  const resp = await fetch(
    "http://localhost:8080/maven-tickeasy-v1/user/buy/book-tickets/save",
    {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: reqBody,
    }
  );
  const respBody = await resp.json();
  console.log(respBody);
};
