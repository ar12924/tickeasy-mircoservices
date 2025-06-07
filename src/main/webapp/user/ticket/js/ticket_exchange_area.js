/**
 * 轉票專區 Vue 應用 - 完全從資料庫讀取版本
 * 創建日期: 2025-05-27
 */

// 基礎 API URL
const API_BASE_URL = '/maven-tickeasy-v1/api';


// 創建 Vue 應用
window.app = Vue.createApp({
    data() {
        return {
            // 活動ID (從 URL 參數中獲取)
            eventId: null,
            // 活動名稱
            eventName: '',
            // 會員ID
            memberId: null,
            // 會員暱稱 - 新增
            memberNickname: null,
            // 用戶登錄狀態
            isLoggedIn: false,
            // 轉票貼文列表
            swapPosts: [],
            // 用戶票券列表
            userTickets: [],
            // 載入狀態
            loading: true,
            // 錯誤訊息
            error: null,
            // 提交狀態
            submitting: false,
            // 轉票表單資料
            swapForm: {
                ticketId: '',
                wantedTicketType: '',
                description: ''
            }
        };
    },

    methods: {
        // 初始化頁面
        async initPage() {
            this.loading = true;
            this.error = null;

            try {
                // 檢查登錄狀態
                await this.checkLoginStatus();

                // 確認登入狀態已經設定後，再執行其他操作
                console.log('登入狀態確認完成，isLoggedIn:', this.isLoggedIn);

                // 平行執行其他操作
                const promises = [
                    this.fetchEventInfo(),
                    this.fetchSwapPosts()
                ];

                // 如果已登入，加入獲取用戶票券
                if (this.isLoggedIn) {
                    promises.push(this.fetchUserTickets());
                }

                // 等待所有操作完成
                await Promise.all(promises);
            } catch (err) {
                console.error('初始化頁面時發生錯誤:', err);
                this.error = '頁面載入失敗，請重新整理頁面';
            } finally {
                this.loading = false;
            }
        },

        // 檢查用戶登錄狀態
        async checkLoginStatus() {
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
                            this.isLoggedIn = true;
                            this.memberNickname = data.data.nickname;
                            this.memberId = null;
                            console.log('登入成功:', this.memberNickname);
                        } else {
                            this.isLoggedIn = false;
                            this.memberNickname = null;
                            this.memberId = null;
                            console.log('用戶未登入');
                        }
                    }
                } else {
                    console.error('檢查登入狀態HTTP錯誤:', response.status);
                    this.isLoggedIn = false;
                    this.memberNickname = null;
                    this.memberId = null;
                }
            } catch (err) {
                console.error('檢查登錄狀態時發生錯誤:', err);
                this.isLoggedIn = false;
                this.memberNickname = null;
                this.memberId = null;
            }
        },



        // 獲取活動資訊
        async fetchEventInfo() {
            if (!this.eventId) return;

            try {
                console.log('獲取活動資訊，eventId:', this.eventId);
                const response = await fetch(`${API_BASE_URL}/events/${this.eventId}`);

                if (response.ok) {
                    const data = await response.json();
                    console.log('活動資訊回應:', data);

                    if (data.status === 200 && data.data) {
                        this.eventName = data.data.eventName;
                        console.log('活動名稱:', this.eventName);
                    } else {
                        console.warn('活動資訊格式不正確:', data);
                        this.eventName = '未知活動';
                    }
                } else {
                    console.error('獲取活動資訊HTTP錯誤:', response.status);
                    this.eventName = '未知活動';
                }
            } catch (err) {
                console.error('獲取活動資訊時發生錯誤:', err);
                this.eventName = '未知活動';
            }
        },

        // 獲取轉票貼文列表
        async fetchSwapPosts() {
            if (!this.eventId) return;

            try {
                console.log('獲取轉票貼文，eventId:', this.eventId);
                const response = await fetch(`${API_BASE_URL}/ticket-exchange/posts/event/${this.eventId}`);

                if (response.ok) {
                    const data = await response.json();
                    console.log('轉票貼文回應:', data);

                    if (data.success && data.data) {
                        this.swapPosts = data.data.map(post => ({
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
                        console.log('已載入轉票貼文數量:', this.swapPosts.length);
                    } else {
                        console.warn('轉票貼文回應格式不正確:', data);
                        this.swapPosts = [];
                    }
                } else {
                    console.error('獲取轉票貼文HTTP錯誤:', response.status);
                    this.swapPosts = [];
                }
            } catch (err) {
                console.error('獲取轉票貼文時發生錯誤:', err);
                this.swapPosts = [];
            }
        },

        // 獲取用戶票券列表
        async fetchUserTickets() {
            if (!this.isLoggedIn) return;

            try {
                console.log('獲取用戶票券...');

                const response = await fetch(`${API_BASE_URL}/my-tickets`, {
                    credentials: 'include'  // 重要：攜帶 session
                });

                if (response.ok) {
                    const data = await response.json();
                    console.log('用戶票券回應:', data);

                    if (data.success && data.data) {
                        this.userTickets = data.data;
                        console.log('已載入用戶票券數量:', this.userTickets.length);
                    } else {
                        this.userTickets = [];
                    }
                } else {
                    console.error('獲取用戶票券HTTP錯誤:', response.status);
                    this.userTickets = [];
                }
            } catch (err) {
                console.error('獲取用戶票券時發生錯誤:', err);
                this.userTickets = [];
            }
        },

        // 提交轉票貼文
        async submitSwapPost() {
            if (!this.isLoggedIn) {
                alert('請先登入才能發表轉票貼文');
                return;
            }

            if (!this.validateSwapForm()) {
                return;
            }

            this.submitting = true;

            try {
                const requestData = {
                    ticketId: parseInt(this.swapForm.ticketId),
                    description: `希望交換: ${this.swapForm.wantedTicketType}\n${this.swapForm.description}`,
                    eventId: parseInt(this.eventId)
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
                        await this.fetchSwapPosts();

                        // 清空表單
                        this.resetSwapForm();

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
                this.submitting = false;
            }
        },

        // 顯示留言表單
        showCommentForm(post) {
            if (!this.isLoggedIn) {
                alert('請先登入才能發表留言');
                return;
            }

            // 重置表單
            post.commentForm = {
                ticketId: '',
                description: ''
            };
            post.showCommentForm = true;
        },

        // 隱藏留言表單
        hideCommentForm(post) {
            post.showCommentForm = false;
            post.commentForm = {
                ticketId: '',
                description: ''
            };
        },

        // 提交留言
        async submitComment(post) {
            if (!this.validateCommentForm(post.commentForm)) {
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
                        await this.loadComments(post.postId);

                        // 隱藏留言表單
                        this.hideCommentForm(post);

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
        },

        // 載入留言
        async loadComments(postId) {
            try {
                console.log('載入留言，postId:', postId);
                const response = await fetch(`${API_BASE_URL}/ticket-exchange/posts/${postId}/comments`);

                if (response.ok) {
                    const data = await response.json();
                    console.log('留言回應:', data);

                    if (data.success) {
                        // 找到對應的貼文並更新留言
                        const postIndex = this.swapPosts.findIndex(p => p.postId === postId);
                        if (postIndex !== -1) {
                            this.swapPosts[postIndex].comments = data.data;
                            this.swapPosts[postIndex].commentCount = data.data.length;
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
        },

        // 切換留言顯示
        async toggleComments(postId) {
            const postIndex = this.swapPosts.findIndex(p => p.postId === postId);
            if (postIndex === -1) return;

            const post = this.swapPosts[postIndex];

            if (!post.showComments && !post.comments) {
                // 首次顯示留言時從API載入
                await this.loadComments(postId);
            }

            this.swapPosts[postIndex].showComments = !post.showComments;
        },

        // 更新留言狀態
        async updateCommentStatus(commentId, status) {
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
                        for (const post of this.swapPosts) {
                            if (post.showComments) {
                                await this.loadComments(post.postId);
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
        },

        // 刪除貼文
        async deletePost(postId) {
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
                        await this.fetchSwapPosts();
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
        },

        // 安全獲取會員暱稱
        getMemberNickName(post) {
            return (post.member && post.member.nickName) ? post.member.nickName : '匿名用戶';
        },

        getMemberPhotoUrl(member) {
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
        },

        // 安全獲取相對時間
        getRelativeTime(post) {
            return post.relativeTime ? post.relativeTime : (post.createTime || '未知時間');
        },

        // 安全獲取票券類別名稱
        getTicketCategoryName(post) {
            if (!post || !post.ticket) return '未知票種';
            return post.ticket.categoryName || '未知票種';
        },

        // 安全獲取票券價格
        getTicketPrice(post) {
            if (!post || !post.ticket) return 0;
            return post.ticket.price || 0;
        },

        // 安全獲取留言會員暱稱
        getCommentMemberNickName(comment) {
            return (comment.member && comment.member.nickName) ? comment.member.nickName : '匿名用戶';
        },

        // 安全獲取留言相對時間
        getCommentRelativeTime(comment) {
            return comment.relativeTime ? comment.relativeTime : (comment.createTime || '未知時間');
        },

        // 安全獲取留言狀態文字
        getCommentStatusText(comment) {
            return comment.statusText ? comment.statusText : this.getStatusText(comment.swappedStatus);
        },

        // 安全獲取留言票券類別名稱
        getCommentTicketCategoryName(comment) {
            return (comment.ticket && comment.ticket.categoryName) ? comment.ticket.categoryName : '未知票種';
        },

        // 安全獲取留言票券價格
        getCommentTicketPrice(comment) {
            return (comment.ticket && comment.ticket.price) ? comment.ticket.price : 0;
        },

        // 驗證轉票表單
        validateSwapForm() {
            if (!this.swapForm.ticketId) {
                alert('請選擇您的票券');
                return false;
            }

            if (!this.swapForm.wantedTicketType.trim()) {
                alert('請輸入希望交換的票區');
                return false;
            }
            return true;
        },

        // 驗證留言表單
        validateCommentForm(commentForm) {
            if (!commentForm.ticketId) {
                alert('請選擇您要交換的票券');
                return false;
            }

            if (!commentForm.description.trim()) {
                alert('請輸入留言內容');
                return false;
            }

            return true;
        },

        // 重置轉票表單
        resetSwapForm() {
            this.swapForm = {
                ticketId: '',
                wantedTicketType: '',
                description: ''
            };
        },

        // 檢查是否為用戶自己的貼文
        isMyPost(post) {
            return this.isLoggedIn && post.member && post.member.nickName === this.memberNickname;
        },

        // 檢查是否可以更新留言狀態
        canUpdateCommentStatus(post, comment) {
            if (!this.isLoggedIn) return false;

            // 貼文擁有者或留言者可以更新狀態
            return (post.member && post.member.nickName === this.memberNickname) ||
                (comment.member && comment.member.nickName === this.memberNickname);
        },

        // 獲取狀態CSS類別
        getStatusClass(status) {
            const statusClasses = {
                0: 'status-pending',
                1: 'status-waiting',
                2: 'status-completed',
                3: 'status-cancelled'
            };
            return statusClasses[status] || '';
        },

        // 獲取狀態文字
        getStatusText(status) {
            const statusTexts = {
                0: '待換票',
                1: '待確認',
                2: '已完成',
                3: '已取消'
            };
            return statusTexts[status] || '未知狀態';
        },

        // 提取希望交換的票種
        extractWantedTicketType(description) {
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
        },

        // 格式化價格
        formatPrice(price) {
            if (price === null || price === undefined) return '0';
            if (typeof price === 'string') {
                const numPrice = parseFloat(price);
                return isNaN(numPrice) ? '0' : numPrice.toLocaleString('zh-TW');
            }
            if (typeof price === 'number') {
                return price.toLocaleString('zh-TW');
            }
            return '0';
        },

        // 獲取頭像文字
        getAvatarText(name) {
            if (!name) return '?';
            return name.charAt(0).toUpperCase();
        },

        // 處理圖片載入錯誤
        handleImageError(event, member) {
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
        },

        // 獲取 Cookie 值
        getCookie(name) {
            if (!document.cookie) return null;

            const cookies = document.cookie.split(';');
            for (let i = 0; i < cookies.length; i++) {
                const cookie = cookies[i].trim();
                if (cookie.startsWith(name + '=')) {
                    return cookie.substring(name.length + 1);
                }
            }
            return null;
        },

        // 導向登入頁面
        goToLogin() {
            const currentUrl = encodeURIComponent(window.location.href);
            window.location.href = `http://localhost:8080/maven-tickeasy-v1/user/member/login.html?redirect=${currentUrl}`;
        },

        // 導航方法
        goBackToEvent() {
            if (this.eventId) {
                window.location.href = `../buy/event_ticket_purchase.html?eventId=${this.eventId}`;
            } else {
                window.history.back();
            }
        },

        goToEventInfo() {
            if (this.eventId) {
                window.location.href = `../buy/event_ticket_purchase.html?eventId=${this.eventId}`;
            }
        },

        goToTicketPrice() {
            if (this.eventId) {
                window.location.href = `../buy/event_ticket_purchase.html?eventId=${this.eventId}#price`;
            }
        }
    },

    // 生命週期鉤子
    created() {
        // 從 URL 獲取活動 ID
        const urlParams = new URLSearchParams(window.location.search);
        this.eventId = urlParams.get('eventId');

        console.log('Vue 應用創建，eventId:', this.eventId);

        // 如果沒有提供活動 ID，顯示錯誤
        if (!this.eventId) {
            this.error = '缺少活動ID參數';
            this.loading = false;
            console.error('缺少eventId參數');
        }
    },

    // 在掛載後初始化頁面
    mounted() {
        console.log('Vue 應用掛載');

        if (this.eventId) {
            this.initPage();
        }

        // 設置頁面標題
        document.title = 'TickEasy - 轉票專區';
    },

    // 在資料更新後更新頁面標題
    updated() {
        if (this.eventName) {
            document.title = `TickEasy - 轉票專區 - ${this.eventName}`;
        }
    }
});

// 掛載應用
console.log('掛載 Vue 應用');
// 確保 nav 和 footer 載入完成後再掛載 Vue
$(document).ready(() => {
    setTimeout(() => {
        window.app.mount('#app');
        console.log('Vue 應用已掛載');
    }, 100);
});