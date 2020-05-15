package de.my5t3ry.eternal.init;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by Jens Greive on 06.12.18.
 */
@Entity
@NoArgsConstructor
@Getter
public class InitLog {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @NotNull
    private String message;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn;

    @NotNull
    private String createdBy;


    public InitLog(@NotNull String message, @NotNull String createdBy) {
        this.message = message;
        this.createdBy = createdBy;
        this.createdOn = new Date();
    }
}
