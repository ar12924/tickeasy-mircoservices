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

const userNameError = document.querySelector("#userNameError");
const nickNameError = document.querySelector("#nickNameError");
const emailError = document.querySelector("#emailError");
const passwordError = document.querySelector("#passwordError");
const rePasswordError = document.querySelector("#rePasswordError");
const phoneError = document.querySelector("#phoneError");
const idCardError = document.querySelector("#idCardError");
const unicodeError = document.querySelector("#unicodeError");
const agreeError = document.querySelector("#agreeError");

function showError(el, errorEl, text) {
  errorEl.textContent = text;
  el.classList.add("error");
}
function clearError(el, errorEl) {
  errorEl.textContent = "";
  el.classList.remove("error");
}

username.addEventListener("input", function () {
  clearError(username, userNameError);
  const len = username.value.trim().length;
  if (len < 5 || len > 50) {
    showError(username, userNameError, "使用者名稱長度須介於5~50字元");
  }
});

nickname.addEventListener("input", function () {
  clearError(nickname, nickNameError);
  const len = nickname.value.trim().length;
  if (len < 1 || len > 20) {
    showError(nickname, nickNameError, "暱稱長度須介於1~20字元");
  }
});

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

phone.addEventListener("input", function () {
  clearError(phone, phoneError);
  if (!/^09\d{8}$/.test(phone.value.trim())) {
    showError(phone, phoneError, "手機格式錯誤，需 09 開頭共 10 碼");
  }
});

idCard.addEventListener("input", function () {
  clearError(idCard, idCardError);
  if (!validateIdCard(idCard.value.trim())) {
    showError(idCard, idCardError, "身分證格式錯誤，開頭英文字母＋9碼數字");
  }
});

unicode.addEventListener("input", function () {
  clearError(unicode, unicodeError);
  const v = unicode.value.trim();
  if (v !== "" && !/^\d{8}$/.test(v)) {
    showError(unicode, unicodeError, "統一編號格式錯誤，應為8碼數字");
  }
});

agree.addEventListener("change", function () {
  clearError(agree, agreeError);
  if (!agree.checked) {
    showError(agree, agreeError, "請先同意服務條款");
  }
});

domainSelect.addEventListener("change", function () {
  const isOther = domainSelect.value === "other";
  customDomain.style.display = isOther ? "inline-block" : "none";
  customDomain.required = isOther;
  if (!isOther) customDomain.value = "";
});

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
  if (!isConfirmValid) return;
  const invalidFields = document.querySelectorAll(
    "input.error, select.error, textarea.error"
  );
  if (invalidFields.length > 0) return;
  let fullEmail = email.value.trim();
  if (!fullEmail.includes("@")) {
    if (domainSelect.value !== "other") fullEmail += domainSelect.value;
    else {
      let cd = customDomain.value.trim();
      if (!cd.startsWith("@")) cd = "@" + cd;
      fullEmail += cd;
    }
  }
  const member = {
    userName: username.value,
    nickName: nickname.value,
    email: fullEmail,
    password: password.value,
    rePassword: cPassword.value,
    birthDate: birthDate.value,
    phone: phone.value,
    gender: gender.value,
    idCard: idCard.value,
    unicode: unicode.value.trim() || null,
    agree: agree.checked,
    hostApply: hostApply.checked,
  };
  const hasPhoto = !!photoInput.files[0];
  const doRegister = (photoKey) => {
    const payload = { ...member };
    if (photoKey) payload.photoKey = photoKey;
    fetch(`/api/members/register`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload),
    })
      .then((resp) => resp.json())
      .then((body) => {
        msg.style.color = body.successful ? "green" : "red";
        if (body.successful) {
          Swal.fire({
            icon: "success",
            title: "註冊成功",
            text: "請至註冊信箱收取認證信",
            confirmButtonText: "前往登入",
            allowOutsideClick: false,
          }).then(() => {
            window.location.href = "login.html";
          });
        } else {
          msg.textContent = body.message;
        }
      })
      .catch(() => {
        Swal.fire({ icon: "error", title: "伺服器錯誤", text: "請稍後再試" });
      });
  };

  if (!hasPhoto) {
    doRegister(null);
    return;
  }

  const file = photoInput.files[0];
  fetch(`/api/media/presign-upload`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ contentType: file.type }),
  })
    .then((resp) => resp.json())
    .then(async (data) => {
      if (!data || !data.url || !data.key) throw new Error("預簽名失敗");
      const putResp = await fetch(data.url, {
        method: "PUT",
        headers: { "Content-Type": file.type },
        body: file,
      });
      if (!putResp.ok) throw new Error("上傳失敗");
      doRegister(data.key);
    })
    .catch((e) => {
      Swal.fire({
        icon: "error",
        title: "圖片上傳失敗",
        text: e.message || "請稍後再試",
      });
    });
});
