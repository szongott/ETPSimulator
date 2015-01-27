The file seenNets.dat consist of all networks that have been received during WiFi scans during the complete study by all users. It is formatted like this (one network per line):

<id>|<bssid>|<ssid>|<rssi>|<supported encryption schemes>|<frequency>

id:
	app-specific id of the received network

bssid:
	BSSID of the received access point

ssid:
	SSID of the received network

rssi:
	signal strength of the received access point

supported encryption schemes:
	supported encryption schemes of the received access point

	example 1: [WPA2-PSK-CCMP+TKIP-preauth][ESS]
	example 2: [WPA-PSK-TKIP][WPA2-PSK-CCMP][WPS][ESS]

 frequency:
 	frequency at which the access point is operated