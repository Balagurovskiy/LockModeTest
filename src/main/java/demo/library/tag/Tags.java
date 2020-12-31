package demo.library.tag;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import demo.library.book.Books;
import demo.library.model.BaseEntity;

@Entity
@Table(name = "tags")
public class Tags  extends BaseEntity{

	@ManyToMany
    @JoinTable(name = "book_tag", 
	    joinColumns = { @JoinColumn(name = "tag_Id") }, 
	    inverseJoinColumns = { @JoinColumn(name = "book_id") })
    private Set<Books> books = new HashSet<Books>();
	
	
	public Set<Books> getBooks() {
		return books;
	}

	public void setBooks(Set<Books> books) {
		this.books = books;
	}
}
