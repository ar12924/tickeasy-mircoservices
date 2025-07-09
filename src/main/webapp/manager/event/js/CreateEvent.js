(() => {
	const memberId = sessionStorage.getItem("memberId");
	if (!memberId) {
		alert("請先登入");
		window.location.href = "/maven-tickeasy-v1/user/member/login.html";
		return;
	}
	const nextBtn = document.querySelector('#btnNext');
	const msg = document.querySelector('#msg');
	const eventNameInput = document.querySelector('#event_name');
	const eventFromInput = document.querySelector('#event_from_date');
	const eventToDate = document.querySelector('#event_to_date');
	const total_capacity = document.querySelector('#total_capacity');
	const placeInput = document.querySelector('#place');
	const summaryInput = document.querySelector('#summary');
	const imageInput = document.querySelector('#event_image');
	const categoryCheckboxes = document.querySelectorAll('input.category-checkbox');
	const saveBtn = document.querySelector('#btnSaveEvent');

	const category_name = document.querySelector('#category_name');
	const sell_from_time = document.querySelector('#sell_from_time');
	const sell_to_time = document.querySelector('#sell_to_time');
	const price = document.querySelector('#price');
	const capacity = document.querySelector('#capacity');

	const summernoteEditor = $('#summernote');

	$(document).ready(() => {
		summernoteEditor.summernote();
		// ✅ 新增：設定預覽圖片的初始狀態
		imagePreview.style.display = 'none';
		imagePreview.parentElement.innerHTML = `
            <div class="text-center text-muted p-4">
                <i class="bi bi-image fs-1"></i>
                <div>請選擇圖片</div>
            </div>
        `;
	});
	// ✅ 新增：圖片預覽功能
	imageInput.addEventListener('change', function (e) {
		const file = e.target.files[0];
		const previewContainer = imagePreview.parentElement;

		if (file && file.type.startsWith('image/')) {
			const reader = new FileReader();

			reader.onload = function (e) {
				// 顯示預覽圖片
				previewContainer.innerHTML = `
                    <img id="eventPreview" src="${e.target.result}" 
                         class="img-fluid preview-img" alt="預覽圖片" 
                         style="max-height: 180px; object-fit: cover;">
                `;
			};

			reader.readAsDataURL(file);
		} else {
			// 如果不是圖片檔案，顯示錯誤
			previewContainer.innerHTML = `
                <div class="text-center text-danger p-4">
                    <i class="bi bi-exclamation-triangle fs-1"></i>
                    <div>請選擇有效的圖片檔案</div>
                </div>
            `;
		}
	});


	nextBtn.addEventListener('click', () => {
		if (eventNameInput.value.trim().length < 1 || eventNameInput.value.trim().length > 100) {
			msg.textContent = '活動名稱長度須介於1~100字元';
			return;
		}
		if (!eventFromInput.value || !eventToDate.value) {
			msg.textContent = '請填寫活動起訖時間';
			return;
		}
		if (new Date(eventFromInput.value) >= new Date(eventToDate.value)) {
			msg.textContent = '活動結束時間須大於開始時間';
			return;
		}
		if (!placeInput.value.trim()) {
			msg.textContent = '請填寫活動地點';
			return;
		}
		if (!summaryInput.value) {
			msg.textContent = '請填寫活動簡介';
			return;
		}
		if (!summernoteEditor.summernote('code').trim() || summernoteEditor.summernote('code').trim() === '<p><br></p>') {
			msg.textContent = '請填寫活動描述';
			return;
		}
		if (!imageInput.files || imageInput.files.length === 0 || !imageInput.files[0].type.startsWith('image/')) {
			msg.textContent = '請上傳有效的圖片檔案';
			return;
		}

		const checkedCats = Array.from(categoryCheckboxes).filter(cb => cb.checked);
		if (checkedCats.length === 0 || checkedCats.length > 3) {
			msg.textContent = '請選擇 1~3 個活動分類';
			return;
		}

		msg.textContent = '';
		const tabEl = document.querySelector('[data-bs-target="#nav-Eticket"]');
		const ticketTab = new bootstrap.Tab(tabEl);
		ticketTab.show();
	});

	saveBtn.addEventListener('click', async () => {
		if (!category_name.value || !sell_from_time.value || !sell_to_time.value || !price.value || !capacity.value) {
			msg.textContent = '請完整填寫票種資訊';
			return;
		}

		try {
			msg.textContent = '正在建立活動分類...';
			msg.className = 'text-info';

			const checkedCats = Array.from(categoryCheckboxes).filter(cb => cb.checked).map(cb => cb.value);
			const keywordPayload = {
				keywordName1: checkedCats[0] || null,
				keywordName2: checkedCats[1] || null,
				keywordName3: checkedCats[2] || null,
			};

			console.log('準備送出關鍵字payload：', keywordPayload);

			// 步驟1：建立關鍵字分類
			const res1 = await fetch('http://localhost:8080/maven-tickeasy-v1/manager/eventkeyword', {
				method: 'POST',
				headers: { 'Content-Type': 'application/json' },
				body: JSON.stringify(keywordPayload)
			});

			if (!res1.ok) {
				throw new Error(`關鍵字建立失敗，狀態碼：${res1.status}`);
			}

			const data1 = await res1.json();
			console.log('關鍵字建立完整回應：', data1);
			console.log('successful 值：', data1.successful);
			console.log('data 值：', data1.data);
			console.log('message 值：', data1.message);

			// ✅ 修正：正確檢查成功狀態
			if (!data1.successful) {
				throw new Error(data1.message || '關鍵字建立失敗');
			}

			// ✅ 修正：成功時不要拋出錯誤
			const keywordId = data1.data;
			if (!keywordId || keywordId <= 0) {
				throw new Error('未取得有效的關鍵字ID');
			}

			console.log('✅ 關鍵字建立成功，ID：', keywordId);

			// 步驟2：建立活動
			msg.textContent = '正在建立活動...';

			const appendSeconds = (t) => t.length === 16 ? t + ':00' : t;

			const eventPayload = {
				eventName: eventNameInput.value.trim(),
				eventFromDate: appendSeconds(eventFromInput.value),
				eventToDate: appendSeconds(eventToDate.value),
				eventHost: "Tibame",
				totalCapacity: parseInt(total_capacity.value, 10) || 0,
				place: placeInput.value.trim(),
				summary: summaryInput.value.trim(),
				detail: summernoteEditor.summernote('code'),
				keywordId: keywordId,
				memberId: memberId
			};

			// 建立活動的函數
			const createEvent = async (payload) => {
				console.log("準備送出 create-event payload：", payload);

				const response = await fetch('http://localhost:8080/maven-tickeasy-v1/manager/create-event', {
					method: 'POST',
					headers: { 'Content-Type': 'application/json' },
					body: JSON.stringify(payload),
				});

				if (!response.ok) {
					throw new Error(`活動建立失敗，狀態碼：${response.status}`);
				}

				const result = await response.json();
				console.log('活動建立回應：', result);

				if (result.successful) {
					// 成功
					[eventNameInput, eventFromInput, eventToDate, total_capacity, placeInput, imageInput, saveBtn].forEach(i => {
						i.disabled = true;
					});
					msg.className = 'text-success';
					msg.textContent = '✅ 活動建立成功！活動ID：' + result.data;
				} else {
					throw new Error(result.message || '活動建立失敗');
				}
			};

			// 處理圖片並建立活動
			if (imageInput.files[0]) {
				const reader = new FileReader();
				reader.onload = async (e) => {
					try {
						eventPayload.image = e.target.result.split(',')[1];
						await createEvent(eventPayload);
					} catch (error) {
						console.error('建立活動時發生錯誤：', error);
						msg.className = 'text-danger';
						msg.textContent = '❌ 活動建立失敗：' + error.message;
					}
				};
				reader.readAsDataURL(imageInput.files[0]);
			} else {
				await createEvent(eventPayload);
			}

		} catch (error) {
			console.error('整個流程發生錯誤：', error);
			msg.className = 'text-danger';
			msg.textContent = '❌ 送出失敗：' + error.message;
		}
	});
})();