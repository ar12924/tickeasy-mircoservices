import { getUrlParam, getContextPath } from "../../common/utils.js";

document.addEventListener("DOMContentLoaded", function () {
  const token = getUrlParam("token");

  const step1 = document.getElementById("step1");
  const step2 = document.getElementById("step2");
  const email = document.getElementById("email");
  const newPassword = document.getElementById("newPassword");
  const confirmPassword = document.getElementById("confirmPassword");
  const requestBtn = document.getElementById("requestBtn");
  const resetBtn = document.getElementById("resetBtn");
  const message1 = document.getElementById("message1");
  const message2 = document.getElementById("message2");

  // 如果有 token，直接顯示步驟2
  if (token) {
    step1.classList.add("hidden");
    step2.classList.remove("hidden");
    verifyToken(token);
  }

  // 請求密碼重設
  requestBtn.addEventListener("click", () => {
    const emailValue = email.value.trim();
    if (!emailValue) {
      showMessage(message1, "請輸入 Email 地址", "error");
      return;
    }

    requestBtn.disabled = true;
    requestBtn.textContent = "發送中...";

    fetch(`${getContextPath()}/user/member/reset-password/request`, {
      method: "POST",
      headers: {
        "Content-Type": "application/x-www-form-urlencoded",
      },
      body: `email=${encodeURIComponent(emailValue)}`,
    })
      .then((res) => res.json())
      .then((data) => {
        if (data.successful) {
          showMessage(message1, data.message, "success");
          requestBtn.textContent = "郵件已發送";
        } else {
          showMessage(message1, data.message, "error");
          requestBtn.disabled = false;
          requestBtn.textContent = "發送重置郵件";
        }
      })
      .catch((err) => {
        showMessage(message1, "發送失敗，請稍後再試", "error");
        requestBtn.disabled = false;
        requestBtn.textContent = "發送重置郵件";
      });
  });

  // 重置密碼
  resetBtn.addEventListener("click", () => {
    const newPwd = newPassword.value;
    const confirmPwd = confirmPassword.value;

    if (!newPwd || !confirmPwd) {
      showMessage(message2, "請輸入新密碼和確認密碼", "error");
      return;
    }

    if (newPwd.length < 6) {
      showMessage(message2, "密碼長度至少需要 6 個字元", "error");
      return;
    }

    if (newPwd !== confirmPwd) {
      showMessage(message2, "密碼與確認密碼不一致", "error");
      return;
    }

    resetBtn.disabled = true;
    resetBtn.textContent = "重置中...";

    fetch(`${getContextPath()}/user/member/reset-password/reset`, {
      method: "POST",
      headers: {
        "Content-Type": "application/x-www-form-urlencoded",
      },
      body: `token=${encodeURIComponent(
        token
      )}&newPassword=${encodeURIComponent(newPwd)}`,
    })
      .then((res) => res.json())
      .then((data) => {
        if (data.successful) {
          showMessage(message2, data.message, "success");
          resetBtn.textContent = "重置成功";
          setTimeout(() => {
            window.location.href = "login.html";
          }, 2000);
        } else {
          showMessage(message2, data.message, "error");
          resetBtn.disabled = false;
          resetBtn.textContent = "重置密碼";
        }
      })
      .catch((err) => {
        showMessage(message2, "重置失敗，請稍後再試", "error");
        resetBtn.disabled = false;
        resetBtn.textContent = "重置密碼";
      });
  });

  // 驗證 token
  function verifyToken(token) {
    fetch(
      `${getContextPath()}/user/member/reset-password/verify?token=${encodeURIComponent(
        token
      )}`
    )
      .then((res) => res.json())
      .then((data) => {
        if (!data.successful) {
          showMessage(message2, data.message, "error");
          resetBtn.disabled = true;
        }
      })
      .catch((err) => {
        showMessage(message2, "驗證失敗，請稍後再試", "error");
        resetBtn.disabled = true;
      });
  }

  function showMessage(element, message, type) {
    element.textContent = message;
    element.className = `message ${type}`;
  }
});
