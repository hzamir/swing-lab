package com.baliset.conf;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

//
//import javax.validation.constraints.Max;
//import javax.validation.constraints.Min;
//import javax.validation.constraints.NotNull;

@Configuration
@ConfigurationProperties(prefix = "app.defaults")

public class SwingLabDefaultsConfig implements InitializingBean
{

  public int initialRows;
  public int initialSectors;

  public void setInitialRows(int initialRows)       { this.initialRows = initialRows; }
  public void setInitialSectors(int initialSectors) { this.initialSectors = initialSectors; }

  @Override
  public void afterPropertiesSet() throws Exception
  {
    System.out.println("hi");
  }
}
