<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Login</title>
</head>
<body>
<h2>會員登入</h2>
	<form action="${pageContext.request.contextPath}/user/member/login" method="POST">
		<label for="userName">使用者名稱: </label>
		<input type="text" name="userName" id="userName" required><br>
		<label for="password">密碼: </label>
		<input type="password" name="password" required><br>
		<input type="checkbox" id="rememberMe"> 記住我<br>
		<input type="submit" value="登入">
	</form>
	<div id="msg" style="color: red;">${message}</div>
	 <a href="register.jsp">尚未註冊？點此註冊</a>
</body>

<script>
window.onload = function () {
	const remembered = localStorage.getItem("rememberedUser");
	if (remembered) {
		document.getElementById("userName").value = remembered;
		document.getElementById("rememberMe").checked = true;
	}
};

document.getElementById("loginForm").onsubmit = function () {
	const username = document.getElementById("userName").value;
	const remember = document.getElementById("rememberMe").checked;
	if (remember) {
		localStorage.setItem("rememberedUser", username);
	} else {
		localStorage.removeItem("rememberedUser");
	}
};

</script>
</html>