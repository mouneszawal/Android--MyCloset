package tr.yildiz.mycloset;

import android.content.Context;
import android.net.Uri;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Utils {
    /**
     * copies image from external storage to the app's internal storage
     * @param context
     * @param src
     * @param fileName
     * @return
     * the uri of the new image file
     * @throws IOException
     */
    public static Uri copyFile(Context context, Uri src, String fileName) throws IOException{
        try (InputStream in = context.getContentResolver().openInputStream(src)) {
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            try (OutputStream out = fos) {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
        }

        return Uri.fromFile(context.getFileStreamPath(fileName));
    }
}
