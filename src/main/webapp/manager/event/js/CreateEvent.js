//(() => {
//  // 按鈕、訊息區、各輸入欄位
//  const nextBtn = document.querySelector('#btnNext');    // 為你的「下一步」按鈕加個 id="#btnNext"
//  const msgArea = document.querySelector('#msg');        // 顯示錯誤訊息的 <div id="msg"></div>
//  const eventNameInput = document.querySelector('#event_name');
//  const eventFromInput = document.querySelector('#event_from_date');
//  const eventToInput = document.querySelector('#event_to_date');
//  const placeInput = document.querySelector('#place');
//  const summaryInput = document.querySelector('#summary');
//  const summernoteEditor = $('#summernote');
//  const categoryCheckboxes = document.querySelectorAll('input.category-checkbox');
//  
//
//  nextBtn.addEventListener('click', () => {
//    // 1. 活動名稱長度 1~100
//    const nameLen = eventNameInput.value.trim().length;
//    if (nameLen < 1 || nameLen > 100) {
//      msgArea.textContent = '活動名稱長度須介於1~100字元';
//      return;
//    }
//
//    // 2. 開始/結束時間都要有，且 結束 > 開始
//    if (!eventFromInput.value) {
//      msgArea.textContent = '請填寫活動開始時間';
//      return;
//    }
//    if (!eventToInput.value) {
//      msgArea.textContent = '請填寫活動結束時間';
//      return;
//    }
//    if (new Date(eventFromInput.value) >= new Date(eventToInput.value)) {
//      msgArea.textContent = '活動結束時間須大於開始時間';
//      return;
//    }
//
//    // 3. 地點、簡介必填
//    if (!placeInput.value.trim()) {
//      msgArea.textContent = '請填寫活動地點';
//      return;
//    }
//    if (!summaryInput.value.trim()) {
//      msgArea.textContent = '請填寫活動簡介';
//      return;
//    }
//
//    // 4. Summernote 內容
//    const detail = summernoteEditor.summernote('code').trim();
//    if (!detail || detail === '<p><br></p>') {
//      msgArea.textContent = '請填寫活動描述';
//      return;
//    }
//
//    // 全部通過，清空訊息並切換到票種 Tab
//    msgArea.textContent = '';
//    const tabEl = document.querySelector('[data-bs-target="#nav-Eticket"]');
//    const ticketTab = new bootstrap.Tab(tabEl);
//    ticketTab.show();
//
//	const ticketTabTriggerEl = document.querySelector('#nav-Eticket-tab'); 
//	ticketTab.show();
//  });
//})();




(() => {
	// 按鈕、訊息區、各輸入欄位
	const nextBtn = document.querySelector('#btnNext');
	const msg = document.querySelector('#msg');        // 顯示錯誤訊息
	const eventNameInput = document.querySelector('#event_name');
	const eventFromInput = document.querySelector('#event_from_date');
	const eventToDate = document.querySelector('#event_to_date');
	const total_capacity = document.querySelector('#total_capacity');
	const placeInput = document.querySelector('#place');
	const summaryInput = document.querySelector('#summary');
	const summernoteEditor = document.querySelector('#summernote');
	const imageInput = document.querySelector('#event_image');
	const categoryCheckboxes = document.querySelectorAll('input.category-checkbox');
	const saveBtn = document.querySelector('#btnSaveEvent');


	const category_name = document.querySelector('#category_name');
	const sell_from_time = document.querySelector('#sell_from_time');
	const sell_to_time = document.querySelector('#sell_to_time');
	const price = document.querySelector('#price');
	const capacity = document.querySelector('#capacity');

	// summernote
	$(document).ready(function () {
		$('#summernote').summernote();
	});

	nextBtn.addEventListener('click', () => {
		// 活動名稱長度 1~100
		const nameLen = eventNameInput.value.trim().length;
		if (nameLen < 1 || nameLen > 100) {
			msg.textContent = '活動名稱長度須介於1~100字元';
			return;
		}

		// 開始/結束時間都要有，且 結束 > 開始
		if (!eventFromInput.value) {
			msg.textContent = '請填寫活動開始時間';
			return;
		}
		if (!eventToDate.value) {
			msg.textContent = '請填寫活動結束時間';
			return;
		}
		if (new Date(eventFromInput.value) >= new Date(eventToDate.value)) {
			msg.textContent = '活動結束時間須大於開始時間';
			return;
		}

		// 地點、簡介必填
		if (!placeInput.value.trim()) {
			msg.textContent = '請填寫活動地點';
			return;
		}
		if (!summaryInput.value.trim()) {
			msg.textContent = '請填寫活動簡介';
			return;
		}

		// 檢查檔案
		if (!imageInput.files || imageInput.files.length === 0) {
			msg.textContent = '請上傳活動圖片';
			return;
		}

		// 檢查檔案類型
		const file = imageInput.files[0];
		if (!file.type.startsWith('image/')) {
			msg.textContent = '請上傳圖片檔案（jpg、png…）';
			return;
		}
		// 活動圖片預覽
		$('#event_image').on('change', function () {
			const file = this.files[0];
			if (file) {
				const reader = new FileReader();
				reader.onload = function (e) {
					$('#eventPreview').attr('src', e.target.result);
				};
				reader.readAsDataURL(file);
			}
		});

		// Summernote 內容
		const detail = summaryInput.summernote('code').trim();
		if (!detail || detail === '<p><br></p>') {
			msg.textContent = '請填寫活動描述';
			return;
		}

		// 至少勾一個分類
		const checkedCats = Array.from(categoryCheckboxes).filter(cb => cb.checked);
		const maxChecked = 3;
		if (checkedCats.length === 0) {
			msg.textContent = '至少選擇 1 個活動分類';
			return;
		}
		if (checkedCats.length > maxChecked) {
			msg.textContent = '最多選擇 3 個活動分類';
			return;
		}

		// 全部通過，清空訊息並切換到票種 Tab
		msg.textContent = '';
		const tabEl = document.querySelector('[data-bs-target="#nav-Eticket"]');
		const ticketTab = new bootstrap.Tab(tabEl);
		ticketTab.show();

		const ticketTabTriggerEl = document.querySelector('#nav-Eticket-tab');
		ticketTab.show();
	});

	saveBtn.addEventListener('click', async (e) => {
		if (!category_name.value) {
			msg.textContent = '請填寫票種名稱';
			return;
		}
		if (!sell_from_time.value) {
			msg.textContent = '請填寫販售開始時間';
			return;
		}
		if (!sell_to_time.value) {
			msg.textContent = '請填寫販售結束時間';
			return;
		}
		if (!price.value) {
			msg.textContent = '請填寫價格';
			return;
		}
		if (!capacity.value) {
			msg.textContent = '請填寫數量';
			return;
		}
		msg.textContent = '';

		//第一次fetch，取得KeywordId
		const mCategory = {
			categories: Array.from(categoryCheckboxes)
				.filter(cb => cb.checked)
				.map(cb => cb.value),
		}

		try {
			const res1 = await fetch('http://localhost:8080/maven-tickeasy-v1/manager/eventkeyword', {
				method: 'POST',
				headers: { 'Content-Type': 'application/json' },
				body: JSON.stringify(mCategory)
			});
			// 2.1. 解析伺服器回傳的 JSON
			const data1 = await res1.json();
			// 2.2. 若伺服器端告知「失敗」，丟出錯誤進入 catch
			if (!data1.successful) throw new Error(data1.message);

			console.log(data1);

			// 3. 根據第一支請求的結果，組出第二支 fetch 的參數
			//    例如：data1.data[0] 是剛新增分類的主鍵
			const secondPayload = {
				id: data1.data[0],
				extra: 'foo'
			};

			// 4. 再發第二個 fetch，只有等第一個完成後才會執行這裡
			const res2 = await fetch('/api/second-endpoint', {
				method: 'POST',
				headers: { 'Content-Type': 'application/json' },
				body: JSON.stringify(secondPayload)
			});
			// 4.1. 同樣 parse JSON
			const data2 = await res2.json();
			if (!data2.successful) throw new Error(data2.message);

			// 5. 若一路都成功，就更新畫面訊息
			msg.textContent = '兩筆資料皆已成功送出！';
			msg.className = 'info';
		} catch (error) {
			// 6. 任何 fetch 或 JSON 解析出錯，都會進到這裡
			console.error(err);
			msg.textContent = '送出失敗：' + err.message;
			msg.className = 'error';
		}
		function appendSeconds(t) {
			return t.length === 16 ? t + ':00' : t;
		}

		// 準備好要送的 payload
		const payload = {
			eventName: eventNameInput.value.trim(),
			eventFromDate: appendSeconds(eventFromInput.value),
			eventToDate: appendSeconds(eventToDate.value),
			eventHost: "Tibame",
			totalCapacity: parseInt(total_capacity.value, 10),
			place: placeInput.value.trim(),
			summary: summaryInput.value.trim(),
			detail: summernoteEditor.summernote('code'),
			keywordId: parseInt(data1.data[0], 10),
			memberId: 8,

			// 		categoryName: category_name.value,
			//      sellFromTime: sell_from_time.value,
			//      sellToTime: sell_to_time.value,
			//      price: price.value.trim(),
			//		capacity: capacity.value,
		};
		delete payload.categories;
		const upload = () => {
			fetch('/manager/create-event', {
				method: 'POST',
				headers: { 'Content-Type': 'application/json' },
				body: JSON.stringify(payload),
			})
				.then(resp => resp.json())
				.then(body => {
					if (body.successful) {
						// 成功後鎖掉所有輸入
						[eventNameInput, eventFromInput, eventToDate, total_capacity, placeInput, summaryInput, imageInput, saveBtn]
							.forEach(i => i.disabled = true);
						msg.className = 'info';
						msg.textContent = '活動建立成功';
					} else {
						msg.className = 'error';
						msg.textContent = body.message || '活動建立失敗';
					}
				})
				.catch(() => {
					msg.className = 'error';
					msg.textContent = '網路錯誤，請稍後再試';
				});
		};

		// 如果有選圖片，就用 FileReader 先轉 Base64
		if (imageInput.files[0]) {
			const reader = new FileReader();
			reader.addEventListener('load', e => {
				payload.image = e.target.result.split(',')[1];
				upload();
			});
			reader.readAsDataURL(imageInput.files[0]);
		} else {
			// 沒選圖片就直接送
			upload();
		}
	});
})();