package me.sebaarkadia.practice.util.item;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.sebaarkadia.practice.util.ParserUtil;

public enum GenericItemStack {

	INSTANCE;
	
	private Plugin plugin;
	private static Map<String, ItemData> NAME_MAP = new HashMap<>();
	
	public void initialize(Plugin plugin) {
		this.plugin = plugin;
		
		NAME_MAP.clear();

		List<String> lines = readLines();

		for (String line : lines) {
			String[] parts = line.split(",");

			NAME_MAP.put(
					parts[0],
					new ItemData(Material.getMaterial(Integer.parseInt(parts[1])), Short.parseShort(parts[2]))
			);
		}
	}
	
	public static ItemData[] repeat(Material material, int times) {
		return repeat(material, (byte) 0, times);
	}

	public static ItemData[] repeat(Material material, byte data, int times) {
		ItemData[] itemData = new ItemData[times];

		for (int i = 0; i < times; i++) {
			itemData[i] = new ItemData(material, data);
		}

		return itemData;

	}

	public static ItemData[] armorOf(ArmorPart part) {
		List<ItemData> data = new ArrayList<>();

		for (ArmorType at : ArmorType.values()) {
			data.add(new ItemData(Material.valueOf(at.name() + "_" + part.name()), (short) 0));
		}

		return data.toArray(new ItemData[data.size()]);
	}

	public static ItemData[] swords() {
		List<ItemData> data = new ArrayList<>();

		for (SwordType at : SwordType.values()) {
			data.add(new ItemData(Material.valueOf(at.name() + "_SWORD"), (short) 0));
		}

		return data.toArray(new ItemData[data.size()]);
	}

	public static ItemStack get(String input, int amount) {
		ItemStack item = get(input);

		if (item != null) {
			item.setAmount(amount);
		}

		return item;
	}

	public static ItemStack get(String input) {
		if (ParserUtil.isInteger(input)) {
			return new ItemStack(Material.getMaterial(Integer.parseInt(input)));
		}

		if (input.contains(":")) {
			if (ParserUtil.isShort(input.split(":")[1])) {
				if (ParserUtil.isInteger(input.split(":")[0])) {
					return new ItemStack(
							Material.getMaterial(Integer.parseInt(input.split(":")[0])), 1,
							Short.parseShort(input.split(":")[1])
					);
				} else {
					if (!NAME_MAP.containsKey(input.split(":")[0].toLowerCase())) {
						return null;
					}

					ItemData data = NAME_MAP.get(input.split(":")[0].toLowerCase());
					return new ItemStack(data.getMaterial(), 1, Short.parseShort(input.split(":")[1]));
				}
			} else {
				return null;
			}
		}

		if (!NAME_MAP.containsKey(input)) {
			return null;
		}

		return NAME_MAP.get(input).toItemStack();
	}

	public static String getName(ItemStack original) {
		if (original.getDurability() != 0) {
			String named = CraftItemStack.asNMSCopy(original).getName();

			if (named != null) {
				if (named.contains(".")) {
					named = WordUtils.capitalize(original.getType().toString().toLowerCase().replace("_", " "));
				}

				return named;
			}
		}

		String string = original.getType().toString().replace("_", " ");
		char[] chars = string.toLowerCase().toCharArray();
		boolean found = false;

		for (int i = 0; i < chars.length; i++) {
			if (!found && Character.isLetter(chars[i])) {
				chars[i] = Character.toUpperCase(chars[i]);
				found = true;
			} else if (Character.isWhitespace(chars[i]) || chars[i] == '.' || chars[i] == '\'') {
				found = false;
			}
		}

		return String.valueOf(chars);
	}

	private List<String> readLines() {
		try {
			return IOUtils.readLines(this.plugin.getClass().getClassLoader().getResourceAsStream("items.csv"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public static Map<String, ItemData> getNameMap() {
		return NAME_MAP;
	}

	public enum ArmorPart {
		HELMET, CHESTPLATE, LEGGINGS, BOOTS;
	}

	public enum ArmorType {
		DIAMOND, IRON, GOLD, LEATHER
	}

	public enum SwordType {
		DIAMOND, IRON, GOLD, STONE
	}

	@Getter
	public static class ItemData {

		private Material material;
		private short data1;

		public ItemData(Material material, short data1) {
			this.material = material;
			this.data1 = data1;
		}
		
		public String getName() {
			return GenericItemStack.getName(toItemStack());
		}

		public boolean matches(ItemStack item) {
			return item != null && item.getType() == material && item.getDurability() == data1;
		}

		public ItemStack toItemStack() {
			return new ItemStack(material, 1, data1);
		}

		public Material getMaterial() {
			return material;
		}
		
		public short getData() {
			return data1;
		}
	}

}
