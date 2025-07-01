document.addEventListener("DOMContentLoaded", function () {
  console.log("Dashboard.js 開始載入...");

  // 檢查 ApexCharts 是否可用
  if (typeof ApexCharts === "undefined") {
    console.error("ApexCharts 未載入！");
    return;
  } else {
    console.log("ApexCharts 已載入:", typeof ApexCharts);
  }

  // 檢查用戶權限
  const roleLevel = sessionStorage.getItem("roleLevel");
  const memberId = sessionStorage.getItem("memberId");

  console.log("權限檢查:", { roleLevel, memberId });

  if (!roleLevel || roleLevel !== "2") {
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
  const totalTicketsEl = document.querySelector("#total-tickets");
  const totalRevenueEl = document.querySelector("#total-revenue");
  const unsoldTicketsEl = document.querySelector("#unsold-tickets");
  const salesRateEl = document.querySelector("#sales-rate");
  const salesDataTableEl = document.querySelector("#sales-data-table");
  const salesChartEl = document.querySelector("#sales-chart");
  const dashboardContainerEl = document.querySelector("#dashboard-container");

  console.log("DOM 元素檢查:", {
    eventSelectEl: !!eventSelectEl,
    eventNameEl: !!eventNameEl,
    totalTicketsEl: !!totalTicketsEl,
    totalRevenueEl: !!totalRevenueEl,
    unsoldTicketsEl: !!unsoldTicketsEl,
    salesRateEl: !!salesRateEl,
    salesDataTableEl: !!salesDataTableEl,
    salesChartEl: !!salesChartEl,
    dashboardContainerEl: !!dashboardContainerEl,
  });

  // 全域變數
  let events = [];
  let currentChart = null;

  // 載入活動列表
  loadEvents();

  // 獲取會員ID的輔助函數
  function getMemberIdFromSession() {
    return sessionStorage.getItem("memberId");
  }

  // 選擇活動的輔助函數
  function selectEvent(eventId) {
    if (eventSelectEl) {
      eventSelectEl.value = eventId;
    }
    loadDashboardData(eventId);
  }

  function loadEvents() {
    console.log("開始載入活動列表");

    // 從 sessionStorage 獲取 memberId 用於顯示
    const memberId = sessionStorage.getItem("memberId");
    if (!memberId) {
      console.error("無法獲取 memberId");
      showMessage("無法獲取用戶信息，請重新登入", "error");
      return;
    }

    console.log("顯示用 memberId:", memberId);

    // 獲取路徑
    const currentPath = window.location.pathname;
    const pathParts = currentPath.split("/");
    const contextPath = pathParts.slice(0, 2).join("/"); // 取前2段: /maven-tickeasy-v1
    // 不傳遞 memberId 參數，讓後端使用 session 中的用戶ID
    const apiUrl = `${contextPath}/manager/eventdetail/dashboard`;

    console.log("當前路徑:", currentPath);
    console.log("路徑分段:", pathParts);
    console.log("上下文路徑:", contextPath);
    console.log("API URL:", apiUrl);

    fetch(apiUrl)
      .then((response) => {
        console.log("活動列表狀態:", response.status);
        return response.json();
      })
      .then((data) => {
        console.log("活動列表數據:", data);

        if (data.successful) {
          const events = data.data || [];
          console.log("解析到的活動列表:", events);

          // 清空下拉選單
          const eventSelect = document.getElementById("event-select");
          eventSelect.innerHTML = '<option value="">請選擇活動</option>';

          // 添加活動選項
          events.forEach((event) => {
            const option = document.createElement("option");
            option.value = event.eventId;
            option.textContent = event.eventName;
            eventSelect.appendChild(option);
          });

          console.log("活動下拉選單已更新，共", events.length, "個活動");
        } else {
          console.error("載入活動列表失敗:", data.message);
          showMessage("載入活動列表失敗: " + data.message, "error");
        }
      })
      .catch((error) => {
        console.error("載入活動列表時發生錯誤:", error);
        showMessage("載入活動列表時發生錯誤", "error");
      });
  }

  function loadDashboardData(eventId) {
    console.log("開始載入儀表板數據，eventId:", eventId);

    if (!eventId) {
      console.log("eventId 為空，清空儀表板");
      clearDashboard();
      return;
    }

    const currentPath = window.location.pathname;
    const pathParts = currentPath.split("/");
    const contextPath = pathParts.slice(0, 2).join("/"); // 取前2段: /maven-tickeasy-v1
    const apiUrl = `${contextPath}/manager/eventdetail/dashboard?eventId=${eventId}`;

    console.log("當前路徑:", currentPath);
    console.log("路徑分段:", pathParts);
    console.log("上下文路徑:", contextPath);
    console.log("API URL:", apiUrl);

    fetch(apiUrl)
      .then((response) => {
        console.log("儀表板數據響應狀態:", response.status);
        return response.json();
      })
      .then((data) => {
        console.log("儀表板數據響應:", data);

        if (data.successful) {
          const result = data.data;
          if (result) {
            updateDashboard(result);
          } else {
            console.log("沒有儀表板數據");
            clearDashboard();
            showMessage("該活動目前沒有銷售數據", "info");
          }
        } else {
          console.error("載入儀表板數據失敗:", data.message);
          showMessage("載入儀表板數據失敗: " + data.message, "error");
          clearDashboard();
        }
      })
      .catch((error) => {
        console.error("載入儀表板數據時發生錯誤:", error);
        showMessage("載入儀表板數據時發生錯誤", "error");
        clearDashboard();
      });
  }

  function updateDashboard(data) {
    console.log("更新儀表板數據:", data);

    // 直接使用後端回傳的數據
    if (eventNameEl)
      eventNameEl.textContent = data.eventName || "活動銷售儀表板";
    if (totalTicketsEl)
      totalTicketsEl.textContent = (data.soldCount || 0).toLocaleString();
    if (totalRevenueEl)
      totalRevenueEl.textContent = `$ ${(
        data.totalRevenue || 0
      ).toLocaleString()}`;
    if (unsoldTicketsEl)
      unsoldTicketsEl.textContent = (data.unsold || 0).toLocaleString();
    if (salesRateEl)
      salesRateEl.textContent = `${(data.salesRate || 0).toFixed(1)}%`;

    // 填充銷售數據表格
    if (salesDataTableEl) {
      salesDataTableEl.innerHTML = ""; // 清空舊數據
      if (data.salesData && data.salesData.length > 0) {
        data.salesData.forEach((item) => {
          const row = `<tr>
                      <td>${item.categoryName}</td>
                      <td>${
                        item.ticketsSold != null
                          ? item.ticketsSold.toLocaleString()
                          : "0"
                      }</td>
                      <td>$ ${
                        item.totalRevenue != null
                          ? item.totalRevenue.toLocaleString()
                          : "0"
                      }</td>
                      <td>${
                        item.unsold != null ? item.unsold.toLocaleString() : "0"
                      }</td>
                  </tr>`;
          salesDataTableEl.innerHTML += row;
        });
      } else {
        salesDataTableEl.innerHTML =
          "<tr><td colspan='4' class='text-center'>暫無銷售數據</td></tr>";
      }
    }

    // 渲染圓餅圖
    if (salesChartEl && data.salesData) {
      console.log("準備渲染圖表，銷售數據:", data.salesData);
      renderChart(data.salesData);
    } else {
      console.warn("無法渲染圖表:", {
        salesChartEl: !!salesChartEl,
        salesData: !!data.salesData,
      });
    }
  }

  function clearDashboard() {
    if (eventNameEl) eventNameEl.textContent = "活動銷售儀表板";
    if (totalTicketsEl) totalTicketsEl.textContent = "0";
    if (totalRevenueEl) totalRevenueEl.textContent = "$0";
    if (unsoldTicketsEl) unsoldTicketsEl.textContent = "0";
    if (salesRateEl) salesRateEl.textContent = "0%";
    if (salesDataTableEl) salesDataTableEl.innerHTML = "";

    // 清理圖表
    if (currentChart) {
      currentChart.destroy();
      currentChart = null;
    }
    if (salesChartEl) salesChartEl.innerHTML = "";
  }

  // 頁面卸載時清理圖表資源
  window.addEventListener("beforeunload", function () {
    if (currentChart) {
      currentChart.destroy();
      currentChart = null;
    }
  });

  // 監聽下拉選單變化
  if (eventSelectEl) {
    eventSelectEl.addEventListener("change", function () {
      const selectedEventId = this.value;
      loadDashboardData(selectedEventId);
    });
  }

  function renderChart(salesData) {
    console.log("開始渲染圖表，數據:", salesData);

    if (!salesChartEl) {
      console.error("salesChartEl 不存在");
      return;
    }

    if (!salesData || salesData.length === 0) {
      console.warn("沒有銷售數據可顯示");
      return;
    }

    // 準備圖表數據：包含已售出和未銷售
    const chartSeries = [];
    const chartLabels = [];
    const chartColors = [];

    salesData.forEach((item) => {
      const totalForCategory = item.totalTickets || 0;
      const soldForCategory = item.ticketsSold || 0;
      const unsoldForCategory = totalForCategory - soldForCategory;

      // 添加已售出數據
      if (soldForCategory > 0) {
        chartSeries.push(soldForCategory);
        chartLabels.push(`${item.categoryName} (已售出)`);
        chartColors.push("#28a745"); // 綠色表示已售出
      }

      // 添加未銷售數據
      if (unsoldForCategory > 0) {
        chartSeries.push(unsoldForCategory);
        chartLabels.push(`${item.categoryName} (未銷售)`);
        chartColors.push("#ffc107"); // 黃色表示未銷售
      }
    });

    const chartOptions = {
      series: chartSeries,
      chart: {
        type: "donut",
        height: 400,
        animations: {
          enabled: true,
          easing: "easeinout",
          speed: 800,
          animateGradually: {
            enabled: true,
            delay: 150,
          },
          dynamicAnimation: {
            enabled: true,
            speed: 350,
          },
        },
      },
      labels: chartLabels,
      colors: chartColors,
      legend: {
        position: "bottom",
        fontSize: "12px",
      },
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

    console.log("圖表配置:", chartOptions);

    // 如果圖表已存在，使用 updateOptions 更新數據
    if (currentChart) {
      console.log("更新現有圖表");
      currentChart.updateOptions(
        {
          series: chartOptions.series,
          labels: chartOptions.labels,
          colors: chartOptions.colors,
        },
        false,
        true
      ); // false: 不重新渲染, true: 重新繪製
    } else {
      // 第一次創建圖表
      console.log("創建新圖表");
      try {
        currentChart = new ApexCharts(salesChartEl, chartOptions);
        currentChart.render();
        console.log("圖表創建成功");
      } catch (error) {
        console.error("創建圖表時發生錯誤:", error);
      }
    }
  }

  // 顯示消息函數
  function showMessage(message, type = "info") {
    console.log(`${type.toUpperCase()}: ${message}`);

    // 創建消息元素
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

    // 插入到頁面頂部
    const container =
      document.querySelector(".content-wrapper") || document.body;
    container.insertBefore(messageEl, container.firstChild);

    // 3秒後自動移除
    setTimeout(() => {
      if (messageEl.parentNode) {
        messageEl.remove();
      }
    }, 3000);
  }
});
