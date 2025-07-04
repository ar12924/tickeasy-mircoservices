document.addEventListener("DOMContentLoaded", function () {
  console.log("Dashboard 頁面開始載入...");

  // 檢查用戶權限
  const roleLevel = sessionStorage.getItem("roleLevel");
  const memberId = sessionStorage.getItem("memberId");

  console.log("權限檢查:", { roleLevel, memberId });

  if (!roleLevel || (roleLevel !== "2" && roleLevel !== "3")) {
    alert("您沒有權限訪問此頁面");
    window.location.href = "user/member/login.html";
    return;
  }

  if (!memberId) {
    alert("請先登入");
    window.location.href = "user/member/login.html";
    return;
  }

  // DOM Elements
  const eventSelectEl = document.querySelector("#event-select");
  const dashboardContainerEl = document.querySelector("#dashboard-container");
  const eventNameEl = document.querySelector("#event-name");

  // 全域變數
  let events = [];
  let currentEventId = null;

  // 初始化頁面
  initPage();

  function initPage() {
    console.log("初始化儀表板...");
    loadEvents();
    setupEventListeners();
  }

  function setupEventListeners() {
    if (eventSelectEl) {
      eventSelectEl.addEventListener("change", function () {
        currentEventId = this.value;
        console.log("選擇活動:", currentEventId);
        if (currentEventId) {
          loadDashboardData(currentEventId);
        } else {
          clearDashboardData();
        }
      });
    }
  }

  function loadEvents() {
    console.log("開始載入活動列表");

    const memberId = sessionStorage.getItem("memberId");
    if (!memberId) {
      console.error("無法獲取 memberId");
      showMessage("無法獲取用戶信息，請重新登入", "error");
      return;
    }

    console.log("使用 memberId:", memberId);

    fetch(`../eventdetail/dashboard?memberId=${memberId}`, {
      credentials: "include",
    })
      .then((response) => {
        console.log("活動列表響應狀態:", response.status);
        if (!response.ok) {
          throw new Error(`HTTP 錯誤! 狀態: ${response.status}`);
        }
        return response.json();
      })
      .then((data) => {
        console.log("活動列表響應數據:", data);

        if (data.successful) {
          const events = data.data || [];
          console.log("解析到的活動列表:", events);

          if (eventSelectEl) {
            eventSelectEl.innerHTML = '<option value="">請選擇活動</option>';

            events.forEach((event) => {
              const option = document.createElement("option");
              option.value = event.eventId;
              option.textContent = event.eventName;
              eventSelectEl.appendChild(option);
            });
            console.log("活動下拉選單已更新，共", events.length, "個活動");
          } else {
            console.error("找不到 #event-select 元素");
          }
        } else {
          console.error("載入活動列表失敗:", data.message);
          showMessage("載入活動列表失敗: " + data.message, "error");
        }
      })
      .catch((error) => {
        console.error("載入活動列表時發生錯誤:", error);
        showMessage("載入活動列表時發生錯誤: " + error.message, "error");
      });
  }

  function loadDashboardData(eventId) {
    console.log(`開始載入儀表板數據，eventId: ${eventId}`);

    if (!eventId) {
      console.log("eventId 為空，清空儀表板數據");
      clearDashboardData();
      return;
    }

    fetch(`../eventdetail/dashboard?eventId=${eventId}`, {
      credentials: "include",
    })
      .then((response) => {
        console.log("儀表板數據響應狀態:", response.status);
        if (!response.ok) {
          throw new Error(`HTTP 錯誤! 狀態: ${response.status}`);
        }
        return response.json();
      })
      .then((data) => {
        console.log("儀表板數據響應:", data);

        if (data.successful) {
          const result = data.data;
          if (result) {
            displayDashboardData(result);
          } else {
            console.log("沒有儀表板數據");
            clearDashboardData();
            showMessage("該活動目前沒有銷售數據", "info");
          }
        } else {
          console.error("載入儀表板數據失敗:", data.message);
          showMessage("載入儀表板數據失敗: " + data.message, "error");
          clearDashboardData();
        }
      })
      .catch((error) => {
        console.error("載入儀表板數據時發生錯誤:", error);
        showMessage("載入儀表板數據時發生錯誤: " + error.message, "error");
        clearDashboardData();
      });
  }

  function displayDashboardData(data) {
    console.log("顯示儀表板數據:", data);

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

    // 更新圖表（如果有的話）
    if (window.ApexCharts && data.chartData) {
      updateChart(data.chartData);
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
  }

  function updateChart(chartData) {
    // 這裡可以添加圖表更新邏輯
    console.log("更新圖表數據:", chartData);
  }

  function showMessage(message, type = "info") {
    console.log(`${type.toUpperCase()}: ${message}`);

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
