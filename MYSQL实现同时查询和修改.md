## MYSQL实现同时查询和修改

报错

```sql
UPDATE `fsnsh_loan_apply` SET  `PRODUCT_ID` = '689892679303036001' where ID IN (SELECT ID FROM `fsnsh_loan_apply` where PRODUCT_ID = '1');
```

正确 方式一

```sql
UPDATE `fsnsh_loan_apply` AS A INNER JOIN (SELECT ID FROM `fsnsh_loan_apply` where PRODUCT_ID = '1') AS B ON A.ID=B.ID SET A.PRODUCT_ID='689892679303036001';
```

