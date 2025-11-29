package course.examples.newsspace.utils;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;public class FileUtils {
    /**
 * Chuyển đổi một Uri (từ thư viện ảnh) thành một đối tượng File.
 * Phương thức này tạo một file tạm trong bộ nhớ cache của ứng dụng để upload.
 * @param context Context của ứng dụng.
 * @param uri Uri của file cần chuyển đổi.
 * @return một đối tượng File, hoặc null nếu có lỗi.
 */
public static File getFileFromUri(Context context, Uri uri) {
    if (uri == null) {
        return null;
    }
    try {
        // Tạo một tên file tạm duy nhất
        String fileName = "upload_temp_" + System.currentTimeMillis();
        File destinationFile = new File(context.getCacheDir(), fileName);

        // Sao chép nội dung từ Uri vào file tạm
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        if (inputStream == null) {
            return null; // Không thể mở luồng đọc từ Uri
        }
        OutputStream outputStream = new FileOutputStream(destinationFile);
        byte[] buf = new byte[1024];
        int len;
        while ((len = inputStream.read(buf)) > 0) {
            outputStream.write(buf, 0, len);
        }
        inputStream.close();
        outputStream.close();

        return destinationFile;
    } catch (IOException e) {
        e.printStackTrace();
        return null;
    }
}

    /**
     * Tạo một MultipartBody.Part từ một đối tượng File.
     * @param context Context để xác định loại MIME.
     * @param partName Tên của "part" mà backend mong đợi (ví dụ: "image", "avatar").
     * @param file Đối tượng file cần upload.
     * @return MultipartBody.Part đã sẵn sàng để gửi đi, hoặc null nếu file không tồn tại.
     */
    public static MultipartBody.Part createPartFromFile(Context context, String partName, File file) {
        if (file == null || !file.exists()) {
            return null;
        }

        // Xác định loại media type của file một cách chính xác hơn
        String mimeType = getMimeType(context, Uri.fromFile(file));
        if (mimeType == null) {
            mimeType = "application/octet-stream"; // Loại mặc định
        }

        RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), file);
        // Tạo MultipartBody.Part với tên part và tên file gốc
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

    /**
     * Một phương thức tiện ích kết hợp cả hai bước trên.
     * Chuyển thẳng từ Uri sang MultipartBody.Part.
     * @param context Context của ứng dụng.
     * @param uri Uri của file cần upload.
     * @param partName Tên của "part" mà backend mong đợi.
     * @return MultipartBody.Part đã sẵn sàng, hoặc null nếu có lỗi.
     */
    public static MultipartBody.Part uriToMultipartBodyPart(Context context, Uri uri, String partName) {
        File file = getFileFromUri(context, uri);
        if (file == null) {
            return null;
        }
        return createPartFromFile(context, partName, file);
    }

    /**
     * Lấy loại MIME từ một Uri.
     */
    private static String getMimeType(Context context, Uri uri) {
        String mimeType;
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            ContentResolver cr = context.getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase());
        }
        return mimeType;
    }}