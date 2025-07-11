(() => {
    let eventId = null;
    let originalEventData = null;

    // DOM 元素
    const loadingMessage = document.querySelector('#loadingMessage');
    const eventForm = document.querySelector('#eventForm');
    const msg = document.querySelector('#msg');
    const eventNameInput = document.querySelector('#event_name');
    const eventFromInput = document.querySelector('#event_from_date');
    const eventToInput = document.querySelector('#event_to_date');
    const totalCapacityInput = document.querySelector('#total_capacity');
    const placeInput = document.querySelector('#place');
    const summaryInput = document.querySelector('#summary');
    const imageInput = document.querySelector('#event_image');
    const updateBtn = document.querySelector('#btnUpdate');
    const imagePreviewContainer = document.querySelector('#imagePreviewContainer');
    const summernoteContent = document.querySelector('#summernote');
    // let summernoteEditor;

    // 從 URL 獲取活動ID
    function getEventIdFromUrl() {
        const urlParams = new URLSearchParams(window.location.search);
        return urlParams.get('eventId');
    }

    // 格式化日期時間為 datetime-local 格式
    function formatDateTimeLocal(dateString) {
        if (!dateString) return '';
        const date = new Date(dateString);
        return date.toISOString().slice(0, 16);
    }

    // 載入活動資料
    async function loadEventData() {
        try {
            const response = await fetch(`/maven-tickeasy-v1/manager/show-event/${eventId}`);

            if (!response.ok) {
                throw new Error(`載入失敗，狀態碼：${response.status}`);
            }

            const result = await response.json();
            console.log('載入活動資料:', result);

            if (result.successful && result.data) {
                originalEventData = result.data;
                populateForm(result.data);

                // 隱藏載入訊息，顯示表單
                loadingMessage.style.display = 'none';
                eventForm.style.display = 'block';
            } else {
                throw new Error(result.message || '載入活動資料失敗');
            }
        } catch (error) {
            console.error('載入活動資料錯誤:', error);
            loadingMessage.innerHTML = `
                <i class="bi bi-exclamation-triangle text-danger"></i>
                <p class="text-danger">載入失敗：${error.message}</p>
                <a href="../index.html" class="btn btn-secondary">返回活動列表</a>
            `;
        }
    }

    // 填充表單數據
    function populateForm(eventData) {
        eventNameInput.value = eventData.eventName || '';
        eventFromInput.value = formatDateTimeLocal(eventData.eventFromDate);
        eventToInput.value = formatDateTimeLocal(eventData.eventToDate);
        totalCapacityInput.value = eventData.totalCapacity || '';
        placeInput.value = eventData.place || '';
        summaryInput.value = eventData.summary || '';

        // 設置 Summernote 內容
        // if (summernoteEditor) {
        //     summernoteEditor.summernote('code', eventData.detail || '');
        // }

        // 顯示現有圖片
        if (eventData.image) {
            const imageUrl = `data:image/jpeg;base64,${eventData.image}`;
            imagePreviewContainer.innerHTML = `
                <img src="${imageUrl}" class="preview-img" alt="活動圖片">
            `;
        }
    }

    // 圖片預覽功能
    function setupImagePreview() {
        imageInput.addEventListener('change', function (e) {
            const file = e.target.files[0];

            if (file && file.type.startsWith('image/')) {
                const reader = new FileReader();

                reader.onload = function (e) {
                    imagePreviewContainer.innerHTML = `
                        <img src="${e.target.result}" class="preview-img" alt="預覽圖片">
                    `;
                };

                reader.readAsDataURL(file);
            } else if (file) {
                imagePreviewContainer.innerHTML = `
                    <div class="text-center text-danger p-4">
                        <i class="bi bi-exclamation-triangle fs-1"></i>
                        <div>請選擇有效的圖片檔案</div>
                    </div>
                `;
            }
        });
    }

    // 驗證表單
    function validateForm() {
        if (eventNameInput.value.trim().length < 1 || eventNameInput.value.trim().length > 100) {
            return '活動名稱長度須介於1~100字元';
        }

        if (!eventFromInput.value || !eventToInput.value) {
            return '請填寫活動起訖時間';
        }

        if (new Date(eventFromInput.value) >= new Date(eventToInput.value)) {
            return '活動結束時間須大於開始時間';
        }

        if (!placeInput.value.trim()) {
            return '請填寫活動地點';
        }

        if (!summaryInput.value.trim()) {
            return '請填寫活動簡介';
        }

        // const summernoteContent = summernoteEditor.value.trim();
        if (!summernoteContent) {
            return '請填寫活動描述';
        }

        return null;
    }

    // 更新活動
    async function updateEvent() {
        const validationError = validateForm();
        if (validationError) {
            msg.className = 'text-danger mb-2';
            msg.textContent = validationError;
            return;
        }

        try {
            msg.className = 'text-info mb-2';
            msg.textContent = '正在更新活動...';
            updateBtn.disabled = true;

            const appendSeconds = (t) => t.length === 16 ? t + ':00' : t;

            const eventPayload = {
                eventId: parseInt(eventId),
                eventName: eventNameInput.value.trim(),
                eventFromDate: appendSeconds(eventFromInput.value),
                eventToDate: appendSeconds(eventToInput.value),
                eventHost: originalEventData.eventHost,
                totalCapacity: parseInt(totalCapacityInput.value) || 0,
                place: placeInput.value.trim(),
                summary: summaryInput.value.trim(),
                detail: summernoteContent.value.trim(),
                keywordId: originalEventData.keywordId,
                memberId: originalEventData.memberId,
                isPosted: originalEventData.isPosted
            };

            // 處理圖片
            if (imageInput.files[0]) {
                const reader = new FileReader();
                reader.onload = async function (e) {
                    eventPayload.image = e.target.result.split(',')[1];
                    await submitUpdate(eventPayload);
                };
                reader.readAsDataURL(imageInput.files[0]);
            } else {
                // 保留原有圖片
                eventPayload.image = originalEventData.image;
                await submitUpdate(eventPayload);
            }

        } catch (error) {
            console.error('更新活動錯誤:', error);
            msg.className = 'text-danger mb-2';
            msg.textContent = '更新失敗：' + error.message;
            updateBtn.disabled = false;
        }
    }

    // 提交更新
    async function submitUpdate(payload) {
        try {
            console.log('準備送出更新payload:', payload);
            // ✅ 確保 eventId 是數字
            payload.eventId = parseInt(payload.eventId);

            // ✅ 確保必填欄位不為空
            if (!payload.eventName || !payload.place || !payload.summary) {
                throw new Error('必填欄位不能為空');
            }

            // ✅ 確保日期格式正確
            if (!payload.eventFromDate || !payload.eventToDate) {
                throw new Error('活動日期不能為空');
            }

            const response = await fetch('/maven-tickeasy-v1/manager/update-event', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            if (!response.ok) {
                const errorText = await response.text();
                console.error('伺服器回應錯誤:', errorText);
                throw new Error(`更新失敗，狀態碼：${response.status}`);
            }

            const result = await response.json();
            console.log('更新回應:', result);

            if (result.successful) {
                msg.className = 'text-success mb-2';
                msg.textContent = '✅ 活動更新成功！';

                // 3秒後重新導向
                setTimeout(() => {
                    window.location.href = '../index.html';
                }, 3000);
            } else {
                throw new Error(result.message || '活動更新失敗');
            }

        } catch (error) {
            console.error('提交更新錯誤:', error);
            msg.className = 'text-danger mb-2';
            msg.textContent = '❌ 更新失敗：' + error.message;
        } finally {
            updateBtn.disabled = false;
        }
    }

    // 初始化
    function init() {
        eventId = getEventIdFromUrl();

        if (!eventId) {
            loadingMessage.innerHTML = `
                <i class="bi bi-exclamation-triangle text-danger"></i>
                <p class="text-danger">無效的活動ID</p>
                <a href="../index.html" class="btn btn-secondary">返回活動列表</a>
            `;
            return;
        }

        // 初始化 Summernote
        // summernoteEditor = $('#summernote');
        // summernoteEditor.summernote({
        //     height: 200,
        //     lang: 'zh-TW'
        // });

        // 設置圖片預覽
        setupImagePreview();

        // 綁定更新按鈕
        updateBtn.addEventListener('click', updateEvent);

        // 載入活動資料
        loadEventData();
    }

    // 頁面載入後初始化
    $(document).ready(init);
})();