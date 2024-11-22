package fr.univ_rouen.categorymanagement.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    @Column(nullable = false, unique = true)
    private String code;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    @JsonBackReference // Empêche la sérialisation récursive du parent
    private Category parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.PERSIST)
    @JsonManagedReference // Sérialise seulement la partie "enfants"
    private List<Category> children = new ArrayList<>();

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

//    Si is_root retourne True alors la categorie n'est pas un enfant d'une autre catégorie
    public boolean isRoot(){
        return (parent == null);
    }

    public Long getParentId() {
        return parent != null ? parent.getId() : null;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public void setParent(Category parent) {
        // Si le parent actuel est le même que le nouveau, rien à faire
        if (Objects.equals(this.parent, parent)) {
            return;
        }

        // Si la catégorie a déjà un parent, le retirer de l'ancienne relation
        if (this.parent != null) {
            this.parent.removeChild(this);
        }

        // Définir le nouveau parent
        this.parent = parent;

        // Ajouter cette catégorie aux enfants du nouveau parent si non null
        if (parent != null && !parent.getChildren().contains(this)) {
            parent.getChildren().add(this);
        }
    }

    public void addChild(Category child) {
        // Vérifier que la catégorie ne s'ajoute pas elle-même en tant qu'enfant
        if (Objects.equals(child.getId(), this.id)) {
            throw new IllegalArgumentException("Une catégorie ne peut pas être enfant d'elle-même.");
        }

        // Ajouter l'enfant seulement s'il n'est pas déjà présent
        if (!children.contains(child)) {
            children.add(child);
            child.setParent(this);
        }
    }

    public void removeChild(Category child) {
        if (children.contains(child)) {
            children.remove(child);
            child.setParent(null); // Supprime le lien parent pour l’enfant
        }
    }

}
