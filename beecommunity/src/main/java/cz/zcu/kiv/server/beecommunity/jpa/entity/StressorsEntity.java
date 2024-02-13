package cz.zcu.kiv.server.beecommunity.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity of stressors for hive inspection
 */
@Entity
@Table(name = "STRESSORS", schema = "public")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StressorsEntity {

    /**
     * Unique identification of stressors entity in database table
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "varroa_mites")
    private boolean varroaMites;

    @Column(name = "chalkbrood")
    private boolean chalkbrood;

    @Column(name = "sacbrood")
    private boolean sacbrood;

    @Column(name = "american_foulbrood")
    private boolean americanFoulbrood;

    @Column(name = "european_foulbrood")
    private boolean europeanFoulbrood;

    @Column(name = "nosema")
    private boolean nosema;

    @Column(name = "beetles")
    private boolean beetles;

    @Column(name = "mice")
    private boolean mice;

    @Column(name = "ants")
    private boolean ants;

    @Column(name = "moths")
    private boolean moths;

    @Column(name = "wasps")
    private boolean wasps;

    @Column(name = "yellow_jackets")
    private boolean yellowJackets;

    @Column(name = "other_stressors")
    private boolean otherStressors;

    @Column(name = "none_of_stressors")
    private boolean noneOfStressors;
}
