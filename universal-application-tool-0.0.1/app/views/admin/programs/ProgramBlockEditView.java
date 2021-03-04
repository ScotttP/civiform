package views.admin.programs;

import static com.google.common.base.Preconditions.checkNotNull;
import static j2html.TagCreator.a;
import static j2html.TagCreator.div;
import static j2html.TagCreator.h1;
import static j2html.TagCreator.input;
import static j2html.TagCreator.p;
import static j2html.TagCreator.span;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import j2html.tags.ContainerTag;
import j2html.tags.Tag;
import java.util.Comparator;
import play.mvc.Http.Request;
import play.twirl.api.Content;
import services.program.BlockDefinition;
import services.program.ProgramDefinition;
import services.question.QuestionDefinition;
import services.question.QuestionType;
import views.BaseHtmlView;
import views.StyleUtils;
import views.Styles;
import views.admin.AdminLayout;

public class ProgramBlockEditView extends BaseHtmlView {

  private final AdminLayout layout;

  private final String TEXT_COLOR = Styles.TEXT_GRAY_300;
  private final String BG_COLOR = Styles.BG_GRAY_800;
  // private final String BG_SECONDARY_COLOR = Styles.BG_GRAY_700;
  private final String BG_TERTIARY_COLOR = Styles.BG_GRAY_600;
  /*
  private final String SELECTED_STYLING = BG_TERTIARY_COLOR;
  */
  private final String BLOCK_HOVER_STYLING = StyleUtils.hover(Styles.BG_GRAY_500);

  @Inject
  public ProgramBlockEditView(AdminLayout layout) {
    this.layout = checkNotNull(layout);
  }

  public Content render(
      Request request,
      ProgramDefinition program,
      BlockDefinition block,
      ImmutableList<QuestionDefinition> questions) {

    return layout.renderBody(
        div()
            .withClasses(BG_COLOR, TEXT_COLOR)
            .with(
                renderHeaderSection(program.name()), renderMainContent(program, block, questions)));
  }

  private Tag renderHeaderSection(String appTitle) {
    // header (back to "All Applications", program title, logged in user)
    String link = controllers.admin.routes.AdminProgramController.index().url();
    Tag allAppsLink =
        a("← All Applications")
            .withHref(link)
            .withClasses(
                "inline-block text-left w-1/5 hover:text-white transition-all transform"
                    + " hover:scale-105");
    Tag titleTag = p(span(appTitle)).withClasses("flex-grow text-center text-white");
    Tag userProfileTag = p("User").withClasses("inline-block text-right w-1/5");
    return div()
        .withClasses("bg-gray-700 px-8 py-4 font-light flex absolute inset-x-0 top-0 text-md")
        .with(allAppsLink, titleTag, userProfileTag);
  }

  private Tag renderMainContent(
      ProgramDefinition program,
      BlockDefinition block,
      ImmutableList<QuestionDefinition> questions) {
    return div()
        .withClasses("flex p-relative h-screen pt-8")
        .with(renderMain(program, block), renderQuestionBank(questions));
  }

  private Tag renderMain(ProgramDefinition program, BlockDefinition focusedBlock) {
    return div(blockOrderPanel(program, focusedBlock), renderAddBlockTag(program))
        .withClass("inline-block w-1/2 m-0 pt-8 pr-12")
        .withStyle("width: calc(100% - 375px);");
  }

  private Tag renderQuestionBank(ImmutableList<QuestionDefinition> questions) {
    return div(questionBankPanel(questions))
        .withClass("inline-block w-1/3 m-0 pt-12 pb-12 pr-4")
        .withStyle("width: 375px;");
  }

  private ContainerTag renderAddBlockTag(ProgramDefinition program) {
    String addBlockUrl =
        controllers.admin.routes.AdminProgramBlocksController.create(program.id()).url();

    return div(a("Add Block").withHref(addBlockUrl))
        .withClasses(
            "inline-block cursor-pointer float-left p-3 my-2 bg-gray-700 hover:bg-gray-400"
                + " text-white rounded-md focus:outline-none focus:ring-2 ring-blue-200"
                + " ring-offset-2");
  }

  private ContainerTag blockOrderPanel(ProgramDefinition program, BlockDefinition focusedBlock) {
    ContainerTag ret = div();
    for (BlockDefinition block : program.blockDefinitions()) {
      String editBlockLink =
          controllers.admin.routes.AdminProgramBlocksController.edit(program.id(), block.id())
              .url();
      ContainerTag linkContent =
          div()
              .withClasses(Styles.ML_12)
              .with(
                  p(block.name()).withClasses("text-base font-medium text-primaryText"),
                  p(block.description() + "\u00a0").withClasses("mt-1 text-sm text-secondaryText"));
      ContainerTag link = a(linkContent).withHref(editBlockLink);
      String selectedStyling = block.hasSameId(focusedBlock) ? BG_TERTIARY_COLOR : "";
      link.withClasses(Styles.BLOCK, Styles.P_2, BLOCK_HOVER_STYLING, selectedStyling);

      ret.with(link);
    }

    return ret;
  }

  private ContainerTag questionBankPanel(ImmutableList<QuestionDefinition> questionDefinitions) {
    ContainerTag ret =
        div().withClasses("inline-block w-1/3 m-0 pb-6 pr-4").withStyle("width: 375px;");
    ContainerTag innerDiv =
        div()
            .withClasses(
                "rounded-lg bg-gray-600 shadow-lg ring-2 ring-black ring-opacity-5 overflow-hidden"
                    + " pt-1");
    ret.with(innerDiv);
    ContainerTag contentDiv = div().withClasses("relative grid gap-6 bg-secondary px-5 py-6");
    innerDiv.with(contentDiv);

    ContainerTag headerDiv = h1("Question bank").withClasses("mx-1 px-3 -mb-1 text-xl");
    contentDiv.with(headerDiv);

    Tag filterInput =
        input()
            .withType("text")
            .withName("questionFilter")
            .attr("placeholder", "Filter questions")
            .withClasses(
                "h-10 px-10 pr-5 w-full rounded-full text-sm border border-gray-200"
                    + " focus:outline-none shadow bg-grey-500 text-secondaryText");

    String svgPath = "M55.146,51.887L41.588,37.786c3.486-4.144,5.396-9.358,5.396-14.786c0-12.682-10.318-23-23-23s-23,10.318-23,23  s10.318,23,23,23c4.761,0,9.298-1.436,13.177-4.162l13.661,14.208c0.571,0.593,1.339,0.92,2.162,0.92  c0.779,0,1.518-0.297,2.079-0.837C56.255,54.982,56.293,53.08,55.146,51.887z M23.984,6c9.374,0,17,7.626,17,17s-7.626,17-17,17  s-17-7.626-17-17S14.61,6,23.984,6z";
    ContainerTag filterIcon = renderSvg(svgPath, 56).withClasses("h-4 w-4");    
    ContainerTag filterIconDiv =
        div().withClasses("absolute ml-4 mt-3 mr-4").with(filterIcon);
    ContainerTag filterDiv = div(filterIconDiv, filterInput).withClasses("relative text-primaryText w-85");

    contentDiv.with(filterDiv);
    
    ImmutableList<QuestionDefinition> sortedQuestions =
        ImmutableList.sortedCopyOf(Comparator.comparing(QuestionDefinition::getName),
            questionDefinitions);

    sortedQuestions.forEach(
        questionDefinition -> contentDiv.with(renderQuesitonDefinition(questionDefinition)));

    return ret;
  }

  private ContainerTag renderQuesitonDefinition(QuestionDefinition definition) {
    ContainerTag ret =
        div()
            .withClasses(
                "-m-3 p-3 flex items-start rounded-lg hover:bg-gray-200 hover:text-gray-800"
                    + " transition-all transform hover:scale-105");

                    
    ContainerTag icon = renderQuestionTypeSvg(definition.getQuestionType(), 24).withClasses("flex-shrink-0 h-12 w-6 text-primaryText");    
    ContainerTag content =
        div()
            .withClasses("ml-4")
            .with(
                p(definition.getName()).withClasses("text-base font-medium text-primaryText"),
                p(definition.getDescription()).withClasses("mt-1 text-sm text-secondaryText"));
    return ret.with(icon, content);
  }
  
  public ContainerTag renderQuestionTypeSvg(QuestionType type, int size) {
      return renderQuestionTypeSvg(type, size, size);
  }

  public ContainerTag renderQuestionTypeSvg(QuestionType type, int width, int height) {
    String iconPath = "";
    switch(type) {
      case ADDRESS:
        iconPath = "M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7zm0 9.5c-1.38 0-2.5-1.12-2.5-2.5s1.12-2.5 2.5-2.5 2.5 1.12 2.5 2.5-1.12 2.5-2.5 2.5z";
      break;
      case NAME:
        iconPath = "M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 3c1.66 0 3 1.34 3 3s-1.34 3-3 3-3-1.34-3-3 1.34-3 3-3zm0 14.2c-2.5 0-4.71-1.28-6-3.22.03-1.99 4-3.08 6-3.08 1.99 0 5.97 1.09 6 3.08-1.29 1.94-3.5 3.22-6 3.22z";
      break;
      case TEXT:
      default: 
        iconPath = "M11 18h2v-2h-2v2zm1-16C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 18c-4.41 0-8-3.59-8-8s3.59-8 8-8 8 3.59 8 8-3.59 8-8 8zm0-14c-2.21 0-4 1.79-4 4h2c0-1.1.9-2 2-2s2 .9 2 2c0 2-3 1.75-3 5h2c0-2.25 3-2.5 3-5 0-2.21-1.79-4-4-4z";
    }
    return renderSvg(iconPath, width, height);
  }

  public ContainerTag renderSvg(String pathString, int size) {
    return renderSvg(pathString, size, size);
  }

  public ContainerTag renderSvg(String pathString, int width, int height) {
    return renderSvg(pathString).attr("viewBox", String.format("0 0 %1$d %2$d", width, height));
  }

  public ContainerTag renderSvg(String pathString) {
    return new ContainerTag("svg")
        .attr("xmlns", "http://www.w3.org/2000/svg")
        .attr("fill", "currentColor")
        .attr("stroke", "currentColor")
        .attr("stroke-width", "1%")
        .attr("aria-hidden", "true")
        .with(
            new ContainerTag("path")
                .attr("xmlns", "http://www.w3.org/2000/svg")
                .attr("d", pathString));
  }
}
