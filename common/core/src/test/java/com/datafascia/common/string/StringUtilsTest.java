// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.string;

import com.datafascia.common.string.StringUtils;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

/**
 * Test code for StringUtils
 */
public class StringUtilsTest {

  private final static String PLAINTEXT1 = "Now is the time for all good men to come to the aid";
  private final static String ENCODED1 = "Tm93IGlzIHRoZSB0aW1lIGZvciBhbGwgZ29vZCBtZW4gdG8gY29tZSB0b"
      + "yB0aGUgYWlk";
  private final static String PLAINTEXT2 = "(╯°□°）╯︵ ┻━┻";
  private final static String ENCODED2 = "KOKVr8Kw4pahwrDvvInila/vuLUg4pS74pSB4pS7";
  private final static String PLAINTEXT3 = "Lorem ipsum dolor sit amet, consectetur adipiscing elit"
      + ". Quisque ultrices, eros accumsan elementum eleifend, ex eros bibendum urna, tincidunt tem"
      + "pus magna ante a odio. Nullam sollicitudin dictum sapien, vel rhoncus metus tempus quis. C"
      + "ras auctor euismod leo, ut pulvinar elit ornare vitae. Aenean aliquam gravida arcu a digni"
      + "ssim. Etiam congue lectus sit amet nisl eleifend, ac pulvinar nisl dictum. Vivamus volutpa"
      + "t odio ac dui rutrum, a fermentum quam ultricies. Nunc quis libero ornare, molestie augue "
      + "sit amet, volutpat odio. Curabitur vitae felis bibendum, ultrices nibh id, pretium velit. "
      + "Integer blandit, nulla non fringilla placerat, felis dolor iaculis augue, ut sagittis nisi"
      + " tortor vel libero. Mauris bibendum purus libero, ut rhoncus purus convallis in. Etiam et "
      + "purus quis elit finibus pellentesque. Integer ut lobortis neque.\n\nProin tristique mattis"
      + " tincidunt. Etiam non porta eros, id sollicitudin diam. Praesent vehicula ullamcorper phar"
      + "etra. Nullam in mollis purus. Praesent lectus velit, pharetra vel ornare finibus, vestibul"
      + "um eget nunc. Quisque pulvinar rutrum felis, eget porta felis cursus sit amet. Vestibulum "
      + "blandit placerat erat sit amet accumsan. Etiam lacinia ullamcorper nibh semper porttitor. "
      + "In mi tellus, dignissim ac felis feugiat, rhoncus imperdiet nibh. Donec vel elit aliquam, "
      + "tristique massa vel, ultricies justo. Aliquam auctor laoreet ornare. Etiam arcu velit, sag"
      + "ittis ut scelerisque in, venenatis eget urna. Duis euismod porttitor urna.\n\nMauris laore"
      + "et suscipit ex, id hendrerit neque fermentum non. Donec porttitor laoreet pulvinar. Praese"
      + "nt eu tincidunt lectus, sed mollis sapien. Quisque non nisi ac magna posuere consectetur e"
      + "get vel felis. Interdum et malesuada fames ac ante ipsum primis in faucibus. Etiam venenat"
      + "is porttitor enim ut vulputate. Ut sem justo, laoreet sit amet augue id, semper sodales an"
      + "te. Curabitur luctus odio ipsum, accumsan imperdiet est accumsan vitae. Morbi at ex non do"
      + "lor semper porta faucibus sit amet erat. Integer facilisis urna augue, et hendrerit lorem "
      + "bibendum non.\n\nAenean eget metus non nibh pretium aliquam non at elit. Aenean non sem nu"
      + "nc. Vivamus sit amet diam sit amet augue ultrices pretium dapibus in est. Integer gravida "
      + "nibh vel sapien scelerisque, in vehicula leo pharetra. Suspendisse quis arcu semper, sodal"
      + "es dui eu, efficitur elit. Nulla laoreet finibus libero, id viverra sem elementum sit amet"
      + ". Curabitur sit amet velit fringilla, accumsan libero nec, sodales diam. Etiam vehicula lo"
      + "rem eu eros cursus, eget malesuada metus egestas. Donec efficitur leo urna, a efficitur me"
      + "tus sollicitudin ut. Suspendisse nec venenatis nunc.\n\nNam cursus fringilla sapien, sit a"
      + "met dictum tortor. Donec eu malesuada ex. Morbi nec bibendum est. Etiam iaculis nisi vel v"
      + "enenatis maximus. Curabitur commodo euismod massa, id feugiat nulla vehicula sed. Suspendi"
      + "sse at gravida leo. Cras quam enim, congue non euismod quis, venenatis in massa. Ut vitae "
      + "consectetur orci. Pellentesque sollicitudin luctus placerat. Suspendisse iaculis ut enim s"
      + "it amet facilisis. Fusce at neque tortor. Mauris sem leo, maximus a arcu sit amet, imperdi"
      + "et suscipit nibh.";
  private final static String PLAINTEXT4 = "This one is intended to fail";
  private final static String ENCODED4 = "didn't encode, LOL";

  @Test
  public void testBase64Encode() {
    assertEquals(StringUtils.base64Encode(PLAINTEXT1), ENCODED1);
    assertEquals(StringUtils.base64Encode(PLAINTEXT2), ENCODED2);
    try {
      assertEquals(new String(Base64.getDecoder().decode(StringUtils.base64Encode(PLAINTEXT3)),
          "UTF-8"), PLAINTEXT3);
    } catch (UnsupportedEncodingException ex) {
      throw new RuntimeException(ex);
    }
    assertFalse(ENCODED4.equals(StringUtils.base64Encode(PLAINTEXT4)));
  }
}
