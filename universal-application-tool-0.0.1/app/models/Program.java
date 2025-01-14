package models;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import io.ebean.annotation.DbJson;
import javax.persistence.Entity;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import play.data.validation.Constraints;
import services.program.BlockDefinition;
import services.program.ProgramDefinition;

/** The ebeans mapped class for the program object. */
@Entity
@Table(name = "programs")
public class Program extends BaseModel {

  private ProgramDefinition programDefinition;

  private @Constraints.Required String name;

  private @Constraints.Required String description;

  private @Constraints.Required @DbJson ImmutableList<BlockDefinition> blockDefinitions;

  public ProgramDefinition getProgramDefinition() {
    return checkNotNull(this.programDefinition);
  }

  public Program(ProgramDefinition definition) {
    this.programDefinition = definition;
    this.id = definition.id();
    this.name = definition.name();
    this.description = definition.description();
    this.blockDefinitions = definition.blockDefinitions();
  }

  /**
   * Construct a new Program object with the given program name and description, and with an empty
   * block named Block 1.
   */
  public Program(String name, String description) {
    this.name = name;
    this.description = description;
    BlockDefinition emptyBlock =
        BlockDefinition.builder()
            .setId(1L)
            .setName("Block 1")
            .setDescription("")
            .setProgramQuestionDefinitions(ImmutableList.of())
            .build();
    this.blockDefinitions = ImmutableList.of(emptyBlock);
  }

  /** Populates column values from {@link ProgramDefinition} */
  @PreUpdate
  public void persistChangesToProgramDefinition() {
    this.id = this.programDefinition.id();
    this.name = this.programDefinition.name();
    this.description = this.programDefinition.description();
    this.blockDefinitions = this.programDefinition.blockDefinitions();
  }

  /** Populates {@link ProgramDefinition} from column values. */
  @PostLoad
  @PostPersist
  @PostUpdate
  public void loadProgramDefinition() {
    this.programDefinition =
        ProgramDefinition.builder()
            .setId(this.id)
            .setName(this.name)
            .setDescription(this.description)
            .setBlockDefinitions(this.blockDefinitions)
            .build();
  }
}
