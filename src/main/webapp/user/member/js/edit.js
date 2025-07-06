import { fetchMemberFromSession, getContextPath } from "../../common/utils.js";

const loggedInNickname = document.querySelector("#loggedInNickname");
const logoutButton = document.querySelector("#logoutButton");
const saveButton = document.querySelector("#saveButton");
const username = document.querySelector("#username");
const password = document.querySelector("#password");
const cPassword = document.querySelector("#cPassword");
const nickname = document.querySelector("#nickname");
const email = document.querySelector("#email");
const unicode = document.querySelector("#unicode");
const msg = document.querySelector("#msg");
const photoPreview = document.querySelector("#photoPreview");
const photoInput = document.querySelector("#photoInput");
const changePasswordBtn = document.querySelector("#changePasswordBtn");
const passwordChangeArea = document.querySelector("#passwordChangeArea");
const defaultIcon = document.querySelector("#defaultIcon");
const sendVerificationBtn = document.querySelector("#sendVerificationBtn");
const verificationMsg = document.querySelector("#verificationMsg");
const photoError = document.querySelector("#photoError");

// 顯示登入者暱稱
loggedInNickname.textContent =
  sessionStorage.getItem("loggedInNickname") || "訪客";

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
    photoPreview.classList.remove("show");
    defaultIcon.classList.remove("hide");
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
      photoPreview.classList.add("show");
      defaultIcon.classList.add("hide");
    };

    img.onerror = function () {
      // 照片載入失敗，顯示預設圖示
      console.log("照片載入失敗，顯示預設圖示");
      photoPreview.classList.remove("show");
      defaultIcon.classList.remove("hide");
    };

    img.src = photoUrl;
  } catch (err) {
    console.error("載入照片失敗:", err);
    photoPreview.classList.remove("show");
    defaultIcon.classList.remove("hide");
  }
}

// 載入會員資料
loadMemberInfo();

// 圖片即時預覽
photoInput.addEventListener("change", (e) => {
  const file = e.target.files[0];
  if (!file) {
    // 沒有選擇檔案時，顯示預設圖示
    photoPreview.classList.remove("show");
    defaultIcon.classList.remove("hide");
    return;
  }
  const reader = new FileReader();
  reader.onload = () => {
    photoPreview.src = reader.result;
    photoPreview.classList.add("show");
    defaultIcon.classList.add("hide");
  };
  reader.readAsDataURL(file);
});

// 密碼區塊滑動展開/收合
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

// 儲存變更
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

// 登出
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

sendVerificationBtn.addEventListener("click", () => {
  verificationMsg.textContent = "";
  const em = email.value.trim();
  if (!em || !/^[\w.-]+@[\w.-]+\.[A-Za-z]{2,6}$/.test(em)) {
    verificationMsg.textContent = "請輸入正確的電子郵件";
    verificationMsg.style.color = "red";
    return;
  }
  sendVerificationBtn.disabled = true;
  // 使用 getContextPath() 替代硬編碼路徑
  fetch(`${getContextPath()}/user/member/edit/send-verify-mail`, {
    method: "POST",
    credentials: "include",
  })
    .then((resp) => resp.json())
    .then((body) => {
      if (body.successful) {
        verificationMsg.textContent = "驗證信已發送，請至信箱收信";
        verificationMsg.style.color = "blue";
      } else {
        verificationMsg.textContent = body.message || "發送失敗";
        verificationMsg.style.color = "red";
      }
      sendVerificationBtn.disabled = false;
    })
    .catch(() => {
      verificationMsg.textContent = "伺服器錯誤，請稍後再試";
      verificationMsg.style.color = "red";
      sendVerificationBtn.disabled = false;
    });
});
