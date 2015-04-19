# install rqlite
go get github.com/otoolep/rqlite
export GOPATH=$PWD

# Startup 2 nodes
$GOPATH/bin/rqlite node_1 &
sleep 2s
$GOPATH/bin/rqlite -join localhost:4001 -p 4002 node_2 &
sleep 2s
$GOPATH/bin/rqlite -join localhost:4001 -p 4003 node_3 &
sleep 2s

# Do some big inserts, alternating XPOSTS between the two nodes
curl -L -XPOST localhost:4001/db?pretty -d 'CREATE TABLE "user" ("id" INTEGER DEFAULT NULL PRIMARY KEY AUTOINCREMENT,"email" TEXT DEFAULT NULL,"password_encrypted" TEXT DEFAULT NULL,"name" TEXT DEFAULT NULL,"authenticated" INTEGER DEFAULT NULL,"email_code" TEXT DEFAULT NULL,"created_at" INTEGER NOT NULL  DEFAULT CURRENT_TIMESTAMP,UNIQUE (email));
INSERT INTO "user" VALUES(1,"happydooby@gmail.com","zWyTu4eFKjXgNedQyjlA1WacfgcluFDGtxFmzU6iYL4G6jPXFqK1K3xuw+afjqzA",NULL,"true",NULL,"2015-04-14 00:29:16");
INSERT INTO "user" VALUES(2,"tchoulihan@gmail.com","mYmiuWZaS8bndTItTFYbe0lTbUOwoll1Vg23qtdBiEVSlQJpm7Ylqxk6kzptP1wh","derp","true",NULL,"2015-04-14 01:20:58");
CREATE TABLE "seller" ("id" INTEGER DEFAULT NULL PRIMARY KEY AUTOINCREMENT,"user_id" INTEGER DEFAULT NULL REFERENCES "user" ("id"),"shop_name" TEXT DEFAULT NULL,"bitmerchant_address" TEXT DEFAULT NULL,"created_at" INTEGER NOT NULL  DEFAULT CURRENT_TIMESTAMP,UNIQUE (user_id));
INSERT INTO "seller" VALUES(1,1,"garg","http://96.28.13.51:4567/","2015-04-14 00:29:16");
CREATE TABLE "product" ("id" INTEGER DEFAULT NULL PRIMARY KEY AUTOINCREMENT,"seller_id" INTEGER NOT NULL  DEFAULT NULL REFERENCES "seller" ("id"),"category_id" INTEGER DEFAULT NULL REFERENCES "category" ("id"),"buy" INTEGER DEFAULT NULL,"auction" INTEGER DEFAULT NULL,"quantity" INTEGER DEFAULT NULL,"title" TEXT DEFAULT NULL,"processing_time_span_id" INTEGER DEFAULT NULL REFERENCES "time_span" ("id"),"physical" INTEGER DEFAULT 0,"created_at" INTEGER NOT NULL  DEFAULT CURRENT_TIMESTAMP);
INSERT INTO "product" VALUES(1,1,3,NULL,NULL,NULL,"test_item",NULL,0,"2015-04-14 00:30:45");
INSERT INTO "product" VALUES(2,1,19,NULL,0,10,"Pink frosted sprinkled donut",3,0,"2015-04-14 01:02:24");
INSERT INTO "product" VALUES(3,1,NULL,NULL,NULL,NULL,NULL,NULL,0,"2015-04-14 14:09:32");
INSERT INTO "product" VALUES(4,1,18,NULL,0,NULL,"A tasty red donut",NULL,0,"2015-04-14 14:10:00");
INSERT INTO "product" VALUES(5,1,25,NULL,0,5,"A Chocalatey donut",3,0,"2015-04-14 14:10:45");
CREATE TABLE "shipment" ("id" INTEGER DEFAULT NULL PRIMARY KEY AUTOINCREMENT,"address_id" INTEGER DEFAULT NULL REFERENCES "address" ("id"),"tracking_url" TEXT DEFAULT NULL,"created_at" INTEGER NOT NULL  DEFAULT CURRENT_TIMESTAMP);
INSERT INTO "shipment" VALUES(1,1,NULL,"2015-04-14 01:14:21");
INSERT INTO "shipment" VALUES(2,2,NULL,"2015-04-14 01:22:16");
INSERT INTO "shipment" VALUES(3,2,NULL,"2015-04-14 13:02:23");
CREATE TABLE "review" ("id" INTEGER DEFAULT NULL PRIMARY KEY AUTOINCREMENT,"product_id" INTEGER DEFAULT NULL REFERENCES "product" ("id"),"user_id" INTEGER DEFAULT NULL REFERENCES "user" ("id"),"stars" INTEGER DEFAULT NULL,"headline" TEXT DEFAULT NULL,"text_html" TEXT DEFAULT NULL,"created_at" INTEGER NOT NULL  DEFAULT CURRENT_TIMESTAMP,UNIQUE (product_id, user_id));
INSERT INTO "review" VALUES(1,2,1,3,"Great donut","Would eat again. 10/10&ltsemicolonbr&gtsemicolon","2015-04-14 14:19:43");
CREATE TABLE "address" ("id" INTEGER DEFAULT NULL PRIMARY KEY AUTOINCREMENT,"user_id" INTEGER DEFAULT NULL REFERENCES "user" ("id"),"full_name" TEXT DEFAULT NULL,"address_line_1" TEXT DEFAULT NULL,"address_line_2" TEXT DEFAULT NULL,"city" TEXT DEFAULT NULL,"state" TEXT DEFAULT NULL,"zip" INTEGER DEFAULT NULL,"country_id" INTEGER DEFAULT NULL REFERENCES "country" ("id"),"default_" INTEGER DEFAULT NULL,"created_at" INTEGER NOT NULL  DEFAULT CURRENT_TIMESTAMP);
INSERT INTO "address" VALUES(1,1,"fsdafasdf","asdf","asdf","asdf","fdsa",1235,7,0,"2015-04-14 01:14:18");
INSERT INTO "address" VALUES(2,2,"fdsafd","asdf","asdf","asdf","asdf",123,8,0,"2015-04-14 01:22:12");
CREATE TABLE "feedback" ("id" INTEGER DEFAULT NULL PRIMARY KEY AUTOINCREMENT,"cart_item_id" INTEGER DEFAULT NULL REFERENCES "cart_item" ("id"),"stars" INTEGER DEFAULT NULL,"arrived_on_time" INTEGER DEFAULT NULL,"correctly_described" INTEGER DEFAULT NULL,"prompt_service" INTEGER DEFAULT NULL,"comments" TEXT DEFAULT NULL,"created_at" INTEGER NOT NULL  DEFAULT CURRENT_TIMESTAMP);
INSERT INTO "feedback" VALUES(1,2,NULL,NULL,NULL,NULL,NULL,"2015-04-14 13:02:22");
CREATE TABLE "cart_item" ("id" INTEGER DEFAULT NULL PRIMARY KEY AUTOINCREMENT,"user_id" INTEGER DEFAULT NULL REFERENCES "user" ("id"),"product_id" INTEGER DEFAULT NULL REFERENCES "product" ("id"),"quantity" INTEGER DEFAULT NULL,"purchased" INTEGER DEFAULT 0,"shipment_id" INTEGER DEFAULT NULL REFERENCES "shipment" ("id"),"payment_id" INTEGER DEFAULT NULL REFERENCES "payment" ("id"),"created_at" INTEGER NOT NULL  DEFAULT CURRENT_TIMESTAMP);
INSERT INTO "cart_item" VALUES(1,1,2,1,0,1,1,"2015-04-14 01:11:49");
INSERT INTO "cart_item" VALUES(2,2,2,1,1,2,3,"2015-04-14 01:21:52");
INSERT INTO "cart_item" VALUES(3,2,2,1,0,3,4,"2015-04-14 13:02:22");
CREATE TABLE "payment" ("id" INTEGER DEFAULT NULL PRIMARY KEY AUTOINCREMENT,"order_iframe" TEXT DEFAULT NULL,"completed" INTEGER DEFAULT 0,"created_at" INTEGER NOT NULL  DEFAULT CURRENT_TIMESTAMP);
INSERT INTO "payment" VALUES(1,"<iframe id=doublequotebitmerchant_iframedoublequote name=doublequote4doublequote src=doublequotehttp://104.236.44.89:4569/html/payment_iframe.htmldoublequote  style=doublequotewidth: 460pxsemicolon height: 300pxsemicolon border: nonesemicolon doublequote allowtransparency=doublequotetruedoublequote frameborder=doublequote0doublequote white-space=doublequotenowrapdoublequote></iframe>",0,"2015-04-14 01:13:57");
INSERT INTO "payment" VALUES(2,NULL,0,"2015-04-14 01:22:01");
INSERT INTO "payment" VALUES(3,"<iframe id=doublequotebitmerchant_iframedoublequote name=doublequote1doublequote src=doublequotehttp://96.28.13.51:4567/html/payment_iframe.htmldoublequote  style=doublequotewidth: 460pxsemicolon height: 300pxsemicolon border: nonesemicolon doublequote allowtransparency=doublequotetruedoublequote frameborder=doublequote0doublequote white-space=doublequotenowrapdoublequote></iframe>",1,"2015-04-14 01:23:05");
INSERT INTO "payment" VALUES(4,"<iframe id=doublequotebitmerchant_iframedoublequote name=doublequote3doublequote src=doublequotehttp://96.28.13.51:4567/html/payment_iframe.htmldoublequote  style=doublequotewidth: 460pxsemicolon height: 300pxsemicolon border: nonesemicolon doublequote allowtransparency=doublequotetruedoublequote frameborder=doublequote0doublequote white-space=doublequotenowrapdoublequote></iframe>",0,"2015-04-14 12:48:01");
CREATE TABLE "bid" ("id" INTEGER DEFAULT NULL PRIMARY KEY AUTOINCREMENT,"auction_id" INTEGER DEFAULT NULL REFERENCES "auction" ("id"),"user_id" INTEGER DEFAULT NULL REFERENCES "user" ("id"),"amount" INTEGER DEFAULT NULL,"time" INTEGER DEFAULT NULL,"created_at" INTEGER NOT NULL  DEFAULT CURRENT_TIMESTAMP);
CREATE TABLE "auction" ("id" INTEGER DEFAULT NULL PRIMARY KEY AUTOINCREMENT,"product_id" INTEGER DEFAULT NULL REFERENCES "product" ("id"),"expire_time" INTEGER DEFAULT NULL,"start_amount" NUMERIC DEFAULT NULL,"currency_id" INTEGER DEFAULT NULL REFERENCES "currency" ("id"),"reserve_amount" INTEGER DEFAULT NULL,"created_at" INTEGER NOT NULL  DEFAULT CURRENT_TIMESTAMP,UNIQUE (product_id));
INSERT INTO "auction" VALUES(1,2,NULL,NULL,2,NULL,"2015-04-14 01:09:43");
'
curl -L -XPOST localhost:4002/db?pretty -d 'INSERT INTO "auction" VALUES(2,4,NULL,NULL,2,NULL,"2015-04-14 14:10:26");
INSERT INTO "auction" VALUES(3,5,NULL,NULL,3,NULL,"2015-04-14 14:11:14");
CREATE TABLE "currency" ("id" INTEGER DEFAULT NULL PRIMARY KEY AUTOINCREMENT,"desc" TEXT DEFAULT NULL,"iso" TEXT DEFAULT NULL,"unicode" TEXT DEFAULT NULL,"created_at" INTEGER NOT NULL  DEFAULT CURRENT_TIMESTAMP);
INSERT INTO "currency" VALUES(1,"Bitcoin","BTC","?","2015-04-14 00:27:36");
INSERT INTO "currency" VALUES(2,"United States Dollar","USD","$","2015-04-14 00:27:36");
INSERT INTO "currency" VALUES(3,"Euro","EUR","?","2015-04-14 00:27:37");
INSERT INTO "currency" VALUES(4,"British Pound Sterling","GBP","?","2015-04-14 00:27:37");
INSERT INTO "currency" VALUES(5,"Australian Dollar","AUD","$","2015-04-14 00:27:37");
INSERT INTO "currency" VALUES(6,"Brazilian Real","BRL","R$","2015-04-14 00:27:37");
INSERT INTO "currency" VALUES(7,"Canadian Dollar","CAD","$","2015-04-14 00:27:37");
INSERT INTO "currency" VALUES(8,"Swiss Franc","CHF","?","2015-04-14 00:27:37");
INSERT INTO "currency" VALUES(9,"Chinese Yuan","CNY","?","2015-04-14 00:27:37");
INSERT INTO "currency" VALUES(10,"Hong Kong Dollar","HKD","$","2015-04-14 00:27:37");
INSERT INTO "currency" VALUES(11,"Indonesian Rupiah","IDR","?","2015-04-14 00:27:38");
INSERT INTO "currency" VALUES(12,"Israeli New Sheqel","ILS","?","2015-04-14 00:27:38");
INSERT INTO "currency" VALUES(13,"Mexican Peso","MXN","?","2015-04-14 00:27:38");
INSERT INTO "currency" VALUES(14,"Norwegian Krone","NOK","kr","2015-04-14 00:27:38");
INSERT INTO "currency" VALUES(15,"New Zealand Dollar","NZD","$","2015-04-14 00:27:38");
INSERT INTO "currency" VALUES(16,"Polish Zloty","PLN","z","2015-04-14 00:27:38");
INSERT INTO "currency" VALUES(17,"Romanian Leu","RON","leu","2015-04-14 00:27:38");
INSERT INTO "currency" VALUES(18,"Russian Ruble","RUB","?","2015-04-14 00:27:39");
INSERT INTO "currency" VALUES(19,"Swedish Krona","SEK","kr","2015-04-14 00:27:39");
INSERT INTO "currency" VALUES(20,"Singapore Dollar","SGD","$","2015-04-14 00:27:39");
INSERT INTO "currency" VALUES(21,"Turkish Lira","TRY","?","2015-04-14 00:27:39");
INSERT INTO "currency" VALUES(22,"South African Rand","ZAR","R","2015-04-14 00:27:39");
CREATE TABLE "country" ("id" INTEGER DEFAULT NULL PRIMARY KEY AUTOINCREMENT,"name" TEXT DEFAULT NULL,"country_code" TEXT DEFAULT NULL,"created_at" INTEGER NOT NULL  DEFAULT CURRENT_TIMESTAMP);
INSERT INTO "country" VALUES(1,"United States","US","2015-04-14 00:27:39");
INSERT INTO "country" VALUES(2,"Afghanistan","AF","2015-04-14 00:27:39");
'
curl -L -XPOST localhost:4001/db?pretty -d 'INSERT INTO "country" VALUES(3,"�land Islands","AX","2015-04-14 00:27:40");
INSERT INTO "country" VALUES(4,"Albania","AL","2015-04-14 00:27:40");
INSERT INTO "country" VALUES(5,"Algeria","DZ","2015-04-14 00:27:40");
INSERT INTO "country" VALUES(6,"American Samoa","AS","2015-04-14 00:27:40");
INSERT INTO "country" VALUES(7,"Andorra","AD","2015-04-14 00:27:40");
INSERT INTO "country" VALUES(8,"Angola","AO","2015-04-14 00:27:40");
INSERT INTO "country" VALUES(9,"Anguilla","AI","2015-04-14 00:27:40");
INSERT INTO "country" VALUES(10,"Antarctica","AQ","2015-04-14 00:27:40");
INSERT INTO "country" VALUES(11,"Antigua And Barbuda","AG","2015-04-14 00:27:41");
INSERT INTO "country" VALUES(12,"Argentina","AR","2015-04-14 00:27:41");
INSERT INTO "country" VALUES(13,"Armenia","AM","2015-04-14 00:27:41");
INSERT INTO "country" VALUES(14,"Aruba","AW","2015-04-14 00:27:41");
INSERT INTO "country" VALUES(15,"Australia","AU","2015-04-14 00:27:41");
INSERT INTO "country" VALUES(16,"Austria","AT","2015-04-14 00:27:41");
INSERT INTO "country" VALUES(17,"Azerbaijan","AZ","2015-04-14 00:27:41");
INSERT INTO "country" VALUES(18,"Bahamas","BS","2015-04-14 00:27:41");
INSERT INTO "country" VALUES(19,"Bahrain","BH","2015-04-14 00:27:42");
INSERT INTO "country" VALUES(20,"Bangladesh","BD","2015-04-14 00:27:42");
INSERT INTO "country" VALUES(21,"Barbados","BB","2015-04-14 00:27:42");
INSERT INTO "country" VALUES(22,"Belarus","BY","2015-04-14 00:27:42");
INSERT INTO "country" VALUES(23,"Belgium","BE","2015-04-14 00:27:42");
INSERT INTO "country" VALUES(24,"Belize","BZ","2015-04-14 00:27:42");
INSERT INTO "country" VALUES(25,"Benin","BJ","2015-04-14 00:27:42");
CREATE TABLE "review_vote" ("id" INTEGER DEFAULT NULL PRIMARY KEY AUTOINCREMENT,"user_id" INTEGER DEFAULT NULL REFERENCES "user" ("id"),"review_id" INTEGER DEFAULT NULL REFERENCES "review" ("id"),"vote" NUMERIC DEFAULT NULL,"created_at" INTEGER NOT NULL  DEFAULT CURRENT_TIMESTAMP,UNIQUE (user_id, review_id));
INSERT INTO "review_vote" VALUES(1,1,1,"up","2015-04-14 14:20:26");
CREATE TABLE "review_comment" ("id" INTEGER DEFAULT NULL PRIMARY KEY AUTOINCREMENT,"user_id" INTEGER DEFAULT NULL REFERENCES "user" ("id"),"review_id" INTEGER DEFAULT NULL REFERENCES "review" ("id"),"comment" TEXT DEFAULT NULL,"created_at" INTEGER NOT NULL  DEFAULT CURRENT_TIMESTAMP);
CREATE TABLE "question" ("id" INTEGER DEFAULT NULL PRIMARY KEY AUTOINCREMENT,"user_id" INTEGER DEFAULT NULL REFERENCES "user" ("id"),"product_id" INTEGER DEFAULT NULL REFERENCES "product" ("id"),"text" TEXT DEFAULT NULL,"created_at" INTEGER NOT NULL  DEFAULT CURRENT_TIMESTAMP);
CREATE TABLE "answer" ("id" INTEGER DEFAULT NULL PRIMARY KEY AUTOINCREMENT,"user_id" INTEGER DEFAULT NULL REFERENCES "user" ("id"),"question_id" INTEGER DEFAULT NULL REFERENCES "question" ("id"),"text" TEXT DEFAULT NULL,"created_at" INTEGER NOT NULL  DEFAULT CURRENT_TIMESTAMP);
CREATE TABLE "question_vote" ("id" INTEGER DEFAULT NULL PRIMARY KEY AUTOINCREMENT,"question_id" INTEGER DEFAULT NULL REFERENCES "question" ("id"),"user_id" INTEGER DEFAULT NULL,"vote" NUMERIC DEFAULT NULL,"created_at" INTEGER NOT NULL  DEFAULT CURRENT_TIMESTAMP);
CREATE TABLE "answer_vote" ("id" INTEGER DEFAULT NULL PRIMARY KEY AUTOINCREMENT,"user_id" INTEGER DEFAULT NULL REFERENCES "user" ("id"),"answer_id" INTEGER DEFAULT NULL REFERENCES "answer" ("id"),"vote" NUMERIC DEFAULT NULL,"created_at" INTEGER NOT NULL  DEFAULT CURRENT_TIMESTAMP);
CREATE TABLE "category" ("id" INTEGER DEFAULT NULL PRIMARY KEY AUTOINCREMENT,"name" TEXT DEFAULT NULL,"parent" INTEGER DEFAULT NULL,"is_physical" INTEGER DEFAULT true,"created_at" INTEGER NOT NULL  DEFAULT CURRENT_TIMESTAMP);
INSERT INTO "category" VALUES(1,"Animals & Pet Supplies",NULL,"true","2015-04-14 00:27:29");
INSERT INTO "category" VALUES(2,"Live Animals",1,"true","2015-04-14 00:27:29");
INSERT INTO "category" VALUES(3,"Pet Supplies",1,"true","2015-04-14 00:27:29");
INSERT INTO "category" VALUES(4,"Bird Supplies",3,"true","2015-04-14 00:27:30");
INSERT INTO "category" VALUES(5,"Bird Cage Accessories",4,"true","2015-04-14 00:27:30");
INSERT INTO "category" VALUES(6,"Bird Cage Food & Water Dishes",5,"true","2015-04-14 00:27:30");
INSERT INTO "category" VALUES(7,"Bird Cages & Stands",4,"true","2015-04-14 00:27:30");
INSERT INTO "category" VALUES(8,"Bird Food",4,"true","2015-04-14 00:27:30");
'
curl -L -XPOST localhost:4002/db?pretty -d 'INSERT INTO "category" VALUES(9,"Bird Gyms & Playstands",4,"true","2015-04-14 00:27:31");
INSERT INTO "category" VALUES(10,"Bird Ladders & Perches",4,"true","2015-04-14 00:27:31");
INSERT INTO "category" VALUES(11,"Bird Toys",4,"true","2015-04-14 00:27:31");
INSERT INTO "category" VALUES(12,"Bird Treats",4,"true","2015-04-14 00:27:31");
INSERT INTO "category" VALUES(13,"Cat Supplies",3,"true","2015-04-14 00:27:31");
INSERT INTO "category" VALUES(14,"Cat Apparel",13,"true","2015-04-14 00:27:31");
INSERT INTO "category" VALUES(15,"Cat Beds",13,"true","2015-04-14 00:27:31");
INSERT INTO "category" VALUES(16,"Cat Food",13,"true","2015-04-14 00:27:31");
INSERT INTO "category" VALUES(17,"Cat Furniture",13,"true","2015-04-14 00:27:32");
INSERT INTO "category" VALUES(18,"Cat Litter",13,"true","2015-04-14 00:27:32");
INSERT INTO "category" VALUES(19,"Cat Litter Box Mats",13,"true","2015-04-14 00:27:32");
INSERT INTO "category" VALUES(20,"Cat Litter Boxes",13,"true","2015-04-14 00:27:32");
INSERT INTO "category" VALUES(21,"Cat Toys",13,"true","2015-04-14 00:27:32");
INSERT INTO "category" VALUES(22,"Cat Treats",13,"true","2015-04-14 00:27:32");
INSERT INTO "category" VALUES(23,"Dog Supplies",3,"true","2015-04-14 00:27:32");
INSERT INTO "category" VALUES(24,"Dog Apparel",23,"true","2015-04-14 00:27:33");
INSERT INTO "category" VALUES(25,"Dog Beds",23,"true","2015-04-14 00:27:33");
CREATE TABLE "wishlist_item" ("id" INTEGER DEFAULT NULL PRIMARY KEY AUTOINCREMENT,"user_id" INTEGER DEFAULT NULL REFERENCES "user" ("id"),"product_id" INTEGER DEFAULT NULL REFERENCES "product" ("id"),"purchased" INTEGER DEFAULT NULL,"created_at" INTEGER NOT NULL  DEFAULT CURRENT_TIMESTAMP);
INSERT INTO "wishlist_item" VALUES(1,1,2,0,"2015-04-14 01:11:47");
CREATE TABLE "login" ("id" INTEGER DEFAULT NULL PRIMARY KEY AUTOINCREMENT,"user_id" INTEGER DEFAULT NULL REFERENCES "user" ("id"),"session_id" TEXT DEFAULT NULL,"time_" INTEGER DEFAULT NULL,"expire_time" INTEGER DEFAULT NULL,"created_at" INTEGER NOT NULL  DEFAULT CURRENT_TIMESTAMP);
INSERT INTO "login" VALUES(1,1,"1sdfgs5kqivfofa2tum79g4301469s41h3e6d2qquntkf9umr2oh",1428971430539,1428974638358,"2015-04-14 00:30:30");
INSERT INTO "login" VALUES(2,1,"1cmefbba3hk8kfbjl74kki02nav3h23fuj6jpod2sfp4ratjm9sp",1428973901740,1428974447001,"2015-04-14 01:11:42");
INSERT INTO "login" VALUES(3,2,"1opvct1n4h7tlk8r1onpkoup8pi8b9duhfo3i8a4s7tcb6rtj38b",1428974487159,1428978087159,"2015-04-14 01:21:27");
INSERT INTO "login" VALUES(4,1,"bhmv5etav7rbhhk3kur835u41hb2veg0av6gs00mqm964u90j5j",1428974645457,1428978245457,"2015-04-14 01:24:05");
INSERT INTO "login" VALUES(5,2,"q0flrr0bvh7mkr8vnvktshj1ago6dsc7icdq25tdr0ok3kfrvhu",1429015664731,1429019264731,"2015-04-14 13:02:22");
INSERT INTO "login" VALUES(6,1,"1cd6q1o14i65609a3dqr85kk1e91ulacj6l4b6di5tt5l8mfq84e",1429015923407,1429016564440,"2015-04-14 13:02:23");
INSERT INTO "login" VALUES(7,1,"1g0jknpm2ad6n0skkjh9je6120qrogj4d5b88hvf5h9lt2go2pui",1429016579502,1429020179502,"2015-04-14 13:06:04");
INSERT INTO "login" VALUES(8,1,"1v5fr7bm6c5dq5tahdujdon0u5ujpecft4ranpr8rml722ch9e1o",1429016676795,1429016685373,"2015-04-14 13:06:04");
INSERT INTO "login" VALUES(9,1,"4vjp9v6qj14lpt4fitkg5vm5p72pvdn14hlp5m2s9ml8nka6lc0",1429016793426,1429016802385,"2015-04-14 13:06:33");
INSERT INTO "login" VALUES(10,1,"ci0pm52vg36kat3p9821m51rptrbdi3lji2lg8a0u3audkc22r",1429016822694,1429017095173,"2015-04-14 13:38:54");
INSERT INTO "login" VALUES(11,1,"1nt8r0qira5qvntv11iuc6udvjjtmitnbnuielmrouqabu7ua19m",1429017113246,1429020713246,"2015-04-14 13:38:54");
INSERT INTO "login" VALUES(12,1,"1mcse38fgklq0odhus34e7g1gdbvu2rtpcivvu6k0f37vdsoo7oj",1429021175749,1429024775749,"2015-04-14 14:19:36");
CREATE TABLE "product_price" ("id" INTEGER DEFAULT NULL PRIMARY KEY AUTOINCREMENT,"product_id" INTEGER DEFAULT NULL REFERENCES "product" ("id"),"price" INTEGER DEFAULT NULL,"native_currency_id" INTEGER DEFAULT NULL REFERENCES "currency" ("id"),"variable_price" INTEGER DEFAULT NULL,"price_select" INTEGER DEFAULT NULL,"price_1" INTEGER DEFAULT NULL,"price_2" INTEGER DEFAULT NULL,"price_3" INTEGER DEFAULT NULL,"price_4" INTEGER DEFAULT NULL,"price_5" INTEGER DEFAULT NULL,"created_at" INTEGER NOT NULL  DEFAULT CURRENT_TIMESTAMP,UNIQUE (product_id));
INSERT INTO "product_price" VALUES(1,2,2.5,2,0,0,NULL,NULL,NULL,NULL,NULL,"2015-04-14 01:07:20");
INSERT INTO "product_price" VALUES(2,4,1.99,2,0,0,NULL,NULL,NULL,NULL,NULL,"2015-04-14 14:10:26");
INSERT INTO "product_price" VALUES(3,5,1.96,3,0,0,NULL,NULL,NULL,NULL,NULL,"2015-04-14 14:11:14");
CREATE TABLE "product_group" ("id" INTEGER DEFAULT NULL PRIMARY KEY AUTOINCREMENT,"created_at" INTEGER NOT NULL  DEFAULT CURRENT_TIMESTAMP);
CREATE TABLE "product_group_item" ("id" INTEGER DEFAULT NULL PRIMARY KEY AUTOINCREMENT,"product_group" INTEGER DEFAULT NULL REFERENCES "product_group" ("id"),"product_id" INTEGER DEFAULT NULL REFERENCES "product" ("id"),"created_at" INTEGER NOT NULL  DEFAULT CURRENT_TIMESTAMP);
CREATE TABLE "product_page" ("id" INTEGER DEFAULT NULL PRIMARY KEY AUTOINCREMENT,"product_id" INTEGER DEFAULT NULL REFERENCES "product" ("id"),"product_html" TEXT DEFAULT NULL,"created_at" INTEGER NOT NULL  DEFAULT CURRENT_TIMESTAMP,UNIQUE (product_id));
INSERT INTO "product_page" VALUES(1,2,"&ltsemicolonh1 style=&quotsemicolontext-align: centersemicolon&quotsemicolon&gtsemicolonThe best donut ever made&ltsemicolon/h1&gtsemicolon&ltsemicolonblockquote&gtsemicolon&ltsemicolonp style=&quotsemicolon text-align: leftsemicolon&quotsemicolon&gtsemicolon&ltsemicolonspan style=&quotsemicolonfont-weight: boldsemicolon&quotsemicolon&gtsemicolonSaid everyone&ltsemicolon/span&gtsemicolon&ltsemicolonbr&gtsemicolon&ltsemicolon/p&gtsemicolon&ltsemicolon/blockquote&gtsemicolon","2015-04-14 14:06:56");
CREATE TABLE "product_picture" ("id" INTEGER DEFAULT NULL PRIMARY KEY AUTOINCREMENT,"product_id" INTEGER DEFAULT NULL REFERENCES "product" ("id"),"num_" INTEGER DEFAULT NULL,"url" TEXT DEFAULT NULL,"created_at" INTEGER NOT NULL  DEFAULT CURRENT_TIMESTAMP);
INSERT INTO "product_picture" VALUES(1,2,1,"http://vignette1.wikia.nocookie.net/smosh/images/b/b2/Pink_frosted_sprinkled_donut.jpg/revision/latest?cb=20120101131536","2015-04-14 14:06:16");
INSERT INTO "product_picture" VALUES(2,4,1,"http://jennyfunderburke.com/blog/wp-content/uploads/2011/08/donut.jpg","2015-04-14 14:10:07");
INSERT INTO "product_picture" VALUES(3,5,1,"http://www.withsprinklesontop.net/wp-content/uploads/2012/01/DSC_0406x900.jpg","2015-04-14 14:11:36");
CREATE TABLE "tag" ("id" INTEGER DEFAULT NULL PRIMARY KEY AUTOINCREMENT,"title" TEXT DEFAULT NULL,"created_at" INTEGER NOT NULL  DEFAULT CURRENT_TIMESTAMP);
CREATE TABLE "product_tag" ("id" INTEGER DEFAULT NULL PRIMARY KEY AUTOINCREMENT,"tag_id" INTEGER DEFAULT NULL REFERENCES "tag" ("id"),"product_id" INTEGER DEFAULT NULL REFERENCES "product" ("id"),"created_at" INTEGER NOT NULL  DEFAULT CURRENT_TIMESTAMP);
CREATE TABLE "time_type" ("id" INTEGER DEFAULT NULL PRIMARY KEY AUTOINCREMENT,"name" TEXT DEFAULT NULL,"created_at" INTEGER NOT NULL  DEFAULT CURRENT_TIMESTAMP);
INSERT INTO "time_type" VALUES(1,"business days","2015-04-14 00:27:35");
INSERT INTO "time_type" VALUES(2,"weeks","2015-04-14 00:27:35");
CREATE TABLE "time_span" ("id" INTEGER DEFAULT NULL PRIMARY KEY AUTOINCREMENT,"time_type_id" INTEGER DEFAULT NULL REFERENCES "time_type" ("id"),"min" INTEGER DEFAULT NULL,"max" INTEGER DEFAULT NULL,"created_at" INTEGER NOT NULL  DEFAULT CURRENT_TIMESTAMP);
INSERT INTO "time_span" VALUES(1,1,1,2,"2015-04-14 00:27:35");
INSERT INTO "time_span" VALUES(2,1,1,3,"2015-04-14 00:27:35");
INSERT INTO "time_span" VALUES(3,1,3,5,"2015-04-14 00:27:35");
INSERT INTO "time_span" VALUES(4,2,1,2,"2015-04-14 00:27:36");
INSERT INTO "time_span" VALUES(5,2,2,3,"2015-04-14 00:27:36");
INSERT INTO "time_span" VALUES(6,2,3,4,"2015-04-14 00:27:36");
INSERT INTO "time_span" VALUES(7,2,4,6,"2015-04-14 00:27:36");
INSERT INTO "time_span" VALUES(8,2,6,8,"2015-04-14 00:27:36");
CREATE TABLE "shipping" ("id" INTEGER DEFAULT NULL PRIMARY KEY AUTOINCREMENT,"product_id" INTEGER DEFAULT NULL REFERENCES "product" ("id"),"from_country_id" INTEGER DEFAULT NULL REFERENCES "country" ("id"),"created_at" INTEGER NOT NULL  DEFAULT CURRENT_TIMESTAMP,UNIQUE (product_id));
CREATE TABLE "shipping_cost" ("id" INTEGER DEFAULT NULL PRIMARY KEY AUTOINCREMENT,"shipping_id" INTEGER DEFAULT NULL REFERENCES "shipping" ("id"),"to_country_id" INTEGER DEFAULT NULL REFERENCES "country" ("id"),"num_" INTEGER DEFAULT NULL,"price" INTEGER DEFAULT NULL,"native_currency_id" INTEGER DEFAULT NULL REFERENCES "currency" ("id"),"created_at" INTEGER NOT NULL  DEFAULT CURRENT_TIMESTAMP);
CREATE TABLE "product_bullet" ("id" INTEGER DEFAULT NULL PRIMARY KEY AUTOINCREMENT,"product_id" INTEGER DEFAULT NULL REFERENCES "product" ("id"),"num_" INTEGER DEFAULT NULL,"text" TEXT DEFAULT NULL,"created_at" INTEGER NOT NULL  DEFAULT CURRENT_TIMESTAMP);
INSERT INTO "product_bullet" VALUES(1,2,1,"This is a Pink frosted sprinkled donut","2015-04-14 14:05:51");
INSERT INTO "product_bullet" VALUES(2,2,2,"It has sprinkles","2015-04-14 14:05:57");
INSERT INTO "product_bullet" VALUES(3,2,3,"It has frosting","2015-04-14 14:06:12");
CREATE TABLE "payment_type" ("id" INTEGER DEFAULT NULL PRIMARY KEY AUTOINCREMENT,"name" TEXT DEFAULT NULL,"created_at" INTEGER NOT NULL  DEFAULT CURRENT_TIMESTAMP);
CREATE TABLE "payment_info" ("id" INTEGER DEFAULT NULL PRIMARY KEY AUTOINCREMENT,"seller_id" INTEGER DEFAULT NULL REFERENCES "seller" ("id"),"payment_type_id" INTEGER DEFAULT NULL REFERENCES "payment_type" ("id"),"payment_info_location" TEXT DEFAULT NULL,"created_at" INTEGER NOT NULL  DEFAULT CURRENT_TIMESTAMP);
CREATE TABLE "message" ("id" INTEGER DEFAULT NULL PRIMARY KEY AUTOINCREMENT,"from_user_id" INTEGER DEFAULT NULL REFERENCES "user" ("id"),"to_user_id" INTEGER DEFAULT NULL REFERENCES "user" ("id"),"subject" TEXT DEFAULT NULL,"html" TEXT DEFAULT NULL,"message_status_id" INTEGER DEFAULT NULL REFERENCES "message_status" ("id"),"created_at" INTEGER DEFAULT CURRENT_TIMESTAMP);
CREATE TABLE "message_status" ("id" INTEGER DEFAULT NULL PRIMARY KEY AUTOINCREMENT,"title" INTEGER DEFAULT NULL,"created_at" INTEGER DEFAULT CURRENT_TIMESTAMP);
DELETE FROM sqlite_sequence;
INSERT INTO "sqlite_sequence" VALUES("category",25);
INSERT INTO "sqlite_sequence" VALUES("time_type",2);
INSERT INTO "sqlite_sequence" VALUES("time_span",8);
INSERT INTO "sqlite_sequence" VALUES("currency",22);
INSERT INTO "sqlite_sequence" VALUES("country",25);
INSERT INTO "sqlite_sequence" VALUES("user",2);
INSERT INTO "sqlite_sequence" VALUES("seller",1);
INSERT INTO "sqlite_sequence" VALUES("login",12);
INSERT INTO "sqlite_sequence" VALUES("product",5);
INSERT INTO "sqlite_sequence" VALUES("product_price",3);
INSERT INTO "sqlite_sequence" VALUES("auction",3);
INSERT INTO "sqlite_sequence" VALUES("wishlist_item",1);
INSERT INTO "sqlite_sequence" VALUES("cart_item",3);
'
curl localhost:4001/raft?pretty
curl localhost:4002/raft?pretty

curl -L -XPOST localhost:4001/db?pretty -d 'INSERT INTO "sqlite_sequence" VALUES("payment",4);
INSERT INTO "sqlite_sequence" VALUES("address",2);
INSERT INTO "sqlite_sequence" VALUES("shipment",3);
INSERT INTO "sqlite_sequence" VALUES("feedback",1);
INSERT INTO "sqlite_sequence" VALUES("product_bullet",3);
INSERT INTO "sqlite_sequence" VALUES("product_picture",3);
INSERT INTO "sqlite_sequence" VALUES("product_page",1);
INSERT INTO "sqlite_sequence" VALUES("review",1);
INSERT INTO "sqlite_sequence" VALUES("review_vote",1);
CREATE VIEW time_span_view AS SELECT time_span.id, min || "-" || max || " " || name as time_span_string FROM time_span left join time_type on time_span.time_type_id = time_type.id;
CREATE VIEW product_view AS SELECT product.id,seller_id,shop_name,category_id,buy,auction,quantity,title,processing_time_span_id,time_span_string,price,native_currency_id,currency.iso as price_iso,variable_price,price_select,price_1,price_2,price_3,price_4,price_5,expire_time,start_amount,reserve_amount,physical,currency_id as auction_currency_id,auction_currency.iso as auction_currency_iso,shipping.id as shipping_id, from_country_id, from_country.name as from_country, count(review.id) as number_of_reviews, ifnull(avg(review.stars),0) as review_avg, product_html FROM product left join time_span_view on product.processing_time_span_id = time_span_view.id left join product_page on product.id = product_page.product_id left join product_price on product.id = product_price.product_id left join auction on product.id = auction.product_id left join currency on product_price.native_currency_id = currency.id left join currency as auction_currency on auction.currency_id = auction_currency.id left join shipping on product.id = shipping.product_id left join country as from_country on shipping.from_country_id = from_country.id left join review on review.product_id = product.id left join seller on product.seller_id = seller.id  group by product.id;
CREATE VIEW product_thumbnail_view AS SELECT product.id as product_id,title,seller_id,category_id,shop_name, count(review.id) as number_of_reviews, ifnull(avg(review.stars),0) as review_avg, auction,CASE WHEN auction="1"	THEN (select max(bid.amount) from bid where bid.auction_id = auction.id) 	ELSE max(price) END as price, currency.iso as price_iso FROM product inner join currency on product_price.native_currency_id = currency.id left join seller on product.seller_id = seller.id left join auction on product.id = auction.product_id left join review on review.product_id = product.id left join product_price on product_price.product_id = product.id group by product.id;
CREATE VIEW category_tree_view AS  SELECT t1.name AS name_1, t1.id AS id_1,t2.name AS name_2, t2.id AS id_2,t3.name AS name_3, t3.id AS id_3,t4.name AS name_4, t4.id AS id_4,t5.name AS name_5, t5.id AS id_5,t6.name AS name_6, t6.id AS id_6,t7.name AS name_7, t7.id AS id_7 FROM category AS t1 LEFT JOIN category AS t2 ON t2.parent = t1.id LEFT JOIN category AS t3 ON t3.parent = t2.id LEFT JOIN category AS t4 ON t4.parent = t3.id LEFT JOIN category AS t5 ON t5.parent = t4.id LEFT JOIN category AS t6 ON t6.parent = t5.id LEFT JOIN category AS t7 ON t7.parent = t6.id where t1.parent IS NULL;
CREATE VIEW browse_view AS select distinct id_1, id_2, name_1, name_2 from category_tree_view;
CREATE VIEW review_view AS select review.id,product_id,review.user_id,user.name as user_name, stars,headline,text_html, review.created_at, SUM(vote) as votes_sum, count(review_vote.id) as votes_count from review left join review_vote on review.id = review_vote.review_id left join user on review.user_id = user.id group by review.id;
CREATE VIEW feedback_view AS select feedback.id,cart_item_id,cart_item.product_id,cart_item.user_id,stars,arrived_on_time,correctly_described,prompt_service,comments,feedback.created_at from feedback left join cart_item on feedback.cart_item_id = cart_item.id;
CREATE VIEW question_view AS select question.id, question.user_id, user.name as user_name, product_id, text, question.created_at, SUM(vote) as votes_sum, count(question_vote.id) as votes_count from question left join question_vote on question.id = question_vote.question_id left join user on question.user_id = user.id group by question.id;
CREATE VIEW answer_view AS select answer.id, answer.user_id, user.name as user_name,question_id, text, answer.created_at, SUM(case when vote = 1 then 1 when vote = -1 then -1 else 0 end) as votes_sum, count(answer_vote.id) as votes_count from answer left join answer_vote on answer.id = answer_vote.answer_id left join user on answer.user_id = user.id group by answer.id;
CREATE VIEW shipping_cost_view AS select shipping_cost.id,shipping_id, to_country_id, num_,price, name as to_country,iso as shipping_cost_iso from shipping_cost left join country on shipping_cost.to_country_id = country.id left join currency on shipping_cost.native_currency_id = currency.id;
CREATE VIEW cart_view AS select cart_item.id,cart_item.user_id,product.seller_id,cart_item.product_id,cart_item.quantity,purchased,cart_item.created_at,title, url,product_price.price,iso ,cart_item.shipment_id,cart_item.payment_id from cart_item left join product_picture on cart_item.product_id = product_picture.product_id and num_ = 1 left join product on product.id = cart_item.product_id left join product_price on cart_item.product_id = product_price.product_id left join currency on product_price.native_currency_id = currency.id where purchased = 0;
CREATE VIEW cart_group AS select cart_item.user_id,seller_id, shop_name, max(time_span_string) as time_span_string, sum(product_price.price*cart_item.quantity) as cost,max(iso) as iso,IFNULL(max(shipping_cost.price),0) as shipping,sum(product_price.price*cart_item.quantity) + IFNULL(max(shipping_cost.price),0) as checkout_total ,shipment_id, address.full_name,address.address_line_1,address.address_line_2,address.city,address.state,address.zip,address.country_id,payment_id,purchased, bitmerchant_address,order_iframe, cart_item.created_at from cart_item left join product on product.id = cart_item.product_id left join product_price on cart_item.product_id = product_price.product_id left join shipping on cart_item.product_id = shipping.product_id left join shipping_cost on shipping.id = shipping_cost.shipping_id left join seller on product.seller_id = seller.id left join time_span_view on product.processing_time_span_id = time_span_view.id left join currency on product_price.native_currency_id = currency.id left join shipment on cart_item.shipment_id = shipment.id left join address on shipment.address_id = address.id left join payment on cart_item.payment_id = payment.id where purchased = 0 group by cart_item.user_id, product.seller_id;
CREATE VIEW order_group AS select cart_item.user_id,seller_id, shop_name, max(time_span_string) as time_span_string, sum(product_price.price*cart_item.quantity) as cost,max(iso) as iso,IFNULL(max(shipping_cost.price),0) as shipping,sum(product_price.price*cart_item.quantity) + IFNULL(max(shipping_cost.price),0) as checkout_total ,shipment_id, shipment.tracking_url, address.full_name,address.address_line_1,address.address_line_2,address.city,address.state,address.zip,address.country_id,payment_id,purchased, completed, order_iframe,payment.created_at from cart_item left join product on product.id = cart_item.product_id left join product_price on cart_item.product_id = product_price.product_id left join shipping on cart_item.product_id = shipping.product_id left join shipping_cost on shipping.id = shipping_cost.shipping_id left join seller on product.seller_id = seller.id left join time_span_view on product.processing_time_span_id = time_span_view.id left join currency on product_price.native_currency_id = currency.id left join shipment on cart_item.shipment_id = shipment.id left join address on shipment.address_id = address.id left join payment on cart_item.payment_id = payment.id where purchased = 1 group by cart_item.user_id, payment_id;
CREATE VIEW order_view AS select cart_item.id,cart_item.user_id,product.seller_id,cart_item.product_id,cart_item.quantity,purchased,cart_item.created_at,title, url,product_price.price,iso ,cart_item.shipment_id,cart_item.payment_id from cart_item left join product_picture on cart_item.product_id = product_picture.product_id and num_ = 1 left join product on product.id = cart_item.product_id left join product_price on cart_item.product_id = product_price.product_id left join currency on product_price.native_currency_id = currency.id where purchased = 1;
CREATE VIEW address_view AS select address.id,user_id,full_name,address_line_1,address_line_2,city,state,zip,country_id,default_,country.name as country_name,address.created_at from address left join country on address.country_id = country.id;
'
curl localhost:4001/raft?pretty
curl localhost:4002/raft?pretty

#kills all the rqlite instances
ps aux | grep -ie rqlite | awk '{print $2}' | xargs kill -9
rm -rf node_1 node_2 node_3
