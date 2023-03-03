import java.util.ArrayList;
import java.util.HashMap;

public class Library {
	private HashMap<String, Integer> inventory;
	private ArrayList<Loan> loans;
	private ArrayList<String> books;
	private int nextLoanId;
	
	public Library(HashMap<String,Integer> i, ArrayList<String> b) {
		inventory = i;
		loans = new ArrayList<Loan>();
		nextLoanId = 1;
		books = b;
	}
	
	public synchronized String checkoutBook(String book, String user) {
		Integer quantity = inventory.get(book);
		if(quantity == null) {
			return "Request Failed - We do not have this book";
		}else if(quantity == 0) {
			return "Request Failed - Book not available";
		}else {
			int loanId = nextLoanId;
			nextLoanId +=1;
			loans.add(new Loan(loanId, book, user));
			quantity -=1;
			inventory.put(book, quantity);
			return "Your request has been approved, " + loanId +
					" " + user + " " + book;
		}
	}
	
	public synchronized String endLoan(int loanId) {
		for(int i =0; i<loans.size(); i++) {
			Loan l = loans.get(i);
			if(l.loanId == loanId) {
				int quantity = inventory.get(l.bookName);
				quantity += 1;
				inventory.put(l.bookName, quantity);
				loans.remove(i);
				return loanId + " is returned";
			}
		}
		return loanId + " not found, no such borrow record";
	}
	
	public synchronized String getLoans(String username) {
		String userLoans = "";
		for(int i =0; i<loans.size(); i++) {
			Loan l = loans.get(i);
			if(l.username.equals(username)) {
				userLoans += "\n" + l.loanId + " " + l.bookName;
			}
		}
		if(userLoans == "") {
			return "No record found for " + username;
		}else {
			return userLoans.substring(1);
		}
	}
	
	public synchronized String getInventory() {
		String inv = "";
		for(String book:books) {
			int quantity = inventory.get(book);
			inv += "\n" + book + " " + quantity;
		}
		if(inv == "") {
			return inv;
		}else {
			return inv.substring(1);
		}
	}
}


