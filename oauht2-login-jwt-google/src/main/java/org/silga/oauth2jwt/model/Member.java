package org.silga.oauth2jwt.model;

import lombok.*;

import javax.persistence.*;
import java.util.Collection;

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

    String registrationId;

    String name;

    @ElementCollection
    Collection<String> authorities;

    public Member(String username, String registrationId, String name, Collection<String> authorities) {
        this(null, username, registrationId, name, authorities);
    }
}
