const loggedInNickname = document.querySelector('#loggedInNickname');
const logoutButton = document.querySelector('#logoutButton');
const saveButton = document.querySelector('#saveButton');
const username = document.querySelector('#username');
const form = document.querySelector('#editForm');
const password = document.querySelector('#password');
const cPassword = document.querySelector('#cPassword');
const nickname = document.querySelector('#nickname');
const email = document.querySelector('#email');
const unicode = document.querySelector('#unicode');
const msg = document.querySelector('#msg');
const photoPreview = document.querySelector('#photoPreview');
const photoInput = document.querySelector('#photoInput');
const changePasswordBtn = document.querySelector('#changePasswordBtn');
const passwordChangeArea = document.querySelector('#passwordChangeArea');
const sendVerificationBtn = document.querySelector('#sendVerificationBtn');
const verificationMsg = document.querySelector('#verificationMsg');

// 顯示登入者暱稱
loggedInNickname.textContent = sessionStorage.getItem('loggedInNickname') || '訪客';

// 載入會員資訊
fetch('find', { credentials: 'include' })
	.then(resp => resp.json())
	.then(body => {
		if (!body.successful) {
			msg.textContent = '尚未登入，請先登入';
			msg.innerHTML += '<br><a href="login.html">登入頁面</a>';
			return;
		}
		const data = body.data;
		username.value = data.userName || '';
		nickname.value = data.nickName || '';
		email.value = data.email || '';
		unicode.value = data.unicode || '';
		if (data.photo) {
			photoPreview.src = 'data:image/jpeg;base64,' + data.photo;
			photoPreview.style.display = 'block';
			defaultIcon.style.display = 'none';
		} else {
			photoPreview.style.display = 'none';
			defaultIcon.style.display = 'inline-block';;
		}
	})
	.catch(err => {
		console.error(err);
		msg.textContent = '載入會員資料失敗';
		msg.innerHTML += '<br><a href="login.html">重新登入</a>';
	});

// 圖片即時預覽
photoInput.addEventListener('change', e => {
	const file = e.target.files[0];
	if (!file) return;
	const reader = new FileReader();
	reader.onload = () => photoPreview.src = reader.result;
	reader.readAsDataURL(file);
});

// 儲存變更
changePasswordBtn.addEventListener('click', () => {
	if (passwordChangeArea.style.display === 'none') {
		passwordChangeArea.style.display = 'block';
		changePasswordBtn.textContent = '取消變更密碼';
	} else {
		passwordChangeArea.style.display = 'none';
		changePasswordBtn.textContent = '變更密碼';
		// 清空密碼欄位
		password.value = '';
		cPassword.value = '';
		verificationMsg.textContent = '';
	}
});

const pw = password.value.trim();
const cpw = cPassword.value.trim();
const nick = nickname.value.trim();
const em = email.value.trim();
const uni = unicode.value.trim();
const fd = new FormData(); // 建立 FormData，含檔案上傳

sendVerificationBtn.addEventListener('click', () => {
	if (!pw) {
		verificationMsg.textContent = '請輸入新密碼';
		verificationMsg.style.color = 'red';
		return;
	}
	if (pw.length < 6) {
		verificationMsg.textContent = '密碼長度須大於等於 6 字元';
		verificationMsg.style.color = 'red';
		return;
	}

	if (pw !== cpw) {
		verificationMsg.textContent = '密碼與確認密碼不一致';
		verificationMsg.style.color = 'red';
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

if (nick) fd.append('nickName', nick);
if (em) fd.append('email', em);
if (uni) fd.append('unicode', uni);
if (photoInput.files.length > 0) {
	fd.append('photo', photoInput.files[0]);
}

fetch('edit', {
	method: 'POST',
	credentials: 'include',
	body: fd
})
	.then(resp => resp.json())
	.then(body => {
		if (body.successful) {
			msg.style.color = 'blue';
			msg.innerHTML = `修改成功<br>
                           使用者名稱：${body.userName}<br>
                           暱稱：${body.nickName}`;
			sessionStorage.setItem('loggedInNickname', body.nickName);
		} else {
			msg.textContent = body.message || '更新失敗';
		}
	});
});

// 登出
logoutButton.addEventListener('click', () => {
	if (!confirm('是否登出？')) return;
	fetch('logout', { credentials: 'include' })
		.then(resp => resp.json())
		.then(body => {
			if (body.successful) {
				sessionStorage.removeItem('loggedInNickname');
				location.href = 'login.html';
			} else {
				alert(body.message || '登出失敗');
			}
		});
});