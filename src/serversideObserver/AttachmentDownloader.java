package serversideObserver;

import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartBody;

import java.io.FileOutputStream;
import java.io.IOException;
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
                try (FileOutputStream fileOutFile = new FileOutputStream("directory_to_store_attachments" + filename)) {
                    fileOutFile.write(fileByteArray);
                }
            }
        }
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
