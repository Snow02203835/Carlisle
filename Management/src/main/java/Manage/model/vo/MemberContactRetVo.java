package Manage.model.vo;

import Core.util.AES;
import Manage.model.bo.User;
import Manage.model.po.MemberPo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MemberContactRetVo {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "会员名")
    private String name;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "电话号码")
    private String mobile;

    public MemberContactRetVo(MemberPo memberPo) {
        this.id = memberPo.getId();
        this.name = memberPo.getName();
        this.email = AES.decrypt(memberPo.getEmail(), User.AES_PASS);
        this.mobile = AES.decrypt(memberPo.getMobile(), User.AES_PASS);
    }
}
