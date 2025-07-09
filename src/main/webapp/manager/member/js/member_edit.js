// 全域變數
let currentMemberId = null;

// 頁面載入完成後執行
document.addEventListener('DOMContentLoaded', function () {
    // 檢查用戶權限
    const roleLevel = sessionStorage.getItem("roleLevel");
    const memberId = sessionStorage.getItem("memberId");

    if (!roleLevel || (roleLevel !== "2" && roleLevel !== "3")) {
        alert("您沒有權限訪問此頁面");
        window.location.href = "/maven-tickeasy-v1/user/member/login.html";
        return;
    }

    if (!memberId) {
        alert("請先登入");
        window.location.href = "/maven-tickeasy-v1/user/member/login.html";
        return;
    }
    
    // 初始化編輯頁面
    initEditPage();
});

// 初始化編輯頁面
function initEditPage() {
    // 從URL獲取會員ID
    const urlParams = new URLSearchParams(window.location.search);
    currentMemberId = urlParams.get('id');

    if (!currentMemberId) {
        showError('缺少會員ID參數');
        setTimeout(() => {
            window.location.href = 'member_list.html';
        }, 2000);
        return;
    }

    // 載入會員資料
    loadMemberData(currentMemberId);
}

// 載入會員資料
function loadMemberData(memberId) {
    showLoading();

    fetch(`/maven-tickeasy-v1/api/manager/member/${memberId}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            hideLoading();
            if (data.success) {
                populateForm(data.data);
                loadMemberPhoto(memberId);
                bindEditEvents();
                showForm();
            } else {
                throw new Error(data.message || '載入會員資料失敗');
            }
        })
        .catch(error => {
            hideLoading();
            console.error('載入會員資料失敗:', error);
            showError('載入會員資料失敗：' + error.message);
        });
}

// 填充表單資料
function populateForm(member) {
    document.getElementById('userName').value = member.userName || '';
    document.getElementById('nickName').value = member.nickName || '';
    document.getElementById('email').value = member.email || '';
    document.getElementById('phone').value = member.phone || '';

    // 處理日期格式
    if (member.birthDate) {
        const date = new Date(member.birthDate);
        const formattedDate = date.toISOString().split('T')[0];
        document.getElementById('birthDate').value = formattedDate;
    }

    document.getElementById('gender').value = member.gender || '';
    document.getElementById('idCard').value = member.idCard || '';
    document.getElementById('unicode').value = member.unicode || '';
    document.getElementById('roleLevel').value = member.roleLevel || '';
    document.getElementById('isActive').value = member.isActive || '';

    // 更新系統資訊顯示
    updateSystemInfo(member);
}

// 更新系統資訊顯示
function updateSystemInfo(member) {
    const systemInfoDd = document.querySelectorAll('.card-body dl dd');
    if (systemInfoDd.length >= 3) {
        systemInfoDd[0].textContent = member.memberId || '';
        systemInfoDd[1].textContent = formatDateTime(member.createTime);
        systemInfoDd[2].textContent = formatDateTime(member.updateTime);
    }
}

// 載入會員照片
function loadMemberPhoto(memberId) {
    const photoUrl = `/maven-tickeasy-v1/api/manager/member/photo/${memberId}`;
    const photoImg = document.querySelector('.card-body img');
    const defaultPhoto = '../common/assets/img/user2-128x128.png';
    // 先設定預設圖片
    photoImg.src = defaultPhoto;
    photoImg.alt = '預設照片';

    // 嘗試載入實際照片
    const testImg = new Image();
    
    testImg.onload = function() {
        // 載入成功才更換為實際照片
        photoImg.src = photoUrl + '?t=' + new Date().getTime();
        photoImg.alt = '會員照片';
    };
    
    testImg.onerror = function() {
        // 載入失敗保持預設圖片
        console.log('會員照片不存在，使用預設圖片');
    };
    
    // 開始載入，加上時間戳避免快取問題
    testImg.src = photoUrl + '?timestamp=' + new Date().getTime();
}

// 綁定事件
function bindEditEvents() {
    // 表單提交事件
    document.getElementById('memberEditForm').addEventListener('submit', function (e) {
        e.preventDefault();
        updateMember();
    });

    // 照片預覽事件
    document.getElementById('photo').addEventListener('change', function (e) {
        const file = e.target.files[0];
        if (file) {
            // 檔案大小檢查 (2MB)
            if (file.size > 2 * 1024 * 1024) {
                alert('檔案大小不能超過 2MB');
                this.value = '';
                return;
            }

            // 檔案類型檢查
            if (!file.type.startsWith('image/')) {
                alert('請選擇圖片檔案');
                this.value = '';
                return;
            }

            const reader = new FileReader();
            reader.onload = function (e) {
                document.querySelector('.card-body img').src = e.target.result;
            };
            reader.readAsDataURL(file);
        }
    });

    // 密碼重設事件
    document.getElementById('resetPassword').addEventListener('change', function () {
        const passwordGroups = ['newPasswordGroup', 'confirmPasswordGroup'];
        const newPasswordInput = document.getElementById('newPassword');
        const confirmPasswordInput = document.getElementById('confirmPassword');
        passwordGroups.forEach(id => {
            const element = document.getElementById(id);
            if (element) {
                element.style.display = this.checked ? 'block' : 'none';
            }
        });

        if (!this.checked) {
            // 清空密碼欄位並移除驗證狀態
            if (newPasswordInput) {
                newPasswordInput.value = '';
                newPasswordInput.classList.remove('is-valid', 'is-invalid');
                newPasswordInput.setCustomValidity('');
            }
            if (confirmPasswordInput) {
                confirmPasswordInput.value = '';
                confirmPasswordInput.classList.remove('is-valid', 'is-invalid');
                confirmPasswordInput.setCustomValidity('');
            }
        }
    });

    const idCardInput = document.getElementById('idCard');
    idCardInput.addEventListener('input', function () {
        validateIdCard(this);
    });
    idCardInput.addEventListener('blur', function () {
        validateIdCard(this);
    });

    // 統一編號即時驗證
    const unicodeInput = document.getElementById('unicode');
    unicodeInput.addEventListener('input', function () {
        validateUnicode(this);
    });
    unicodeInput.addEventListener('blur', function () {
        validateUnicode(this);
    });

    // 電子郵件即時驗證
    const emailInput = document.getElementById('email');
    emailInput.addEventListener('input', function () {
        validateEmail(this);
    });

    // 手機號碼即時驗證
    const phoneInput = document.getElementById('phone');
    phoneInput.addEventListener('input', function () {
        validatePhone(this);
    });

    // 密碼即時驗證
    const newPasswordInput = document.getElementById('newPassword');
    const confirmPasswordInput = document.getElementById('confirmPassword');

    newPasswordInput.addEventListener('input', function () {
        validateNewPassword(this);
        validatePasswordMatch();
    });

    confirmPasswordInput.addEventListener('input', function () {
        validatePasswordMatch();
    });

    // 必填欄位即時驗證
    const form = document.getElementById('memberEditForm');
    const requiredFields = form.querySelectorAll('input[required], select[required]');
    requiredFields.forEach(field => {
        field.addEventListener('blur', function () {
            if (this.value.trim() === '' && this.hasAttribute('required')) {
                this.classList.add('is-invalid');
                this.classList.remove('is-valid');
            } else if (this.checkValidity() && this.value.trim() !== '') {
                this.classList.add('is-valid');
                this.classList.remove('is-invalid');
            }
        });

        field.addEventListener('input', function () {
            if (this.classList.contains('is-invalid') && this.value.trim() !== '') {
                if (this.checkValidity()) {
                    this.classList.remove('is-invalid');
                    this.classList.add('is-valid');
                }
            }
        });
    });
}

// 更新會員資料
function updateMember() {
    // 表單驗證
    const form = document.getElementById('memberEditForm');
    if (!form.checkValidity()) {
        form.classList.add('was-validated');
        return;
    }

    // 密碼驗證
    const resetPasswordCheckbox = document.getElementById('resetPassword');
    let rePassword = '';
    if (resetPasswordCheckbox.checked) {
        const newPassword = document.getElementById('newPassword').value;
        const confirmPassword = document.getElementById('confirmPassword').value;

        if (newPassword.length < 6) {
            alert('密碼長度至少需要6個字元！');
            return;
        }

        if (newPassword !== confirmPassword) {
            alert('兩次輸入的密碼不一致！');
            return;
        }

        rePassword = confirmPassword;
    }

    // 顯示載入狀態
    const submitBtn = document.querySelector('button[type="submit"]');
    const originalText = submitBtn.innerHTML;
    submitBtn.innerHTML = '<i class="bi bi-hourglass-split"></i> 更新中...';
    submitBtn.disabled = true;

    const formData = new FormData();

    // 基本欄位
    formData.append('userName', form.userName.value);
    formData.append('nickName', form.nickName.value);
    formData.append('email', form.email.value);
    formData.append('phone', form.phone.value);
    formData.append('birthDate', form.birthDate.value);
    formData.append('gender', form.gender.value);
    formData.append('idCard', form.idCard.value || '');
    formData.append('unicode', form.unicode.value || '');
    formData.append('roleLevel', form.roleLevel.value);
    formData.append('isActive', form.isActive.value);

    // 密碼處理
    if (resetPasswordCheckbox.checked) {
        formData.append('password', document.getElementById('newPassword').value);
        formData.append('rePassword', rePassword);
    } else {
        formData.append('password', ''); // 空密碼表示不修改
        formData.append('rePassword', '');
    }

    // 提交更新
    fetch(`/maven-tickeasy-v1/api/manager/member/${currentMemberId}`, {
        method: 'PUT',
        body: formData
    })
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            if (data.success) {
                // 如果有照片，進行上傳
                if (form.photo.files[0]) {
                    uploadPhoto(currentMemberId, form.photo.files[0])
                        .then(() => {
                            alert('會員資料已更新成功！');
                            window.location.href = 'member_list.html';
                        })
                        .catch(() => {
                            alert('會員資料已更新成功，但照片上傳失敗！');
                            window.location.href = 'member_list.html';
                        });
                } else {
                    alert('會員資料已更新成功！');
                    window.location.href = 'member_list.html';
                }
            } else {
                throw new Error(data.message || '更新失敗');
            }
        })
        .catch(error => {
            console.error('更新會員失敗:', error);
            alert('更新會員失敗：' + error.message);
        })
        .finally(() => {
            submitBtn.innerHTML = originalText;
            submitBtn.disabled = false;
        });
}

// 上傳照片函數
function uploadPhoto(memberId, photoFile) {
    const photoFormData = new FormData();
    photoFormData.append('photo', photoFile);

    return fetch(`/maven-tickeasy-v1/api/manager/member/photo/${memberId}`, {
        method: 'POST',
        body: photoFormData
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('照片上傳失敗');
            }
            return response.json();
        });
}

// 顯示載入中
function showLoading() {
    document.getElementById('loadingIndicator').style.display = 'block';
    document.getElementById('editForm').style.display = 'none';
    document.getElementById('errorAlert').classList.add('d-none');
}

// 隱藏載入中
function hideLoading() {
    document.getElementById('loadingIndicator').style.display = 'none';
}

// 顯示表單
function showForm() {
    document.getElementById('editForm').style.display = 'block';
}

// 顯示錯誤訊息
function showError(message) {
    document.getElementById('errorMessage').textContent = message;
    document.getElementById('errorAlert').classList.remove('d-none');
    document.getElementById('loadingIndicator').style.display = 'none';
    document.getElementById('editForm').style.display = 'none';
}

// 格式化日期時間
function formatDateTime(dateTimeString) {
    if (!dateTimeString) return '';
    const date = new Date(dateTimeString);
    return date.toLocaleString('zh-TW', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
    });
}

// 驗證身份證號
function validateIdCard(input) {
    const value = input.value.trim();
    const errorElement = input.nextElementSibling;

    input.classList.remove('is-valid', 'is-invalid');
    input.setCustomValidity('');

    if (value === '') {
        return;
    }

    // 配合後端驗證：只檢查開頭是否為英文字母
    const idCardPattern = /^[A-Za-z]/;

    if (!idCardPattern.test(value)) {
        input.classList.add('is-invalid');
        input.setCustomValidity('身份證號開頭應為英文字母');

        const errorMsg = '身份證號開頭應為英文字母';

        if (errorElement && errorElement.classList.contains('invalid-feedback')) {
            errorElement.textContent = errorMsg;
        }
    } else {
        input.classList.add('is-valid');
        input.setCustomValidity('');
    }
}

// 驗證統一編號
function validateUnicode(input) {
    const value = input.value.trim();
    const errorElement = input.nextElementSibling;

    input.classList.remove('is-valid', 'is-invalid');
    input.setCustomValidity('');

    if (value === '') {
        return;
    }

    const unicodePattern = /^[0-9]{8}$/;

    if (!unicodePattern.test(value)) {
        input.classList.add('is-invalid');
        input.setCustomValidity('統一編號格式錯誤（應為8位數字）');

        let errorMsg = '統一編號格式錯誤';
        if (value.length !== 8) {
            errorMsg = `統一編號應為8位數字（目前${value.length}位）`;
        } else if (!/^[0-9]+$/.test(value)) {
            errorMsg = '統一編號只能包含數字';
        }

        if (errorElement && errorElement.classList.contains('invalid-feedback')) {
            errorElement.textContent = errorMsg;
        }
    } else {
        input.classList.add('is-valid');
        input.setCustomValidity('');
    }
}

// 驗證電子郵件
function validateEmail(input) {
    const value = input.value.trim();
    const emailPattern = /^[^\s@]+@[^\s@]+\.[a-zA-Z]{2,6}$/;

    input.classList.remove('is-valid', 'is-invalid');
    input.setCustomValidity('');

    if (value === '') {
        return;
    }

    if (!emailPattern.test(value)) {
        input.classList.add('is-invalid');
        input.setCustomValidity('請輸入有效的電子郵件地址');
    } else {
        input.classList.add('is-valid');
        input.setCustomValidity('');
    }
}

// 驗證手機號碼
function validatePhone(input) {
    const value = input.value.trim();
    const phonePattern = /^09\d{8}$/;

    input.classList.remove('is-valid', 'is-invalid');
    input.setCustomValidity('');

    if (value === '') {
        return;
    }

    if (!phonePattern.test(value)) {
        input.classList.add('is-invalid');
        let errorMsg = '請輸入有效的台灣手機號碼（09開頭共10碼）';
        if (value.length !== 10) {
            errorMsg = `手機號碼應為10位數字（目前${value.length}位）`;
        } else if (!value.startsWith('09')) {
            errorMsg = '手機號碼應以09開頭';
        }
        input.setCustomValidity(errorMsg);

        const errorElement = input.nextElementSibling;
        if (errorElement && errorElement.classList.contains('invalid-feedback')) {
            errorElement.textContent = errorMsg;
        }
    } else {
        input.classList.add('is-valid');
        input.setCustomValidity('');
    }
}

// 驗證新密碼
function validateNewPassword(input) {
    const resetPasswordCheckbox = document.getElementById('resetPassword');

    // 如果沒有勾選重設密碼，就不驗證
    if (!resetPasswordCheckbox.checked) {
        input.classList.remove('is-valid', 'is-invalid');
        input.setCustomValidity('');
        return;
    }

    const value = input.value;
    input.classList.remove('is-valid', 'is-invalid');
    input.setCustomValidity('');

    if (value === '') {
        input.classList.add('is-invalid');
        input.setCustomValidity('請輸入新密碼');
        return;
    }

    if (value.length < 6) {
        input.classList.add('is-invalid');
        input.setCustomValidity('密碼長度至少需要6個字元');
    } else {
        input.classList.add('is-valid');
        input.setCustomValidity('');
    }
}

// 驗證密碼確認
function validatePasswordMatch() {
    const resetPasswordCheckbox = document.getElementById('resetPassword');
    const newPassword = document.getElementById('newPassword').value;
    const confirmPassword = document.getElementById('confirmPassword').value;
    const confirmPasswordInput = document.getElementById('confirmPassword');

    // 如果沒有勾選重設密碼，就不驗證
    if (!resetPasswordCheckbox.checked) {
        confirmPasswordInput.classList.remove('is-valid', 'is-invalid');
        confirmPasswordInput.setCustomValidity('');
        return;
    }

    confirmPasswordInput.classList.remove('is-valid', 'is-invalid');
    confirmPasswordInput.setCustomValidity('');

    if (confirmPassword === '') {
        confirmPasswordInput.classList.add('is-invalid');
        confirmPasswordInput.setCustomValidity('請確認密碼');
        return;
    }

    if (newPassword !== confirmPassword) {
        confirmPasswordInput.classList.add('is-invalid');
        confirmPasswordInput.setCustomValidity('密碼確認不相符');
        const errorElement = confirmPasswordInput.nextElementSibling;
        if (errorElement && errorElement.classList.contains('invalid-feedback')) {
            errorElement.textContent = '密碼確認不相符';
        }
    } else if (newPassword.length >= 6) {
        confirmPasswordInput.classList.add('is-valid');
        confirmPasswordInput.setCustomValidity('');
    }
}