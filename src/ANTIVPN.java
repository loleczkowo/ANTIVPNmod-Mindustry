package antivpn;

import mindustry.mod.Mod;
import arc.Events;
import mindustry.game.EventType.PlayerConnect;
import mindustry.gen.Player;
import arc.util.Log;
import mindustry.Vars;
import mindustry.mod.Mods;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ANTIVPN extends Mod {
    public ANTIVPN() {
        Log.info("Loaded ANTIVPN constructor.");

        // Run check when a player connects, before joining world
        Events.on(PlayerConnect.class, event -> {
            Player p = event.player;
            String ip = p.con.address;

            Log.info("Connection attempt: @ from @", p.name, ip);

            if (isUsingVPN(ip)) {
                p.kick("VPN connections are not allowed.");
                Log.info("Kicked @ due to VPN detection.", p.name);
            }
        });
    }

    // Calls the Python script to check if IP is OpenVPN
    private boolean isUsingVPN(String host) {
        try {
            Mods.ModMeta meta = Vars.mods.getMod(ANTIVPN.class).meta;
            String scriptPath = Vars.mods.getMod(ANTIVPN.class).root.child("antivpn.py").file().getAbsolutePath();

            Process proc = new ProcessBuilder("python3", scriptPath, host)
                    .redirectErrorStream(true)
                    .start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line;
            boolean vpnDetected = false;

            while ((line = reader.readLine()) != null) {
                Log.info("VPN check output: @", line);
                if (line.contains("VPN:TRUE")) {
                    vpnDetected = true;
                }
            }

            proc.waitFor();
            return vpnDetected;
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
