package com.tanhua.db.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class UserTest implements Serializable {


  @TableId(type = IdType.AUTO)
  private Long id;
  private Long qid;
  private Date created;
  private Long userId;
  private Integer score;
  private Integer conclusionId;
}
