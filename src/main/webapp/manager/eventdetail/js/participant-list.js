document.addEventListener("DOMContentLoaded", function () {
  console.log("Participant List 頁面開始載入...");

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
  const participantTableEl = document.querySelector("#participant-table");
  const participantCountEl = document.querySelector("#participant-count");
  const loadingEl = document.querySelector("#loading");
  const noDataEl = document.querySelector("#no-data");

  console.log("DOM 元素檢查:", {
    eventSelectEl: !!eventSelectEl,
    participantTableEl: !!participantTableEl,
    participantCountEl: !!participantCountEl,
    loadingEl: !!loadingEl,
    noDataEl: !!noDataEl,
  });

  // 全域變數
  let events = [];
  let currentEventId = null;
  let currentPage = 1;
  const pageSize = 10; // 每頁顯示數量

  // 初始化頁面
  initPage();

  function initPage() {
    console.log("初始化頁面...");
    loadEvents();
    setupEventListeners();
  }

  function setupEventListeners() {
    if (eventSelectEl) {
      eventSelectEl.addEventListener("change", function () {
        currentEventId = this.value;
        currentPage = 1; // 切換活動時，重置回第一頁
        console.log("選擇活動:", currentEventId);
        if (currentEventId) {
          loadParticipants(currentEventId);
        } else {
          clearParticipantTable();
        }
      });
    }

    // 為搜尋按鈕新增事件監聽器
    const filterBtn = document.querySelector("#filter-btn");
    if (filterBtn) {
      filterBtn.addEventListener("click", function () {
        currentPage = 1; // 執行新搜尋時，重置回第一頁
        loadParticipants(currentEventId);
      });
    }

    // 為篩選輸入框和下拉選單新增事件
    const nameFilter = document.querySelector("#name-filter");
    const emailFilter = document.querySelector("#email-filter");
    const ticketTypeFilter = document.querySelector("#ticket-type-select");

    const triggerSearch = () => {
      currentPage = 1;
      loadParticipants(currentEventId);
    };

    if (nameFilter) {
      nameFilter.addEventListener("keypress", (e) => {
        if (e.key === "Enter") triggerSearch();
      });
    }
    if (emailFilter) {
      emailFilter.addEventListener("keypress", (e) => {
        if (e.key === "Enter") triggerSearch();
      });
    }
    if (ticketTypeFilter) {
      ticketTypeFilter.addEventListener("change", triggerSearch);
    }
  }

  function loadEvents() {
    console.log("開始載入活動列表");

    // 從 sessionStorage 獲取 memberId
    const memberId = sessionStorage.getItem("memberId");
    if (!memberId) {
      console.error("無法獲取 memberId");
      showMessage("無法獲取用戶信息，請重新登入", "error");
      return;
    }

    console.log("使用 memberId:", memberId);

    // 獲取應用程序的上下文路徑
    const currentPath = window.location.pathname;
    const pathParts = currentPath.split("/");
    const contextPath = pathParts.slice(0, 2).join("/"); // 取前2段: /maven-tickeasy-v1
    const apiUrl = `${contextPath}/manager/eventdetail/participants?memberId=${memberId}`;

    console.log("API URL:", apiUrl);

    fetch(apiUrl, {
      credentials: "include",
    })
      .then((response) => {
        console.log("活動列表響應狀態:", response.status);
        return response.json();
      })
      .then((data) => {
        console.log("活動列表響應數據:", data);

        if (data.successful) {
          const events = data.data || [];
          console.log("解析到的活動列表:", events);

          if (eventSelectEl) {
            // 清空下拉選單
            eventSelectEl.innerHTML = '<option value="">請選擇活動</option>';

            // 添加活動選項
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
        showMessage("載入活動列表時發生錯誤", "error");
      });
  }

  function loadParticipants(eventId, page = 1) {
    console.log(`開始載入參與者列表，eventId: ${eventId}, page: ${page}`);
    currentEventId = eventId;
    currentPage = page;

    if (!currentEventId) {
      console.log("eventId 為空，清空參與者列表");
      clearParticipantTable();
      return;
    }

    // 收集篩選條件
    const nameFilter = document.querySelector("#name-filter")?.value || "";
    const emailFilter = document.querySelector("#email-filter")?.value || "";
    const ticketTypeFilter =
      document.querySelector("#ticket-type-select")?.value || "";

    console.log("篩選條件:", {
      name: nameFilter,
      email: emailFilter,
      ticketType: ticketTypeFilter,
    });

    // 獲取應用程序的上下文路徑
    const currentPath = window.location.pathname;
    const pathParts = currentPath.split("/");
    const contextPath = pathParts.slice(0, 2).join("/");

    let apiUrl = `${contextPath}/manager/eventdetail/participants?eventId=${currentEventId}&pageNumber=${currentPage}&pageSize=${pageSize}`;
    if (nameFilter) apiUrl += `&participantName=${nameFilter}`;
    if (emailFilter) apiUrl += `&email=${emailFilter}`;
    if (ticketTypeFilter) apiUrl += `&ticketTypeId=${ticketTypeFilter}`;

    console.log("API URL:", apiUrl);

    fetch(apiUrl, {
      credentials: "include",
    })
      .then((response) => {
        console.log("參與者列表響應狀態:", response.status);
        return response.json();
      })
      .then((data) => {
        console.log("參與者列表響應數據:", data);

        if (data.successful) {
          const result = data.data;
          if (result && result.participants) {
            displayParticipants(result.participants);
            // 呼叫 displayTicketTypes
            if (result.ticketTypes) {
              displayTicketTypes(result.ticketTypes);
            }
            // 呼叫 updatePagination
            updatePagination(result.total, currentPage, pageSize);
          } else {
            console.log("沒有參與者數據");
            clearParticipantTable();
            showMessage("該活動目前沒有參與者", "info");
          }
        } else {
          console.error("載入參與者列表失敗:", data.message);
          showMessage("載入參與者列表失敗: " + data.message, "error");
          clearParticipantTable();
        }
      })
      .catch((error) => {
        console.error("載入參與者列表時發生錯誤:", error);
        showMessage("載入參與者列表時發生錯誤", "error");
        clearParticipantTable();
      });
  }

  function displayParticipants(participants) {
    if (!participantTableEl) return;

    if (!participants || participants.length === 0) {
      showNoData("該活動暫無參與者");
      return;
    }

    // 更新參與者數量
    if (participantCountEl) {
      participantCountEl.textContent = participants.length;
    }

    // 清空表格
    participantTableEl.innerHTML = "";

    // 添加表頭
    const thead = document.createElement("thead");
    thead.innerHTML = `
      <tr>
        <th>序號</th>
        <th>參與者姓名</th>
        <th>電子郵件</th>
        <th>電話號碼</th>
        <th>票券類型</th>
        <th>狀態</th>
        <th>購買時間</th>
        <th>操作</th>
      </tr>
    `;
    participantTableEl.appendChild(thead);

    // 添加表格內容
    const tbody = document.createElement("tbody");
    participants.forEach((participant, index) => {
      const statusText = participant.status === 1 ? "有效" : "已取消";
      const row = document.createElement("tr");
      row.innerHTML = `
        <td>${index + 1}</td>
        <td>${participant.participantName || "未提供"}</td>
        <td>${participant.email || "未提供"}</td>
        <td>${participant.phone || "未提供"}</td>
        <td>${
          participant.eventTicketType
            ? participant.eventTicketType.categoryName
            : "未指定"
        }</td>
        <td>${statusText}</td>
        <td>${formatDate(
          participant.buyerOrder ? participant.buyerOrder.orderTime : null
        )}</td>
        <td>
          <button class="btn btn-sm btn-info" onclick="viewParticipantDetail(${
            participant.ticketId
          })">
            查看詳情
          </button>
        </td>
      `;
      tbody.appendChild(row);
    });
    participantTableEl.appendChild(tbody);

    // 隱藏無數據提示
    if (noDataEl) {
      noDataEl.style.display = "none";
    }
  }

  function clearParticipantTable() {
    if (participantTableEl) {
      participantTableEl.innerHTML = "";
    }
    if (participantCountEl) {
      participantCountEl.textContent = "0";
    }
    showNoData("請先從上方選擇一個活動");

    // 清空分頁和票種
    const paginationContainer = document.querySelector("#pagination-container");
    if (paginationContainer) paginationContainer.innerHTML = "";
    const ticketTypeSelectEl = document.querySelector("#ticket-type-select");
    if (ticketTypeSelectEl)
      ticketTypeSelectEl.innerHTML = '<option value="">所有票種</option>';
  }

  function showLoading(show) {
    if (loadingEl) {
      loadingEl.style.display = show ? "block" : "none";
    }
  }

  function showNoData(message) {
    if (noDataEl) {
      noDataEl.textContent = message || "暫無數據";
      noDataEl.style.display = "block";
    }
    if (participantCountEl) {
      participantCountEl.textContent = "0";
    }
  }

  function formatDate(dateString) {
    if (!dateString) return "未提供";
    try {
      const date = new Date(dateString);
      return date.toLocaleString("zh-TW");
    } catch (error) {
      return dateString;
    }
  }

  // 全域函數，供 HTML 調用
  window.viewParticipantDetail = function (ticketId) {
    console.log("查看參與者詳情，ticketId:", ticketId);
    // 跳轉到參與者詳情頁面
    window.location.href = `/maven-tickeasy-v1/manager/eventdetail/participant-detail.html?ticketId=${ticketId}`;
  };

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

  // 新增 displayTicketTypes 函式
  function displayTicketTypes(ticketTypes) {
    const ticketTypeSelectEl = document.querySelector("#ticket-type-select");
    if (!ticketTypeSelectEl) return;

    // 保存目前選擇的值
    const selectedValue = ticketTypeSelectEl.value;

    // 清空選項（保留第一個"所有票種"）
    ticketTypeSelectEl.innerHTML = '<option value="">所有票種</option>';

    ticketTypes.forEach((type) => {
      const option = document.createElement("option");
      option.value = type.typeId;
      option.textContent = type.categoryName;
      ticketTypeSelectEl.appendChild(option);
    });

    // 恢復選擇
    ticketTypeSelectEl.value = selectedValue;
  }

  // 新增 updatePagination 函式
  function updatePagination(totalItems, currentPage, pageSize) {
    const paginationContainer = document.querySelector("#pagination-container");
    if (!paginationContainer) return;

    paginationContainer.innerHTML = "";
    const totalPages = Math.ceil(totalItems / pageSize);

    if (totalPages <= 1) return;

    // 上一頁按鈕
    const prevLi = document.createElement("li");
    prevLi.className = `page-item ${currentPage === 1 ? "disabled" : ""}`;
    prevLi.innerHTML = `<a class="page-link" href="#" data-page="${
      currentPage - 1
    }">上一頁</a>`;
    paginationContainer.appendChild(prevLi);

    // 頁碼按鈕
    for (let i = 1; i <= totalPages; i++) {
      const pageLi = document.createElement("li");
      pageLi.className = `page-item ${i === currentPage ? "active" : ""}`;
      pageLi.innerHTML = `<a class="page-link" href="#" data-page="${i}">${i}</a>`;
      paginationContainer.appendChild(pageLi);
    }

    // 下一頁按鈕
    const nextLi = document.createElement("li");
    nextLi.className = `page-item ${
      currentPage === totalPages ? "disabled" : ""
    }`;
    nextLi.innerHTML = `<a class="page-link" href="#" data-page="${
      currentPage + 1
    }">下一頁</a>`;
    paginationContainer.appendChild(nextLi);

    // 為所有分頁連結加上事件監聽
    paginationContainer.querySelectorAll(".page-link").forEach((link) => {
      link.addEventListener("click", function (e) {
        e.preventDefault();
        const page = parseInt(this.dataset.page);
        if (page && page !== currentPage) {
          loadParticipants(currentEventId, page);
        }
      });
    });
  }
});
