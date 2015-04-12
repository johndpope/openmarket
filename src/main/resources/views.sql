CREATE VIEW time_span_view AS 
SELECT time_span.id, 
min || "-" || max || " " || name as time_span_string 
FROM time_span 
left join time_type on time_span.time_type_id = time_type.id
;

CREATE VIEW product_view AS 
SELECT product.id,
seller_id,
shop_name,
category_id,
buy,
auction,
quantity,
title,
processing_time_span_id,
time_span_string,
price,
native_currency_id,
currency.iso as price_iso,
variable_price,
price_select,
price_1,
price_2,
price_3,
price_4,
price_5,
expire_time,
start_amount,
reserve_amount,
physical,
currency_id as auction_currency_id,
auction_currency.iso as auction_currency_iso,
shipping.id as shipping_id, 
from_country_id, 
from_country.name as from_country, 
count(review.id) as number_of_reviews, 
ifnull(avg(review.stars),0) as review_avg, 
product_html 
FROM product 
left join time_span_view on product.processing_time_span_id = time_span_view.id 
left join product_page on product.id = product_page.product_id 
left join product_price on product.id = product_price.product_id 
left join auction on product.id = auction.product_id 
left join currency on product_price.native_currency_id = currency.id 
left join currency as auction_currency on auction.currency_id = auction_currency.id 
left join shipping on product.id = shipping.product_id 
left join country as from_country on shipping.from_country_id = from_country.id 
left join review on review.product_id = product.id 
left join seller on product.seller_id = seller.id  
group by product.id
;

CREATE VIEW product_thumbnail_view AS 
SELECT 
product.id as product_id,
title,
seller_id,
category_id,
shop_name, 
count(review.id) as number_of_reviews, 
ifnull(avg(review.stars),0) as review_avg, 
auction,
CASE WHEN auction='1'
	THEN (select max(bid.amount) from bid where bid.auction_id = auction.id) 
	ELSE max(price) 
END as price, 
currency.iso as price_iso 
FROM product 
inner join currency on product_price.native_currency_id = currency.id 
left join seller on product.seller_id = seller.id 
left join auction on product.id = auction.product_id 
left join review on review.product_id = product.id 
left join product_price on product_price.product_id = product.id 
group by product.id 
;


CREATE VIEW category_tree_view AS  
SELECT t1.name AS name_1, t1.id AS id_1,
t2.name AS name_2, t2.id AS id_2,
t3.name AS name_3, t3.id AS id_3,
t4.name AS name_4, t4.id AS id_4,
t5.name AS name_5, t5.id AS id_5,
t6.name AS name_6, t6.id AS id_6,
t7.name AS name_7, t7.id AS id_7 
FROM category AS t1 
LEFT JOIN category AS t2 ON t2.parent = t1.id 
LEFT JOIN category AS t3 ON t3.parent = t2.id 
LEFT JOIN category AS t4 ON t4.parent = t3.id 
LEFT JOIN category AS t5 ON t5.parent = t4.id 
LEFT JOIN category AS t6 ON t6.parent = t5.id 
LEFT JOIN category AS t7 ON t7.parent = t6.id 
where t1.parent IS NULL 
;

CREATE VIEW browse_view AS 
select distinct id_1, id_2, name_1, name_2 from category_tree_view
;

CREATE VIEW review_view AS 
select review.id,
product_id,
review.user_id,
user.name as user_name, 
stars,
headline,
text_html, 
review.created_at, 
SUM(vote) as votes_sum, 
count(review_vote.id) as votes_count 
from review 
left join review_vote on review.id = review_vote.review_id 
left join user on review.user_id = user.id 
group by review.id 
;

CREATE VIEW feedback_view AS 
select feedback.id,
cart_item_id,
cart_item.product_id,
cart_item.user_id,
stars,
arrived_on_time,
correctly_described,
prompt_service,
comments,
feedback.created_at 
from feedback 
left join cart_item on feedback.cart_item_id = cart_item.id 
;

CREATE VIEW question_view AS 
select question.id, 
question.user_id, 
user.name as user_name, 
product_id, 
text, 
question.created_at, 
SUM(vote) as votes_sum, 
count(question_vote.id) as votes_count 
from question 
left join question_vote on question.id = question_vote.question_id 
left join user on question.user_id = user.id 
group by question.id 
;

CREATE VIEW answer_view AS 
select answer.id, 
answer.user_id, 
user.name as user_name,
question_id, 
text, 
answer.created_at, 
SUM(case when vote = 1 then 1 when vote = -1 then -1 else 0 end) as votes_sum, 
count(answer_vote.id) as votes_count 
from answer 
left join answer_vote on answer.id = answer_vote.answer_id 
left join user on answer.user_id = user.id 
group by answer.id 
;

CREATE VIEW shipping_cost_view AS 
select shipping_cost.id,
shipping_id, 
to_country_id, 
num_,
price, 
name as to_country,
iso as shipping_cost_iso 
from shipping_cost 
left join country on shipping_cost.to_country_id = country.id 
left join currency on shipping_cost.native_currency_id = currency.id 
;

CREATE VIEW cart_view AS 
select cart_item.id,
cart_item.user_id,
product.seller_id,
cart_item.product_id,
cart_item.quantity,
purchased,
cart_item.created_at,
title, 
url,
product_price.price,
iso ,
cart_item.shipment_id,
cart_item.payment_id 
from cart_item 
left join product_picture on cart_item.product_id = product_picture.product_id and num_ = 1 
left join product on product.id = cart_item.product_id 
left join product_price on cart_item.product_id = product_price.product_id 
left join currency on product_price.native_currency_id = currency.id 
where purchased = 0 
;

CREATE VIEW cart_group AS 
select cart_item.user_id,
seller_id, 
shop_name, 
max(time_span_string) as time_span_string, 
sum(product_price.price*cart_item.quantity) as cost,
max(iso) as iso,
IFNULL(max(shipping_cost.price),0) as shipping,
sum(product_price.price*cart_item.quantity) + IFNULL(max(shipping_cost.price),0) as checkout_total ,
shipment_id, 
address.full_name,
address.address_line_1,
address.address_line_2,
address.city,
address.state,
address.zip,
address.country_id,
payment_id,
purchased, 
bitmerchant_address,
order_iframe, 
cart_item.created_at 
from cart_item 
left join product on product.id = cart_item.product_id 
left join product_price on cart_item.product_id = product_price.product_id 
left join shipping on cart_item.product_id = shipping.product_id 
left join shipping_cost on shipping.id = shipping_cost.shipping_id 
left join seller on product.seller_id = seller.id 
left join time_span_view on product.processing_time_span_id = time_span_view.id 
left join currency on product_price.native_currency_id = currency.id 
left join shipment on cart_item.shipment_id = shipment.id 
left join address on shipment.address_id = address.id 
left join payment on cart_item.payment_id = payment.id 
where purchased = 0 
group by cart_item.user_id, product.seller_id 
;

CREATE VIEW order_group AS 
select cart_item.user_id,
seller_id, 
shop_name, 
max(time_span_string) as time_span_string, 
sum(product_price.price*cart_item.quantity) as cost,
max(iso) as iso,
IFNULL(max(shipping_cost.price),0) as shipping,
sum(product_price.price*cart_item.quantity) + IFNULL(max(shipping_cost.price),0) as checkout_total ,
shipment_id, 
shipment.tracking_url, 
address.full_name,
address.address_line_1,
address.address_line_2,
address.city,
address.state,
address.zip,
address.country_id,
payment_id,
purchased, 
completed, 
order_iframe,
payment.created_at 
from cart_item 
left join product on product.id = cart_item.product_id 
left join product_price on cart_item.product_id = product_price.product_id 
left join shipping on cart_item.product_id = shipping.product_id 
left join shipping_cost on shipping.id = shipping_cost.shipping_id 
left join seller on product.seller_id = seller.id 
left join time_span_view on product.processing_time_span_id = time_span_view.id 
left join currency on product_price.native_currency_id = currency.id 
left join shipment on cart_item.shipment_id = shipment.id 
left join address on shipment.address_id = address.id 
left join payment on cart_item.payment_id = payment.id 
where purchased = 1 
group by cart_item.user_id, payment_id 
;

CREATE VIEW order_view AS 
select cart_item.id,
cart_item.user_id,
product.seller_id,
cart_item.product_id,
cart_item.quantity,
purchased,
cart_item.created_at,
title, 
url,
product_price.price,
iso ,
cart_item.shipment_id,
cart_item.payment_id 
from cart_item 
left join product_picture on cart_item.product_id = product_picture.product_id and num_ = 1 
left join product on product.id = cart_item.product_id 
left join product_price on cart_item.product_id = product_price.product_id 
left join currency on product_price.native_currency_id = currency.id 
where purchased = 1
;

CREATE VIEW address_view AS 
select address.id,
user_id,
full_name,
address_line_1,
address_line_2,
city,
state,
zip,
country_id,
default_,
country.name as country_name,
address.created_at 
from address 
left join country on address.country_id = country.id 
;


