package com.ss.lms.dao.db;


import com.ss.lms.dao.DataConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/*
 * Db is a Singleton. The first time Db.getConnection() 
 * 	is called, a new connection is created. Subsequent calls will
 * 	return the same connection
 */

public class Db {

	private static DataConnector connector = new DataConnector();
	
	
	private static Db _instance = new Db();
	
	private Db() { }

	// Initialize the Singleton if necessary
	public static Db getConnection() {
		return _instance;
	}
	

	

	/*
	 * 	The third parameter (the injector) is a function that can
	 *  set the parameters of the query via the QueryParameterList object 
	 *  it is given. The return type of the function
	 *  is a list that contains the result of applying the mapper 
	 *  (2nd argument of the function) to each row returned by the query
	 */
	public <T> List<T> withQuery(
			String sql, 
			Function<TableRow, T> mapper, 
			Consumer<QueryParameterList> injector) 
			throws SQLException {
		
		Connection connection = getCurrConnection();

		try (PreparedStatement statement = connection.prepareStatement(sql)) {

			QueryParameterList parameterList = new QueryParameterList(statement);
			
			//Call the injector function to set the parameters of the query
			injector.accept(parameterList);
			
			try(ResultSet result = statement.executeQuery()) {
				return mapResultSet(mapper, result);
			}
		} 
	}
	
	public <T> List<T> withQuery(String sql, Function<TableRow, T> mapper) 
			throws SQLException {

		return withQuery(sql, mapper, p -> {});
	}
	
	/* 
	 * Returns an Optional of the 1st fetched result or Optional.empty if 
	 * there were 0 fetched rows
	 */
	public <T> Optional<T> withQueryOne(
			String sql, 
			Function<TableRow, T> mapper,
			Consumer<QueryParameterList> injector)
		throws SQLException {
		
		List<T> result = withQuery(sql, mapper, injector);
		return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
	}

	/*
	 *	The 2nd parameter (the injector) is a function that can
	 *  set the parameters of the update statement via the QueryParameterList object 
	 *  it is given. 
	 *  The function returns the number of rows affected by the statement
	 */
	public int withUpdate(String sql, Consumer<QueryParameterList> injector) 
			throws SQLException {
		
		Connection connection = getCurrConnection();

		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			
			
			QueryParameterList parameterList = new QueryParameterList(statement);
			
			//Call the injector function to set the parameters of the query
			injector.accept(parameterList);
			
			int rowCount = statement.executeUpdate();
			return rowCount;
		} 
	}
	
	public int withUpdate(String sql) throws SQLException {
		return withUpdate(sql, p -> {});
	}
	
	//Use a mapping function to transform each row in the ResultSet
	private <T> List<T> mapResultSet(Function<TableRow, T> mapper, ResultSet resultSet) 
			throws SQLException {

		List<T> rows = new ArrayList<T>();

		while(resultSet.next()) {
			rows.add(mapper.apply(new TableRow(resultSet)));
		}

		return rows;
	}
	
	private Connection getCurrConnection() throws SQLException {
		return connector.getCurrConnection();
	}
}
