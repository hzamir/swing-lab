package com.baliset.conf;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import javax.validation.constraints.*;

@Configuration
@ConfigurationProperties(prefix = "app.defaults")

public class SwingLabDefaultsConfig implements InitializingBean
{

  @Min(1)           public int initialRows;
  @Min(1) @Max(300) public int initialSectors;

  public void setInitialRows(int v)    { initialRows    = v; }
  public void setInitialSectors(int v) { initialSectors = v; }

  @Override
  public void afterPropertiesSet() throws Exception
  {
    System.out.println("hi");
  }
}
