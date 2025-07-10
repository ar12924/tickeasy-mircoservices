const title = document.querySelector("#verifyTitle");
const msg = document.querySelector("#verifyMsg");

const params = new URLSearchParams(window.location.search);
const token = params.get("token");

if (!token) {
  title.textContent = "驗證失敗";
  msg.textContent = "沒有提供驗證 token。";
} else {
  fetch(`/maven-tickeasy-v1/user/member/verify?token=${token}`)
    .then((res) => res.json())
    .then((data) => {
      if (data.successful) {
        title.textContent = "帳號啟用成功";
        msg.textContent =
          data.message || "您的帳號已成功啟用，即將跳轉到登入頁面。";
        // 延遲 2 秒後跳轉到登入頁面
        setTimeout(() => {
          window.location.href =
            "/maven-tickeasy-v1/user/member/login.html?verified=true";
        }, 2000);
      } else {
        title.textContent = "驗證失敗";
        msg.textContent = data.message || "驗證失敗";
        // 延遲 3 秒後跳轉到登入頁面
        setTimeout(() => {
          window.location.href =
            "/maven-tickeasy-v1/user/member/login.html?verified=false";
        }, 3000);
      }
    })
    .catch(() => {
      title.textContent = "系統錯誤";
      msg.textContent = "請稍後再試或聯絡客服。";
    });
}
