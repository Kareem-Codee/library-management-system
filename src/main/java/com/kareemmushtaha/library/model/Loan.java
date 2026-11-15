package com.kareemmushtaha.library.model;

import java.time.LocalDate;

public class Loan {

    private int id;
    private int bookId;
    private int memberId;
    private LocalDate loanDate;
    private LocalDate dueDate;

    // these two are only used to print the overdue list
    private String bookTitle;
    private String memberName;

    public Loan(int bookId, int memberId, LocalDate loanDate, LocalDate dueDate) {
        this.bookId = bookId;
        this.memberId = memberId;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBookId() {
        return bookId;
    }

    public int getMemberId() {
        return memberId;
    }

    public LocalDate getLoanDate() {
        return loanDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    @Override
    public String toString() {
        return "Loan #" + id + ": \"" + bookTitle + "\" borrowed by " + memberName
                + " (due: " + dueDate + ")";
    }
}