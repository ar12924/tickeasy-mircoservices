package manager.event.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import common.vo.Core;
import manager.event.service.EventService;
import manager.event.vo.MngEventInfo;

@RestController
@RequestMapping("manager")
@CrossOrigin(origins = { "http://127.0.0.1:5500", "http://127.0.0.1:5501", "http://127.0.0.1:8080",
		"http://localhost:5500", "http://localhost:5501", "http://localhost:8080" })
public class CreateEventController {

	@Autowired
	private EventService service;

	@PostMapping("create-event")
	public Core<Integer> createEvent(@RequestBody(required = false) MngEventInfo mngEventInfo) {
		System.out.println("Received: " + mngEventInfo);
		Core<Integer> core = new Core<>();

		try {
			// 1. 檢查是否有傳入資料
			if (mngEventInfo == null) {
				core.setSuccessful(false);
				core.setMessage("未提供任何事件資訊");
				core.setCount(0L);
				core.setData(null);
				return core;
			}

			// 2. 資料驗證
			String validationError = validateEventInfo(mngEventInfo);
			if (validationError != null) {
				core.setSuccessful(false);
				core.setMessage(validationError);
				core.setCount(0L);
				core.setData(null);
				return core;
			}

			// 3. 建立活動
			int result = service.createEvent(mngEventInfo);

			// 4. 檢查建立結果
			if (result > 0) {
				core.setSuccessful(true);
				core.setMessage("活動建立成功");
				core.setCount(1L);
				core.setData(result);
			} else {
				core.setSuccessful(false);
				core.setMessage("活動建立失敗");
				core.setCount(0L);
				core.setData(null);
			}

		} catch (Exception e) {
			// 5. 錯誤處理
			System.err.println("建立活動時發生錯誤: " + e.getMessage());
			e.printStackTrace();

			core.setSuccessful(false);
			core.setMessage("系統錯誤：" + e.getMessage());
			core.setCount(0L);
			core.setData(null);
		}

		return core;
	}

	@PostMapping("update-event")
	public Core<Integer> updateEvent(@RequestBody(required = false) MngEventInfo mngEventInfo) {
		System.out.println("Update received: " + mngEventInfo);
		Core<Integer> core = new Core<>();

		try {
			if (mngEventInfo == null) {
				core.setSuccessful(false);
				core.setMessage("未提供任何事件資訊");
				core.setCount(0L);
				core.setData(null);
				return core;
			}

			// 檢查活動ID
			if (mngEventInfo.getEventId() == null || mngEventInfo.getEventId() <= 0) {
				core.setSuccessful(false);
				core.setMessage("無效的活動ID");
				core.setCount(0L);
				core.setData(null);
				return core;
			}

			// 檢查活動是否存在
			MngEventInfo existingEvent = service.findEventById(mngEventInfo.getEventId());
			if (existingEvent == null) {
				core.setSuccessful(false);
				core.setMessage("找不到指定的活動");
				core.setCount(0L);
				core.setData(null);
				return core;
			}

			// 確保關聯資料存在
			if (mngEventInfo.getKeywordId() == null || mngEventInfo.getKeywordId() <= 0) {
				mngEventInfo.setKeywordId(existingEvent.getKeywordId());
			}

			if (mngEventInfo.getMemberId() == null || mngEventInfo.getMemberId() <= 0) {
				mngEventInfo.setMemberId(existingEvent.getMemberId());
			}

			// 資料驗證
			String validationError = validateEventInfo(mngEventInfo);
			if (validationError != null) {
				core.setSuccessful(false);
				core.setMessage(validationError);
				core.setCount(0L);
				core.setData(null);
				return core;
			}

			// 更新活動
			int result = service.updateEvent(mngEventInfo);

			if (result > 0) {
				core.setSuccessful(true);
				core.setMessage("活動更新成功");
				core.setCount(1L);
				core.setData(mngEventInfo.getEventId());
			} else {
				core.setSuccessful(false);
				core.setMessage("活動更新失敗");
				core.setCount(0L);
				core.setData(null);
			}

		} catch (Exception e) {
			System.err.println("更新活動時發生錯誤: " + e.getMessage());
			e.printStackTrace();

			core.setSuccessful(false);
			core.setMessage("系統錯誤：" + e.getMessage());
			core.setCount(0L);
			core.setData(null);
		}

		return core;
	}

	/**
	 * 驗證活動資訊
	 * 
	 * @param eventInfo 活動資訊
	 * @return 錯誤訊息，如果驗證通過則返回 null
	 */
	private String validateEventInfo(MngEventInfo eventInfo) {
		// 檢查活動名稱
		if (eventInfo.getEventName() == null || eventInfo.getEventName().trim().isEmpty()) {
			return "活動名稱不可為空";
		}
		if (eventInfo.getEventName().length() > 100) {
			return "活動名稱不可超過100字元";
		}

		// 檢查活動時間
		if (eventInfo.getEventFromDate() == null) {
			return "請填寫活動開始時間";
		}
		if (eventInfo.getEventToDate() == null) {
			return "請填寫活動結束時間";
		}
		if (eventInfo.getEventFromDate().compareTo(eventInfo.getEventToDate()) >= 0) {
			return "活動結束時間必須大於開始時間";
		}

		// 檢查活動地點
		if (eventInfo.getPlace() == null || eventInfo.getPlace().trim().isEmpty()) {
			return "活動地點不可為空";
		}

		// 檢查活動簡介
		if (eventInfo.getSummary() == null || eventInfo.getSummary().trim().isEmpty()) {
			return "活動簡介不可為空";
		}

		// 檢查人數上限（如果有設定的話）
		if (eventInfo.getTotalCapacity() < 0) {
			return "人數上限不可為負數";
		}

		// 檢查關鍵字ID
		if (eventInfo.getKeywordId() <= 0) {
			return "無效的活動分類";
		}

		// 檢查會員ID
		if (eventInfo.getMemberId() <= 0) {
			return "無效的會員ID";
		}

		return null; // 驗證通過
	}
}