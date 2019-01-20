package haumsweetohm;

import javafx.scene.paint.Color;

import java.util.concurrent.Callable;

public class AtmosphericSensor implements Callable<Void> {

    private Communicator communicator;

    protected AtmosphericSensor(Communicator communicator) {
        this.communicator = communicator;
    }

    @Override
    public Void call() throws Exception {
        if ( !communicator.isConnected()) {
            return null;
        } else {
            communicator.getReceiverListeners().put("atmosphere/temperature", message -> {
                System.out.println("atmosphere/temperature " + handleTemperatureValue(message, "Laumio_0FBFBF"));
            });

            communicator.getReceiverListeners().put("atmosphere/pression", message -> {
                handlePressionValue(message, "Laumio_0FBFBF");
            });

            communicator.getReceiverListeners().put("atmosphere/humidite", message -> handleHumidityValue(message, "Laumio_0FBFBF"));

            communicator.getReceiverListeners().put("atmosphere/humidite_absolue", message -> {
                handleAbsoluteHumidityValue(message, "Laumio_0FBFBF");
            });
        }

        return null;
    }

    private String handleTemperatureValue(String message, String name) {
        System.out.println("atmosphere/temperature : " + message);

        System.out.println("Temperature : " + message + " °C");
        return message;
    }

    private String handlePressionValue(String message, String name) {
        System.out.println("atmosphere/pression : " + message);
        System.out.println("Pression : " + message + " hPa");
        return message;
    }

    private String handleHumidityValue(String message, String name) {
        System.out.println("atmosphere/humidite : " + message);
        System.out.println("Humidité : " + message + " %");
        return message;
    }

    private String handleAbsoluteHumidityValue(String message, String name) {
        System.out.println("atmosphere/pression : " + message);
        System.out.println("Humidité absolue : " + message + " g/m^3");
        return message;
    }

    public boolean advertise()
    {
        return communicator.sendMessage("atmosphere/status/advertise", "");
    }
}