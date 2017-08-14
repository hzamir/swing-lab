package com.baliset.util;

import junit.framework.TestCase;

public class CryptoTest extends TestCase
{

  public void testEncryption() throws Exception
  {
        final String[] tests= {
            "Hello",
            "Awake, for morning in the bowl of night has flung the stone that puts the stars to flight",
        };

        Crypto crypto = new Crypto("afsd0afsl-2494");

        for(String s: tests)
        {
          String t = crypto.encryptString(s);
          String d = crypto.decryptString(t);
          String msg = String.format("%s->%s->%s", s,t,d);
          System.out.println(msg);
          assertEquals(s,d);
        }
    
  }


}