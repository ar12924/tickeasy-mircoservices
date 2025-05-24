const username = document.querySelector('#username');
const password = document.querySelector('#password');
const remember = document.querySelector('#rememberMe'); // 「記住我」checkbox
const msg = document.querySelector('#msg');
const loginBtn = document.querySelector('#loginBtn');

const saved = localStorage.getItem('savedUsername');
if (saved) {
	username.value = saved;
	if (remember) remember.checked = true;
}


loginBtn.addEventListener('click', () => {
	msg.textContent = '';
	msg.style.color = 'red';

	if (!username.value.trim() || !password.value) {
		msg.textContent = '請輸入帳號與密碼';
		return;
	}

	fetch('login', {
		method: 'POST',
		headers: { 'Content-Type': 'application/json' },
		credentials: "include",
		body: JSON.stringify({
			userName: username.value.trim(),
			password: password.value,
			rememberMe: remember ? remember.checked : false
		})
	})
		.then(resp => resp.json())
		.then(body => {
			if (body.successful) {
				msg.style.color = 'green';
				sessionStorage.setItem("loggedInNickname", body.data.nickName);
				if (remember && remember.checked) {
					localStorage.setItem('savedUsername', username.value.trim());
				} else {
					localStorage.removeItem('savedUsername');
				}
				window.location.href = 'result.html';
			} else {
				alert(body.message || "登入失敗");
			}
		});
});
