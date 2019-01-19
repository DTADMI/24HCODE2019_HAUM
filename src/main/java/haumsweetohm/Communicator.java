package haumsweetohm;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.scene.paint.Color;

public class Communicator {

	public static interface CommunicatorReceiverListener {
		void onMessageReceived(String message);
	}

	private String address;

	private int port;

	private IMqttClient client;

	private boolean isConnected = false;

	private ObservableMap<String, CommunicatorReceiverListener> receiverListeners = FXCollections.observableHashMap();

	private Map<String, Thread> listeningThreads = new HashMap<>();

	public ObservableMap<String, CommunicatorReceiverListener> getReceiverListeners() {
		return receiverListeners;
	}

	public Communicator(String address, int port) {
		this.address = address;
		this.port = port;
	}

	private Thread startListeningThread(String topic) {
		Thread t = new Thread(() -> {
			try {
				client.subscribe(topic, (topic2, msg) -> {
					byte[] payload = msg.getPayload();
					String value = new String(payload);
					CommunicatorReceiverListener rl = receiverListeners.get(topic);
					if (rl != null) {
						rl.onMessageReceived(value);
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		t.start();
		return t;
	}

	public boolean start() {
		try {
			String publisherId = UUID.randomUUID().toString();
			final IMqttClient client = new MqttClient("tcp://" + address + ":" + port, publisherId);

			MqttConnectOptions options = new MqttConnectOptions();
			options.setAutomaticReconnect(true);
			options.setCleanSession(true);
			options.setConnectionTimeout(10);
			client.connect(options);
			isConnected = true;

			receiverListeners.addListener((MapChangeListener<String, CommunicatorReceiverListener>) change -> {
				String topic = change.getKey();
				if (change.wasAdded()) {
					Thread alreadyExistentThread = listeningThreads.get(topic);
					if (alreadyExistentThread == null) {
						listeningThreads.put(topic, startListeningThread(topic));
					}
				} else if (change.wasRemoved()) {
					Thread alreadyExistentThread = listeningThreads.remove(topic);
					if (alreadyExistentThread != null) {
						try {
							client.unsubscribe(topic);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			});

		} catch (MqttException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean sendMessage(String topic, Map<String, Object> parameters) {
		if (!isConnected) {
			return false;
		}
		String json = "{\n";
		for (Entry<String, Object> p : parameters.entrySet()) {
			json += "'" + p.getKey() + "':";
			Object value = p.getValue();
			if (value instanceof String) {
				json += "'" + value + "'";
			} else if (value instanceof Number) {
				json += value.toString();
			} else if (value instanceof Color) {
				Color color = (Color) value;
				json += "[" + Math.min(255, (int) color.getRed() * 255) + ","
						+ Math.min(255, (int) color.getRed() * 255) + "," + Math.min(255, (int) color.getRed() * 255)
						+ "]";
			}
		}
		json += "}";
		byte[] payload = json.getBytes();
		MqttMessage msg = new MqttMessage(payload);
		msg.setQos(0);
		msg.setRetained(true);
		try {
			client.publish(topic, msg);
		} catch (MqttException e) {
			return false;
		}
		return true;
	}
}
