package com.haoyunhu.tools.mybatis.page;


/**
 * 支持Oracle
 */
public class OracleDialect extends Dialect {

    public String getLimitString(String sql, int offset, String offsetPlaceholder, int limit, String limitPlaceholder) {
        sql = sql.trim();

        StringBuffer pagingSelect = new StringBuffer(sql.length() + 100);
        if (offset > 0) {
            pagingSelect.append("select * from ( select row_.*, rownum rownum_ from ( ");
        } else {
            if(limit > 0){
                pagingSelect.append("select * from ( ");
            }
        }
        pagingSelect.append(sql);
        if (offset > 0) {
            String endString = offsetPlaceholder + "+" + limitPlaceholder;
            pagingSelect.append(" ) row_ ) where rownum_ <= " + endString + " and rownum_ > " + offsetPlaceholder);
        } else {
            if(limit > 0){
                pagingSelect.append(" ) where rownum <= " + limitPlaceholder);
            }
        }

        return pagingSelect.toString();
    }

}

