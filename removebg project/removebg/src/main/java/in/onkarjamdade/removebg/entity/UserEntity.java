package in.onkarjamdade.removebg.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="tbl_users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  long id;
    private String firstName;
    private String lastName;
    @Column(unique=true, nullable = false)
    private String clerkId;
    @Column(unique=true, nullable = false)
    private String email;
    @Column(name = "photo_url", length = 1000)
    private String photoUrl;
    private Integer credits;

    @PrePersist
    public void prePersist(){
        if(this.credits == null){
            this.credits = 5;
        }
    }
}
