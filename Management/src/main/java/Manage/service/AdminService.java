package Manage.service;

import Core.util.*;
import Manage.dao.AdminDao;
import Manage.dao.UserDao;
import Manage.model.bo.Admin;
import Manage.model.bo.User;
import Manage.model.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @author snow create 2021/02/14 16:32
 */
@Service
public class AdminService {

    @Value("${CarlisleService.admin.jwtExpire}")
    private Integer jwtExpireTime;

    @Value("${CarlisleService.admin.departId}")
    private Long adminDepartId;

    private static final String verifyEmailTitle = "【卡莱尔国际公寓会员管理系统】邮箱验证通知";
    private static final String resetPasswordEmailTitle = "【卡莱尔国际公寓会员管理系统】重置密码通知";

    @Autowired
    private AdminDao adminDao;

    @Autowired
    private UserDao userDao;

    /**
     * 管理员登录
     * @author snow create 2021/01/19 00:28
     *            modified 2021/01/19 00:43
     * @param adminName
     * @param password
     * @return
     */
    public ReturnObject<String> adminLogin(String adminName, String password){
        ReturnObject retObj = adminDao.findAdminByUserName(adminName);
        if(retObj.getData() == null){
            return retObj;
        }
        Admin admin = (Admin) retObj.getData();
        if(admin.isSignatureBeenModify()){
            return new ReturnObject<>(ResponseCode.RESOURCE_FALSIFY);
        }
        if(admin.getEmailVerify() == (byte)0){
            return new ReturnObject<>(ResponseCode.EMAIL_NOT_VERIFIED);
        }
//        System.out.println("store: " + password + ", set: " + AES.decrypt(admin.getPassword(), User.AES_PASS));
        password = AES.encrypt(password, User.AES_PASS);
        if(admin == null || !password.equals(admin.getPassword())){
            return new ReturnObject<>(ResponseCode.AUTH_INVALID_ACCOUNT);
        }
        String jwt = new JwtHelper().createToken(admin.getId(), adminDepartId, jwtExpireTime);
        return new ReturnObject<>(jwt);
    }

    /**
     * 管理员获取本人信息
     * @author snow create 2021/02/28 10:55
     * @param adminId
     * @return
     */
    public ReturnObject getAdminInfo(Long adminId){
        ReturnObject<Admin> retObj = adminDao.findAdminById(adminId);
        if(retObj.getData() != null){
            return new ReturnObject(new AdminBasicInfoRetVo(retObj.getData()));
        }
        else{
            return retObj;
        }
    }

    /**
     * 管理员新建管理员
     * @author snow create 2021/01/23 18:44
     * @param adminVo
     * @return
     */
    public ReturnObject appendAdmin(UserVo adminVo){
        Admin admin = new Admin(adminVo);
        return adminDao.insertAdmin(admin);
    }

    /**
     * 管理员验证密码
     * @author snow create 2021/02/28 20:10
     * @param adminId
     * @param loginVo
     * @return
     */
    public ReturnObject adminVerifyPassword(Long adminId, UserLoginVo loginVo){
        ReturnObject<Admin> retObj = adminDao.findAdminById(adminId);
        if(retObj.getData() == null){
            return retObj;
        }
        Admin admin = retObj.getData();
        if(admin.getUserName().equals(loginVo.getUserName()) && admin.getPassword().equals(AES.encrypt(loginVo.getPassword(), User.AES_PASS))){
            //生成验证码
            String verifyCode = VerifyCode.generateVerifyCode(6);
            userDao.putVerifyCodeIntoRedis(verifyCode, admin.getId().toString());
            return new ReturnObject(verifyCode);
        }
        else{
            return new ReturnObject(ResponseCode.AUTH_USER_FORBIDDEN);
        }
    }

    /**
     * 管理员申请重置密码
     * @author snow create 2021/01/23 19:13
     * @param adminVo
     * @param ip
     * @return
     */
    public ReturnObject adminResetPassword(UserPasswordVo adminVo, String ip){
        ReturnObject<Admin> retObj = adminDao.findAdminByUserName(adminVo.getUserName());
        if(retObj.getData() == null){
            return retObj;
        }
        Admin admin = retObj.getData();
        if(!adminVo.getEmail().equals(admin.getDecryptEmail())){
            return new ReturnObject(ResponseCode.EMAIL_WRONG);
        }

        if(userDao.isAllowRequestForVerifyCode(ip)) {
            //生成验证码
            String verifyCode = VerifyCode.generateVerifyCode(6);
            userDao.putVerifyCodeIntoRedis(verifyCode, admin.getId().toString());
            String emailContent = "您正在【卡莱尔国际公寓会员管理系统】进行找回密码，您的验证码为：" + verifyCode + "，请于5分钟内完成验证！";
            sendVerifyCode(resetPasswordEmailTitle, emailContent, adminVo.getEmail());
            return new ReturnObject(ResponseCode.OK);
        }
        else{
            return new ReturnObject(ResponseCode.AUTH_USER_FORBIDDEN);
        }
    }

    /**
     * 管理员修改密码
     * @author snow create 2021/01/23 19:16
     * @param modifyPasswordVo
     * @return
     */
    public ReturnObject adminModifyPassword(UserModifyPasswordVo modifyPasswordVo){
        Long adminId = Long.valueOf(userDao.getUserIdByVerifyCode(modifyPasswordVo.getVerifyCode()));
        if(adminId == null){
            System.out.println("Can't find anything in redis with: " + modifyPasswordVo.getVerifyCode());
            return new ReturnObject(ResponseCode.VERIFY_CODE_EXPIRE);
        }
        ReturnObject retObj = adminDao.findAdminById(adminId);
        if(retObj.getData() == null){
            return retObj;
        }
        Admin admin = (Admin) retObj.getData();
        if(!modifyPasswordVo.getUserName().equals(admin.getUserName())){
            System.out.println("Pass: " + modifyPasswordVo.getUserName() + ", Store: " + admin.getUserName());
            return new ReturnObject(ResponseCode.VERIFY_CODE_EXPIRE);
        }
        String password = AES.encrypt(modifyPasswordVo.getPassword(), User.AES_PASS);
        if(password.equals(admin.getPassword())){
            return new ReturnObject(ResponseCode.PASSWORD_SAME);
        }
        admin.setPassword(password);
        admin.setSignature(admin.createSignature());
        userDao.disableVerifyCodeAfterSuccessfullyModifyPassword(modifyPasswordVo.getVerifyCode());
        return adminDao.updateAdminInformation(admin);
    }

    /**
     * 管理员修改基础信息
     * @author snow create 2021/01/23 19:18
     * @param adminId
     * @param userBasicInfoVo
     * @return
     */
    public ReturnObject adminModifyBasicInformation(Long adminId, UserBasicInfoVo userBasicInfoVo){
        if(userBasicInfoVo.getUserName() != null && adminDao.isUserNameAlreadyExist(userBasicInfoVo.getUserName())){
            return new ReturnObject(ResponseCode.ADMIN_NAME_EXIST);
        }
        if(userBasicInfoVo.getMobile() != null && adminDao.isMobileAlreadyExist(AES.encrypt(userBasicInfoVo.getMobile(), User.AES_PASS))){
            return new ReturnObject(ResponseCode.MOBILE_REGISTERED);
        }
        ReturnObject retObj = adminDao.findAdminById(adminId);
        if (retObj.getData() == null){
            return retObj;
        }
        Admin admin = (Admin) retObj.getData();
        admin.updateUserInfo(userBasicInfoVo);
        admin.setSignature(admin.createSignature());
        return adminDao.updateAdminInformation(admin);
    }

    /**
     * 管理员申请验证邮箱
     * @author snow create 2021/01/23 19:23
     * @param userName
     * @param ip
     * @return
     */
    public ReturnObject adminApplyVerifyEmail(String userName, String ip){
        ReturnObject<Admin> retObj = adminDao.findAdminByUserName(userName);
        if(retObj.getData() == null){
            return retObj;
        }
        Admin admin = retObj.getData();
        if(userDao.isAllowRequestForVerifyCode(ip)) {
            //生成验证码
            String verifyCode = VerifyCode.generateVerifyCode(6);
            userDao.putVerifyCodeIntoRedis(verifyCode, userName);
            String emailContent = "您正在【卡莱尔国际公寓会员管理系统】进行邮箱验证，您的验证码为：" + verifyCode + "，请于5分钟内完成验证！";
            sendVerifyCode(verifyEmailTitle, emailContent, admin.getDecryptEmail());
            return new ReturnObject(ResponseCode.OK);
        }
        else{
            return new ReturnObject(ResponseCode.AUTH_USER_FORBIDDEN);
        }
    }

    /**
     * 管理员验证邮箱
     * @param userName
     * @param verifyCode
     * @return
     */
    public ReturnObject adminVerifyEmail(String userName, String verifyCode){
        if(!userName.equals(userDao.getUserIdByVerifyCode(verifyCode))){
            System.out.println("Can't find anything in redis with: " + verifyCode);
            return new ReturnObject(ResponseCode.VERIFY_CODE_EXPIRE);
        }
        ReturnObject retObj = adminDao.findAdminByUserName(userName);
        if(retObj.getData() == null){
            return retObj;
        }
        Admin admin = (Admin)retObj.getData();
        admin.setEmailVerify((byte)1);
        retObj = adminDao.updateAdminInformation(admin);
        if(retObj.getCode() == ResponseCode.OK){
            String value = LocalDateTime.now().toString() + VerifyCode.generateVerifyCode(4);
            userDao.putEmailVerifyKeyIntoRedis(admin.getId(), value);
            return new ReturnObject<>(value);
        }
        else {
            return retObj;
        }
    }

    /**
     * 管理员修改邮箱
     * @author snow create 2021/01/23 19:26
     *            modified 2021/02/28 15:37
     * @param adminId
     * @param userVo
     * @return
     */
    public ReturnObject adminModifyEmail(Long adminId, AdminModifyEmailVo userVo){
        System.out.println(adminId);
        if(!userVo.getKey().equals(userDao.getEmailVerifyKey(adminId))){
            System.out.println("Can't find anything in redis with: " + adminId);
            return new ReturnObject(ResponseCode.VERIFY_CODE_EXPIRE);
        }
        String email = AES.encrypt(userVo.getEmail(), User.AES_PASS);
        System.out.println(email);
        if(adminDao.isEmailAlreadyExist(email)){
            return new ReturnObject(ResponseCode.EMAIL_REGISTERED);
        }
        ReturnObject<Admin> retObj= adminDao.findAdminById(adminId);
        if(retObj.getData() == null){
            return retObj;
        }
        Admin admin = retObj.getData();
        admin.setEmail(email);
        admin.setEmailVerify((byte)0);
        admin.setSignature(admin.createSignature());
        System.out.println(adminId.toString());
        return adminDao.updateAdminInformation(admin);
    }

    /**
     * 发送验证码
     * @author snow create 2021/01/17 22:52
     *            modified 2021/01/23 17:16
     * @param title
     * @param content
     * @param toEmailAddress
     * @return
     */
    public Boolean sendVerifyCode(String title, String content, String toEmailAddress){
        try{

            //发送邮件
            SendEmail.sendEmail(toEmailAddress, title, content);
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

}
