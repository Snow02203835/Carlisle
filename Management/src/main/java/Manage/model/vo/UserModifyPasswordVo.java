package Manage.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author snow create 2021/01/17 23:46
 */
@Data
@ApiModel(description = "用户修改密码对象")
public class UserModifyPasswordVo {

    @ApiModelProperty(value = "用户名")
    @NotNull
    private String userName;

    @ApiModelProperty(value = "验证码")
    @NotNull
    private String verifyCode;

    @ApiModelProperty(value = "新密码")
    @NotNull
    private String password;
}
