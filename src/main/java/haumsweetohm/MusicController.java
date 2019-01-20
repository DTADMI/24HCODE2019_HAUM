package haumsweetohm;

import haumsweetohm.cbv.MusicStatus;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class MusicController implements Callable<Void> {

    private Communicator communicator;

    protected MusicController(Communicator communicator) {
        this.communicator = communicator;
    }

    protected MusicController(String address, int port) {
        this.communicator = new Communicator(address, port);
    }


    @Override
    public Void call() throws Exception {
        if ( !communicator.isConnected()) {
            return null;
        } else {
            communicator.getReceiverListeners().put("music/status", message -> {
                System.out.println("music/status " + handleStatus(message));
            });

            communicator.getReceiverListeners().put("music/control/getstate", message -> {
                System.out.println("music/control/getstate " + message);
                handleState(message);
            });

            communicator.getReceiverListeners().put("music/control/getvol", message -> handleVolume(message, "Laumio_0FBFBF"));
        }

        return null;
    }

    public String handleStatus(String message)
    {
        System.out.println("music/status : " + message);
        for(MusicStatus status : MusicStatus.values()){
            if(status.value().equals(message)){
                return message;
            }
        }
        return MusicStatus.STOP.value();
    }
    public String handleState(String message)
    {
        System.out.println("music/control/getstate : " + message);
        return message;
    }

    public String handleVolume(String message, String name)
    {
        System.out.println("music/control/getvol : " + message);
        Lamp lamp = new Lamp(communicator, name);
        int volume = Integer.getInteger(message);
        System.out.println(name + " switched to [" + volume + ", "+ volume + ", "+ volume +"]");
        lamp.fill(volume, volume, volume);
        System.out.println("Volume : " + message + " m");
        return message;
    }

    public boolean nextMusic()
    {
        return communicator.sendMessage("music/control/next", "");
    }

    public boolean previousMusic()
    {
        return communicator.sendMessage("music/control/previous", "");
    }

    public boolean stopMusic()
    {
        return communicator.sendMessage("music/control/stop", "");
    }

    public boolean playMusic()
    {
        return communicator.sendMessage("music/control/play", "");
    }

    public boolean pauseMusic()
    {
        return communicator.sendMessage("music/control/pause", "");
    }

    public boolean toggleMusic()
    {
        return communicator.sendMessage("music/control/toggle", "");
    }

    public boolean setVolume(String message)
    {
        return communicator.sendMessage("music/control/setvol", Integer.getInteger(message)%100);
    }

    public boolean setVolume(int volume) {
        return communicator.sendMessage("music/control/setvol", volume);
    }
}