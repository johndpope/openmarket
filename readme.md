
==================


[OpenMarket](http://tchoulihan.github.io/bitmerchant/) &mdash; A free, decentralized marketplace.
==========

**Currently in alpha testing**

OpenMarket is an open-source, decentralized marketplace, where you can sell anything in exchange for bitcoin. The network and website is hosted by any seller running software installed from this page. 

It comes with an integrated [BitcoinJ](https://github.com/bitcoinj/bitcoinj)-based [Bitcoin wallet](http://tchoulihan.github.io/bitmerchant/) so you can immediately start accepting payments without having to go through an intermediary service like coinbase or bitpay;


## Features include
* A complete
* A decentrally hosted database, built atop [rqlite](https://github.com/otoolep/rqlite).
* A fully portable website and service, built with java [Spark](https://github.com/perwendel/spark).
* An offline bitcoin [wallet](http://tchoulihan.github.io/bitmerchant/) that uses [BIP70](https://github.com/bitcoin/bips/blob/master/bip-0070.mediawiki).
* Refund orders at the click of a button.


## Screenshots:
<img src="http://i.imgur.com/V6BHKZy.png">


## Installation
### Requirements
- Java 8
- Go (version at least 1.4)
- If behind a router, make sure ports 4566-4570 are correctly forwarded to your local ip address.

Download the jar, located [here](https://github.com/tchoulihan/bitmerchant/releases/download/1.3/bitmerchant-shaded.jar)

To help test, use this command to join an existing test node that uses the bitcoin testnet:
<pre>java -jar openmarket.jar -testnet -join 96.28.13.51:4570</pre>


<pre>java -jar openmarket.jar [parameters]</pre>
<pre>parameters:
 -deleteDB     : Delete the sqlite DB before running.(Warning, this deletes
                 your wallets too)
 -join VAL     : Startup OpenMarket joining a master nodeIE, 127.0.0.1:4001
 -loglevel VAL : Sets the log level [INFO, DEBUG, etc.]
 -port N       : Startup your webserver on a different port(default is 4567)
 -testnet      : Run using the Bitcoin testnet3, and a test DB
 -uninstall    : Uninstall OpenMarket.(Warning, this deletes your wallets too)

</pre>

If accessing from another machine, vnc to the machine, or use a vpn service
http://localhost:4567/

## Building from scratch

To build OpenMarket, run the following commands:
```
git clone https://github.com/tchoulihan/openmarket
cd openmarket
chmod +x deploy.sh
./deploy.sh
```



## Support 
If you'd like to contribute to the project, you can either post bounties for desired features [here](https://www.bountysource.com/trackers/9805417-tchoulihan-bitmerchant), or click [this link](http://tchoulihan.github.io/bitmerchant/support.html).


## Thanks
* Special thanks to Mike Hearn and Andreas Schildbach for their assistance with BIP70 and refunding orders.

## Feature requests / todos
* 

