/**
 * Header 功能管理模組
 * 用於統一管理各頁面的 header 功能
 */
(function() {
    'use strict';

    // 預設頭像路徑
    const DEFAULT_AVATAR = "../common/assets/img/user2-128x128.png";

    /**
     * 初始化 Header 功能
     */
    function initHeader() {
        console.log('=== Header 初始化開始 ===');
        updateUserInfo();
        console.log('=== Header 初始化完成 ===');
    }

    /**
     * 更新使用者資訊顯示
     */
    function updateUserInfo() {
        // 從 sessionStorage 讀取使用者資料
        const loggedInNickname = sessionStorage.getItem("loggedInNickname");
        const memberId = sessionStorage.getItem("memberId");

        console.log('讀取 session 資料:', {
            loggedInNickname,
            memberId
        });

        // 更新導航列中的使用者名稱顯示
        const navUserNameSpan = document.querySelector('.app-header .nav-link.dropdown-toggle .d-none.d-md-inline');
        if (navUserNameSpan) {
            navUserNameSpan.textContent = loggedInNickname || 'Guest';
            console.log('更新導航列使用者名稱:', loggedInNickname);
        }

        // 更新下拉選單中的使用者資訊
        const userHeaderP = document.querySelector('.app-header .user-header p');
        if (userHeaderP && loggedInNickname) {
            userHeaderP.innerHTML = `${loggedInNickname}<small>TickEasy 管理平台</small>`;
            console.log('更新下拉選單使用者資訊:', loggedInNickname);
        }

        // 載入使用者頭像
        if (memberId) {
            loadUserAvatar(memberId);
        } else {
            // 沒有 memberId 時使用預設頭像
            setDefaultAvatar();
        }
    }

    /**
     * 載入使用者頭像
     * @param {string} memberId - 會員ID
     */
    function loadUserAvatar(memberId) {
        console.log('開始載入會員頭像, memberId:', memberId);
        
        // 只選擇 header 區域的頭像元素，避免影響頁面內容區域
        const avatarElements = document.querySelectorAll('.app-header .user-image, .app-header .user-header img');
        const avatarUrl = `/maven-tickeasy-v1/api/manager/member/photo/${memberId}`;

        console.log('找到的頭像元素數量:', avatarElements.length);

        // 先設定預設頭像，避免載入期間顯示空白
        setDefaultAvatar();

        // 測試頭像是否存在
        const testImg = new Image();
        
        testImg.onload = function() {
            console.log('會員頭像載入成功');
            // 頭像載入成功，更新所有 header 區域的頭像元素
            avatarElements.forEach((img, index) => {
                img.src = avatarUrl + '?t=' + new Date().getTime();
                img.alt = '會員頭像';
                console.log(`更新第 ${index + 1} 個頭像元素`);
            });
        };
        
        testImg.onerror = function() {
            console.log('會員頭像載入失敗，使用預設頭像');
            // 頭像載入失敗，保持預設頭像
            setDefaultAvatar();
        };
        
        // 開始載入頭像
        testImg.src = avatarUrl + '?timestamp=' + new Date().getTime();
    }

    /**
     * 設定預設頭像
     */
    function setDefaultAvatar() {
        // 只設定 header 區域的預設頭像
        const avatarElements = document.querySelectorAll('.app-header .user-image, .app-header .user-header img');
        avatarElements.forEach((img, index) => {
            img.src = DEFAULT_AVATAR;
            img.alt = '預設頭像';
            console.log(`設定第 ${index + 1} 個預設頭像`);
        });
    }

    /**
     * 處理登出功能
     */
    function handleLogout() {
        if (confirm('確定要登出嗎？')) {
            console.log('開始登出程序');
            
            // 清除 sessionStorage
            sessionStorage.clear();
            console.log('清除 sessionStorage');
            
            // 呼叫登出 API
            fetch('/maven-tickeasy-v1/user/member/logout', {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json'
                }
            })
            .then(response => response.json())
            .then(data => {
                console.log('登出 API 回應:', data);
                if (data.successful) {
                    console.log('登出成功，跳轉到首頁');
                    window.location.href = '/maven-tickeasy-v1/user/buy/index.html';
                } else {
                    console.log('登出 API 失敗，但仍跳轉到首頁');
                    window.location.href = '/maven-tickeasy-v1/user/buy/index.html';
                }
            })
            .catch(error => {
                console.error('登出錯誤:', error);
                console.log('登出出錯，但仍跳轉到首頁');
                window.location.href = '/maven-tickeasy-v1/user/buy/index.html';
            });
        }
    }

    /**
     * DOM 載入完成後初始化
     */
    function initializeHeader() {
        if (document.readyState === 'loading') {
            document.addEventListener('DOMContentLoaded', initHeader);
        } else {
            initHeader();
        }
    }

    // 將函數暴露到全域
    window.initHeader = initHeader;
    window.handleLogout = handleLogout;

    // 自動初始化
    initializeHeader();
})();