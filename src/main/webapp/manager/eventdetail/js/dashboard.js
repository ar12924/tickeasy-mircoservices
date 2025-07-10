function getQueryParam(name) {
  const url = new URL(window.location.href);
  return url.searchParams.get(name);
}

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
          // === 這裡加上自動選擇 ===
          const eventId = getQueryParam("eventId");
          if (eventId && eventSelectEl) {
            eventSelectEl.value = eventId;
            currentEventId = eventId; // 修正：同步設定 currentEventId
            eventSelectEl.dispatchEvent(new Event("change"));
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
      eventNameEl.textContent = `${data.eventName} 銷售概況`;
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

    // 銷售數據橫向進度條
    const salesBarList = document.getElementById("sales-bar-list");
    if (salesBarList && data.salesData) {
      salesBarList.innerHTML = "";
      data.salesData.forEach((ticketType) => {
        const sold = ticketType.ticketsSold || 0;
        const unsold = ticketType.unsold || 0;
        let percent = sold + unsold > 0 ? (sold / (sold + unsold)) * 100 : 0;
        let percentText = percent.toFixed(1) + "%";
        // 若有銷售但小於1%，進度條最小寬度設2%
        let barWidth = percent > 0 && percent < 1 ? 2 : percent;
        barWidth = Math.min(barWidth, 100); // 避免超過100%
        const bar = document.createElement("div");
        bar.className = "sales-bar-row";
        bar.innerHTML = `
          <span class="ticket-type">${ticketType.categoryName}</span>
          <div class="progress-bar-bg">
            <div class="progress-bar-fill" style="width: ${barWidth}%;"></div>
          </div>
          <span class="percent">${percentText}</span>
          <table class="sold-unsold-table"><tr>
            <td class="sold">已售：</td><td>${sold} 張</td>
            <td class="unsold">未售：</td><td>${unsold} 張</td>
          </tr></table>
        `;
        salesBarList.appendChild(bar);
      });
    }
    // 票種收入圓餅圖
    if (data.salesData) {
      const incomeData = data.salesData.map((item) => ({
        name: item.categoryName,
        value: item.totalRevenue || 0,
      }));
      renderIncomePieChart(incomeData);
    }

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

    // 更新銷售趨勢圖（多票種）
    if (currentEventId) {
      loadTicketTypeTrend(currentEventId);
    } else {
      clearTrendChart();
    }
    console.log("salesData", data.salesData);
  }

  function renderIncomePieChart(incomeData) {
    const chartContainer = document.getElementById("income-pie-chart");
    if (!chartContainer) return;
    if (typeof ApexCharts === "undefined") {
      chartContainer.innerHTML =
        '<div class="text-center text-danger py-5">圖表庫載入失敗</div>';
      return;
    }
    chartContainer.innerHTML = "";
    const series = incomeData.map((item) => item.value);
    const labels = incomeData.map((item) => item.name);
    const colors = [
      "#FE6D73",
      "#48cae4",
      "#17C3B2",
      "#FFCB77",
      "#A0C4FF",
      "#BDB2FF",
      "#FFD6A5",
      "#FDFFB6",
    ];
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
            return "$" + val.toLocaleString();
          },
        },
      },
      dataLabels: {
        formatter: function (val, opts) {
          const name = opts.w.globals.labels[opts.seriesIndex];
          const value = opts.w.globals.series[opts.seriesIndex];
          const total = opts.w.globals.seriesTotals.reduce((a, b) => a + b, 0);
          const percentage = total > 0 ? ((value / total) * 100).toFixed(1) : 0;
          return `${name}\n$${value.toLocaleString()} (${percentage}%)`;
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
      const chart = new ApexCharts(chartContainer, options);
      chart.render();
    } catch (error) {
      chartContainer.innerHTML =
        '<div class="text-center text-danger py-5">圖表創建失敗</div>';
    }
  }

  // --- 票種趨勢切換與明細 ---
  let lastTrendChartData = null;
  let lastTrendTypeDetail = null;
  let lastTrendCategories = null;
  let lastTrendSeries = null;
  let trendChartAutoSwitched = false; // 新增 flag

  function loadTicketTypeTrend(eventId) {
    trendChartAutoSwitched = false; // 每次載入新活動時重置
    fetch(`../eventdetail/dashboard/ticketTypeTrend?eventId=${eventId}`)
      .then((res) => res.json())
      .then((data) => {
        lastTrendCategories = data.categories;
        lastTrendSeries = data.series;
        lastTrendChartData = data.trendData;
        lastTrendTypeDetail = data.trendTypeDetail;
        renderTrendChart();
      });
  }

  function renderTrendChart() {
    const chartContainer = document.getElementById("sales-trend-chart");
    const toggleTypeTrend = document.getElementById("toggle-type-trend");
    const showTypeTrend = toggleTypeTrend && toggleTypeTrend.checked;
    const categories = lastTrendCategories || [];
    const series = lastTrendSeries || [];
    const trendData = lastTrendChartData || [];
    chartContainer.innerHTML = "";
    // 判斷資料是否為空
    // fallback: 只在單線模式下組裝
    let fallbackTrendData = [];
    let singleLineData = trendData;
    if (
      !showTypeTrend &&
      (!trendData || trendData.length === 0) &&
      series &&
      series.length > 0 &&
      categories &&
      categories.length > 0
    ) {
      for (let i = 0; i < categories.length; i++) {
        let sum = 0;
        for (let j = 0; j < series.length; j++) {
          if (Array.isArray(series[j].data)) {
            sum += Number(series[j].data[i] || 0);
          }
        }
        fallbackTrendData.push({ date: categories[i], value: sum });
      }
      singleLineData = fallbackTrendData;
    }
    let hasMultiLineData =
      showTypeTrend &&
      series &&
      series.length > 0 &&
      series.some((s) => Array.isArray(s.data) && s.data.some((v) => v > 0));
    let hasSingleLineData =
      !showTypeTrend &&
      singleLineData &&
      singleLineData.length > 0 &&
      singleLineData.some((item) => item.value > 0);
    if (
      !trendChartAutoSwitched &&
      !showTypeTrend &&
      !hasSingleLineData &&
      series &&
      series.length > 0 &&
      series.some((s) => Array.isArray(s.data) && s.data.some((v) => v > 0))
    ) {
      if (toggleTypeTrend) {
        toggleTypeTrend.checked = true;
        trendChartAutoSwitched = true;
        renderTrendChart();
        return;
      }
    }
    if (
      (showTypeTrend && !hasMultiLineData) ||
      (!showTypeTrend && !hasSingleLineData)
    ) {
      chartContainer.innerHTML =
        '<div class="text-center text-muted py-5">暫無銷售趨勢資料</div>';
      document.getElementById("trend-type-detail").innerHTML = "";
      return;
    }
    let chartOptions;
    if (showTypeTrend) {
      chartOptions = {
        chart: { type: "line", height: 350, toolbar: { show: false } },
        series: series,
        xaxis: { categories: categories, title: { text: "日期" } },
        yaxis: { title: { text: "銷售數量" } },
        stroke: { width: 3, curve: "smooth" },
        markers: { size: 5 },
        tooltip: { y: { formatter: (val) => `${val} 張` } },
        legend: { show: true },
      };
    } else {
      chartOptions = {
        chart: {
          type: "line",
          height: 350,
          toolbar: { show: false },
          events: {
            dataPointSelection: function (event, chartContext, config) {
              showTrendTypeDetail(config.dataPointIndex);
            },
          },
        },
        series: [
          {
            name: "總銷售數量",
            data: singleLineData.map((item) => item.value),
          },
        ],
        xaxis: {
          categories: singleLineData.map((item) => item.date),
          title: { text: "日期" },
        },
        yaxis: { title: { text: "銷售數量" } },
        stroke: { width: 3, curve: "smooth" },
        markers: { size: 5 },
        tooltip: { y: { formatter: (val) => `${val} 張` } },
        legend: { show: false },
      };
    }
    try {
      const chart = new ApexCharts(chartContainer, chartOptions);
      chart.render();
    } catch (e) {
      chartContainer.innerHTML =
        '<div class="text-center text-danger py-5">圖表創建失敗</div>';
    }
    document.getElementById("trend-type-detail").innerHTML = "";
  }

  function showTrendTypeDetail(dataPointIndex) {
    if (!lastTrendCategories || !lastTrendTypeDetail) return;
    const date = lastTrendCategories[dataPointIndex];
    const detail = lastTrendTypeDetail[date] || [];
    let html = `<div class='card'><div class='card-body'><h5>${date} 各票種銷售明細</h5>`;
    if (detail.length === 0) {
      html += "<div>當日無銷售資料</div>";
    } else {
      html += "<ul>";
      detail.forEach((item) => {
        html += `<li>${item.type}：${item.count} 張</li>`;
      });
      html += "</ul>";
    }
    html += "</div></div>";
    document.getElementById("trend-type-detail").innerHTML = html;
  }

  function clearDashboardData() {
    if (eventNameEl) {
      eventNameEl.textContent = "請選擇活動";
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
    clearTrendChart(); // 新增：清空銷售趨勢圖
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
      "#FE6D73", // 粉紅色
      "#48cae4", // 藍色
      "#17C3B2", // 青綠色
      "#FFCB77", // 黃色/橙色
      "#FE6D73", // 粉紅色（重複）
      "#48cae4", // 藍色（重複）
      "#17C3B2", // 青綠色（重複）
      "#FFCB77", // 黃色/橙色（重複）
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

  function clearTrendChart() {
    const chartContainer = document.querySelector("#sales-trend-chart");
    if (chartContainer) {
      chartContainer.innerHTML =
        '<div class="text-center text-muted py-5">暫無銷售趨勢資料</div>';
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

  document.addEventListener("change", function (e) {
    if (e.target && e.target.id === "toggle-type-trend") {
      renderTrendChart();
    }
  });
});
