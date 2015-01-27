Due to privacy reasons, the connection metadata of the study can not be published. This document describes of which form the input data for the simulator has to be.

The files in this directory are named <user-id>.result and consist all connections for this specific user. They are formatted as follows:

The following list consists of all entries, sperarated by the character "|". One connection per line.

pos	name 			description
-------------------------------------------------------
0 	id				connection id
1 	timestamp		timestamp of connection
2	duration		duration counter for competition lots
3	app version		version of the app that reported the connection
4	manufacturer	manufacturer of mobile device
5	device descr	manufacturer description of device
6	device code 	device code
7	install id		unique id for each installation
8	device id		unique id for each device
9	connect. net id	id of the currently connected network
10	seen Nets		comma-spearated list of ids of received networks 
11	location id		unique id for each location
12	screen state	was screen on/off during connection process?
13	lock state		was device locked/unlocked during connection process?
14	battery			battery state during connection process
15	simcard state	simcard state during connection process
16	cellID			cellID of current mobile connection
17	LAC 			location area code of current mobile connection
18	configured Nets	comma-spearated list of ids of configured networks 
19	connect. net id	id of the currently connected network
20	connected bssid	bssid of the currently connected network
21	connected ssid	ssid of the currently connected network
22	rssi			signal strength of the currently connected network
23	net speed		speed of the currently connected network
24	location id		unique id for each location
25	provider		location provider
26	location ts 	timestamp of the location
27	longitude		longitude of the location
28	accuracy		accuracy of the location
29	latitude		latitude of the location

