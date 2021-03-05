package Manage.model.bo;

import Core.util.AES;
import Manage.model.vo.UserBasicInfoVo;
import lombok.Data;

/**
 * @author snow create 2021/01/18 11:45
 */
@Data
public class User {
    public static String AES_PASS = "Carlisle2021/02/14";

    protected Long id;
    protected Byte gender;
    protected String email;
    protected String mobile;
    protected String userName;
    protected String password;
    protected String realName;

    public String getDecryptEmail(){
        if(this.email != null) {
            return AES.decrypt(this.email, AES_PASS);
        }
        return null;
    }

    public String getDecryptMobile(){
        if(this.mobile != null) {
            return AES.decrypt(this.mobile, AES_PASS);
        }
        return null;
    }

//    public String getGender(){
//        if(this.gender == (byte)0){
//            return "女";
//        }
//        else if(this.gender == (byte)1){
//            return "男";
//        }
//        return null;
//    }
//
//    public void setGender(String gender){
//        if(gender.equals("男")){
//            this.gender = (byte)1;
//        }
//        else if(gender.equals("女")){
//            this.gender = (byte)0;
//        }
//    }

    /**
     * 通过userBasicInfo中非空的属性更新属性值
     * @author snow create 2021/01/23 13:53
     * @param userBasicInfoVo
     */
    public void updateUserInfo(UserBasicInfoVo userBasicInfoVo){
        if(userBasicInfoVo.getUserName() != null){
            this.userName = userBasicInfoVo.getUserName();
        }
        if(userBasicInfoVo.getMobile() != null){
            this.mobile = AES.encrypt(userBasicInfoVo.getMobile(), AES_PASS);
        }
        if(userBasicInfoVo.getGender() != null){
            setGender(userBasicInfoVo.getGender());
        }
        if(userBasicInfoVo.getRealName() != null){
            this.realName = userBasicInfoVo.getRealName();
        }
    }
}
