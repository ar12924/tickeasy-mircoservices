const form = document.querySelector("#registerForm");
const username = document.querySelector("#userName");
const nickname = document.querySelector("#nickName");
const photo = document.querySelector("#photo");
const email = document.querySelector("#email");
const domainSelect = document.querySelector("#domainSelect");
const customDomain = document.querySelector("#customDomain");
const password = document.querySelector("#password");
const cPassword = document.querySelector("#rePassword");
const birthDate = document.querySelector("#birthDate");
const phone = document.querySelector("#phone");
const gender = document.querySelector("#gender");
const idCard = document.querySelector("#idCard");
const unicode = document.querySelector("#unicode");
const agree = document.querySelector("#agree");
const photoPreview = document.querySelector("#photoPreview");
const defaultIcon = document.querySelector("#defaultIcon");
const hostApply = document.querySelector("#hostApply");
const msg = document.querySelector("#msg");

const errorElements = {
  userName: document.querySelector("#userNameError"),
  nickName: document.querySelector("#nickNameError"),
  email: document.querySelector("#emailError"),
  password: document.querySelector("#passwordError"),
  rePassword: document.querySelector("#rePasswordError"),
  phone: document.querySelector("#phoneError"),
  idCard: document.querySelector("#idCardError"),
  unicode: document.querySelector("#unicodeError"),
  agree: document.querySelector("#agreeError"),
};

const validations = [
  {
    field: username,
    errorEl: errorElements.userName,
    validate: (value) => value.trim().length >= 5 && value.trim().length <= 50,
    errorMsg: "使用者名稱長度須介於5~50字元",
  },
  {
    field: nickname,
    errorEl: errorElements.nickName,
    validate: (value) => value.trim().length >= 1 && value.trim().length <= 20,
    errorMsg: "暱稱長度須介於1~20字元",
  },
  {
    field: email,
    errorEl: errorElements.email,
    validate: (value) => {
      let tempEmail = value.trim();
      if (domainSelect.value !== "other") {
        tempEmail += domainSelect.value;
      } else if (customDomain.value) {
        tempEmail += customDomain.value;
      } else {
        return false;
      }
      return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(tempEmail);
    },
    errorMsg: "電子信箱格式錯誤",
  },
  {
    field: password,
    errorEl: errorElements.password,
    validate: (value) => value.length >= 6 && value.length <= 12,
    errorMsg: "密碼長度須介於6~12字元",
  },
  {
    field: cPassword,
    errorEl: errorElements.rePassword,
    validate: (value) => value === password.value,
    errorMsg: "密碼與確認密碼不符合",
  },
  {
    field: phone,
    errorEl: errorElements.phone,
    validate: (value) => /^09\d{8}$/.test(value.trim()),
    errorMsg: "手機格式錯誤，需 09 開頭共 10 碼",
  },
  {
    field: idCard,
    errorEl: errorElements.idCard,
    validate: (value) => /^[A-Za-z]\d{9}$/.test(value.trim()),
    errorMsg: "身分證格式錯誤，開頭英文字母＋9碼數字",
  },
  {
    field: unicode,
    errorEl: errorElements.unicode,
    validate: (value) => value.trim() === "" || /^\d{8}$/.test(value.trim()),
    errorMsg: "統一編號格式錯誤，應為 8 碼數字",
  },
  {
    field: agree,
    errorEl: errorElements.agree,
    validate: (value) => value,
    errorMsg: "請先同意服務條款",
  },
];

validations.forEach((validation) => {
  validation.field.addEventListener("input", () => {
    const value =
      validation.field.type === "checkbox"
        ? validation.field.checked
        : validation.field.value;
    if (validation.validate(value)) {
      validation.errorEl.textContent = "";
      validation.field.classList.remove("error");
    } else {
      validation.errorEl.textContent = validation.errorMsg;
      validation.field.classList.add("error");
    }
  });
});

domainSelect.addEventListener("change", () => {
  if (domainSelect.value === "other") {
    customDomain.style.display = "inline-block";
    customDomain.required = true;
  } else {
    customDomain.style.display = "none";
    customDomain.required = false;
    customDomain.value = "";
  }
});

function combine() {
  if (domainSelect.value === "other") {
    if (!customDomain.value) {
      errorElements.email.textContent = "請輸入電子信箱";
      return;
    }
  }

  const event = new Event("input");
  email.dispatchEvent(event);
}

// 圖片即時預覽
photo.addEventListener("change", (e) => {
  const file = e.target.files[0];
  if (!file) return;

  const reader = new FileReader();
  reader.onload = () => {
    photoPreview.src = reader.result;
    photoPreview.style.display = "block";
    defaultIcon.style.display = "none";
  };
  reader.readAsDataURL(file);
});

function validateForm() {
  let isValid = true;

  // 清除所有錯誤訊息
  msg.textContent = "";
  validations.forEach((validation) => {
    validation.errorEl.textContent = "";
    validation.field.classList.remove("error");
  });

  // 驗證每個欄位
  validations.forEach((validation) => {
    const value =
      validation.field.type === "checkbox"
        ? validation.field.checked
        : validation.field.value;
    if (!validation.validate(value)) {
      validation.errorEl.textContent = validation.errorMsg;
      validation.field.classList.add("error");
      isValid = false;
    }
  });

  return isValid;
}

form.addEventListener("submit", async (e) => {
  e.preventDefault();
  if (!validateForm()) {
    return;
  }
  let fullEmail = email.value.trim();
  if (domainSelect.value !== "other") {
    fullEmail += domainSelect.value;
  } else if (customDomain.value) {
    fullEmail += customDomain.value;
  }

  const payload = {
    userName: username.value,
    nickName: nickname.value,
    email: fullEmail,
    password: password.value,
    birthDate: birthDate.value,
    phone: phone.value,
    gender: gender.value,
    idCard: idCard.value,
    unicode: unicode.value,
    agree: agree.checked,
    hostApply: hostApply.checked,
  };

  const fd = new FormData();
  fd.append("json", JSON.stringify(payload));
  if (photo.files[0]) {
    fd.append("photo", photo.files[0]);
  }
  fetch(form.action, {
    method: "POST",
    body: fd, // 不要手動設定 Content-Type！
    credentials: "include", // 如需帶 Cookie/Session
  })
    .then((resp) => resp.json())
    .then((body) => {
      if (body.successful || body.message.startsWith("註冊成功")) {
        msg.style.color = "red";
        msg.textContent = body.message;
        setTimeout(() => {
          window.location.href = "login.html";
        }, 2000);
      } else {
        msg.textContent = body.message;
      }
    })
    .catch((err) => {
      console.error(err);
      msg.textContent = "伺服器錯誤，請稍後再試";
    });
});
