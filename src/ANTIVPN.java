package antivpn;

import mindustry.mod.Mod;
import arc.Events;
import mindustry.game.EventType.PlayerConnect;
import mindustry.gen.Player;
import arc.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ANTIVPN extends Mod {
    public ANTIVPN() {
        Log.info("Loaded ANTIVPN constructor.");

        // Run check when a player connects, before joining world
        Events.on(PlayerConnect.class, event -> {
            Player p = event.player;
            String ip = p.con.address;
            Log.info("ANTIVPN check attempt: @ from @", p.name, ip);

            if (isUsingVPN(ip)) {
                p.kick("VPN connections are not allowed.");
                Log.info("Kicked @ due to VPN detection.", p.name);
            }
        });
    }

    private boolean isUsingVPN(String host) {
        try {
            Process proc = new ProcessBuilder(
                "nmap", "-sV", "-Pn",
                "--version-intensity", "5",
                "--max-retries", "1",
                "--host-timeout", "15s",
                "-p", "443", host
            ).redirectErrorStream(true).start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("443/tcp")) {
                        // debug: Log.info("VPN check output: @", line);
                        if (line.matches("^443/tcp\\s+open\\s+openvpn.*")) {
                            return true;
                        }
                        break;
                    }
                }
            }
            proc.waitFor();
        } catch (Exception e) {
            Log.err("VPN check failed for @: @", host, e.getMessage());
        }
        return false;
    }


    @Override
    public void loadContent() {
        Log.info("ANTIVPN mod Loaded");
    }
}
