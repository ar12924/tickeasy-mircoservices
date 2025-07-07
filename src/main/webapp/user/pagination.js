/**
 * 遍歷資料，填充並渲染 HTML 元素
 * @param {Array<Object>} data - 從API取得的資料。
 */
function renderContent(data) {
  let html = '<div class="columns is-multiline">';

  data.forEach((item) => {
    html += `
                    <div class="column is-6">
                        <div class="card content-item">
                            <div class="card-image">
                                <figure class="image is-16by9">
                                    <img src="${item.image}" alt="${
      item.title
    }">
                                </figure>
                            </div>
                            <div class="card-content">
                                <div class="media">
                                    <div class="media-content">
                                        <p class="title is-5">${item.title}</p>
                                        <p class="subtitle is-6 has-text-grey">${
                                          item.host
                                        }</p>
                                    </div>
                                </div>
                                <div class="content">
                                    <p class="mb-3">${item.description}</p>
                                    <div class="field is-grouped is-grouped-multiline mb-3">
                                        ${item.tags
                                          .map(
                                            (tag) =>
                                              `<div class="control"><span class="tag is-light">${tag}</span></div>`
                                          )
                                          .join("")}
                                    </div>
                                    <div class="level is-mobile">
                                        <div class="level-left">
                                            <div class="level-item">
                                                <span class="icon has-text-info"><i class="fas fa-map-marker-alt"></i></span>
                                                <span class="ml-1">${
                                                  item.location
                                                }</span>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="level is-mobile">
                                        <div class="level-left">
                                            <div class="level-item">
                                                <span class="icon has-text-primary"><i class="fas fa-calendar"></i></span>
                                                <span class="ml-1">${
                                                  item.date
                                                }</span>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="level is-mobile mt-4">
                                        <div class="level-left">
                                            <div class="level-item">
                                                <span class="tag is-success is-medium">${
                                                  item.price
                                                }</span>
                                            </div>
                                        </div>
                                        <div class="level-right">
                                            <div class="level-item">
                                                <button class="button is-primary">
                                                    <span class="icon"><i class="fas fa-ticket-alt"></i></span>
                                                    <span>購票</span>
                                                </button>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                `;
  });

  html += "</div>";
  $("#content-container").html(html);
}

/**
 * 渲染 pagination 部分 HTML 元素
 * @param {number} totalItems - API資料的總資料個數。
 * @param {number} currentPage - 當前點擊頁數。
 * @param {number} itemsPerPage - 每頁內容個數。
 * @returns
 */
function renderPagination(totalItems, currentPage, itemsPerPage) {
  const totalPages = Math.ceil(totalItems / itemsPerPage);
  if (totalPages <= 1) {
    $("#pagination-container").html("");
    return;
  }

  let html = "";

  // 上一頁按鈕
  html += `<a class="pagination-previous${
    currentPage === 1 ? " is-disabled" : ""
  }" data-page="${currentPage - 1}">
                        <span class="icon"><i class="fas fa-chevron-left"></i></span>
                        <span>上一頁</span>
                     </a>`;

  // 下一頁按鈕
  html += `<a class="pagination-next${
    currentPage === totalPages ? " is-disabled" : ""
  }" data-page="${currentPage + 1}">
                        <span>下一頁</span>
                        <span class="icon"><i class="fas fa-chevron-right"></i></span>
                     </a>`;

  // 頁碼列表
  html += '<ul class="pagination-list">';

  // 第一頁
  if (currentPage > 3) {
    html += `<li><a class="pagination-link" data-page="1">1</a></li>`;
    if (currentPage > 4) {
      html += '<li><span class="pagination-ellipsis">&hellip;</span></li>';
    }
  }

  // 當前頁面附近的頁碼(介於 currentPage - 2 到 currentPage + 2 之間)
  for (
    let i = Math.max(1, currentPage - 2);
    i <= Math.min(totalPages, currentPage + 2);
    i++
  ) {
    html += `<li><a class="pagination-link${
      i === currentPage ? " is-current" : ""
    }" data-page="${i}">${i}</a></li>`;
  }

  // 最後一頁
  if (currentPage < totalPages - 2) {
    if (currentPage < totalPages - 3) {
      html += '<li><span class="pagination-ellipsis">&hellip;</span></li>';
    }
    html += `<li><a class="pagination-link" data-page="${totalPages}">${totalPages}</a></li>`;
  }

  html += "</ul>";

  $("#pagination-container").html(html);
}

//
/**
 * 顯示指定頁面(含主內容、pagination)
 * @param {number} page - 指定頁數。
 * @param {number} itemsPerPage - 每頁內容個數。
 * @param {Array<Object>} filteredData - 從API取得的資料。
 */
function showPage(page, itemsPerPage, filteredData) {
  const startIndex = (page - 1) * itemsPerPage;
  const endIndex = startIndex + itemsPerPage;
  const pageData = filteredData.slice(startIndex, endIndex);

  renderContent(pageData);
  renderPagination(filteredData.length, page, itemsPerPage);

  // 滾動到頂部
  $("html, body").animate(
    {
      scrollTop: $("#content-container").offset().top,
    },
    300
  );
}

// 初始化
(async function () {
  // 模擬資料
  const resp = await fetch("./buy/data/pagination-sample.json");
  const mockData = await resp.json();

  // 頁碼相關參數初始化
  let currentPage = 1;
  const itemsPerPage = 4;
  let filteredData = [...mockData];

  // 預設載入第1頁
  showPage(1, itemsPerPage, filteredData);

  // 分頁點擊事件
  $(document).on(
    "click",
    ".pagination-link, .pagination-previous, .pagination-next",
    function (e) {
      e.preventDefault();
      const page = parseInt($(this).data("page"));
      if (
        page &&
        page >= 1 &&
        page <= Math.ceil(filteredData.length / itemsPerPage) &&
        !$(this).hasClass("is-disabled")
      ) {
        showPage(page, itemsPerPage, filteredData);
      }
    }
  );
})();
