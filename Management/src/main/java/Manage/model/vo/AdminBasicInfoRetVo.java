package Manage.model.vo;

import Core.model.VoObject;
import Manage.model.bo.Admin;
import lombok.Data;

import java.io.Serializable;

/**
 * @author snow create 2021/02/28 10:50
 */
@Data
public class AdminBasicInfoRetVo implements VoObject, Serializable {

    private Byte gender;
    private String email;
    private String mobile;
    private String userName;
    private String realName;

    public AdminBasicInfoRetVo(Admin admin){
        this.gender = admin.getGender();
        this.email = admin.getDecryptEmail();
        this.mobile = admin.getDecryptMobile();
        this.userName = admin.getUserName();
        this.realName = admin.getRealName();
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
