const form = document.querySelector('#registerForm');
const username = document.querySelector('#userName');
const nickname = document.querySelector('#nickName');
const photo = document.querySelector('#photo');
const email = document.querySelector('#email');
const password = document.querySelector('#password');
const cPassword = document.querySelector('#rePassword');
const birthDate = document.querySelector('#birthDate');
const phone = document.querySelector('#phone');
const gender = document.querySelector('#gender');
const idCard = document.querySelector('#idCard');
const unicode = document.querySelector('#unicode');
const agree = document.querySelector('#agree');
const hostApply = document.querySelector('#hostApply');
const msg = document.querySelector('#msg');


form.addEventListener('submit', async e => {
	e.preventDefault();
	msg.textContent = '';
	msg.style.color = 'red';

	const u = username.value.trim();
	if (u.length < 5 || u.length > 50) {
		msg.textContent = '使用者名稱長度須介於5~50字元';
		return;
	}
	const pw = password.value;
	if (pw.length < 6 || pw.length > 12) {
		msg.textContent = '密碼長度須介於6~12字元';
		return;
	}
	if (pw !== cPassword.value) {
		msg.textContent = '密碼與確認密碼不符合';
		return;
	}
	const nn = nickname.value.trim();
	if (nn.length < 1 || nn.length > 20) {
		msg.textContent = '暱稱長度須介於1~20字元';
		return;
	}
	const em = email.value.trim();
	if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(em)) {
		msg.textContent = '電子郵件格式錯誤';
		return;
	}
	const ph = phone.value.trim();
	if (!/^09\d{8}$/.test(ph)) {
		msg.textContent = '手機格式錯誤，需 09 開頭共 10 碼';
		return;
	}
	const bd = birthDate.value.trim();
	if (!/^\d{4}-\d{2}-\d{2}$/.test(bd)) {
		msg.textContent = '出生日期格式錯誤，請輸入 YYYY-MM-DD';
		return;
	}
	const gen = gender.value;
	if (!gen) {
		msg.textContent = '請選擇性別';
		return;
	}
	const idv = idCard.value.trim();
	if (!/^[A-Za-z]\d{9}$/.test(idv)) {
		msg.textContent = '身分證格式錯誤，開頭英文字母＋9碼數字';
		return;
	}
	const un = unicode.value.trim();
	if (un && !/^\d{8}$/.test(un)) {
		msg.textContent = '統一編號格式錯誤，應為 8 碼數字';
		return;
	}
	if (!agree.checked) {
		msg.textContent = '請先同意服務條款';
		return;
	}

	const payload = {
		userName: u,
		nickName: nn,
		email: em,
		password: pw,
		rePassword: cPassword.value,
		birthDate: bd,
		phone: ph,
		gender: gen,
		idCard: idv,
		unicode: un,
		agree: agree.checked,
		hostApply: hostApply.checked
	};

	const fd = new FormData();
	fd.append('json', JSON.stringify(payload));
	if (photo.files[0]) {
		fd.append('photo', photo.files[0]);
	}
	fetch(form.action, {
		method: 'POST',
		body: fd,            // 不要手動設定 Content-Type！
		credentials: 'include'      // 如需帶 Cookie/Session
	})
		.then(resp => resp.json())
		.then(body => {
			if (body.successful || body.message.startsWith('註冊成功')) {
				msg.style.color = 'red';
				msg.textContent = body.message;
				// 2 秒后跳转到登入页
				setTimeout(() => {
					window.location.href = 'login.html';
				}, 2000);
			} else {
				msg.textContent = body.message;
			}
		})
		.catch(err => {
			console.error(err);
			msg.textContent = '伺服器錯誤，請稍後再試';
		});
});