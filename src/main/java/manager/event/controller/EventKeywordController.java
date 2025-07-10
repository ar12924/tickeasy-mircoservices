package manager.event.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import common.vo.Core;
import manager.event.service.EventService;
import manager.event.vo.MngKeywordCategory;

@RestController
@RequestMapping("manager/eventkeyword")
@CrossOrigin(origins = { 
	    "http://127.0.0.1:5500", 
	    "http://127.0.0.1:5501", 
	    "http://127.0.0.1:8080",
	    "http://localhost:5500", 
	    "http://localhost:5501", 
	    "http://localhost:8080"
	})
public class EventKeywordController {
    
    @Autowired
    private EventService eventService;
    
    @PostMapping
    public Core<Integer> createKeywordCategory(@RequestBody(required = false) MngKeywordCategory mCategory) {
        System.out.println("Received keyword category: " + mCategory);
        Core<Integer> core = new Core<>();
        
        try {
            if (mCategory == null) {
                core.setSuccessful(false);
                core.setMessage("請提供分類資料");
                core.setData(null);
                core.setCount(0L);
                return core;
            }
            
            Integer keywordId = eventService.createKeywordCategory(mCategory);
            
            if (keywordId != null && keywordId > 0) {
                core.setSuccessful(true);
                core.setMessage("分類建立成功");
                core.setData(keywordId);  // 確保這裡是 Integer，不是陣列
                core.setCount(1L);
            } else {
                core.setSuccessful(false);
                core.setMessage("分類建立失敗");
                core.setData(null);
                core.setCount(0L);
            }
            
        } catch (Exception e) {
            System.err.println("建立關鍵字分類時發生錯誤: " + e.getMessage());
            e.printStackTrace();
            
            core.setSuccessful(false);
            core.setMessage("系統錯誤：" + e.getMessage());
            core.setData(null);
            core.setCount(0L);
        }
        
        return core;
    }
    
    /**
     * 檢查是否所有關鍵字都是空的
     */
    private boolean isAllKeywordsEmpty(MngKeywordCategory category) {
        return (category.getKeywordName1() == null || category.getKeywordName1().trim().isEmpty()) &&
               (category.getKeywordName2() == null || category.getKeywordName2().trim().isEmpty()) &&
               (category.getKeywordName3() == null || category.getKeywordName3().trim().isEmpty());
    }
}