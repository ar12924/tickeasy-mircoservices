document.querySelector('button').addEventListener('click', () => {
	const userName = document.querySelector('#userName').value.trim();
	const nickName = document.querySelector('#nickName').value.trim();
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
	const photoFile = document.querySelector('#photo').files[0];

	msg.textContent = '';
	msg.style.color = 'red';

	if (userName.length < 5 || userName.length > 50) {
		msg.textContent = '帳號長度須介於 5~50 字元';
		return;
	}

	if (password.length < 6) {
		msg.textContent = '密碼長度須至少 6 字元';
		return;
	}

	if (password !== rePassword) {
		msg.textContent = '兩次密碼不一致';
		return;
	}

	if (!/^[A-Za-z]/.test(idCard)) {
		msg.textContent = '身分證號開頭需為英文字母';
		return;
	}

	if (!/^\d{8}$/.test(unicode)) {
		msg.textContent = '統一編號需為 8 碼數字';
		return;
	}


	if (email && !/^[\w.-]+@[\w.-]+\.[A-Za-z]{2,6}$/.test(email)) {
		msg.textContent = '電子郵件格式錯誤';
		return;
	}

	if (!/^(09)\d{8}$/.test(phone)) {
		msg.textContent = '手機號碼格式錯誤';
		return;
	}

	if (!birthDate) {
		msg.textContent = '請選擇出生日期';
		return;
	}

	if (!(gender === 'M' || gender === 'F')) {
		msg.textContent = '請選擇性別';
		return;
	}

	if (!agree) {
		msg.textContent = '請同意服務條款';
		return;
	}

	if (photoFile) {
		const reader = new FileReader();
		reader.onload = () => {
			// reader.result is "data:<mime>;base64,AAAA…"
			const base64 = reader.result.split(',', 2)[1];
			sendRegisterJSON(base64);
		};
		reader.onerror = () => {
			console.error('讀取大頭貼失敗', reader.error);
			msg.textContent = '無法讀取大頭貼，請重新選擇';
		};
		reader.readAsDataURL(photoFile);

	} else {
		// 沒選檔就傳 null
		sendRegisterJSON(null);
	}

	function sendRegisterJSON(photoBase64) {
		const payload = {
			userName, nickName, password, email, phone,
			birthDate, gender, idCard, unicode, agree, hostApply,
			// 將 Base64 字串傳給後端
			photo: photoBase64
		};


		fetch('register', {
			method: 'POST',
			headers: { 'Content-Type': 'application/json' },
			body: JSON.stringify(payload)
		})

			.then(resp => resp.json())
			.then(body => {
				console.log("後端回傳：", body);  //測試用
				const { successful, message } = body;
				msg.style.color = successful ? 'blue' : 'red';
				msg.textContent = message || (successful ? '註冊成功' : '註冊失敗');

				if (successful) {
					setTimeout(() => location.href = 'login.html', 1500);
				}
			})
	};
})
