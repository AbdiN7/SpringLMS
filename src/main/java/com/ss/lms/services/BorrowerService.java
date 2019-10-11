package com.ss.lms.services;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import com.ss.lms.dao.*;
import com.ss.lms.model.*;

public class BorrowerService {

	private BorrowerDao borrowerDao;
	
	private static BorrowerService service = new BorrowerService(
			BorrowerDao.getDao()
			);
	
	public static BorrowerService getService() {
		return service;
	}
	
	private BorrowerService(BorrowerDao borrowerDao) {
		this.borrowerDao = borrowerDao;
	}
	
	public void delete(Borrower borrower) throws SQLException {
		borrowerDao.delete(borrower);
	}
	
	public void insert(Borrower borrower) throws SQLException {
		borrowerDao.insert(borrower);
	}
	
	public void update(Borrower borrower) throws SQLException {
		borrowerDao.update(borrower);
	}

	public Optional<Borrower> get(int cardNo) throws SQLException {
		return borrowerDao.get(cardNo);
	}
	
	public List<Borrower> getAll() throws SQLException {
		return borrowerDao.getAll();
	}
}
