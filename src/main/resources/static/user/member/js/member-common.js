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

(async () => {
  const navTemplate = await fetchNavTemplate();
  await renderNav(navTemplate);
  initNavJSEvents();
  const footerTemplate = await fetchFooterTemplate();
  renderFooter(footerTemplate);
})();
