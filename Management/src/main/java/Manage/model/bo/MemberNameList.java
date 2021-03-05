package Manage.model.bo;

import Core.model.VoObject;
import Manage.model.vo.MemberNameRetVo;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

@Data
public class MemberNameList implements VoObject, Serializable {

    private ArrayList<MemberNameRetVo> memberNames;

    public MemberNameList(Map<Byte, ArrayList<String>> members){
        memberNames = new ArrayList<>();
        for (Byte i : members.keySet()){
            MemberNameRetVo temp = new MemberNameRetVo();
            temp.setDay(i);
            temp.setMemberNames(members.get(i));
            memberNames.add(temp);
        }
    }

    @Override
    public Object createVo() {
        return this;
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }
}
