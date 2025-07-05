document.addEventListener("DOMContentLoaded", function () {
  // 檢查用戶權限
  const roleLevel = sessionStorage.getItem("roleLevel");
  const memberId = sessionStorage.getItem("memberId");

  if (!roleLevel || (roleLevel !== "2" && roleLevel !== "3")) {
    alert("您沒有權限訪問此頁面");
    window.location.href = "/maven-tickeasy-v1/user/member/login.html";
    return;
  }

  if (!memberId) {
    alert("請先登入");
    window.location.href = "/maven-tickeasy-v1/user/member/login.html";
    return;
  }

  // DOM Elements
  const eventSelectEl = document.querySelector("#event-select");
  const eventNameEl = document.querySelector("#event-name");

  // 全域變數
  let currentEventId = null;

  // 初始化頁面
  initPage();

  function initPage() {
    loadEvents();
    setupEventListeners();
  }

  function setupEventListeners() {
    if (eventSelectEl) {
      eventSelectEl.addEventListener("change", function () {
        currentEventId = this.value;
        if (currentEventId) {
          loadDashboardData(currentEventId);
        } else {
          clearDashboardData();
        }
      });
    }
  }

  function loadEvents() {
    const memberId = sessionStorage.getItem("memberId");
    if (!memberId) {
      showMessage("無法獲取用戶信息，請重新登入", "error");
      return;
    }

    fetch(`../eventdetail/dashboard?memberId=${memberId}`, {
      credentials: "include",
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error(`HTTP 錯誤! 狀態: ${response.status}`);
        }
        return response.json();
      })
      .then((data) => {
        if (data.successful) {
          const events = data.data || [];

          if (eventSelectEl) {
            eventSelectEl.innerHTML = '<option value="">請選擇活動</option>';

            events.forEach((event) => {
              const option = document.createElement("option");
              option.value = event.eventId;
              option.textContent = event.eventName;
              eventSelectEl.appendChild(option);
            });
          }
        } else {
          showMessage("載入活動列表失敗: " + data.message, "error");
        }
      })
      .catch((error) => {
        showMessage("載入活動列表時發生錯誤: " + error.message, "error");
      });
  }

  function loadDashboardData(eventId) {
    if (!eventId) {
      clearDashboardData();
      return;
    }

    fetch(`../eventdetail/dashboard?eventId=${eventId}`, {
      credentials: "include",
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error(`HTTP 錯誤! 狀態: ${response.status}`);
        }
        return response.json();
      })
      .then((data) => {
        if (data.successful) {
          const result = data.data;
          if (result) {
            displayDashboardData(result);
          } else {
            clearDashboardData();
            showMessage("該活動目前沒有銷售數據", "info");
          }
        } else {
          showMessage("載入儀表板數據失敗: " + data.message, "error");
          clearDashboardData();
        }
      })
      .catch((error) => {
        showMessage("載入儀表板數據時發生錯誤: " + error.message, "error");
        clearDashboardData();
      });
  }

  function displayDashboardData(data) {
    // 更新活動名稱
    if (eventNameEl && data.eventName) {
      eventNameEl.textContent = data.eventName;
    }

    // 更新統計數據
    document.querySelector("#total-tickets").textContent = data.soldCount || 0;
    document.querySelector("#total-revenue").textContent = `$${(
      data.totalRevenue || 0
    ).toLocaleString()}`;
    document.querySelector("#unsold-tickets").textContent = data.unsold || 0;

    // 銷售率後端已經是百分比（0~100），直接顯示一位小數
    const salesRate = data.salesRate || 0;
    document.querySelector("#sales-rate").textContent = `${salesRate.toFixed(
      1
    )}%`;

    // 更新銷售數據表格
    const salesDataTable = document.querySelector("#sales-data-table");
    if (salesDataTable && data.salesData) {
      salesDataTable.innerHTML = "";
      data.salesData.forEach((ticketType) => {
        const row = document.createElement("tr");
        row.innerHTML = `
          <td>${ticketType.categoryName}</td>
          <td>${ticketType.ticketsSold || 0}</td>
          <td>$${(ticketType.totalRevenue || 0).toLocaleString()}</td>
          <td>${ticketType.unsold || 0}</td>
        `;
        salesDataTable.appendChild(row);
      });
    }

    // 更新圓餅圖
    if (data.chartData && data.chartData.length > 0) {
      updateChart(data.chartData);
    } else {
      clearChart();
    }
  }

  function clearDashboardData() {
    if (eventNameEl) {
      eventNameEl.textContent = "活動銷售儀表板";
    }
    document.querySelector("#total-tickets").textContent = "0";
    document.querySelector("#total-revenue").textContent = "$0";
    document.querySelector("#unsold-tickets").textContent = "0";
    document.querySelector("#sales-rate").textContent = "0%";

    const salesDataTable = document.querySelector("#sales-data-table");
    if (salesDataTable) {
      salesDataTable.innerHTML = "";
    }

    // 清空圖表
    clearChart();
  }

  function updateChart(chartData) {
    const chartContainer = document.querySelector("#sales-chart");
    if (!chartContainer) {
      return;
    }

    if (typeof ApexCharts === "undefined") {
      chartContainer.innerHTML =
        '<div class="text-center text-danger py-5">圖表庫載入失敗</div>';
      return;
    }

    // 清空容器
    chartContainer.innerHTML = "";

    // 準備圖表數據
    const series = chartData.map((item) => item.value);
    const labels = chartData.map((item) => item.name);

    // 生成顏色
    const colors = [
      "#FF6384",
      "#36A2EB",
      "#FFCE56",
      "#4BC0C0",
      "#9966FF",
      "#FF9F40",
      "#FF6384",
      "#C9CBCF",
    ];

    // 創建圖表配置
    const options = {
      series: series,
      chart: {
        type: "pie",
        height: 300,
      },
      labels: labels,
      colors: colors.slice(0, series.length),
      legend: {
        position: "bottom",
        fontSize: "14px",
      },
      tooltip: {
        y: {
          formatter: function (val) {
            return val + " 張票";
          },
        },
      },
      plotOptions: {
        pie: {
          donut: {
            size: "60%",
          },
          dataLabels: {
            offset: -5,
          },
        },
      },
      dataLabels: {
        formatter: function (val, opts) {
          const name = opts.w.globals.labels[opts.seriesIndex];
          const value = opts.w.globals.series[opts.seriesIndex];
          const total = opts.w.globals.seriesTotals.reduce((a, b) => a + b, 0);
          const percentage = ((value / total) * 100).toFixed(1);
          return `${name}\n${value}張 (${percentage}%)`;
        },
        style: {
          fontSize: "12px",
          fontFamily: "Helvetica, Arial, sans-serif",
          fontWeight: "bold",
        },
      },
      responsive: [
        {
          breakpoint: 480,
          options: {
            chart: {
              height: 250,
            },
            legend: {
              position: "bottom",
            },
          },
        },
      ],
    };

    try {
      // 創建圖表
      const chart = new ApexCharts(chartContainer, options);
      chart.render();
    } catch (error) {
      chartContainer.innerHTML =
        '<div class="text-center text-danger py-5">圖表創建失敗</div>';
    }
  }

  function clearChart() {
    const chartContainer = document.querySelector("#sales-chart");
    if (chartContainer) {
      chartContainer.innerHTML =
        '<div class="text-center text-muted py-5">暫無銷售數據</div>';
    }
  }

  function showMessage(message, type = "info") {
    const messageEl = document.createElement("div");
    messageEl.className = `alert alert-${
      type === "error" ? "danger" : type === "success" ? "success" : "info"
    } alert-dismissible fade show`;
    messageEl.innerHTML = `
      ${message}
      <button type="button" class="close" data-dismiss="alert" aria-label="Close">
        <span aria-hidden="true">&times;</span>
      </button>
    `;

    const container =
      document.querySelector(".content-wrapper") || document.body;
    container.insertBefore(messageEl, container.firstChild);

    setTimeout(() => {
      if (messageEl.parentNode) {
        messageEl.remove();
      }
    }, 3000);
  }
});
