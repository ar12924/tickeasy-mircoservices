/**
 * 活動詳情頁面 Vue 應用
 * 使用 Vue 3 Options API 實現
 * 使用 Fetch API 進行後端通信
 */

// 基礎 API URL (實際使用時需要設定正確的路徑)
const API_BASE_URL = '/maven-tickeasy-v1/api';

// 創建 Vue 應用
const app = Vue.createApp({
    // 資料
    data() {
        return {
            // 活動ID (從 URL 參數中獲取)
            eventId: null,
            // 活動詳情
            event: null,
            // 票券類型列表
            ticketTypes: [],
            // 加載狀態
            loading: true,
            // 錯誤訊息
            error: null,
            // 用戶登錄狀態
            isLoggedIn: false,
            // 用戶ID
            memberId: null,
            // 處理關注操作的加載狀態
            favoriteLoading: false
        };
    },

    // 計算屬性
    computed: {
        // 活動圖片 URL
        eventImageUrl() {
            if (!this.event) return '';
            return `${API_BASE_URL}/events/${this.event.eventId}/image`;
        },
        // 是否有可購買的票券
        hasAvailableTickets() {
            if (!this.ticketTypes || this.ticketTypes.length === 0) return false;
            
            // 檢查是否有任何票券剩餘且當前時間在售票期間內
            const now = new Date();
            
            return this.ticketTypes.some(ticket => {
                const sellFromTime = new Date(ticket.sellFromTime);
                const sellToTime = new Date(ticket.sellToTime);
                return ticket.remainingTickets > 0 && now >= sellFromTime && now <= sellToTime;
            });
        },
        // 活動時間格式化（開始時間到結束時間）
        eventTimeFormatted() {
            if (!this.event) return '';
            
            const fromDate = new Date(this.event.eventFromDate);
            const toDate = new Date(this.event.eventToDate);
            
            // 如果開始和結束是同一天，只顯示日期一次
            if (fromDate.toDateString() === toDate.toDateString()) {
                const dateStr = this.formatDateWithoutTime(fromDate);
                const fromTimeStr = this.formatTimeOnly(fromDate);
                const toTimeStr = this.formatTimeOnly(toDate);
                return `${dateStr} ${fromTimeStr} - ${toTimeStr}`;
            } else {
                return `${this.formatDate(this.event.eventFromDate)} - ${this.formatDate(this.event.eventToDate)}`;
            }
        }
    },

    // 方法
    methods: {
        // 獲取活動詳情資料
        async fetchEventData() {
            this.loading = true;
            this.error = null;
            
            try {
                // 確保 eventId 是有效的
                if (!this.eventId || isNaN(parseInt(this.eventId))) {
                    throw new Error('無效的活動ID');
                }
                
                // 獲取活動詳情
                const eventResponse = await fetch(`${API_BASE_URL}/events/${this.eventId}`);
                
                if (!eventResponse.ok) {
                    throw new Error(`HTTP錯誤: ${eventResponse.status}`);
                }
                
                const eventData = await eventResponse.json();
                
                if (eventData.status === 200) {
                    this.event = eventData.data;
                    
                    // 獲取票券類型
                    await this.fetchTicketTypes();
                } else {
                    throw new Error(eventData.userMessage || '無法獲取活動詳情');
                }
            } catch (err) {
                console.error('獲取活動資料時發生錯誤:', err);
                this.error = err.message || '網絡錯誤，請檢查您的網絡連接';
            } finally {
                this.loading = false;
            }
        },

        // 獲取票券類型資料
        async fetchTicketTypes() {
            try {
                const ticketResponse = await fetch(`${API_BASE_URL}/events/${this.eventId}/tickets`);
                
                if (!ticketResponse.ok) {
                    console.warn(`HTTP錯誤: ${ticketResponse.status}`);
                    this.ticketTypes = [];
                    return;
                }
                
                const ticketData = await ticketResponse.json();
                
                if (ticketData.status === 200) {
                    this.ticketTypes = ticketData.data;
                    
                    // 對票券類型按價格排序
                    this.ticketTypes.sort((a, b) => a.price - b.price);
                } else {
                    console.warn('無法獲取票券信息:', ticketData.errorMessage);
                    this.ticketTypes = [];
                }
            } catch (err) {
                console.error('獲取票券類型時發生錯誤:', err);
                this.ticketTypes = [];
            }
        },
        
        // 切換關注狀態
        async toggleFavorite() {
            // 防止重複點擊
            if (this.favoriteLoading) return;
            
            // 檢查用戶是否已登入
            if (!this.isLoggedIn) {
                if (confirm('請先登入才能關注活動。是否前往登入頁面？')) {
                    // 導向登入頁面，並設置回調頁面
                    window.location.href = `/maven-tickeasy-v1/login?redirect=${encodeURIComponent(window.location.href)}`;
                }
                return;
            }
            
            this.favoriteLoading = true;
            
            try {
                // 切換關注狀態 (1: 關注, 0: 取消關注)
                const newStatus = this.event.isFollowed === 1 ? 0 : 1;
                
                const response = await fetch(`${API_BASE_URL}/events/${this.eventId}/favorite?isFollowed=${newStatus}`, {
                    method: 'POST',
                    credentials: 'include',
                    headers: {
                        'Content-Type': 'application/json'
                    }
                });
                
                if (!response.ok) {
                    throw new Error(`HTTP錯誤: ${response.status}`);
                }
                
                const data = await response.json();
                
                if (data.status === 200) {
                    // 更新前端顯示
                    this.event.isFollowed = newStatus;
                    
                    // 顯示成功訊息（可以考慮使用更友好的通知方式）
                    const message = newStatus === 1 ? '已成功加入我的關注!' : '已取消關注';
                    alert(message);
                } else {
                    throw new Error(data.userMessage || '操作失敗，請稍後再試');
                }
            } catch (err) {
                console.error('切換關注狀態時發生錯誤:', err);
                alert(err.message || '網絡錯誤，請稍後再試');
            } finally {
                this.favoriteLoading = false;
            }
        },
        
        // 處理購票按鈕點擊
        handleBookingClick() {
            if (!this.hasAvailableTickets) return;
            
            // 檢查用戶是否已登入
            if (!this.isLoggedIn) {
                if (confirm('請先登入才能購票。是否前往登入頁面？')) {
                    // 導向登入頁面，並設置回調頁面
                    window.location.href = `/maven-tickeasy-v1/login?redirect=${encodeURIComponent(window.location.href)}`;
                }
                return;
            }
            
            // 導向購票頁面
            window.location.href = `/maven-tickeasy-v1/buy/booking.html?eventId=${this.eventId}`;
        },
        
        // 格式化日期 (不含時間)
        formatDate(dateString) {
            if (!dateString) return '';
            
            const date = new Date(dateString);
            const options = {
                year: 'numeric',
                month: '2-digit',
                day: '2-digit',
                hour: '2-digit',
                minute: '2-digit',
                hour12: false
            };
            
            return new Intl.DateTimeFormat('zh-TW', options).format(date);
        },
        
        // 格式化日期 (不含時間)
        formatDateWithoutTime(date) {
            if (!date) return '';
            
            const options = {
                year: 'numeric',
                month: '2-digit',
                day: '2-digit'
            };
            
            return new Intl.DateTimeFormat('zh-TW', options).format(date);
        },

        // 僅格式化時間
        formatTimeOnly(date) {
            if (!date) return '';
            
            const options = {
                hour: '2-digit',
                minute: '2-digit',
                hour12: false
            };
            
            return new Intl.DateTimeFormat('zh-TW', options).format(date);
        },
        
        // 格式化日期（短格式）
        formatDateShort(dateString) {
            if (!dateString) return '';
            
            const date = new Date(dateString);
            const options = {
                year: 'numeric',
                month: '2-digit',
                day: '2-digit'
            };
            
            return new Intl.DateTimeFormat('zh-TW', options).format(date);
        },
        
        // 格式化價格
        formatPrice(price) {
            return price.toLocaleString('zh-TW');
        },
        
        // 檢查用戶登錄狀態
        checkLoginStatus() {
            // 從 sessionStorage 或 cookie 中獲取用戶資訊
            try {
                // 檢查 session 中是否有 memberId
                const memberId = this.getCookie('memberId') || sessionStorage.getItem('memberId');
                
                if (memberId && !isNaN(parseInt(memberId))) {
                    this.isLoggedIn = true;
                    this.memberId = parseInt(memberId, 10);
                } else {
                    // 嘗試從後端檢查登錄狀態
                    this.checkServerLoginStatus();
                }
            } catch (err) {
                console.error('檢查登錄狀態時發生錯誤:', err);
                // 嘗試從後端檢查登錄狀態
                this.checkServerLoginStatus();
            }
        },

        // 從伺服器檢查登錄狀態
        async checkServerLoginStatus() {
            try {
                const response = await fetch(`${API_BASE_URL}/member/status`, {
                    credentials: 'include'
                });
                
                if (!response.ok) return;
                
                const data = await response.json();
                
                if (data.status === 200 && data.data.isLoggedIn) {
                    this.isLoggedIn = true;
                    this.memberId = data.data.memberId;
                    
                    // 保存到 sessionStorage 供其他頁面使用
                    sessionStorage.setItem('memberId', this.memberId);
                }
            } catch (err) {
                console.error('從伺服器檢查登錄狀態時發生錯誤:', err);
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
        
        // 判斷是否為有效日期
        isValidDate(dateString) {
            if (!dateString) return false;
            
            const date = new Date(dateString);
            return !isNaN(date.getTime());
        }
    },
    
    // 生命週期鉤子
    created() {
        // 從 URL 獲取活動 ID
        const urlParams = new URLSearchParams(window.location.search);
        this.eventId = urlParams.get('eventId');
        
        // 如果沒有提供活動 ID，使用預設值
        if (!this.eventId) {
            console.warn('未提供活動 ID，使用預設值');
            this.eventId = 1; // 使用預設 ID
        }
        
        // 檢查用戶登錄狀態
        this.checkLoginStatus();
    },
    
    // 在掛載後獲取資料
    mounted() {
        // 獲取活動資料
        this.fetchEventData();
        
        // 添加頁面標題
        document.title = 'TickEasy - 活動詳情';
    },
    
    // 在資料更新後更新頁面標題
    updated() {
        if (this.event) {
            document.title = `TickEasy - ${this.event.eventName}`;
        }
    }
});

// 掛載應用
app.mount('#app');