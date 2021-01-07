*A Bukkit 1092 plugin to block users from joining your server with a VPN*

---

**Features:**

* Block people from using VPNs on your server!
* Lookup player's IP info!
* Log player joins along with their IP and organization!
* Switch on/off the plugin with one command!
* An efficient IP caching system with expiration!
* Async, meaning no lag!

---

**Installation:**

1. Download AetherVPN.jar and place it in your plugins folder
2. Restart the server
5. Enjoy!

---

**Commands:**

    /aethervpn - View plugin information
    /aethervpn <enable/disable> - Enable or disable the VPN check
    /aethervpn clearcache
	/lookup <player> - View a player's IP information

**Permissions:**

This plugin supports PermissionsEx, YetiPermissions, and GroupManager (with Fake YetiPermissions bridge)

    aethervpn - /aethervpn
	aethervpn.lookup - /lookup
    aethervpn.alert - Get alerted when a player joins the server
    aethervpn.bypass - Bypass VPN check

---

**Configuration:**

This plugin uses vpnblocker.net for its API. Without an API key,
there is a limit of 500 monthly requests. Although there is a caching system in place
in order not to waste API queries, you may need more depending on your server size,
an unlimited plan is available for $5/month at https://vpnblocker.net/pricing.

---

**Building:**

In order to build the plugin, you need to download and add CraftBukkit1092.jar, 
Permissions.jar (Yeti Permissions), and PermissionsEx-1.12.jar to your `lib` folder.
Then you should be able to edit the plugin in your favorite IDE (Developed in IntelliJ IDEA)
and use Gradle to build it.

---

**Help:**

If you still need help feel free to contact me on Discord: Johnanater#6836

or on my Discord server: https://discord.gg/VTCzMVG

---	

**Love my work?**

Bitcoin: 1L9kdrW3hJ1abzJAKJwVGNrg3otAEmekY2

Ethereum: 0x43db5a4a44a57f0699c320dbf1131879ec831274

Ripple: rDrdhCVD79js6dTWHC1d6cdHjvj2hD3T1H

[![](https://www.paypalobjects.com/webstatic/en_US/btn/btn_donate_cc_147x47.png)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=7QEHYC457X5SW)
