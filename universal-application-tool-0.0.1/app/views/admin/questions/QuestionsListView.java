package views.admin.questions;

import static j2html.TagCreator.a;
import static j2html.TagCreator.body;
import static j2html.TagCreator.div;
import static j2html.TagCreator.each;
import static j2html.TagCreator.head;
import static j2html.TagCreator.span;
import static j2html.TagCreator.table;
import static j2html.TagCreator.tbody;
import static j2html.TagCreator.td;
import static j2html.TagCreator.th;
import static j2html.TagCreator.thead;
import static j2html.TagCreator.tr;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import j2html.tags.ContainerTag;
import j2html.tags.DomContent;
import j2html.tags.Tag;
import java.util.Locale;
import play.twirl.api.Content;
import services.question.QuestionDefinition;
import services.question.QuestionType;
import views.BaseHtmlLayout;
import views.Icons;
import views.ViewUtils;

public final class QuestionsListView extends BaseHtmlLayout {
  @Inject
  public QuestionsListView(ViewUtils viewUtils) {
    super(viewUtils);
  }

  /** Renders a page with either a table or a list view of all questions. */
  private Content render(ImmutableList<Tag> questionContent) {
    return htmlContent(
        head(tailwindStyles()),
        body()
            .withClass("h-screen w-screen bg-gradient-to-tr from-red-400 to-pink-900")
            .with(questionContent));
  }

  /** Renders a page with either a table view of all questions. */
  public Content renderAsTable(ImmutableList<QuestionDefinition> questions) {
    return render(ImmutableList.of(renderQuestionTable(questions)));
  }

  private Tag renderAddQuestionLink() {
    return a("Create a new question").withHref("/admin/questions/new");
  }

  private Tag renderSummary(ImmutableList<QuestionDefinition> questions) {
    return div(
        renderAddQuestionLink().withClass("text-left text-white"),
        span("Total Questions: " + questions.size()).withClass("float-right"));
  }

  /** Renders the full table. */
  private Tag renderQuestionTable(ImmutableList<QuestionDefinition> questions) {
    ContainerTag tableContainer =
        table()
            .withClass("min-w-full divide-y divide-gray-200")
            .with(renderQuestionTableHeaderRow())
            .with(
                tbody(each(questions, question -> renderQuestionTableRow(question)))
                    .withClasses("bg-white", "divide-y", "divide-gray-200"));

    return div(
            div(tableContainer)
                .withClasses(
                    "shadow", "overflow-hidden", "border-b", "border-gray-200", "sm:rounded-lg"),
            renderSummary(questions)
                .withClasses(
                    "bg-gray-500 text-white px-5 mx-px py-3 rounded-b-lg",
                    "text-xs",
                    "font-medium",
                    "text-white",
                    "uppercase",
                    "tracking-wider"))
        .withClasses("py-2", "align-middle", "inline-block", "min-w-full", "sm:px-6", "lg:px-8");
  }

  /** Render the question table header row. */
  private Tag renderQuestionTableHeaderRow() {
    String[] columns = {"Name", "Question text", "Id/Path"};
    ContainerTag tableHeader = thead().withClasses("bg-gray-500");
    for (String column : columns) {
      tableHeader.with(
          th(column)
              .attr("scope", "col")
              .withClasses(
                  "px-6",
                  "py-3",
                  "text-left",
                  "text-xs",
                  "font-medium",
                  "text-white",
                  "uppercase",
                  "tracking-wider"));
    }
    tableHeader.with(th().attr("scope", "col").withClass("relative px-6 py-3"));

    return tableHeader;
  }

  /** Display this as a table row with all fields. */
  private Tag renderQuestionTableRow(QuestionDefinition definition) {
    ContainerTag row = tr();
    String questionText = "";
    String questionHelpText = "";

    try {
      questionText = definition.getQuestionText(Locale.ENGLISH);
      questionHelpText = definition.getQuestionHelpText(Locale.ENGLISH);
    } catch (Exception e) {
      System.out.println("I don't really care about this error.");
    }

    row.with(
        mainInfoTd(
            definition.getName(), definition.getDescription(), definition.getQuestionType()));
    row.with(dualInfoTd(questionText, questionHelpText));
    row.with(
        dualInfoTd(
            "id: " + definition.getId() + "." + definition.getVersion(), definition.getPath()));
    row.with(QuestionTableCell.ACTIONS.getCellValue(definition));

    return row;
  }

  private Tag mainInfoTd(String title, String subtitle, QuestionType type) {
    ContainerTag titleDiv = div(title).withClasses("text-sm", "font-medium", "text-gray-900");
    ContainerTag subtitleDiv = div(subtitle).withClasses("text-sm", "text-gray-500");
    ContainerTag svgDiv = div(renderQuestionTypeSvg(type)).withClass("flex-shrink-0 h-12 w-18");
    ContainerTag contentDiv =
        div(svgDiv, div().withClass("ml-4").with(titleDiv, subtitleDiv))
            .withClass("flex items-center");
    return td(contentDiv).withClass("px-6 py-4 whitespace-nowrap");
  }

  private Tag dualInfoTd(String topText, String bottomText) {
    ContainerTag topDiv = div(topText).withClasses("text-sm", "text-gray-900");
    ContainerTag bottomDiv = div(bottomText).withClasses("text-sm", "text-gray-500");
    return td(topDiv, bottomDiv).withClass("px-6 py-4 whitespace-nowrap");
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
    return renderSvg(pathString)
        .attr("viewBox", "0 0 24 24")
        .withClasses("flex-shrink-0", "h-12", "w-6", "text-gray-600");
  }

  private ContainerTag renderSvg(String pathString) {
    ContainerTag svgTag =
        new ContainerTag("svg")
            .attr("xmlns", "http://www.w3.org/2000/svg")
            .attr("fill", "currentColor")
            .attr("stroke", "currentColor")
            .attr("aria-hidden", "true");
    ContainerTag pathTag =
        new ContainerTag("path").attr("xmlns", "http://www.w3.org/2000/svg").attr("d", pathString);
    svgTag.with(pathTag);

    return svgTag;
  }
}
