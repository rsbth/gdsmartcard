package gdsmartcard.io;

public interface GDCardChannel {

    boolean addCommunicationListener(CommunicationListener l);

    boolean removeCommunicationListener(CommunicationListener l);

}