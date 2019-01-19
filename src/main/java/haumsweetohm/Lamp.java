package haumsweetohm;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.paint.Color;

public class Lamp {

	private Communicator communicator;

	private String name;

	protected Lamp(Communicator communicator, String name) {
		this.name = name;
		this.communicator = communicator;
	}

	public boolean fill(Color color) {
		Map<String, Object> values = new HashMap<>();
		values.put("command", "fill");
		values.put("rgb", color);
		return communicator.sendMessageJson("laumio/" + name + "/json", values);
	}

	public boolean pixel(int pixelId, Color color)
	{
		Map<String, Object> values = new HashMap<>();
		values.put("command", "set_pixel");
		values.put("led", pixelId);
		values.put("rgb", color);
		return communicator.sendMessageJson("laumio/" + name + "/json", values);
	}

	public boolean ring(int ringId, Color color)
	{
		Map<String, Object> values = new HashMap<>();
		values.put("command", "set_ring");
		values.put("ring", ringId);
		values.put("rgb", color);
		return communicator.sendMessageJson("laumio/" + name + "/json", values);
	}

	public boolean column(int columnId, Color color)
	{
		Map<String, Object> values = new HashMap<>();
		values.put("command", "set_column");
		values.put("column", columnId);
		values.put("rgb", color);
		return communicator.sendMessageJson("laumio/" + name + "/json", values);
	}
}
