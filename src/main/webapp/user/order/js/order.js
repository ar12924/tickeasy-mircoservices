// // ==================== import all =====================
import { fetchNavTemplate, renderNav, initNavJSEvents } from "../../layout/nav/nav.js";
import { fetchFooterTemplate, renderFooter } from "../../layout/footer/footer.js";

// // ==================== 1. 工具函數 (Utilities) ====================
// // 這些函數負責處理一些通用的、無關特定業務邏輯的任務

// /**
//  * 從 URL 查詢參數中獲取指定參數的值。
//  * @param {string} paramName - 要獲取的參數名稱。
//  * @returns {string|null} 參數的值，如果不存在則為 null。
//  */
// export const getUrlParam = (paramName) => {
//   const queryString = window.location.search;
//   const urlParams = new URLSearchParams(queryString);
//   return urlParams.get(paramName);
// };

// // ==================== 2. API 服務層 (API Service Layer) ====================
// // 這些函數負責與後端 API 進行互動，處理請求的發送和響應的接收。

// /**
//  * 將選定的票種和數量數據 POST 到後端 Redis 進行保存。
//  * @param {Array<Object>} selectionList - 包含票種選擇信息的數組。
//  * @param {number} eventId - 事件 ID。
//  */
// const saveBookTicketSelections = async (selectionLst, eventId) => {
//   const resp = await fetch(
//     `http://localhost:8080/maven-tickeasy-v1/user/orders`,
//     {
//       method: "GET",
//       headers: { "Content-Type": "application/json" },
//       body: JSON.stringify(selectionLst),
//     }
//   );
//   const respBody = await resp.json();
//   console.log("Redis Save Response: ", respBody);
// };

// // ==================== 3. 數據處理層 (Data Processing) ====================
// // 這些函數負責從 DOM 中提取數據，並對數據進行格式化或轉換。

// /**
//  * 從頁面中抓取所有票種輸入框的數值和相關信息。
//  * @returns {Array<Object>} 包含 quantity, categoryName, price 的數組。
//  */
// const getTicketInputsValues = () => {
//   const inputsValues = $(".type-quantity")
//     .map((i, input) => {
//       const parentNode = $(input).closest(".level");
//       const categoryName = parentNode.find(".type-name").text();
//       const price = parentNode
//         .find(".type-price")
//         .text()
//         .replace(/[^0-9.]/g, ""); // 過濾非數字符號
//       return {
//         quantity: $(input).val(),
//         categoryName,
//         price,
//       };
//     })
//     .get(); // .get() 將 jQuery 物件轉換為原生 JavaScript 數組
//   return inputsValues;
// };

// // ==================== 4. DOM 事件處理與頁面邏輯 (DOM Events & Page Logic) ====================
// // 這是主要頁面邏輯的入口點，負責綁定事件和協調不同層級的函數。

// const initBookTicketsJSEvents = () => {
//   // ====== "更新票券" 按鈕點擊事件 ======
//   $(".update").on("mouseenter mouseleave", (e) => {
//     $(e.target).closest(".update").toggleClass("is-focused");
//   });
//   // ====== "上一步" 按鈕點擊事件 ======
//   $(".back").on("click", () => {
//     location.href = "https://www.google.com";
//   });
//   // ====== "下一步" 按鈕點擊事件 ======
//   $(".next").on("mouseenter mouseleave", (e) => {
//     $(e.target).toggleClass("is-focused");
//   });
//   $(".next").on("click", () => {
//     const eventId = getUrlParam("eventId");
//     if (!eventId) {
//       alert("缺少活動id，無法繼續!!");
//       return;
//     }
//     const selectedBookTickets = getTicketInputsValues();
//     if (selectedBookTickets.length === 0) {
//       alert("請至少選擇1種票券!!");
//       return;
//     }
//     saveBookTicketSelections(selectedBookTickets, eventId); // post 選擇到的票種至 Redis
//     location.href = `bookDetails.html?eventId=${eventId}`;
//   });
// };

// // ==================== 5. 頁面初始化 (Initialization) ====================
// // 確保 DOM 加載完成後再執行初始化邏輯


(async () => {
  // Nav 部分
  const navTemplate = await fetchNavTemplate();
  await renderNav(navTemplate);
  initNavJSEvents();

  // bookTickets 部分
  initBookTicketsJSEvents(); // 載入 JS 事件監聽

  // footer 部分
  const footerTemplate = await fetchFooterTemplate();
  await renderFooter(footerTemplate);
})();




function init() {
//   $('#eventList').DataTable({
//     ajax: {
//       url: 'http://localhost:8080/maven-tickeasy-v1/manager/show-event',
//       dataSrc: ''
//     },
//     columns: [
//       { data: 'eventName' },
//       { data: 'eventFromDate' },
//       {
//         data: 'isPosted',
//         render: function (data) {
//           return data ? "已發佈" : "未發佈";
//         }
//       },
//       {
//         data: null,
//         render: function (data) {
//           return `
//             <div class="d-grid gap-2">
//               <a href="${data.eventId}" class="btn btn-warning text-danger">編輯活動</a>
//               <a href="#" class="btn btn-warning text-danger">編輯報名表單</a>
//               <a href="#" class="btn btn-warning text-danger">編輯票種</a>
//               <a href="#" class="btn btn-warning text-danger">粉絲交流管理</a>
//               <a href="#" class="btn btn-secondary">上／下架活動</a>
//             </div>
//           `;
//         }
//       },
//       {
//         data: null,
//         render: function (data) {
//           return `
//             <div class="d-grid gap-2">
//               <a href="#" class="btn btn-warning text-danger">訂票列票</a>
//               <a href="./ticket_sales.html" class="btn btn-warning text-danger">票券銷售情形</a>
//               <a href="#" class="btn btn-warning text-danger">報名人列表</a>
//               <a href="#" class="btn btn-warning text-danger">換票分票紀錄</a>
//             </div>
//           `;
//         }
//       }
//     ],
//     language: {
//       emptyTable: "目前沒有活動資料",
//       loadingRecords: "載入中...",
//       lengthMenu: "顯示 _MENU_ 筆資料",
//       zeroRecords: "找不到符合條件的資料",
//       info: "顯示第 _START_ 到第 _END_ 筆資料，共 _TOTAL_ 筆",
//       infoEmpty: "目前沒有資料",
//       search: "搜尋：",
//       paginate: {
//         first: "第一頁",
//         last: "最後一頁",
//         next: "下一頁",
//         previous: "上一頁"
//       }
//     }
//   });
}

// 頁面載入後初始化
// window.addEventListener('DOMContentLoaded', init);

async function loadOrders() {
  try {
    // 1) 送出 fetch
    const resp = await fetch('/user/orders');
    // 2) 解析成 JS 陣列
    const orders = await resp.json();
    // 如果你的回傳格式是 { data: [...] }，就改成：
    // const { data: orders } = await resp.json();

    // 3) 找到容器
    const ul = document.getElementById('eventList');
    if(orders.authStatus=='NOT_LOGGED_IN'){
      window.alert(orders.message);
    }

    // 4) 用 .map 產生 HTML 再一次塞進 innerHTML
    ul.innerHTML = orders.map(ev => `
      <li>
        <strong></strong><br>
        開始時間：${new Date(ev.eventFromDate).toLocaleString()}<br>
        狀態：${ev.isPosted ? '已發佈' : '未發佈'}
      </li>

      <div class="order-card box">
        <div class="columns is-vcentered is-mobile">
          <!-- 活動圖片 -->
          <div class="column is-narrow">
            <figure class="image is-96x96 mr-4">
              <img
                src="https://images.unsplash.com/photo-1464983953574-0892a716854b?auto=format&fit=facearea&w=128&h=128&q=80"
                alt="活動圖片">
            </figure>
          </div>
          <!-- 活動資訊 -->
          <div class="column">
            <p class="title is-5 mb-1">${ev.eventName}</p>
            <div class="columns is-gapless is-mobile">
              <div class="column is-narrow has-text-grey-dark">
                <p class="mb-1 mr-5"><b>活動時間</b></p>
                <p class="mb-1 mr-5"><b>活動地點</b></p>
                <p class="mb-1 mr-5"><b>訂單編號</b></p>
                <p class="mb-1 mr-5"><b>訂單成立</b></p>
                <p class="mb-1 mr-5"><b>訂單總額</b></p>
              </div>
              <div class="column pl-2">
                <p class="mb-1">2024/03/15</p>
                <p class="mb-1">台北市信義區｜台北流行音樂中心</p>
                <p class="mb-1">#T_1123</p>
                <p class="mb-1">2024/02/01 00:00</p>
                <p class="mb-1">$3,900</p>
              </div>
            </div>
          </div>
          <!-- 狀態、倒數 -->
          <div class="column is-narrow has-text-centered">
            <span class="tag is-warning is-medium">未付款</span>
            <br>
            <span class="text-red" style="font-weight:700;display:block;margin-top:1rem;">剩餘付款時間 12:15</span>
          </div>
        </div>
        <!-- 票券明細表格 -->
        <table class="table is-fullwidth mt-4">
          <thead>
            <tr>
              <th>票號</th>
              <th>票種</th>
              <th>序號</th>
              <th>金額</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>#123456781</td>
              <td>一般區</td>
              <td>1</td>
              <td>1,100</td>
            </tr>
            <tr>
              <td>#123456782</td>
              <td>搖滾區</td>
              <td>2</td>
              <td>2,800</td>
            </tr>
          </tbody>
          <tfoot>
            <tr>
              <th colspan="3" class="has-text-right">合計</th>
              <th>3,900</th>
            </tr>
          </tfoot>
        </table>
      </div>
      


    `).join('');

  } catch (err) {
    console.error(err);
    document.getElementById('eventList').textContent = '載入失敗';
  }
}

window.addEventListener('DOMContentLoaded', loadOrders);