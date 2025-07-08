import { fetchMemberFromSession, getContextPath } from "../../common/utils.js";
import { fetchNavTemplate, renderNav } from "../../layout/nav/nav.js";

const logoutButton = document.querySelector("#logoutButton");
const saveButton = document.querySelector("#saveButton");
const username = document.querySelector("#username");
const password = document.querySelector("#password");
const cPassword = document.querySelector("#cPassword");
const nickname = document.querySelector("#nickname");
const email = document.querySelector("#email");
const unicode = document.querySelector("#unicode");
const phone = document.querySelector("#phone");
const birthDate = document.querySelector("#birthDate");
const gender = document.querySelector("#gender");
const msg = document.querySelector("#msg");
const photoPreview = document.querySelector("#photoPreview");
const photoInput = document.querySelector("#photoInput");
const changePasswordBtn = document.querySelector("#changePasswordBtn");
const passwordChangeArea = document.querySelector("#passwordChangeArea");
const defaultIcon = document.querySelector("#defaultIcon");
const sendVerificationBtn = document.querySelector("#sendVerificationBtn");
const verificationMsg = document.querySelector("#verificationMsg");
const photoError = document.querySelector("#photoError");

// 密碼更新狀態指示器
const passwordUpdateStatus = document.querySelector("#passwordUpdateStatus");
const passwordUpdateStatusText = document.querySelector(
  "#passwordUpdateStatusText"
);

// 檢查 URL 參數，顯示密碼更新結果
const urlParams = new URLSearchParams(window.location.search);
const success = urlParams.get("success");
const error = urlParams.get("error");

if (success === "password_updated") {
  msg.textContent = "密碼更新成功！";
  msg.style.color = "green";
  // 更新狀態指示器
  if (passwordUpdateStatus) {
    passwordUpdateStatus.style.display = "block";
    passwordUpdateStatus.style.backgroundColor = "#d4edda";
    passwordUpdateStatus.style.borderColor = "#c3e6cb";
    passwordUpdateStatusText.innerHTML =
      '<i class="fas fa-check-circle" style="color: #28a745; margin-right: 5px;"></i>密碼已成功更新！';
  }
  // 清除 URL 參數
  window.history.replaceState({}, document.title, window.location.pathname);
} else if (error) {
  let errorMessage = "";
  switch (error) {
    case "invalid_token":
      errorMessage = "認證連結無效或已過期";
      break;
    case "no_password":
      errorMessage = "找不到密碼更新請求";
      break;
    case "update_failed":
      errorMessage = "密碼更新失敗";
      break;
    case "system_error":
      errorMessage = "系統錯誤，請稍後再試";
      break;
    default:
      errorMessage = "發生未知錯誤";
  }
  msg.textContent = errorMessage;
  msg.style.color = "red";
  // 更新狀態指示器顯示錯誤
  if (passwordUpdateStatus) {
    passwordUpdateStatus.style.display = "block";
    passwordUpdateStatus.style.backgroundColor = "#f8d7da";
    passwordUpdateStatus.style.borderColor = "#f5c6cb";
    passwordUpdateStatusText.innerHTML = `<i class="fas fa-exclamation-circle" style="color: #dc3545; margin-right: 5px;"></i>${errorMessage}`;
  }
  // 清除 URL 參數
  window.history.replaceState({}, document.title, window.location.pathname);
}

// 載入會員資訊
async function loadMemberInfo() {
  try {
    const resp = await fetch(`${getContextPath()}/user/member/edit`, {
      method: "GET",
      credentials: "include",
    });
    const body = await resp.json();

    if (!body.successful) {
      msg.textContent = "尚未登入，請先登入";
      msg.innerHTML += '<br><a href="login.html">登入頁面</a>';
      return;
    }
    const data = body.data;
    console.log("會員資料:", data); // 除錯資訊
    username.value = data.userName || "";
    nickname.value = data.nickName || "";
    email.value = data.email || "";
    unicode.value = data.unicode || "";
    phone.value = data.phone || "";
    // birthDate 需轉 yyyy-MM-dd 字串
    if (data.birthDate) {
      const d = new Date(data.birthDate);
      // 修正時區偏移，確保日期正確
      const local = new Date(d.getTime() - d.getTimezoneOffset() * 60000)
        .toISOString()
        .slice(0, 10);
      birthDate.value = local;
    } else {
      birthDate.value = "";
    }
    gender.value = data.gender || "";

    // 載入會員照片
    await loadMemberPhoto(data.memberId);
  } catch (err) {
    console.error(err);
    msg.textContent = "載入會員資料失敗";
    msg.innerHTML += '<br><a href="login.html">重新登入</a>';
  }
}

// 載入會員照片
async function loadMemberPhoto(memberId) {
  if (!memberId) {
    console.warn("會員ID為空，無法載入照片");
    photoPreview.style.display = "none";
    defaultIcon.style.display = "block";
    return;
  }

  console.log("載入會員照片，會員ID:", memberId); // 除錯資訊
  try {
    const photoUrl = `${getContextPath()}/api/member-photos/${memberId}`;
    console.log("照片URL:", photoUrl); // 除錯資訊
    const img = new Image();

    img.onload = function () {
      console.log("照片載入成功"); // 除錯資訊
      photoPreview.src = photoUrl + "?t=" + new Date().getTime(); // 加時間戳避免快取
      photoPreview.style.display = "block";
      defaultIcon.style.display = "none";
    };

    img.onerror = function () {
      // 照片載入失敗，顯示預設圖示
      console.log("照片載入失敗，顯示預設圖示");
      photoPreview.style.display = "none";
      defaultIcon.style.display = "block";
    };

    img.src = photoUrl;
  } catch (err) {
    console.error("載入照片失敗:", err);
    photoPreview.style.display = "none";
    defaultIcon.style.display = "block";
  }
}

// 載入會員資料
loadMemberInfo();

// 圖片即時預覽
if (photoInput) {
  photoInput.addEventListener("change", (e) => {
    const file = e.target.files[0];
    if (!file) {
      // 沒有選擇檔案時，顯示預設圖示
      photoPreview.style.display = "none";
      defaultIcon.style.display = "block";
      return;
    }
    const reader = new FileReader();
    reader.onload = () => {
      photoPreview.src = reader.result;
      photoPreview.style.display = "block";
      defaultIcon.style.display = "none";
    };
    reader.readAsDataURL(file);
  });
} else {
  console.warn("#photoInput not found");
}

// 密碼區塊滑動展開/收合
if (changePasswordBtn) {
  changePasswordBtn.addEventListener("click", () => {
    const isVisible = passwordChangeArea.classList.contains("show");

    if (!isVisible) {
      passwordChangeArea.classList.add("show");
      changePasswordBtn.textContent = "取消變更密碼";
    } else {
      passwordChangeArea.classList.remove("show");
      changePasswordBtn.textContent = "變更密碼";
      password.value = "";
      cPassword.value = "";
    }
  });
} else {
  console.warn("#changePasswordBtn not found");
}

// 儲存變更
if (saveButton) {
  saveButton.addEventListener("click", (e) => {
    e.preventDefault();
    msg.textContent = "";
    photoError.textContent = "";

    // 檢查照片格式
    if (photoInput.files.length > 0) {
      const file = photoInput.files[0];
      if (!file.type.startsWith("image/")) {
        photoError.textContent = "請選擇圖片檔案";
        return;
      }
    }

    // 前端驗證
    const nick = nickname.value.trim();
    const em = email.value.trim();
    const uni = unicode.value.trim();
    const pw = password.value.trim();
    const cpw = cPassword.value.trim();

    if (nick.length < 1 || nick.length > 20) {
      msg.textContent = "暱稱長度須介於 1～20 字元";
      return;
    }
    if (em && !/^[\w.-]+@[\w.-]+\.[A-Za-z]{2,6}$/.test(em)) {
      msg.textContent = "電子郵件格式錯誤";
      return;
    }
    if (uni && !/^\d{8}$/.test(uni)) {
      msg.textContent = "統一編號格式錯誤，應為 8 碼數字";
      return;
    }
    if (passwordChangeArea.classList.contains("show") && (pw || cpw)) {
      if (pw.length < 6) {
        msg.textContent = "密碼長度須大於等於 6 字元";
        return;
      }
      if (pw !== cpw) {
        msg.textContent = "密碼與確認密碼不一致";
        return;
      }
    }

    // 組成會員資料物件
    const member = {
      nickName: nick,
      email: em,
      unicode: uni,
      phone: phone.value.trim(),
      birthDate: birthDate.value ? birthDate.value : null,
      gender: gender.value,
    };
    if (passwordChangeArea.classList.contains("show") && pw) {
      member.password = pw;
    }

    // 建立 FormData
    const fd = new FormData();
    fd.append("member", JSON.stringify(member));
    if (photoInput.files.length > 0) {
      fd.append("photo", photoInput.files[0]);
    }

    // 使用 getContextPath() 替代硬編碼路徑
    fetch(`${getContextPath()}/user/member/edit/update`, {
      method: "POST",
      credentials: "include",
      body: fd,
    })
      .then((resp) => resp.json())
      .then((body) => {
        if (body.successful) {
          msg.style.color = "blue";
          msg.innerHTML = `修改成功<br>
							使用者名稱：${body.data.userName}<br>
							暱稱：${body.data.nickName}`;
          sessionStorage.setItem("loggedInNickname", body.data.nickName);

          // 重新渲染 nav
          fetchNavTemplate().then(renderNav);

          // 如果成功儲存照片，重新載入照片
          if (photoInput.files.length > 0) {
            loadMemberPhoto(body.data.memberId);
          }
        } else {
          msg.style.color = "red";
          msg.textContent = body.message || "更新失敗";
        }
      })
      .catch((err) => {
        msg.style.color = "red";
        msg.textContent = "伺服器錯誤，請稍後再試";
      });
  });
} else {
  console.warn("#saveButton not found");
}

// 登出
if (logoutButton) {
  logoutButton.addEventListener("click", () => {
    if (!confirm("是否登出？")) return;
    // 使用 getContextPath() 替代硬編碼路徑
    fetch(`${getContextPath()}/user/member/logout`, {
      method: "DELETE",
      credentials: "include",
    })
      .then((resp) => resp.json())
      .then((body) => {
        if (body.successful) {
          sessionStorage.removeItem("loggedInNickname");
          location.href = "login.html";
        } else {
          alert(body.message || "登出失敗");
        }
      });
  });
} else {
  console.warn("#logoutButton not found");
}

if (sendVerificationBtn) {
  sendVerificationBtn.addEventListener("click", () => {
    verificationMsg.textContent = "";
    const pw = password.value.trim();
    const cpw = cPassword.value.trim();

    // 驗證密碼
    if (!pw || !cpw) {
      verificationMsg.textContent = "請輸入新密碼和確認密碼";
      verificationMsg.style.color = "red";
      return;
    }

    if (pw.length < 6) {
      verificationMsg.textContent = "密碼長度至少需要6個字元";
      verificationMsg.style.color = "red";
      return;
    }

    if (pw !== cpw) {
      verificationMsg.textContent = "密碼與確認密碼不一致";
      verificationMsg.style.color = "red";
      return;
    }

    sendVerificationBtn.disabled = true;
    sendVerificationBtn.textContent = "發送中...";

    // 調用發送密碼更新認證信 API
    fetch(`${getContextPath()}/user/member/edit/send-password-update-mail`, {
      method: "POST",
      headers: {
        "Content-Type": "application/x-www-form-urlencoded",
      },
      body: `newPassword=${encodeURIComponent(pw)}`,
      credentials: "include",
    })
      .then((resp) => resp.json())
      .then((body) => {
        if (body.successful) {
          // 顯示狀態指示器
          passwordUpdateStatus.style.display = "block";
          passwordUpdateStatusText.textContent =
            "密碼更新認證信已發送，請至信箱收信確認";
          verificationMsg.textContent = "";
          // 不清空密碼欄位，讓使用者可以繼續編輯其他資料
          // 不隱藏密碼變更區域，保持當前狀態
        } else {
          verificationMsg.textContent = body.message || "發送失敗";
          verificationMsg.style.color = "red";
        }
        sendVerificationBtn.disabled = false;
        sendVerificationBtn.textContent = "發送密碼更新認證信";
      })
      .catch(() => {
        verificationMsg.textContent = "伺服器錯誤，請稍後再試";
        verificationMsg.style.color = "red";
        sendVerificationBtn.disabled = false;
        sendVerificationBtn.textContent = "發送密碼更新認證信";
      });
  });
} else {
  console.warn("#sendVerificationBtn not found");
}
