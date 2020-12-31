package demo.library.author;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import demo.library.book.Books;
import demo.library.model.BaseEntity;

import javax.persistence.JoinColumn;

@Entity
@Table(name = "authors")
public class Authors extends BaseEntity{
	
	@ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "author_book", 
	    joinColumns = { @JoinColumn(name = "author_Id") }, 
	    inverseJoinColumns = { @JoinColumn(name = "book_id") })
    private Set<Books> books = new HashSet<Books>();

	
	public Set<Books> getBooks() {
		return books;
	}

	public void setBooks(Set<Books> books) {
		this.books = books;
	}
}
