document.addEventListener("DOMContentLoaded", function () {
  // 檢查用戶權限
  const roleLevel = sessionStorage.getItem("roleLevel");
  const memberId = sessionStorage.getItem("memberId");

  if (!roleLevel || roleLevel !== "2") {
    alert("您沒有權限訪問此頁面");
    window.location.href = "/maven-tickeasy-v1/user/member/login.html";
    return;
  }

  const urlParams = new URLSearchParams(window.location.search);
  const eventId = urlParams.get("eventId");

  // DOM Elements
  const containerEl = document.querySelector(".container-fluid");

  if (!eventId) {
    // 如果沒有eventId，顯示活動列表
    fetch(
      `/maven-tickeasy-v1/manager/eventdetail/events?memberId=${memberId}`,
      {
        headers: {
          "X-Member-Id": memberId,
          "X-Role-Level": roleLevel,
        },
      }
    )
      .then((response) => {
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.json();
      })
      .then((result) => {
        if (!result.successful) {
          containerEl.innerHTML = `<div class="alert alert-danger">${
            result.message || "無法載入活動列表"
          }</div>`;
          return;
        }

        const events = result.data;
        if (!events || events.length === 0) {
          containerEl.innerHTML = `
            <div class="alert alert-info">
              您還沒有任何活動。
              <a href="event_new.html" class="alert-link">立即建立新活動</a>
            </div>`;
          return;
        }

        // 顯示活動列表
        containerEl.innerHTML = `
          <div class="row">
            <div class="col-12">
              <div class="card">
                <div class="card-header">
                  <h3 class="card-title">我的活動</h3>
                  <div class="card-tools">
                    <a href="event_new.html" class="btn btn-primary btn-sm">
                      <i class="bi bi-plus"></i> 建立新活動
                    </a>
                  </div>
                </div>
                <div class="card-body p-0">
                  <div class="table-responsive">
                    <table class="table table-hover">
                      <thead>
                        <tr>
                          <th>活動名稱</th>
                          <th>開始日期</th>
                          <th>結束日期</th>
                          <th>狀態</th>
                          <th>操作</th>
                        </tr>
                      </thead>
                      <tbody>
                        ${events
                          .map(
                            (event) => `
                          <tr>
                            <td>${event.eventName}</td>
                            <td>${new Date(
                              event.startDate
                            ).toLocaleDateString()}</td>
                            <td>${new Date(
                              event.endDate
                            ).toLocaleDateString()}</td>
                            <td>
                              <span class="badge ${
                                event.status === 1 ? "bg-success" : "bg-danger"
                              }">
                                ${event.status === 1 ? "進行中" : "已結束"}
                              </span>
                            </td>
                            <td>
                              <a href="dashboard.html?eventId=${
                                event.eventId
                              }" class="btn btn-info btn-sm">
                                <i class="bi bi-graph-up"></i> 查看儀表板
                              </a>
                              <a href="participant-list.html?eventId=${
                                event.eventId
                              }" class="btn btn-primary btn-sm">
                                <i class="bi bi-people"></i> 報名人列表
                              </a>
                            </td>
                          </tr>
                        `
                          )
                          .join("")}
                      </tbody>
                    </table>
                  </div>
                </div>
              </div>
            </div>
          </div>`;
      })
      .catch((error) => {
        console.error("獲取活動列表時發生錯誤:", error);
        if (containerEl) {
          containerEl.innerHTML = `<div class="alert alert-danger">無法載入活動列表: ${error.message}</div>`;
        }
      });
    return;
  }

  // 如果有eventId，顯示儀表板
  const eventNameEl = document.querySelector("#event-name");
  const totalTicketsEl = document.querySelector("#total-tickets");
  const totalRevenueEl = document.querySelector("#total-revenue");
  const salesDataTableEl = document.querySelector("#sales-data-table");
  const salesChartEl = document.querySelector("#sales-chart");

  const API_URL = `/maven-tickeasy-v1/manager/eventdetail/dashboard/data?eventId=${eventId}`;

  fetch(API_URL, {
    headers: {
      "X-Member-Id": memberId,
      "X-Role-Level": roleLevel,
    },
  })
    .then((response) => {
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      return response.json();
    })
    .then((result) => {
      if (!result.successful) {
        alert(result.message || "無法載入儀表板資料");
        return;
      }

      const data = result.data;

      // 更新儀表板標題和總計數據
      if (eventNameEl)
        eventNameEl.textContent = data.eventName || "活動銷售儀表板";
      if (totalTicketsEl)
        totalTicketsEl.textContent = data.totalTickets.toLocaleString();
      if (totalRevenueEl)
        totalRevenueEl.textContent = `$ ${data.totalRevenue.toLocaleString()}`;

      // 填充銷售數據表格
      if (salesDataTableEl) {
        salesDataTableEl.innerHTML = ""; // 清空舊數據
        data.salesData.forEach((item) => {
          const row = `<tr>
                      <td>${item.categoryName}</td>
                      <td>${item.ticketsSold.toLocaleString()}</td>
                      <td>$ ${item.totalRevenue.toLocaleString()}</td>
                  </tr>`;
          salesDataTableEl.innerHTML += row;
        });
      }

      // 渲染圓餅圖
      if (salesChartEl) {
        renderChart(data.salesData);
      }
    })
    .catch((error) => {
      console.error("獲取儀表板資料時發生錯誤:", error);
      if (containerEl) {
        containerEl.innerHTML = `<div class="alert alert-danger">無法載入資料: ${error.message}</div>`;
      }
    });

  function renderChart(salesData) {
    if (!salesChartEl) return;

    const chartOptions = {
      series: salesData.map((item) => item.ticketsSold),
      chart: {
        type: "donut",
        height: 400,
      },
      labels: salesData.map((item) => item.categoryName),
      responsive: [
        {
          breakpoint: 480,
          options: {
            chart: {
              width: 200,
            },
            legend: {
              position: "bottom",
            },
          },
        },
      ],
      tooltip: {
        y: {
          formatter: function (val) {
            return val.toLocaleString() + " 張";
          },
        },
      },
    };

    const chart = new ApexCharts(salesChartEl, chartOptions);
    chart.render();
  }
});
