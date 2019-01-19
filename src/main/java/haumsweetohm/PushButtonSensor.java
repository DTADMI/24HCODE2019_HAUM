package haumsweetohm;

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
                System.out.println("capteur_bp/status "+getStatus(message));
            });

            communicator.getReceiverListeners().put("capteur_bp/switch/led1/state", message -> {
                System.out.println(getLed1Status("capteur_bp/switch/led1/state "+message));
            });

            communicator.getReceiverListeners().put("capteur_bp/switch/led2/state", message -> {
                System.out.println(getLed2Status("capteur_bp/switch/led2/state "+message));
            });

            communicator.getReceiverListeners().put("capteur_bp/switch/led3/state", message -> {
                System.out.println(getLed3Status("capteur_bp/switch/led3/state "+message));
            });

            communicator.getReceiverListeners().put("capteur_bp/switch/led4/state", message -> {
                System.out.println(getLed4Status("capteur_bp/switch/led4/state "+message));
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
    private String getLed1Status(String message) {
        return message;
    }

    private String getLed2Status(String message) {
        return message;
    }

    private String getLed3Status(String message) {
        return message;
    }

    private String getLed4Status(String message) {
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