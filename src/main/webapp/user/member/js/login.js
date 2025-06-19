const username = document.querySelector("#username");
const password = document.querySelector("#password");
const remember = document.querySelector("#rememberMe"); // 「記住我」checkbox
const msg = document.querySelector("#msg");
const loginBtn = document.querySelector("#loginBtn");

const saved = localStorage.getItem("savedUsername");
if (saved) {
  username.value = saved;
  if (remember) remember.checked = true;
}

loginBtn.addEventListener("click", () => {
  msg.textContent = "";
  msg.style.color = "red";

  if (!username.value.trim() || !password.value) {
    msg.textContent = "請輸入帳號與密碼";
    return;
  }

  fetch("/maven-tickeasy-v1/user/member/login", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    credentials: "include",
    body: JSON.stringify({
      userName: username.value.trim(),
      password: password.value,
      rememberMe: remember ? remember.checked : false,
    }),
  })
    .then((resp) => resp.json())
    .then((body) => {
      console.log("登入響應:", body);

      if (body.successful) {
        msg.style.color = "green";
        sessionStorage.setItem("loggedInNickname", body.data.nickName);
        sessionStorage.setItem("memberId", body.data.memberId);
        sessionStorage.setItem("roleLevel", body.data.roleLevel);

        console.log("用戶角色等級:", body.data.roleLevel);

        if (remember && remember.checked) {
          localStorage.setItem("savedUsername", username.value.trim());
        } else {
          localStorage.removeItem("savedUsername");
        }

        // 根據角色跳轉到不同頁面
        if (parseInt(body.data.roleLevel) === 2) {
          console.log("準備跳轉到管理頁面");
          alert("登入成功！您的角色是活動方，即將跳轉到活動儀表板。");
          // 活動方跳轉到儀表板頁面
          window.location.href =
            "/maven-tickeasy-v1/manager/eventdetail/dashboard.html";
        } else {
          console.log("準備跳轉到用戶中心");
          alert("登入成功！您的角色是普通用戶，即將跳轉到用戶中心。");
          // 普通用戶跳轉到用戶中心
          window.location.href = "result.html";
        }
      } else {
        alert(body.message || "登入失敗");
      }
    })
    .catch((error) => {
      console.error("登入請求失敗:", error);
      alert("登入請求失敗，請稍後再試");
    });
});
