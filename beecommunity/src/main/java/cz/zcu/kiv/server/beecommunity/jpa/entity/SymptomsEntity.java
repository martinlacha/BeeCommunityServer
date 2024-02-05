package cz.zcu.kiv.server.beecommunity.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "SYMPTOMS", schema = "public")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SymptomsEntity {

    /**
     * Unique identification of symptoms entity in database table
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bees_cant_fly")
    private boolean beesCantFly;

    @Column(name = "deformedWings")
    private boolean deformed_wings;

    @Column(name = "hyperactivity")
    private boolean hyperactivity;

    @Column(name = "poor_motor_coordination")
    private boolean poorMotorCoordination;

    @Column(name = "deformed_abdomens")
    private boolean deformedAbdomens;

    @Column(name = "bees_fighting")
    private boolean beesFighting;

    @Column(name = "trembling")
    private boolean trembling;

    @Column(name = "shiny_black_bees")
    private boolean shinyBlackBees;

    @Column(name = "dead_larvae")
    private boolean deadLarvae;

    @Column(name = "chalky_larvae")
    private boolean chalkyLarvae;

    @Column(name = "discolored_larvae")
    private boolean discoloredLarvae;

    @Column(name = "mites_on_larvae")
    private boolean mitesOnLarvae;

    @Column(name = "patchy_brood")
    private boolean patchyBrood;

    @Column(name = "punctured_capped_brood")
    private boolean puncturedCappedBrood;

    @Column(name = "ropey_larvae")
    private boolean ropeyLarvae;

    @Column(name = "saclike_larvae")
    private boolean saclikeLarvae;

    @Column(name = "sunken_cappings")
    private boolean sunkenCappings;

    @Column(name = "chalky_corpses")
    private boolean chalkyCorpses;

    @Column(name = "dead_bees")
    private boolean deadBees;

    @Column(name = "translucent_pale_corpses")
    private boolean translucentPaleCorpses;

    @Column(name = "bad_smell")
    private boolean badSmell;

    @Column(name = "fecal_markings")
    private boolean fecalMarkings;

    @Column(name = "other")
    private boolean other;

    @Column(name = "none_of_symptoms")
    private boolean noneOfSymptoms;

}
