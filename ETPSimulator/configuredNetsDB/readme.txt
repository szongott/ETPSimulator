Due to privacy reasons, the configured networks collected during the study can not be published. This document describes of which form the input data for the simulator has to be.

The file configured.dat consists of all configured networks on the user's devices. They are formatted like the following example one per line:

<network-id>|<allowed key management>|"<ssid>"|<allowed group ciphers>

network-id:
	An app-specific id to correlate the data

allowed key management:
	The allowed key management schemes as described in http://developer.android.com/reference/android/net/wifi/WifiConfiguration.html#allowedKeyManagement

ssid:
	Only the ssid of the configured network, usually quoted

allowed group cipher:
	The allowed group ciphers of the network as described in http://developer.android.com/reference/android/net/wifi/WifiConfiguration.html#allowedGroupCiphers	