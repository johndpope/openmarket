SELECT 
t1.name AS lev1, 
t2.name as lev2, 
t3.name as lev3, 
t4.name as lev4, 
t5.name as lev5, 
t6.name as lev6, 
t7.name as lev7, 
t8.name as lev8, 
t9.name as lev9

FROM category AS t1
LEFT JOIN category AS t2 ON t2.parent = t1.id
LEFT JOIN category AS t3 ON t3.parent = t2.id
LEFT JOIN category AS t4 ON t4.parent = t3.id
LEFT JOIN category AS t5 ON t5.parent = t4.id
LEFT JOIN category AS t6 ON t6.parent = t5.id
LEFT JOIN category AS t7 ON t7.parent = t6.id
LEFT JOIN category AS t8 ON t8.parent = t7.id
LEFT JOIN category AS t9 ON t9.parent = t8.id

WHERE t1.parent = NULL;


WHERE t1.name = 'Animals & Pet Supplies';