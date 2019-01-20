package haumsweetohm;

import javafx.scene.paint.Color;

import java.util.concurrent.Callable;

public class PresenceDetectorSensor implements Callable<Void> {

    private Communicator communicator;

    protected PresenceDetectorSensor(Communicator communicator) {
        this.communicator = communicator;
    }

    @Override
    public Void call() throws Exception {
        if ( !communicator.isConnected()) {
            return null;
        } else {
            communicator.getReceiverListeners().put("presence/state", message -> {
                System.out.println("distance/value " + handlePresenceState(message, "Laumio_0FBFBF"));
            });

            communicator.getReceiverListeners().put("presence/status", message -> {
                getStatus(message);
            });
        }

        return null;
    }

    private String handlePresenceState(String message, String name) {
        System.out.println("presence/state : " + message);
        Lamp lamp = new Lamp(communicator, name);
        if("ON".equals(message)){
            System.out.println(name + " switched to WHITE");
            lamp.fill(Color.WHITE);
        } else {
            System.out.println(name + " switched to BLACK");
            lamp.fill(Color.BLACK);
        }
        return message;
    }


    private String getStatus(String message){
        System.out.println("presence/status : " + message);
        return message;
    }

    public boolean advertise()
    {
        return communicator.sendMessage("distance/status/advertise", "");
    }

}