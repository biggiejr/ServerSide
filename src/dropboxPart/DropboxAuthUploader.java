package dropboxPart;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.UploadErrorException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author Mato
 */
public class DropboxAuthUploader {

    private static final String ACCESS_TOKEN = "gJpuKawxsWAAAAAAAAAACFuYhT-t75j1KQUBiC_yMqDExtnM6zHB8DYBxVZJx06E";

    public void createAuthAndUpload(File file) throws DbxException, FileNotFoundException, UploadErrorException, IOException {
        DbxRequestConfig config = new DbxRequestConfig("dropbox/java-tutorial", "en_US");
        DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);

        try (InputStream in = new FileInputStream(file)) {
            FileMetadata metadata = client.files().uploadBuilder("/ROOT/" + file).uploadAndFinish(in);
        }
    }

}
