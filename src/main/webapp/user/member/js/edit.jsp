<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>修改會員資料</title>
</head>
<body>
    <h2>修改會員資料</h2>
    <form action="${pageContext.request.contextPath}/user/member/edit" method="post">
        使用者名稱（不可改）：<input type="text" name="userName" value="${sessionScope.member.userName}" readonly><br>
        密碼：<input type="password" name="password"><br>
        電子郵件：<input type="email" name="email" value="${sessionScope.member.email}"><br>
        電話：<input type="text" name="phone" value="${sessionScope.member.phone}"><br>
        出生日期：<input type="date" name="birthDate" value="${sessionScope.member.birthDate}"><br>
        性別：<select name="gender">
            <option value="">請選擇</option>
            <option value="M" ${sessionScope.member.gender == 'M' ? 'selected' : ''}>男</option>
            <option value="F" ${sessionScope.member.gender == 'F' ? 'selected' : ''}>女</option>
        </select><br>
        身分證：<input type="text" name="idCard" value="${sessionScope.member.idCard}"><br>
        統一編號：<input type="text" name="unicode" value="${sessionScope.member.unicode}"><br>
        <button type="submit">送出修改</button>
    </form>
    <div style="color:red">${message}</div>
</body>
</html>