package haumsweetohm;

import javafx.scene.paint.Color;

import java.util.concurrent.Callable;

public class PushButtonSensor implements Callable<Void> {

    private Communicator communicator;

    protected PushButtonSensor(Communicator communicator) {
        this.communicator = communicator;
    }

    protected PushButtonSensor(String address, int port) {
        this.communicator = new Communicator(address, port);
    }

    /*private PushButtonSensor(String address, int port) throws MqttException {
        String publisherId = UUID.randomUUID().toString();
        this.client = new MqttClient("tcp://" + address + ":" + port, publisherId);
    }*/

    @Override
    public Void call() throws Exception {
        if ( !communicator.isConnected()) {
            return null;
        } else {
            communicator.getReceiverListeners().put("capteur_bp/status", message -> {
                System.out.println("capteur_bp/status " + getStatus(message));
            });

            communicator.getReceiverListeners().put("capteur_bp/switch/led1/state", message -> {
                handleLed1Status(message, "Laumio_0FBFBF");
            });

            communicator.getReceiverListeners().put("capteur_bp/switch/led2/state", message -> handleLed2Status(message, "Laumio_0FBFBF"));

            communicator.getReceiverListeners().put("capteur_bp/switch/led3/state", message -> {
                handleLed3Status(message, "Laumio_0FBFBF");
            });

            communicator.getReceiverListeners().put("capteur_bp/switch/led4/state", message -> {
                handleLed4Status(message, "Laumio_0FBFBF");
            });

            communicator.getReceiverListeners().put("capteur_bp/binary_sensor/bp1/state", message -> {
                System.out.println(getPushButton1Status(message));
            });

            communicator.getReceiverListeners().put("capteur_bp/binary_sensor/bp2/state", message -> {
                System.out.println(getPushButton2Status(message));
            });

            communicator.getReceiverListeners().put("capteur_bp/binary_sensor/bp3/state", message -> {
                System.out.println(getPushButton3Status(message));
            });

            communicator.getReceiverListeners().put("capteur_bp/binary_sensor/bp4/state", message -> {
                System.out.println(getPushButton4Status(message));
            });

            communicator.getReceiverListeners().put("capteur_bp/sensor/bp_rssi/state", message -> {
                System.out.println(getWifiStatus(message));
            });

            communicator.getReceiverListeners().put("capteur_bp/sensor/uptime_sensor/state", message -> {
                System.out.println(getUpTimeStatus(message));
            });
        }

        return null;
    }

    private String getStatus(String message){
        return message;
    }

    private String handleLed1Status(String message, String name) {
        System.out.println("capteur_bp/switch/led1/state : " + message);
        Lamp lamp = new Lamp(communicator, name);
        if("ON".equals(message)){
            System.out.println(name + " switched to RED");
            lamp.fill(Color.RED);
        } else {
            System.out.println(name + " switched to WHITE");
            lamp.fill(Color.WHITE);
        }
        return message;
    }

    private String handleLed2Status(String message, String name) {
        System.out.println("capteur_bp/switch/led2/state : " + message);
        Lamp lamp = new Lamp(communicator, name);
        if("ON".equals(message)){
            System.out.println(name + " switched to BLUE");
            lamp.fill(Color.BLUE);
        } else {
            System.out.println(name + " switched to WHITE");
            lamp.fill(Color.WHITE);
        }
        return message;
    }

    private String handleLed3Status(String message, String name) {
        System.out.println("capteur_bp/switch/led3/state : " + message);
        Lamp lamp = new Lamp(communicator, name);
        if("ON".equals(message)){
            System.out.println(name + " switched to YELLOW");
            lamp.fill(Color.YELLOW);
        } else {
            System.out.println(name + " switched to WHITE");
            lamp.fill(Color.WHITE);
        }
        return message;
    }

    private String handleLed4Status(String message, String name) {
        System.out.println("capteur_bp/switch/led4/state : " + message);
        Lamp lamp = new Lamp(communicator, name);
        if("ON".equals(message)){
            System.out.println(name + " switched to VIOLET");
            lamp.fill(Color.VIOLET);
        } else {
            System.out.println(name + " switched to WHITE");
            lamp.fill(Color.WHITE);
        }
        return message;
    }

    private String getPushButton1Status(String message) {
        return message;
    }

    private String getPushButton2Status(String message) {
        return message;
    }

    private String getPushButton3Status(String message) {
        return message;
    }

    private String getPushButton4Status(String message) {
        return message;
    }

    private String getWifiStatus(String message) {
        return message;
    }

    private String getUpTimeStatus(String message) {
        return message;
    }

    public boolean switchLed(int ledNum, String message)
    {
        if(!"ON".equals(message)){
            message = "OFF";
        }
        return communicator.sendMessage("capteur_bp/switch/led"+ledNum+"/command", message);
    }

    public boolean advertise()
    {
        return communicator.sendMessage("capteur_bp/status/advertise", "");
    }
}