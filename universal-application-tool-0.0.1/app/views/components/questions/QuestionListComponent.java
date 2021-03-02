package views.components.questions;

import static j2html.TagCreator.body;
import static j2html.TagCreator.div;
import static j2html.TagCreator.each;
import static j2html.TagCreator.head;
import static j2html.TagCreator.input;
import static j2html.TagCreator.p;

import com.google.common.collect.ImmutableList;
import j2html.tags.ContainerTag;
import j2html.tags.DomContent;
import javax.inject.Inject;
import play.twirl.api.Content;
import services.question.QuestionDefinition;
import services.question.QuestionType;
import views.BaseHtmlLayout;
import views.Icons;
import views.Styles;
import views.ViewUtils;

public class QuestionListComponent extends BaseHtmlLayout {
  // We should pull this out into a theme.
  private static String THEME_BG_COLOR = Styles.BG_GRAY_200;
  private static String THEME_TEXT_COLOR = Styles.TEXT_GRAY_400;
  private static String THEME_TEXT_TITLE_COLOR = Styles.TEXT_GRAY_900;

  @Inject
  public QuestionListComponent(ViewUtils viewUtils) {
    super(viewUtils);
  }

  public Content renderPage(ImmutableList<QuestionDefinition> questions) {
    return htmlContent(head(tailwindStyles()), body(renderComponent(questions)));
  }

  public DomContent renderComponent(ImmutableList<QuestionDefinition> questions) {
    ContainerTag container =
        div().withClasses(Styles.H_SCREEN, Styles.W_SCREEN, QuestionListComponent.THEME_BG_COLOR);

    String[] contentClasses = {"relative", "grid", "gap-6", "px-5", "py-6", "bg-white"};
    ContainerTag contentDiv =
        div()
            .withClasses(contentClasses)
            .with(
                renderHeader("Question Bank"),
                renderQuestionFilterBox(),
                each(questions, question -> renderQuestionDefinitionListItem(question)));

    ContainerTag framingDivs =
        div()
            .withClass(
                "absolute z-10 mt-3 transform w-screen max-w-md px-2 sm:px-0 -ml-4 lg:ml-0"
                    + " lg:left-1/2 lg:-translate-x-1/2")
            .with(
                div()
                    .withClass(
                        "rounded-lg shadow-lg ring-1 ring-black ring-opacity-5 overflow-hidden")
                    .with(contentDiv));

    return container.with(framingDivs);
  }

  private DomContent renderQuestionFilterBox() {
    DomContent svgTag = Icons.renderSvg(Icons.MAGNIFYING_GLASS, 56, 56).withClass("h-4 w-4");
    return div()
        .withClasses("relative w-85", QuestionListComponent.THEME_TEXT_COLOR)
        .with(div().withClasses("absolute ml-4 mt-3 mr-4").with(svgTag))
        .with(
            input()
                .withPlaceholder("Filter questions")
                .withName("filterQuestions")
                .withClasses(
                    "h-10 px-10 pr-5 w-full rounded-full text-sm border border-gray-200"
                        + " focus:outline-none shadow bg-grey-500",
                    QuestionListComponent.THEME_TEXT_COLOR));
  }

  private DomContent renderQuestionDefinitionListItem(QuestionDefinition qd) {
    return div()
        .withClass("-m-3 p-3 flex items-start rounded-lg hover:bg-gray-50")
        .with(renderQuestionTypeSvg(qd.getQuestionType()), renderQuestionDescription(qd));
  }

  private DomContent renderQuestionTypeSvg(QuestionType type) {
    String pathString = "";
    switch (type) {
      case ADDRESS:
        pathString = Icons.GOOGLE_MATERIAL_PLACE;
        break;
      case NAME:
        pathString = Icons.GOOGLE_MATERIAL_ACCOUNT_CIRCLE;
        break;
      case TEXT:
        pathString = Icons.GOOGLE_MATERIAL_HELP_OUTLINE;
        break;
      default:
        return div();
    }
    return Icons.renderSvg(pathString, 24)
        .withClasses(
            Styles.FLEX_SHRINK_0, Styles.H_12, Styles.W_6, QuestionListComponent.THEME_TEXT_COLOR);
  }

  private DomContent renderQuestionDescription(QuestionDefinition qd) {
    return div()
        .withClass("ml-4")
        .with(
            p(qd.getName())
                .withClasses("text-base font-medium", QuestionListComponent.THEME_TEXT_TITLE_COLOR))
        .with(
            p(qd.getDescription())
                .withClasses("text-sm mt-1", QuestionListComponent.THEME_TEXT_COLOR));
  }
}
