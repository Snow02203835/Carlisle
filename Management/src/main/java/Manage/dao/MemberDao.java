package Manage.dao;

import Core.util.ResponseCode;
import Core.util.ReturnObject;
import Manage.mapper.MemberPoMapper;
import Manage.model.bo.Member;
import Manage.model.bo.MemberContactList;
import Manage.model.po.MemberPo;
import Manage.model.po.MemberPoExample;
import Manage.model.vo.MemberContactRetVo;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author snow create 2021/02/14 21:06
 */
@Repository
public class MemberDao {

    @Autowired
    private MemberPoMapper memberPoMapper;

    private static final byte[] totalDays = {0, 31, 0, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    /**
     * 管理员插入会员信息
     * @author snow create 2021/02/14 21:37
     * @param member
     * @return
     */
    public ReturnObject<Member> insertMember(Member member){
        try{
            MemberPo memberPo = member.createMemberPo();
            memberPo.setGmtCreate(LocalDateTime.now());
            int result = memberPoMapper.insert(memberPo);
            if(result == 1){
                member.setId(memberPo.getId());
                return new ReturnObject(member);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR);
    }

    /**
     * 管理员删除会员信息
     * @author snow create 2021/02/14 21:39
     * @param memberId
     * @return
     */
    public ReturnObject deleteMember(Long memberId){
        try{
            int result = memberPoMapper.deleteByPrimaryKey(memberId);
            if(result == 1){
                return new ReturnObject(ResponseCode.OK);
            }
            else{
                return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR);
    }

    /**
     * 管理员修改会员信息
     * @author snow create 2021/02/14 21:42
     * @param member
     * @return
     */
    public ReturnObject updateMemberInfo(Member member){
        try{
            MemberPo memberPo = member.createMemberPo();
            memberPo.setGmtModified(LocalDateTime.now());
            int result = memberPoMapper.updateByPrimaryKeySelective(memberPo);
            if(result == 1){
//                System.out.println("更新成功: " + memberPo.toString());
                return new ReturnObject(ResponseCode.OK);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR);
    }

    /**
     * 管理员根据id获取会员信息
     * @author snow create 2021/02/24 18:24
     * @param id
     * @return
     */
    public ReturnObject<Member> getMemberById(Long id){
        try {
            MemberPo memberPo = memberPoMapper.selectByPrimaryKey(id);
            if (memberPo != null){
                return new ReturnObject<>(new Member(memberPo));
            }
            else{
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
    }

    /**
     * 管理员查看会员列表
     * @author snow create 2021/02/14 21:52
     * @param memberId
     * @param name
     * @param gender
     * @param email
     * @param mobile
     * @param identityId
     * @return
     */
    public PageInfo<MemberPo> getMemberInfoList(Long memberId, String name, Byte gender,
                                                String email, String mobile, String identityId){
        try {
            MemberPoExample example = new MemberPoExample();
            MemberPoExample.Criteria criteria = example.createCriteria();
            if(memberId != null) {
                criteria.andMemberIdEqualTo(memberId);
            }
            if(name != null){
                criteria.andNameEqualTo(name);
            }
            if(gender != null){
                criteria.andGenderEqualTo(gender);
            }
            if(email != null){
                criteria.andEmailEqualTo(email);
            }
            if(mobile != null){
                criteria.andMobileEqualTo(mobile);
            }
            if(identityId != null){
                criteria.andIdentityIdEqualTo(identityId);
            }
            List<MemberPo> topicPos = memberPoMapper.selectByExample(example);
            return new PageInfo<>(topicPos);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据月份返回当月所有生日会员的姓名
     * @author snow create 2021/02/17 21:38
     * @param birthMonth
     * @return
     */
    public Map<Byte, ArrayList<String>> getBirthdayMemberNameByMonth(Byte birthMonth){
        try {
            Byte totalDay = 0;
            if(birthMonth == 2){
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
                long currentYear = Long.parseLong(sdf.format(new Date()));
                totalDay = ((currentYear%4 == 0 && currentYear % 100 != 0) || currentYear%400 == 0) ? (byte)29 : (byte)28;
            }
            else{
                totalDay = totalDays[birthMonth];
            }
//            System.out.println("Total: " + totalDay);
            MemberPoExample example = new MemberPoExample();
            MemberPoExample.Criteria criteria = example.createCriteria();
            criteria.andBirthMonthEqualTo(birthMonth);
            criteria.andBirthdayLessThanOrEqualTo(totalDay);
            List<MemberPo> memberPos = memberPoMapper.selectByExample(example);
//            System.out.println(memberPos);
//            System.out.println(memberPos.size());
            Map<Byte, ArrayList<String>> memberNameMap = new HashMap<>();
            if(memberPos != null && memberPos.size() != 0){
//                System.out.println(memberPos.toString());
                for (MemberPo memberPo : memberPos){
                    if(memberPo.getBirthday() != null){
                        if(memberNameMap.get(memberPo.getBirthday()) == null){
                            ArrayList<String> temp = new ArrayList<>();
                            memberNameMap.put(memberPo.getBirthday(), temp);
                        }
                        memberNameMap.get(memberPo.getBirthday()).add(memberPo.getName());
                    }
                }
//                System.out.println(memberNameMap.toString());
            }
            return memberNameMap;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据月份返回当月所有生日会员的姓名
     * @author snow create 2021/02/24 16:48
     * @param month
     * @param day
     * @return
     */
    public ReturnObject<MemberContactList> getBirthdayMemberNameByDate(Byte month, Byte day){
        try {
            MemberPoExample example = new MemberPoExample();
            MemberPoExample.Criteria criteria = example.createCriteria();
            criteria.andBirthMonthEqualTo(month);
            criteria.andBirthdayEqualTo(day);
            List<MemberPo> memberPos = memberPoMapper.selectByExample(example);
            if(memberPos != null && memberPos.size() != 0){
                MemberContactList memberContactList = new MemberContactList();
                for (MemberPo memberPo : memberPos){
                    MemberContactRetVo memberContactRetVo = new MemberContactRetVo(memberPo);
                    memberContactList.getMemberContacts().add(memberContactRetVo);
                }
                return new ReturnObject<>(memberContactList);
            }
            else{
                return new ReturnObject<>(ResponseCode.OK);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
    }

    /**
     * 判断邮箱是否已重复
     * @author snow create 2021/02/14 22:03
     * @param email
     * @return
     */
    private Boolean isEmailExist(String email){
        MemberPoExample example = new MemberPoExample();
        MemberPoExample.Criteria criteria = example.createCriteria();
        criteria.andEmailEqualTo(email);
        List<MemberPo> memberPos = memberPoMapper.selectByExample(example);
        if(memberPos == null || memberPos.size() == 0){
            return false;
        }
        return true;
    }

    /**
     * 判断电话号码是否已重复
     * @author snow create 2021/02/14 22:04
     * @param mobile
     * @return
     */
    private Boolean isMobileExist(String mobile){
        MemberPoExample example = new MemberPoExample();
        MemberPoExample.Criteria criteria = example.createCriteria();
        criteria.andMobileEqualTo(mobile);
        List<MemberPo> memberPos = memberPoMapper.selectByExample(example);
        if(memberPos == null || memberPos.size() == 0){
            return false;
        }
        return true;
    }
}
