package serversideObserver;

import com.google.api.services.gmail.Gmail;
import java.io.IOException;
import java.util.Timer;

/**
 *
 * @author Mato
 */
class Observer extends Thread{

    final static String userId = "me";
    

    public static void main(String[] args) throws IOException {
        Gmail service = GmailQuickstart.getGmailService();
     
        UnreadMessageLister lister = new UnreadMessageLister(service, userId);
        //while (true) {
        //nespravne riesenie
            Timer timer = new Timer();
            timer.schedule(lister, 1000);   //refreshes every 1/2 hour
            
        //}
    }

}
