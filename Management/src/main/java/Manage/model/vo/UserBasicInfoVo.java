package Manage.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author snow create 2021/02/14 16:35
 */
@Data
public class UserBasicInfoVo {

    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "真实姓名")
    private String realName;

    @ApiModelProperty(value = "性别")
    private Byte gender;

    @ApiModelProperty(value = "电话号码")
    private String mobile;
}
