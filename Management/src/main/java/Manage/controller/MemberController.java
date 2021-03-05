package Manage.controller;

import Core.annotation.Audit;
import Core.annotation.LoginUser;
import Core.util.Common;
import Core.util.ResponseCode;
import Core.util.ReturnObject;
import Manage.model.vo.MemberVo;
import Manage.service.MemberService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;

/**
 * @author snow create 2021/02/14 16:30
 */
@Api(value = "会员管理", tags = "member")
@RestController
@RequestMapping(value = "/members", produces = "application/json;charset=UTF-8")
public class MemberController {

    @Autowired
    private HttpServletResponse httpServletResponse;

    @Autowired
    private MemberService memberService;

    /**
     * 管理员新增会员
     * @author snow create 2021/02/14 22:20
     * @param adminId
     * @param memberVo
     * @return
     */
    @ApiOperation(value = "管理员新增会员", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "token", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "MemberVo", name = "memberVo", value = "会员信息", required = true),

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 601, message = "会员姓名不能为空"),
            @ApiResponse(code = 611, message = "会员卡号不能为空"),
    })
    @Audit
    @PostMapping("")
    public Object addMember(@LoginUser @ApiIgnore Long adminId,
                             @RequestBody MemberVo memberVo){
        if(memberVo.getMemberId() == null){
            httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            return new ReturnObject(ResponseCode.MEMBER_ID_EMPTY);
        }
        if(memberVo.getName() == null){
            httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            return new ReturnObject(ResponseCode.MEMBER_NAME_EMPTY);
        }
        System.out.println(adminId);
        System.out.println(memberVo.toString());

        ReturnObject retObj = memberService.addMember(adminId, memberVo);

        if(retObj.getData() != null){
            return Common.getRetObject(retObj);
        }else{
            httpServletResponse.setStatus(HttpStatus.CREATED.value());
            return Common.decorateReturnObject(retObj);
        }
    }

    /**
     * 管理员删除会员
     * @author snow create 2021/02/14 22:28
     * @param id
     * @return
     */
    @ApiOperation(value = "管理员删除会员", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "id", value = "会员id", required = true),

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @DeleteMapping("{id}")
    public Object deleteMember(@PathVariable Long id){
        return Common.decorateReturnObject(memberService.removeMember(id));
    }

    /**
     * 管理员修改会员信息
     * @author snow create 2021/02/14 22:30
     * @param id
     * @param memberVo
     * @return
     */
    @ApiOperation(value = "管理员修改会员信息", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "token", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "MemberVo", name = "memberVo", value = "会员信息", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "id", value = "会员id", required = true),

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @PutMapping("{id}")
    public Object modifyMember(@PathVariable Long id, @RequestBody MemberVo memberVo){
//        System.out.println(memberVo.toString());
        return Common.decorateReturnObject(memberService.modifyMemberInfo(id, memberVo));
    }

    /**
     * 管理员获取会员列表
     * @author snow create 2021/02/14 22:40
     * @param memberId
     * @param name
     * @param gender
     * @param email
     * @param mobile
     * @param identityId
     * @param page
     * @param pageSize
     * @return
     */
    @ApiOperation(value = "管理员获取会员列表", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "token", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "memberId", value = "会员卡号", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "name", value = "会员姓名", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "gender", value = "性别", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "email", value = "邮箱", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "mobile", value = "电话号码", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "identityId", value = "身份证", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "page", value = "页码", defaultValue = "1", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "pageSize", value = "页大小", defaultValue = "20", required = true),

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @GetMapping("")
    public Object findMember(@RequestParam(required = false) Long memberId,
                             @RequestParam(required = false) String name,
                             @RequestParam(required = false) Byte gender,
                             @RequestParam(required = false) String email,
                             @RequestParam(required = false) String mobile,
                             @RequestParam(required = false) String identityId,
                             @RequestParam(defaultValue = "1") Integer page,
                             @RequestParam(defaultValue = "20") Integer pageSize){
        return Common.getPageRetObject(memberService.findMemberInfoList(memberId, name, gender,
                email, mobile, identityId, page, pageSize));
    }

    /**
     * 管理员获取某月生日会员姓名
     * @author snow create 2021/02/17 21:51
     * @param month
     * @return
     */
    @ApiOperation(value = "管理员获取某月生日会员姓名", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "month", value = "出生月份", required = true),

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @GetMapping("{month}")
    public Object getBirthdayMemberNameByMonth(@PathVariable Byte month){
        System.out.println("CMonth: " + month);
        ReturnObject retObj = memberService.findBirthdayMemberNamesByMonth(month);
        if(retObj.getData() == null){
            return Common.decorateReturnObject(retObj);
        }
        else{
            return Common.getRetObject(retObj);
        }
    }

    /**
     * 管理员获取当天生日会员姓名与联系信息
     * @author snow create 2021/02/24 17:50
     * @return
     */
    @ApiOperation(value = "管理员获取当天生日会员姓名与联系信息", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "token", required = true),

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @GetMapping("contact")
    public Object getBirthdayMemberNameToday(){
        ReturnObject retObj = memberService.findBirthdayMemberNamesToday();
        if(retObj.getData() == null){
            return Common.decorateReturnObject(retObj);
        }
        else{
            return Common.getRetObject(retObj);
        }
    }

    /**
     * 管理员给生日会员发送邮件祝福
     * @author snow create 2021/02/24 18:45
     * @param id
     * @return
     */
    @ApiOperation(value = "管理员给生日会员发送邮件祝福", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "id", value = "ID", required = true),

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 743, message = "预留的邮箱为空"),
            @ApiResponse(code = 620, message = "今日不是该会员生日"),
    })
    @Audit
    @GetMapping("birthday/email/{id}")
    public Object informBirthdayMemberByEmail(@PathVariable Long id){
        return Common.decorateReturnObject(memberService.informBirthdayMemberByEmail(id));
    }
}
