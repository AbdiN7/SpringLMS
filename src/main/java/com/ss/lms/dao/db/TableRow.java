package com.ss.lms.dao.db;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

/*
 * Wraps a ResultSet object
 */

public class TableRow {
	
	private ResultSet resultSet;

	public TableRow(ResultSet resultSet) {
		this.resultSet = resultSet;
	}
	
	public int getInt(String columnName) {
		
		try {
			return resultSet.getInt(columnName);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public String getString(String columnName) {
		
		try {
			return resultSet.getString(columnName);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public Date getDate(String columnName) {
		
		try {
			return resultSet.getDate(columnName);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
