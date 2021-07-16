package me.hulipvp.hcf.backend.license;

import me.hulipvp.hcf.HCF;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Scanner;

public class License {

	private Plugin plugin;
	private String url = "https://licenses.colddev.cf/api/validate";

	public License(Plugin plugin){
		this.plugin = plugin;
	}

	public void register() {
		System.out.println("[" + plugin.getName() + "] " + "Checking license....");
		String key = HCF.getInstance().getConfig().getString("license");

		try {
			if (key.equals("")) key = "XXXX-XXXX-XXXX";
			URL url = new URL(this.url + "/" + key + "/" + plugin.getName() + "/");
			HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
			InputStream is = con.getInputStream();
			Scanner s = new Scanner((new InputStreamReader(is)));
			if (s.hasNext()) {
				String response = s.next();
				s.close();
				if (response.equalsIgnoreCase("Success")) {
					System.out.println("[" + plugin.getName() + "] " + "Success, license is valid!");
					return;
				} else {
					System.out.println("[" + plugin.getName() + "] " + "Error, license is invalid: " + response);
					Bukkit.getScheduler().cancelTasks(plugin);
					Bukkit.getPluginManager().disablePlugin(plugin);
					return;
				}
			} else {
				s.close();
				System.out.println("[" + plugin.getName() + "] " + "License is NOT valid!");
				System.out.println("[" + plugin.getName() + "] " + "Error, license is invalid: Licensing server offline?");
				Bukkit.getScheduler().cancelTasks(plugin);
				Bukkit.getPluginManager().disablePlugin(plugin);
				return;
			}
		} catch (IOException e) {
			System.out.println("[" + plugin.getName() + "] " + "License is NOT valid!");
			System.out.println("[" + plugin.getName() + "] " + "Error, license is invalid: Licensing server offline?");
			Bukkit.getScheduler().cancelTasks(plugin);
			Bukkit.getPluginManager().disablePlugin(plugin);
			return;
		}
	}
}
