package views.questiontypes;

import services.applicant.ApplicantQuestion;
import services.question.QuestionDefinition;

public class ApplicantQuestionRendererFactory {

  public ApplicantQuestionRenderer getRenderer(QuestionDefinition definition) {
    ApplicantQuestion question = new ApplicantQuestion(definition);
    return getRenderer(question);
  }

  public ApplicantQuestionRenderer getRenderer(ApplicantQuestion question) {
    switch (question.getType()) {
      case TEXT:
        {
          return new TextQuestionRenderer(question);
        }

      case NAME:
        {
          return new NameQuestionRenderer(question);
        }

      default:
        throw new UnsupportedOperationException(
            "Unrecognized question type: " + question.getType());
    }
  }
}
