package com.ss.lms.services;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import com.ss.lms.dao.*;
import com.ss.lms.model.*;

public class BookLoanService {

	private BookLoanDao loanDao;
	private BookCopiesDao copiesDao;
	
	private static BookLoanService service = new BookLoanService(
			BookLoanDao.getDao(),
			BookCopiesDao.getDao()
			);
	
	public static BookLoanService getService() {
		return service;
	}
	
	private BookLoanService(BookLoanDao loanDao, BookCopiesDao copiesDao) {
		this.loanDao = loanDao;
		this.copiesDao = copiesDao;
	}
	
	

	public List<BookLoans> getByBorrower(Borrower borrower, LibraryBranch branch) throws SQLException {
		return loanDao.getByBorrower(borrower, branch);
	}
	
	public Optional<BookLoans> get(Borrower borrower, LibraryBranch branch, Book book) 
		throws SQLException {
		
		return loanDao.get(borrower, branch, book);
	}

	//Loan Update
	public void updateDueDate(BookLoans loan) throws SQLException {
		loanDao.updateDueDate(loan);
	
	}
	
	//Also decrements the noOfCopies 
	//Idempotent (Will do nothing if the loan already exists)
	//Don't call if there are no copies of the book in the library
	public void insertLoan(BookLoans loan) throws SQLException {
		
		Borrower borrower = new Borrower(loan.getCardNo(), null, null, null);
		LibraryBranch branch = new LibraryBranch(loan.getBranchId(), null, null);
		Book book = new Book(loan.getBookId(), null, null, null);
		
		Optional<BookLoans> existing = loanDao.get(
				borrower, branch, book
				);
		
		//Do nothing if the loan exists
		if(existing.isPresent()) {
			return;
		}
		
		BookCopies copies = copiesDao.get(branch,  book).get();
		
		//decrement the noOfCopies
		copies.setNoOfCopies(copies.getNoOfCopies() - 1);
		copiesDao.update(copies);
		
		loanDao.insert(loan);
	}
	
	//Also increments the noOfCopies 
	//Idempotent (Will do nothing if the loan already exists)
	//Don't call if there were never any copies of the book in the library
	public void deleteLoan(BookLoans loan) throws SQLException {
		
		Borrower borrower = new Borrower(loan.getCardNo(), null, null, null);
		LibraryBranch branch = new LibraryBranch(loan.getBranchId(), null, null);
		Book book = new Book(loan.getBookId(), null, null, null);
		
		Optional<BookLoans> existing = loanDao.get(
				borrower, branch, book
				);
		
		//Do nothing if the loan does not exist
		if(existing.isEmpty()) {
			return;
		}
		BookCopies copies = copiesDao.get(branch, book).get();
		
		//increment the noOfCopies
		copies.setNoOfCopies(copies.getNoOfCopies() + 1);
		copiesDao.update(copies);
		
		loanDao.delete(loan);
	}
}
