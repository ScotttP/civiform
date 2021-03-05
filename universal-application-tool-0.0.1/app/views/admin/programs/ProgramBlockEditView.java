package views.admin.programs;

import static com.google.common.base.Preconditions.checkNotNull;
import static j2html.TagCreator.div;
import static j2html.TagCreator.p;
import static j2html.TagCreator.span;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import j2html.tags.ContainerTag;
import j2html.tags.Tag;
import play.mvc.Http.Request;
import play.twirl.api.Content;
import services.program.BlockDefinition;
import services.program.ProgramDefinition;
import services.question.QuestionDefinition;
import views.BaseHtmlView;
import views.Styles;
import views.admin.AdminLayout;

public class ProgramBlockEditView extends BaseHtmlView {

  private final AdminLayout layout;

  @Inject
  public ProgramBlockEditView(AdminLayout layout) {
    this.layout = checkNotNull(layout);
  }

  public Content render(
      Request request,
      ProgramDefinition program,
      BlockDefinition block,
      ImmutableList<QuestionDefinition> questions) {
    Tag csrfTag = makeCsrfTokenInputTag(request);

    String str = block.name() + questions.toString();
    Tag junkDiv = div(csrfTag, span(str)).withClasses(Styles.HIDDEN);

    return layout.render(programInfo(program), programContent(program), junkDiv);
  }

  private Tag programInfo(ProgramDefinition program) {
    ContainerTag programStatus =
        div("Draft").withId("program-status").withClasses(Styles.TEXT_XS, Styles.UPPERCASE);
    ContainerTag programTitle =
        div(program.name()).withId("program-title").withClasses(Styles.TEXT_3XL, Styles.PB_3);
    ContainerTag programDescription = div(program.description()).withClasses(Styles.TEXT_SM);

    ContainerTag programInfo =
        div(programStatus, programTitle, programDescription)
            .withId("program-info")
            .withClasses(
                Styles.BG_GRAY_100,
                Styles.TEXT_GRAY_800,
                Styles.SHADOW_MD,
                Styles.P_8,
                Styles.PT_4);

    return programInfo;
  }

  private Tag programContent(ProgramDefinition program) {
    ContainerTag blockEditor =
        div().withId("block-editor").withClasses(Styles.FLEX, Styles.H_FULL, Styles.RELATIVE);

    ContainerTag programBlocks = renderProgramBlocks(program);

    ContainerTag questionBank =
        div()
            .withId("question-bank")
            .withClasses(
                "w-1/3",
                Styles.RELATIVE,
                Styles.BORDER_L,
                Styles.BORDER_GRAY_400,
                Styles.BG_GRAY_50,
                Styles.SHADOW_INNER,
                Styles.TEXT_GRAY_600,
                Styles.P_4);

    return blockEditor.with(programBlocks, questionBank);
  }

  private ContainerTag renderProgramBlocks(ProgramDefinition program) {
    ContainerTag programBlocks = div().withId("program-blocks").withClasses("w-2/3", Styles.P_6);
    program.blockDefinitions().forEach(block -> programBlocks.with(renderProgramBlock(block)));
    return programBlocks;
  }

  private ContainerTag renderProgramBlock(BlockDefinition block) {
    String blockString = "block-" + block.id();
    ContainerTag programBlock =
        div()
            .withId("program-" + blockString)
            .withClasses(Styles.SHADOW_XL, Styles.ROUNDED_MD, Styles.MB_8);

    boolean blockHasQuestions = block.getQuestionCount() > 0;
    ContainerTag blockData =
        div()
            .withId(blockString + "-data")
            .withClasses(
                Styles.RELATIVE,
                Styles.BG_GRAY_400,
                Styles.TEXT_GRAY_50,
                Styles.P_4,
                blockHasQuestions ? Styles.ROUNDED_T_MD : Styles.ROUNDED_MD);

    ContainerTag blockTitle =
        div(block.name()).withId(blockString + "-title").withClasses(Styles.MB_1, Styles.UPPERCASE);

    ContainerTag blockDescription =
        div(block.description()).withId(blockString + "-description").withClasses(Styles.TEXT_XS);

    blockData.with(blockTitle, blockDescription);
    // Delete block.
    // <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 absolute top-7 right-5" fill="none"
    // viewBox="0 0 24 24" stroke="currentColor">
    //   <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867
    // 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1
    // 1 0 00-1 1v3M4 7h16" />
    // </svg>
    if (!blockHasQuestions) {
        return programBlock.with(blockData);
    }

    ContainerTag blockQuestions =
        div()
            .withId(blockString + "-questions")
            .withClasses(
                Styles.BG_WHITE,
                Styles.BORDER,
                Styles.BORDER_T_0,
                Styles.BORDER_GRAY_300,
                Styles.ROUNDED_B_MD,
                Styles.TEXT_GRAY_600);

    block
        .programQuestionDefinitions()
        .forEach(
            question -> {
              ContainerTag questionDiv =
                  div(
                      // add type svg
                      p().withText(question.getQuestionDefinition().getName())
                          .withClasses(Styles.INLINE, Styles.TEXT_SM))
                      .withClasses(
                          "odd:bg-gray-100 border-t border-gray-300 first:border-transparent p-3");
              blockQuestions.with(questionDiv);
            });
    //   <svg class="inline flex-shrink-0 h-6 w-6 mr-1 text-sm" xmlns="http://www.w3.org/2000/svg"
    // fill="currentColor" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1%"
    // aria-hidden="true">
    //     <path xmlns="http://www.w3.org/2000/svg" d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10
    // 10-4.48 10-10S17.52 2 12 2zm0 3c1.66 0 3 1.34 3 3s-1.34 3-3 3-3-1.34-3-3 1.34-3 3-3zm0
    // 14.2c-2.5 0-4.71-1.28-6-3.22.03-1.99 4-3.08 6-3.08 1.99 0 5.97 1.09 6 3.08-1.29 1.94-3.5
    // 3.22-6 3.22z" />
    //   </svg>

    return programBlock.with(blockData, blockQuestions);
  }
}
