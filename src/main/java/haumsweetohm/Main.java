package haumsweetohm;

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

            AtmosphericSensor atmosphericSensor = new AtmosphericSensor(communicator);
            DistanceDetectorSensor distanceDetectorSensor = new DistanceDetectorSensor(communicator);
            InfraredSwitchSensor infraredSwitchSensor = new InfraredSwitchSensor(communicator);
            PresenceDetectorSensor presenceDetectorSensor = new PresenceDetectorSensor(communicator);

            MusicController musicController = new MusicController(communicator);

            //Lamp lamp = new Lamp(communicator, "all");
            //Lamp lamp1 = new Lamp(communicator, "Laumio_0FBFBF");
            //lamp1.fill(Color.BLUE);
            //pushButtonSensor.call();
            atmosphericSensor.call();
            //distanceDetectorSensor.call();
            //infraredSwitchSensor.call();
            //presenceDetectorSensor.call();
            //musicController.call();

            //musicController.playMusic();//"Laumio_0FBFBF");

            /*if(pushButtonSensor.advertise()){
                //lamp1.fill(Color.RED);
                //pushButtonSensor.switchLed(1, "OFF"); //RED
                //pushButtonSensor.switchLed(2, "OFF"); //BLUE
                //pushButtonSensor.switchLed(3, "OFF"); //YELLOW
                pushButtonSensor.switchLed(4, "OFF"); //DARKGREEN

            }*/
            System.out.println("End");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

}
