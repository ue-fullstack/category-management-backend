package fr.univ_rouen.categorymanagement.model;

import fr.univ_rouen.categorymanagement.dto.CategoryDTO;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
@Getter
@Setter
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    @JsonBackReference
    private Category parent;

    @Column(name = "parent_id", insertable = false, updatable = false)
    private Long parentId;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Category> children = new ArrayList<>();

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_selected")
    private boolean selected;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public boolean isRoot() {
        return !children.isEmpty();
    }

    public void setParent(Category parent) {  // Changé de CategoryDTO à Category
        if (this.parent != null) {
            this.parent.getChildren().remove(this);
        }
        this.parent = parent;
        this.parentId = parent != null ? parent.getId() : null;
        if (parent != null) {
            parent.getChildren().add(this);
            this.selected = true;
            parent.setSelected(false);
        }
    }

    public void addChild(Category child) {
        children.add(child);
        child.setParent(this);
    }

    public void removeChild(Category child) {
        children.remove(child);
        child.setParent(null);
    }

    @JsonIgnore
    public Long getParentId() {
        return parent != null ? parent.getId() : null;
    }
}
