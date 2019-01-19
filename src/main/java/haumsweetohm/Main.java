package haumsweetohm;

import javafx.scene.paint.Color;

public class Main {

    private static final String ADRESS = "mpd.lan";
    private static final int PORT = 1883;

    public static void main(String[] args) {
        try {
            Communicator communicator = new Communicator(ADRESS, PORT);
            if (!communicator.start())
            {
                System.out.println("Erreur connection");
                return;
            }
            System.out.println("Connected");
            PushButtonSensor pushButtonSensor = new PushButtonSensor(communicator);

            Lamp lamp1 = new Lamp(communicator, "all");

            pushButtonSensor.call();

            if(pushButtonSensor.advertise()){
                lamp1.fill(Color.RED);
                //pushButtonSensor.switchLed(1, "OFF"); //RED
                //pushButtonSensor.switchLed(2, "OFF"); //BLUE
                //pushButtonSensor.switchLed(3, "OFF"); //YELLOW
                pushButtonSensor.switchLed(4, "OFF"); //GREEN

            }
            System.out.println("End");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

}
