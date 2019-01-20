package haumsweetohm;

import haumsweetohm.cbv.InfraRedSwitchOptions;
import javafx.scene.paint.Color;

import java.util.concurrent.Callable;

public class InfraredSwitchSensor implements Callable<Void> {

    private Communicator communicator;

    protected InfraredSwitchSensor(Communicator communicator) {
        this.communicator = communicator;
    }

    @Override
    public Void call() throws Exception {
        if ( !communicator.isConnected()) {
            return null;
        } else {
            System.out.println("InfraredSwitchSensor");
            for(InfraRedSwitchOptions option : InfraRedSwitchOptions.values()){
                System.out.println("adding receiver for option : " + option.value());
                communicator.getReceiverListeners().put("remote/"+ option +"/state", message -> {
                    System.out.println("distance/value " + handleRemoteOptionValue(message, "Laumio_0FBFBF", option.value()));
                });
            }
        }
        return null;
    }

    private String handleRemoteOptionValue(String message, String name, String option) {
        System.out.println("remote/"+ option +"/state : " + message);
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

}