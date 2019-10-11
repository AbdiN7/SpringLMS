package com.ss.lms.dao;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import com.ss.lms.dao.db.*;
import com.ss.lms.model.*;

public class BookLoanDao {

	private Db db;

	private static BookLoanDao dao = new BookLoanDao(Db.getConnection());

	public static BookLoanDao getDao() {
		return dao;
	}

	public BookLoanDao(Db db) {
		this.db = db;
	}

	//Get all loans by the borrower
	public List<BookLoans> getByBorrower(Borrower borrower) throws SQLException {

		String query = "SELECT * FROM library.tbl_book b " +
				"JOIN library.tbl_author a ON a.authorId=b.authId " +
				"JOIN library.tbl_publisher p ON p.publisherId=b.pubId " + 
				"JOIN library.tbl_book_loans bl ON bl.bookId=b.bookId " + 
				"JOIN library.tbl_library_branch lb ON lb.branchId=bl.branchId " +
				"JOIN library.tbl_borrower bor ON bor.cardNo=bl.cardNo " +
				"WHERE bor.cardNo=?";

		return db.withQuery(query, 
				row -> rowToLoans(borrower, row),
				parameterList -> {
					parameterList.setInt(1, borrower.getBorrowerCardNumber());
				});
	}

	//Get all loans by the borrower at the branch
	public List<BookLoans> getByBorrower(Borrower borrower, LibraryBranch branch)
			throws SQLException {

		String query = "SELECT * FROM library.tbl_book b " +
				"JOIN library.tbl_author a ON a.authorId=b.authId " +
				"JOIN library.tbl_publisher p ON p.publisherId=b.pubId " + 
				"JOIN library.tbl_book_loans bl ON bl.bookId=b.bookId " + 
				"JOIN library.tbl_library_branch lb ON lb.branchId=bl.branchId " +
				"JOIN library.tbl_borrower bor ON bor.cardNo=bl.cardNo " +
				"WHERE bor.cardNo=? AND " +
				"lb.branchId=?";

		return db.withQuery(query, 
				row -> rowToLoans(borrower, row),
				parameterList -> {
					parameterList.setInt(1, borrower.getBorrowerCardNumber());
					parameterList.setInt(2, branch.getBranchId());
				});
	}

	public Optional<BookLoans> get(Borrower borrower, LibraryBranch branch, Book book) 
			throws SQLException {

		String query = "SELECT * FROM library.tbl_book b " +
				"JOIN library.tbl_author a ON a.authorId=b.authId " +
				"JOIN library.tbl_publisher p ON p.publisherId=b.pubId " + 
				"JOIN library.tbl_book_loans bl ON bl.bookId=b.bookId " + 
				"JOIN library.tbl_library_branch lb ON lb.branchId=bl.branchId " +
				"JOIN library.tbl_borrower bor ON bor.cardNo=bl.cardNo " +
				"WHERE bor.cardNo=? AND " +
				"lb.branchId=? AND " + 
				"b.bookId=?";

		return db.withQueryOne(query, 
				row -> rowToLoans(borrower, row),
				parameterList -> {
					parameterList.setInt(1, borrower.getBorrowerCardNumber());
					parameterList.setInt(2, branch.getBranchId());
					parameterList.setInt(3, book.getBookId());
				});
	}

	//Will fail if the loan already exists
	public void insert(BookLoans loan) throws SQLException {

		String query = "INSERT INTO library.tbl_book_loans " + 
				"VALUES (?,?,?, ?,?) ";

		db.withUpdate(query, parameterList -> {
			parameterList.setInt(1, loan.getBookId());
			parameterList.setInt(2, loan.getBranchId());
			parameterList.setInt(3, loan.getCardNo());
			parameterList.setDate(4, loan.getDateOut());
			parameterList.setDate(5, loan.getDueDate());
		});
	}

	public void delete(BookLoans loan) throws SQLException {

		String query = "DELETE FROM library.tbl_book_loans bl WHERE " + 
				"bl.bookId=? AND " +
				"bl.branchId=? AND " +
				"bl.cardNo=?";

		db.withUpdate(query, parameterList -> {
			parameterList.setInt(1, loan.getBookId());
			parameterList.setInt(2, loan.getBranchId());
			parameterList.setInt(3, loan.getCardNo());;
		});
	}
	
	public void updateDueDate(BookLoans bookLoan) throws SQLException {
		
		String query = "UPDATE library.tbl_book_loans SET " + 
				"dueDate=? " +
				"WHERE bookId=? AND branchId=? AND cardNo=?";
		
		db.withUpdate(query, parameterList -> {
			parameterList.setDate(1, bookLoan.getDueDate());
			parameterList.setInt(2, bookLoan.getBookId());
			parameterList.setInt(3, bookLoan.getBranchId());
			parameterList.setInt(4, bookLoan.getCardNo());
		});
	}

	private BookLoans rowToLoans(Borrower borrower, TableRow row) {

		Date dateOut = row.getDate("dateOut");
		Date dueDate = row.getDate("dueDate");

		int bookId = row.getInt("bookId");
		int branchId = row.getInt("branchId");


		BookLoans loan = new BookLoans(bookId, branchId, borrower.getBorrowerCardNumber(), dateOut, dueDate);

		return loan;
	}
}