package services.question;

import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface QuestionService {

  /**
   * Get a {@link ReadOnlyQuestionService} which implements synchronous, in-memory read behavior for
   * questions.
   */
  CompletionStage<ReadOnlyQuestionService> getReadOnlyQuestionService();

  /**
   * Creates a new Question Definition. Returns a QuestionDefinition object on success and {@link
   * Optional#empty} on failure.
   *
   * <p>This will fail if he path provided already resolves to a QuestionDefinition or Scalar.
   *
   * <p>NOTE: This does not update the version.
   */
  Optional<QuestionDefinition> create(QuestionDefinition definition);

  /**
   * Builds a question definition, accepting a type paramater and generating an id and version for
   * you!
   */
  Optional<QuestionDefinition> build(
      String name,
      String path,
      String description,
      ImmutableMap<Locale, String> questionText,
      Optional<ImmutableMap<Locale, String>> questionHelpText,
      Optional<QuestionType> questionType);

  /**
   * Adds a new translation to an existing question definition. Returns true if the write is
   * successful.
   *
   * <p>The write will fail if:
   *
   * <p>- The path does not resolve to a QuestionDefinition.
   *
   * <p>- A translation with that Locale already exists for a given question path.
   *
   * <p>NOTE: This does not update the version.
   */
  boolean addTranslation(
      String path, Locale Locale, String questionText, Optional<String> questionHelpText)
      throws InvalidPathException;

  /**
   * Destructive overwrite of a question at a given path.
   *
   * <p>NOTE: This updates the service and question versions.
   */
  QuestionDefinition update(QuestionDefinition definition);
}
