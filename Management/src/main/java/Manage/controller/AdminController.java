package Manage.controller;

import Core.annotation.Audit;
import Core.annotation.Depart;
import Core.annotation.LoginUser;
import Core.util.*;
import Manage.model.vo.*;
import Manage.service.AdminService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author snow create 2021/02/14 16:30
 */
@Api(value = "", tags = "admin")
@RestController
@RequestMapping(value = "/admin", produces = "application/json;charset=UTF-8")
public class AdminController {

    @Autowired
    private HttpServletResponse httpServletResponse;

    @Autowired
    private AdminService adminService;

    /**
     * 管理员登录
     * @author snow create 2021/01/19 00:11
     *            modified 2021/01/19 00:45
     * @param loginVo
     * @param bindingResult
     * @param httpServletResponse
     * @return
     */
    @ApiOperation(value = "管理员登录", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "body", dataType = "UserLoginVo", name = "loginVo", value = "管理员号与密码", required = true),

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 507, message = "信息签名不正确"),
            @ApiResponse(code = 700, message = "用户名不存在或者密码错误"),
            @ApiResponse(code = 744, message = "Email未确认"),
    })
    @PostMapping("login")
    public Object adminLogin(@Validated @RequestBody UserLoginVo loginVo,
                             BindingResult bindingResult,
                             HttpServletResponse httpServletResponse){

        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if(returnObject != null){
            return returnObject;
        }

        ReturnObject<String> jwt = adminService.adminLogin(loginVo.getUserName(), loginVo.getPassword());

        if(jwt.getData() == null){
            return ResponseUtil.fail(jwt.getCode(), jwt.getErrmsg());
        }else{
            httpServletResponse.setStatus(HttpStatus.CREATED.value());
            return ResponseUtil.ok(jwt.getData());
        }
    }

    /**
     * 管理员获取本人资料
     * @author snow create 2021/02/28 10:58
     * @param adminId
     * @return
     */
    @ApiOperation(value = "管理员获取本人资料", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "token", required = true),

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 700, message = "用户名不存在或者密码错误"),
    })
    @Audit
    @GetMapping("")
    public Object getAdminInfo(@ApiIgnore @LoginUser Long adminId){

        return Common.decorateReturnObject(adminService.getAdminInfo(adminId));
    }

    /**
     * 管理员新建管理员
     * @author snow create 2021/01/19 00:45
     * @param userVo
     * @param bindingResult
     * @param httpServletResponse
     * @return
     */
    @ApiOperation(value = "管理员新建管理员", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "token", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "UserVo", name = "userVo", value = "管理员信息", required = true),

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功", response = AdminRetVo.class),
            @ApiResponse(code = 602, message = "会员姓名已存在"),
            @ApiResponse(code = 732, message = "邮箱已被注册"),
            @ApiResponse(code = 733, message = "电话已被注册"),
    })
    @Audit
    @PostMapping("")
    public Object appendAdmin(@Validated @RequestBody UserVo userVo,
                              BindingResult bindingResult,
                              HttpServletResponse httpServletResponse){

        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if(returnObject != null){
            return returnObject;
        }

        ReturnObject retObj = adminService.appendAdmin(userVo);

        if(retObj.getData() == null){
            return Common.getNullRetObj(retObj, httpServletResponse);
        }else{
            httpServletResponse.setStatus(HttpStatus.CREATED.value());
            return Common.getRetObject(retObj);
        }
    }

    /**
     * 管理员验证密码
     * @author snow create 2021/02/28 20:04
     * @param adminId
     * @param loginVo
     * @param bindingResult
     * @return
     */
    @ApiOperation(value = "管理员验证密码", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "token", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "UserLoginVo", name = "loginVo", value = "管理员验证密码", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 702, message = "用户被禁止登录"),
    })
    @Audit
    @PutMapping("password/verify")
    public Object adminVerifyPassword(@LoginUser @ApiIgnore Long adminId,
                                      @Validated @RequestBody UserLoginVo loginVo,
                                     BindingResult bindingResult){
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if(returnObject != null){
            return returnObject;
        }
        ReturnObject retObj = adminService.adminVerifyPassword(adminId, loginVo);
        if(retObj.getData() == null){
            return ResponseUtil.fail(retObj.getCode(), retObj.getErrmsg());
        }else{
            return ResponseUtil.ok(retObj.getData());
        }
    }

    /**
     * 管理员找回密码
     * @author snow create 2021/01/23 19:29
     * @param adminVo
     * @param bindingResult
     * @return
     */
    @ApiOperation(value = "管理员找回密码", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "body", dataType = "UserPasswordVo", name = "adminVo", value = "管理员验证身份信息", required = true),

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 702, message = "用户被禁止登录"),
            @ApiResponse(code = 742, message = "与系统预留的邮箱不一致"),
    })
    @PutMapping("password/reset")
    public Object adminResetPassword(@Validated @RequestBody UserPasswordVo adminVo,
                                     BindingResult bindingResult,
                                     HttpServletRequest httpServletRequest){
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if(returnObject != null){
            return returnObject;
        }

        String ip = IpUtil.getIpAddr(httpServletRequest);
        return Common.decorateReturnObject(adminService.adminResetPassword(adminVo, ip));
    }

    /**
     * 管理员修改密码
     * @author snow create 2021/01/23 19:30
     * @param modifyPasswordVo
     * @param bindingResult
     * @return
     */
    @ApiOperation(value = "管理员修改密码", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "body", dataType = "UserModifyPasswordVo", name = "modifyPasswordVo", value = "修改密码对象", required = true),

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 700, message = "用户名不存在或者密码错误"),
            @ApiResponse(code = 741, message = "新密码不能与旧密码相同"),
            @ApiResponse(code = 750, message = "验证码不正确或已过期"),
    })
    @PutMapping("password")
    public Object adminModifyPassword(@Validated @RequestBody UserModifyPasswordVo modifyPasswordVo,
                                      BindingResult bindingResult){
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if(returnObject != null){
            return returnObject;
        }

        return Common.decorateReturnObject(adminService.adminModifyPassword(modifyPasswordVo));
    }

    /**
     * 管理员修改基本信息
     * @author snow create 2021/01/23 14:11
     * @param adminId
     * @param userBasicInfoVo
     * @param bindingResult
     * @return
     */
    @ApiOperation(value = "管理员修改基本信息", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "token", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "UserBasicInfoVo", name = "userBasicInfoVo", value = "修改信息对象", required = true),

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 700, message = "用户名不存在或者密码错误"),
            @ApiResponse(code = 733, message = "电话已被注册"),
            @ApiResponse(code = 735, message = "管理员号已被注册"),
    })
    @Audit
    @PutMapping("")
    public Object adminModifyBasicInformation(@ApiIgnore @LoginUser Long adminId,
                                              @Validated @RequestBody UserBasicInfoVo userBasicInfoVo,
                                              BindingResult bindingResult){
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if(returnObject != null){
            return returnObject;
        }

        return Common.decorateReturnObject(adminService.adminModifyBasicInformation(adminId, userBasicInfoVo));
    }

    /**
     * 管理员申请验证邮箱
     * @author snow create 2021/01/23 19:34
     * @param loginVo
     * @param httpServletRequest
     * @return
     */
    @ApiOperation(value = "管理员验证邮箱", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "body", dataType = "UserLoginVo", name = "loginVo", value = "token", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 700, message = "用户名不存在或者密码错误"),
    })
    @PutMapping("email/apply")
    public Object adminApplyVerifyEmail(@RequestBody UserLoginVo loginVo, HttpServletRequest httpServletRequest){
        if(loginVo.getUserName() == null){
            return Common.getNullRetObj(new ReturnObject(ResponseCode.FIELD_NOTVALID), httpServletResponse);
        }
        String ip = IpUtil.getIpAddr(httpServletRequest);

        return Common.decorateReturnObject(adminService.adminApplyVerifyEmail(loginVo.getUserName(), ip));
    }

    /**
     * 管理员验证邮箱
     * @author snow create 2021/02/28 15：12
     * @param verifyEmailVo
     * @param bindingResult
     * @return
     */
    @ApiOperation(value = "管理员验证邮箱", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "body", dataType = "AdminVerifyEmailVo", name = "verifyEmailVo", value = "验证码对象", required = true),

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 750, message = "验证码不正确或已过期"),
    })
    @PutMapping("email/verify")
    public Object adminVerifyEmail(@Validated @RequestBody AdminVerifyEmailVo verifyEmailVo,
                                   BindingResult bindingResult){
        System.out.println(verifyEmailVo.toString());
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if(returnObject != null){
            return returnObject;
        }

        ReturnObject retObj = adminService.adminVerifyEmail(verifyEmailVo.getUserName(), verifyEmailVo.getVerifyCode());

        if(retObj.getData() == null){
            return ResponseUtil.fail(retObj.getCode(), retObj.getErrmsg());
        }else{
            return ResponseUtil.ok(retObj.getData());
        }
    }

    /**
     * 管理员修改邮箱
     * @author snow create 2021/01/23 16:58
     * @param userVo
     * @param bindingResult
     * @return
     */
    @ApiOperation(value = "管理员修改邮箱", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "body", dataType = "UserModifyEmailVo", name = "userVo", value = "修改邮箱对象", required = true),

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 700, message = "用户名不存在或者密码错误"),
            @ApiResponse(code = 732, message = "邮箱已被注册"),
            @ApiResponse(code = 750, message = "验证码不正确或已过期"),
    })
    @Audit
    @PutMapping("email")
    public Object adminModifyEmail(@LoginUser @ApiIgnore Long adminId, @Validated @RequestBody AdminModifyEmailVo userVo,
                                   BindingResult bindingResult){
        System.out.println(userVo.toString());
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if(returnObject != null){
            return returnObject;
        }

        return Common.decorateReturnObject(adminService.adminModifyEmail(adminId, userVo));
    }
}
