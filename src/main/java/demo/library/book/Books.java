package demo.library.book;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import demo.library.author.Authors;
import demo.library.model.BaseEntity;
import demo.library.tag.Tags;

@Entity
@Table(name = "books")
public class Books extends BaseEntity{

	@ManyToMany(mappedBy = "books")
    private Set<Authors> authors = new HashSet<Authors>();
	
	@ManyToMany(mappedBy = "books")
    private Set<Tags> tags = new HashSet<Tags>();
	
    @Column(name = "release_date", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
    private Date releaseDate;
    
    
	public Set<Tags> getTags() {
		return tags;
	}

	public void setTags(Set<Tags> tags) {
		this.tags = tags;
	}

	public Set<Authors> getAuthors() {
		return authors;
	}

	public void setAuthors(Set<Authors> authors) {
		this.authors = authors;
	}

	public Date getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}
}
