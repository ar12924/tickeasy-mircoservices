function init() {
  $('#eventList').DataTable({
    ajax: {
      url: 'http://localhost:8080/maven-tickeasy-v1/manager/show-event',
      dataSrc: ''
    },
    columns: [
      { data: 'eventName' },
      { data: 'eventFromDate' },
      {
        data: 'isPosted',
        render: function (data) {
          return data ? "已發佈" : "未發佈";
        }
      },
      {
        data: null,
        render: function (data) {
          return `
            <div class="d-grid gap-2">
              <a href="${data.eventId}" class="btn btn-warning text-danger">編輯活動</a>
              <a href="#" class="btn btn-warning text-danger">編輯報名表單</a>
              <a href="#" class="btn btn-warning text-danger">編輯票種</a>
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
              <a href="#" class="btn btn-warning text-danger">訂票列票</a>
              <a href="eventdetail/dashboard.html" class="btn btn-warning text-danger">票券銷售情形</a>
              <a href="eventdetail/participant-list.html" class="btn btn-warning text-danger">報名人列表</a>
              <a href="eventDetail/ticket_exchange.html" class="btn btn-warning text-danger">換票分票紀錄</a>
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
        previous: "上一頁"
      }
    }
  });
}

// 頁面載入後初始化
window.addEventListener('DOMContentLoaded', init);