package com.tanhua.db.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
@Data
public class Question extends BasePojo implements Serializable {
    @TableId(type = IdType.INPUT)
    private Long id;
    private Long userId;
    private String txt;
}
