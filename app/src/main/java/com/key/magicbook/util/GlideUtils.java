package com.key.magicbook.util;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.key.magicbook.R;

/**
 * created by key  on 2020/3/3
 */
public class GlideUtils {

    public static void loadCenter(Context context, String url, ImageView iv) {
        RequestOptions options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE).skipMemoryCache(false).transform(new CenterCrop())
                .placeholder(R.mipmap.load_img_cover);
        loadImage(context, url, options, iv);
    }

    public static void load(Context context, String url, ImageView iv) {
        RequestOptions options = new RequestOptions().placeholder(R.mipmap.bg);
        loadImage(context, url, options, iv);
    }

    private static void loadImage(Context context, String url, RequestOptions options, ImageView view) {
        Glide.with(context)
                .load(url)
                .apply(options)
                .into(view);
    }
}
