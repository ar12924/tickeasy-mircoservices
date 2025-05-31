package test.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Student {
    public enum Gender {
        MALE, FEMALE
    }

    private String id;
    private String name;
    private Gender gender;
    private int grade;

//    private int typeCount;   // 票券數量
//    private String typeName; // 票券類型名稱
//    private int typePrice;   // 票券單價
}
