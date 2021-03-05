package Manage.service;

import Core.model.VoObject;
import Core.util.ResponseCode;
import Core.util.ReturnObject;
import Core.util.SendEmail;
import Manage.dao.MemberDao;
import Manage.model.bo.Member;
import Manage.model.bo.MemberNameList;
import Manage.model.po.MemberPo;
import Manage.model.vo.MemberVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MemberService {

    @Autowired
    private MemberDao memberDao;

    /**
     * 管理员新增会员
     * @author snow create 2021/02/14 22:05
     * @param adminId
     * @param memberVo
     * @return
     */
    public ReturnObject addMember(Long adminId, MemberVo memberVo){
        System.out.println("Service");
        Member member = new Member(memberVo);
        System.out.println(member.toString());
        member.setCreatorId(adminId);
        return memberDao.insertMember(member);
    }

    /**
     * 管理员删除会员
     * @author snow create 2021/02/14 22:06
     * @param memberId
     * @return
     */
    public ReturnObject removeMember(Long memberId){
        return memberDao.deleteMember(memberId);
    }

    /**
     * 管理员修改会员信息
     * @author snow create 2021/02/14 22:09
     * @param memberId
     * @param memberVo
     * @return
     */
    public ReturnObject modifyMemberInfo(Long memberId, MemberVo memberVo){
        Member member = new Member(memberVo);
        member.setId(memberId);
        return memberDao.updateMemberInfo(member);
    }

    /**
     * 管理员查看会员信息
     * @author snow create 2021/02/14 23:00
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
    public ReturnObject findMemberInfoList(Long memberId, String name,
                                           Byte gender, String email,
                                           String mobile, String identityId,
                                           Integer page, Integer pageSize){
        PageHelper.startPage(page, pageSize);
        PageInfo<MemberPo> topicPos = memberDao.getMemberInfoList(memberId, name, gender, email, mobile, identityId);
        List<VoObject> topicList = topicPos.getList().stream().map(Member::new).filter(Member::authentic).collect(Collectors.toList());

        PageInfo<VoObject> retObj = new PageInfo<>(topicList);
        retObj.setPages(topicPos.getPages());
        retObj.setPageNum(topicPos.getPageNum());
        retObj.setPageSize(topicPos.getPageSize());
        retObj.setTotal(topicPos.getTotal());

        return new ReturnObject<>(retObj);

    }

    /**
     * 管理员根据月份查看当月所有生日会员的姓名
     * @author snow create 2021/02/17 21:49
     * @param birthMonth
     * @return
     */
    public ReturnObject findBirthdayMemberNamesByMonth(Byte birthMonth){
        Map<Byte, ArrayList<String>> memberNames = memberDao.getBirthdayMemberNameByMonth(birthMonth);
        if(memberNames == null){
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR);
        }
        else{
            return new ReturnObject(new MemberNameList(memberNames));
        }

    }

    /**
     * 返回当天所有生日会员的姓名与联系方式
     * @author snow create 2021/02/24 17:45
     * @return
     */
    public ReturnObject findBirthdayMemberNamesToday(){
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
        String[] today = sdf.format(new Date()).split("-");
        byte currentMonth = Byte.parseByte(today[0]);
        byte currentDay = Byte.parseByte(today[1]);
        return memberDao.getBirthdayMemberNameByDate(currentMonth, currentDay);
    }

    /**
     * 管理员给生日会员发送邮件祝福
     * @author snow create 2021/02/24 18:43
     * @param id
     * @return
     */
    public ReturnObject informBirthdayMemberByEmail(Long id){
        ReturnObject<Member> retObj = memberDao.getMemberById(id);
        if(retObj.getData() != null){
            Member member = retObj.getData();
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
            String[] today = sdf.format(new Date()).split("-");
            byte currentMonth = Byte.parseByte(today[0]);
            byte currentDay = Byte.parseByte(today[1]);
            if(member.getBirthMonth() != currentMonth || member.getBirthday() != currentDay){
                return new ReturnObject(ResponseCode.MEMBER_NOT_IN_BIRTHDAY);
            }
            if (member.getDecryptEmail() != null){
                if(sendBirthdayBlessing((member.getRealName() == null?"尊敬的卡莱尔会员":member.getRealName()), member.getDecryptEmail())){
                    return new ReturnObject(ResponseCode.OK);
                }
                else {
                    return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR);
                }
            }
            else{
                return new ReturnObject(ResponseCode.EMAIL_EMPTY);
            }
        }
        else{
            return retObj;
        }
    }

    /**
     * 发送邮件
     * @author snow create 2021/02/24 18:31
     * @param name
     * @param toEmailAddress
     * @return
     */
    public Boolean sendBirthdayBlessing(String name, String toEmailAddress){
        try{
            //发送邮件
            String title = "生日快乐";
            String content = "Hi, " + name + ".\n" +
                    "  有些事情可能你已经忘记，但我们依然记得。今天是你的生日，卡莱尔祝你生日快乐！Happy Birthday!";
            SendEmail.sendEmail(toEmailAddress, title, content);
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

}
