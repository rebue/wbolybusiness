package com.wboly.modules.entity.config;

/**
 * 配置表
 * @author tai yang wang
 * @createTime:2016-1-18
 */
import java.util.List;

import com.wboly.system.sys.entity.SysEntity;


public class VblConfigEntity  extends SysEntity{
	
	private static final long serialVersionUID = 1L;
	
	
    //配置名
    private String configname;
    //配置值
    private String configvalue;
    //配置备注
    private String describe;
    
    //配置名组
    List<String> confignames;



    public String getConfigname() {
          return configname;
    }
     public void setConfigname(String configname) {
          this.configname = configname;
    }
    public String getConfigvalue() {
          return configvalue;
    }
     public void setConfigvalue(String configvalue) {
          this.configvalue = configvalue;
    }
    public String getDescribe() {
          return describe;
    }
     public void setDescribe(String describe) {
          this.describe = describe;
    }
	public List<String> getConfignames() {
		return confignames;
	}
	public void setConfignames(List<String> confignames) {
		this.confignames = confignames;
	}


	
	

}
