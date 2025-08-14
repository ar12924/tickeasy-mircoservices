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

const passwordUpdateStatus = document.querySelector("#passwordUpdateStatus");
const passwordUpdateStatusText = document.querySelector(
  "#passwordUpdateStatusText"
);

const nicknameError = document.querySelector("#nicknameError");
const emailError = document.querySelector("#emailError");
const phoneError = document.querySelector("#phoneError");
const birthDateError = document.querySelector("#birthDateError");
const genderError = document.querySelector("#genderError");

const urlParams = new URLSearchParams(window.location.search);
const success = urlParams.get("success");
const error = urlParams.get("error");

if (success === "password_updated") {
  msg.textContent = "密碼更新成功！";
  msg.style.color = "green";
  if (passwordUpdateStatus) {
    passwordUpdateStatus.style.display = "block";
    passwordUpdateStatus.style.backgroundColor = "#d4edda";
    passwordUpdateStatus.style.borderColor = "#c3e6cb";
    passwordUpdateStatusText.innerHTML =
      '<i class="fas fa-check-circle" style="color: #28a745; margin-right: 5px;"></i>密碼已成功更新！';
  }
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
  if (passwordUpdateStatus) {
    passwordUpdateStatus.style.display = "block";
    passwordUpdateStatus.style.backgroundColor = "#f8d7da";
    passwordUpdateStatus.style.borderColor = "#f5c6cb";
    passwordUpdateStatusText.innerHTML = `<i class=\"fas fa-exclamation-circle\" style=\"color: #dc3545; margin-right: 5px;\"></i>${errorMessage}`;
  }
  window.history.replaceState({}, document.title, window.location.pathname);
}

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
    username.value = data.userName || "";
    nickname.value = data.nickName || "";
    email.value = data.email || "";
    unicode.value = data.unicode || "";
    phone.value = data.phone || "";
    if (data.birthDate) {
      const d = new Date(data.birthDate);
      const local = new Date(d.getTime() - d.getTimezoneOffset() * 60000)
        .toISOString()
        .slice(0, 10);
      birthDate.value = local;
    } else {
      birthDate.value = "";
    }
    gender.value = data.gender || "";
    await loadMemberPhoto(data.memberId);
  } catch (err) {
    msg.textContent = "載入會員資料失敗";
    msg.innerHTML += '<br><a href="login.html">重新登入</a>';
  }
}

async function loadMemberPhoto(memberId) {
  if (!memberId) {
    photoPreview.style.display = "none";
    defaultIcon.style.display = "block";
    return;
  }
  try {
    const photoUrl = `${getContextPath()}/api/member-photos/${memberId}`;
    const img = new Image();
    img.onload = function () {
      photoPreview.src = photoUrl + "?t=" + new Date().getTime();
      photoPreview.style.display = "block";
      defaultIcon.style.display = "none";
    };
    img.onerror = function () {
      photoPreview.style.display = "none";
      defaultIcon.style.display = "block";
    };
    img.src = photoUrl;
  } catch (err) {
    photoPreview.style.display = "none";
    defaultIcon.style.display = "block";
  }
}

loadMemberInfo();

if (photoInput) {
  photoInput.addEventListener("change", (e) => {
    const file = e.target.files[0];
    if (!file) {
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
}

nickname.addEventListener("input", function () {
  if (nickname.value.trim().length < 1 || nickname.value.trim().length > 20) {
    nicknameError.textContent = "暱稱長度須介於 1～20 字元";
  } else {
    nicknameError.textContent = "";
  }
});

email.addEventListener("input", function () {
  if (!/^[\w.-]+@[\w.-]+\.[A-Za-z]{2,6}$/.test(email.value.trim())) {
    emailError.textContent = "電子郵件格式錯誤";
  } else {
    emailError.textContent = "";
  }
});

phone.addEventListener("input", function () {
  if (!/^09\d{8}$/.test(phone.value.trim())) {
    phoneError.textContent = "手機格式錯誤，需為台灣手機號碼 09 開頭共 10 碼";
  } else {
    phoneError.textContent = "";
  }
});

birthDate.addEventListener("change", function () {
  if (!birthDate.value) {
    birthDateError.textContent = "出生日期不可為空";
  } else {
    birthDateError.textContent = "";
  }
});

gender.addEventListener("change", function () {
  if (!(gender.value === "M" || gender.value === "F")) {
    genderError.textContent = "性別請選擇男 (M) 或 女 (F)";
  } else {
    genderError.textContent = "";
  }
});

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
}

if (saveButton) {
  saveButton.addEventListener("click", (e) => {
    e.preventDefault();
    msg.textContent = "";
    photoError.textContent = "";
    if (photoInput.files.length > 0) {
      const file = photoInput.files[0];
      if (!file.type.startsWith("image/")) {
        photoError.textContent = "請選擇圖片檔案";
        return;
      }
    }
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
    const hasPhoto = photoInput.files.length > 0;
    const doEdit = (photoKey) => {
      const payload = { ...member };
      if (photoKey) payload.photoKey = photoKey;
      fetch(`/api/members/edit`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
      })
        .then((resp) => resp.json())
        .then((body) => {
          if (body.successful) {
            msg.style.color = "blue";
            msg.innerHTML = `修改成功<br>
  						使用者名稱：${body.data.userName}<br>
  						暱稱：${body.data.nickName}`;
            sessionStorage.setItem("loggedInNickname", body.data.nickName);
            fetchNavTemplate().then(renderNav);
            if (photoKey) {
              loadMemberPhoto(body.data.memberId);
            }
          } else {
            msg.style.color = "red";
            msg.textContent = body.message || "更新失敗";
          }
        })
        .catch(() => {
          msg.style.color = "red";
          msg.textContent = "伺服器錯誤，請稍後再試";
        });
    };

    if (!hasPhoto) {
      doEdit(null);
      return;
    }
    const file = photoInput.files[0];
    fetch(`/api/media/presign-upload`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ contentType: file.type }),
    })
      .then((resp) => resp.json())
      .then(async (data) => {
        if (!data || !data.url || !data.key) throw new Error("預簽名失敗");
        const putResp = await fetch(data.url, {
          method: "PUT",
          headers: { "Content-Type": file.type },
          body: file,
        });
        if (!putResp.ok) throw new Error("上傳失敗");
        doEdit(data.key);
      })
      .catch((e) => {
        msg.style.color = "red";
        msg.textContent = e.message || "圖片上傳失敗";
      });
  });
}

if (logoutButton) {
  logoutButton.addEventListener("click", () => {
    if (!confirm("是否登出？")) return;
    fetch(`${getContextPath()}/user/member/logout`, {
      method: "DELETE",
      credentials: "include",
    })
      .then((resp) => resp.json())
      .then((body) => {
        if (body.successful) {
          sessionStorage.removeItem("loggedInNickname");
          location.href = "login.html";
        }
      });
  });
}

if (sendVerificationBtn) {
  sendVerificationBtn.addEventListener("click", () => {
    verificationMsg.textContent = "";
    const pw = password.value.trim();
    const cpw = cPassword.value.trim();
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
    fetch(`${getContextPath()}/user/member/edit/send-password-update-mail`, {
      method: "POST",
      headers: { "Content-Type": "application/x-www-form-urlencoded" },
      body: `newPassword=${encodeURIComponent(pw)}`,
      credentials: "include",
    })
      .then((resp) => resp.json())
      .then((body) => {
        if (body.successful) {
          passwordUpdateStatus.style.display = "block";
          passwordUpdateStatusText.textContent =
            "密碼更新認證信已發送，請至信箱收信確認";
          verificationMsg.textContent = "";
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
}
