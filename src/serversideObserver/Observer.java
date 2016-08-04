package serversideObserver;

import com.google.api.services.gmail.Gmail;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mato
 */
class Observer extends Thread {

    final static String userId = "me";
    UnreadMessageLister lister;
    Gmail service;

    public static void main(String[] args) throws IOException {

        while (true) {

            Observer o = new Observer();
            Thread t = new Thread(o);
            t.start();
            try {
                Thread.sleep(1800000);//sleeps for half an hour
            } catch (InterruptedException ex) {
                Logger.getLogger(Observer.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }

    @Override
    public void run() {

        try {
            service = GmailQuickstart.getGmailService();
        } catch (IOException ex) {
            Logger.getLogger(Observer.class.getName()).log(Level.SEVERE, null, ex);
        }

        lister = new UnreadMessageLister(service, userId);
        Thread t = new Thread(lister);
        t.start();

    }

}
