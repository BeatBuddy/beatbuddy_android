package be.kdg.teamd.beatbuddy.util;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Created by Ignace on 16/02/2016.
 */
public class ImageEncoder
{
    public static String encodeImageUrl(String url)
    {
        if(url == null) return null;

        String toEncodePart = url.substring(0, url.lastIndexOf("."));
        String encodedPart =  Uri.encode(toEncodePart, "utf-8");
        String extension = url.substring(url.lastIndexOf("."));

        return encodedPart + extension;
    }

    public static String encodeFullImageUrl(String url, String imageLocation)
    {
        if(url == null) return null;

        int positionEndPrefix = url.indexOf(imageLocation) + imageLocation.length();
        String prefix = url.substring(0, positionEndPrefix);

        String toEncodePart = url.substring(positionEndPrefix, url.lastIndexOf("."));
        String encodedPart =  Uri.encode(toEncodePart, "utf-8");
        String extension = url.substring(url.lastIndexOf("."));

        return prefix + encodedPart + extension;
    }

    public static String convertToBase64(Uri image, ContentResolver contentResolver) throws EncodingException
    {
        try
        {
            Bitmap bm = MediaStore.Images.Media.getBitmap(contentResolver, image);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 90, baos);
            byte[] b = baos.toByteArray();
            String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

            return encodedImage;
        } catch (Exception e)
        {
            throw new EncodingException("error encoding image");
        }
    }

    public static class EncodingException extends RuntimeException
    {
        public EncodingException(String s)
        {
            super(s);
        }
    }
}
