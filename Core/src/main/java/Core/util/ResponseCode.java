package Core.util;

/**
 * 返回的错误码
 * @author Ming Qiu
 */
public enum ResponseCode {
    OK(0,"成功"),
    /***************************************************
     *    系统级错误
     **************************************************/
    INTERNAL_SERVER_ERR(500,"服务器内部错误"),
    //所有需要登录才能访问的API都可能会返回以下错误
    AUTH_INVALID_JWT(501,"JWT不合法"),
    AUTH_JWT_EXPIRED(502,"JWT过期"),

    //以下错误码提示可以自行修改
    //--------------------------------------------
    FIELD_NOTVALID(503,"字段不合法"),
    //所有路径带id的API都可能返回此错误
    RESOURCE_ID_NOTEXIST(504,"操作的资源id不存在"),
    RESOURCE_ID_OUTSCOPE(505,"操作的资源id不是自己的对象"),
    FILE_NO_WRITE_PERMISSION(506,"目录文件夹没有写入的权限"),
    RESOURCE_FALSIFY(507, "信息签名不正确"),
    //--------------------------------------------


    /***************************************************
     *    会员模块错误码
     **************************************************/
    MEMBER_NAME_EMPTY(601,"会员姓名不能为空"),
    ADMIN_NAME_EXIST(602,"会员姓名已存在"),
    MEMBER_ID_EMPTY(611,"会员卡号不能为空"),
    MEMBER_NOT_IN_BIRTHDAY(620, "今日不是该会员生日"),

    /***************************************************
     *    权限模块错误码
     **************************************************/
    AUTH_INVALID_ACCOUNT(700, "用户名不存在或者密码错误"),
    AUTH_ID_NOT_EXIST(701,"登录用户id不存在"),
    AUTH_USER_FORBIDDEN(702,"用户被禁止登录"),
    AUTH_NEED_LOGIN(704, "需要先登录"),
    AUTH_NOT_ALLOW(705,"无权限访问"),
    EMAIL_REGISTERED(732, "邮箱已被注册"),
    MOBILE_REGISTERED(733,"电话已被注册"),
    PASSWORD_SAME(741,"新密码不能与旧密码相同"),
    EMAIL_WRONG(742,"与系统预留的邮箱不一致"),
    EMAIL_EMPTY(743,"预留的邮箱为空"),
    EMAIL_NOT_VERIFIED(744,"Email未确认"),
    MOBILE_WRONG(746,"与系统预留的电话不一致"),
    MOBILE_EMPTY(747,"预留的电话为空"),
    MOBILE_NOT_VERIFIED(748,"电话号码未确认"),
    VERIFY_CODE_EXPIRE(750, "验证码不正确或已过期");

    private int code;
    private String message;
    ResponseCode(int code, String message){
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage(){
        return message;
    }

}
