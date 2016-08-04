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
public class DropboxManager {

    private static final String ACCESS_TOKEN = "gJpuKawxsWAAAAAAAAAACFuYhT-t75j1KQUBiC_yMqDExtnM6zHB8DYBxVZJx06E";

    public void createAuthAndUpload(File file) throws DbxException, FileNotFoundException, UploadErrorException, IOException {
        // Create Dropbox client
        DbxRequestConfig config = new DbxRequestConfig("dropbox/java-tutorial", "en_US");
        DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);

        //check account 
//        FullAccount account = client.users().getCurrentAccount();
//        System.out.println(account.getName().getDisplayName());
        
        try (InputStream in = new FileInputStream(file)) {
        FileMetadata metadata = client.files().uploadBuilder("/ROOT/"+file).uploadAndFinish(in);
            System.out.println(metadata);
}
    }

}
