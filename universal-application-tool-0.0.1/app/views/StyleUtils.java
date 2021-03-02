package views;

import java.util.Arrays;
import java.util.stream.Collectors;

public class StyleUtils {
  public static String RESPONSIVE_SM = "sm";
  public static String RESPONSIVE_MD = "md";
  public static String RESPONSIVE_LG = "lg";
  public static String RESPONSIVE_XL = "xl";
  public static String RESPONSIVE_2XL = "2xl";

  private static String[] ALL_SIZES = {
    RESPONSIVE_SM, RESPONSIVE_MD, RESPONSIVE_LG, RESPONSIVE_XL, RESPONSIVE_2XL
  };

  public static String applyResponsiveSize(String[] classes, String size) {
    return Arrays.stream(classes).map(entry -> size + ":" + entry).collect(Collectors.joining(" "));
  }

  public static String buildResponsiveClass(String[] values, String defaultValue) {
    return StyleUtils.buildResponsiveClass(StyleUtils.ALL_SIZES, values, defaultValue);
  }

  public static String buildResponsiveClass(String[] sizes, String[] values, String defaultValue) {
    if (sizes.length != values.length) {
      return defaultValue;
    }

    StringBuilder builder = new StringBuilder(defaultValue);
    for (int i = 0; i < sizes.length; i++) {
      builder.append(String.format(" %1$s:%2$s", sizes[i], values[i]));
    }

    return builder.toString();
  }
}
