import { getContextPath } from "../../common/utils.js";

const username = document.querySelector("#username");
const password = document.querySelector("#password");
const remember = document.querySelector("#rememberMe");
const msg = document.querySelector("#msg");
const loginBtn = document.querySelector("#loginBtn");

const resendSection = document.querySelector("#resendVerificationSection");
const resendEmail = document.querySelector("#resendEmail");
const resendBtn = document.querySelector("#resendBtn");
const resendMsg = document.querySelector("#resendMsg");

const urlParams = new URLSearchParams(window.location.search);
const verified = urlParams.get("verified");

if (verified === "true") {
  msg.style.color = "green";
  msg.textContent = "驗證成功！請接續登入";
  window.history.replaceState({}, document.title, window.location.pathname);
} else if (verified === "false") {
  msg.style.color = "red";
  msg.textContent = "驗證失敗，請確認連結是否有效";
  window.history.replaceState({}, document.title, window.location.pathname);
}

const saved = localStorage.getItem("savedUsername");
if (saved) {
  username.value = saved;
  if (remember) remember.checked = true;
}

if (resendBtn) {
  resendBtn.addEventListener("click", () => {
    const email = resendEmail.value.trim();
    if (!email) {
      resendMsg.textContent = "請輸入 Email 地址";
      resendMsg.style.color = "red";
      return;
    }
    resendBtn.disabled = true;
    resendBtn.textContent = "發送中...";
    resendMsg.textContent = "";
    fetch(`${getContextPath()}/user/member/register/resend-verification`, {
      method: "POST",
      headers: { "Content-Type": "application/x-www-form-urlencoded" },
      body: `email=${encodeURIComponent(email)}`,
    })
      .then((res) => res.json())
      .then((data) => {
        if (data.successful) {
          resendMsg.textContent = data.message;
          resendMsg.style.color = "green";
          resendBtn.textContent = "發送成功";
        } else {
          resendMsg.textContent = data.message;
          resendMsg.style.color = "red";
          resendBtn.disabled = false;
          resendBtn.textContent = "重新發送驗證信";
        }
      })
      .catch(() => {
        resendMsg.textContent = "發送失敗，請稍後再試";
        resendMsg.style.color = "red";
        resendBtn.disabled = false;
        resendBtn.textContent = "重新發送驗證信";
      });
  });
}

if (loginBtn) {
  loginBtn.addEventListener("click", () => {
    msg.textContent = "";
    msg.style.color = "red";
    if (!username.value.trim() || !password.value) {
      msg.textContent = "請輸入帳號與密碼";
      return;
    }
    fetch(
      `${getContextPath()}/user/member/login/${username.value}/${
        password.value
      }`
    )
      .then((resp) => resp.json())
      .then((body) => {
        if (body.successful) {
          msg.style.color = "green";
          const memberData = body.data;
          sessionStorage.setItem("loggedInNickname", memberData.nickName || "");
          sessionStorage.setItem("memberId", memberData.memberId || "");
          sessionStorage.setItem("roleLevel", memberData.roleLevel || "");
          if (remember && remember.checked) {
            localStorage.setItem("savedUsername", username.value.trim());
          } else {
            localStorage.removeItem("savedUsername");
          }
          const role = memberData.roleLevel;
          if (parseInt(role) === 2 || parseInt(role) === 3) {
            window.location.href = `${getContextPath()}/manager/index.html`;
          } else {
            window.location.href = `${getContextPath()}/user/buy/index.html`;
          }
        } else {
          if (body.message && body.message.includes("帳號尚未驗證")) {
            resendSection.style.display = "block";
            resendEmail.value = "";
          } else {
            resendSection.style.display = "none";
            if (body.message && body.message.includes("使用者名稱或密碼錯誤")) {
              if (confirm("查無此會員，是否前往註冊？")) {
                window.location.href = `${getContextPath()}/user/member/register.html`;
                return;
              }
            }
            Swal.fire({
              icon: "error",
              title: "登入失敗",
              text: body.message || "登入失敗",
            });
          }
        }
      })
      .catch(() => {
        Swal.fire({
          icon: "error",
          title: "登入請求失敗",
          text: "登入請求失敗，請稍後再試",
        });
      });
  });
}
