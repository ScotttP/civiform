package models;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.Locale;
import java.util.OptionalLong;
import org.junit.Before;
import org.junit.Test;
import repository.ProgramRepository;
import repository.WithPostgresContainer;
import services.Path;
import services.program.BlockDefinition;
import services.program.ProgramDefinition;
import services.program.ProgramQuestionDefinition;
import services.question.AddressQuestionDefinition;
import services.question.NameQuestionDefinition;
import services.question.QuestionDefinition;
import services.question.TextQuestionDefinition;

public class ProgramTest extends WithPostgresContainer {

  private ProgramRepository repo;

  @Before
  public void setupProgramRepository() {
    repo = instanceOf(ProgramRepository.class);
  }

  @Test
  public void canSaveProgram() {
    QuestionDefinition questionDefinition =
        new TextQuestionDefinition(
            OptionalLong.of(123L),
            2L,
            "question",
            Path.create("applicant.name"),
            "applicant's name",
            ImmutableMap.of(Locale.US, "What is your name?"),
            ImmutableMap.of());

    BlockDefinition blockDefinition =
        BlockDefinition.builder()
            .setId(1L)
            .setName("First Block")
            .setDescription("basic info")
            .setProgramQuestionDefinitions(
                ImmutableList.of(ProgramQuestionDefinition.create(questionDefinition)))
            .build();

    ProgramDefinition definition =
        ProgramDefinition.builder()
            .setId(1L)
            .setName("ProgramTest")
            .setDescription("desc")
            .setBlockDefinitions(ImmutableList.of(blockDefinition))
            .build();
    Program program = new Program(definition);

    program.save();

    Program found = repo.lookupProgram(program.id).toCompletableFuture().join().get();

    assertThat(found.getProgramDefinition().name()).isEqualTo("ProgramTest");
    assertThat(found.getProgramDefinition().blockDefinitions().get(0).name())
        .isEqualTo("First Block");

    assertThat(
            found
                .getProgramDefinition()
                .blockDefinitions()
                .get(0)
                .programQuestionDefinitions()
                .get(0)
                .id())
        .isEqualTo(questionDefinition.getId());
  }

  @Test
  public void correctlySerializesDifferentQuestionTypes() {
    AddressQuestionDefinition addressQuestionDefinition =
        new AddressQuestionDefinition(
            OptionalLong.of(456L),
            2L,
            "address question",
            Path.create("applicant.address"),
            "applicant's address",
            ImmutableMap.of(Locale.US, "What is your address?"),
            ImmutableMap.of());
    NameQuestionDefinition nameQuestionDefinition =
        new NameQuestionDefinition(
            OptionalLong.of(789L),
            2L,
            "name question",
            Path.create("applicant.name"),
            "applicant's name",
            ImmutableMap.of(Locale.US, "What is your name?"),
            ImmutableMap.of());

    BlockDefinition blockDefinition =
        BlockDefinition.builder()
            .setId(1L)
            .setName("First Block")
            .setDescription("basic info")
            .setProgramQuestionDefinitions(
                ImmutableList.of(
                    ProgramQuestionDefinition.create(addressQuestionDefinition),
                    ProgramQuestionDefinition.create(nameQuestionDefinition)))
            .build();

    ProgramDefinition definition =
        ProgramDefinition.builder()
            .setId(1L)
            .setName("ProgramTest")
            .setDescription("desc")
            .setBlockDefinitions(ImmutableList.of(blockDefinition))
            .build();
    Program program = new Program(definition);
    program.save();

    Program found = repo.lookupProgram(program.id).toCompletableFuture().join().get();

    ProgramQuestionDefinition addressQuestion =
        found.getProgramDefinition().blockDefinitions().get(0).programQuestionDefinitions().get(0);
    assertThat(addressQuestion.id()).isEqualTo(addressQuestionDefinition.getId());
    ProgramQuestionDefinition nameQuestion =
        found.getProgramDefinition().blockDefinitions().get(0).programQuestionDefinitions().get(1);
    assertThat(nameQuestion.id()).isEqualTo(nameQuestionDefinition.getId());
  }
}
