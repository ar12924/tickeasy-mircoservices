(() => {
	const memberId = sessionStorage.getItem("memberId");
	const host = sessionStorage.getItem("savedUsername");
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
	const imagePreview = document.querySelector('#eventPreview');
	const category_name = document.querySelector('#category_name');
	const sell_from_time = document.querySelector('#sell_from_time');
	const sell_to_time = document.querySelector('#sell_to_time');
	const price = document.querySelector('#price');
	const capacity = document.querySelector('#capacity');

	// const summernoteEditor = $('#summernote');
	const summernoteEditor = document.querySelector('#summernote');

	$(document).ready(() => {
		// summernoteEditor.summernote();
		// ✅ 新增：設定預覽圖片的初始狀態
		const previewContainer = imagePreview.parentElement;
		previewContainer.innerHTML = `
            <div class="text-center text-muted p-4">
                <i class="bi bi-image fs-1"></i>
                <div>請選擇圖片</div>
            </div>
        `;
	});
	// ✅ 新增：圖片預覽功能
	imageInput.addEventListener('change', function (e) {
		const file = e.target.files[0];
		const previewContainer = imageInput.parentElement.parentElement.querySelector('.border.rounded.bg-light');

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
		// if (!summernoteEditor.summernote('code').trim() || summernoteEditor.summernote('code').trim() === '<p><br></p>') {
		if (!summernoteEditor.value.trim) {
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

			// ✅ 修正：確保 payload 格式正確，並處理空值
			const keywordPayload = {
				keywordName1: checkedCats[0] || "",  // 使用空字串而非 null
				keywordName2: checkedCats[1] || "",
				keywordName3: checkedCats[2] || "",
			};

			console.log('準備送出關鍵字payload：', keywordPayload);

			// 步驟1：建立關鍵字分類
			const res1 = await fetch('/maven-tickeasy-v1/manager/eventkeyword', {
				method: 'POST',
				headers: {
					'Content-Type': 'application/json',
					'Accept': 'application/json'  // ✅ 加入 Accept header
				},
				body: JSON.stringify(keywordPayload)
			});

			console.log('關鍵字API回應狀態:', res1.status);
			console.log('關鍵字API回應headers:', res1.headers);

			if (!res1.ok) {
				const errorText = await res1.text();
				console.error('關鍵字API錯誤回應:', errorText);
				throw new Error(`關鍵字建立失敗，狀態碼：${res1.status}`);
			}

			// ✅ 檢查回應內容類型
			const contentType = res1.headers.get('content-type');
			if (!contentType || !contentType.includes('application/json')) {
				const responseText = await res1.text();
				console.error('非JSON回應:', responseText);
				throw new Error('伺服器回應格式錯誤');
			}

			const data1 = await res1.json();
			console.log('關鍵字建立完整回應：', data1);
			console.log('successful 值：', data1.successful);
			console.log('data 值：', data1.data);
			console.log('message 值：', data1.message);

			// ✅ 檢查成功狀態
			if (!data1.successful) {
				throw new Error(data1.message || '關鍵字建立失敗');
			}

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
				eventHost: host,
				totalCapacity: parseInt(total_capacity.value, 10) || 0,
				place: placeInput.value.trim(),
				summary: summaryInput.value.trim(),
				// detail: summernoteEditor.summernote('code'),
				detail: summernoteEditor.value.trim(),
				keywordId: keywordId,
				memberId: parseInt(memberId, 10)  // ✅ 確保是數字
			};

			// 建立活動的函數
			const createEvent = async (payload) => {
				console.log("準備送出 create-event payload：", payload);

				const response = await fetch('/maven-tickeasy-v1/manager/create-event', {
					method: 'POST',
					headers: {
						'Content-Type': 'application/json',
						'Accept': 'application/json'
					},
					body: JSON.stringify(payload),
				});

				console.log('活動建立API回應狀態:', response.status);

				if (!response.ok) {
					const errorText = await response.text();
					console.error('活動建立API錯誤回應:', errorText);
					throw new Error(`活動建立失敗，狀態碼：${response.status}`);
				}

				const result = await response.json();
				console.log('活動建立回應：', result);

				if (result.successful) {
					// 成功後建立票種
					msg.textContent = '正在建立票種...';
					const ticketTypeId = await createTicketType(result.data);

					// 禁用表單
					[eventNameInput, eventFromInput, eventToDate, total_capacity, placeInput, imageInput, saveBtn].forEach(i => {
						i.disabled = true;
					});

					msg.className = 'text-success';
					msg.innerHTML = `
                    ✅ 活動及票種建立成功！<br>
                    📋 活動ID：${result.data}<br>
                    🎫 票種ID：${ticketTypeId}<br>
                    💰 票種：${category_name.value.trim()}<br>
                    💵 價格：NT$ ${price.value}<br>
                    👥 數量：${capacity.value} 張
                `;
					// ✅ 新增：3秒倒數計時跳轉功能
					let countdown = 3;
					const countdownElement = document.getElementById('countdown');

					const timer = setInterval(() => {
						countdown--;
						if (countdownElement) {
							countdownElement.textContent = countdown;
						}

						if (countdown <= 0) {
							clearInterval(timer);
							console.log('🔄 自動跳轉到活動列表...');
							window.location.href = '../index.html';
						}
					}, 1000);

				} else {
					throw new Error(result.message || '活動建立失敗');
				}
			};

			// ✅ 新增：建立票種的函數
			const createTicketType = async (eventId) => {
				const ticketPayload = {
					eventId: eventId,
					categoryName: category_name.value.trim(),
					sellFromTime: appendSeconds(sell_from_time.value),
					sellToTime: appendSeconds(sell_to_time.value),
					price: parseFloat(price.value), // ✅ 改為 parseFloat 以支援小數
					capacity: parseInt(capacity.value, 10)
				};

				console.log("準備送出票種payload：", ticketPayload);

				const ticketResponse = await fetch('/maven-tickeasy-v1/manager/create-ticket-type', {
					method: 'POST',
					headers: {
						'Content-Type': 'application/json',
						'Accept': 'application/json'
					},
					body: JSON.stringify(ticketPayload),
				});

				console.log('票種建立API回應狀態:', ticketResponse.status);

				if (!ticketResponse.ok) {
					const errorText = await ticketResponse.text();
					console.error('票種建立API錯誤回應:', errorText);
					throw new Error(`票種建立失敗，狀態碼：${ticketResponse.status}`);
				}

				const ticketResult = await ticketResponse.json();
				console.log('票種建立回應：', ticketResult);

				if (!ticketResult.successful) {
					throw new Error(ticketResult.message || '票種建立失敗');
				}

				return ticketResult.data; // 回傳票種ID
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