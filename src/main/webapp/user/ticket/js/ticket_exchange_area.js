/**
 * 轉票專區 Vue 應用 - Composition API 版本
 * 創建日期: 2025-05-27
 */

// ES6 import Vue Composition API
import { createApp, ref, reactive, onMounted, onUpdated, computed } from '../../../common/vendors/vue.esm-browser-3.5.16.js';

// 從共用組件匯入需要的函數
import { fetchNavTemplate, renderNav, initNavJSEvents } from '../../layout/nav/nav.js';
import { fetchFooterTemplate, renderFooter } from '../../layout/footer/footer.js';

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
        const navTemplate = await fetchNavTemplate();
        await renderNav(navTemplate);
        console.log('導覽列載入完成');

        const footerTemplate = await fetchFooterTemplate();
        await renderFooter(footerTemplate);
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

            // ==================== 初始化方法 ====================

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
                            if (data.data.isLoggedIn && data.data.nickname && data.data.memberId) {
                                isLoggedIn.value = true;
                                memberNickname.value = data.data.nickname;
                                memberId.value = data.data.memberId;
                            } else {
                                isLoggedIn.value = false;
                                memberNickname.value = null;
                                memberId.value = null;
                            }
                        }
                    } else {
                        isLoggedIn.value = false;
                        memberNickname.value = null;
                        memberId.value = null;
                    }
                } catch (err) {
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
                try {
                    if (!eventId.value) return;

                    const timestamp = new Date().getTime();
                    const response = await fetch(`${API_BASE_URL}/ticket-exchange/posts/event/${eventId.value}?t=${timestamp}`, {
                        cache: 'no-cache',
                        headers: {
                            'Cache-Control': 'no-cache',
                            'Pragma': 'no-cache'
                        }
                    });

                    if (response.ok) {
                        const data = await response.json();
                        if (data.success && data.data) {
                            // ✅ 核心修正：保留現有的UI狀態和留言資料
                            const currentPosts = swapPosts.value || [];

                            swapPosts.value = data.data.map(post => {
                                // 查找對應的現有貼文
                                const existingPost = currentPosts.find(p => p.postId === post.postId);

                                if (existingPost) {
                                    // ✅ 現有貼文：保留所有UI狀態，只更新後端資料
                                    return {
                                        ...post,                                    // 新的後端資料
                                        showComments: existingPost.showComments,   // 保留留言顯示狀態
                                        showCommentForm: existingPost.showCommentForm, // 保留表單顯示狀態
                                        comments: existingPost.comments,           // 保留已載入的留言
                                        commentCount: existingPost.commentCount,   // 保留留言數量
                                        commentForm: existingPost.commentForm,     // 保留表單資料
                                        commentSubmitting: existingPost.commentSubmitting // 保留提交狀態
                                    };
                                } else {
                                    // ✅ 新貼文：使用預設的UI狀態
                                    return {
                                        ...post,
                                        showComments: false,
                                        showCommentForm: false,
                                        comments: null,
                                        commentCount: 0,
                                        commentForm: {
                                            ticketId: '',
                                            description: '',
                                            availableTickets: []
                                        },
                                        commentSubmitting: false
                                    };
                                }
                            });

                            console.log('✅ 貼文列表已更新，UI狀態已保留');
                        } else {
                            swapPosts.value = [];
                        }
                    } else {
                        swapPosts.value = [];
                    }

                } catch (err) {
                    console.error('載入換票貼文時發生錯誤:', err);
                }
            };

            // 獲取用戶票券列表
            const fetchUserTickets = async () => {
                if (!isLoggedIn.value) return;

                try {
                    console.log('獲取用戶票券...');

                    const response = await fetch(`${API_BASE_URL}/my-tickets/event/${eventId.value}`, {
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

            // ==================== 貼文操作方法 ====================

            // 提交轉票貼文
            const submitSwapPost = async () => {
                if (!isLoggedIn.value) {
                    alert('請先登入才能發表轉票貼文');
                    return;
                }

                if (isSubmitting.value) {
                    console.log('正在提交中，忽略重複請求');
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

                        if (data.success) {
                            // 重新載入貼文列表
                            await fetchSwapPosts();

                            // 清空表單
                            resetSwapForm();

                            alert('轉票貼文發表成功！');
                        } else {
                            throw new Error(data.message || '發表轉票貼文失敗');
                        }
                    } else {
                        const errorData = await response.json().catch(() => ({}));
                        throw new Error(errorData.message || `HTTP錯誤: ${response.status}`);
                    }
                } catch (err) {
                    console.error('提交轉票貼文時發生錯誤:', err);
                    if (err.message.includes('已將此票券用於其他進行中的換票')) {
                        alert('您選擇的票券已用於其他進行中的換票，請選擇其他票券或先完成/取消原有的換票');
                    } else if (err.message.includes('已對此活動發布')) {
                        alert('您已對此活動發布過換票貼文，請編輯現有貼文或先刪除後重新發布');
                    } else if (err.message.includes('同一活動')) {
                        alert('只能交換同一活動的票券，請確認您的選擇');
                    } else {
                        alert(`發布失敗：${err.message || '請稍後再試'}`);
                    }
                } finally {
                    isSubmitting.value = false;
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
                            throw new Error(data.message || '刪除失敗');
                        }
                    } else {
                        const errorData = await response.json().catch(() => ({}));
                        console.error('刪除貼文HTTP錯誤:', response.status, errorData);
                        throw new Error(errorData.message || `HTTP錯誤: ${response.status}`);
                    }
                } catch (err) {
                    console.error('刪除貼文時發生錯誤:', err);
                    alert(err.message || '刪除失敗，請稍後再試');
                }
            };

            // ==================== 留言操作方法 ====================

            // 顯示留言表單
            const showCommentForm = (post) => {
                if (!isLoggedIn.value) {
                    alert('請先登入才能發表留言');
                    return;
                }

                const postEventName = post.event?.eventName;

                // 過濾掉已用於進行中換票的票券
                const availableTickets = userTickets.value.filter(ticket => {
                    return ticket.eventName === postEventName &&
                        !isTicketUsedInActiveExchange(ticket.ticketId) &&
                        ticket.ticketId !== post.ticket?.ticketId;
                });

                if (availableTickets.length === 0) {
                    alert(`您沒有「${postEventName}」的可用票券進行交換，或您的票券已用於其他進行中的換票`);
                    return;
                }

                // 用戶友好提示
                if (availableTickets.length === 1) {
                    console.log('系統已為您篩選出唯一可交換的票券');
                } else if (availableTickets.length > 3) {
                    console.log(`您有 ${availableTickets.length} 張可交換票券，請仔細選擇`);
                }

                // 重置表單
                post.commentForm = {
                    ticketId: '',
                    description: '',
                    availableTickets: availableTickets
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
                if (post.commentSubmitting) {
                    return;
                }

                if (!validateCommentForm(post.commentForm)) {
                    return;
                }

                const selectedTicket = post.commentForm.availableTickets.find(
                    t => t.ticketId === parseInt(post.commentForm.ticketId, 10)
                );

                if (!selectedTicket) {
                    alert('找不到選中的票券，請重新選擇');
                    return;
                }

                if (isTicketUsedInActiveExchange(selectedTicket.ticketId)) {
                    alert('選中的票券已用於其他交換，請重新整理頁面');
                    return;
                }

                if (!confirm(`確定要用「${selectedTicket.categoryName} - 票券#${selectedTicket.ticketId}」進行交換嗎？`)) {
                    return;
                }

                post.commentSubmitting = true;

                try {
                    const requestData = {
                        postId: parseInt(post.postId, 10),
                        ticketId: parseInt(post.commentForm.ticketId, 10),
                        description: String(post.commentForm.description || '').trim()
                    };

                    const response = await fetch(`${API_BASE_URL}/ticket-exchange/comments`, {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        credentials: 'include',
                        body: JSON.stringify(requestData)
                    });

                    if (response.status === 401) {
                        alert('登入已過期，請重新登入');
                        goToLogin();
                        return;
                    }

                    if (response.ok) {
                        const data = await response.json();
                        if (data.success) {
                            await loadComments(post.postId);
                            hideCommentForm(post);
                            post.showComments = true;
                            alert('留言發表成功！');
                        } else {
                            throw new Error(data.message || '發表留言失敗');
                        }
                    } else {
                        const errorData = await response.json().catch(() => ({}));
                        throw new Error(errorData.message || `HTTP錯誤 ${response.status}`);
                    }
                } catch (err) {
                    console.error('提交留言錯誤:', err);
                    alert(`發表失敗：${err.message}`);
                } finally {
                    post.commentSubmitting = false;
                }
            };

            // 🔧 修改：載入留言時確保響應式
            const loadComments = async (postId) => {
                try {
                    const timestamp = new Date().getTime();
                    const response = await fetch(`${API_BASE_URL}/ticket-exchange/posts/${postId}/comments?t=${timestamp}`, {
                        cache: 'no-cache',
                        headers: {
                            'Cache-Control': 'no-cache, no-store, must-revalidate',
                            'Pragma': 'no-cache',
                            'Expires': '0'
                        }
                    });

                    if (response.ok) {
                        const data = await response.json();
                        if (data.success) {
                            const postIndex = swapPosts.value.findIndex(p => p.postId === postId);
                            if (postIndex !== -1) {
                                // 🔧 關鍵：確保留言資料響應式
                                const reactiveComments = data.data.map(comment => ({
                                    ...comment,
                                    swappedStatus: comment.swappedStatus || 0,
                                    statusText: comment.statusText || getStatusText(comment.swappedStatus || 0)
                                }));
                                
                                swapPosts.value[postIndex].comments = reactiveComments;
                                swapPosts.value[postIndex].commentCount = reactiveComments.length;
                                console.log(`貼文 ${postId} 的留言已更新，共 ${reactiveComments.length} 則`);
                            }
                        }
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

            // ==================== 🔧 修改：簡化的狀態操作方法 ====================

            // 🔧 新增：接受請求並直接完成交換
            const acceptAndCompleteExchange = async (commentId) => {
                if (!confirm('確定要接受此換票請求嗎？票券將立即完成交換且無法撤銷。')) {
                    return;
                }

                console.log('開始接受並完成交換，commentId:', commentId);

                try {
                    // 🔧 修改：直接發送狀態2
                    const response = await fetch(`${API_BASE_URL}/ticket-exchange/comments/${commentId}/status`, {
                        method: 'PUT',
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        credentials: 'include',
                        body: JSON.stringify({ status: 2 }) // 🔧 直接設為完成狀態
                    });

                    if (response.ok) {
                        const data = await response.json();
                        if (data.success) {
                            console.log('後端回應成功');
                            
                            // 🔧 立即更新本地狀態為已完成
                            updateLocalCommentStatusReactive(commentId, 2);
                            
                            // 顯示成功訊息
                            alert(data.message || '票券交換完成！');
                            
                            // 重新載入資料
                            setTimeout(async () => {
                                await refreshAllData(commentId);
                            }, 500);
                            
                        } else {
                            throw new Error(data.message || '操作失敗');
                        }
                    } else {
                        const errorData = await response.json().catch(() => ({}));
                        throw new Error(errorData.message || `HTTP錯誤: ${response.status}`);
                    }
                } catch (err) {
                    console.error('接受並完成交換時發生錯誤:', err);
                    alert(`操作失敗：${err.message}`);
                }
            };

            // 🔧 修改：取消請求（保持不變）
            const cancelSwapRequest = async (commentId) => {
                if (!confirm('確定要取消此換票請求嗎？')) {
                    return;
                }

                try {
                    const response = await fetch(`${API_BASE_URL}/ticket-exchange/comments/${commentId}/status`, {
                        method: 'PUT',
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        credentials: 'include',
                        body: JSON.stringify({ status: 3 })
                    });

                    if (response.ok) {
                        const data = await response.json();
                        if (data.success) {
                            // 立即更新本地狀態
                            updateLocalCommentStatusReactive(commentId, 3);
                            alert(data.message || '已取消換票請求');
                            
                            // 重新載入資料
                            setTimeout(async () => {
                                await refreshAllData(commentId);
                            }, 500);
                        } else {
                            throw new Error(data.message || '操作失敗');
                        }
                    } else {
                        const errorData = await response.json().catch(() => ({}));
                        throw new Error(errorData.message || `HTTP錯誤: ${response.status}`);
                    }
                } catch (err) {
                    console.error('取消請求時發生錯誤:', err);
                    alert(`操作失敗：${err.message}`);
                }
            };

            // ==================== 🔧 修改：響應式更新和權限判斷 ====================

            // 🔧 新增：修復響應式更新問題
            const updateLocalCommentStatusReactive = (commentId, newStatus) => {
                console.log(`立即更新本地留言狀態: commentId=${commentId}, newStatus=${newStatus}`);
                
                for (const post of swapPosts.value) {
                    if (post.comments && Array.isArray(post.comments)) {
                        const commentIndex = post.comments.findIndex(c => c.commentId === commentId);
                        if (commentIndex !== -1) {
                            // 🔧 關鍵：使用 Vue 3 正確的響應式更新
                            const updatedComment = {
                                ...post.comments[commentIndex],
                                swappedStatus: newStatus,
                                statusText: getStatusText(newStatus)
                            };
                            post.comments.splice(commentIndex, 1, updatedComment);
                            
                            console.log(`成功更新留言 ${commentId} 的狀態為 ${newStatus}`);
                            break;
                        }
                    }
                }
            };

            // 🔧 新增：統一的資料重新載入方法
            const refreshAllData = async (targetCommentId) => {
                try {
                    console.log('開始重新載入所有資料...');
                    
                    // 1. 重新載入貼文列表（保留UI狀態）
                    await fetchSwapPosts();
                    
                    // 2. 重新載入用戶票券
                    if (isLoggedIn.value) {
                        await fetchUserTickets();
                    }
                    
                    // 3. 找到包含目標留言的貼文，重新載入留言
                    for (const post of swapPosts.value) {
                        if (post.showComments || (post.comments && post.comments.length > 0)) {
                            const hasTargetComment = post.comments && 
                                post.comments.some(c => c.commentId === targetCommentId);
                            
                            if (hasTargetComment) {
                                console.log(`重新載入貼文 ${post.postId} 的留言`);
                                await loadComments(post.postId);
                                post.showComments = true; // 確保留言區塊保持展開
                                break;
                            }
                        }
                    }
                    
                    console.log('所有資料重新載入完成');
                } catch (error) {
                    console.error('重新載入資料失敗:', error);
                }
            };

            // ==================== 🔧 修改：簡化權限判斷方法 ====================

            // 🔧 新增：檢查是否可以接受並完成交換
            const canAcceptAndComplete = (post, comment) => {
                if (!isLoggedIn.value || !post.member || !comment || comment.swappedStatus !== 0) {
                    return false;
                }
                // 只有貼文發起方可以接受並完成交換
                return post.member.memberId === memberId.value;
            };

            // 🔧 修改：簡化取消權限判斷
            const canCancel = (post, comment) => {
                if (!isLoggedIn.value || !comment || comment.swappedStatus !== 0) { // 🔧 只有待換票狀態可以取消
                    return false;
                }
                
                // 留言方或貼文方都可以取消
                const isCommentOwner = comment.member && comment.member.memberId === memberId.value;
                const isPostOwner = post.member && post.member.memberId === memberId.value;
                
                return isCommentOwner || isPostOwner;
            };

            // ==================== 輔助方法 ====================

            // 檢查票券是否已用於進行中的換票
            const isTicketUsedInActiveExchange = (ticketId) => {
                // 檢查是否已在貼文中
                const usedInPost = swapPosts.value.some(post =>
                    post.ticket && post.ticket.ticketId === ticketId
                );

                if (usedInPost) {
                    return true;
                }

                // 🔧 修改：檢查是否已在進行中的留言中（狀態為0）
                const usedInActiveComment = swapPosts.value.some(post =>
                    post.comments && post.comments.some(comment =>
                        comment.ticket && comment.ticket.ticketId === ticketId &&
                        comment.swappedStatus === 0 // 🔧 只檢查待換票狀態
                    )
                );

                return usedInActiveComment;
            };

            // ==================== 其他原有方法保持不變 ====================

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
                console.log('驗證留言表單:', commentForm);
                if (!commentForm) {
                    alert('表單對象不存在');
                    return false;
                }

                const ticketId = parseInt(commentForm.ticketId, 10);

                if (!commentForm.ticketId || commentForm.ticketId === '' || isNaN(ticketId) || ticketId <= 0) {
                    alert('請選擇您要交換的票券');
                    return false;
                }

                if (!commentForm.description || String(commentForm.description).trim() === '') {
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
                return isLoggedIn.value && post.member && post.member.memberId === memberId.value;
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

                    // 正確使用傳入的 member 參數
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

            // 改良後的票券顯示格式
            const formatTicketDisplay = (ticket) => {
                const purchaseDate = ticket.createTime ?
                    new Date(ticket.createTime).toLocaleDateString('zh-TW', { month: '2-digit', day: '2-digit' }) : '';
                return `${ticket.categoryName} - ${ticket.participantName} (NT$ ${formatPrice(ticket.price)}) - 票券#${ticket.ticketId}${purchaseDate ? ` [${purchaseDate}購買]` : ''}`;
            };

            // 🔧 修改：簡化的狀態文字對應
            const getStatusText = (status) => {
                const statusTexts = {
                    0: '待換票',
                    2: '已完成',
                    3: '已取消'
                };
                return statusTexts[status] || '未知狀態';
            };

            // 獲取狀態CSS類別
            const getStatusClass = (status) => {
                const statusClasses = {
                    0: 'status-pending',
                    2: 'status-completed',
                    3: 'status-cancelled'
                };
                return statusClasses[status] || '';
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

            // ==================== 🔧 修改：返回模板所需的所有內容 ====================
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

                // 初始化方法
                initPage,
                checkLoginStatus,
                fetchEventInfo,
                fetchSwapPosts,
                fetchUserTickets,

                // 貼文操作方法
                submitSwapPost,
                deletePost,

                // 留言操作方法
                showCommentForm,
                hideCommentForm,
                submitComment,
                loadComments,
                toggleComments,

                // 🔧 修改：簡化的狀態操作方法
                acceptAndCompleteExchange, // 🔧 新增：接受並完成交換
                cancelSwapRequest,         // 保留：取消請求

                // 🔧 修改：簡化的權限判斷方法
                canAcceptAndComplete,      // 🔧 新增：是否可以接受並完成
                canCancel,                 // 修改：簡化的取消權限判斷

                // 輔助方法
                updateLocalCommentStatusReactive, // 🔧 新增
                refreshAllData,                   // 🔧 新增
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
                formatTicketDisplay,
                isTicketUsedInActiveExchange
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