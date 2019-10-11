package com.ss.lms.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import com.ss.lms.dao.db.*;
import com.ss.lms.model.*;

public class BookCopiesDao {

	private Db db;

	private static BookCopiesDao dao = new BookCopiesDao(Db.getConnection());

	public static BookCopiesDao getDao() {
		return dao;
	}

	public BookCopiesDao(Db db) {
		this.db = db;
	}

	//Get all books that have copies remaining at the branch
	public List<BookCopies> get(LibraryBranch branch) throws SQLException {

		String query = "SELECT b.*, a.*, p.* COALESCE(bc.noOfCopies, 0) AS noOfCopies FROM library.tbl_book b " +
				"JOIN library.tbl_author a ON a.authorId=b.authId " +
				"JOIN library.tbl_publisher p ON p.publisherId=b.pubId " + 
				"LEFT JOIN library.tbl_book_copies bc ON bc.bookId=b.bookId AND bc.branchId=?"; 

		return db.withQuery(query, 
				row -> rowToBookCopies(branch, row),
				parameterList -> {
					parameterList.setInt(1, branch.getBranchId());
				});
	}

	public Optional<BookCopies> get(LibraryBranch branch, Book book) throws SQLException {

		String query = "SELECT b.*, a.*, p.*, COALESCE(bc.noOfCopies, 0) AS noOfCopies FROM library.tbl_book b " +
				"JOIN library.tbl_author a ON a.authorId=b.authId " +
				"JOIN library.tbl_publisher p ON p.publisherId=b.pubId " + 
				"LEFT JOIN library.tbl_book_copies bc ON bc.bookId=b.bookId AND bc.branchId=? " +
				"WHERE b.bookId=?"; 

		return db.withQueryOne(query, 
				row -> rowToBookCopies(branch, row),
				parameterList -> {
					parameterList.setInt(1, branch.getBranchId());
					parameterList.setInt(2, book.getBookId());
				});
	}


	/*
	 * Only updates the noOfCopies
	 */
	public void update(BookCopies copies) throws SQLException {

		String query = "INSERT INTO library.tbl_book_copies " + 
				"VALUES (?,?,?) " +
				"ON DUPLICATE KEY UPDATE " +
				"noOfCopies=?";

		db.withUpdate(query, parameterList -> {
			parameterList.setInt(1, copies.getBookId());
			parameterList.setInt(2, copies.getBranchId());
			parameterList.setInt(3, copies.getNoOfCopies());
			parameterList.setInt(4, copies.getNoOfCopies());
		});
	}

	private BookCopies rowToBookCopies(LibraryBranch branch, TableRow row) {

		int noOfCopies = row.getInt("noOfCopies");
		int bookId = row.getInt("bookId");

		BookCopies copies = new BookCopies(bookId, branch.getBranchId(), noOfCopies);

		return copies;
	}
}