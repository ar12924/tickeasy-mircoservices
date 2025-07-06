// ==================== 會員頁面通用初始化 ====================
import { getContextPath } from "../../common/utils.js";
import {
  fetchNavTemplate,
  renderNav,
  initNavJSEvents,
} from "../../layout/nav/nav.js";
import {
  fetchFooterTemplate,
  renderFooter,
} from "../../layout/footer/footer.js";

// ==================== 會員頁面初始化 ====================
(async () => {
  // ====== nav 部分 ======
  const navTemplate = await fetchNavTemplate();
  await renderNav(navTemplate);
  initNavJSEvents();

  // ====== footer 部分 ======
  const footerTemplate = await fetchFooterTemplate();
  renderFooter(footerTemplate);
})();
