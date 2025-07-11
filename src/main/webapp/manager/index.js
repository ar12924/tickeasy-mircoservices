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
      dataSrc: function (json) {
        console.log('API 回傳資料:', json); // 除錯用
        if (json.successful) {
          return json.data; // 回傳實際的資料陣列
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
          const isPosted = data.isPosted;
          const toggleButtonText = isPosted ? "下架活動" : "上架活動";
          const toggleButtonClass = isPosted ? "btn-warning" : "btn-success";
          return `
            <div class="d-grid gap-2">
              <a href="event/EditEvent.html?eventId=${data.eventId}" class="btn btn-warning text-danger">編輯活動</a>
              <a href="event/EditTicketType.html?eventId=${data.eventId}" class="btn btn-warning text-danger">編輯票種</a>
              <button class="btn ${toggleButtonClass} btn-toggle-status" 
                      data-event-id="${data.eventId}" 
                      data-current-status="${isPosted}">
                ${toggleButtonText}
              </button>
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
    sessionStorage.setItem('eventId', eventId);
    window.location.href = `eventdetail/order-list.html?eventId=${eventId}`;
  });
  $(document).on("click", ".btn-dashboard", function () {
    const eventId = $(this).data("event-id");
    sessionStorage.setItem('eventId', eventId);
    window.location.href = `eventdetail/dashboard.html?eventId=${eventId}`;
  });
  $(document).on("click", ".btn-participant", function () {
    const eventId = $(this).data("event-id");
    sessionStorage.setItem('eventId', eventId);
    window.location.href = `eventdetail/participant-list.html?eventId=${eventId}`;
  });
  $(document).on("click", ".btn-exchange", function () {
    const eventId = $(this).data("event-id");
    sessionStorage.setItem('eventId', eventId);
    window.location.href = `eventdetail/ticket_exchange.html?eventId=${eventId}`;
  });

  // 上/下架活動功能
  $(document).on("click", ".btn-toggle-status", function () {
    const eventId = $(this).data("event-id");
    const currentStatus = $(this).data("current-status");
    const newStatus = currentStatus ? 0 : 1; // 切換狀態
    const actionText = currentStatus ? "下架" : "上架";

    if (!confirm(`確定要${actionText}這個活動嗎？`)) {
      return;
    }

    toggleEventStatus(eventId, newStatus, $(this));
  });

  // 為編輯活動連結加上點擊事件
  $(document).on("click", "a[href*='EditEvent.html']", function () {
    const urlParams = new URLSearchParams(this.href.split('?')[1]);
    const eventId = urlParams.get('eventId');

    if (eventId) {
      sessionStorage.setItem('eventId', eventId);
    }
  });

  // 為編輯票種連結加上點擊事件  
  $(document).on("click", "a[href*='EditTicketType.html']", function () {
    const urlParams = new URLSearchParams(this.href.split('?')[1]);
    const eventId = urlParams.get('eventId');

    if (eventId) {
      sessionStorage.setItem('eventId', eventId);
    }
  });
}

// 切換活動狀態的函數
async function toggleEventStatus(eventId, newStatus, buttonElement) {
  try {
    // 更新按鈕狀態為載入中
    const originalText = buttonElement.text();
    const originalClass = buttonElement.attr('class');

    buttonElement.text('處理中...')
      .prop('disabled', true)
      .removeClass('btn-success btn-warning')
      .addClass('btn-secondary');

    console.log(`準備切換活動 ${eventId} 狀態至: ${newStatus}`);

    const response = await fetch(`http://localhost:8080/maven-tickeasy-v1/manager/toggle-event-status/${eventId}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ isPosted: newStatus })
    });

    if (!response.ok) {
      throw new Error(`狀態切換失敗，狀態碼：${response.status}`);
    }

    const result = await response.json();
    console.log('狀態切換回應:', result);

    if (result.successful) {
      // 成功：重新載入 DataTable
      $('#eventList').DataTable().ajax.reload(null, false);

      // 顯示成功訊息
      showMessage('success', `活動${newStatus ? '上架' : '下架'}成功！`);
    } else {
      throw new Error(result.message || '狀態切換失敗');
    }

  } catch (error) {
    console.error('切換活動狀態錯誤:', error);
    showMessage('error', `操作失敗：${error.message}`);

    // 恢復按鈕原始狀態
    buttonElement.text(originalText)
      .prop('disabled', false)
      .attr('class', originalClass);
  }
}

// 顯示訊息的函數
function showMessage(type, message) {
  const alertType = type === 'success' ? 'alert-success' : 'alert-danger';
  // const messageHtml = `
  //   <div class="alert ${alertType} alert-dismissible fade show" role="alert">
  //     ${message}
  //     <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
  //   </div>
  // `;
  const messageHtml = message;

  // 在頁面頂部顯示訊息
  // $('main .container').prepend(messageHtml);
  window.alert(messageHtml);

  // 3秒後自動隱藏
  setTimeout(() => {
    $('.alert').fadeOut();
  }, 3000);
}

// 頁面載入後初始化
window.addEventListener("DOMContentLoaded", init);
