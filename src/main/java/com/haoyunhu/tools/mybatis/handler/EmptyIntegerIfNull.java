package com.haoyunhu.tools.mybatis.handler;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EmptyIntegerIfNull implements TypeHandler<String> {

	@Override
	public void setParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public String getResult(ResultSet rs, String columnName) throws SQLException {
		return covert(rs.getString(columnName));
	}

	@Override
	public String getResult(ResultSet rs, int columnIndex) throws SQLException {
		return covert(rs.getString(columnIndex));
	}

	@Override
	public String getResult(CallableStatement cs, int columnIndex) throws SQLException {
		return covert(cs.getString(columnIndex));
	}

	private String covert(String value){
		if(StringUtils.isBlank(value)){
			return "0";
		}

		try {
			BigDecimal bigDecimal = NumberUtils.createBigDecimal(value);
			return bigDecimal.toString();
		} catch (Exception e){
			return "0";
		}
	}
}
