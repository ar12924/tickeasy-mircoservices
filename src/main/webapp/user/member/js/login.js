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

  // 僅用 GET 方式
  fetch(`login/${username.value}/${password.value}`)
    .then((resp) => resp.json())
    .then((body) => {
      if (body.successful) {
        msg.style.color = "green";
        const memberData = body.data; // 從 Core<T> 中提取資料
        sessionStorage.setItem("loggedInNickname", memberData.nickName || "");
        sessionStorage.setItem("memberId", memberData.memberId || "");
        sessionStorage.setItem("roleLevel", memberData.roleLevel || "");
        if (remember && remember.checked) {
          localStorage.setItem("savedUsername", username.value.trim());
        } else {
          localStorage.removeItem("savedUsername");
        }
        // 分角色導向前，顯示會員頭像預覽
        const avatar = document.getElementById("avatarPreview");
        if (avatar && memberData.memberId) {
          const memberId = memberData.memberId;
          avatar.src = `/maven-tickeasy-v1/api/member-photos/${memberId}`;
          avatar.style.display = "block";
        }
        // 分角色導向
        const role = memberData.roleLevel;
        if (parseInt(role) === 2 || parseInt(role) === 3) {
          alert("登入成功！您的角色是活動方，即將跳轉到活動儀表板。");
          window.location.href =
            "/maven-tickeasy-v1/manager/eventdetail/dashboard.html";
        } else {
          alert("登入成功！即將跳轉到首頁。");
          window.location.href = "/maven-tickeasy-v1/user/buy/index.html";
        }
      } else {
        // 新增：查無會員時導向註冊
        if (body.message && body.message.includes("使用者名稱或密碼錯誤")) {
          if (confirm("查無此會員，是否前往註冊？")) {
            window.location.href =
              "/maven-tickeasy-v1/user/member/register.html";
            return;
          }
        }
        alert(body.message || "登入失敗");
      }
    })
    .catch((error) => {
      console.error("登入請求失敗:", error);
      alert("登入請求失敗，請稍後再試");
    });
});
