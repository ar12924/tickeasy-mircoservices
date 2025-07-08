import { fetchNavTemplate, renderNav } from "../../layout/nav/nav.js";
import {
  fetchFooterTemplate,
  renderFooter,
} from "../../layout/footer/footer.js";
(async () => {
  // ====== 資料儲存變數區 ======
  //   const recentEvent = await fetchRecentEventInfo();
  //   console.log(recentEvent); // ok!!!

  // ====== nav 部分 ======
  const navTemplate = await fetchNavTemplate();
  renderNav(navTemplate);
  initNavJSEvents();

  // ====== footer 部分 ======
  const footerTemplate = await fetchFooterTemplate();
  renderFooter(footerTemplate);
})();
