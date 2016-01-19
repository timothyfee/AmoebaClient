package NetworkComponents;

import Connection.ConnectionPool;
import DataTransferUnits.NetworkMessage;
import Mercury.NetworkMessageMailer;
import NetworkControllers.NetworkClientController;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.logging.*;

public class AmoebaNetworkClientController extends NetworkClientController {
    private final NetworkMessageMailer incoming = incomingMailer;
    private final NetworkMessageMailer outgoing = outgoingMailer;

    public AmoebaNetworkClientController() {
        super(new AmoebaCodec(), new AmoebaInterpreter());
        attach(new Compressor());
    }

    public void sendMessage(NetworkMessage message){
        outgoing.push(ConnectionPool.FIRST_CONNECTION, message);
    }
    
    public ArrayList<NetworkMessage> getMessages(){
        return incoming.dump();
    }
    
    @Override
    public boolean connect(String address, int port) {
        if (connectionPool.getConnection(ConnectionPool.FIRST_CONNECTION).connect(address, port, 3)) {
            start();
            try {
                connectionPool.getConnection(ConnectionPool.FIRST_CONNECTION).socket.setSoLinger(true, 1);
            } catch (SocketException ex) {
                Logger.getLogger(AmoebaNetworkClientController.class.getName()).log(Level.WARNING, null, ex);
            }
            return true;
        }
        return false;
    }

    @Override
    public void disconnect(){
        connectionPool.getConnection(ConnectionPool.FIRST_CONNECTION).disconnect();
    }
    
    @Override
    public boolean isConnected() {
        return this.connectionPool.getConnection(ConnectionPool.FIRST_CONNECTION).isConnected();
    }
}
