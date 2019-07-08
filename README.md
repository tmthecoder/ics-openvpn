popcornVPN
=============
[![Build Status](https://travis-ci.org/tmthecoder/ics-openvpn.svg?branch=master)](https://travis-ci.org/schwabe/ics-openvpn)

Description
------------
When connected to a VPN for long periods of time, it's relatively easy to determine, from an outside perspective, that you're connected to a VPN. It then becomes rather easy to intercept traffic from that VPN. At this point, the security of your VPN is compromised.
This project is a fork of [Arne Schwabe's ics-openvpn](https://github.com/schwabe/ics-openvpn/)

Developing
---------------
If you want to develop on popcornVPN please read the [doc/README.txt](https://github.com/tmthecoder/ics-openvpn/blob/master/doc/README.txt) *before* opening issues or emailing me. 

Also please note that before contributing to the project that I would like to retain my ability to relicense the project for different third parties and therefore probably need a contributer's agreement from any contributing party. To get started, [sign the Contributor License Agreement](https://www.clahub.com/agreements/tmthecoder/ics-openvpn).

You can help
------------
Even if you are no programmer you can help by translating the popcornVPN client into your native language. [Crowdin provides a free service for non commercial open source projects](http://crowdin.net/project/ics-openvpn/invite) (Fixing/completing existing translations is very welcome as well)

FAQ
-----
You can find the FAQ here (same as in app): http://ics-openvpn.blinkt.de/FAQ.html

Controlling from external apps
------------------------------

There is the AIDL API for real controlling (see developing section). Due to high demand also the Activities `de.blinkt.openvpn.api.DisconnectVPN` and `de.blinkt.openvpn.api.ConnectVPN` exist. It uses `de.blinkt.openvpn.api.profileName` as extra for the name of the VPN profile.

Note to administrators
------------------------

You make your life and that of your users easier if you embed the certificates into the .ovpn file. You or the users can mail the .ovpn as a attachment to the phone and directly import and use it. Also downloading and importing the file works. The MIME Type should be application/x-openvpn-profile. 

Inline files are supported since OpenVPN 2.1rc1 and documented in the  [OpenVPN 2.3 man page](https://community.openvpn.net/openvpn/wiki/Openvpn23ManPage) (under INLINE FILE SUPPORT) 

(Using inline certifaces can also make your life on non-Android platforms easier since you only have one file.)

For example `ca mycafile.pem` becomes
```
  <ca>
  -----BEGIN CERTIFICATE-----
  MIIHPTCCBSWgAwIBAgIBADANBgkqhkiG9w0BAQQFADB5MRAwDgYDVQQKEwdSb290
  [...]
  -----END CERTIFICATE-----
  </ca>
```
Footnotes
-----------
Please note that OpenVPN used by this project is under GPLv2. 

If you cannot or do not want to use the Play Store you can [download the apk files directly](http://plai.de/android/).

The new Git repository is now at GitHub under https://github.com/tmthecoder/ics-openvpn

Please read the doc/README before asking questions or starting development.
