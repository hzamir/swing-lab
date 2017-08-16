package com.baliset.conf;

import org.slf4j.*;
import org.springframework.beans.factory.*;
import org.springframework.boot.context.properties.*;
import org.springframework.context.annotation.*;

import javax.validation.constraints.*;


@Configuration
@ConfigurationProperties(prefix = "app.defaults")
public class SwingLabDefaultsConfig implements InitializingBean
{
  private static final Logger logger = LoggerFactory.getLogger(SwingLabDefaultsConfig.class);


  @Min(1)           public int initialRows;
  @Min(1) @Max(300) public int initialSectors;

  public void setInitialRows(int v)    { initialRows    = v; }
  public void setInitialSectors(int v) { initialSectors = v; }

  @Override
  public void afterPropertiesSet() throws Exception
  {
    logger.info("1. Configuration happens here") ;
  }
}
