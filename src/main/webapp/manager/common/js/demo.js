/**
 * AdminLTE Demo
 *
 * This file contains demo functionality for AdminLTE
 */

$(function () {
  "use strict";

  // 初始化所有 AdminLTE 組件
  if (typeof $.fn.layout !== "undefined") {
    $("body").layout("fixLayoutHeight");
  }

  // 初始化卡片小工具
  if (typeof CardWidget !== "undefined") {
    // 卡片小工具已經在 adminlte.js 中初始化
  }

  // 初始化樹狀視圖
  if (typeof $.fn.treeview !== "undefined") {
    $('[data-widget="treeview"]').treeview("init");
  }

  // 初始化推送選單
  if (typeof $.fn.pushMenu !== "undefined") {
    $('[data-widget="pushmenu"]').pushMenu();
  }

  // 初始化直接聊天
  if (typeof $.fn.directChat !== "undefined") {
    $('[data-widget="chat-pane-toggle"]').directChat();
  }

  // 初始化全螢幕功能
  if (typeof $.fn.fullScreen !== "undefined") {
    $('[data-widget="fullscreen"]').fullScreen();
  }

  // 控制台日誌
  console.log("AdminLTE Demo initialized");
});
