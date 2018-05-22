package com.haoyunhu.tools.mybatis.handler;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EmptyStringIfNull implements TypeHandler<String> {

	@Override
	public void setParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public String getResult(ResultSet rs, String columnName) throws SQLException {
		return (rs.getString(columnName) == null) ? "" : rs.getString(columnName);
	}

	@Override
	public String getResult(ResultSet rs, int columnIndex) throws SQLException {
		return (rs.getString(columnIndex) == null) ? "" : rs.getString(columnIndex);
	}

	@Override
	public String getResult(CallableStatement cs, int columnIndex) throws SQLException {
		return (cs.getString(columnIndex) == null) ? "" : cs.getString(columnIndex);
	}

}
