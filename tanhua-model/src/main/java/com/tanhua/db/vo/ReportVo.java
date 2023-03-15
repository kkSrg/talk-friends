package com.tanhua.db.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ReportVo implements Serializable {
    private String conclusion;
    private String cover;
    private List similarYou;
    private List dimensions;
}
