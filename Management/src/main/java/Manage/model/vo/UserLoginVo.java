package Manage.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author snow create 2021/02/14 16:31
 */
@Data
public class UserLoginVo {

    @ApiModelProperty(value = "用户名")
    @NotNull
    private String userName;

    @ApiModelProperty(value = "密码")
    @NotNull
    private String password;
}
