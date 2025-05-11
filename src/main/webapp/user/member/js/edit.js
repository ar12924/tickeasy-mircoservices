const loggedInNickname = document.querySelector('#loggedInNickname');
const logoutButton = document.querySelector('#logoutButton');
const username = document.querySelector('#username');
const password = document.querySelector('#password');
const cPassword = document.querySelector('#cPassword');
const nickname = document.querySelector('#nickname');
const email = document.querySelector('#email');
const unicode = document.querySelector('#unicode');
const saveButton = document.querySelector('#saveButton');
const msg = document.querySelector('#msg');

// 顯示登入者暱稱
loggedInNickname.textContent = sessionStorage.getItem('loggedInNickname');

// 載入會員資訊
fetch('/user/member/find')
  .then(resp => resp.json())
  .then(body => {
    if (body.successful) {
      username.value = body.userName || '';
      nickname.value = body.nickname || '';
      email.value = body.email || '';
      unicode.value = body.unicode || '';
    } else {
      alert(body.message || '請先登入');
      location.href = 'login.html';
    }
  });

saveButton.addEventListener('click', () => {
  msg.textContent = '';
  msg.style.color = 'red';

  const pw = password.value.trim();
  const cpw = cPassword.value.trim();
  const nick = nickname.value.trim();
  const em = email.value.trim();
  const uni = unicode.value.trim();

  if (pw && pw.length < 6) {
    msg.textContent = '密碼長度須大於 6 字元';
    return;
  }

  if (pw !== cpw) {
    msg.textContent = '密碼與確認密碼不一致';
    return;
  }

  if (nick.length < 1 || nick.length > 20) {
    msg.textContent = '暱稱長度須介於 1～20 字元';
    return;
  }

  if (uni && !/^\d{8}$/.test(uni)) {
    msg.textContent = '統一編號格式錯誤，應為 8 碼數字';
    return;
  }

  if (em && !/^[\w.-]+@[\w.-]+\.[A-Za-z]{2,6}$/.test(em)) {
    msg.textContent = '電子郵件格式錯誤';
    return;
  }

  fetch('/user/member/edit', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      password: pw,
      nickname: nick,
      email: em,
      unicode: uni
    })
  })
    .then(resp => resp.json())
    .then(body => {
      if (body.successful) {
        msg.style.color = 'blue';
        msg.innerHTML = `✅ 修改成功<br>使用者名稱：${body.userName}<br>暱稱：${body.nickname}`;
        sessionStorage.setItem('loggedInNickname', body.nickname);
      } else {
        msg.textContent = body.message || '更新失敗';
      }
    });
});

logoutButton.addEventListener('click', () => {
  if (confirm('是否登出？')) {
    fetch('/user/member/logout')
      .then(resp => resp.json())
      .then(body => {
        if (body.successful) {
          sessionStorage.removeItem('loggedInNickname');
          location.href = 'login.html';
        } else {
          alert(body.message || '登出失敗');
        }
      });
  }
});
