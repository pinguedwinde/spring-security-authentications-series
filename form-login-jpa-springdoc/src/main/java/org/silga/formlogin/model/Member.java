package org.silga.formlogin.model;

import lombok.*;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Generated
@Getter
@Setter
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @Column(unique = true)
    String username;

    String password;

    String name;

    @ElementCollection
    Collection<String> authorities;

    public Member(String username, String password, String name, Collection<String> authorities) {
        this(null, username, password, name, authorities);
    }
}
