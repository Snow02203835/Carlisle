package Manage.model.bo;

import Core.model.VoObject;
import Manage.model.vo.MemberContactRetVo;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class MemberContactList implements VoObject, Serializable {

    private List<MemberContactRetVo> memberContacts;

    public MemberContactList() {
        memberContacts = new ArrayList<>();
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
