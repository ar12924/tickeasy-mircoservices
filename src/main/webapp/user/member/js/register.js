import { validateIdCard, getContextPath } from "../../common/utils.js";

const form = document.querySelector("#registerForm");
const username = document.querySelector("#userName");
const nickname = document.querySelector("#nickName");
const email = document.querySelector("#email");
const domainSelect = document.querySelector("#domainSelect");
const customDomain = document.querySelector("#customDomain");
const photoInput = document.querySelector("#photo");
const photoPreview = document.querySelector("#photoPreview");
const defaultIcon = document.querySelector("#defaultIcon");
const password = document.querySelector("#password");
const cPassword = document.querySelector("#rePassword");
const birthDate = document.querySelector("#birthDate");
const phone = document.querySelector("#phone");
const gender = document.querySelector("#gender");
const idCard = document.querySelector("#idCard");
const unicode = document.querySelector("#unicode");
const agree = document.querySelector("#agree");
const hostApply = document.querySelector("#hostApply");
const msg = document.querySelector("#msg");

// 錯誤訊息元素
const userNameError = document.querySelector("#userNameError");
const nickNameError = document.querySelector("#nickNameError");
const emailError = document.querySelector("#emailError");
const passwordError = document.querySelector("#passwordError");
const rePasswordError = document.querySelector("#rePasswordError");
const phoneError = document.querySelector("#phoneError");
const idCardError = document.querySelector("#idCardError");
const unicodeError = document.querySelector("#unicodeError");
const agreeError = document.querySelector("#agreeError");

// 顯示／清除錯誤
function showError(el, errorEl, text) {
  errorEl.textContent = text;
  el.classList.add("error");
}
function clearError(el, errorEl) {
  errorEl.textContent = "";
  el.classList.remove("error");
}

// === 即時驗證 ===
// 使用者名稱
username.addEventListener("input", function () {
  clearError(username, userNameError);
  const len = username.value.trim().length;
  if (len < 5 || len > 50) {
    showError(username, userNameError, "使用者名稱長度須介於5~50字元");
  }
});

// 暱稱
nickname.addEventListener("input", function () {
  clearError(nickname, nickNameError);
  const len = nickname.value.trim().length;
  if (len < 1 || len > 20) {
    showError(nickname, nickNameError, "暱稱長度須介於1~20字元");
  }
});

// Email
function validateEmail() {
  clearError(email, emailError);
  let e = email.value.trim();
  if (!e.includes("@")) {
    if (domainSelect.value !== "other") e += domainSelect.value;
    else if (customDomain.value.trim()) {
      let cd = customDomain.value.trim();
      if (!cd.startsWith("@")) cd = "@" + cd;
      e += cd;
    }
  }
  const re = /^[\w.-]+@[\w.-]+\.[A-Za-z]{2,6}$/;
  if (!re.test(e)) showError(email, emailError, "電子信箱格式錯誤");
}
email.addEventListener("input", validateEmail);
domainSelect.addEventListener("change", validateEmail);
customDomain.addEventListener("input", validateEmail);

// 密碼

function validateConfirmPassword() {
  clearError(cPassword, rePasswordError);

  const pwd = password.value;
  const rep = cPassword.value;
  if (rep !== "" || pwd !== "") {
    if (rep !== pwd) {
      showError(cPassword, rePasswordError, "密碼與確認密碼不一致");
      return false;
    }
  }
  return true;
}

cPassword.addEventListener("input", validateConfirmPassword);
password.addEventListener("input", function () {
  clearError(password, passwordError);
  clearError(cPassword, rePasswordError);

  const len = password.value.length;
  if (len < 6 || len > 12) {
    showError(password, passwordError, "密碼長度須介於6~12字元");
  }
});

// 電話
phone.addEventListener("input", function () {
  clearError(phone, phoneError);
  if (!/^09\d{8}$/.test(phone.value.trim())) {
    showError(phone, phoneError, "手機格式錯誤，需 09 開頭共 10 碼");
  }
});

// 身分證 - 使用 utils.js 的 validateIdCard 函數
idCard.addEventListener("input", function () {
  clearError(idCard, idCardError);
  if (!validateIdCard(idCard.value.trim())) {
    showError(idCard, idCardError, "身分證格式錯誤，開頭英文字母＋9碼數字");
  }
});

// 統一編號
unicode.addEventListener("input", function () {
  clearError(unicode, unicodeError);
  const v = unicode.value.trim();
  if (v !== "" && !/^\d{8}$/.test(v)) {
    showError(unicode, unicodeError, "統一編號格式錯誤，應為8碼數字");
  }
});

// 同意條款
agree.addEventListener("change", function () {
  clearError(agree, agreeError);
  if (!agree.checked) {
    showError(agree, agreeError, "請先同意服務條款");
  }
});

// domainSelect 切換顯示 customDomain
domainSelect.addEventListener("change", function () {
  const isOther = domainSelect.value === "other";
  customDomain.style.display = isOther ? "inline-block" : "none";
  customDomain.required = isOther;
  if (!isOther) customDomain.value = "";
});

// 圖片預覽
photoInput.addEventListener("change", function (e) {
  const file = e.target.files[0];
  if (!file) return;
  const reader = new FileReader();
  reader.onload = function () {
    photoPreview.src = reader.result;
    photoPreview.style.display = "block";
    defaultIcon.style.display = "none";
  };
  reader.readAsDataURL(file);
});

form.addEventListener("submit", function (e) {
  e.preventDefault();
  msg.textContent = "";
  // 逐一再執行一次各欄位檢驗
  validateEmail();
  username.dispatchEvent(new Event("input"));
  nickname.dispatchEvent(new Event("input"));
  password.dispatchEvent(new Event("input"));
  phone.dispatchEvent(new Event("input"));
  idCard.dispatchEvent(new Event("input"));

  if (unicode.value.trim()) {
    unicode.dispatchEvent(new Event("input"));
  } else {
    clearError(unicode, unicodeError);
  }

  agree.dispatchEvent(new Event("change"));

  const isConfirmValid = validateConfirmPassword();
  if (!isConfirmValid) {
    return;
  }

  const invalidFields = document.querySelectorAll(
    "input.error, select.error, textarea.error"
  );
  if (invalidFields.length > 0) {
    return;
  }

  // 組 Email
  let fullEmail = email.value.trim();
  if (!fullEmail.includes("@")) {
    if (domainSelect.value !== "other") fullEmail += domainSelect.value;
    else {
      let cd = customDomain.value.trim();
      if (!cd.startsWith("@")) cd = "@" + cd;
      fullEmail += cd;
    }
  }

  const fd = new FormData();
  fd.append("userName", username.value);
  fd.append("nickName", nickname.value);
  fd.append("email", fullEmail);
  fd.append("password", password.value);
  fd.append("rePassword", cPassword.value);
  fd.append("birthDate", birthDate.value);
  fd.append("phone", phone.value);
  fd.append("gender", gender.value);
  fd.append("idCard", idCard.value);
  if (unicode.value.trim()) {
    fd.append("unicode", unicode.value);
  }
  fd.append("agree", agree.checked.toString());
  fd.append("hostApply", hostApply.checked.toString());
  if (photoInput.files[0]) fd.append("photo", photoInput.files[0]);

  fetch(`${getContextPath()}/user/member/register`, {
    method: "POST",
    body: fd,
    credentials: "include",
  })
    .then((resp) => resp.json())
    .then((body) => {
      msg.style.color = body.successful ? "green" : "red";
      if (body.successful) {
        msg.textContent = "註冊成功，請至註冊信箱收取認證信";
        setTimeout(() => (window.location.href = "login.html"), 3000);
      } else {
        msg.textContent = body.message;
      }
    })
    .catch((err) => {
      console.error(err);
      msg.style.color = "red";
      msg.textContent = "伺服器錯誤，請稍後再試";
    });
});
