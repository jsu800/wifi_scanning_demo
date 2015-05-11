##Wifi Scanner
This project illustrates the use of WiFi, cellular network, GPS, sensors, and a combination of those to arrive at proximity 
estimation mostly for indoor applications. A side module in this application uses Google's fused location provider API 
to arrive at location updates and Android's built in Activity sensors to determine user's indoor location and orientation
in real time.

##Signal Components
Wifi, Google Fused Location Provider, Cellular, GPS

## USAGE

#CALIBRATION MODE:

This mode can be used against a set of predetermined locations (which can be externalized, and not baked in app) to 
ascertain RSSIs from various WiFi access points (AP) per given locale. A map of signal fingerprints is generated.

An AP whilelist is provided to filter all unwanted access points out of the calibration equation. This whitelist can
and should also be externalized to enable ease of modification. 

Each filtered AP through the whilelist is assessed N # of times (6 by default) for its RSSI strength. An average RSSI is calculated per AP per location. 

Signal data are serialized and persisted on disk. Data can be wiped via the "WIPE ALL DATA" mode, in which case data persistency for calibration will be gone. Please exercise judgement when wiping data. Once gone a re-calibration is 
necessary. 

#MAP MODE - WIFI

This mode computes user location indoor in real time comparing to the prior calibration fingerprint stored on disk. 
Routing is also available in this mode providing real time routing visualization given two endpoints. 

Dijstra implementation for calculating the shorting path between any two endpoints traversing through a list of inter-
media vertices is available in the com.wifi.indoor.scanner.dijkstra package. Also included but not tested in the context
of this application yet.

#MAP MODE - GPS, WIFI, CELL NETWORK, SENSORS

This mode uses Google's fused location provider API combinating with its Activity provisioning to track user locations
indoor. WiFi is necessary to fetch Google map tiles but it is also used for indoor positioning estimation. User movement
is tracked through device's built-in gyro and accelerometer which enables movement update in map, in real time. 

##License 
/*
 * MIT License
 * Copyright (c) 2014 Joseph Su (http://www.github.com/jsu800)
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated 
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation 
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and 
 * to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of 
 * the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO 
 * THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 */
