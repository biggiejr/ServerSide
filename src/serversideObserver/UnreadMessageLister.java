package serversideObserver;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

class UnreadMessageLister extends Thread  {

    AttachmentDownloader ad;
    Gmail service;
    List<Message> messages;
    String userId;
    String query = "is:unread";

    public UnreadMessageLister(Gmail service, String userId) {
        this.service = service;
        this.userId = userId;
    }

    /**
     * List all Messages of the user's mailbox matching the query.
     *
     * @param service Authorized Gmail API instance.
     * @param userId User's email address. The special value "me" can be used to
     * indicate the authenticated user.
     * @param query String used to filter the Messages listed.
     * @return
     * @throws IOException
     */
    public  List<Message> listMessagesMatchingQuery(Gmail service, String userId,
      String query) throws IOException {
    ListMessagesResponse response = service.users().messages().list(userId).setQ(query).execute();
    messages = new ArrayList<>();
    while (response.getMessages() != null) {
      messages.addAll(response.getMessages());
      if (response.getNextPageToken() != null) {
        String pageToken = response.getNextPageToken();
        response = service.users().messages().list(userId).setQ(query)
            .setPageToken(pageToken).execute();
      } else {
        break;
      }
    }


    for (Message message : messages) {
      System.out.println(message.toPrettyString());
    }

    return messages;
  }

    
    
    private void sendId() {
        try {
            listMessagesMatchingQuery(service, userId, query);
        } catch (IOException ex) {
            Logger.getLogger(UnreadMessageLister.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (Message message : messages) {
            ad = new AttachmentDownloader(service, userId, message.getId());
            Thread t = new Thread(ad);
            t.start();
        }
    }

    @Override
    public void run(){
            sendId();        

    }

}
