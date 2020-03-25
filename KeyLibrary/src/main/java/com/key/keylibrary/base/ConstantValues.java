package com.key.keylibrary.base;

import com.key.keylibrary.utils.UiUtils;

/**
 * created by key  on 2019/10/1
 */
public class ConstantValues {
  public static final String DOWNLOAD = GlobalApplication.getContext().getExternalFilesDir("Download").getAbsolutePath();
  public static final String FILE_PHOTO = UiUtils.getContext().getExternalFilesDir("TEMP").getAbsolutePath();
  public static final String TAKE_PHOTO = "take_photo_identification";
}
