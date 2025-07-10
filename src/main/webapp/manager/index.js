function init() {
  const memberId = sessionStorage.getItem("memberId");
  if (!memberId) {
    alert("請先登入");
    window.location.href = "/maven-tickeasy-v1/user/member/login.html";
    return;
  }
  $("#eventList").DataTable({
    ajax: {
      url: `/maven-tickeasy-v1/manager/show-event/member/${memberId}`,
      dataSrc: function(json) {
            console.log('API 回傳資料:', json); // 除錯用
            if (json.successful) {
                return json.data; // ✅ 回傳實際的資料陣列
            } else {
                console.error('API 錯誤:', json.message);
                return []; // 回傳空陣列
            }
        }
    },
    columns: [
      { data: "eventName" },
      { data: "eventFromDate" },
      {
        data: "isPosted",
        render: function (data) {
          return data ? "已發佈" : "未發佈";
        }
      },
      {
        data: null,
        render: function (data) {
          return `
            <div class="d-grid gap-2">
              <a href="event/EditEvent.html?eventId=${data.eventId}" class="btn btn-warning text-danger">編輯活動</a>
              <a href="#" class="btn btn-warning text-danger">編輯報名表單</a>
              <a href="event/EditTicketType.html?eventId=${data.eventId}" class="btn btn-warning text-danger">編輯票種</a>
              <a href="#" class="btn btn-warning text-danger">粉絲交流管理</a>
              <a href="#" class="btn btn-secondary">上／下架活動</a>
            </div>
          `;
        }
      },
      {
        data: null,
        render: function (data) {
          return `
            <div class="d-grid gap-2">
              <button class="btn btn-warning text-danger btn-order" data-event-id="${data.eventId}">訂票列表</button>
              <button class="btn btn-warning text-danger btn-dashboard" data-event-id="${data.eventId}">票券銷售情形</button>
              <button class="btn btn-warning text-danger btn-participant" data-event-id="${data.eventId}">報名人列表</button>
              <button class="btn btn-warning text-danger btn-exchange" data-event-id="${data.eventId}">換票分票紀錄</button>
            </div>
          `;
        }
      }
    ],
    language: {
      emptyTable: "目前沒有活動資料",
      loadingRecords: "載入中...",
      lengthMenu: "顯示 _MENU_ 筆資料",
      zeroRecords: "找不到符合條件的資料",
      info: "顯示第 _START_ 到第 _END_ 筆資料，共 _TOTAL_ 筆",
      infoEmpty: "目前沒有資料",
      search: "搜尋：",
      paginate: {
        first: "第一頁",
        last: "最後一頁",
        next: "下一頁",
        previous: "上一頁",
      }
    }
  });

  // 綁定按鈕點擊事件
  $(document).on("click", ".btn-order", function () {
    const eventId = $(this).data("event-id");
    window.location.href = `eventdetail/order-list.html?eventId=${eventId}`;
  });
  $(document).on("click", ".btn-dashboard", function () {
    const eventId = $(this).data("event-id");
    window.location.href = `eventdetail/dashboard.html?eventId=${eventId}`;
  });
  $(document).on("click", ".btn-participant", function () {
    const eventId = $(this).data("event-id");
    window.location.href = `eventdetail/participant-list.html?eventId=${eventId}`;
  });
  $(document).on("click", ".btn-exchange", function () {
    const eventId = $(this).data("event-id");
    window.location.href = `eventdetail/ticket_exchange.html?eventId=${eventId}`;
  });
}

// 頁面載入後初始化
window.addEventListener("DOMContentLoaded", init);
