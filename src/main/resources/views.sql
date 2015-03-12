CREATE VIEW time_span_view AS 
SELECT time_span.id, 
min || "-" || max || " " || name as time_span_string 
FROM time_span 
left join time_type on time_span.time_type_id = time_type.id
;

CREATE VIEW product_view AS 
SELECT product.id,
seller_id,
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
product_html 
FROM product 
left join time_span_view on product.processing_time_span_id = time_span_view.id 
left join product_page on product.id = product_page.product_id 
left join product_price on product.id = product_price.product_id 
left join currency on product_price.native_currency_id = currency.id 
;