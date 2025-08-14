document.addEventListener("DOMContentLoaded", function () {
  htmlNotifyPush();
  createWebSocket();
});

function htmlNotifyPush() {
  document.querySelector("main").insertAdjacentHTML(
    "beforeend",
    `	<div id="notification_box" class="notification_box">
		       		 	<p id="notification_title">通知內容</p>
				        <p id="notification_text">這是通知內容</p>
				        <button onclick="hideNotification()">關閉</button>
				    </div> `
  );
}

let notificationQueue;
// 顯示通知函數
function showNotification(content) {
  var notificationBox = document.getElementById("notification_box");
  var notificationTitle = document.getElementById("notification_title");
  var notificationText = document.getElementById("notification_text");

  // 設置通知內容
  notificationTitle.textContent = "您有新的通知:";
  notificationText.textContent = content;

  // 顯示通知，重置動畫
  notificationBox.classList.remove("show"); // 先移除顯示樣式
  void notificationBox.offsetWidth; // 強制重繪（觸發動畫重啟）
  notificationBox.classList.add("show"); // 重新加上顯示樣式

  // 設置幾秒鐘後自動隱藏通知
  setTimeout(hideNotification, 3000); // 5秒後隱藏

  // 在通知顯示後，檢查隊列中是否有等待顯示的通知
  setTimeout(checkQueue, 3000);
}

// 隱藏通知函數
function hideNotification() {
  var notificationBox = document.getElementById("notification_box");
  notificationBox.classList.remove("show");
}

// 檢查是否有等待顯示的通知
function checkQueue() {
  console.log("Checking queue... Current length:", notificationQueue.length);
  console.log("Queue contents:", notificationQueue); // 查看隊列內容
  if (notificationQueue.length > 0) {
    console.log("notificationQueue");
    // 取出隊列中的第一條通知並顯示
    var nextNotification = notificationQueue.shift();
    showNotification(nextNotification.content);
  } else {
    console.log("<0");
  }
}

function createWebSocket() {
  notificationQueue = [];
  var memberId = sessionStorage.getItem("memberId"); // 使用者的 memberId
  var socket = new WebSocket(
    (location.protocol === "https:" ? "wss://" : "ws://") +
      location.host +
      "/ws?memberId=" +
      memberId
  );

  // 監聽 WebSocket 連接成功事件
  socket.addEventListener("open", (e) => {
    console.log("WebSocket 連接已建立！"); // 可以在控制台中打印這條信息確認連接成功
    // 可以在這裡執行其他邏輯，比如發送消息到後端等
  });

  socket.addEventListener("message", (e) => {
    var message = e.data;
    console.log(message);
    // 如果隊列中沒有通知，直接顯示，否則將其加入隊列
    const notificationBox = document.getElementById("notification_box");
    if (
      notificationBox &&
      !document.getElementById("notification_box").classList.contains("show")
    ) {
      showNotification(message);
    } else {
      // 把新通知加入隊列
      notificationQueue.push({ content: message });
    }
  });
  // 當 WebSocket 連接錯誤時
  socket.addEventListener("error", function (e) {
    console.error("WebSocket 發生錯誤:", e);
  });

  // 當 WebSocket 連接關閉時
  socket.addEventListener("close", function (e) {
    console.log("WebSocket 連接已關閉");
    // 這裡可以執行一些清理工作，或者根據需求重連
    reconnectWebSocket(); // 重連邏輯
  });
}
// 重連 WebSocket 連接
function reconnectWebSocket() {
  var reconnectTimeout;
  // 如果已有重連操作，取消之前的重試
  if (reconnectTimeout) {
    clearTimeout(reconnectTimeout);
  }

  // 5秒後嘗試重新建立 WebSocket 連接
  reconnectTimeout = setTimeout(function () {
    console.log("正在重試 WebSocket 連接...");
    createWebSocket(); // 重新創建 WebSocket 連接
  }, 5000); // 5秒後重試
}
