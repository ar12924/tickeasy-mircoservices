/**
 * 轉票專區 Vue 應用 - Composition API 版本
 * 創建日期: 2025-05-27
 */

// ES6 import Vue Composition API
import { createApp, ref, reactive, onMounted, onUpdated, computed } from '../../../common/vendors/vue.esm-browser-3.5.16.js';

// 從共用組件匯入需要的函數
import { renderNav, initNavJSEvents } from '../../layout/nav/nav.js';
import { renderFooter } from '../../layout/footer/footer.js';

// 基礎 API URL
const API_BASE_URL = '/maven-tickeasy-v1/api';

// 初始化應用程序
async function initializeTicketExchangeApp() {
    try {
        // 先確保 DOM 完全就緒
        await new Promise(resolve => {
            if (document.readyState === 'complete') {
                resolve();
            } else {
                window.addEventListener('load', resolve);
            }
        });

        console.log('開始載入共用組件...');

        // 同步載入 nav 和 footer
        await renderNav();
        console.log('導覽列載入完成');

        await renderFooter();
        console.log('頁腳載入完成');

        // 給 DOM 一點時間更新
        await new Promise(resolve => setTimeout(resolve, 50));

        initNavJSEvents();
        console.log('導覽列事件綁定完成');

    } catch (error) {
        console.error('載入共用組件時發生錯誤:', error);
    }

    // 確保共用組件完全載入後再初始化 Vue
    await new Promise(resolve => setTimeout(resolve, 100));

    // 初始化 Vue 應用
    initTicketExchangeVueApp();
}

// Vue 應用初始化函數
function initTicketExchangeVueApp() {
    // 創建 Vue 應用
    const ticketExchangeApp = createApp({
        setup() {
            // ==================== 響應式數據 ====================
            const eventId = ref(null);
            const eventName = ref('');
            const memberId = ref(null);
            const memberNickname = ref(null);
            const isLoggedIn = ref(false);
            const swapPosts = ref([]);
            const userTickets = ref([]);
            const isLoading = ref(true);
            const errorMessage = ref(null);
            const isSubmitting = ref(false);

            // 轉票表單資料
            const swapForm = reactive({
                ticketId: '',
                wantedTicketType: '',
                description: ''
            });

            // ==================== 方法定義 ====================

            // 初始化頁面
            const initPage = async () => {
                isLoading.value = true;
                errorMessage.value = null;

                try {
                    // 檢查登錄狀態
                    await checkLoginStatus();

                    // 確認登入狀態已經設定後，再執行其他操作
                    console.log('登入狀態確認完成，isLoggedIn:', isLoggedIn.value);

                    // 平行執行其他操作
                    const promises = [
                        fetchEventInfo(),
                        fetchSwapPosts()
                    ];

                    // 如果已登入，加入獲取用戶票券
                    if (isLoggedIn.value) {
                        promises.push(fetchUserTickets());
                    }

                    // 等待所有操作完成
                    await Promise.all(promises);
                } catch (err) {
                    console.error('初始化頁面時發生錯誤:', err);
                    errorMessage.value = '頁面載入失敗，請重新整理頁面';
                } finally {
                    isLoading.value = false;
                }
            };

            // 檢查用戶登錄狀態
            const checkLoginStatus = async () => {
                try {
                    console.log('從後端檢查登入狀態...');

                    const response = await fetch(`${API_BASE_URL}/auth/status`, {
                        credentials: 'include'
                    });

                    if (response.ok) {
                        const data = await response.json();
                        console.log('後端登入狀態回應:', data);

                        if (data.success && data.data) {
                            if (data.data.isLoggedIn && data.data.nickname) {
                                isLoggedIn.value = true;
                                memberNickname.value = data.data.nickname;
                                console.log('登入成功:', memberNickname.value);
                            } else {
                                isLoggedIn.value = false;
                                memberNickname.value = null;
                                memberId.value = null;
                                console.log('用戶未登入');
                            }
                        }
                    } else {
                        console.error('檢查登入狀態HTTP錯誤:', response.status);
                        isLoggedIn.value = false;
                        memberNickname.value = null;
                        memberId.value = null;
                    }
                } catch (err) {
                    console.error('檢查登錄狀態時發生錯誤:', err);
                    isLoggedIn.value = false;
                    memberNickname.value = null;
                    memberId.value = null;
                }
            };

            // 獲取活動資訊
            const fetchEventInfo = async () => {
                if (!eventId.value) return;

                try {
                    console.log('獲取活動資訊，eventId:', eventId.value);
                    const response = await fetch(`${API_BASE_URL}/events/${eventId.value}`);

                    if (response.ok) {
                        const data = await response.json();
                        console.log('活動資訊回應:', data);

                        if (data.status === 200 && data.data) {
                            eventName.value = data.data.eventName;
                            console.log('活動名稱:', eventName.value);
                        } else {
                            console.warn('活動資訊格式不正確:', data);
                            eventName.value = '未知活動';
                        }
                    } else {
                        console.error('獲取活動資訊HTTP錯誤:', response.status);
                        eventName.value = '未知活動';
                    }
                } catch (err) {
                    console.error('獲取活動資訊時發生錯誤:', err);
                    eventName.value = '未知活動';
                }
            };

            // 獲取轉票貼文列表
            const fetchSwapPosts = async () => {
                if (!eventId.value) return;

                try {
                    console.log('獲取轉票貼文，eventId:', eventId.value);
                    const response = await fetch(`${API_BASE_URL}/ticket-exchange/posts/event/${eventId.value}`);

                    if (response.ok) {
                        const data = await response.json();
                        console.log('轉票貼文回應:', data);

                        if (data.success && data.data) {
                            swapPosts.value = data.data.map(post => ({
                                ...post,
                                showComments: false,
                                showCommentForm: false,
                                comments: null,
                                commentCount: 0,
                                commentSubmitting: false,
                                commentForm: {
                                    ticketId: '',
                                    description: ''
                                }
                            }));
                            console.log('已載入轉票貼文數量:', swapPosts.value.length);
                        } else {
                            console.warn('轉票貼文回應格式不正確:', data);
                            swapPosts.value = [];
                        }
                    } else {
                        console.error('獲取轉票貼文HTTP錯誤:', response.status);
                        swapPosts.value = [];
                    }
                } catch (err) {
                    console.error('獲取轉票貼文時發生錯誤:', err);
                    swapPosts.value = [];
                }
            };

            // 獲取用戶票券列表
            const fetchUserTickets = async () => {
                if (!isLoggedIn.value) return;

                try {
                    console.log('獲取用戶票券...');

                    const response = await fetch(`${API_BASE_URL}/my-tickets`, {
                        credentials: 'include'
                    });

                    if (response.ok) {
                        const data = await response.json();
                        console.log('用戶票券回應:', data);

                        if (data.success && data.data) {
                            userTickets.value = data.data;
                            console.log('已載入用戶票券數量:', userTickets.value.length);
                        } else {
                            userTickets.value = [];
                        }
                    } else {
                        console.error('獲取用戶票券HTTP錯誤:', response.status);
                        userTickets.value = [];
                    }
                } catch (err) {
                    console.error('獲取用戶票券時發生錯誤:', err);
                    userTickets.value = [];
                }
            };

            // 提交轉票貼文
            const submitSwapPost = async () => {
                if (!isLoggedIn.value) {
                    alert('請先登入才能發表轉票貼文');
                    return;
                }

                if (!validateSwapForm()) {
                    return;
                }

                isSubmitting.value = true;

                try {
                    const requestData = {
                        ticketId: parseInt(swapForm.ticketId),
                        description: `希望交換: ${swapForm.wantedTicketType}\n${swapForm.description}`,
                        eventId: parseInt(eventId.value)
                    };

                    console.log('提交轉票貼文:', requestData);

                    const response = await fetch(`${API_BASE_URL}/ticket-exchange/posts`, {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        credentials: 'include',
                        body: JSON.stringify(requestData)
                    });

                    if (response.ok) {
                        const data = await response.json();
                        console.log('提交轉票貼文回應:', data);

                        if (data.success) {
                            // 重新載入貼文列表
                            await fetchSwapPosts();

                            // 清空表單
                            resetSwapForm();

                            alert('轉票貼文發表成功！');
                        } else {
                            throw new Error(data.userMessage || '發表轉票貼文失敗');
                        }
                    } else {
                        const errorData = await response.json().catch(() => ({}));
                        console.error('提交轉票貼文HTTP錯誤:', response.status, errorData);
                        throw new Error(errorData.userMessage || `HTTP錯誤: ${response.status}`);
                    }
                } catch (err) {
                    console.error('提交轉票貼文時發生錯誤:', err);
                    alert(err.message || '發表失敗，請稍後再試');
                } finally {
                    isSubmitting.value = false;
                }
            };

            // 顯示留言表單
            const showCommentForm = (post) => {
                if (!isLoggedIn.value) {
                    alert('請先登入才能發表留言');
                    return;
                }

                // 重置表單
                post.commentForm = {
                    ticketId: '',
                    description: ''
                };
                post.showCommentForm = true;
            };

            // 隱藏留言表單
            const hideCommentForm = (post) => {
                post.showCommentForm = false;
                post.commentForm = {
                    ticketId: '',
                    description: ''
                };
            };

            // 提交留言
            const submitComment = async (post) => {
                if (!validateCommentForm(post.commentForm)) {
                    return;
                }

                post.commentSubmitting = true;

                try {
                    const requestData = {
                        postId: post.postId,
                        ticketId: parseInt(post.commentForm.ticketId),
                        description: post.commentForm.description
                    };

                    console.log('提交留言:', requestData);

                    const response = await fetch(`${API_BASE_URL}/ticket-exchange/comments`, {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        credentials: 'include',
                        body: JSON.stringify(requestData)
                    });

                    if (response.ok) {
                        const data = await response.json();
                        console.log('提交留言回應:', data);

                        if (data.success) {
                            // 重新載入該貼文的留言
                            await loadComments(post.postId);

                            // 隱藏留言表單
                            hideCommentForm(post);

                            // 自動顯示留言
                            post.showComments = true;

                            alert('留言發表成功！');
                        } else {
                            throw new Error(data.userMessage || '發表留言失敗');
                        }
                    } else {
                        const errorData = await response.json().catch(() => ({}));
                        console.error('提交留言HTTP錯誤:', response.status, errorData);
                        throw new Error(errorData.userMessage || `HTTP錯誤: ${response.status}`);
                    }
                } catch (err) {
                    console.error('提交留言時發生錯誤:', err);
                    alert(err.message || '發表失敗，請稍後再試');
                } finally {
                    post.commentSubmitting = false;
                }
            };

            // 載入留言
            const loadComments = async (postId) => {
                try {
                    console.log('載入留言，postId:', postId);
                    const response = await fetch(`${API_BASE_URL}/ticket-exchange/posts/${postId}/comments`);

                    if (response.ok) {
                        const data = await response.json();
                        console.log('留言回應:', data);

                        if (data.success) {
                            // 找到對應的貼文並更新留言
                            const postIndex = swapPosts.value.findIndex(p => p.postId === postId);
                            if (postIndex !== -1) {
                                swapPosts.value[postIndex].comments = data.data;
                                swapPosts.value[postIndex].commentCount = data.data.length;
                                console.log('已載入留言數量:', data.data.length);
                            }
                        } else {
                            console.warn('留言回應格式不正確:', data);
                        }
                    } else {
                        console.error('載入留言HTTP錯誤:', response.status);
                    }
                } catch (err) {
                    console.error('載入留言時發生錯誤:', err);
                }
            };

            // 切換留言顯示
            const toggleComments = async (postId) => {
                const postIndex = swapPosts.value.findIndex(p => p.postId === postId);
                if (postIndex === -1) return;

                const post = swapPosts.value[postIndex];

                if (!post.showComments && !post.comments) {
                    // 首次顯示留言時從API載入
                    await loadComments(postId);
                }

                swapPosts.value[postIndex].showComments = !post.showComments;
            };

            // 更新留言狀態
            const updateCommentStatus = async (commentId, status) => {
                try {
                    const requestData = {
                        status: status
                    };

                    console.log('更新留言狀態:', commentId, status);

                    const response = await fetch(`${API_BASE_URL}/ticket-exchange/comments/${commentId}/status`, {
                        method: 'PUT',
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        credentials: 'include',
                        body: JSON.stringify(requestData)
                    });

                    if (response.ok) {
                        const data = await response.json();
                        console.log('更新留言狀態回應:', data);

                        if (data.success) {
                            // 重新載入所有有顯示留言的貼文
                            for (const post of swapPosts.value) {
                                if (post.showComments) {
                                    await loadComments(post.postId);
                                }
                            }

                            alert('狀態更新成功！');
                        } else {
                            throw new Error(data.userMessage || '狀態更新失敗');
                        }
                    } else {
                        const errorData = await response.json().catch(() => ({}));
                        console.error('更新留言狀態HTTP錯誤:', response.status, errorData);
                        throw new Error(errorData.userMessage || `HTTP錯誤: ${response.status}`);
                    }
                } catch (err) {
                    console.error('更新留言狀態時發生錯誤:', err);
                    alert(err.message || '更新失敗，請稍後再試');
                }
            };

            // 刪除貼文
            const deletePost = async (postId) => {
                if (!confirm('確定要刪除這篇轉票貼文嗎？')) {
                    return;
                }

                try {
                    console.log('刪除貼文:', postId);

                    const response = await fetch(`${API_BASE_URL}/ticket-exchange/posts/${postId}`, {
                        method: 'DELETE',
                        credentials: 'include'
                    });

                    if (response.ok) {
                        const data = await response.json();
                        console.log('刪除貼文回應:', data);

                        if (data.success) {
                            // 重新載入貼文列表
                            await fetchSwapPosts();
                            alert('貼文刪除成功！');
                        } else {
                            throw new Error(data.userMessage || '刪除失敗');
                        }
                    } else {
                        const errorData = await response.json().catch(() => ({}));
                        console.error('刪除貼文HTTP錯誤:', response.status, errorData);
                        throw new Error(errorData.userMessage || `HTTP錯誤: ${response.status}`);
                    }
                } catch (err) {
                    console.error('刪除貼文時發生錯誤:', err);
                    alert(err.message || '刪除失敗，請稍後再試');
                }
            };

            // ==================== 輔助方法 ====================

            // 安全獲取會員暱稱
            const getMemberNickName = (post) => {
                return (post.member && post.member.nickName) ? post.member.nickName : '匿名用戶';
            };

            const getMemberPhotoUrl = (member) => {
                if (!member || !member.photoUrl) return null;

                // 如果已經是完整 URL，直接返回
                if (member.photoUrl.startsWith('http://') || member.photoUrl.startsWith('https://')) {
                    return member.photoUrl;
                }

                // 如果是相對路徑，補上完整的基礎路徑
                if (member.photoUrl.startsWith('/api/')) {
                    return `/maven-tickeasy-v1${member.photoUrl}`;
                }

                return member.photoUrl;
            };

            // 安全獲取相對時間
            const getRelativeTime = (post) => {
                return post.relativeTime ? post.relativeTime : (post.createTime || '未知時間');
            };

            // 安全獲取票券類別名稱
            const getTicketCategoryName = (post) => {
                if (!post || !post.ticket) return '未知票種';
                return post.ticket.categoryName || '未知票種';
            };

            // 安全獲取票券價格
            const getTicketPrice = (post) => {
                if (!post || !post.ticket) return 0;
                return post.ticket.price || 0;
            };

            // 安全獲取留言會員暱稱
            const getCommentMemberNickName = (comment) => {
                return (comment.member && comment.member.nickName) ? comment.member.nickName : '匿名用戶';
            };

            // 安全獲取留言相對時間
            const getCommentRelativeTime = (comment) => {
                return comment.relativeTime ? comment.relativeTime : (comment.createTime || '未知時間');
            };

            // 安全獲取留言狀態文字
            const getCommentStatusText = (comment) => {
                return comment.statusText ? comment.statusText : getStatusText(comment.swappedStatus);
            };

            // 安全獲取留言票券類別名稱
            const getCommentTicketCategoryName = (comment) => {
                return (comment.ticket && comment.ticket.categoryName) ? comment.ticket.categoryName : '未知票種';
            };

            // 安全獲取留言票券價格
            const getCommentTicketPrice = (comment) => {
                return (comment.ticket && comment.ticket.price) ? comment.ticket.price : 0;
            };

            // 驗證轉票表單
            const validateSwapForm = () => {
                if (!swapForm.ticketId) {
                    alert('請選擇您的票券');
                    return false;
                }

                if (!swapForm.wantedTicketType.trim()) {
                    alert('請輸入希望交換的票區');
                    return false;
                }
                return true;
            };

            // 驗證留言表單
            const validateCommentForm = (commentForm) => {
                if (!commentForm.ticketId) {
                    alert('請選擇您要交換的票券');
                    return false;
                }

                if (!commentForm.description.trim()) {
                    alert('請輸入留言內容');
                    return false;
                }

                return true;
            };

            // 重置轉票表單
            const resetSwapForm = () => {
                swapForm.ticketId = '';
                swapForm.wantedTicketType = '';
                swapForm.description = '';
            };

            // 檢查是否為用戶自己的貼文
            const isMyPost = (post) => {
                return isLoggedIn.value && post.member && post.member.nickName === memberNickname.value;
            };

            // 檢查是否可以更新留言狀態
            const canUpdateCommentStatus = (post, comment) => {
                if (!isLoggedIn.value) return false;

                // 貼文擁有者或留言者可以更新狀態
                return (post.member && post.member.nickName === memberNickname.value) ||
                    (comment.member && comment.member.nickName === memberNickname.value);
            };

            // 獲取狀態CSS類別
            const getStatusClass = (status) => {
                const statusClasses = {
                    0: 'status-pending',
                    1: 'status-waiting',
                    2: 'status-completed',
                    3: 'status-cancelled'
                };
                return statusClasses[status] || '';
            };

            // 獲取狀態文字
            const getStatusText = (status) => {
                const statusTexts = {
                    0: '待換票',
                    1: '待確認',
                    2: '已完成',
                    3: '已取消'
                };
                return statusTexts[status] || '未知狀態';
            };

            // 提取希望交換的票種
            const extractWantedTicketType = (description) => {
                if (!description) return '未指定';

                // 嘗試多種模式匹配
                const patterns = [
                    /希望交換[:：]\s*([^\n\r]+)/,
                    /想換\s*([^\n\r，。！？]+)/,
                    /交換\s*([^\n\r，。！？]+)/,
                    /換取\s*([^\n\r，。！？]+)/
                ];

                for (const pattern of patterns) {
                    const match = description.match(pattern);
                    if (match && match[1]) {
                        let result = match[1].trim();
                        // 移除可能的換行符號和多餘文字
                        result = result.split(/[，。！？\n\r]/)[0].trim();
                        if (result.length > 0 && result.length <= 50) {
                            return result;
                        }
                    }
                }

                // 如果沒有找到特定模式，嘗試從描述中提取關鍵資訊
                const lines = description.split(/[\n\r]+/);
                for (const line of lines) {
                    if (line.includes('VIP') || line.includes('區') || line.includes('票')) {
                        const cleaned = line.trim();
                        if (cleaned.length <= 30) {
                            return cleaned;
                        }
                    }
                }

                // 最後回退到截取前30個字符
                return description.substring(0, 30) + (description.length > 30 ? '...' : '');
            };

            // 格式化價格
            const formatPrice = (price) => {
                if (price === null || price === undefined) return '0';
                if (typeof price === 'string') {
                    const numPrice = parseFloat(price);
                    return isNaN(numPrice) ? '0' : numPrice.toLocaleString('zh-TW');
                }
                if (typeof price === 'number') {
                    return price.toLocaleString('zh-TW');
                }
                return '0';
            };

            // 獲取頭像文字
            const getAvatarText = (name) => {
                if (!name) return '?';
                return name.charAt(0).toUpperCase();
            };

            // 處理圖片載入錯誤
            const handleImageError = (event, member) => {
                try {
                    console.log('圖片載入失敗:', event.target.src);
                    const img = event.target;
                    img.style.display = 'none';

                    const placeholder = img.nextElementSibling;
                    if (placeholder && placeholder.classList.contains('avatar-placeholder')) {
                        placeholder.style.display = 'flex';
                    }

                    // ✅ 正確使用傳入的 member 參數
                    if (member && member.memberId) {
                        console.warn(`會員 ${member.memberId} 的照片載入失敗`);
                    }
                } catch (error) {
                    console.error('處理圖片錯誤時發生異常:', error);
                }
            };

            // 獲取 Cookie 值
            const getCookie = (name) => {
                if (!document.cookie) return null;

                const cookies = document.cookie.split(';');
                for (let i = 0; i < cookies.length; i++) {
                    const cookie = cookies[i].trim();
                    if (cookie.startsWith(name + '=')) {
                        return cookie.substring(name.length + 1);
                    }
                }
                return null;
            };

            // 導向登入頁面
            const goToLogin = () => {
                const currentUrl = encodeURIComponent(window.location.href);
                window.location.href = `http://localhost:8080/maven-tickeasy-v1/user/member/login.html?redirect=${currentUrl}`;
            };

            // 導航方法
            const goBackToEvent = () => {
                if (eventId.value) {
                    window.location.href = `../buy/event_ticket_purchase.html?eventId=${eventId.value}`;
                } else {
                    window.history.back();
                }
            };

            const goToEventInfo = () => {
                if (eventId.value) {
                    window.location.href = `../buy/event_ticket_purchase.html?eventId=${eventId.value}`;
                }
            };

            const goToTicketPrice = () => {
                if (eventId.value) {
                    window.location.href = `../buy/event_ticket_purchase.html?eventId=${eventId.value}#price`;
                }
            };

            // ==================== 生命週期鉤子 ====================

            // 組件掛載時執行
            onMounted(() => {
                // 從 URL 獲取活動 ID
                const urlParams = new URLSearchParams(window.location.search);
                eventId.value = urlParams.get('eventId');

                console.log('Vue 應用掛載，eventId:', eventId.value);

                // 如果沒有提供活動 ID，顯示錯誤
                if (!eventId.value) {
                    errorMessage.value = '缺少活動ID參數';
                    isLoading.value = false;
                    console.error('缺少eventId參數');
                } else {
                    initPage();
                }

                // 設置頁面標題
                document.title = 'TickEasy - 轉票專區';
            });

            // 在資料更新後更新頁面標題
            onUpdated(() => {
                if (eventName.value) {
                    document.title = `TickEasy - 轉票專區 - ${eventName.value}`;
                }
            });

            // ==================== 返回模板所需的所有內容 ====================
            return {
                // 響應式數據
                eventId,
                eventName,
                memberId,
                memberNickname,
                isLoggedIn,
                swapPosts,
                userTickets,
                isLoading,
                errorMessage,
                isSubmitting,
                swapForm,

                // 方法
                initPage,
                checkLoginStatus,
                fetchEventInfo,
                fetchSwapPosts,
                fetchUserTickets,
                submitSwapPost,
                showCommentForm,
                hideCommentForm,
                submitComment,
                loadComments,
                toggleComments,
                updateCommentStatus,
                deletePost,

                // 輔助方法
                getMemberNickName,
                getMemberPhotoUrl,
                getRelativeTime,
                getTicketCategoryName,
                getTicketPrice,
                getCommentMemberNickName,
                getCommentRelativeTime,
                getCommentStatusText,
                getCommentTicketCategoryName,
                getCommentTicketPrice,
                validateSwapForm,
                validateCommentForm,
                resetSwapForm,
                isMyPost,
                canUpdateCommentStatus,
                getStatusClass,
                getStatusText,
                extractWantedTicketType,
                formatPrice,
                getAvatarText,
                handleImageError,
                getCookie,
                goToLogin,
                goBackToEvent,
                goToEventInfo,
                goToTicketPrice
            };
        }
    });

    // 掛載應用
    ticketExchangeApp.mount('#app');
}

// 當 DOM 載入完成後初始化應用
document.addEventListener('DOMContentLoaded', initializeTicketExchangeApp);

// 導出函數供其他模組使用
export { initializeTicketExchangeApp, initTicketExchangeVueApp };