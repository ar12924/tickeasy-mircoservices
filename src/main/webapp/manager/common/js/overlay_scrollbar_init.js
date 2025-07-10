/**
 * Sidebar OverlayScrollbars 初始化模組
 * 用於統一管理側邊欄滾動條的初始化設定
 */
(function() {
    'use strict';
    
    // 設定常數
    const SELECTOR_SIDEBAR_WRAPPER = ".sidebar-wrapper";
    const Default = {
        scrollbarTheme: "os-theme-light",
        scrollbarAutoHide: "leave",
        scrollbarClickScroll: true,
    };

    /**
     * 初始化側邊欄滾動條
     */
    function initSidebarScrollbars() {
        const sidebarWrapper = document.querySelector(SELECTOR_SIDEBAR_WRAPPER);
        
        if (sidebarWrapper && typeof OverlayScrollbarsGlobal?.OverlayScrollbars !== "undefined") {
            OverlayScrollbarsGlobal.OverlayScrollbars(sidebarWrapper, {
                scrollbars: {
                    theme: Default.scrollbarTheme,
                    autoHide: Default.scrollbarAutoHide,
                    clickScroll: Default.scrollbarClickScroll,
                },
            });
        }
    }

    // DOM 載入完成後自動執行初始化
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', initSidebarScrollbars);
    } else {
        // DOM 已經載入完成，直接執行
        initSidebarScrollbars();
    }

    // 將初始化函數暴露到全域
    window.initSidebarScrollbars = initSidebarScrollbars;
})();