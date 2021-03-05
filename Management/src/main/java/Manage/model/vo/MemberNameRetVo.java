package Manage.model.vo;

import lombok.Data;

import java.util.ArrayList;

@Data
public class MemberNameRetVo {
    private Byte day;
    private ArrayList<String> memberNames;
}
