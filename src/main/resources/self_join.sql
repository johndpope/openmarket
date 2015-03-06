-- There are 7 levels max in the google taxonomy

--  Retrieving the full tree / IE getting all the children
SELECT 
t1.name AS lev1, 
t2.name as lev2, 
t3.name as lev3, 
t4.name as lev4, 
t5.name as lev5, 
t6.name as lev6, 
t7.name as lev7

FROM category AS t1
LEFT JOIN category AS t2 ON t2.parent = t1.id
LEFT JOIN category AS t3 ON t3.parent = t2.id
LEFT JOIN category AS t4 ON t4.parent = t3.id
LEFT JOIN category AS t5 ON t5.parent = t4.id
LEFT JOIN category AS t6 ON t6.parent = t5.id
LEFT JOIN category AS t7 ON t7.parent = t6.id


-- WHERE t1.parent is null;

WHERE t1.name = 'Enlarging Equipment';
-- WHERE t1.name = 'Animals & Pet Supplies';

-- Query to get only first two levels
SELECT 
t1.name AS lev1, 
t2.name as lev2 
FROM category AS t1
LEFT JOIN category AS t2 ON t2.parent = t1.id
WHERE t1.parent is null;

-- Query to get all the parents and children of a given category
SELECT 
t1.name AS lev1, 
t2.name as lev2, 
t3.name as lev3, 
t4.name as lev4, 
t5.name as lev5, 
t6.name as lev6, 
t7.name as lev7

FROM category AS t1
LEFT JOIN category AS t2 ON t2.parent = t1.id
LEFT JOIN category AS t3 ON t3.parent = t2.id
LEFT JOIN category AS t4 ON t4.parent = t3.id
LEFT JOIN category AS t5 ON t5.parent = t4.id
LEFT JOIN category AS t6 ON t6.parent = t5.id
LEFT JOIN category AS t7 ON t7.parent = t6.id
where 
t1.parent IS NULL AND

(t1.name = 'Enlarging Equipment' OR
t2.name = 'Enlarging Equipment' OR
t3.name = 'Enlarging Equipment' OR
t4.name = 'Enlarging Equipment' OR
t5.name = 'Enlarging Equipment' OR
t6.name = 'Enlarging Equipment' OR
t7.name = 'Enlarging Equipment');
