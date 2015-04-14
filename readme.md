
==================


[OpenMarket](http://tchoulihan.github.io/bitmerchant/) &mdash; A free, decentralized marketplace.
==========

**Currently in alpha testing**

OpenMarket is an open-source, decentralized marketplace, where you can sell anything in exchange for bitcoin. The network and website is hosted by the sellers running software installed from this page.


It comes with an integrated [Bitcoin wallet](http://tchoulihan.github.io/bitmerchant/) so you can immediately start accepting payments.



Bitmerchant is a full [Bitcoinj](https://github.com/bitcoinj/bitcoinj)-based merchant-services platform. It lets you accept bitcoin payments or donations on your own website, without having to go through an intermediary service like coinbase or bitpay; the purchases or donations go directly into a wallet that **you control**, running on your server.



For more information, head over to http://tchoulihan.github.io/bitmerchant/

## Features include
* A complete, decentrally hosted marketplace, in a slick bootstrap-based web GUI. 
* A fully-functioning, offline bitcoin [wallet](http://tchoulihan.github.io/bitmerchant/), 
* A well-documented [API](http://tchoulihan.github.io/bitmerchant/api.html).
* A slick [payment-button generator](http://imgur.com/a/laYYn) that can create orders using your own *native currency*.
* Refund orders at the click of a button.
* Uses the BIP70 Payment protocol to ensure correct payment amounts, and refund addresses.
* Implement your own SSL certs.


## Screenshots:
<img src="http://i.imgur.com/V6BHKZy.png">


## Installation
### Requirements
- Java 8
- Go (version at least 1.4)
- If behind a router, make sure ports 4566-4570 are correctly forwarded to your local ip address.

Download the jar, located [here](https://github.com/tchoulihan/bitmerchant/releases/download/1.3/bitmerchant-shaded.jar)

To help test, use this command to join an existing test node that uses the bitcoin testnet:
<pre>java -jar openmarket.jar -testnet -join 96.28.13.51:4002 -port 4569</pre>


<pre>java -jar openmarket.jar [parameters]</pre>
<pre>parameters:
	-testnet  : run on the bitcoin testnet3
	-deleteDB : delete the local database before starting
	-loglevel [INFO,WARN, etc] : Sets the log level
</pre>

If accessing from another machine, vnc to the machine, or use a vpn service, and access either
http://localhost:4567/ , or
https://localhost:4567/ once you've enabled ssl.

## Building from scratch

To build Bitmerchant, make sure you have both java 8, and maven installed. Then run the following commands:
```
git clone https://github.com/tchoulihan/bitmerchant
cd bitmerchant
mvn install
```
To run Bitmerchant:

<pre>
java -jar target/bitmerchant-shaded.jar [parameters]

or better, use the run script, which also creates a log.out:

./run.sh [parameters]

parameters:
	-testnet  : run on the bitcoin testnet3
	-deleteDB : delete the local database before starting
	-loglevel [INFO,WARN, etc] : Sets the log level
</pre>

If accessing from another machine, vnc to the machine, or use a vpn service, and access either
http://localhost:4567/ , or
https://localhost:4567/ once you've enabled ssl.



## Support 
If you'd like to contribute to the project, you can either post bounties for desired features [here](https://www.bountysource.com/trackers/9805417-tchoulihan-bitmerchant), or click [this link](http://tchoulihan.github.io/bitmerchant/support.html).


## Thanks
* Special thanks to Mike Hearn and Andreas Schildbach for their assistance with BIP70 and refunding orders.

## Feature requests / todos
* Increase memory params
* Possible buttercoin integration
* Add [-fullnode parameter]

