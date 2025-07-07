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
(function () {
  // 模擬資料
  const mockData = [
    {
      id: 1,
      title: "2025 春季搖滾音樂節",
      host: "閃電娛樂",
      location: "台北體育館",
      date: "2025-05-15 18:00",
      price: "NT$ 1,200 起",
      image:
        "https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=400&h=300&fit=crop",
      tags: ["音樂", "搖滾", "熱門"],
      description: "年度最盛大的搖滾音樂節，匯集國內外頂尖搖滾樂團",
    },
    {
      id: 2,
      title: "台北交響樂團2025年度音樂會",
      host: "台北交響樂團",
      location: "國家音樂廳",
      date: "2025-06-10 19:30",
      price: "NT$ 800 起",
      image:
        "https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=400&h=300&fit=crop",
      tags: ["音樂", "古典", "精緻"],
      description: "台北交響樂團年度壓軸演出，呈現貝多芬第九號交響曲",
    },
    {
      id: 3,
      title: "《人生如戲》舞台劇",
      host: "光影劇團",
      location: "國家戲劇院",
      date: "2025-05-25 19:30",
      price: "NT$ 600 起",
      image:
        "https://images.unsplash.com/photo-1503095396549-807759245b35?w=400&h=300&fit=crop",
      tags: ["戲劇", "藝術", "深度"],
      description: "獲獎無數的現代舞台劇，探討生活與戲劇的界線",
    },
    {
      id: 4,
      title: "2025中華職棒開幕戰",
      host: "中華職棒",
      location: "台北棒球場",
      date: "2025-03-15 14:00",
      price: "NT$ 300 起",
      image:
        "https://images.unsplash.com/photo-1566577739112-5180d4bf9390?w=400&h=300&fit=crop",
      tags: ["運動", "棒球", "熱血"],
      description: "2025年中華職棒聯賽開幕戰，四支球隊齊聚一堂",
    },
    {
      id: 5,
      title: "2025台北國際馬拉松",
      host: "台北市政府",
      location: "台北市政府廣場",
      date: "2025-04-20 06:00",
      price: "NT$ 1,500 起",
      image:
        "https://images.unsplash.com/photo-1544639101-63508cb8ea40?w=400&h=300&fit=crop",
      tags: ["運動", "馬拉松", "挑戰"],
      description: "台灣最大規模的馬拉松賽事，設有全馬、半馬和健康跑",
    },
    {
      id: 6,
      title: "當代藝術展：未來視界",
      host: "新視野藝術中心",
      location: "台北當代藝術館",
      date: "2025-05-10 10:00",
      price: "NT$ 250 起",
      image:
        "https://images.unsplash.com/photo-1518709268805-4e9042af2176?w=400&h=300&fit=crop",
      tags: ["藝術", "展覽", "創新"],
      description: "匯集國際當代藝術家作品，探索科技與藝術的融合",
    },
    {
      id: 7,
      title: "《貓》音樂劇台北站",
      host: "環球劇場",
      location: "台北文化中心",
      date: "2025-06-20 19:00",
      price: "NT$ 1,800 起",
      image:
        "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&h=300&fit=crop",
      tags: ["音樂劇", "經典", "國際"],
      description: "百老匯經典音樂劇《貓》首次來台，原版班底傾力演出",
    },
    {
      id: 8,
      title: "2025創業趨勢論壇",
      host: "創業台灣基金會",
      location: "台北文創大樓",
      date: "2025-05-05 09:00",
      price: "NT$ 500 起",
      image:
        "https://images.unsplash.com/photo-1515169067868-5387ec356754?w=400&h=300&fit=crop",
      tags: ["論壇", "創業", "商業"],
      description: "匯集成功創業家和投資人，分享2025年創業趨勢與機會",
    },
    {
      id: 9,
      title: "2025人工智能發展峰會",
      host: "台灣AI學會",
      location: "台灣大學綜合體育館",
      date: "2025-06-25 09:30",
      price: "NT$ 1,000 起",
      image:
        "https://images.unsplash.com/photo-1485827404703-89b55fcc595e?w=400&h=300&fit=crop",
      tags: ["科技", "AI", "未來"],
      description: "聚焦人工智能最新發展，邀請全球頂尖AI專家演講",
    },
    {
      id: 10,
      title: "台北美食節2025",
      host: "台北市觀光傳播局",
      location: "信義區香堤大道",
      date: "2025-07-01 11:00",
      price: "免費入場",
      image:
        "https://images.unsplash.com/photo-1414235077428-338989a2e8c0?w=400&h=300&fit=crop",
      tags: ["美食", "節慶", "免費"],
      description: "台北年度美食盛會，集結各地特色料理與街頭小食",
    },
  ];
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
