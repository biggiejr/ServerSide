package gmailPart;

import com.dropbox.core.DbxException;
import dropboxPart.DropboxManager;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartBody;
import com.google.api.services.gmail.model.ModifyThreadRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

class AttachmentDownloader implements Runnable {
    
    Gmail service;
    String userId;
    String messageId;

    AttachmentDownloader(Gmail service, String userId, String messageId) {
        this.service = service;
        this.userId = userId;
        this.messageId = messageId;
    }

    /**
     * Get the attachments in a given email.
     *
     * @param service Authorized Gmail API instance.
     * @param userId User's email address. The special value "me" can be used to
     * indicate the authenticated user.
     * @param messageId ID of Message containing attachment..
     * @throws IOException
     */
    private static void getAttachments(Gmail service, String userId, String messageId)
            throws IOException {
        Message message = service.users().messages().get(userId, messageId).execute();
        List<MessagePart> parts = message.getPayload().getParts();
        for (MessagePart part : parts) {
            if (part.getFilename() != null && part.getFilename().length() > 0) {
                String filename = part.getFilename();
                String attId = part.getBody().getAttachmentId();
                MessagePartBody attachPart = service.users().messages().attachments().
                        get(userId, messageId, attId).execute();
                byte[] fileByteArray = Base64.decodeBase64(attachPart.getData());

                //--------------------------------------------------------
                File tempFile = new File(filename);
                try (FileOutputStream fileOutFile = new FileOutputStream(tempFile)) {
                    
                   fileOutFile.write(fileByteArray);
                   fileOutFile.close();
                //--------------------------------------------------------
                   DropboxManager dm = new DropboxManager();
                    try {
                        dm.createAuthAndUpload(tempFile);
                    } catch (DbxException ex) {
                        Logger.getLogger(AttachmentDownloader.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(AttachmentDownloader.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    List<String> toRemove = new ArrayList();
                    toRemove.add("UNREAD");

                    List<String> toAdd = new ArrayList<>();
                    modifyThread(service, userId, messageId, toAdd, toRemove);
                }
            }
        }

    }

    /**
     * Modify the Labels applied to a Thread..
     *
     * @param service Authorized Gmail API instance.
     * @param userId User's email address. The special value "me" can be used to
     * indicate the authenticated user.
     * @param threadId Id of the thread within the user's account.
     * @param labelsToAdd List of label ids to add.
     * @param labelsToRemove List of label ids to remove.
     * @throws IOException
     */
    
    
    
    //Marking as read
    public static void modifyThread(Gmail service, String userId, String threadId,
            List<String> labelsToAdd, List<String> labelsToRemove) throws IOException {
        ModifyThreadRequest mods = new ModifyThreadRequest().setAddLabelIds(labelsToAdd)
                .setRemoveLabelIds(labelsToRemove);
        com.google.api.services.gmail.model.Thread thread = service.users().threads().modify(userId, threadId, mods).execute();

        System.out.println("Thread id: " + thread.getId());
        System.out.println(thread.toPrettyString());
    }

    @Override
    public void run() {
        try {
            getAttachments(service, userId, messageId);
        } catch (IOException ex) {
            Logger.getLogger(AttachmentDownloader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
