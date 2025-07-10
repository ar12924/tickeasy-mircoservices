/**
 * 表單初始化
 */
function initializeForm() {
    const form = document.getElementById('memberForm');
    if (form) {
        form.classList.remove('was-validated');

        const inputs = form.querySelectorAll('input, select');
        inputs.forEach(input => {
            input.classList.remove('is-invalid', 'is-valid');
            input.setCustomValidity('');
        });
    }
}

/**
 * 密碼顯示/隱藏切換功能
 */
function initializePasswordToggle() {
    const togglePassword = document.getElementById('togglePassword');
    const passwordInput = document.getElementById('password');

    if (togglePassword && passwordInput) {
        togglePassword.addEventListener('click', function () {
            const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
            passwordInput.setAttribute('type', type);

            const icon = this.querySelector('i');
            icon.classList.toggle('bi-eye');
            icon.classList.toggle('bi-eye-slash');
        });
    }
}

/**
 * 照片預覽功能
 */
function initializePhotoPreview() {
    const photoInput = document.getElementById('photo');
    const photoPreview = document.getElementById('photoPreview');
    const removePhotoBtn = document.getElementById('removePhoto');
    const defaultPhoto = '../common/assets/img/user2-128x128.png';

    if (photoInput && photoPreview && removePhotoBtn) {
        photoInput.addEventListener('change', function (e) {
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
                    photoPreview.src = e.target.result;
                };
                reader.readAsDataURL(file);
            }
        });

        removePhotoBtn.addEventListener('click', function () {
            photoInput.value = '';
            photoPreview.src = defaultPhoto;
        });
    }
}

/**
 * 表單驗證功能初始化
 */
function initializeFormValidation() {
    const form = document.getElementById('memberForm');
    if (!form) return;

    // 身份證號驗證
    initializeIdCardValidation();
    
    // 統一編號驗證
    initializeUnicodeValidation();
    
    // 密碼驗證
    initializePasswordValidation();
    
    // 電子郵件驗證
    initializeEmailValidation();
    
    // 手機號碼驗證
    initializePhoneValidation();
    
    // 必填欄位驗證
    initializeRequiredFieldsValidation();
    
    // 表單提交處理
    initializeFormSubmit();
    
    // 表單重置處理
    initializeFormReset();
}

/**
 * 身份證號驗證
 */
function initializeIdCardValidation() {
    const idCardInput = document.getElementById('id_card');
    if (!idCardInput) return;

    idCardInput.addEventListener('input', function () {
        validateIdCard(this);
    });
    
    idCardInput.addEventListener('blur', function () {
        validateIdCard(this);
    });
}

/**
 * 驗證身份證號
 */
function validateIdCard(input) {
    const value = input.value.trim();
    const errorElement = input.nextElementSibling;

    input.classList.remove('is-valid', 'is-invalid');
    input.setCustomValidity('');

    if (value === '') {
        return;
    }

    const idCardPattern = /^[A-Za-z][0-9]{9}$/;

    if (!idCardPattern.test(value)) {
        input.classList.add('is-invalid');
        input.setCustomValidity('身份證號格式錯誤（應為1個英文字母+9個數字）');

        let errorMsg = '身份證號格式錯誤';
        if (value.length !== 10) {
            errorMsg = `身份證號應為10位（目前${value.length}位）`;
        } else if (!/^[A-Za-z]/.test(value)) {
            errorMsg = '身份證號開頭應為英文字母';
        } else if (!/[0-9]{9}$/.test(value.substring(1))) {
            errorMsg = '身份證號後9位應為數字';
        }

        if (errorElement && errorElement.classList.contains('invalid-feedback')) {
            errorElement.textContent = errorMsg;
        }
    } else {
        input.classList.add('is-valid');
        input.setCustomValidity('');
    }
}

/**
 * 統一編號驗證
 */
function initializeUnicodeValidation() {
    const unicodeInput = document.getElementById('unicode');
    if (!unicodeInput) return;

    unicodeInput.addEventListener('input', function () {
        validateUnicode(this);
    });
    
    unicodeInput.addEventListener('blur', function () {
        validateUnicode(this);
    });
}

/**
 * 驗證統一編號
 */
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

/**
 * 密碼驗證
 */
function initializePasswordValidation() {
    const passwordInput = document.getElementById('password');
    const rePasswordInput = document.getElementById('rePassword');
    
    if (!passwordInput || !rePasswordInput) return;

    passwordInput.addEventListener('input', function () {
        // 密碼長度驗證
        const password = this.value;
        this.classList.remove('is-valid', 'is-invalid');
        this.setCustomValidity('');

        if (password === '') {
            return;
        }

        if (password.length < 6) {
            this.classList.add('is-invalid');
            this.setCustomValidity('密碼長度至少需要6個字元');
        } else {
            this.classList.add('is-valid');
            this.setCustomValidity('');
        }

        // 同時驗證密碼確認
        validatePasswordMatch();
    });

    rePasswordInput.addEventListener('input', validatePasswordMatch);
}

/**
 * 密碼確認驗證
 */
function validatePasswordMatch() {
    const passwordInput = document.getElementById('password');
    const rePasswordInput = document.getElementById('rePassword');
    
    if (!passwordInput || !rePasswordInput) return;

    const password = passwordInput.value;
    const rePassword = rePasswordInput.value;

    rePasswordInput.classList.remove('is-valid', 'is-invalid');
    rePasswordInput.setCustomValidity('');

    if (rePassword === '') {
        return;
    }

    if (password !== rePassword) {
        rePasswordInput.classList.add('is-invalid');
        rePasswordInput.setCustomValidity('密碼確認不相符');
        const errorElement = rePasswordInput.nextElementSibling;
        if (errorElement && errorElement.classList.contains('invalid-feedback')) {
            errorElement.textContent = '密碼確認不相符';
        }
    } else if (password.length >= 6) {
        rePasswordInput.classList.add('is-valid');
        rePasswordInput.setCustomValidity('');
    }
}

/**
 * 電子郵件驗證
 */
function initializeEmailValidation() {
    const emailInput = document.getElementById('email');
    if (!emailInput) return;

    emailInput.addEventListener('input', function () {
        const value = this.value.trim();
        const emailPattern = /^[^\s@]+@[^\s@]+\.[a-zA-Z]{2,6}$/;

        this.classList.remove('is-valid', 'is-invalid');
        this.setCustomValidity('');

        if (value === '') {
            return;
        }

        if (!emailPattern.test(value)) {
            this.classList.add('is-invalid');
            this.setCustomValidity('請輸入有效的電子郵件地址');
        } else {
            this.classList.add('is-valid');
            this.setCustomValidity('');
        }
    });
}

/**
 * 手機號碼驗證
 */
function initializePhoneValidation() {
    const phoneInput = document.getElementById('phone');
    if (!phoneInput) return;

    phoneInput.addEventListener('input', function () {
        const value = this.value.trim();
        const phonePattern = /^09\d{8}$/;

        this.classList.remove('is-valid', 'is-invalid');
        this.setCustomValidity('');

        if (value === '') {
            return;
        }

        if (!phonePattern.test(value)) {
            this.classList.add('is-invalid');
            let errorMsg = '請輸入有效的台灣手機號碼（09開頭共10碼）';
            if (value.length !== 10) {
                errorMsg = `手機號碼應為10位數字（目前${value.length}位）`;
            } else if (!value.startsWith('09')) {
                errorMsg = '手機號碼應以09開頭';
            }
            this.setCustomValidity(errorMsg);

            const errorElement = this.nextElementSibling;
            if (errorElement && errorElement.classList.contains('invalid-feedback')) {
                errorElement.textContent = errorMsg;
            }
        } else {
            this.classList.add('is-valid');
            this.setCustomValidity('');
        }
    });
}

/**
 * 必填欄位驗證
 */
function initializeRequiredFieldsValidation() {
    const form = document.getElementById('memberForm');
    if (!form) return;

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
    });

    // 即時驗證（移除錯誤狀態）
    const inputs = form.querySelectorAll('input[required], select[required]');
    inputs.forEach(input => {
        input.addEventListener('input', function () {
            if (this.checkValidity()) {
                this.classList.remove('is-invalid');
                this.classList.add('is-valid');
            }
        });
    });
}

/**
 * 表單提交處理
 */
function initializeFormSubmit() {
    const form = document.getElementById('memberForm');
    if (!form) return;

    form.addEventListener('submit', function (e) {
        e.preventDefault();

        if (!form.checkValidity()) {
            e.stopPropagation();
            form.classList.add('was-validated');
            return;
        }

        // 自定義驗證
        let isValid = true;

        const customValidatedFields = [
            document.getElementById('id_card'),
            document.getElementById('unicode'),
            document.getElementById('password'),
            document.getElementById('rePassword'),
            document.getElementById('email'),
            document.getElementById('phone')
        ];

        customValidatedFields.forEach(field => {
            if (field && !field.checkValidity()) {
                isValid = false;
            }
        });

        form.classList.add('was-validated');

        if (isValid) {
            submitForm();
        }
    });
}

/**
 * 表單重置處理
 */
function initializeFormReset() {
    const form = document.getElementById('memberForm');
    const photoPreview = document.getElementById('photoPreview');
    const defaultPhoto = '../common/assets/img/user2-128x128.png';
    
    if (!form) return;

    form.addEventListener('reset', function () {
        form.classList.remove('was-validated');
        if (photoPreview) {
            photoPreview.src = defaultPhoto;
        }
        // 重置所有自定義驗證訊息
        const inputs = form.querySelectorAll('input, select');
        inputs.forEach(input => {
            input.setCustomValidity('');
        });
    });
}

/**
 * 活動方申請功能
 */
function initializeHostApplyFeature() {
    const hostApplyCheckbox = document.getElementById('hostApply');
    const roleLevel = document.getElementById('role_level');
    
    if (!hostApplyCheckbox || !roleLevel) return;

    hostApplyCheckbox.addEventListener('change', function () {
        if (this.checked) {
            roleLevel.value = '2'; // 活動方
            roleLevel.disabled = true;
        } else {
            roleLevel.disabled = false;
            roleLevel.value = ''; // 重置選擇
        }
    });
}

/**
 * 提交表單
 */
function submitForm() {
    // 顯示載入狀態
    const submitBtn = document.querySelector('button[type="submit"]');
    const originalText = submitBtn.innerHTML;
    submitBtn.innerHTML = '<i class="bi bi-hourglass-split"></i> 建立中...';
    submitBtn.disabled = true;

    const form = document.getElementById('memberForm');
    // 收集表單資料
    const formData = new FormData();

    // 基本欄位
    formData.append('userName', form.user_name.value);
    formData.append('nickName', form.nick_name.value);
    formData.append('email', form.email.value);
    formData.append('phone', form.phone.value || '');
    formData.append('birthDate', form.birth_date.value || '');
    formData.append('gender', form.gender.value || '');
    formData.append('idCard', form.id_card.value || '');
    formData.append('unicode', form.unicode.value || '');
    formData.append('password', form.password.value);
    formData.append('roleLevel', form.role_level.value);
    formData.append('isActive', form.is_active.checked ? '1' : '0');
    formData.append('rePassword', form.rePassword.value);
    formData.append('hostApply', form.hostApply.checked);
    formData.append('agree', form.agree.checked);

    // 提交到後端
    fetch('/maven-tickeasy-v1/api/manager/member', {
        method: 'POST',
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
                uploadPhoto(data.data.memberId, form.photo.files[0])
                    .then(() => {
                        showSuccessMessage();
                    })
                    .catch(() => {
                        showSuccessMessage('會員建立成功，但照片上傳失敗');
                    });
            } else {
                showSuccessMessage();
            }
        } else {
            showErrorMessage(data.message);
        }
    })
    .catch(error => {
        console.error('建立會員失敗:', error);
        showErrorMessage('系統錯誤，請稍後再試');
    })
    .finally(() => {
        submitBtn.innerHTML = originalText;
        submitBtn.disabled = false;
    });
}

/**
 * 上傳照片
 */
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

/**
 * 顯示成功訊息
 */
function showSuccessMessage(customMessage) {
    const message = customMessage || '會員已成功建立。';
    alert(message);

    // 立即跳轉回列表頁
    window.location.href = 'member_list.html';
}

/**
 * 顯示錯誤訊息
 */
function showErrorMessage(message) {
    alert('錯誤：' + message);
}

/**
 * 頁面載入完成後初始化所有功能
 */
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

    // 權限檢查通過後，初始化 header
    if (typeof window.initHeader === 'function') {
        window.initHeader();
    }
    
    // 表單初始化
    initializeForm();
    
    // 密碼顯示/隱藏功能
    initializePasswordToggle();
    
    // 照片預覽功能
    initializePhotoPreview();
    
    // 表單驗證功能
    initializeFormValidation();
    
    // 活動方申請功能
    initializeHostApplyFeature();
});