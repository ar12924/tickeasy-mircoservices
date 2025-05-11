const usernameEl = document.querySelector('#username');
const passwordEl = document.querySelector('#password');
const rememberMeEl = document.querySelector('#rememberMe');
const msgEl = document.querySelector('#msg');

// 載入 localStorage
window.onload = () => {
  const remembered = localStorage.getItem("rememberedUser");
  if (remembered) {
    usernameEl.value = remembered;
    rememberMeEl.checked = true;
  }
};

document.querySelector('#loginBtn').addEventListener('click', () => {
  const username = usernameEl.value.trim();
  const password = passwordEl.value;

  if (!username || !password) {
    msgEl.textContent = '請輸入帳號與密碼';
    return;
  }

  fetch('login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ userName: username, password })
  })
    .then(res => res.json())
    .then(data => {
      if (data.successful) {
        if (rememberMeEl.checked) {
          localStorage.setItem("rememberedUser", username);
        } else {
          localStorage.removeItem("rememberedUser");
        }
        window.location.href = "result.html";
      } else {
        msgEl.textContent = data.message || "登入失敗";
      }
    });
});