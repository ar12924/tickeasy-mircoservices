  const userName = document.querySelector('#userName').value.trim();
  const password = document.querySelector('#password').value.trim();
  const rePassword = document.querySelector('#rePassword').value.trim();
  const email = document.querySelector('#email').value.trim();
  const birthDate = document.querySelector('#birthDate').value;
  const phone = document.querySelector('#phone').value.trim();
  const gender = document.querySelector('#gender').value;
  const unicode = document.querySelector('#unicode').value.trim();
  const idCard = document.querySelector('#idCard').value.trim();
  const agree = document.querySelector('#agree').checked;
  const msg = document.querySelector('#msg');
  
  document.querySelector('button').addEventListener('click', () => {
    msg.textContent = '';
    msg.style.color = 'red';

    let len = userName.value.length;
    if (len < 5 || len > 50) {
      msg.textContent = '帳號長度須介於 5~50 字元';
      return;
    }

    len = password.value.length;
    if (len < 6) {
      msg.textContent = '密碼長度須至少 6 字元';
      return;
    }

    if (password.value !== rePassword.value) {
      msg.textContent = '兩次密碼不一致';
      return;
    }

    if (!/^\d{8}$/.test(unicode.value)) {
      msg.textContent = '統一編號需為 8 碼數字';
      return;
    }

    if (!/^[A-Za-z]/.test(idCard.value)) {
      msg.textContent = '身分證號開頭需為英文字母';
      return;
    }

    if (email.value && !/^[\w.-]+@[\w.-]+\.[A-Za-z]{2,6}$/.test(email.value)) {
      msg.textContent = '電子郵件格式錯誤';
      return;
    }

    if (!/^(09)\d{8}$/.test(phone.value)) {
      msg.textContent = '手機號碼格式錯誤';
      return;
    }

    if (!birthDate.value) {
      msg.textContent = '請選擇出生日期';
      return;
    }

    if (!(gender.value === 'M' || gender.value === 'F')) {
      msg.textContent = '請選擇性別';
      return;
    }

    if (!agree.checked) {
      msg.textContent = '請同意服務條款';
      return;
    }

    fetch('/user/member/register', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        userName: userName.value,
        password: password.value,
        email: email.value,
        phone: phone.value,
        birthDate: birthDate.value,
        gender: gender.value,
        idCard: idCard.value,
        unicode: unicode.value
      })
    })
      .then(resp => resp.json())
      .then(body => {
        const { successful, message } = body;
        msg.style.color = successful ? 'blue' : 'red';
        msg.textContent = message || (successful ? '註冊成功' : '註冊失敗');

        if (successful) {
          setTimeout(() => location.href = 'login.html', 1500);
        }
      });
  });