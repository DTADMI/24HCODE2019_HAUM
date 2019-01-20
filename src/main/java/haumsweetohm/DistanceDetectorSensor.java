package haumsweetohm;

import javafx.scene.paint.Color;

import java.util.concurrent.Callable;

public class DistanceDetectorSensor implements Callable<Void> {

    private Communicator communicator;

    protected DistanceDetectorSensor(Communicator communicator) {
        this.communicator = communicator;
    }

    @Override
    public Void call() throws Exception {
        if ( !communicator.isConnected()) {
            return null;
        } else {
            communicator.getReceiverListeners().put("distance/value", message -> {
                System.out.println("distance/value " + handleDistanceValue(message, "Laumio_0FBFBF"));
            });

            communicator.getReceiverListeners().put("distance/status", message -> {
                getStatus(message);
            });
        }

        return null;
    }

    private String handleDistanceValue(String message, String name) {
        System.out.println("distance/value : " + message);
        Lamp lamp = new Lamp(communicator, name);
        Double distanceTime100 = Double.valueOf(message)*100;
        int distanceModulo255 = distanceTime100.intValue()%255;
        System.out.println(name + " :: switched to [" + distanceModulo255 + ","+ distanceModulo255 + ","+ distanceModulo255 +"]");
        lamp.fill(distanceModulo255, distanceModulo255, distanceModulo255);
        System.out.println("Distance : " + message + " m");
        return message;
    }


    private String getStatus(String message){
        System.out.println("distance/status : " + message);
        return message;
    }

}