package com.tanhua.db.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class Sound implements Serializable {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long userId;//用户id
    private String soundUrl;//语音存储路径
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    @TableLogic
    private Integer logicDel;//逻辑删除

}
