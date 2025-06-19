document.addEventListener("DOMContentLoaded", function () {
  const urlParams = new URLSearchParams(window.location.search);
  const eventId = urlParams.get("eventId");
  let currentPage = 1;
  const pageSize = 10;

  // DOM Elements
  const searchForm = document.querySelector("#search-form");
  const tableBody = document.querySelector("#participant-table-body");
  const paginationEl = document.querySelector("#pagination");
  const resultSummaryEl = document.querySelector("#result-summary");
  const container = document.querySelector(".container");

  if (!eventId) {
    container.innerHTML =
      '<div class="alert alert-danger">錯誤：URL 中未提供 eventId。</div>';
    return;
  }

  function fetchParticipants(page = 1) {
    currentPage = page;

    const formData = new FormData(searchForm);
    const searchParams = {};
    for (const [key, value] of formData.entries()) {
      if (value) {
        searchParams[key] = value;
      }
    }

    const params = new URLSearchParams({
      eventId: eventId,
      pageNumber: page,
      pageSize: pageSize,
      ...searchParams,
    });

    const API_URL = `/maven-tickeasy-v1/participants?eventId=${eventId}&pageNumber=${page}&pageSize=${pageSize}`;

    fetch(API_URL)
      .then((response) => response.json())
      .then((result) => {
        if (!result.successful) {
          alert(result.message || "無法載入報名人列表");
          tableBody.innerHTML = `<tr><td colspan="7" class="text-center text-danger">${
            result.message || "查詢失敗"
          }</td></tr>`;
          resultSummaryEl.textContent = "";
          paginationEl.innerHTML = "";
          return;
        }

        const participants = result.data.data;
        const total = result.data.total;
        updateTable(participants);
        updatePagination(total);
      })
      .catch((error) => {
        console.error("獲取報名人列表時出錯:", error);
        tableBody.innerHTML = `<tr><td colspan="7" class="text-center text-danger">請求失敗: ${error.message}</td></tr>`;
        resultSummaryEl.textContent = "";
        paginationEl.innerHTML = "";
      });
  }

  function updateTable(participants) {
    tableBody.innerHTML = "";
    if (!participants || participants.length === 0) {
      tableBody.innerHTML =
        '<tr><td colspan="7" class="text-center">沒有找到任何報名人。</td></tr>';
      return;
    }

    participants.forEach((p) => {
      const statusText = p.status === 1 ? "有效" : "已取消";
      const detailUrl = `participant-detail.html?ticketId=${p.ticketId}`;
      const row = `
                <tr>
                    <td>${p.ticketId}</td>
                    <td>${p.participantName}</td>
                    <td>${p.email}</td>
                    <td>${p.phone}</td>
                    <td>${p.eventTicketType.categoryName}</td>
                    <td>${statusText}</td>
                    <td><a href="${detailUrl}" class="btn btn-sm btn-info">查看詳情</a></td>
                </tr>`;
      tableBody.innerHTML += row;
    });
  }

  function updatePagination(total) {
    const totalPages = Math.ceil(total / pageSize);
    paginationEl.innerHTML = "";
    resultSummaryEl.textContent = `共 ${total} 筆資料`;

    for (let i = 1; i <= totalPages; i++) {
      const liClass = i === currentPage ? "page-item active" : "page-item";
      const pageLink = `<li class="${liClass}"><a class="page-link" href="#" data-page="${i}">${i}</a></li>`;
      paginationEl.innerHTML += pageLink;
    }
  }

  // Initial load
  fetchParticipants(1);

  // Search form submission
  searchForm.addEventListener("submit", function (e) {
    e.preventDefault();
    fetchParticipants(1);
  });

  // Pagination click
  paginationEl.addEventListener("click", function (e) {
    e.preventDefault();
    if (e.target.matches(".page-link")) {
      const page = Number(e.target.getAttribute("data-page"));
      if (page !== currentPage) {
        fetchParticipants(page);
      }
    }
  });
});
